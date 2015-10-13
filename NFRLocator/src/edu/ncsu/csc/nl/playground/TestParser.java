package edu.ncsu.csc.nl.playground;

//import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
//import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
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
public class TestParser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
	    java.util.Properties props = new java.util.Properties();
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    
	    // read some text in the text variable
	    String text = "The administrator should periodically update the prescription codes.";
	    text= "The doctor may order a prescription for the patient.  The nurse shall not order prescriptions.";
	    /*
	    text= "Prescriptions shall not be ordered by a nurse";
	    text = "The doctor may order a prescription for the patient.  The nurse shall not order prescriptions.";
	    text = "Prescriptions can only be ordered by a doctor for his or her patient.  Prescriptions shall not be ordered by nurses for any patients.";
	    
	    
	   text = "An HCP may upload a photo of the patient as part of the patient's demographic records [E2].";
	   text = "An HCP can create a lab procedure for a given office visit [S1]. An HCP can view a previously created lab procedure for a given office visit [S2]. An HCP can reassign a previously created lab procedure [S3]. An HCP can remove a previously created lab procedure [S4]. An HCP can add commentary to a pending lab procedure and update the lab procedure status to completed [S5]. A patient may view his or her own lab procedure results [S6]. A Lab Technician can view his or her priority queue of lab procedures [S7]. A Lab Technician can record the results of a lab procedure [S8]. A Lab Technician can update the status of a lab procedure as received [S9]. All events are logged (UC5)";
	   text = "Health Care Personnel (HCP): All of designated licensed health care professionals, licensed health care professionals, and unlicensed authorized personnel, as defined below.";
	   
	   text= "[S1] A patient or personal health representative may enter or edit their own demographic information including their security question/answer according to data format 6.1.";
	    //text="This is a sample sentence";
	    //text="Bell, based in Los Angeles, makes and distributes electronic, computer and building products.";
	    //text = "Bills on ports and immigration were submitted by Senator Brownbac.";*/
	   
	   //text = "The university is the largest one of its kind in the universe.  He is being very foolish.  I am happy.  He is happy.";
	    text ="The weather is often bad in South Bend.  As such our committee should meet elsewhere.";
	    text = "No nurse may write a prescription.  Nurse write no prescriptions.";
	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(text);
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    
	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    java.util.List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    
	    for(CoreMap sentence: sentences) {
	      // traversing the words in the current sentence
	      // a CoreLabel is a CoreMap with additional token-specific methods
	      for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	        // this is the text of the token
	        String word = token.get(TextAnnotation.class);
	        // this is the POS tag of the token
	        String pos = token.get(PartOfSpeechAnnotation.class);
	        // this is the NER label of the token
	        String ne = token.get(NamedEntityTagAnnotation.class);
	        String lemma = token.get(LemmaAnnotation.class);
        
	        System.out.println(word+":"+pos+":"+ne+":"+lemma);
	        
	      }

	      // this is the parse tree of the current sentence
	      //Tree tree = sentence.get(TreeAnnotation.class);
	    
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
	      System.out.println("---------------");
	      System.out.println(toPOSList(dependencies));
	      
	    }

	    // This is the coreference link graph
	    // Each chain stores a set of mentions that link to each other,
	    // along with a method for getting the most representative mention
	    // Both sentence and token offsets start at 1!
	    //java.util.Map<Integer, CorefChain> graph =     document.get(edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation.class);

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
