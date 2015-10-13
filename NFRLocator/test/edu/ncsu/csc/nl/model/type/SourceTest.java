package edu.ncsu.csc.nl.model.type;

//import static org.junit.Assert.*;

import org.junit.Test;

public class SourceTest {

	@Test
	public void testToString() {
		assert(Source.USER.toString().equals("user"));
	}

}
