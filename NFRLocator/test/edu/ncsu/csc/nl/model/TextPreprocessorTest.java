package edu.ncsu.csc.nl.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TextPreprocessorTest {

	@Test
	public void testProcessLine() {
	
		
		TextProcessor tp = new TextProcessor();
		
		String noChange = "Hello, I'm John.";
		String noChangedProcessed = tp.preProcessLine(noChange);
		
		assertEquals(noChange,noChangedProcessed);
	
		
		String slash = tp.preProcessLine("test/replace");
		assertEquals(slash,"test or replace");

		String multiSlash = tp.preProcessLine("test/replace/modify/go");
		assertEquals(multiSlash,"test or replace or modify or go");
		
		String bracketEmpty = tp.preProcessLine("[This is text to remove]");
		assertEquals(bracketEmpty,"");
		
		String multiBracketEmpty = tp.preProcessLine("[This is text to remove][more][keep]");
		assertEquals(multiBracketEmpty,"");

		String multiBracket = tp.preProcessLine("[This is text to remove]keep[more]");
		assertEquals(multiBracket,"keep");

		String andOR = tp.preProcessLine("stuff and/or more stuff");
		//System.out.println(andOR);
		assertEquals(andOR,"stuff possibly and possibly more stuff");

		String emptryString = tp.preProcessLine("");
		assertEquals(emptryString,"");
		
		String without = tp.preProcessLine("w/out");
		assertEquals(without,"without");
		
		String within = tp.preProcessLine("w/in");
		assertEquals(within,"within");
		
		String with = tp.preProcessLine("w/ ");
		assertEquals(with,"with ");

		String na = tp.preProcessLine("n/a");
		assertEquals(na,"not applicable");
		na = tp.preProcessLine("N/A");
		assertEquals(na,"not applicable");

		String fileName =  tp.preProcessLine("starwars.xls");
		assertEquals(fileName,"starwarsZDOTZxls");
		fileName=tp.postProcessLine(fileName);
		assertEquals(fileName,"starwars.xls");
		

		String lineNum = tp.preProcessLine("1. The user edits the file.");
		assertEquals(lineNum,"1) The user edits the file.");
		lineNum = tp.preProcessLine("105. The user edits the file.");
		assertEquals(lineNum,"105) The user edits the file.");
		
		lineNum = tp.preProcessLine("105.42 The user edits the file.");  // No change here
		assertEquals(lineNum,"105.42 The user edits the file.");
		lineNum = tp.preProcessLine("105.a The user edits the file.");  // No change here
		assertEquals(lineNum,"105.a The user edits the file.");
		
		//TODO add tests for the stack trace here
		
		
		String combinedTest = tp.preProcessLine("[S1]The user can edit/view/delete the data and/or report on it w/out any regard to w/in his choice w/ her.");
		assertEquals(combinedTest,"The user can edit or view or delete the data possibly and possibly report on it without any regard to within his choice with her.");		
	}

}
