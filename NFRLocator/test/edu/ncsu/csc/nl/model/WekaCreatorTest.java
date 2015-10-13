package edu.ncsu.csc.nl.model;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc.nl.GCController;
import edu.ncsu.csc.nl.model.classification.ClassificationAttribute;
import edu.ncsu.csc.nl.model.english.StopWord;
import edu.ncsu.csc.nl.model.type.NamedEntity;
import edu.ncsu.csc.nl.model.type.PartOfSpeech;
import edu.ncsu.csc.nl.model.type.Relationship;
import edu.ncsu.csc.nl.model.type.WordType;
import edu.ncsu.csc.nl.test.TestConfiguration;

public class WekaCreatorTest {

	@Before
	public void setUp() throws Exception {
		TestConfiguration.setupEnvironment();
	}	
		
	
	@Test
	public void testCreatePartOfSpeechStringFastVector() {
		 weka.core.FastVector fv = (new WekaCreator()).createPartOfSpeechStringFastVector();
		 assertEquals(PartOfSpeech.values().length + 1, fv.size());  // the plus one is for the undefined value
		 assertEquals("0", fv.elementAt(0));  // make sure the default is in place
		 PartOfSpeech[] pos = PartOfSpeech.values();
		 for (int i=0;i< pos.length;i++) {
			 assertEquals(pos[i].getActualLabel(), fv.elementAt(i+1));
		 }
	}
	
	@Test
	public void testCreateRelationshipStringFastVector() {
		 weka.core.FastVector fv = (new WekaCreator()).createRelationshipStringFastVector();
		 assertEquals(Relationship.values().length + 1, fv.size());  // the plus one is for the undefined value
		 assertEquals("0", fv.elementAt(0));  // make sure the default is in place
		 Relationship[] rel = Relationship.values();
		 for (int i=0;i< rel.length;i++) {
			 assertEquals(rel[i].getLabel(), fv.elementAt(i+1));
		 }
	}
	
	@Test
	public void testGetMaxNodesInGraph() {
		NLDocument doc = TestConfiguration.createBasicDocument();
		assertEquals(5, (new WekaCreator()).getMaxNodesInGraph(doc.getSentences()));
	}
	
	@Test
	public void testCasamayorStringRepresentation() {
		List<String> stopWords = Arrays.asList(StopWord.getListByName(StopWord.GLASGOW));
		assertEquals("doctor write prescript patient",WekaCreator.getCasamayorStringRepresentation("The 5 doctors write prescriptions for their patients.", stopWords));
		assertEquals("",WekaCreator.getCasamayorStringRepresentation("\t \t \t \t    \t  213 . ; ' : ‘ “ ”", stopWords));
		assertEquals("doctor patient ignor",WekaCreator.getCasamayorStringRepresentation("The  doctor's patients are ignore(s).", stopWords));
		assertEquals("let crazi",WekaCreator.getCasamayorStringRepresentation("Let’s go’ crazy", stopWords));
		assertEquals("g let crazi",WekaCreator.getCasamayorStringRepresentation("(G) Let’s go’ crazy", stopWords));

	}
		
	@Test
	public void testBuildAttributesForClassifications() {
		NLDocument doc = TestConfiguration.createBasicDocument();
		WekaCreator wc = new WekaCreator();
		int maxNodes = wc.getMaxNodesInGraph(doc.getSentences());
		java.util.List<ClassificationAttribute> classificationAttributes = GCController.getTheGCController().getClassificationAttributes().getAttributeList();
		WekaCreatorOptions wo = new WekaCreatorOptions();

		weka.core.FastVector fv =  wc.buildAttributesForClassifications(maxNodes, classificationAttributes, wo);
		assertEquals(maxNodes+1+classificationAttributes.size(), fv.size());
		
		wo.setExportSentenceAsString(true);
		fv =  wc.buildAttributesForClassifications(maxNodes, classificationAttributes, wo);
		assertEquals(2+classificationAttributes.size(), fv.size());
		
		wo.setNERSentenceIndicators(true);
		fv =  wc.buildAttributesForClassifications(maxNodes, classificationAttributes, wo);
		assertEquals(2+classificationAttributes.size() + NamedEntity.NAMED_ENTITY_CATEGORIES.length , fv.size());
		
		for (int i=0; i < classificationAttributes.size(); i++) {
			assertEquals(classificationAttributes.get(i).getName(), ( (weka.core.Attribute)fv.elementAt(i)).name() );
		}
		
		for (int i=0; i < NamedEntity.NAMED_ENTITY_CATEGORIES.length; i++) {
			assertEquals( NamedEntity.NAMED_ENTITY_CATEGORIES[i], ((weka.core.Attribute) fv.elementAt(i+classificationAttributes.size())).name()   );
		}
	}
	
	@Test
	public void testcreateWekaInstancesForClassifications() {
		fail("not implemented");
	}
	
}
