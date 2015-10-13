package edu.ncsu.csc.nl.playground;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

//import edu.stanford.nlp.dcoref.CorefChain;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;


/**
 * Simple test parser to take an existing sentence and parse it with the NLP
 * 
 * @author John
 *
 */
public class TestFileParser {

	/**
	 * 
	 * Code taken from http://www.java2s.com/Tutorial/Java/0180__File/ReadLinesreadfiletolistofstrings.htm
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static List<String> readLines(String fileName)  {
		List<String> results = new ArrayList<String>();;
		try {
	      BufferedReader reader = new BufferedReader(new FileReader(fileName));
	      String line = reader.readLine();
	      while (line != null) {
	          results.add(line);
	          line = reader.readLine();
	      }
	      reader.close();
		}	
		catch (Exception e) {
			System.out.println(e);
			System.exit(-1);
		}
		return results;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<String> lines = readLines("C:\\Users\\John\\Dropbox\\Research Work\\NL and RBAC\\iTrust_requirements.txt");

	    // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
	    java.util.Properties props = new java.util.Properties();
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    

	    for (String line: lines) {
	    	Annotation document = new Annotation(line);

	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    
	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    java.util.List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    
	    for(CoreMap sentence: sentences) {
	    	/*
	      // traversing the words in the current sentence
	      // a CoreLabel is a CoreMap with additional token-specific methods
	      for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	        // this is the text of the token
	        String word = token.get(TextAnnotation.class);
	        // this is the POS tag of the token
	        String pos = token.get(PartOfSpeechAnnotation.class);
	        // this is the NER label of the token
	        String ne = token.get(NamedEntityTagAnnotation.class);
	        
	        //System.out.println(word+":"+pos+":"+ne);
	       
	      }
	       */
	      System.out.println(sentence);

	      // this is the parse tree of the current sentence
	     // Tree tree = sentence.get(TreeAnnotation.class);
	    
	      //tree.pennPrint();
	     // System.out.println("---------------");
	      //tree.printLocalTree();
	      //System.out.println("---------------");
	      
	     // walkTree(tree,"");
	      
	      // this is the Stanford dependency graph of the current sentence
	      SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
	      dependencies.prettyPrint();
	      //System.out.println("---------------");
	      //System.out.println(toList(dependencies));
	      // System.out.println("---------------");
	      // System.out.println(toPOSList(dependencies));
	      
	      
	      
	      
	    }

	    // This is the coreference link graph
	    // Each chain stores a set of mentions that link to each other,
	    // along with a method for getting the most representative mention
	    // Both sentence and token offsets start at 1!
	    //java.util.Map<Integer, CorefChain> graph =  document.get(edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation.class);
	    }
	}
	
	public static void walkTree(Tree tree, String depth) {
		System.out.println(depth+tree.label());
		
		CoreLabel l = (CoreLabel) tree.label();
		System.out.println(l.value());
		System.out.println(l.getClass().getName());
		System.out.println(depth+"Original Text: "+l.originalText());   // text of the current token
		System.out.println(depth+"Category: "+l.category());         // ROOT,  S, NP, VP, Dyt
		System.out.println(depth+"Begin Position: "+l.beginPosition());
		System.out.println(depth+"End Position: "+l.endPosition());
		System.out.println(depth+"ner: "+l.ner());
		System.out.println(depth+"Tag: "+l.tag());
		
		int numChildren = tree.numChildren();
		System.out.println(numChildren);
		depth = depth+"  ";
		for (int i=0;i<numChildren;i++) {
			walkTree(tree.getChild(i),depth);
		}
		
	}
	
	  public static String toList(SemanticGraph dependencies) {
		    StringBuilder buf = new StringBuilder();
		    for (SemanticGraphEdge edge : dependencies.edgeListSorted()) {
		      buf.append(edge.getRelation().toString()).append("(");
		      buf.append(toDepStyle(edge.getSource())).append(",");
		      buf.append(toDepStyle(edge.getTarget())).append(")\n");
		      

		    }
		    return buf.toString();
	  }

		  /**
		   * Similar to toList(), but uses POS tags instead of word and index.
		   */
		  public static String toPOSList(SemanticGraph dependencies) {
		    StringBuilder buf = new StringBuilder();
		    for (SemanticGraphEdge edge : dependencies.edgeListSorted()) {
		      buf.append(edge.getRelation().toString()).append("(");
		      buf.append(toPOSStyle(edge.getSource())).append(",");
		      buf.append(toPOSStyle(edge.getTarget())).append(")\n");
		      IndexedWord fl = edge.getSource();
		      System.out.println(edu.stanford.nlp.process.Morphology.stemStatic(fl.word(),fl.tag()));		      
		    }
		    return buf.toString();
		  }

		  private static String toDepStyle(IndexedWord fl) {
		    StringBuilder buf = new StringBuilder();
		    buf.append(fl.word());
		    buf.append("-");
		    buf.append(fl.index());
		    return buf.toString();
		  }

		  private static String toPOSStyle(IndexedWord fl) {
		    StringBuilder buf = new StringBuilder();
		    buf.append(fl.word());
		    buf.append("/");
		    buf.append(fl.tag());
		    buf.append("-");
		    buf.append(fl.index());
		    
		    return buf.toString();
		  }	
	
	

}
