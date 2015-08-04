package com.covisint.platform.clog.model;



import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class FilterTest {
    @Mock
    Filter filter;
	@Before
	public void setUp() throws Exception {
		
		filter=Mockito.mock(Filter.class);
		Mockito.when(filter.getBool()).thenReturn(createBool());
		Mockito.when(filter.getAdditionalProperties()).thenReturn(addAdditionalProp());
	}

	private Map<String, Object> addAdditionalProp() {
		HashMap<String, Object> map=new HashMap<String,Object>();
		map.put("KEY", "VALUE");
		return map;
	}

	private Bool createBool() {
		// TODO Auto-generated method stub
		Bool bool=new Bool();
		List<Should> should =new ArrayList<>();
		Term t1=new Term();
		
		Should should1 =new Should();
		should1.setAdditionalProperty("TEST", "TEST");
		t1.setGroupId("12345");
		t1.setX_realm("REALM_ID");
		should1.setTerm(t1);
		
		
		should.add(should1);
		
		bool.setShould(should);
		bool.setAdditionalProperty("TEST", "TEST");
		return bool;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testgetBool() {
		
		assertEquals(filter.getBool().getShould().get(0).getTerm().getGroupId(),"12345");
		assertEquals(filter.getBool().getShould().get(0).getTerm().getX_realm(),"REALM_ID");
		
	
	}
	
	@Test
	public void testgetAdditionalProperties() {
		
		assertEquals(filter.getAdditionalProperties().get("KEY"), "VALUE");
		
	
	}

}
