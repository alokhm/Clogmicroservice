/* Copyright (C) 2014 Covisint. All Rights Reserved. */
package com.covisint.platform.clog.core.cloginstance.io.json;

import static com.covisint.platform.clog.core.SupportedMediaTypesE.CLOG_INSTANCE_V1_MEDIA_TYPE;
import static com.covisint.platform.clog.core.cloginstance.io.json.ClogInstanceJsonConstants.JSON_CLOG_INSTANCE_NAME;
import static com.covisint.platform.clog.core.cloginstance.io.json.ClogInstanceJsonConstants.JSON_PLATFORM_GROUP_ID;
import static com.covisint.platform.clog.core.cloginstance.io.json.ClogInstanceJsonConstants.JSON_PLATFORM_INSTANCE_ID;
import static com.covisint.platform.clog.core.cloginstance.io.json.ClogInstanceJsonConstants.JSON_PLATFORM_SOLUTION_ID;
import static com.covisint.platform.clog.core.cloginstance.io.json.ClogInstanceJsonConstants.JSON_STATUS;

import javax.json.JsonObject;
import javax.json.JsonString;

import com.covisint.core.http.service.core.io.jsonp.AbstractRealmScopedResourceReader;
import com.covisint.core.http.service.core.io.jsonp.JsonpSupport;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.platform.clog.core.ClogInstanceStatusE;
import com.covisint.platform.clog.core.cloginstance.ClogInstance;
import com.google.common.net.MediaType;

/**
 * Responsible for reading and populating {@link ClogInstance} from a JSON
 * String.
 * 
 * @since 7/8/2015
 */
public final class ClogInstanceReader extends AbstractRealmScopedResourceReader<ClogInstance> {

	/** {@inheritDoc} */
	@Override
	public boolean isReadable(@Nonnull final MediaType mt) {
		return mt.withoutParameters().toString().equals(CLOG_INSTANCE_V1_MEDIA_TYPE.string());
	}

	/** {@inheritDoc} */
	public Class<ClogInstance> getResourceType() {
		return ClogInstance.class;
	}

	/** {@inheritDoc} */
	@Override
	protected ClogInstance createResource(final MediaType mt) {
		return new ClogInstance();
	}

	/** {@inheritDoc} */
	@Override
	protected ClogInstance readResource(@Nonnull final MediaType mediaType, @Nonnull final JsonObject json) {
		ClogInstance resource = super.readResource(mediaType, json);

		resource.setName(propertyFromJson(json, JSON_CLOG_INSTANCE_NAME));
		resource.setPlatformSolutionId(propertyFromJson(json, JSON_PLATFORM_SOLUTION_ID));
		resource.setPlatformInstanceId(propertyFromJson(json, JSON_PLATFORM_INSTANCE_ID));
		resource.setPlatformGroupId(propertyFromJson(json, JSON_PLATFORM_GROUP_ID));
		
		final JsonString statusJsonString = json.getJsonString(JSON_STATUS);
		if(!JsonpSupport.isJsonStringPropertyEmptyOrNull(statusJsonString)){
			resource.setStatus(ClogInstanceStatusE.valueOf(statusJsonString.getString()));
		}

		return resource;
	}

	/**
	 * Gets the property value from JSON String. Check to see it is not empty
	 * before returning it.
	 * 
	 * @param json
	 *            - JsonObject representing the JSON String
	 * @param jsonPropertyName
	 *            - The Property Name of the JSON Property
	 * @return JSON Property Value or Null if it is empty.
	 */
	private String propertyFromJson(@Nonnull final JsonObject json, final String jsonPropertyName) {
		final JsonString propertyJsonValue = json.getJsonString(jsonPropertyName);
		if (!JsonpSupport.isJsonStringPropertyEmptyOrNull(propertyJsonValue)) {
			return propertyJsonValue.getString();
		}
		return null;
	}

}
