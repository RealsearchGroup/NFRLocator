package edu.ncsu.csc.nl.model.type;

import org.junit.Test;
import org.junit.Assert;

public class BooleanTypeTest {

	@Test
	public void test() {
		
		assert(BooleanType.TRUE.toString().equals("true"));
		assert(BooleanType.FALSE.toString().equals("false"));
		assert(BooleanType.UNKNOWN.equals("unknown"));

	}
	
	@Test
	public void testBooleanValue() {
		Assert.assertTrue(BooleanType.TRUE.getBooleanValue());
		Assert.assertFalse(BooleanType.FALSE.getBooleanValue());
	}

	@Test(expected=java.lang.RuntimeException.class)
	public void testUnknownBooleanValue() {
		Assert.assertTrue(BooleanType.UNKNOWN.getBooleanValue());		
	}
	
}
