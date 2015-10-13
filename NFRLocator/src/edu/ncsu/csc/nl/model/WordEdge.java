package edu.ncsu.csc.nl.model;

import java.io.Serializable;

import edu.ncsu.csc.nl.model.type.Relationship;

/**
 * Represents the "edge" / relationship between two nodes
 * 
 * Although it is technically a graph, I am still using the terms parent & child because the relationship is one way
 * 
 * @author John
 *
 */
public class WordEdge implements Serializable {
	
	public static final long serialVersionUID = 1;
	
	private Relationship _relationship;
	private WordVertex _parentNode;
	private WordVertex _childNode;
	
	/** flag if the relationship should be wildcard during using as a relational pattern  */
	private boolean _wildcardRelationship = false;
	
	public WordEdge( Relationship relation,  WordVertex parent, WordVertex child) {
		 _relationship  = relation;
		 _parentNode    = parent;
		 _childNode     = child;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("parent(");
		sb.append(_parentNode);
		sb.append(") child(");
		sb.append(_childNode);
		sb.append(") relationship:");
		sb.append(_relationship.getLabel());
		
		return sb.toString();
	}
	
	public Relationship getRelationship() {
		return _relationship;
	}
	
	public WordVertex getParentNode() {
		return _parentNode;
	}
	
	public WordVertex getChildNode() {
		return _childNode;
	}

	public void changeRelationship(Relationship newValue) {
		_relationship = newValue;
	}
	
	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o instanceof WordEdge) {
			WordEdge we = (WordEdge) o;
			
			return (this._relationship == we._relationship && this._parentNode.getID() == we._parentNode.getID() && this._childNode.getID() == we._childNode.getID());
		}
		return false;
	}
	
	public boolean isWildcardRelationship() {
		return _wildcardRelationship;
	}
	
	public void setWildcardRelationship(boolean newValue) {
		_wildcardRelationship = newValue;
	}
}
