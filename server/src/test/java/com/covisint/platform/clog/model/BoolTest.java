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

public class BoolTest {
    @Mock
    Bool bool;
	
	@Before
	public void setUp() throws Exception {
		
		bool = Mockito.mock(Bool.class);
		Mockito.when(bool.getShould()).thenReturn(createShould());
		Mockito.when(bool.getAdditionalProperties()).thenReturn(addAdditionalProp());
	}

	private Map<String, Object> addAdditionalProp() {
		HashMap<String, Object> map=new HashMap<String,Object>();
		map.put("KEY", "VALUE");
		return map;
	}

	private List<Should> createShould() {
		// TODO Auto-generated method stub
		List<Should> should =new ArrayList<>();
		Term t1=new Term();
		//Term t2=new Term();
		Should should1 =new Should();
		t1.setGroupId("12345");
		t1.setX_realm("REALM_ID");
		should1.setTerm(t1);
		
		
		should.add(should1);
		
		return should;
	}

	@After
	public void tearDown() throws Exception {
	}

	
	
	/**
	 * Test method for {@link com.covisint.platform.clog.model.Action#getShould()}.
	 */
	@Test
	public void testGetShould() {
		assertEquals(bool.getShould().get(0).getTerm().getGroupId(),"12345");
		assertEquals(bool.getShould().get(0).getTerm().getX_realm(),"REALM_ID");
		
		
	}
	
	@Test
	public void testGetAdditionalproperties() {
		assertEquals(bool.getAdditionalProperties().get("KEY"), "VALUE");
		
		
	}

}
