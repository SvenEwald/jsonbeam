package org.jsonbeam.index.model;

import java.util.Optional;

import org.jsonbeam.index.JBSubQueries;

public interface ReferenceWithIndex extends Reference {

	void addSubCollector(JBSubQueries subCollector);

	Optional<JBSubQueries> getSubCollector();
}
