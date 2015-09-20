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
package org.jsonbeam.tutorial.t02_geonames;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.jsonbeam.JsonProjector;
import org.jsonbeam.annotations.JBRead;
import org.jsonbeam.tutorial.TutorialTestCase;
import org.junit.Test;
/**
 * @author Sven
 */
@SuppressWarnings("static-method")
public class TestGeonames extends TutorialTestCase {

	public interface City {
		@JBRead("name")
		String getName();
	}

	public interface Geonames {
		@JBRead("geonames[*]")
		Stream<City> getCities();
	}

	
	@Test
	public void testCities() {
		String url = "http://api.geonames.org/citiesJSON?formatted=true&north=44.1&south=-9.9&east=-22.4&west=55.2&lang=de&username=demo&style=full";
		Geonames geonames = new JsonProjector().input(UTF_8).url(url).createProjection(Geonames.class);
		geonames.getCities().map(City::getName).forEach(System.out::println);
	}
	
	@Test
	public void testCities2() {
		String url = "http://api.geonames.org/citiesJSON?formatted=true&north=44.1&south=-9.9&east=-22.4&west=55.2&lang=de&username=demo&style=full";
		Optional<City> city = new JsonProjector().input(UTF_8).url(url).evalJPath("geonames[0]").as(City.class);
		city.ifPresent(c->System.out.println(c.getName()));
	}
	
	@Test
	public void testCitiesAsArray() {
		String url = "http://api.geonames.org/citiesJSON?formatted=true&north=44.1&south=-9.9&east=-22.4&west=55.2&lang=de&username=demo&style=full";
		City[] city = new JsonProjector().input(UTF_8).url(url).evalJPath("geonames[*]").asArrayOf(City.class);
		Arrays.stream(city).map(City::getName).forEach(System.out::println);
	}
	
	@Test
	public void testCitiesAsStream() {
		String url = "http://api.geonames.org/citiesJSON?formatted=true&north=44.1&south=-9.9&east=-22.4&west=55.2&lang=de&username=demo&style=full";
		Stream<City> cities = new JsonProjector().input(UTF_8).url(url).evalJPath("geonames[*]").asStreamOf(City.class);
		cities.map(City::getName).forEach(System.out::println);
	}
	
}
