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
package org.jsonbeam.test.examples;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.jsonbeam.annotations.JBRead;
import org.jsonbeam.test.utils.JBExpect;

/**
 * @author Sven
 *
 */
public interface Books {

	@JBRead("$.store.book[*].author")
	@JBExpect(strings={"Nigel Rees","Evelyn Waugh","J. R. R. Tolkien"})
	String[] getAuthors();
	
	@JBRead("$.store.book[*].author")
	@JBExpect(strings={"Nigel Rees","Evelyn Waugh","J. R. R. Tolkien"})
	List<String> getAuthorsAsList();
	
	@JBRead("$.store.book[*].author")
	@JBExpect(strings={"Nigel Rees","Evelyn Waugh","J. R. R. Tolkien"})
	Collection<String> getAuthorsAsCollection();
	
	@JBRead("$.store.book[*].author")
	@JBExpect(strings={"Nigel Rees","Evelyn Waugh","J. R. R. Tolkien"})
	Stream<String> getAuthorsAsStream();
	
	@JBRead("$.store.book[*].author")
	@JBExpect(strings={"Nigel Rees","Evelyn Waugh","J. R. R. Tolkien"})
	Iterable<String> getAuthorsAsIterable();
	
	@JBRead("$.store.book[*].author")	
	Set<String> getAuthorsAsSet();
	
	@JBExpect(strings={"Evelyn Waugh","J. R. R. Tolkien","Nigel Rees"})
	default Stream<String> testSetResult() {
		return getAuthorsAsSet().stream().sorted();
	}
	
	@JBRead("..price")
	@JBExpect(floats={8.95f,12.99f,22.99f,19.95f})
	float[] getPricesAsFloats();
	
	@JBRead("..price")
	@JBExpect(doubles={8.95d,12.99d,22.99d,19.95d})
	double[] getPricesAsDoubles();
 }
