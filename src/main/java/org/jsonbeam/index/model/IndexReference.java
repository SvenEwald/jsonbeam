package org.jsonbeam.index.model;

import java.util.Objects;
import java.util.Optional;

import org.jsonbeam.index.JBSubQueries;

public abstract class IndexReference implements ReferenceWithIndex {

	private JBSubQueries subCollector=null;

	@Override
	public void addSubCollector(final JBSubQueries subCollector) {
		Objects.requireNonNull(subCollector);
		if (this.subCollector==null) {
			this.subCollector=subCollector;
			return;
		}
		this.subCollector.merge(subCollector);
	}

	@Override
	public Optional<JBSubQueries> getSubCollector() {
		return Optional.ofNullable(subCollector);
	}

}
