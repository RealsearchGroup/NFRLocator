package edu.ncsu.csc.nl.event;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 
 * 
 * @author Adminuser
 */
public class NLPEventManager {
	private static NLPEventManager _theEventManager = new NLPEventManager();
	public static NLPEventManager getTheEventManager() {return _theEventManager; }
	
	private HashMap<NLPEventType, ArrayList<NLPEventListener>> _listeners = new HashMap<NLPEventType, ArrayList<NLPEventListener>>();
	
	private NLPEventManager() {	}
	
	public void registerForEvent(NLPEventType eventType, NLPEventListener el) {
		if (!_listeners.containsKey(eventType)) {
			_listeners.put(eventType, new ArrayList<NLPEventListener>());
		}
		ArrayList<NLPEventListener> listeners = _listeners.get(eventType);
		listeners.add(el);
	}
	public void deRegisterForEvent(NLPEventType eventType, NLPEventListener el) {
		if (_listeners.containsKey(eventType)) {
			ArrayList<NLPEventListener> listeners = _listeners.get(eventType);
			listeners.remove(el);
		}
	}

	public void sendEvent(NLPEventType eventType, NLPEvent event) {
		if (_listeners.containsKey(eventType)) {
			ArrayList<NLPEventListener> listeners = _listeners.get(eventType);
			for (NLPEventListener listener: listeners) {
				listener.eventOccured(eventType, event);
			}
		}
		
	}
	
}
