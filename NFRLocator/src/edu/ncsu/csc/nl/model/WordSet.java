package edu.ncsu.csc.nl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.mit.jwi.item.ISynsetID;

/**
 * 
 * @author John
 *
 * @param <E>
 */
public class WordSet<E> extends java.util.TreeSet<E>  implements Serializable {
	
	public static final long serialVersionUID = 1;  //TODO: Look up why this is needed and its purpose..  Shouldn't matter to me.
	
	/** This is a the primary word that should be used to identify this grouping of words*/
	private String _primaryWord;
	
	/* This is a link to the Princeton WordNet synset / set of synonyms */
	private ISynsetID _wordnetSynset = null;   
	

	public WordSet() {
	}
	
	public WordSet(String word) {
		_primaryWord = word;
	}

	public WordSet(String word, ISynsetID id) {
		_primaryWord = word;
		_wordnetSynset = id;
	}
	
	public String getPrimaryWord() {
		return _primaryWord;
	}
	public ISynsetID getWordnetSynsetID() {
		return _wordnetSynset;
	}
	
	
	public List<E> getAllMembers() {
	    return new ArrayList<E>(this);
	}
	
	public String getAllMembersAsString() {
		StringBuffer sb = new StringBuffer();
			
	    Iterator<E> iter = this.iterator();
	    while (iter.hasNext()) {
	    	sb.append(iter.next());
	    	if (iter.hasNext()) {sb.append(", ");}
	    }
	    		
		return sb.toString();
	}
	
	//TODO: add in the permissions here.   Will only be for verbs, but probably do need to consider negative items.
	//what I'm really looking at here is what permissions are associated with a given word....
}
