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
package org.jsonbeam.tutorial.t05_twitter;

import java.nio.charset.StandardCharsets;
import java.util.OptionalInt;

import org.jsonbeam.JsonProjector;
import org.jsonbeam.tutorial.TutorialTestCase;
import org.junit.Test;

/**
 * @author Sven
 */
public class QueryURLinTwitter extends TutorialTestCase {
	
	private final static String BASEURL="http://urls.api.twitter.com/1/urls/count.json?url=";
	
	@Test
	public void testXMLBeam() {
		OptionalInt asInt = new JsonProjector().input(StandardCharsets.UTF_8).url(BASEURL+"xmlbeam.org").evalJPath("count").asInt();
		asInt.ifPresent(System.out::println);
	}
	@Test
	public void testJsonBeam() {
		OptionalInt asInt = new JsonProjector().input(StandardCharsets.UTF_8).url(BASEURL+"jsonbeam.org").evalJPath("count").asInt();
		asInt.ifPresent(System.out::println);
	}
}
