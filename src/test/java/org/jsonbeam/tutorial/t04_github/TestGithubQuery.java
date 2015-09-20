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
package org.jsonbeam.tutorial.t04_github;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import org.jsonbeam.JsonProjector;
import org.jsonbeam.annotations.JBRead;
import org.jsonbeam.tutorial.TutorialTestCase;
import org.junit.Test;

/**
 * @author Sven
 */
public class TestGithubQuery extends TutorialTestCase {

	public interface Result {
		@JBRead("total_count")
		int getTotalCount();

		@JBRead("items..full_name")
		Stream<String> getNames();

		@JBRead("..owner.login")
		List<String> getOwners();
	}

	@Test
	public void testGithubQuery() {
		String url = "https://api.github.com/search/repositories?q=json";
		Result result = new JsonProjector().input(StandardCharsets.UTF_8).url(url).createProjection(Result.class);
		//Result result = new JBProjector().onJSONString(Testdata.RESULT).createProjection(Result.class);

		System.out.println(result.getTotalCount());
		result.getNames().forEach(System.out::println);
	}
}
