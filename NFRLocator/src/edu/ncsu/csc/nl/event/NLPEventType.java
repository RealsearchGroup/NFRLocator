package edu.ncsu.csc.nl.event;

/**
 * Represents a particular type of event that occurred within the
 * system
 * 
 * @author John
 *
 */
public enum NLPEventType {

	CLASSIFIED_SENTENCE("Classified Sentence"),       //used when the classifier classifies a sentence
	MOVED_TO_SENTENCE("Moved to sentence"),      //the sentence is now the current one specified
	MARK_CLASSIFIED_AND_MOVE("Marked classified, move to next statement"),
	NEW_SENTENCE_SELECTED("New sentence to move to"),  // need to move to this current sentence
	SENTENCE_DATA_CHANGE("Sentence data change"),      // some attribute of a sentence changed (ie, classififcation or a training flag) 
	SENTENCE_ACCESS_CONTROL_CHANGE("Sentence, access control mark was changed"),   //The user marked the sentence as having access control elements defined
	SENTENCE_DATABASE_CHANGE("Sentence, database mark was changed"), // the user marked the sentence as having database elements
	VIEW_CHANGED("View has been change between annotations and classifications"),
	UNKNOWN("Unknown - NOT TO BE USED");
	
	private String _label;
	
	private NLPEventType(String label){
		_label = label;
	}
	
	public String toString() {
		return _label;
	}
	
}
