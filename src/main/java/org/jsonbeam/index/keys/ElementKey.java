package org.jsonbeam.index.keys;


@FunctionalInterface
public interface ElementKey {

	static final ElementKey ROOT = new KeyReference("$");
	static final ElementKey WILDCARD = new KeyReference("*");
	static final ElementKey ONE_KEY = new KeyReference("?");
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

	default String apply(CharSequence charArray) {
		return toString();
	}

	boolean matches(ElementKey otherKey);

	default void next() {
	};

}
