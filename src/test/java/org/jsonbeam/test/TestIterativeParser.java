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
package org.jsonbeam.test;

import java.util.Scanner;

import org.jsonbeam.index.JBQueries;
import org.jsonbeam.index.model.Reference;
import org.jsonbeam.jsonprojector.parser.IterativeJSONParser;
import org.jsonbeam.jsonprojector.projector.BCJSONProjector;
import org.jsonbeam.test.examples.ActionLabel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestIterativeParser {

	private final String json = new Scanner(Thread.currentThread().getContextClassLoader().getResourceAsStream("org/jsonbeam/test/examples/ActionLabel.json")).useDelimiter("\\A").next();

	@Test
	public void testActionLabelParsingStaticPathResolverFast() {
		JBQueries queries = new JBQueries();
		//		PathReferenceStack query = new PathReferenceStack();
		//		query.push(new KeyReference("menu"));
		//		query.push(new KeyReference("header"));
		//		queries.addQuery(query );
		//Reference index = new BCParserRefactored(json.toCharArray(),queries).createIndex();
		//index.toString();
		//assertEquals("SVG Viewer", index.getChildren().get(0).getChildren().get(0).apply(json.toCharArray()));
		//index.dump(json, "", false);
		//queries.dumpResults(json.toCharArray());
		Reference reference = new IterativeJSONParser(json, queries).createIndex().getRootReference();
		reference.dump(json);
	}

	@Test
	public void testBCProjector() {
		ActionLabel actionLabel = new BCJSONProjector().onJSONString(json).createProjection(ActionLabel.class);
		assertEquals("SVG Viewer", actionLabel.getHeader());
	}

	@Test
	public void testBCProjectorMulti() {
		ActionLabel actionLabel = new BCJSONProjector().onJSONString(json).createProjection(ActionLabel.class);
		assertEquals(18, actionLabel.getAllIds().size());
	}

	@Test
	public void testBCProjectorProjection() {
		ActionLabel actionLabel = new BCJSONProjector().onJSONString(json).createProjection(ActionLabel.class);
		//assertNotNull(actionLabel.getFirstItem());
		assertEquals("Open", actionLabel.getFirstItem().getId());
	}

	//	@Test
	//	public void testActionLabelParsingBCMultipleResults() {
	//		JBQueries queries = new JBQueries();
	//		PathReferenceStack query = new PathReferenceStack();
	//		query.push(new KeyReference("menu"));
	//		query.push(new KeyReference("items"));
	//		query.push(new KeyReference("label"));
	//		queries.addQuery(query );
	//		Reference index = new BCParserRefactored(json.toCharArray(),queries).createIndex();
	//		//index.toString();
	//		//assertEquals("SVG Viewer", index.getChildren().get(0).getChildren().get(0).apply(json.toCharArray()));
	//		//index.dump(json, "", false);
	//		queries.dumpResults(json.toCharArray());
	//	}
	//
	//	@Test
	//	public void testActionLabelParsingBCMultiplePattern() {
	//		JBQueries queries = new JBQueries();
	//		PathReferenceStack query = new PathReferenceStack();
	//		query.push(PathReferenceStack.WILDCARD);
	//		query.push(new KeyReference("id"));
	//		queries.addQuery(query );
	//		PathReferenceStack query2 = new PathReferenceStack();
	//		query2.push(new KeyReference("menu"));
	//		query2.push(new KeyReference("items"));
	//		query2.push(new KeyReference("label"));
	//		queries.addQuery(query2);
	//		Reference index = new BCParserRefactored(json.toCharArray(),queries).createIndex();
	//		//index.toString();
	//		//assertEquals("SVG Viewer", index.getChildren().get(0).getChildren().get(0).apply(json.toCharArray()));
	//		//index.dump(json, "", false);
	//		queries.dumpResults(json.toCharArray());
	//	}

	//	@Test
	//	public void testActionLabelParsing() {
	//		Object root = new org.jsonbeam.jsonprojector.parser.boon.JsonFastParser().parse(json.toCharArray());//(json.toCharArray()).createIndex();
	//		root.toString();
	//		((Map) root).get("menu");
	//	}
}
