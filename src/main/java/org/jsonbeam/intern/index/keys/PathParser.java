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

import java.io.StringReader;

import org.jsonbeam.intern.jpath.parser.ArrayIndex;
import org.jsonbeam.intern.jpath.parser.ArrayPredicate;
import org.jsonbeam.intern.jpath.parser.FloatLiteral;
import org.jsonbeam.intern.jpath.parser.IntLiteral;
import org.jsonbeam.intern.jpath.parser.JBJPathParser;
import org.jsonbeam.intern.jpath.parser.JBJPathParserVisitor;
import org.jsonbeam.intern.jpath.parser.JSONLiteral;
import org.jsonbeam.intern.jpath.parser.NumberList;
import org.jsonbeam.intern.jpath.parser.ParseException;
import org.jsonbeam.intern.jpath.parser.Predicate;
import org.jsonbeam.intern.jpath.parser.Separator;
import org.jsonbeam.intern.jpath.parser.SimpleNode;
import org.jsonbeam.intern.jpath.parser.Star;
import org.jsonbeam.intern.jpath.parser.Start;
import org.jsonbeam.intern.jpath.parser.StepName;
import org.jsonbeam.intern.jpath.parser.StringLiteral;
import org.jsonbeam.intern.jpath.parser.Test;

/**
 * @author Sven
 */
public class PathParser {
	//
	private static class Visitor implements JBJPathParserVisitor {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(SimpleNode node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(StepName node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(Separator node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(Predicate node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(ArrayPredicate node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(Star node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(ArrayIndex node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(Test node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(NumberList node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(StringLiteral node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(JSONLiteral node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(IntLiteral node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(FloatLiteral node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(Start node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	/**
	 * @param query
	 * @return
	 */
	public static PathReferenceStack parse(String query) {
		//		System.out.println(query);
		JBJPathParser parser = new JBJPathParser(new StringReader(query));
		try {
			PathReferenceStack newPath = new PathReferenceStack();
			parser.Start().childrenAccept(new Visitor(), newPath);
			return newPath;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}
