package org.jsonbeam.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsonbeam.index.keys.ElementKey;
import org.jsonbeam.index.keys.PathReferenceStack;
import org.jsonbeam.index.model.ObjectReference;
import org.jsonbeam.index.model.Reference;

public class JBQueries implements JBResultCollector, JBResultProvider {

	protected final Set<ElementKey> elementsToQuery = new HashSet<ElementKey>();
	protected final Map<PathReferenceStack, PathReferenceStack> directHits = new HashMap<>();
	protected final Map<PathReferenceStack, List<Reference>> results = new HashMap<PathReferenceStack, List<Reference>>();
	protected final Map<ElementKey, Set<PathReferenceStack>> patterns = new HashMap<>();
	private final PathReferenceStack currentPath = new PathReferenceStack();
	private boolean queryAllArrayElements = false;
	private final Map<PathReferenceStack, Supplier<JBSubQueries>> path2SubQueries = new HashMap<>();

	public void addQuery(final PathReferenceStack query, final Supplier<JBSubQueries> subqueries) {
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
			return;
		}
		patterns.computeIfAbsent(lastElement, k -> new LinkedHashSet<>()).add(query);
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

	public boolean dumpResults(final CharSequence buffer) {
		System.out.println("Query results:");
		results.entrySet().forEach(e -> {
			System.out.println(e.getKey().toString() + ":(" + e.getValue().size() + ")" + e.getValue().stream().map(r -> r.apply(buffer)).collect(Collectors.joining(",")));
		});
		return true;
	}

	@Override
	public Optional<JBSubQueries> foundObjectPath(final ObjectReference item) {
		//		System.out.println(DH.oid(this)+" found object path:"+currentPath+ " direct hit:"+directHits.containsKey(currentPath));
		if (currentPath.isEmpty()) {
			return Optional.empty();
		}

		ElementKey lastElement = currentPath.tail();
		//if (!elementsToQuery.contains(lastElement)) {
		if ((!queryAllArrayElements) && (!elementsToQuery.contains(lastElement))) {
			return Optional.empty();
		}
		//List<JBSubQueries> subCollectors = new ArrayList<>();
		PathReferenceStack pathReferenceStack2 = directHits.get(currentPath);
		Optional<JBSubQueries> subQueries = Optional.empty();
		if (pathReferenceStack2 != null) {
			storeResult(pathReferenceStack2, item);
			subQueries = Optional.ofNullable(path2SubQueries.get(pathReferenceStack2).get());//pathReferenceStack2.getSubCollector();
		}
		Set<PathReferenceStack> patternsToMatch = patterns.get(lastElement);
		if (patternsToMatch == null) {
			if (queryAllArrayElements) {
				patternsToMatch = patterns.get(ElementKey.ALL_ARRAY_CHILDREN);
				if (patternsToMatch == null) {
					return subQueries;
				}
			}
			else {
				return subQueries;
			}

			//	return subQueries;
		}
		for (PathReferenceStack pattern : patternsToMatch) {
			if (!pattern.matches(currentPath)) {
				continue;
			}
			storeResult(pattern, item);
			//if (pattern.getSubCollector().isPresent()) {
			if (path2SubQueries.containsKey(pattern)) {//FIXME: this neeeeeds cleanup
				JBSubQueries patternQueries = path2SubQueries.get(pattern).get();//pattern.getSubCollector().get();
				if (!subQueries.isPresent()) {
					subQueries = Optional.ofNullable(path2SubQueries.get(pattern).get());//pattern.getSubCollector();
				}
				else {
					subQueries = subQueries.map(q -> q.merge(patternQueries)); //FIXME: broken
				}
			}
		}
		return subQueries;
	}

	@Override
	public void foundValuePath(final Reference item) {
		//System.out.println(DH.oid(this) + " found value  path:" + currentPath + " direct hit:" + directHits.containsKey(currentPath));
		ElementKey lastElement = currentPath.tail();
		if ((!queryAllArrayElements) && (!elementsToQuery.contains(lastElement))) {
			return;
		}
		PathReferenceStack pathReferenceStack2 = directHits.get(currentPath);
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
		assert !"$".equals(currentKey.toString());
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