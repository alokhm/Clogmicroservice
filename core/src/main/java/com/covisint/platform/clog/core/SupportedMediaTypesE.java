/* Copyright (C) 2014 Covisint. All Rights Reserved. */
package com.covisint.platform.clog.core;

/**
 * List of Media Types Supported by this Microservice.
 * @author Owais.Mohamed@covisint.com
 *
 */
public enum SupportedMediaTypesE {

	/** Media Type for CLOG Instance V1 Resource */
	CLOG_INSTANCE_V1_MEDIA_TYPE("application/vnd.com.covisint.platform.clog.core.cloginstance.v1+json");
	
	/** ENUM constructor */
	private SupportedMediaTypesE(String stringValue){
			this.value=stringValue;
	}
	
	/** String value of the Media Type */
	private String value;
	
	/**
	 * Gets the String value of the Media Type.
	 * @return String representation of the MediaType.
	 */
	public String string(){
		return value;
	}
}
