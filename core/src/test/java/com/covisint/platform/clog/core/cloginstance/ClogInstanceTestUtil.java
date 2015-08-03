/* Copyright (C) 2014 Covisint. All Rights Reserved. */
package com.covisint.platform.clog.core.cloginstance;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.covisint.platform.clog.core.ClogInstanceStatusE;
import com.covisint.platform.clog.core.cloginstance.ClogInstance;
import com.google.common.net.MediaType;

/**
 * Utilities to test JSON IO
 * 
 * @since Jul 9, 2015
 *
 */
public final class ClogInstanceTestUtil {

	public static final MediaType mockMediaType(String mediaType) {
		MediaType mt1 = mock(MediaType.class);
		when(mt1.withoutParameters()).thenReturn(mt1);
		when(mt1.toString()).thenReturn(mediaType);
		return mt1;
	}

	public static final ClogInstance mockClogInstance(String id, long version, String name) {
		//@formatter:off
		return new ClogInstance().
				setId(id).
				setCreationInstant(1234567890).
				setCreator("testCreator").
				setCreatorApplicationId("testAppId").
				setVersion(version).
				setName(name).
				setPlatformGroupId("testGroupId").
				setPlatformInstanceId("testInstanceId").
				setPlatformSolutionId("testSolutionId").
				setRealm("SampleRealm").
				setStatus(ClogInstanceStatusE.ACTIVE);
		//@formatter:on

	}

}
