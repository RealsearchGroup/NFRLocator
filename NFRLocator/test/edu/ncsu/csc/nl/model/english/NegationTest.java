package edu.ncsu.csc.nl.model.english;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.POS;
import edu.ncsu.csc.nl.test.TestConfiguration;

public class NegationTest {
	Dictionary _wordNetDictionary;
	
	@Before
	public void setUp() throws Exception {
		
		try {
			java.io.File wordnetDir = new java.io.File(TestConfiguration.wordNetDictionaryLocation);
			_wordNetDictionary = new Dictionary(wordnetDir);
			_wordNetDictionary.open();
		}
		catch (java.io.IOException ex) {
			System.err.println("Unable to create dictionary from this location: "+TestConfiguration.wordNetDictionaryLocation);
			System.exit(0);
		}		
	}

	@Test
	public void test() {
		
		Assert.assertFalse(Negation.isWordNegativeFromPrefix(_wordNetDictionary,"doctor",POS.NOUN));
		Assert.assertTrue(Negation.isWordNegativeFromPrefix(_wordNetDictionary,"disallow",POS.VERB));
		//fail("Not yet implemented");
	}

}
