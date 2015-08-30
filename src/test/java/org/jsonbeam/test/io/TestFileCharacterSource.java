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
package org.jsonbeam.test.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.jsonbeam.intern.io.FileCharacterSource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Sven
 *
 */
public class TestFileCharacterSource {
	
	@Test
	public void testUTF8FileReading() throws IOException {
		File file = new File("src/test/java/org/jsonbeam/test/examples/ActionLabel.json");
		FileCharacterSource source = new FileCharacterSource(file, StandardCharsets.UTF_8);
		try (Closeable closeme=source.ioHandle();Scanner s=new Scanner(file, "UTF-8")) {
		String orig = s.useDelimiter("\\A").next();
		StringBuilder builder = new StringBuilder();
		while (source.hasNext()) {
			builder.append(source.getNext());
		}
		assertEquals(orig,builder.toString());
		}
	}
}
