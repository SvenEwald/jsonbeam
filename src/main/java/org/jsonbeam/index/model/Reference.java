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

	String apply(CharSequence array);

	default public void dump(final String json) {
		dump(json, "", false);
	}

	default void dump(final String json, final String prefix, final boolean isTail) {
		List<Reference> children = getChildren();
		int length = children.size();
		String value = length > 0 ? "" : apply(json);
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