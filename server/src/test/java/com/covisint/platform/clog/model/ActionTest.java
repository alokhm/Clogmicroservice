/**
 * 
 */
package com.covisint.platform.clog.model;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
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
		Mockito.when(action.getAdd().getAdditionalProperties()).thenReturn(addAdditionalProp());
		
	
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
		assertEquals(action.getAdd().getAlias() , ALIAS);
		assertEquals(action.getAdd().getIndex() , INDEX);
		Assert.assertNotNull(action.getAdd().getFilter());
		Assert.assertNotNull(action.getAdd().getAdditionalProperties());
	}
}
