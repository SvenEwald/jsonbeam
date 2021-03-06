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

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ServerSocketFactory;


/**
 * This class mocks a HTTP server by serving a fixed content.
 * Used to test the IO behavior. See the IO unit tests for usage examples.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public class HTTPParrot {

    private Future<String> future;
    private ServerSocket socket;

    /**
     * Factory method.
     * @param response
     * @return new HTTPParrot instance
     * @throws IOException
     */
    public static HTTPParrot serve(final String response) throws IOException {
        final HTTPParrot parrot = new HTTPParrot();
        parrot.socket = ServerSocketFactory.getDefault().createServerSocket(0, 1, InetAddress.getByName("localhost"));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        parrot.future = executor.submit(new Callable<String>() {

            @Override
            public String call() throws Exception {
                Socket s = parrot.socket.accept();
                String requestHeader = new Scanner(s.getInputStream()).useDelimiter("\r\n\r\n").next();
                s.getOutputStream().write(("HTTP/1.1 200 OK\r\nConnection: Close\r\nContent-Type: application/xml\r\nContent-Length: " + response.getBytes().length + "\r\n\r\n").getBytes());
                s.getOutputStream().write(response.getBytes());
                s.close();
                return requestHeader;
            }
        });
        executor.shutdown();
        return parrot;
    }

    /**
     * get current url (depends on local address & port)
     * @return url
     */
    public URL getURL() {
        try {
            return new URL("http", socket.getInetAddress().getHostAddress(), socket.getLocalPort(), "/test");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Getter for current request
     * @return request
     * @throws Exception
     */
    public String getRequest() throws Exception {
        return future.get();
    }
}