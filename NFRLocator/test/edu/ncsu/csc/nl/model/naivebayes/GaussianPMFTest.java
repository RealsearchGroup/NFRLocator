package edu.ncsu.csc.nl.model.naivebayes;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


public class GaussianPMFTest {
	@Before
	public void setUp() throws Exception {

	}	
	
	
	@Test
	public void test() {

		GaussianPMF gPMF = new GaussianPMF(5.855, 0.187171);
		
		assertEquals(gPMF.getProbability("6.0"), 1.5789, 0.001);
		
	}


	
	
	
}
