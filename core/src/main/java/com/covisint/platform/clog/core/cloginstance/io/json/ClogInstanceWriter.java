/* Copyright (C) 2014 Covisint. All Rights Reserved. */
package com.covisint.platform.clog.core.cloginstance.io.json;

import static com.covisint.platform.clog.core.SupportedMediaTypesE.CLOG_INSTANCE_V1_MEDIA_TYPE;
import static com.covisint.platform.clog.core.cloginstance.io.json.ClogInstanceJsonConstants.JSON_CLOG_INSTANCE_NAME;
import static com.covisint.platform.clog.core.cloginstance.io.json.ClogInstanceJsonConstants.JSON_PLATFORM_GROUP_ID;
import static com.covisint.platform.clog.core.cloginstance.io.json.ClogInstanceJsonConstants.JSON_PLATFORM_INSTANCE_ID;
import static com.covisint.platform.clog.core.cloginstance.io.json.ClogInstanceJsonConstants.JSON_PLATFORM_SOLUTION_ID;

import javax.json.JsonObjectBuilder;

import com.covisint.core.http.service.core.io.jsonp.AbstractRealmScopedResourceWriter;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.core.support.constraint.Nullable;
import com.covisint.platform.clog.core.cloginstance.ClogInstance;
import com.google.common.base.Strings;
import com.google.common.net.MediaType;

/**
 * Responsible for writing {@link ClogInstance} to its JSON String
 * representation
 * 
 * @since Jul 8, 2015
 *
 */
public final class ClogInstanceWriter extends AbstractRealmScopedResourceWriter<ClogInstance> {

	/** {@inheritDoc} */
	@Override
	public boolean isWritable(@SuppressWarnings("rawtypes") @Nullable final Class clazz, @Nonnull final MediaType mt) {
		return mt.withoutParameters().toString().equals(CLOG_INSTANCE_V1_MEDIA_TYPE.string());
	}

	/** {@inheritDoc} */
	@Override
	public Class<?> getResourceType() {
		return ClogInstance.class;
	}

	/** {@inheritDoc} */
	@Override
	protected JsonObjectBuilder writeResource(@Nonnull final MediaType mediaType, @Nonnull final ClogInstance resource) {
		final JsonObjectBuilder jsonBuilder = super.writeResource(mediaType, resource);

		propertyToJson(jsonBuilder, JSON_CLOG_INSTANCE_NAME, resource.getName());
		propertyToJson(jsonBuilder, JSON_PLATFORM_SOLUTION_ID, resource.getPlatformSolutionId());
		propertyToJson(jsonBuilder, JSON_PLATFORM_INSTANCE_ID, resource.getPlatformInstanceId());
		propertyToJson(jsonBuilder, JSON_PLATFORM_GROUP_ID, resource.getPlatformGroupId());
		
		if(null!=resource.getStatus()){
			jsonBuilder.add("status", resource.getStatus().name());
		}

		return jsonBuilder;
	}

	/**
	 * Checks if the property value is not empty and then builds the property
	 * into the {@link JsonObjectBuilder}
	 * 
	 * @param jsonBuilder
	 *            - {@link JsonObjectBuilder} in which the property needs to be
	 *            build.
	 * @param jsonPropertyName
	 *            - Name of the Property in the JSON Object
	 * @param jsonPropertyValue
	 *            - Value of the property in the JSON Object
	 */
	private void propertyToJson(final JsonObjectBuilder jsonBuilder, final String jsonPropertyName, final String jsonPropertyValue) {
		if (!Strings.isNullOrEmpty(jsonPropertyValue)) {
			jsonBuilder.add(jsonPropertyName, jsonPropertyValue);
		}
	}

}
