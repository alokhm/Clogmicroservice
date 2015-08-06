package com.covisint.platform.clog.server.wrapper;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.covisint.platform.clog.core.cloginstance.ClogInstance;

public class ElasticSearchServiceWrapperTest {

	@Mock 
	ElasticSearchServiceWrapper elasticSearchServiceWrapper;

	@Before
	public void setUp() throws Exception {
		elasticSearchServiceWrapper = Mockito.mock(ElasticSearchServiceWrapper.class);
		
		}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateAlias() {
		ClogInstance c = new ClogInstance();
		boolean flag = elasticSearchServiceWrapper.createAlias(c);
		Assert.assertEquals(false, flag);
		Assert.assertNotNull(c);
	}

}
