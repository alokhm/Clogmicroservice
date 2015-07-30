package com.covisint.platform.clog.core.cloginstance.io.json;

import static com.covisint.platform.clog.core.SupportedMediaTypesE.CLOG_INSTANCE_V1_MEDIA_TYPE;
import static com.covisint.platform.clog.core.cloginstance.ClogInstanceTestUtil.mockMediaType;
import static org.junit.Assert.assertEquals;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.covisint.core.http.service.core.io.jsonp.JsonpSupport;
import com.covisint.platform.clog.core.ClogInstanceStatusE;
import com.covisint.platform.clog.core.cloginstance.ClogInstance;
import com.google.common.net.MediaType;

/**
 * Unit Tests for {@link ClogInstanceReader}.
 * 
 * @since Jul 9, 2015
 *
 */
@PrepareForTest(MediaType.class)
@RunWith(PowerMockRunner.class)
public class ClogInstanceReaderTest {

	ClogInstanceReader subject = new ClogInstanceReader();;

	@Test
	public void testGetResourceType() {
		Assert.assertEquals(subject.getResourceType(), ClogInstance.class);
	}

	@Test
	public void testIsReadable() {
		MediaType mt1 = mockMediaType(CLOG_INSTANCE_V1_MEDIA_TYPE.string());
		Assert.assertTrue(subject.isReadable(mt1));
	}

	@Test
	public void testReadResource() {

		MediaType mt1 = mockMediaType(CLOG_INSTANCE_V1_MEDIA_TYPE.string());
		ClogInstance clogInstance = subject.readResource(mt1, TestData.clogInstanceJsonObject());
		assertEquals("somename", clogInstance.getName());
		assertEquals("well..", clogInstance.getPlatformSolutionId());
		assertEquals("someinstanceid",clogInstance.getPlatformInstanceId());
		assertEquals("funkyGroup", clogInstance.getPlatformGroupId());
		assertEquals(ClogInstanceStatusE.ACTIVE, clogInstance.getStatus());
	}

	private static final class TestData {
		final static JsonObject clogInstanceJsonObject() {
			// @formatter:off
			return Json.createObjectBuilder()
					.add(JsonpSupport.ID_PROP, "someid")
					.add(JsonpSupport.VERSION_PROP, 13456)
					.add(JsonpSupport.CREATOR_PROP, "lksdfj")
					.add(JsonpSupport.CREATION_PROP, 5678978)
					.add(JsonpSupport.CREATOR_APP_PROP, "sdfaf")
					.add("name", "somename")
					.add("platformSolutionId", "well..")
					.add("platformInstanceId", "someinstanceid")
					.add("platformGroupId", "funkyGroup")
					.add("status",ClogInstanceStatusE.ACTIVE.toString())
					.build();
			// @formatter:on

		}
	}

}
