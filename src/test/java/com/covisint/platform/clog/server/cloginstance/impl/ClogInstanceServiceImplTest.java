/* Copyright (C) 2014 Covisint. All Rights Reserved. 
package com.covisint.platform.clog.server.cloginstance.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.mock;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.covisint.platform.clog.core.cloginstance.ClogInstance;
import com.covisint.platform.clog.server.cloginstance.ClogInstanceDAO;
import com.covisint.platform.group.client.group.GroupClient;
import com.covisint.platform.group.client.group.entitlement.GroupEntitlementClient;

*//**
 * Tests for {@link ClogInstanceServiceImpl}
 * @since Jul 13, 2015
 *
 *//*
public class ClogInstanceServiceImplTest {

	private ClogInstanceServiceImpl subject = new ClogInstanceServiceImpl(mock(ClogInstanceDAO.class), mock(GroupClient.class), mock(GroupEntitlementClient.class));
	
	@Rule
    public ExpectedException thrown= ExpectedException.none();
	
	@Test
	public void testDeleteClogInstance(){
		expectUnsupportedDeleteException();
		subject.delete(new ClogInstance());
	}
	
	@Test
	public void testDeleteId(){
		expectUnsupportedDeleteException();
		subject.delete("Sdfa");
	}
	
	
	private void expectUnsupportedDeleteException() {
		thrown.expect(UnsupportedOperationException.class);
		thrown.expectMessage(equalTo("Only Soft deletes Allowed. Use Update to change the status flag to deleted."));
	}

}
*/