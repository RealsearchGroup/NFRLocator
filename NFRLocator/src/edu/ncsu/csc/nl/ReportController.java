package edu.ncsu.csc.nl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import java.util.Map;

import edu.ncsu.csc.nl.model.NLDocument;
import edu.ncsu.csc.nl.model.Sentence;

import edu.ncsu.csc.nl.model.classification.BooleanClassification;
import edu.ncsu.csc.nl.model.classification.ClassificationAttribute;
import edu.ncsu.csc.nl.model.classification.ClassificationType;



/**
 * 
 * 
 * @author Adminuser
 */
public class ReportController implements ActionListener {
	private static ReportController _theReportController = new ReportController();
	public static ReportController getTheReportController() {return _theReportController; }
	
	
	private ReportController() {	}


	@Override
	public void actionPerformed(ActionEvent ae) {
		GCController.getTheGCController().setStatusMessage("");
		
		switch (ae.getActionCommand()) {
			case GCConstants.ACTION_REPORT_DOCUMENT_STATISTICS: GCController.getTheGCController().getCurrentDocument().produceReport(); return;
			case GCConstants.ACTION_REPORT_FREQUENCY: produceFrequencyReport(); return;
			case GCConstants.ACTION_REPORT_FREQUENCY_BY_CLASSIFICATION: produceFrequencyByClassReport(); return;
			case GCConstants.ACTION_REPORT_FREQUENCY_SPREADSHEET: produceFrequencySpreadsheetReport(); return;
			case GCConstants.ACTION_REPORT_CLASSIFICATION_SENTENCES: produceSentencesByClassificationReport(); return;
			case GCConstants.ACTION_REPORT_CUSTOM: customReport(); return;
		}
	}
	
	public void produceFrequencyReport() {
		java.util.HashMap<String,Integer> frequency = GCController.getTheGCController().getCurrentDocument().produceWordCount();
		edu.ncsu.csc.nl.util.Utility.printFrequencyTable(frequency, new PrintWriter(System.out),null);
	}
	
	public void produceFrequencyByClassReport() {
		for (ClassificationAttribute ca: GCController.getTheGCController().getClassificationAttributes().getAttributeList()) {
			java.util.HashMap<String,Integer> frequency = GCController.getTheGCController().getCurrentDocument().produceWordCountForClassification(ca);
			edu.ncsu.csc.nl.util.Utility.printFrequencyTable(frequency, new PrintWriter(System.out),ca.getName());
		}		
	}
	
	public void produceFrequencySpreadsheetReport() {
		NLDocument currentDocument = GCController.getTheGCController().getCurrentDocument();
		java.util.List<ClassificationAttribute> classificationAttributeList = GCController.getTheGCController().getClassificationAttributes().getAttributeList();
		
		double numTrainedSentences = currentDocument.getNumberOfTrainedSentences();
		java.util.HashMap<String,Integer> frequency = currentDocument.produceWordCount();
		java.util.HashMap<ClassificationAttribute, HashMap<String,Integer>> wordCountByClass = new java.util.HashMap<ClassificationAttribute, HashMap<String,Integer>>();
		java.util.HashMap<String,Integer> wordCountNonApplicableClass = currentDocument.produceWordCountForClassification(null); 
				
		for (ClassificationAttribute ca: classificationAttributeList) {
			wordCountByClass.put(ca,  currentDocument.produceWordCountForClassification(ca));
		}	
		
		// Now produce the count of classifications for each category
		HashMap<String,Double> classifiedCount = new HashMap<String,Double>();
		double numberOfNotApplicableSentences = 0.0;
		for (Sentence s: currentDocument.getSentences()) {
			if (s.isTrained()) {
				String[] classifications = s.getBooleanClassificationsAsStringArray();			
				for (String key: classifications) {
					double count = classifiedCount.containsKey(key) ? classifiedCount.get(key) : 0.0;
					classifiedCount.put(key, count + 1.0);
				}
				if (classifications.length == 0) {
					numberOfNotApplicableSentences++;
				}
			}
		}
		
		//sort the words alphabetically
		ArrayList<String> words = new ArrayList<String>( frequency.keySet());
		Collections.sort(words);

		//print header
		System.out.print("Word\tTotalFrequency\tSum TFIDF");//\tNA Count\tNA Freq\tNA TF-IDF");
		for (ClassificationAttribute ca: classificationAttributeList) {
			System.out.print("\t");
			System.out.print(ca.getName()+" Count");
			System.out.print("\t");
			System.out.print(ca.getName()+" Freq");
			System.out.print("\t");
			System.out.print(ca.getName()+" TF-IDF");
			System.out.print("\t");
			System.out.print(ca.getName()+" Normalized TF-IDF");
		}
		System.out.println("");
		
		//print body
		for (String word: words) {
			// compute the number of documents containing the word.
			double numDocumentsContainingWord = 0.0;
			for (Sentence s: currentDocument.getSentences()) {
				if (s.isTrained() && s.hasLemma(word)) {
					numDocumentsContainingWord ++;					
				}
			}
			double idf = Math.log10(numTrainedSentences/numDocumentsContainingWord);
			
			System.out.print(word);
			System.out.print("\t");
			System.out.print(frequency.get(word));  // Total Frequency of word in document
			
			System.out.print("\t");
			double sumTF_IDF = 0.0;
			/*
			double naTermFrequency =  ((wordCountNonApplicableClass.containsKey(word)? wordCountNonApplicableClass.get(word):0));
			naTermFrequency  = naTermFrequency/frequency.get(word);
			
			System.out.print((wordCountNonApplicableClass.containsKey(word)? wordCountNonApplicableClass.get(word):0));  //Frequency of word in Na sentences
			System.out.print("\t");
			System.out.print(naTermFrequency);
			System.out.print("\t");
			
			double naTF_IDF = naTermFrequency*idf;
			 sumTF_IDF =naTF_IDF;
			System.out.print(naTF_IDF);
			*/
			for (ClassificationAttribute ca: classificationAttributeList) {
				java.util.HashMap<String,Integer> classCount = wordCountByClass.get(ca);
				if (classCount.containsKey(word)) {
					double tf = classCount.get(word)/classifiedCount.get(ca.getName());
					double tfIDF = tf*idf;
					sumTF_IDF += tfIDF;
				}
			}
			System.out.print("\t");
			System.out.print(sumTF_IDF);
			for (ClassificationAttribute ca: classificationAttributeList) {
				System.out.print("\t");
			
				java.util.HashMap<String,Integer> classCount = wordCountByClass.get(ca);
				if (classCount.containsKey(word)) {
					System.out.print(classCount.get(word));
					System.out.print("\t");
					double tf = classCount.get(word)/classifiedCount.get(ca.getName());
					System.out.print(tf);
					System.out.print("\t");
					
					double tfIDF = tf*idf;
					System.out.print(tfIDF);
					System.out.print("\t");
					System.out.print(tfIDF/sumTF_IDF);
				}
				else {
					System.out.print("0\t0\t0\t0");
				}
				
			}			
			
			
			
			System.out.println();
		}
	}
	
	/**
	 * 
	 * @param al list of classifications 
	 */
	public void produceSentencesByClassificationReport() {  
			
		
		System.out.println("=====================================");
		java.util.List<ClassificationAttribute> classificationAttributeList = GCController.getTheGCController().getClassificationAttributes().getAttributeList();
		for (ClassificationAttribute ca: classificationAttributeList) {
			for (Sentence s:  GCController.getTheGCController().getCurrentDocument().getSentences()) {
				if (s.isTrained()) {
					ClassificationType ct = s.getClassifications().get(ca.getName());
					if (ct != null && ct instanceof BooleanClassification && ((BooleanClassification) ct).getValue().getBooleanValue()) {
						System.out.println(ca.getName()+"\t"+s);
					}
				}
			}
		}		
		// now display sentences with no classifications
		for (Sentence s: GCController.getTheGCController().getCurrentDocument().getSentences()) {
			if (s.isTrained() && s.hasBooleanClassifications() == false) {
				System.out.println("none\t"+s);
			}
		}
	}	
		
	
	
	
	public void customReport() {

			
	}
	
	
	static class ValueComparator implements Comparator<String> {
	    Map<String, Integer> base;
	    public ValueComparator(Map<String, Integer> base) {
	        this.base = base;
	    }
	    public int compare(String a, String b) {
	    	 if (base.get(a) >= base.get(b)) {
	             return -1;
	         } else {
	             return 1;
	         }
	    }
	}
	
	
	
}
