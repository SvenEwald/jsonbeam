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
package org.jsonbeam.tutorial.t02_omdb;

import java.nio.charset.StandardCharsets;

import org.jsonbeam.JBProjector;
import org.jsonbeam.jsonprojector.annotations.JBRead;
import org.jsonbeam.tutorial.TutorialTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author sven
 */
public class TestMovieQuery extends TutorialTestCase {

	public interface Movie {
		@JBRead("Title")
		String getTitle();

		@JBRead("Year")
		int getYear();

		@JBRead("Director")
		String getDirector();
	}

	@Test
	public void testMovieQuery() {
		String url = "http://www.omdbapi.com/?t=Interstellar";

		Movie movie = new JBProjector().input(StandardCharsets.UTF_8).url(url).createProjection(Movie.class);

		System.out.println(movie.getTitle() + "(" + movie.getYear() + ")");
		System.out.println(movie.getDirector());

		assertEquals(2014, movie.getYear());
		assertEquals("Interstellar", movie.getTitle());
		assertEquals("Christopher Nolan", movie.getDirector());
	}
}
