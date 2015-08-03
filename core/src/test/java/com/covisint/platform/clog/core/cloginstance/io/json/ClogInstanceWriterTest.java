/* Copyright (C) 2014 Covisint. All Rights Reserved. */
package com.covisint.platform.clog.core.cloginstance.io.json;

import static com.covisint.platform.clog.core.SupportedMediaTypesE.CLOG_INSTANCE_V1_MEDIA_TYPE;
import static com.covisint.platform.clog.core.cloginstance.ClogInstanceTestUtil.mockClogInstance;
import static com.covisint.platform.clog.core.cloginstance.ClogInstanceTestUtil.mockMediaType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.json.JsonObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.covisint.platform.clog.core.cloginstance.ClogInstance;
import com.google.common.net.MediaType;

/**
 * @since Jul 9, 2015
 *
 */
@PrepareForTest(MediaType.class)
@RunWith(PowerMockRunner.class)
public final class ClogInstanceWriterTest {
	
	
	private final ClogInstanceWriter subject = new ClogInstanceWriter();

	/**
	 * Test method for {@link com.covisint.platform.clog.core.cloginstance.io.json.ClogInstanceWriter#isWritable(java.lang.Class, com.google.common.net.MediaType)}.
	 */
	@Test
	public void testIsWritable() {
		MediaType mt1 = mockMediaType(CLOG_INSTANCE_V1_MEDIA_TYPE.string());
		MediaType mt2 = mockMediaType("application/xml");
		assertTrue(subject.isWritable(null, mt1));
		assertFalse(subject.isWritable(null, mt2));
		
		
	}

	/**
	 * Test method for {@link com.covisint.platform.clog.core.cloginstance.io.json.ClogInstanceWriter#getResourceType()}.
	 */
	@Test
	public void testGetResourceType() {
		assertTrue(subject.getResourceType().equals(ClogInstance.class));
	}

	/**
	 * Test method for {@link com.covisint.platform.clog.core.cloginstance.io.json.ClogInstanceWriter#writeResource(com.google.common.net.MediaType, com.covisint.platform.clog.core.cloginstance.ClogInstance)}.
	 */
	@Test
	public void testWriteResourceMediaTypeClogInstance() {
		JsonObject json = subject.writeResource(mockMediaType(CLOG_INSTANCE_V1_MEDIA_TYPE.string()), mockClogInstance("testId",1234567890, "testName")).build();
		assertEquals("testName", json.getString("name"));
		assertEquals("testGroupId", json.getString("platformGroupId"));
		assertEquals("testSolutionId", json.getString("platformSolutionId"));
		assertEquals("testInstanceId", json.getString("platformInstanceId"));
		assertEquals("ACTIVE", json.getString("status"));
	}

}
