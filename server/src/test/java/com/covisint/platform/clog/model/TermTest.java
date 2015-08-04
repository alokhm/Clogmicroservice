package com.covisint.platform.clog.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class TermTest {
	@Mock
	Term term;

	@Before
	public void setUp() throws Exception {
		term=Mockito.mock(Term.class);
		Mockito.when(term.getGroupId()).thenReturn("createAdd()");
		Mockito.when(term.getX_realm()).thenReturn("createAdd()");
		
	}

	private Term createAdd() {
		// TODO Auto-generated method stub
		Term t1=new Term();
		t1.setGroupId("12345");
		t1.setX_realm("REALM_ID");
		return t1;
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testgetGroupid() {
		assertEquals(term.getGroupId(),"12345");
	
		
	}
	
	@Test
	public void testgetxrealm() {
		assertEquals(term.getX_realm(),"REALM_ID");
	
		
	}

}
