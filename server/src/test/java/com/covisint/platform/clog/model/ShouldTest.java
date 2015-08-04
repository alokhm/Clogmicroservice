package com.covisint.platform.clog.model;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class ShouldTest {
	
	@Mock
	Should should;

	@Before
	public void setUp() throws Exception {
		
		should=Mockito.mock(Should.class);
	    Mockito.when(should.getTerm()).thenReturn(addTerm());
	    Mockito.when(should.getAdditionalProperties()).thenReturn(addAdditionalProp());
	}

	private Map<String, Object> addAdditionalProp() {
		HashMap<String, Object> map=new HashMap<String,Object>();
		map.put("KEY", "VALUE");
		return map;
	}
	
	private Term addTerm() {
		// TODO Auto-generated method stub
		Term term=new Term();
		term.setGroupId("12345");
		term.setX_realm("REALM_ID");
		term.setAdditionalProperty("TEST", "TEST");
		
		return term;
	}

	

	@Test
	public void testgetTerm() {
		assertEquals(should.getTerm().getGroupId(),"12345");
		assertEquals(should.getTerm().getX_realm(),"REALM_ID");
	}

	@Test
	public void testgetAdditionalproperties() {
		assertEquals(should.getAdditionalProperties().get("KEY"), "VALUE");
	}
	
}
