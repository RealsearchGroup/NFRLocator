package edu.ncsu.csc.nl.model.english;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc.nl.GCController;
import edu.ncsu.csc.nl.model.NLDocument;
import edu.ncsu.csc.nl.model.Sentence;
import edu.ncsu.csc.nl.test.TestConfiguration;

public class IgnoreWordTest {

	@Before
	public void setUp() throws Exception {
		TestConfiguration.setupEnvironment();
	}	
	
	@Test
	public void testDeterminersAreIgnored() {
		// test for "a" and "the"
		NLDocument document = new NLDocument();
		List<Sentence> results = document.parseSentence(GCController.getTheGCController().getPipeline(), "A  test goes the  way");
		assertEquals("Number of sentences equals 1", 1,results.size());
		Sentence s = results.get(0);
		assertEquals("Number of vertices = 3", 3, s.getRoot().getGraphSize());

		//test for "an"
		results = document.parseSentence(GCController.getTheGCController().getPipeline(), "An envelope is required");
		assertEquals("Number of sentences equals 1", 1,results.size());
		s = results.get(0);
		assertEquals("an is removed", 3, s.getRoot().getGraphSize());		
	}
	

}
