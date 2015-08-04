package com.covisint.platform.clog.model;

import static org.junit.Assert.*;


import org.junit.After;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class AddTest {
	@Mock 
	Add add;

	@Before
	public void setUp() throws Exception {
		
		
		add=Mockito.mock(Add.class);
		Mockito.when(add.getAlias()).thenReturn("createAdd()");
		Mockito.when(add.getIndex()).thenReturn("createAdd()");
		
	}
	private Add createAdd() {
		Add add = new Add();
		add.setAlias("alias");
		add.setIndex("index");
		Filter f1 = new Filter();
		add.setFilter(f1);
		return add;
		
	}
	
	/**
	 * Test method for {@link com.covisint.platform.clog.model.Action#getAlias()}.
	 */
	@Test
	public void testGetAlias() {
		assertEquals(add.getAlias() , "alias");
		assertEquals(add.getIndex() , "index");
	}
	
	@After
	public void tearDown() throws Exception {
	}


}
