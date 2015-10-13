package edu.ncsu.csc.nl.model.type;


/**
 * The source enumeration represents where did a set value originate?
 * ie, was it something that the system was able to infer, or was set by the user?
 * 
 * @author John
 *
 */
public enum Source {
	
	ANAPHORA_INFERRED_CURRENT_SENTENCE("inferred from current sentence"),
	ANAPHORA_INFERRED_PREVIOUS_SENTENCE("inferred from a previous sentence"),
	DEFAULT("default"),
	NOT_SET("not set"),
	LIST("list"),                     //this was from the dictionary
	LIST_INFER("list-inferred"),     // was able to get via a synonym/hypernom/hyponom
	ML_INSTANCE("ML-Instance"),
	ML_TREE("ML-Tree"),
	ML_RULE("ML-Rule"),
	ML_NAIVE_BAYES("ML-NaiveBayes"),
	ML_BAYES("ML-BAYES"),
	ML_TERM_IDF("ML-Term IDF"),
	PARSER("parser"),   //hard coded rule in the parser.
	USER("user");
	
	private String _label;
	
	private Source(String label) {
		_label = label;
	}
	
	public String toString() {
		return _label;
	}
	

}
