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
package org.jsonbeam.tutorial.t08_OpenNotify;

import org.jsonbeam.JsonProjector;
import org.jsonbeam.annotations.JBDocURL;
import org.jsonbeam.annotations.JBRead;
import org.jsonbeam.tutorial.TutorialTestCase;
import org.junit.Test;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
/**
 * @author Sven
 */
public class WhereIsTheISS extends TutorialTestCase {
	
	@JBDocURL("http://api.open-notify.org/iss-now.json")
	public interface ISSLocation {
		@JBRead("iss_position.latitude")
		double getLat();

		@JBRead("iss_position.longitude")
		double getLon();

		default String asString() {
			return getLat() + "/" + getLon();
		}
	}

	@Test
	public void testPrintISSLocation() throws IOException {
		System.out.println(new JsonProjector().input(UTF_8).fromURLAnnotation(ISSLocation.class).asString());
	}
}
