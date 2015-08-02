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

import java.util.ArrayList;
import java.util.List;

import org.jsonbeam.index.keys.ElementKey;

public class ArrayReference extends IndexReference {

	private final List<Reference> children;

	public ArrayReference() {
		children = new ArrayList<>();
	}

	public ArrayReference(final List<Reference> children) {
		this.children = children;
	}

	@Override
	public void addChild(final ElementKey key, final Reference child) {
		children.add(child);
	}

	@Override
	public String apply(final CharSequence array) {
		String hex=Integer.toHexString(System.identityHashCode(this));
		return "{ArrayRef"+hex+"/childcount:"+children.size()+"}";
	}

	@Override
	public List<Reference> getChildren() {
		return children;
	}

}
