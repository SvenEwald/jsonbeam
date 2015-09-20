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
package org.jsonbeam.tutorial.t10_CityBikes;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.jsonbeam.JsonProjector;
import org.junit.Test;

/**
 * @author sven
 *
 */
public class QueryCityBikes {

	private final static String URL="http://api.citybik.es/networks.json";
	
	
	@Test
	public void queryNetworks() {
		Stream<String> stream = new JsonProjector().input(StandardCharsets.UTF_8).url(URL).evalJPath("..city").asStreamOf(String.class);
		stream.forEach(System.out::println);
	}
}
