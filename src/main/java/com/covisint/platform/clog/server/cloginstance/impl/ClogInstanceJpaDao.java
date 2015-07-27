/* Copyright (C) 2014 Covisint. All Rights Reserved. */
package com.covisint.platform.clog.server.cloginstance.impl;

import javax.persistence.EntityManager;

import com.covisint.core.http.service.server.dao.BaseRealmScopedResourceJpaDao;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.platform.clog.core.cloginstance.ClogInstance;
import com.covisint.platform.clog.server.cloginstance.ClogInstanceDAO;

/**
 * Default Implementation of {@link ClogInstanceDAO}. 
 * Performs CRUD operations for {@link ClogInstance} resource.
 * @since Jul 10, 2015
 *
 */
public final class ClogInstanceJpaDao extends BaseRealmScopedResourceJpaDao<ClogInstance> implements ClogInstanceDAO {

	/** {@inheritDoc} */
	@Override
	protected Class<ClogInstance> getResourceType() {
		return ClogInstance.class;
	}
	
	
	
	@Override
	protected EntityManager getEntityManager() {
		return super.getEntityManager();
	}
	
	/** {@inheritDoc} *//*
	@Override
	public ClogInstance delete(@Nonnull final ClogInstance resource) {
		throw unsupportedDeleteException();
	}*/
	
	/** {@inheritDoc} */
	@Override
	public ClogInstance delete(@Nonnull final String id) {
		throw unsupportedDeleteException();
	}
	
	/** {@inheritDoc} */
	@Override
	public void deleteAll() {
		throw unsupportedDeleteException();
	}

	/**
	 * Creates an unsupported Delete exception for delete methods.
	 */
	private UnsupportedOperationException unsupportedDeleteException() {
		return new UnsupportedOperationException("Only Soft deletes Allowed. Use Update to change the status flag to deleted.");
	}
	
}
