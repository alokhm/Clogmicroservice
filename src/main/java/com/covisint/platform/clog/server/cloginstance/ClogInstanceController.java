/* Copyright (C) 2014 Covisint. All Rights Reserved. */
package com.covisint.platform.clog.server.cloginstance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.covisint.core.http.service.server.controller.BaseResourceController;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.platform.clog.core.cloginstance.ClogInstance;

/**
 * @since Jul 13, 2015
 *
 */
@Controller
@RequestMapping("/clogintances")
public final class ClogInstanceController extends BaseResourceController<ClogInstance, ClogInstanceService> {

	/** {@inheritDoc} */
	@Autowired(required=true)
	public ClogInstanceController(@Qualifier("clogInstanceService") @Nonnull ClogInstanceService service) {
		super(service);
	}

	/** {@inheritDoc} */
	@Override
	protected Class<ClogInstance> getResourceType() {
		return ClogInstance.class;
	}
	

}
