package edu.ncsu.csc.nl.model.english;

import static org.junit.Assert.*;

import org.junit.Test;

public class ReadabilityTest {

	@Test
	public void testSyllables() {
		assertEquals("the",1, Readability.countSyllables("the"));	
		assertEquals("hello",2, Readability.countSyllables("hello"));
		assertEquals("rather",2, Readability.countSyllables("rather"));
		assertEquals("any",2, Readability.countSyllables("any"));
		assertEquals("understand", 3, Readability.countSyllables("understand"));
		assertEquals("equal", 2, Readability.countSyllables("equal"));
		assertEquals("naive",1, Readability.countSyllables("naive"));
		assertEquals("understandability",7, Readability.countSyllables("understandability"));
		assertEquals("greater",2, Readability.countSyllables("greater"));
		//assertEquals("syllable",3, Readability.countSyllables("syllable"));
	}

}
