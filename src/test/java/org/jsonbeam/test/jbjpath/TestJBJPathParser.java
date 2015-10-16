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
package org.jsonbeam.test.jbjpath;

import java.io.Reader;
import java.io.StringReader;

import org.jsonbeam.intern.jpath.parser.JBJPathParser;
import org.jsonbeam.intern.jpath.parser.JJTJBJPathParserState;
import org.jsonbeam.intern.jpath.parser.ParseException;
import org.jsonbeam.intern.jpath.parser.SimpleNode;
import org.junit.Test;

/**
 * @author sven
 *
 */
@SuppressWarnings("static-method")
public class TestJBJPathParser {
	
	
	
	@Test
	public void testSimpleParsing() throws ParseException { 
	Reader input = new StringReader("$.abcde.xxx[*].*.arr[23:24:4].AA[1,2,3]..sdfsadf[?@.abc==\"Huhu\"].a[?@.b!=5.08].b[?@.c!='abc']");
//	Reader input = new StringReader(".sdfsadf[?@.abc==\"Huhu\"]");
	JBJPathParser parser = new JBJPathParser(input);
	SimpleNode start = parser.Start();
	dump(start,""); 
	 
	//start.childrenAccept(visitor, data)
	
	
	}
	
	
	 public void dump(SimpleNode node,String prefix) {
		    System.out.println(node.toString(prefix)+":"+dumpImages(node));
		    for (int i=0;i<node.jjtGetNumChildren();++i) {
		    	dump((SimpleNode) node.jjtGetChild(i),prefix+" ");
		    	
		    }
		    
//		    if (node.g.children != null) {
//		      for (int i = 0; i < children.length; ++i) {
//		        SimpleNode n = (SimpleNode)children[i];
//		        if (n != null) {
//		          n.dump(prefix + " ");
//		        }
//		      }
//		    }
		  }


	/**
	 * @param node
	 * @return
	 */
	private String dumpImages(SimpleNode node) {
		return "'"+node.jjtGetFirstToken()+"'";
//		node.jjtGetFirstToken().image
//		return null;
	}
}
