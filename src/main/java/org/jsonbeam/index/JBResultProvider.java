package org.jsonbeam.index;

import java.util.List;

import org.jsonbeam.index.keys.PathReferenceStack;
import org.jsonbeam.index.model.Reference;

public interface JBResultProvider {

	List<Reference> getResultsForPath(PathReferenceStack path);
}
