package org.jsonbeam.index.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsonbeam.index.keys.ElementKey;

public class ObjectReference extends IndexReference {

	private static class Pair<KK, VV> {
		private final KK key;
		private final VV value;

		private Pair(final KK key, final VV value) {
			this.key = key;
			this.value = value;
		}

		public KK getKey() {
			return key;
		}

		public VV getValue() {
			return value;
		}
	}

	//private final Map<Reference, Reference> children = new MyLazyValueMap<>(lazyChop)
	private final List<Pair<ElementKey, Reference>> children = new ArrayList<>();

	public ObjectReference() {
	}

	@Override
	public void addChild(final ElementKey keyRef, final Reference valueRef) {
		this.children.add(new Pair<ElementKey, Reference>(keyRef, valueRef));
	}

	@Override
	public String apply(final CharSequence array) {
		String hex = Integer.toHexString(System.identityHashCode(this));
		return "{ObjRef" + hex + "/childcount:" + children.size() + "}";
	}

	@Override
	public void dump(final String json, final String prefix, final boolean isTail) {
		if (children.size() == 0) {
			System.out.println(prefix + (isTail ? prefix.endsWith(" ") ? "└── " : "───" : "├── ") + "<EMPTY OBJECT>" + " " + getClass().getSimpleName());
			return;
		}
		//String value = children.size() > 0 ? "" : resolveValue(json);
		//System.out.println(prefix + (isTail ? prefix.endsWith(" ") ? "└── " : "───" : "├── ") + value + " " + getClass().getSimpleName());

		for (int i = 0; i < (children.size() - 1); i++) {
			children.get(i).getValue().dump(json, prefix + (isTail ? "    " : "│   ") + children.get(i).key.apply(json) + ":", false);
		}
		if (children.size() > 0) {
			children.get(children.size() - 1).getValue().dump(json, prefix + (isTail ? "    " : "│   ") + children.get(children.size() - 1).key.apply(json) + ":", true);
		}
	}

	@Override
	public List<Reference> getChildren() {
		return children.stream().map(Pair::getValue).collect(Collectors.toList());
	}
}
