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
package org.jsonbeam.intern.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsonbeam.intern.index.keys.ArrayIndexKey;
import org.jsonbeam.intern.index.keys.ElementKey;
import org.jsonbeam.intern.index.keys.PathReferenceStack;
import org.jsonbeam.intern.index.model.ObjectReference;
import org.jsonbeam.intern.index.model.Reference;

public class JBQueries implements JBResultCollector, JBResultProvider {

	protected final Set<ElementKey> elementsToQuery = new HashSet<ElementKey>();
	protected final Map<PathReferenceStack, PathReferenceStack> directHits = new HashMap<>();
	protected final Map<PathReferenceStack, List<Reference>> results = new HashMap<PathReferenceStack, List<Reference>>();
	protected final Map<ElementKey, Set<PathReferenceStack>> patterns = new HashMap<>();
	private final PathReferenceStack currentPath = new PathReferenceStack();
	private boolean queryAllArrayElements = false;
	private final Map<PathReferenceStack, Supplier<JBSubQueries>> path2SubQueries = new HashMap<>();

	public JBQueries addQuery(final PathReferenceStack query, final Supplier<JBSubQueries> subqueries) {
		if (subqueries != null) {
			path2SubQueries.put(query, subqueries);
		}
		//System.out.println("adding query:"+query);
		//assert (!query.getSubCollector().isPresent()) || (query.getSubCollector().get() != this) : "Found query with own subcollector";
		ElementKey lastElement = query.tail();
		if (lastElement != ElementKey.ALL_ARRAY_CHILDREN) {
			elementsToQuery.add(lastElement);
		}
		else {
			this.queryAllArrayElements = true;
		}
		if (!query.isPattern()) {
			directHits.put(query, query);
			return this;
		}
		patterns.computeIfAbsent(lastElement, k -> new LinkedHashSet<>()).add(query);
		return this;
	}

	@Override
	public String currentPathAsString() {
		return currentPath.isEmpty() ? "''" : currentPath.toString();
	}

	public boolean dumpQueryGraph() {
		System.out.println("Direct hits:");
		directHits.keySet().forEach(p -> {
			System.out.println(p + (results.containsKey(p) ? ":" + results.get(p).size() + " results" : ""));
		});
		System.out.println("Patterns:");
		patterns.values().forEach(p -> {
			System.out.println(p + (results.containsKey(p) ? ":" + results.get(p).size() + " results" : ""));
		});
		return true;
	}

	public boolean dumpResults() {
		System.out.println("Query results:");
		results.entrySet().forEach(e -> {
			System.out.println(e.getKey().toString() + ":(" + e.getValue().size() + ")" + e.getValue().stream().map(r -> r.apply()).collect(Collectors.joining(",")));
		});
		return true;
	}

	@Override
	public JBSubQueries foundObjectPath(final Supplier<ObjectReference> item) {
		//System.out.println(DH.oid(this) + " found object path:" + currentPath + " direct hit:" + directHits.containsKey(currentPath));
		if (currentPath.isEmpty()) {
			return null;
		}

		ElementKey lastElement = currentPath.tail();
		if ((!queryAllArrayElements) && (!elementsToQuery.contains(lastElement))) {
			return null;
		}

		PathReferenceStack pathReferenceStack2 = directHits.get(currentPath);
		JBSubQueries subQueries = null;
		ObjectReference objectReference = null;
		if (pathReferenceStack2 != null) {
			objectReference = item.get();
			storeResult(pathReferenceStack2, objectReference);
			Supplier<JBSubQueries> supplier = path2SubQueries.get(pathReferenceStack2);
			if (supplier != null) {
				subQueries = supplier.get();
			}
		}
		Set<PathReferenceStack> patternsToMatch = patterns.get(lastElement);// FIXME, refactor logic
		if (patternsToMatch == null) {
			if (queryAllArrayElements) {
				patternsToMatch = patterns.get(ElementKey.ALL_ARRAY_CHILDREN);
				if (patternsToMatch == null) {
					updateObjectRef(objectReference, subQueries);
					return subQueries;
				}
			}
			else {
				updateObjectRef(objectReference, subQueries);
				return subQueries;
			}

		}
		for (PathReferenceStack pattern : patternsToMatch) {
			if (!pattern.matches(currentPath)) {
				continue;
			}
			if (objectReference == null) {
				objectReference = item.get();
			}
			storeResult(pattern, objectReference);
			if (path2SubQueries.containsKey(pattern)) {//FIXME: this neeeeeds cleanup
				JBSubQueries patternQueries = path2SubQueries.get(pattern).get();
				if (subQueries == null) {
					subQueries = patternQueries;
				}
				else {
					subQueries = subQueries.merge(patternQueries);
				}
			}
		}
		updateObjectRef(objectReference, subQueries);
		return subQueries;
	}

	/**
	 * @param objectReference
	 * @param subQueries
	 */
	private void updateObjectRef(ObjectReference objectReference, JBSubQueries subQueries) {
		if ((objectReference == null) || (subQueries == null)) {
			return;
		}
		objectReference.addSubCollector(subQueries);
	}

	@Override
	public void foundValuePath(final Reference item) {
		//System.out.println(DH.oid(this) + " found value  path:" + currentPath + " direct hit:" + directHits.containsKey(currentPath));
		ElementKey lastElement = currentPath.tail();
//		if ((!queryAllArrayElements) && (!elementsToQuery.contains(lastElement))) {
//			return;
//		}
		PathReferenceStack pathReferenceStack2 = directHits.get(currentPath);
		//directHits.values().toArray()[2].equals(currentPath)
		if (pathReferenceStack2 != null) {
			storeResult(pathReferenceStack2, item);
		}
		Set<PathReferenceStack> patternsToMatch = patterns.get(lastElement);
		if (patternsToMatch == null) {
			if (queryAllArrayElements) {
				patternsToMatch = patterns.get(ElementKey.ALL_ARRAY_CHILDREN);
				if (patternsToMatch == null) {
					return;
				}
			}
			else {
				return;
			}

		}
		for (PathReferenceStack pattern : patternsToMatch) {
			if (pattern.matches(currentPath)) {
				storeResult(pattern, item);
			}
		}
	}

	@Override
	public boolean currentKeyMightBeInterresting(ElementKey reference) {
		if (elementsToQuery.contains(reference)) {
			return true;
		}
		if (queryAllArrayElements && (reference instanceof ArrayIndexKey)) {
			return true;
		}
		return false;
	}

	@Override
	public List<Reference> getResultsForPath(final PathReferenceStack path) {
		List<Reference> resultList = results.get(path);
		return resultList == null ? Collections.emptyList() : resultList;
	}

	@Override
	public boolean isPathEmpty() {
		return currentPath.isEmpty();
	}

	@Override
	public ElementKey popPath() {
		return currentPath.pop();
	}

	@Override
	public void pushPath(final ElementKey currentKey) {
		Objects.requireNonNull(currentKey);
		assert currentKey != ElementKey.INOBJECT;
		assert currentKey != ElementKey.ROOT;
		currentPath.push(currentKey);
	}

	private void storeResult(final PathReferenceStack path, final Reference item) {
		//	System.out.println("found item for path " + path);
		List<Reference> resultList = results.get(path);
		if (resultList == null) {
			resultList = new ArrayList<>();
			results.put(path, resultList);
		}
		resultList.add(item);
	}

	@Override
	public String toString() {
		String string = getClass().getSimpleName() + "@" + Integer.toString(hashCode(), Character.MAX_RADIX);
		string += Stream.concat(directHits.keySet().stream(), patterns.keySet().stream()).map(Object::toString).collect(Collectors.joining("<,>", ">", "<"));
		return string;
	}
}
