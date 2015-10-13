package edu.ncsu.csc.nl.model.naivebayes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;


public class NaiveBayesClassifier implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private FixedPMF _classes = new FixedPMF();
	
	// indexed by classname, and then likelyhood
	private java.util.HashMap<String, java.util.HashMap<String,PMF>> _likelyhoods = new java.util.HashMap<String, java.util.HashMap<String,PMF>>();
	
	private java.util.ArrayList<String> _classNames = new java.util.ArrayList<String>();
	private java.util.ArrayList<String> _likelyhoodNames = new java.util.ArrayList<String>();
	private java.util.ArrayList<String> _likelyhoodTypes = new java.util.ArrayList<String>(); 
	
	public NaiveBayesClassifier() {
		
	}
	
	public void addClass(String className) {
		_classNames.add(className);
	}
	
	public String[] getAllClassNames() {
		return _classNames.toArray(new String[0]);
	}
	
	/**
	 * Adds the given likely hood with the specific type to the list.  
	 * Behind the scenes, the appropriate PMF will be created
	 * 
	 * @param name
	 * @param type.   Must be "gaussian", "words", "count".
	 * @param args  For gaussian, this needs to be the values for the mean and standard deviation.  this will need to be 2 * # of classes
	 *              For words, this is needs to be the smoothing factor, generally "1"  (optional, defaults to 1)
	 */
	public void addLikelyhood(String name, String type, String... args) {
		if (type.equals("gaussian")) {
			if (args.length != 2 * _classNames.size()) {
				throw new IllegalArgumentException("With gaussian type, must pass the mean and std deviation as well");
			}
			
			//HashMap<String, PMF> temp = new HashMap<String,PMF>();
			int index = 0;
			for (String cName: _classNames) {
				HashMap<String, PMF> temp;
				
				if (_likelyhoods.containsKey(cName)) {
					temp = _likelyhoods.get(cName);
				}
				else {
					temp = new HashMap<String, PMF>();
					_likelyhoods.put(cName, temp);
				}
				temp.put(name,new GaussianPMF(Double.parseDouble(args[index++]),Double.parseDouble(args[index++])) ); 
			}
		}
		else if (type.equals("words") || type.equals("count")) {
			for (String cName: _classNames) {
				HashMap<String, PMF> temp;
				
				if (_likelyhoods.containsKey(cName)) {
					temp = _likelyhoods.get(cName);
				}
				else {
					temp = new HashMap<String, PMF>();
					_likelyhoods.put(cName, temp);
				}
				if (type.equals("words")) {
					double smoothingFactor = 1.0;
					if (args.length == 1) {
						smoothingFactor = Double.parseDouble(args[0]);
					}
					
					
					temp.put(name,new FixedPMF(smoothingFactor)); 
				}
				else {
					temp.put(name,new FixedPMF()); 
				}
			}			
		}
		else {
			throw new IllegalArgumentException(type +" is not a valid value for type(gaussian,words,count)");
		}
		_likelyhoodNames.add(name);
		_likelyhoodTypes.add(type);
	}
	
	//TODO: add error handling/check that className is valid
	public void incrementClass(String className) {
		_classes.increment(className);
	}
	
	/** sets the Prior value (the probability that a given class/hypothesis appears) */
	public void setClassPrior(String className, double prior) {
		_classes.setValue(className, prior);
	}
	
	public void incrementClassLikelyhood(String className, String likelyhoodName, String item) {
		((FixedPMF)_likelyhoods.get(className).get(likelyhoodName)).increment(item);
	}

	public void setValueForClassLikelyhood(String className, String likelyhoodName, String item, double value) {
		((FixedPMF)_likelyhoods.get(className).get(likelyhoodName)).setValue(item, value);
	}
	
	public void multipleForClassLikelyhood(String className, String likelyhoodName, String item, double value) {
		((FixedPMF)_likelyhoods.get(className).get(likelyhoodName)).multiple(item, value);
	}	
	
	public Map<String, Double> computeClassProbabilities(Object... args) {
		HashMap<String, Double> result = new HashMap<String, Double>();
		
		
		// Need to setup the smoothing parameters for likelyhoods that are words
		for (int i=0; i < _likelyhoodNames.size(); i++) {
			if (!_likelyhoodTypes.get(i).equals("words")) { continue; }

			HashSet<String> words = new HashSet<String>();
			for (String className: _classNames) {
				words.addAll(((FixedPMF)_likelyhoods.get(className).get(_likelyhoodNames.get(i))).getAllItems());
			}
			double totalCount = words.size();
			for (String className: _classNames) {
				((FixedPMF)_likelyhoods.get(className).get(_likelyhoodNames.get(i))).setSmoothingElementCount(totalCount);
			}
		}
				
		for (String className: _classNames) {
			double tempResult = _classes.getProbability(className);

			//System.out.println(className +": "+tempResult);
			
			
			for (int i=0; i < _likelyhoodNames.size(); i++) {
				if (_likelyhoodTypes.get(i).equals("words")) {
					PMF pmf = _likelyhoods.get(className).get(_likelyhoodNames.get(i));
					
					String[] words = (String[]) args[i];
					for (String w: words) {
						tempResult *= pmf.getProbability(w);
						//System.out.println(tempResult);
					}
				
				}
				else if (_likelyhoodTypes.get(i).equals("count")) {
					String value = args[i].toString();
					tempResult  *= _likelyhoods.get(className).get(_likelyhoodNames.get(i)).getProbability(value);
				}
				else if (_likelyhoodTypes.get(i).equals("gaussian")) {
					String value = args[i].toString();
					tempResult  *= _likelyhoods.get(className).get(_likelyhoodNames.get(i)).getProbability(value);
				}
				
			}
			
			result.put(className, tempResult);
		}
		return sortByComparator(result);
	}
	
	public Map<String, Double> computeClassProbabilitiesByLogs(Object... args) {
		HashMap<String, Double> result = new HashMap<String, Double>();
		
		
		// Need to setup the smoothing parameters for likelyhoods that are words
		for (int i=0; i < _likelyhoodNames.size(); i++) {
			if (!_likelyhoodTypes.get(i).equals("words")) { continue; }

			HashSet<String> words = new HashSet<String>();
			for (String className: _classNames) {
				words.addAll(((FixedPMF)_likelyhoods.get(className).get(_likelyhoodNames.get(i))).getAllItems());
			}
			double totalCount = words.size();
			for (String className: _classNames) {
				((FixedPMF)_likelyhoods.get(className).get(_likelyhoodNames.get(i))).setSmoothingElementCount(totalCount);
			}
		}
				
		for (String className: _classNames) {
			double tempResult = Math.log(_classes.getProbability(className));

			//System.out.println(className +": "+tempResult);
			
			
			for (int i=0; i < _likelyhoodNames.size(); i++) {
				if (_likelyhoodTypes.get(i).equals("words")) {
					PMF pmf = _likelyhoods.get(className).get(_likelyhoodNames.get(i));
					
					String[] words = (String[]) args[i];
					for (String w: words) {
						tempResult += Math.log(pmf.getProbability(w));
						//System.out.println(tempResult);
					}
				
				}
				else if (_likelyhoodTypes.get(i).equals("count")) {
					String value = args[i].toString();
					tempResult  += Math.log(_likelyhoods.get(className).get(_likelyhoodNames.get(i)).getProbability(value));
				}
				else if (_likelyhoodTypes.get(i).equals("gaussian")) {
					String value = args[i].toString();
					tempResult  += Math.log(_likelyhoods.get(className).get(_likelyhoodNames.get(i)).getProbability(value));
				}
				
			}
			
			result.put(className, tempResult);
		}
		return sortByComparator(result);
	}	
	
	private static Map<String, Double> sortByComparator(Map<String, Double> unsortMap) {
		 
		LinkedList<Entry<String,Double>> list = new LinkedList<Entry<String,Double>>(unsortMap.entrySet());
 
		// sort list based on comparator
		Collections.sort(list, new Comparator<Entry<String,Double>>() {
			public int compare(Entry<String,Double> d1, Entry<String,Double> d2) {
				return  - Double.compare(d1.getValue(), d2.getValue());
				
			}
		});
 
		// put sorted list into map again
                //LinkedHashMap make sure order in which keys were inserted
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for(Entry<String, Double> entry: list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}	
	
}
