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
package org.jsonbeam.tutorial.t05_wikipedia;

import java.nio.charset.StandardCharsets;

import org.jsonbeam.JsonProjector;
import org.jsonbeam.annotations.JBRead;
import org.jsonbeam.tutorial.TutorialTestCase;
import org.junit.Test;

/**
 * @author sven
 */
public class QueryWikipedia extends TutorialTestCase {

	public interface WikipediaStats {
		@JBRead("..statistics.articles")
		int getArticleCount();

		@JBRead("..statistics.activeusers")
		int getActiveUsers();
	}

	@Test
	public void testQueryWikipedia() {
		String url = "https://en.wikipedia.org/w/api.php?action=query&meta=siteinfo&siprop=statistics&format=json";
		WikipediaStats wikipediaStats = new JsonProjector().input(StandardCharsets.UTF_8).url(url).createProjection(WikipediaStats.class);
		System.out.println("Articles:" + wikipediaStats.getArticleCount());
		System.out.println("Active users:" + wikipediaStats.getActiveUsers());
	}
}
