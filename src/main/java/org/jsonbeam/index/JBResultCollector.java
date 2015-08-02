package org.jsonbeam.index;

import java.util.Optional;

import org.jsonbeam.index.keys.ElementKey;
import org.jsonbeam.index.model.ObjectReference;
import org.jsonbeam.index.model.Reference;

public interface JBResultCollector {

	String currentPathAsString();

	Optional<JBSubQueries> foundObjectPath(ObjectReference item);

	//FIXME: split result collecting into object,array and value paths. Maybe literal paths.

	/**
	 * Notifies the Collector that a path was found for the given item.
	 *
	 * @param pathReferenceStack
	 * @param item
	 * @return A list of SubQueries to match on that path.
	 */
	void foundValuePath(Reference item);

	boolean isPathEmpty();

	ElementKey popPath();

	void pushPath(ElementKey currentKey);

}
