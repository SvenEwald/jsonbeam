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
package org.jsonbeam.index.model;

import java.util.Collections;
import java.util.List;

import java.nio.charset.Charset;

import org.jsonbeam.index.keys.ElementKey;
import org.jsonbeam.index.model.values.LiteralReference;

public interface Reference {

	final static Charset KEY_CHARSET = Charset.forName("UTF-8");

	static final Reference TRUE = new LiteralReference("true");
	static final Reference FALSE = new LiteralReference("false");
	static final Reference NULL = new LiteralReference("null");
	static final Reference EMPTY_ARRAY = new ArrayReference(Collections.emptyList());
	static final Reference EMPTY_OBJECT = new ObjectReference();

	default void addChild(final ElementKey key, final Reference child) {
		throw new IllegalStateException(this.getClass() + " is sterile");
	}

	String apply();

	default public void dump(final String json) {
		dump(json, "", false);
	}

	default void dump(final String json, final String prefix, final boolean isTail) {
		List<Reference> children = getChildren();
		int length = children.size();
		String value = length > 0 ? "" : apply();
		System.out.println(prefix + (isTail ? prefix.endsWith(" ") ? "└── " : "───" : "├── ") + value + " " + getClass().getSimpleName());

		for (int i = 0; i < (length - 1); i++) {
			children.get(i).dump(json, prefix + (isTail ? "    " : "│   "), false);
		}
		if (length > 0) {
			children.get(length - 1).dump(json, prefix + (isTail ? "    " : "│   "), true);
		}
	}

	default public List<Reference> getChildren() {
		return Collections.emptyList();
	}

	default public void setChildren(final List<Reference> childrenArray) {
		throw new IllegalStateException(getClass().getSimpleName() + " can not have children");
	}

}