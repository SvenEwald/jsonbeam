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
