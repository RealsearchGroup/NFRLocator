package edu.ncsu.csc.nl.model;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc.nl.model.english.StopWord;
import edu.ncsu.csc.nl.model.type.WordType;
import edu.ncsu.csc.nl.test.TestConfiguration;

public class WekaCreatorOptionsTest {

	
	
	@Test
	public void test() {
		WekaCreatorOptions wco = new WekaCreatorOptions();
		
		assertEquals(false, wco.useNodeNumber());
		assertEquals(true, wco.useLemma());
		assertEquals(false, wco.useNamedEntity());
		assertEquals(false, wco.useNERSentenceIndicators());
		assertEquals(false, wco.useOriginalWord());
		assertEquals(false, wco.useParentNodeNumber());
		assertEquals(false, wco.usePartOfSpeech());
		assertEquals(false, wco.useRelationshipToParent());
		assertEquals(1,wco.getNumberOfNodeOptionsSelected());
		assertEquals(false, wco.exportSentenceAsString());
		
		wco = WekaCreatorOptions.createExportSentence();
		wco.setNERSentenceIndicators(true);
		assertEquals(false, wco.useNodeNumber());
		assertEquals(true, wco.useLemma());
		assertEquals(false, wco.useNamedEntity());
		assertEquals(true, wco.useNERSentenceIndicators());
		assertEquals(false, wco.useOriginalWord());
		assertEquals(false, wco.useParentNodeNumber());
		assertEquals(false, wco.usePartOfSpeech());
		assertEquals(false, wco.useRelationshipToParent());
		assertEquals(true, wco.exportSentenceAsString());

		wco = new WekaCreatorOptions(true, false, true, true, true, true, true, false, true);
		assertEquals(true, wco.useNodeNumber());
		assertEquals(false, wco.useLemma());
		assertEquals(true, wco.useNamedEntity());
		assertEquals(true, wco.useOriginalWord());
		assertEquals(true, wco.useParentNodeNumber());
		assertEquals(true, wco.usePartOfSpeech());
		assertEquals(true, wco.useRelationshipToParent());
		assertEquals(6,wco.getNumberOfNodeOptionsSelected());
		assertEquals(false, wco.exportSentenceAsString());		
		assertEquals(true, wco.useNERSentenceIndicators());
		
		wco.setExportSentenceAsString(true);
		wco.setLemma(false);
		wco.setNamedEntity(true);
		wco.setNERSentenceIndicators(false);
		wco.setNodeNumber(true);
		wco.setOriginalWord(false);
		wco.setParentNodeNumber(true);
		wco.setPartOfSpeech(false);
		wco.setRelationshipToParent(true);
		

		assertEquals(true, wco.useNodeNumber());
		assertEquals(false, wco.useLemma());
		assertEquals(true, wco.useNamedEntity());
		assertEquals(false, wco.useOriginalWord());
		assertEquals(true, wco.useParentNodeNumber());
		assertEquals(false, wco.usePartOfSpeech());
		assertEquals(true, wco.useRelationshipToParent());
		assertEquals(4,wco.getNumberOfNodeOptionsSelected());
		assertEquals(true, wco.exportSentenceAsString());		
		assertEquals(false, wco.useNERSentenceIndicators());
		assertEquals(WordType.LEMMA, wco.getWordType());	
		
		wco.setWordType(WordType.ORIGINAL);
		assertEquals(WordType.ORIGINAL, wco.getWordType());	
		
		wco = WekaCreatorOptions.createCasamayorOption();
		
		assertEquals(true, wco.exportSentenceAsString());
		assertEquals(true, wco.useCasamayorSentenceRepresentation());
		assertEquals(false, wco.useOriginalSentence());
		assertEquals(StopWord.getListByName(StopWord.GLASGOW).length, wco.getStopWords().size());
		
		wco.setUseOriginalSentence(false);
		assertEquals(true, wco.useCasamayorSentenceRepresentation());
		assertEquals(false, wco.useOriginalSentence());
		
		wco.setUseOriginalSentence(true);
		assertEquals(false, wco.useCasamayorSentenceRepresentation());
		assertEquals(true, wco.useOriginalSentence());
		
		wco.setUseCasamayorSentenceRepresentation(false);
		assertEquals(false, wco.useCasamayorSentenceRepresentation());
		assertEquals(true, wco.useOriginalSentence());
		
		wco.setStopWords(Arrays.asList(StopWord.getListByName(StopWord.FRAKES)));
		assertEquals(StopWord.getListByName(StopWord.FRAKES).length, wco.getStopWords().size());
	}
	




	
}
