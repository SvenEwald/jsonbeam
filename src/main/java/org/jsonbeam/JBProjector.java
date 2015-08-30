/**
 *    Copyright 2015 Sven Ewald
 *
 *    This file is part of JSONBeam.
 *
 *    JSONBeam is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, any
 *    later version.
 *
 *    JSONBeam is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with JSONBeam.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jsonbeam;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.jsonbeam.evaluation.DefaultEvaluator;
import org.jsonbeam.io.BytesCharacterSource;
import org.jsonbeam.io.FileCharacterSource;
import org.jsonbeam.io.StringCharacterSource;
import org.jsonbeam.io.CharsCharacterSource;
import org.jsonbeam.jsonprojector.annotations.JBDocURL;
import org.jsonbeam.jsonprojector.projector.BCJSONProjector;
import org.jsonbeam.jsonprojector.projector.intern.CanEvaluateOrProject;
import org.jsonbeam.utils.IOHelper;

public class JBProjector {

	public JBProjector(final Flags... optionalFlags) {
		this.delegate = new BCJSONProjector(optionalFlags);
	}

	private final BCJSONProjector delegate;

	public enum Flags {
		/**
		 * Let the projections toString() method render the projection target as JSON. Be careful if your documents get large.
		 * toString() might be used frequently by the IDE your debugging in.
		 */
		TO_STRING_RENDERS_JSON,
		/**
		 * Option to strip empty values from the result.
		 */
		OMIT_EMPTY_NODES,
		/**
		 * If a node is not present, handle it like it is empty.
		 */
		ABSENT_IS_EMPTY,

	}

	/**
	 * Collection of methods used to read JSON data. Sorry, you must specify the {@link Charset} of your data. There is
	 * deliberately no default character set. See {@link StandardCharsets} for convenient constants.
	 *
	 * @param charset
	 * @return
	 */
	public ProjectionInput input(final Charset charset) {
		return new ProjectionInput() {

			@Override
			public CanEvaluateOrProject url(final URL url) {
				return new DefaultEvaluator(delegate,//
						() -> IOHelper.toBuffer(() -> url.openConnection().getInputStream(), (is, l) -> new BytesCharacterSource(is, l, charset)));
			}

			@Override
			public CanEvaluateOrProject url(final String url) {
				try {
					return url(new URL(url));
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public CanEvaluateOrProject stream(final InputStream is) {
				return new DefaultEvaluator(delegate, () -> IOHelper.toBuffer(is, (s, l) -> new BytesCharacterSource(s, l, charset)));
			}

			@Override
			public <T> T fromURLAnnotation(final Class<T> projectionInterface, final Object... optionalParams) throws IOException {
				JBDocURL annotation = projectionInterface.getAnnotation(JBDocURL.class);
				if (annotation == null) {
					throw new IllegalArgumentException("There is no " + JBDocURL.class.getSimpleName() + " annotation on the interface " + projectionInterface);
				}
				return url(annotation.value()).createProjection(projectionInterface);
			}

			@Override
			public CanEvaluateOrProject file(final String filename) {
				return file(new File(filename));
			}

			@Override
			public CanEvaluateOrProject file(final File file) {
				return new DefaultEvaluator(delegate, () -> new FileCharacterSource(file, charset));
			}

			@Override
			public CanEvaluateOrProject resource(final String resourceName) {
				return new DefaultEvaluator(delegate, () -> null);
			}
		};
	}

	public CanEvaluateOrProject onJSONString(final CharSequence json) {
		return new DefaultEvaluator(delegate, () -> new StringCharacterSource(json));
	}
	
	public CanEvaluateOrProject onJSONChars(final char[] json) {
		return new DefaultEvaluator(delegate, () -> new CharsCharacterSource(json));
	}
	
	public CanEvaluateOrProject onJSONBytes(final byte[] json,final Charset charset) {
		return new DefaultEvaluator(delegate, () -> new BytesCharacterSource(json,json.length,charset));
	}

	/**
	 * Remove all cached reflection data.
	 * Call this method if you are not going to create projections any more.
	 */
	public void dropAllCaches() {
		BCJSONProjector.dropAllCaches();
	}
}
