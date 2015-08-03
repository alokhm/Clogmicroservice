package com.covisint.platform.clog.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class ClogInstanceTest {

	@Mock ClogInstance cloginstance;
	@Before
	public void setUp() throws Exception {
		cloginstance = Mockito.mock(ClogInstance.class);
		Mockito.when(cloginstance.getActions()).thenReturn(createAction());
		Mockito.when(cloginstance.getAdditionalProperties()).thenReturn(addAdditionalProp());
	}

	

	private Map<String, Object> addAdditionalProp() {
		HashMap<String, Object> map=new HashMap<String,Object>();
		map.put("KEY", "VALUE");
		return map;
	}

	private List<Action> createAction() {
		List<Action> list = new ArrayList<Action>();
		
		Action a1 = new Action();
		Action a2 = new Action();
		
		list.add(a1);
		list.add(a2);
		return list;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetActions() {
		//cloginstance.setAdditionalProperty("KEY", "VALUE");
		assertEquals(cloginstance.getActions().size(), 2);
	}
	
	@Test
	public void testGetAdditionalproperties() {
		assertEquals(cloginstance.getAdditionalProperties().get("KEY"), "VALUE");
		
		
	}



}
