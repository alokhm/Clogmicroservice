/* Copyright (C) 2014 Covisint. All Rights Reserved. */
package com.covisint.platform.clog.server.cloginstance.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.covisint.platform.clog.core.cloginstance.ClogInstance;
import com.covisint.platform.clog.server.cloginstance.impl.ClogInstanceJpaDao;

/**
 * Tests {@link ClogInstanceJpaDao}
 * @since Jul 13, 2015
 *
 */
public class ClogInstanceJpaDaoTest {
	
	@Rule
    public ExpectedException thrown= ExpectedException.none();
	
	public ClogInstanceJpaDao subject = new ClogInstanceJpaDao();

	@Test
	public void testGetResourceType() {
		assertThat(subject.getResourceType(), equalTo(ClogInstance.class));
	}
	
	@Test
	public void testDeleteClogInstance(){
		expectUnsupportedDeleteException();
		subject.delete(new ClogInstance());
	}

	@Test
	public void testDeleteID(){
		expectUnsupportedDeleteException();
		subject.delete("Dsfsa");
	}
	
	@Test
	public void testDeleteAll(){
		expectUnsupportedDeleteException();
		subject.deleteAll();
	}
	
	private void expectUnsupportedDeleteException() {
		thrown.expect(UnsupportedOperationException.class);
		thrown.expectMessage(equalTo("Only Soft deletes Allowed. Use Update to change the status flag to deleted."));
	}

}
