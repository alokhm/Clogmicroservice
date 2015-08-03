/* Copyright (C) 2014 Covisint. All Rights Reserved. */
package com.covisint.platform.clog.core;

import com.covisint.platform.clog.core.cloginstance.ClogInstance;

/**
 * Status Constants for {@link ClogInstance} resource.
 * @since Jul 10, 2015
 *
 */
public enum ClogInstanceStatusE {
	/** CLOG Instance is active and being used right now. */
	ACTIVE,
	/** CLOG Instance has been deleted and is not being used anymore. */
	DELETED;

}
