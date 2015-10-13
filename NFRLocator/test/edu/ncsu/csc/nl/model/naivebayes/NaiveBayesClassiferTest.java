package edu.ncsu.csc.nl.model.naivebayes;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;






public class NaiveBayesClassiferTest {
	@Before
	public void setUp() throws Exception {

	}	
	
	
	/**
	 * treats the naivebayes classifier as just multinomial for words
	 */
	@Test
	public void testClassifierAsMultinomial() {
		NaiveBayesClassifier nbc = new NaiveBayesClassifier();
		nbc.addClass("china");
		nbc.addClass("japan");
		nbc.addLikelyhood("text", "words", new String[0]);

		nbc.incrementClass("china");
		nbc.incrementClass("china");
		nbc.incrementClass("china");
		nbc.incrementClass("japan");
				
		// Now build up the data for two classes
		String cWords = "Chinese Beijing Chinese Chinese Chinese Shanghai Chinese Macao";
		String jWords = "Tokyo Japan Chinese";
		
		for (String word: cWords.split(" ")) {
			nbc.incrementClassLikelyhood("china","text", word);
		}

		for (String word: jWords.split(" ")) {
			nbc.incrementClassLikelyhood("japan","text", word);
		}
		
		
		String testString = "Chinese Chinese Chinese Tokyo Japan";

		Object[] arguments = new Object[1];
		arguments[0] = testString.split(" ");
		
		Map<String, Double> result = nbc.computeClassProbabilities(arguments);
		/*
		for (Map.Entry entry : result.entrySet()) {
			System.out.println("Key : " + entry.getKey() 
                                   + " Value : " + entry.getValue());
		}		
		*/

		assertEquals(2,result.size());
		assertEquals( (3.0/4.0) * (3.0/7.0) * (3.0/7.0)* (3.0/7.0) * (1.0/14.0)* (1.0/14.0), result.get("china"), 0.000001  );
		assertEquals( (1.0/4.0) * (2.0/9.0)  * (2.0/9.0) * (2.0/9.0)  * (2.0/9.0)  * (2.0/9.0), result.get("japan"), 0.000001  );
		assertTrue(result.get("china") > result.get("japan"));
		
		assertEquals("china",result.entrySet().iterator().next().getKey());  //since sorted, China should be first

		result = nbc.computeClassProbabilitiesByLogs(arguments);
		assertTrue(result.get("china") > result.get("japan"));
		assertEquals(2,result.size());
		assertEquals( Math.log((3.0/4.0) * (3.0/7.0) * (3.0/7.0)* (3.0/7.0) * (1.0/14.0)* (1.0/14.0)), result.get("china"),     0.000001  );
		assertEquals( Math.log( (1.0/4.0) * (2.0/9.0)  * (2.0/9.0) * (2.0/9.0)  * (2.0/9.0)  * (2.0/9.0)), result.get("japan"), 0.000001  );
		
	}

	/**
	 * 
	 * Data take from sex classification example at http://en.wikipedia.org/wiki/Naive_Bayes_classifier on 11 March 2014
	 */
	@Test
	public void testClassifierAsGaussianElements() {
	
		NaiveBayesClassifier nbc = new NaiveBayesClassifier();
		nbc.addClass("male");
		nbc.addClass("female");
		
		nbc.setClassPrior("male", 0.5);
		nbc.setClassPrior("female", 0.5);
		
		String[] heightArguments = {"5.855", "0.187171","5.4175",".311809"};
		String[] weightArguments = {"176.25", "11.08692924", "132.5", "23.629"};
		String[] footArguments   = {"11.25", ".95742884","7.5","1.291"};
				
		nbc.addLikelyhood("height", "gaussian", heightArguments);
		nbc.addLikelyhood("weight", "gaussian", weightArguments);
		nbc.addLikelyhood("foot", "gaussian", footArguments);

		
		Object[] arguments = {"6","130","8"};
		
		Map<String, Double> result = nbc.computeClassProbabilities(arguments);
		/*for (Map.Entry entry : result.entrySet()) {
			System.out.println("Key : " + entry.getKey() 
                                   + " Value : " + entry.getValue());
		}			
		*/
		assertEquals(2,result.size());
		assertEquals( 0.0000000061984 , result.get("male"), 0.00000000001  );
		assertEquals( 0.00053778, result.get("female"), 0.00000001  );
		assertTrue(result.get("female") > result.get("male"));
		
		assertEquals("female",result.entrySet().iterator().next().getKey());  //since sorted, female should be first
		
		result = nbc.computeClassProbabilitiesByLogs(arguments);
		assertEquals( Math.log(0.0000000061984) , result.get("male"), 0.0001  );
		assertEquals( Math.log(0.00053778), result.get("female"), 0.0001  );
		assertTrue(result.get("female") > result.get("male"));
	}
	
	/**
	 * CookieExample from thinkBayes 
	 * 
	 */
	@Test
	public void testClassifierAsCounts() {
	
		NaiveBayesClassifier nbc = new NaiveBayesClassifier();
		nbc.addClass("bowl 1");
		nbc.addClass("bowl 2");
		
		nbc.setClassPrior("bowl 1", 0.5);
		nbc.setClassPrior("bowl 2", 0.5);
		
		nbc.addLikelyhood("flavor", "count", new String[0]);
		//nbc.addLikelyhood("chocolate", "count", new String[0]);
		
		nbc.setValueForClassLikelyhood("bowl 1", "flavor", "vanilla", 30.0);
		nbc.setValueForClassLikelyhood("bowl 1", "flavor", "chocolate", 10.0);

		nbc.setValueForClassLikelyhood("bowl 2", "flavor", "vanilla", 20.0);
		nbc.setValueForClassLikelyhood("bowl 2", "flavor", "chocolate", 20.0);
		
		
		Object[] arguments = {"vanilla"};
		
		
		Map<String, Double> result = nbc.computeClassProbabilities(arguments);
		/*
		for (Map.Entry entry : result.entrySet()) {
			System.out.println("Key : " + entry.getKey() 
                                   + " Value : " + entry.getValue());
		}	
		*/
		
		assertEquals(2,result.size());
		
		assertEquals( 0.25 , result.get("bowl 2"), 0.00001  );
		assertEquals( 0.375, result.get("bowl 1"), 0.00001  );
		assertFalse(result.get("bowl 1") < result.get("bowl 2"));
		
		assertEquals("bowl 1",result.entrySet().iterator().next().getKey());  //since sorted, female should be first
		

	}	
	
	
}
