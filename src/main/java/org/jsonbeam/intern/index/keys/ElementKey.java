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
package org.jsonbeam.intern.index.keys;

import java.util.function.Predicate;

import org.jsonbeam.intern.index.model.Reference;

@FunctionalInterface
public interface ElementKey {

	static final ElementKey ROOT = new KeyReference("$");
	static final ElementKey WILDCARD = new KeyReference("*");
	static final ElementKey ONE_KEY = new KeyReference("?");
	static final ElementKey INOBJECT = new ElementKey() {
		@Override
		public boolean matches(ElementKey otherKey) {
			throw new IllegalStateException("This refernce should never be matched");
		}

		@Override
		public String toString() {
			return "<ILLEGAL OBJECT REF>";
		}
	};
	static final ElementKey ALL_ARRAY_CHILDREN = new ElementKey() {

		@Override
		public boolean matches(ElementKey otherKey) {
			return otherKey instanceof ArrayIndexKey;
		}

		@Override
		public String toString() {
			return "[*]";
		}
	};

	@Deprecated
	default String apply(CharSequence charArray) {
		return toString();
	}

	boolean matches(ElementKey otherKey);

	default void next() {
	}

	default void addSubFilter(PathReferenceStack predicatePath, Predicate<Reference> filter) {
		throw new IllegalStateException("This method should not be called on this instance:"+this.getClass().getSimpleName());
	}

}
