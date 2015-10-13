package edu.ncsu.csc.nl.weka;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


import weka.core.Instances;
import weka.core.converters.ArffSaver;


/**
 * "Dumping ground" for miscellaneous functionality
 * 
 * 
 * 
 * @author John
 *
 */
public class WekaUtility {

	public static void exportInstancesAsARFF(Instances instances, File  file)  throws IOException, FileNotFoundException {
	    ArffSaver saver = new ArffSaver();
	    saver.setInstances(instances);
	    saver.setFile(file);
	    saver.writeBatch();		
	}
	public static void exportInstancesAsARFF(Instances instances, String filename)  throws IOException, FileNotFoundException {	
		exportInstancesAsARFF(instances,new File(filename));
	}
}
