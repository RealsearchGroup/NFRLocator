package edu.ncsu.csc.nl.event;

/**
 * Object that listens to application events within the system
 * 
 * @author John Slankas
 */
public interface NLPEventListener {

	public void eventOccured(NLPEventType eventType, NLPEvent event);
}
