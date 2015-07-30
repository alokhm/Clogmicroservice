/* Copyright (C) 2014 Covisint. All Rights Reserved. */
package com.covisint.platform.clog.core;

/**
 * Contains all constants related to the ClogInstance persistence Table.
 * @since 7/8/2015
 */
public final class ClogInstancePersistenceConstants {

	/** Table Name */
	public static final String CLOG_INSTANCE_TABLE_NAME = "CLOG_INSTANCE";
	
	/**
	 * NOT TO BE INSTANTIATED.
	 */
	private ClogInstancePersistenceConstants(){}
	
	/**
	 * Column Names for Clog Instance persistence Table
	 * @since 7/8/2015
	 *
	 */
	public static class Columns{
		public static final String NAME="NAME";
		public static final String PLATFORM_SOLUTION_ID="PLATFORM_SOLUTION_ID";
		public static final String PLATFORM_INSTANCE_ID="PLATFORM_INSTANCE_ID";
		public static final String PLATFORM_GROUP_ID="PLATFORM_GROUP_ID";
		public static final String STATUS="STATUS";
		
		/** NOT TO BE INSTANTIATED */
		private Columns(){}
	}
	
}
