package edu.ncsu.csc.nl.model.classification;

import java.util.HashMap;

/**
 * Represents an item that can be classified
 * 
 * @author John Slankas
 */
public interface ClassifiableItem {

	public HashMap<String, ClassificationType> getClassifications();
	public void setClassifications(HashMap<String, ClassificationType> classifications);
}
