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
import java.net.URL;
import org.jsonbeam.jsonprojector.projector.intern.CanEvaluateOrProject;

/**
 * @author Sven
 *
 */

/**
 * A ProjectionIO is responsible for every input operation related to projections.
 */
public interface ProjectionInput {

	/**
	 * Get access to the file IO.
	 *
	 * @param file
	 * @return a XBFileIO for this file.
	 */
	CanEvaluateOrProject file(File file);

	/**
	 * Get access to the file IO.
	 *
	 * @param fileName
	 * @return a XBFileIO for this filename.
	 */
	CanEvaluateOrProject file(String fileName);

	/**
	 * Get access to the url IO.
	 *
	 * @param url
	 * @return a XBUrlIO for this url
	 */
	CanEvaluateOrProject url(String url);

	/**
	 * Get access to the url IO.
	 *
	 * @param url
	 * @return a XBUrlIO for this url
	 */
	CanEvaluateOrProject url(URL url);

	/**
	 * Get access to the url IO.
	 *
	 * @param url
	 * @return a XBUrlIO for this url
	 */
	public CanEvaluateOrProject resource(final String resourceName);

	/**
	 * Get access to the stream IO
	 *
	 * @param is
	 * @return a XBStreamInput for this InputStream
	 */
	CanEvaluateOrProject stream(InputStream is);

	/**
	 * Create a new projection using a {@link XBDocURL} annotation on this interface. When the XBDocURL starts with the protocol
	 * identifier "resource://" the class loader of the projection interface will be used to read the resource from the current
	 * class path.
	 *
	 * @param projectionInterface
	 *            a public interface.
	 * @param optionalParams
	 * @return a new projection instance
	 * @throws IOException
	 */
	<T> T fromURLAnnotation(final Class<T> projectionInterface, Object... optionalParams) throws IOException;
}