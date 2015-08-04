package com.covisint.platform.clog.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Assert;
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
		Mockito.when(add.getAlias()).thenReturn("alias");
		Mockito.when(add.getIndex()).thenReturn("index");
		Mockito.when(add.getFilter()).thenReturn(createFilter());
		
		
	}
	
	private Filter createFilter() {
		Filter filter = new Filter();
		filter.setAdditionalProperty("TEST", "TEST");
		return filter;
	}

	/**
	 * Test method for {@link com.covisint.platform.clog.model.Action#getAlias()}.
	 */
	@Test
	public void testGetAlias() {
		assertEquals(add.getAlias() , "alias");
		assertEquals(add.getIndex() , "index");
		Assert.assertNotNull(add.getFilter());
	}
	
	@After
	public void tearDown() throws Exception {
	}


}
