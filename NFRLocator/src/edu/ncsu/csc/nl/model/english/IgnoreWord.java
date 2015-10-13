package edu.ncsu.csc.nl.model.english;

import java.util.HashSet;

import edu.ncsu.csc.nl.model.type.PartOfSpeech;
import edu.ncsu.csc.nl.model.type.Relationship;

public class IgnoreWord {

	private static HashSet<IgnoreWord> _nodesToIgnore = new   HashSet<IgnoreWord>();	
	static {
		_nodesToIgnore.add(new IgnoreWord(Relationship.DET, PartOfSpeech.DT, "a"));
		_nodesToIgnore.add(new IgnoreWord(Relationship.DET, PartOfSpeech.DT, "an"));
		_nodesToIgnore.add(new IgnoreWord(Relationship.DET, PartOfSpeech.DT, "the"));
		//_nodesToIgnore.add(new IgnoreNode(Relationship.AUX, PartOfSpeech.MD, ""));

	}	
	
	Relationship rel;  // what is the relationship to the parent to ignore?
	PartOfSpeech pos;  // what is the part of spech that should be ignored?
	String       word; // what is the word that should be ignored?  If blank, matches any word
		
	public IgnoreWord (Relationship r,PartOfSpeech p,String w) {
		rel = r;
		pos = p;
		word = w;
	}
		
	public boolean ignoreWord(Relationship r,PartOfSpeech p,String w) {
		if (this.word.equals("")) {
			return (this.rel== r && this.pos == p);
		}
		else {
			return (this.rel== r && this.pos == p && this.word.equalsIgnoreCase(w));
		}
	}
	
	public static boolean shouldIgnore(Relationship r,PartOfSpeech p,String w) {
		for (IgnoreWord iw: IgnoreWord._nodesToIgnore) {
			if (iw.ignoreWord(r, p, w)) {
				return true;
			}
		}
		return false;
	}	
	
	
}
