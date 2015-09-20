/**
 *  Copyright 2013 Sven Ewald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jsonbeam.test.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import org.jsonbeam.intern.utils.Pair;

/**
 * This proxy allows unit tests to do external HTTP requests without generating huge traffic. The content is fetched just once
 * and then reused forever. This ensures independence of external server availability for the test results while still working
 * with real live data. This class cannot be subclassed (Thread.start in constructor).
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public final class JUnitHttpProxy implements Runnable {

	private final static X509Certificate CERTIFICATE = loadCertificate();
	private final static PrivateKey PRIVATE_KEY = loadPrivateKey();

	private final static TrustManager[] TRUST_MY_CERT = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] { CERTIFICATE };
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
		}
	} };

	private final static KeyManager[] KEY_MANAGER = new KeyManager[] { new X509KeyManager() {

		@Override
		public String[] getServerAliases(String keyType, Principal[] issuers) {
			return null;
		}

		@Override
		public PrivateKey getPrivateKey(String alias) {
			return PRIVATE_KEY;
		}

		@Override
		public String[] getClientAliases(String keyType, Principal[] issuers) {
			return null;
		}

		@Override
		public X509Certificate[] getCertificateChain(String alias) {
			return new X509Certificate[] { CERTIFICATE };
		}

		@Override
		public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
			return "junitproxy";
		}

		@Override
		public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
			return null;
		}
	} };

	static {
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(KEY_MANAGER, TRUST_MY_CERT, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			SSL_SOCKET_FACTORY = sc.getSocketFactory();
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	private final static SSLSocketFactory SSL_SOCKET_FACTORY;

	/**
	 * @author Sven
	 */
	private final class HttpProxyListener extends Thread {
		private final Socket accept;

		{
			setDaemon(true);
		}

		/**
		 * @param accept
		 */
		private HttpProxyListener(Socket accept) {
			this.accept = accept;
		}

		@Override
		public void run() {

			try {
				accept.setSoTimeout(15000);
				try (InputStream is = accept.getInputStream()) {
					String requestHeader = new Scanner(is).useDelimiter("(?m)\\r\\n\\r\\n").next();
					if (requestHeader.startsWith("CONNECT")) {
						handleSSLRequest(accept, requestHeader);
					}
					else {
						swallow(accept.getInputStream());
						String url = findURL(requestHeader);
						byte[] content = resolveURLContent(url);
						dropToHTTPClient(accept, content);
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					accept.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		private void handleSSLRequest(Socket accept, String requestHeader) throws IOException {
			swallow(accept.getInputStream());
			Pair<String, Integer> sp = findServerAndPort(requestHeader);
			//	generateCertFor(sp.getKey());
			accept.getOutputStream().write("HTTP/1.0 200 Connection established\n\r\n\r".getBytes());
			SSLSocket sslSocket = (SSLSocket) SSL_SOCKET_FACTORY.createSocket(accept, "junitproxy"/*sp.getKey()*/, sp.getValue(), false);
			sslSocket.setUseClientMode(false);
			sslSocket.startHandshake();
			String httpRequestHeader = new Scanner(sslSocket.getInputStream()).useDelimiter("(?m)\\r\\n\\r\\n").next();
			String url2 = findURL(httpRequestHeader);
			String url = "https://" + sp.getKey() + ":" + sp.getValue() + url2;
			byte[] content = resolveURLContent(url);
			dropToHTTPClient(sslSocket, content);
		}

	}

	private ServerSocket serverSocket;
	private Thread listenThread;
	private String origProxyHost;
	private String origProxyPort;
	private String origProxyHostSSL;
	private String origProxyPortSSL;

	/**
	 */
	public JUnitHttpProxy() {
		try {
			this.serverSocket = new ServerSocket(0, 5, InetAddress.getLocalHost());
			this.listenThread = new Thread(this);
			this.listenThread.setDaemon(true);
			this.listenThread.setName(JUnitHttpProxy.class.getSimpleName() + ".listenThread");
			this.listenThread.start();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	/**
	 * @return
	 */
	private static PrivateKey loadPrivateKey() {
		KeyStore keystore;
		try {
			keystore = KeyStore.getInstance("PKCS12");
			InputStream is = JUnitHttpProxy.class.getResourceAsStream("junitproxy_private.p12");
			keystore.load(is, null);
			PrivateKey key = (PrivateKey) keystore.getKey("junitproxy", new char[] {});
			return key;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return
	 */
	private static X509Certificate loadCertificate() {
		try {
			X509Certificate certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(JUnitHttpProxy.class.getResourceAsStream("junitproxy.cer"));
			return certificate;
		} catch (CertificateException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		try {
			while (true) {
				final Socket accept = serverSocket.accept();
				new HttpProxyListener(accept).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param url
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	private byte[] resolveURLContent(String url) throws FileNotFoundException, IOException, MalformedURLException, UnsupportedEncodingException {
		final File file = new File(JUnitHttpProxy.class.getSimpleName() + "." + URLEncoder.encode(url, "UTF-8") + ".tmp");
		byte[] content;
		if (file.exists()) {
			content = new byte[(int) file.length()];
			FileInputStream inputStream = new FileInputStream(file);
			inputStream.read(content);
			inputStream.close();
			//  System.out.println("Load " + content.length + " bytes");
			return content;
		}
		try {
			restoreProxySettings();
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setReadTimeout(15000);
			connection.getResponseCode();
			content = inputStreamToByteArray(connection.getInputStream());
			// System.out.println("Download " + content.length + " bytes " + " encoding:" + connection.getContentEncoding());
			FileOutputStream fileStream = new FileOutputStream(file);
			fileStream.write(content);
			fileStream.flush();
			fileStream.close();
			return content;
		} finally {
			setAsProxy();
		}

	}

	/**
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	private static byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while (true) {
			int count = inputStream.read(buffer);
			if (count < 0) {
				break;
			}
			if (count > 0) {
				bos.write(buffer, 0, count);
			}
			if ((count == buffer.length) && (buffer.length < 64 * 1024)) {
				buffer = new byte[buffer.length * 2];
			}
		}
		return bos.toByteArray();
	}

	/**
	 * @param accept
	 * @param bytes
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private static void dropToHTTPClient(Socket accept, byte[] content) throws IOException, UnsupportedEncodingException {
		//    System.out.println("Dropping " + content.length + " bytes.");
		String header = "HTTP/1.1 200 OK\r\nConnection: Close\r\nContent-Type: application/xml\r\nContent-Length: " + content.length + "\r\n\r\n";
		accept.getOutputStream().write(header.getBytes("UTF-8"));
		accept.getOutputStream().write(content);
		accept.getOutputStream().flush();
	}

	/**
	 * @param inputStream
	 * @throws IOException
	 */
	private static void swallow(InputStream inputStream) throws IOException {
		while (inputStream.available() > 0) {
			inputStream.read();
		}
	}

	/**
	 * @param request
	 * @return
	 */
	private static String findURL(String request) {
		Matcher matcher = Pattern.compile("T (.*) HTTP/1\\..").matcher(request);
		if (!matcher.find()) {
			throw new IllegalArgumentException();
		}
		String url = matcher.group(1);
		Objects.requireNonNull(url);
		return url;
	}

	/**
	 * @param requestHeader
	 * @return
	 */
	private static Pair<String, Integer> findServerAndPort(String requestHeader) {
		String url = findURL(requestHeader);
		String server = url.replaceAll(":.*", "");
		String port = url.replaceAll(".*:", "");
		return new Pair<>(server, Integer.parseInt(port));
	}

	/**
	 * 
	 */
	public void restoreProxySettings() {
		if (origProxyHost == null) {
			System.clearProperty("http.proxyHost");
			System.clearProperty("http.proxyPort");
		}
		else {
			System.setProperty("http.proxyHost", origProxyHost);
			System.setProperty("http.proxyPort", origProxyPort);
			origProxyHost = null;
		}
		if (origProxyHostSSL == null) {
			System.clearProperty("https.proxyHost");
			System.clearProperty("https.proxyPort");
			return;
		}
		System.setProperty("https.proxyHost", origProxyHostSSL);
		System.setProperty("https.proxyPort", origProxyPortSSL);
		origProxyHostSSL = null;
	}

	/**
	 * 
	 */
	public void stop() {
		restoreProxySettings();
		listenThread.interrupt();
	}

	/**
	 * 
	 */
	public void setAsProxy() {
		origProxyHost = System.getProperty("http.proxyHost");
		origProxyPort = System.getProperty("http.proxyPort");
		System.setProperty("http.proxyHost", serverSocket.getInetAddress().getHostAddress());
		System.setProperty("http.proxyPort", Integer.toString(serverSocket.getLocalPort()));

		origProxyHostSSL = System.getProperty("https.proxyHost");
		origProxyPortSSL = System.getProperty("https.proxyPort");
		System.setProperty("https.proxyHost", serverSocket.getInetAddress().getHostAddress());
		System.setProperty("https.proxyPort", Integer.toString(serverSocket.getLocalPort()));

	}

}
