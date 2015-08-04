/**
 * 
 */
package com.covisint.platform.clog.model;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author alok.shukla
 *
 */
public class ActionTest {

	@Mock 
	Action action;
	
	
	private String ALIAS = "Alias1";
	private String INDEX = "index";
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		action = Mockito.mock(Action.class);
		Mockito.when(action.getAdd()).thenReturn(createAdd());
		Mockito.when(action.getAdditionalProperties()).thenReturn(addAdditionalProp());
		action.setAdditionalProperty("EMPTY", "TEST");
		
	
	}

	private Map<String, Object> addAdditionalProp() {
		return new HashMap<String, Object>();
	}

	private Add createAdd() {
		Add add = new Add();
		add.setIndex(INDEX);
		add.setAlias(ALIAS);
		add.setAdditionalProperty("KEY", "VALUE");
		Filter f1 = new Filter();
		Bool b = new Bool();
		f1.setBool(b);
		add.setFilter(f1);
		return add;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.covisint.platform.clog.model.Action#getAdd()}.
	 */
	@Test
	public void testGetAdd() {
		Assert.assertEquals(action.getAdd().getAlias(), ALIAS);
		action.setAdditionalProperty("EMPTY", "TEST");
		
	}
}
