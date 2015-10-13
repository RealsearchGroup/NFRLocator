package edu.ncsu.csc.nl.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;



/**
 * "Dumping ground" for miscellaneous functionality
 * 
 * 
 * 
 * @author John
 *
 */
public class Utility {

	/**
	 * Code taken from http://www.java2s.com/Tutorial/Java/0180__File/ReadLinesreadfiletolistofstrings.htm
	 * 
	 * Need to be careful when using this that a sentence doesn't span lines
	 * 
	 * @param file
	 * @return List of lines from the file
	 * @throws Exception
	 */
	public static List<String> readLines(String fileName)  throws Exception {
		List<String> results = new ArrayList<String>();

		

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));
		
	    String line = reader.readLine();
	    while (line != null) {
	         results.add(line);
	         line = reader.readLine();
	    }
	    reader.close();
	    
		return results;
	}	
	
	public static void printFrequencyTable(HashMap<String, Integer> table, java.io.PrintWriter pw, String category) {
		//Put keys and values in to an arraylist using entryset
		ArrayList<Entry<String,Integer>> al = new ArrayList<Entry<String,Integer>>(table.entrySet());

		Collections.sort(al, new Comparator<Entry<String, Integer>>(){
	         public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
	        	 if (o1.getValue().equals(o2.getValue())) {
	        		 return o1.getKey().compareTo(o2.getKey());
	        	 }
	        	 else {
	        		 return o2.getValue().compareTo(o1.getValue());
	        	 }
	        }});
		
		for (Entry<String,Integer> e: al) {
			if (category != null) {
				pw.print(category);
				pw.print("\t");

			}
			pw.print(e.getKey());
			pw.print("\t");
			pw.println(e.getValue());
		}
		pw.flush();
	}
	
	/**
	 * http://stackoverflow.com/questions/1128723/in-java-how-can-i-test-if-an-array-contains-a-certain-value
	 * 
	 * @param array
	 * @param v
	 * @return
	 */
	public static <T> boolean contains( final T[] array, final T v ) {
	    for ( final T e : array )
	        if ( e == v || v != null && v.equals( e ) )
	            return true;

	    return false;
	}	
	
	
	/**
	 * Sum
	 * 
	 * @param hashmap
	 * @return
	 */
	public static <T> int sum( HashMap<T, Integer> table ) {
		int result = 0 ;
		
	    for ( Integer value : table.values() ) {
	    	result += value;
	    }
	    return result;
	}
		
	public static HashSet<Character> createCharacterSetFromString (String s) {
		int length= s.length();
		HashSet<Character> result = new HashSet<Character>();
		
		for (int i=0;i<length;i++) {
			result.add(s.charAt(i));
		}
		return result;
	}
	
	/**
	 * Deep-copies an object by serializing to a byte-stream and then back....
	 * 
	 * @param orig
	 * @return
	 */
	public static Object copy(Object orig) {
        Object obj = null;
        try {
            // Write the object out to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(orig);
            out.flush();
            out.close();

            // Make an input stream from the byte array and read
            // a copy of the object back in.
            ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream(bos.toByteArray()));
            obj = in.readObject();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return obj;
    }
	
	/**
	 * Returns a timestamp in the format of YYYYMMDDHHmmss based upon the current time
	 * 
	 * @return
	 */
	public static String getCurrentTimeStamp() {
		java.util.Date date= new java.util.Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(date);
	}

	/**
	 * Show often does a particular character occur in a string?
	 * 
	 * from http://stackoverflow.com/questions/275944/how-do-i-count-the-number-of-occurrences-of-a-char-in-a-string
	 * 
	 * @param s String to search within
	 * @param c Character to search for
	 * @return
	 */
	public static int countOccurrences(String s, char c)
	{
	    int count = 0;
	    for (int i=0; i < s.length(); i++)
	    {
	        if (s.charAt(i) == c)
	        {
	             count++;
	        }
	    }
	    return count;
	}	
	
	
	/**
	 * Returns which word occurs most often.  If two or more words occur most often, then null is returned;
	 * 
	 * @param wordFrequencies
	 * @return
	 */
	public static String getMostFrequentlyOccuringWord(HashMap<String, Integer> wordFrequencies) {
		int max = -1;
		String word = null;
		
		for (String key: wordFrequencies.keySet()) {
			int value = wordFrequencies.get(key);
			if (value == max) {
				word = null;
			}
			else if (value > max) {
				max = value;
				word = key;
			}
		}
		
		return word;
	}
	
}
