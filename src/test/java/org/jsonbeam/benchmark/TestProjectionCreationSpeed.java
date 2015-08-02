package org.jsonbeam.benchmark;

import java.util.Scanner;

import org.jsonbeam.jsonprojector.projector.BCJSONProjector;
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
		DefiantExample2 projection = new BCJSONProjector().projectJSONString(json, DefiantExample2.class);
		projection.getBalances();
		projection.getSubSubProjection();
		//		}
	}
}
