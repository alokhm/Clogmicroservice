package com.covisint.platform.clog.server.wrapper;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.covisint.platform.clog.core.cloginstance.ClogInstance;
import com.covisint.platform.group.client.group.GroupClient;
import com.covisint.platform.group.core.group.GroupEntitlement;

public class GroupServiceWrapperTest {

	@Mock 
	GroupServiceWrapper groupServiceWrapper;

	@Mock 
	GroupClient groupclient;
	
	@Mock 
	GroupEntitlement groupEntitlement;
	
	@Before
	public void setUp() throws Exception {
		groupServiceWrapper = Mockito.mock(GroupServiceWrapper.class);
		groupclient = Mockito.mock(GroupClient.class);
		groupEntitlement = Mockito.mock(GroupEntitlement.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGroupServiceWrapper() {
		ClogInstance c = new ClogInstance();
		boolean flag = groupServiceWrapper.add(c);
		Assert.assertEquals(false, flag);
		Assert.assertNotNull(c);
	}

	

}
