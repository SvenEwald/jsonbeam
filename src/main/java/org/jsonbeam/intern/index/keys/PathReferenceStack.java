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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsonbeam.intern.index.model.Reference;
import org.jsonbeam.intern.utils.collections.IntegerStack;
import org.jsonbeam.intern.utils.collections.ObjectStack;

public class PathReferenceStack implements Iterable<ElementKey> {

	public static final PathReferenceStack EMPTY = new PathReferenceStack();

	public static PathReferenceStack parse(String query) {
		//if (true) return PathParser.parse(query);
		
		if (query.startsWith("$.")) {
			query = query.substring(2);
		}
		query = query.replace("..", ".*.");
		if (query.startsWith(".")) {
			query = query.substring(1);
		}
		PathReferenceStack newStack = new PathReferenceStack();

		for (String s : query.split(Pattern.quote("."))) {

			int i = s.indexOf("[");
			if (i >= 0) {
				if (i > 0) {
					newStack.push(new KeyReference(s.substring(0, i)));
					s = s.substring(i);
				}
				if ("[*]".equals(s)) {
					newStack.isPattern = true;
					newStack.push(ElementKey.ALL_ARRAY_CHILDREN);
					continue;
				}
				if (s.startsWith("[")) {
					int pos = Integer.parseInt(s.substring(1, s.length() - 1));
					newStack.push(new ArrayIndexKey(pos));
					continue;
				}
				continue;
			}
			if ("[*]".equals(s)) {
				newStack.isPattern = true;
				newStack.push(ElementKey.ALL_ARRAY_CHILDREN);
				continue;
			}
			if ("*".equals(s)) {
				newStack.isPattern = true;
				newStack.push(ElementKey.WILDCARD);
				continue;
			}
			newStack.push(new KeyReference(s));
		}
		return newStack;
	}

	private final ObjectStack<ElementKey> elements = new ObjectStack<>();
	private final IntegerStack hashValues;
	private int hashCode;

	//	private static final Map<PathReferenceStack, PathReferenceStack> instances = new ConcurrentHashMap<>();
	//	private final Supplier<JBSubQueries> subCollector;

	private boolean isPattern = false;

	public PathReferenceStack() {
		//		this.subCollector = null;
		hashValues = new IntegerStack();
		hashCode = 0;
	}

	//	public PathReferenceStack(final PathReferenceStack copyMe) {
	//		elements.addAll(copyMe.elements);
	//		hashCode = copyMe.hashCode;
	//		hashValues = new IntegerStack(copyMe.hashValues);
	//		this.subCollector = copyMe.subCollector;
	//	}

	//	public PathReferenceStack(final Supplier<JBSubQueries>... subqueries) {
	//		this.subCollector = (subqueries == null) || (subqueries.length == 0) ? null : subqueries[0];
	//		hashValues = new IntegerStack();
	//		hashCode = 0;
	//	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PathReferenceStack other = (PathReferenceStack) obj;
		if (hashCode != other.hashCode) {
			return false;
		}
		return elements.equals(other.elements);
	}

	//	public Optional<JBSubQueries> getSubCollector() {
	//		return Optional.ofNullable(this.subCollector).map(Supplier::get);
	//	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

	public boolean isPattern() {
		return isPattern; //elements.contains(ElementKey.WILDCARD);
	}

	@Override
	public Iterator<ElementKey> iterator() {
		return elements.iterator();
	}

	public boolean matches(final PathReferenceStack pathReferenceStack) {
		final ObjectStack<ElementKey> pattern = elements;
		final ObjectStack<ElementKey> path = pathReferenceStack.elements;

		boolean[] matches = new boolean[path.size() + 1];
		matches[0] = true;
		int firstMatch = 0;
		for (ElementKey subPattern : pattern) {
			if (ElementKey.WILDCARD.equals(subPattern)) {
				for (int i = firstMatch + 1; i <= path.size(); ++i) {
					matches[i] = true;
				}
				continue;
			}
			int match = -1;
			Iterator<ElementKey> pathIterator = path.descendingIterator();
			for (int i = path.size(); i > firstMatch; --i) {
				matches[i] = subPattern.matches(pathIterator.next()) && matches[i - 1];
				//				matches[i] = (subPattern.equals(KeyReference.ONE_KEY) || subPattern.equals(pathIterator.next())) && matches[i - 1];
				if (matches[i]) {
					match = i;
				}
			}
			if (match < 0) {
				return false;
			}
			firstMatch = match;
		}
		return matches[path.size()];
	}

	//	public static PathReferenceStack intern(final PathReferenceStack pathReferenceStack) {
	//		PathReferenceStack instance = instances.get(pathReferenceStack);
	//		if (instance != null) {
	//			return instance;
	//		}
	//		instance = new PathReferenceStack(pathReferenceStack);
	//		instances.put(instance, instance);
	//		return instance;
	//	}

	public ElementKey pop() {
		//		System.out.println("pop " + this.toString());
		hashCode = hashValues.pop();
		return elements.pop();
	}

	public PathReferenceStack push(final ElementKey element) {
		assert element != null;
		hashValues.push(hashCode);
		hashCode = (31 * hashCode) + element.hashCode();
		elements.push(element);
		//		System.out.println("push " + this.toString());
		return this;
	}

	public int size() {
		return elements.size();
	}

	public ElementKey tail() {
		return elements.peek();
	}

	@Override
	public String toString() {
		//return String.join("", elements);
		String path = elements.stream().map(ElementKey::toString).collect(Collectors.joining("."));
		//		if (subCollector != null) {
		//			return path + " refering to a subcollector: ";
		//		}
		return path;
	}

	/**
	 * @return
	 */
	public Supplier<List<Reference> >getResultListCreator() {
		return ArrayList::new;
	}
}
