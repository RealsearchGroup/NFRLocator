package edu.ncsu.csc.nl.model.naivebayes;

import java.io.Serializable;
import java.util.Set;

/**
 * 
 * 
 * Note: This class is not thread-safe.  Uses HashMap.  Also, the computation of normalizingTotal is not synchronized
 * 
 * @author John Slankas
 */
public class FixedPMF extends PMF  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private java.util.HashMap<String, Double> _map = new java.util.HashMap<String, Double>();
	private java.util.HashMap<String, Double> _normalizedMap = new java.util.HashMap<String, Double>();
	
	private boolean _normalized = false;
	private double  _normalizingTotal = Double.MIN_VALUE;
	private double  _smoothedProbability = Double.MIN_VALUE; // this is the value returned if an element does not exist
	
	private double _smoothingValue = 0.0;
	
	private double _smoothingElementCount = 0.0;
	
	public FixedPMF() {
		
	}
	
	public FixedPMF(double smoothingValue) {
		_smoothingValue = smoothingValue;
	}

	@Override
	public double getProbability(String item) {
		if (!_normalized) {
			this.getNormalizingTotal();
			_normalized = true;
		}
		
		if (_normalizedMap.containsKey(item)) {
			return _normalizedMap.get(item);
		}
		else {
			if (_map.containsKey(item)) {
				//compute, place in map, return
				double p = ( _map.get(item) + _smoothingValue) / ( _normalizingTotal + (_smoothingElementCount* _smoothingValue) );
				_normalizedMap.put(item, p);
				return p;
			}
			else {
				return _smoothedProbability;
			}
		}
		
	}
	
	private void setNormalizedFalse() {
		if (_normalized) {
			_normalized = false;
			_normalizingTotal = Double.MIN_VALUE;
			_smoothedProbability = Double.MIN_VALUE;
			_normalizedMap = new java.util.HashMap<String, Double>();
		}
	}
	
	private double getNormalizingTotal() {
		if (_normalizingTotal == Double.MIN_VALUE) {
			double total = 0.0;
			for (String item: _map.keySet()) {
				total += _map.get(item);
			}	
			_normalizingTotal = total;
			
			_smoothedProbability = _smoothingValue / ( _normalizingTotal + ( _smoothingElementCount * _smoothingValue ) );
		}
		
		return _normalizingTotal;
	}
	
	public void setValue(String item, double probability) {
		this.setNormalizedFalse();
		_map.put(item,probability);
	}
	
	/**
	 * Increments the given item by 1.  If the data is normalized, it is converted back to counts before this action occurs
	 * 
	 * @param item
	 */
	public void increment(String item) {
		this.setNormalizedFalse();
		
		double value =_map.containsKey(item) ? _map.get(item) : 0;
		_map.put(item, value + 1);
	}
	
	public void multiple(String item, double factor) {
		this.setNormalizedFalse();
		
		double value = _map.get(item) * factor;
		_map.put(item, value);
	}
	
	public void setSmoothingElementCount(double newValue) {
		_smoothingElementCount = newValue;
	}
	
	public double getSize() {
		return _map.size();
	}
	
	public Set<String> getAllItems() {
		return _map.keySet();
	}
}
