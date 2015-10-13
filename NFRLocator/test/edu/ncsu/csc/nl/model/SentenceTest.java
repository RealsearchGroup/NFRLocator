package edu.ncsu.csc.nl.model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.ncsu.csc.nl.test.TestConfiguration;

public class SentenceTest {
	@Before
	public void setUp() throws Exception {
		TestConfiguration.setupEnvironment();
	}	
	
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testGenerateWordVertexListFromString() {
		NLDocument document = TestConfiguration.createBasicDocument();
		Sentence s = document.getElementAt(1); 
		assertEquals(3, s.getNumberOfNodes());
		
		List<WordVertex> newList = s.generateWordVertexListFromString("1");
		assertEquals(1,newList.size());
		assertEquals("write", newList.get(0).getLemma());
		
		newList = s.generateWordVertexListFromString("1,2,3");
		assertEquals(3,newList.size());
		assertEquals("write", newList.get(0).getLemma());
		assertEquals("doctor", newList.get(1).getLemma());
		assertEquals("prescription", newList.get(2).getLemma());
		
	    try {
	    	s.generateWordVertexListFromString("0");
	        fail("should've thrown an index out of bounds");
	    } catch (Exception ex) {
	        assertEquals(IndexOutOfBoundsException.class, ex.getClass());
	    }

		try {
			s.generateWordVertexListFromString("4");
			fail("should've thrown an index out of bounds");
		} catch (Exception ex) {
			assertEquals(IndexOutOfBoundsException.class, ex.getClass());
		}

	    try {
	    	s.generateWordVertexListFromString("asfsd");
	        fail("should've thrown a NumberFormatException");
	    } catch (Exception ex) {
	        assertEquals(NumberFormatException.class, ex.getClass());
	    }	  
	    
		try {
			s.generateWordVertexListFromString("1,2,3,1");
			fail("should've thrown an illegal argument exception");
		} catch (Exception ex) {
			assertEquals(IllegalArgumentException.class, ex.getClass());
		}	    		
	}
	
	@Test
	public void testLegal() {
		NLDocument document = TestConfiguration.createBasicDocument();
		Sentence s = document.getElementAt(1); 
		assertEquals("default value of a sentence is false",false, s.isLegal());
		s.setLegal(true);
		assertEquals("changed to ture",true, s.isLegal());
		s = document.getElementAt(0); 
		assertEquals("default value of a sentence is false",false, s.isLegal());
	}	
	
	@Test
	public void testReferredToSentence() {
		NLDocument document = TestConfiguration.createBasicDocument();
		Sentence s = document.getElementAt(1); 
		assertEquals("default value is -1/ constant",Sentence.UNASSIGNED_SENTENCE_POSITION, s.getReferredToSentence(), 0.0001);
		s.setReferredToSentence(1.0);
		assertEquals("set to the first sentence",1.0, s.getReferredToSentence(), 0.0001);
		s = document.getElementAt(0); 
		assertEquals("default value is -1/ constant",Sentence.UNASSIGNED_SENTENCE_POSITION, s.getReferredToSentence(), 0.0001);
	}	
	
	
	@Test
	public void testCompareSecurityObjectAnnotations() {
		fail("Not yet implemented");
		
		/*
		 - cases to create
		 - annotations are the same - zero, or two
		 - annotation(s) in the current object, but not in the compareTo object.
	     - annotation(s) in the compareTo object, but not the current object
	     - different mitigations or impacts between the same security objective 
		 
		 */
	}	
	
}
