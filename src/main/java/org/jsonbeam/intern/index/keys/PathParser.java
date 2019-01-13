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
import java.util.HashSet;
import java.util.Set;

import org.jsonbeam.exceptions.JBUnimplemented;
import org.jsonbeam.intern.index.JBSubQueries;
import org.jsonbeam.intern.index.model.Reference;
import org.jsonbeam.intern.jpath.parser.ArrayIndex;
import org.jsonbeam.intern.jpath.parser.ArrayPredicate;
import org.jsonbeam.intern.jpath.parser.FloatLiteral;
import org.jsonbeam.intern.jpath.parser.IntLiteral;
import org.jsonbeam.intern.jpath.parser.JBJPathParser;
import org.jsonbeam.intern.jpath.parser.JBJPathParserVisitor;
import org.jsonbeam.intern.jpath.parser.JPath;
import org.jsonbeam.intern.jpath.parser.JSONLiteral;
import org.jsonbeam.intern.jpath.parser.Node;
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
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(StepName node, PathReferenceStack data) {
			data.push(new KeyReference(node.jjtGetFirstToken().image));
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(Separator node, PathReferenceStack data) {
			if ("..".equals(node.jjtGetFirstToken().image)) {
				data.push(ElementKey.WILDCARD);
			}
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(Predicate node, PathReferenceStack data) {
			JPath jpath=(JPath) node.jjtGetChild(0);
			Test test=(Test) node.jjtGetChild(1);			
			Node valueNode =node.jjtGetChild(2);
			PathReferenceStack predicatePath=jpath.jjtAccept(this, null);
			
			data.tail().addSubFilter(predicatePath,r->filter(r,test,valueNode));
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(ArrayPredicate node, PathReferenceStack data) {
			int children = node.jjtGetNumChildren();
			if ((children==0)||(children>2)) {
				throw new JBUnimplemented();
			}
			Node child1 = node.jjtGetChild(0);
			if (child1 instanceof Star) {
				data.push(KeyReference.ALL_ARRAY_CHILDREN);
				return data;
			}
//			if (!(child1 instanceof IntLiteral)) {
//				throw new JBUnimplemented();
//			}
			int firstIndex = toInt(child1);//Integer.parseInt(((IntLiteral) child1).jjtGetFirstToken().image);
			if (children==1) {
				data.push(new ArrayIndexKey(firstIndex));
				return data;
			}
		    Node child2 = node.jjtGetChild(1);		  
		    if (child2 instanceof ArrayIndex) {
		    	int arrayIndexCount = child2.jjtGetNumChildren();
		    	if ((arrayIndexCount<1)||(arrayIndexCount>2)) {
		    		throw new JBUnimplemented();
		    	}
		    	int endIndex = toInt(child2.jjtGetChild(0));//Integer.parseInt(((IntLiteral) child2).jjtGetFirstToken().image);
		    	int step=arrayIndexCount==2 ? toInt(child2.jjtGetChild(0)) : 1;
		    	data.push(new ArrayRangeKey(firstIndex, endIndex, step));
		    	return data;
		    }
		    if (child2 instanceof NumberList) {
		    	Set<Integer> indexes=new HashSet<>();
		    	int arrayIndexListCount = child2.jjtGetNumChildren();
		    	if (!(arrayIndexListCount>0)) {
		    		throw new JBUnimplemented();
		    	}
		    	indexes.add(Integer.valueOf(firstIndex));
		    	for (int i=0;i<arrayIndexListCount;++i) {
		    		int index = toInt(child2.jjtGetChild(i));
		    		indexes.add(Integer.valueOf(index));
		    	}
		    	data.push(new ArrayIndexListKey(indexes));
		    	return data;
		    }
		    throw new JBUnimplemented();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(Star node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(ArrayIndex node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(Test node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(NumberList node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(StringLiteral node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(JSONLiteral node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(IntLiteral node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(FloatLiteral node, PathReferenceStack data) {
			// TODO Auto-generated method stub
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(Start node, PathReferenceStack data) {
			throw new IllegalStateException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PathReferenceStack visit(JPath node, PathReferenceStack data) {
			PathReferenceStack newPath = new PathReferenceStack();
			node.childrenAccept(this, newPath);
			return newPath;
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
			SimpleNode start = parser.Start();
			start.dump("");
			Object newPath = start.jjtGetChild(0).jjtAccept(new Visitor(), null);			
			return (PathReferenceStack) newPath;
		} catch (ParseException e) {
			e.printStackTrace();
			throw new JBUnimplemented();
		}		
	}

	/**
	 * @param r
	 * @param test
	 * @param valueNode
	 * @return
	 */
	private static boolean filter(Reference r, Test test, Node valueNode) {
		String data=r.apply();
		String testSymbol=test.jjtGetFirstToken().image;
		String value=((SimpleNode)valueNode).jjtGetFirstToken().image;
		if (valueNode instanceof StringLiteral) {
			if (! (testSymbol.equals("==")||testSymbol.equals("!="))) {
				throw new JBUnimplemented();
			}
			return value.equals(data) ^ "!=".equals(testSymbol);
		}
		if (valueNode instanceof IntLiteral) {
			
		}
		throw new JBUnimplemented();
	}

	private static int toInt(IntLiteral node) {
		return Integer.parseInt(node.jjtGetFirstToken().image);
	}
	
	private static int toInt(Node node) {
		return toInt((IntLiteral)node);
	}
	
}
