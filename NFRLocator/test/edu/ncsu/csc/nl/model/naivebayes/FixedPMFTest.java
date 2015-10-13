package edu.ncsu.csc.nl.model.naivebayes;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;




public class FixedPMFTest {
	@Before
	public void setUp() throws Exception {

	}	
	
	
	@Test
	public void testAsDie() {
		int size = 6;
		double p = 1.0/6.0;
		FixedPMF diePMF = new FixedPMF();
		
		for (int i=1;i<=size;i++) {
			String item = Integer.toString(i);
			diePMF.setValue(item, p);
		}
		
		double checkP = diePMF.getProbability("2");
		assertEquals(p,checkP, 0.0001);
		
	}

	/**
	 * Example taken from Dan Jurafsky in his class notes on NaiveBayes
	 */
	@Test
	public void testIncrement() {
		FixedPMF chineseClassWords = new FixedPMF(1.0);
		FixedPMF japaneseClassWords = new FixedPMF(1.0);
		FixedPMF classes = new FixedPMF();
		
		classes.increment("j");
		classes.increment("c");
		classes.increment("c");
		classes.increment("c");

		//Testing the priors
		assertEquals(0.75,classes.getProbability("c"),0.00001);
		assertEquals(0.25,classes.getProbability("j"),0.00001);
		
		// Now build up the data for two classes
		String cWords = "Chinese Beijing Chinese Chinese Chinese Shanghai Chinese Macao";
		String jWords = "Tokyo Japan Chinese";
		
		for (String word: cWords.split(" ")) {
			chineseClassWords.increment(word);
		}

		for (String word: jWords.split(" ")) {
			japaneseClassWords.increment(word);
		}
		
		//THIS IS WRONG.  NEEDS TO BE UNIQUE ITEMS!!!
		HashSet<String> words = new HashSet<String>();
		words.addAll(chineseClassWords.getAllItems());
		words.addAll(japaneseClassWords.getAllItems());
		double totalCount = words.size();
		
		chineseClassWords.setSmoothingElementCount(totalCount);
		japaneseClassWords.setSmoothingElementCount(totalCount);
		
		assertEquals(chineseClassWords.getProbability("Chinese"), 3.0/7.0, 0.00001 );
		assertEquals(chineseClassWords.getProbability("Tokyo"), 1.0/14.0, 0.00001 );
		assertEquals(chineseClassWords.getProbability("Japan"), 1.0/14.0, 0.00001 );

		assertEquals(japaneseClassWords.getProbability("Chinese"), 2.0/9.0, 0.00001 );
		assertEquals(japaneseClassWords.getProbability("Tokyo"), 2.0/9.0, 0.00001 );
		assertEquals(japaneseClassWords.getProbability("Japan"), 2.0/9.0, 0.00001 );
		
		String testString = "Chinese Chinese Chinese Tokyo Japan";
		
		double probChinese = classes.getProbability("c");
		double probJapanese = classes.getProbability("j");
		for (String word: testString.split(" ")) {
			probChinese *= chineseClassWords.getProbability(word);
			probJapanese *= japaneseClassWords.getProbability(word);
		}
		
		assertEquals(probChinese, (3.0/4.0) * (3.0/7.0) * (3.0/7.0)* (3.0/7.0) * (1.0/14.0)* (1.0/14.0), 0.000001  );
		assertEquals(probJapanese, (1.0/4.0) * (2.0/9.0)  * (2.0/9.0) * (2.0/9.0)  * (2.0/9.0)  * (2.0/9.0), 0.000001  );
		assertTrue(probChinese > probJapanese);

	}
	
	
	
}
