package edu.ncsu.csc.nl.model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc.nl.GCController;
import edu.ncsu.csc.nl.test.TestConfiguration;

public class WordVertexTest {

	@Before
	public void setUp() throws Exception {
		TestConfiguration.setupEnvironment();
	}	
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

	
	@Test
	public void testCollapsing() {
		// tests that children are processed first, such that we can rollup changes
		NLDocument document = new NLDocument();
		List<Sentence> results = document.parseSentence(GCController.getTheGCController().getPipeline(), "iTrust Medical Care Requirements Specification");
		assertEquals("Number of sentences equals 1", 1,results.size());
		Sentence s = results.get(0);
		assertEquals("Number of vertices = 1", 1, s.getRoot().getGraphSize());

		//test words in the Adjective selector list are not collapsed
		results = document.parseSentence(GCController.getTheGCController().getPipeline(), "Doctors write many prescriptions.");
		assertEquals("Number of sentences equals 1", 1,results.size());
		s = results.get(0);
		assertEquals("no collapsing should occur", 4, s.getRoot().getGraphSize());		
		
		//test words that are JJR (comparatives) are not collapsed
		results = document.parseSentence(GCController.getTheGCController().getPipeline(), "Doctors write better prescriptions.");
		assertEquals("Number of sentences equals 1", 1,results.size());
		s = results.get(0);
		assertEquals("no collapsing should occur", 4, s.getRoot().getGraphSize());		
		
		//test words that are JJR (superlatives) are not collapsed
		results = document.parseSentence(GCController.getTheGCController().getPipeline(), "Doctors write best prescriptions.");
		assertEquals("Number of sentences equals 1", 1,results.size());
		s = results.get(0);
		assertEquals("no collapsing should occur", 4, s.getRoot().getGraphSize());
		
		//test words that possive words are not collapsible
		results = document.parseSentence(GCController.getTheGCController().getPipeline(), "Administrators can update the doctor's patient list.");
		assertEquals("Number of sentences equals 1", 1,results.size());
		s = results.get(0);
		assertEquals("doctor's should remain", 5, s.getRoot().getGraphSize());	
	}

	@Test
	public void testSubgraphGeneration() {

		// tests that children are processed first, such that we can rollup changes
		NLDocument document = new NLDocument();
		List<Sentence> results = document.parseSentence(GCController.getTheGCController().getPipeline(), "Doctors may write many prescriptions.");
		assertEquals("Number of sentences equals 1", 1,results.size());
		Sentence s = results.get(0);
		assertEquals("Number of vertices = 5", 5, s.getRoot().getGraphSize());
		
		List<String> subgraphs = s.getRoot().extractAllSubgraphPatterns(1, 100,4);
		for (String s1: subgraphs) {
			System.out.println(s1);
		}
		
		String sentence = "At the medical office, the doctors may write prescriptions and lab orders.";
		sentence = "Often, this document can be used as an informal data dictionary, capturing data definitions";
		sentence = "Often, this document can be used as an informal data dictionary, capturing data definitions so that use-case descriptions and other project documents can focus on what the system must do with the information.";
		System.out.println(sentence);
		 results = document.parseSentence(GCController.getTheGCController().getPipeline(),sentence);// );
		assertEquals("Number of sentences equals 1", 1,results.size());
		s = results.get(0);
		//assertEquals("Number of vertices = 5", 5, s.getRoot().getGraphSize());
		
		subgraphs = s.getRoot().extractAllSubgraphPatterns(1, 100,6);
		for (String s1: subgraphs) {
			System.out.println(s1);
		}
	}
}
