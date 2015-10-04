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
package org.jsonbeam.benchmark;

import java.util.Scanner;

import org.jsonbeam.intern.io.CharSeqCharacterSource;
import org.jsonbeam.intern.io.CharacterSource;
import org.jsonbeam.intern.projector.JBProjector;
import org.jsonbeam.test.examples.DefiantExample2;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

@BenchmarkOptions(callgc = false, benchmarkRounds = 20, warmupRounds = 7)
public class TestProjectionCreationSpeed extends AbstractBenchmark {

	private final static CharSequence json = new Scanner(DefiantExample2.class.getResourceAsStream("DefiantExample2.json")).useDelimiter("\\A").next();

	@Test
	public void testDefiantExample3() {
		//		for (int i = 0; i < 1; ++i) {
		CharacterSource source = new CharSeqCharacterSource(json);
		DefiantExample2 projection = new JBProjector().projectCharacterSource(source, DefiantExample2.class);
		projection.getBalances();
		projection.getSubSubProjection();
		//		}
	}
}
