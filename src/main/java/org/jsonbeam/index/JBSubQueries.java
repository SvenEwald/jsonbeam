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
package org.jsonbeam.index;

import java.util.Optional;

import org.jsonbeam.index.keys.ElementKey;
import org.jsonbeam.index.model.ObjectReference;
import org.jsonbeam.index.model.Reference;

public class JBSubQueries extends JBQueries {
	final private JBResultCollector parent;

	public JBSubQueries(final JBResultCollector parent) {
		assert this != parent;
		this.parent = parent;
	}

	@Override
	public String currentPathAsString() {
		return parent.currentPathAsString() + "->" + super.currentPathAsString();
		//	return "'"+super.currentPathAsString()+"' parent:'"+parent.currentPathAsString()+"'";
	}

	@Override
	public Optional<JBSubQueries> foundObjectPath(final ObjectReference item) {
		parent.foundObjectPath(item); //Ignoring parent subqueries for now
		return super.foundObjectPath(item);
	}

	@Override
	public void foundValuePath(final Reference item) {
		parent.foundValuePath(item);
		super.foundValuePath(item);
	}

	public JBSubQueries merge(final JBSubQueries parent) {
		//		System.out.println("this    :"+this.toString());
		//		System.out.println("existing:"+this.parent);
		//		System.out.println("new     :"+parent);
		patterns.putAll(parent.patterns);
		directHits.putAll(parent.directHits);
		elementsToQuery.addAll(parent.elementsToQuery);
		//		throw new RuntimeException("Not implmented yet");
		return this;
	}

	@Override
	public ElementKey popPath() {
		parent.popPath();
		return super.popPath();
	}

	@Override
	public void pushPath(final ElementKey currentKey) {
		parent.pushPath(currentKey);
		super.pushPath(currentKey);

	}

	//	@Override
	//	public String toString() {
	//		String string = "SubQueries direct hits:";
	//		string+=Stream.concat(directHits.keySet().stream(),patterns.keySet().stream()).map(Object::toString).collect(Collectors.joining(","));
	//		return string;
	//	}
}
