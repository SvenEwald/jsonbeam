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
package org.jsonbeam.intern.utils;

import java.util.function.BiFunction;

public class Pair<KK, VV> {
	final KK key;
	private final VV value;

	public Pair(final KK key, final VV value) {
		this.key = key;
		this.value = value;
	}

	public KK getKey() {
		return key;
	}

	public VV getValue() {
		return value;
	}
	
	public <T> T reduce(BiFunction<KK, VV, T> f) {
		return f.apply(key,value);
	}
}