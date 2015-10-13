package edu.ncsu.csc.nl.model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc.nl.GCController;
import edu.ncsu.csc.nl.test.TestConfiguration;

public class NLDocumentTest {
	
	@Before
	public void setUp() throws Exception {
		TestConfiguration.setupEnvironment();
	}	
	
	
	@Test
	public void testBasicSentenceCreation() {
		NLDocument document = new NLDocument();
		List<Sentence> results = document.parseSentence(GCController.getTheGCController().getPipeline(), "doctors write prescriptions");
		assertEquals("Number of sentences equals 1", 1,results.size());
		Sentence s = results.get(0);
		assertEquals("Number of vertices = 3", s.getNumberOfNodes(), s.getRoot().getGraphSize());
		assertEquals(s.getWordVertexAt(0).getLemma(),"write");
		assertEquals(s.getWordVertexAt(1).getLemma(),"doctor");
		assertEquals(s.getWordVertexAt(2).getLemma(),"prescription");
		
		results = document.parseSentence(GCController.getTheGCController().getPipeline(), "The doctors write many prescriptions");
		assertEquals("Number of sentences equals 1", 1,results.size());
		s = results.get(0);
		assertEquals("Number of vertices = 4", s.getNumberOfNodes(), s.getRoot().getGraphSize());
		assertEquals("write",s.getWordVertexAt(0).getLemma());
		assertEquals("doctor",s.getWordVertexAt(1).getLemma());
		assertEquals("prescription",s.getWordVertexAt(2).getLemma());		
		assertEquals("many",s.getWordVertexAt(3).getLemma());	
		assertEquals(2,s.getRoot().getNumberOfChildren());
		assertEquals(0,s.getRoot().getNumberOfParents());
		
	}

	@Test
	public void testCollapsingSentenceCreation() {
		NLDocument document = new NLDocument();
		List<Sentence> results = document.parseSentence(GCController.getTheGCController().getPipeline(), "licensed doctors write prescriptions.");
		assertEquals("Number of sentences equals 1", 1,results.size());
		Sentence s = results.get(0);
		assertEquals("Number of vertices = 3", s.getNumberOfNodes(), s.getRoot().getGraphSize());
		assertEquals(s.getWordVertexAt(0).getLemma(),"write");
		assertEquals("licensed doctor",s.getWordVertexAt(1).getLemma());
		assertEquals(s.getWordVertexAt(2).getLemma(),"prescription");
	}
	
	
	
	@Test
	public void testComplete() {
		fail("More tests need to be written");
	}

	
	@Test
	public void testAbbrevations() {
		NLDocument nld = new NLDocument();
		
		assertEquals(0,nld.getNumberOfAbbrevations());
		nld.addAbbreviation("HCP", "health care professional");
		assertEquals(1,nld.getNumberOfAbbrevations());
		assertFalse(nld.isAbbreviationDefined("HC"));		
		assertTrue(nld.isAbbreviationDefined("HCP"));

		nld.addAbbreviation("MID", "medical identifier");
		assertEquals(2,nld.getNumberOfAbbrevations());
		assertFalse(nld.isAbbreviationDefined("MI"));		
		assertTrue(nld.isAbbreviationDefined("MID"));
		
		nld.addAbbreviation("RBAC","Role Based Access Control");
		nld.addAbbreviation("NCSU", "North Carolina State University");

		assertEquals(4,nld.getNumberOfAbbrevations());
		
		java.util.Set<String> s= nld.getAbbreviationSet();
		assertEquals(4,s.size());
		for (String k: s) {
			assertTrue(nld.isAbbreviationDefined(k));
		}
		
		assertEquals("medical identifier",nld.getTextForAbbrevation("MID"));
		
	}
	
	@Test
	public void testSerializingfiles() {
		// tests that children are processed first, such that we can rollup changes
		NLDocument document = TestConfiguration.createBasicDocument();
		
		
		
		java.io.File testFile = new java.io.File(TestConfiguration.testSerializationFileLocation);
		try {
			document.writeToSerialFile(testFile);
		}
		catch (Exception e) {
			assertEquals("",e.toString());
			return;
		}
		document = null;
		try {
			document = NLDocument.readFromSerialFile(testFile);
		}
		catch (Exception e) {
			assertEquals("",e.toString());
			return;
		}
		
		TestConfiguration.validateBasicDocument(document);
	}
			
	@Test
	public void testJSONFiles() {
		// tests that children are processed first, such that we can rollup changes
		NLDocument document = TestConfiguration.createBasicDocument();
		
		java.io.File testFile = new java.io.File(TestConfiguration.testJSONFileLocation);
		try {
			document.writeToJSONFile(testFile);
		}
		catch (Exception e) {
			assertEquals("",e.toString());
			return;
		}
		
		document = null;
		try {
			document = NLDocument.readFromJSONFile(testFile);
		}
		catch (Exception e) {
			assertEquals("",e.toString());
			return;
		}
		
		
		TestConfiguration.validateBasicDocument(document);
	}	
	

}
