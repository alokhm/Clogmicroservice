/* Copyright (C) 2014 Covisint. All Rights Reserved. */
package com.covisint.platform.clog.server.cloginstance.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.covisint.core.http.service.server.service.BaseResourceService;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.core.support.constraint.NotEmpty;
import com.covisint.platform.clog.core.cloginstance.ClogInstance;
import com.covisint.platform.clog.server.cloginstance.ClogInstanceDAO;
import com.covisint.platform.clog.server.cloginstance.ClogInstanceService;
import com.covisint.platform.clog.server.wrapper.ElasticSearchServiceWrapper;
import com.covisint.platform.clog.server.wrapper.GroupServiceWrapper;
/**
 * @since Jul 13, 2015
 *
 */
public final class ClogInstanceServiceImpl extends
		BaseResourceService<ClogInstance, ClogInstanceDAO> implements
		ClogInstanceService {

	
	private GroupServiceWrapper groupServiceWrapper;
	private ElasticSearchServiceWrapper elasticSearchServiceWrapper;
	private boolean isGroupSvcSuccess;
	private boolean isESSuccess;
	
	/** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ClogInstanceServiceImpl.class);
	
   
	/**
	 * Constructs the Service Impl using the passed in {@link ClogInstanceDAO}
	 * 
	 * @param dao
	 *            - Data Access Object for the {@link ClogInstance}
	 * @param newGroupClient
	 *            Group client instance.
	 * @param newGroupEntitlementClient
	 *            Group entitlement client instance.
	 */
	
	
	
	public ClogInstanceServiceImpl(@Nonnull final ClogInstanceDAO dao,
			@Nonnull GroupServiceWrapper groupServiceWrapper, 
			@Nonnull @NotEmpty ElasticSearchServiceWrapper elasticSearchServiceWrapper) {
		super(dao);
		
		this.groupServiceWrapper = groupServiceWrapper;
		this.elasticSearchServiceWrapper = elasticSearchServiceWrapper;
	}

	
	/** {@inheritDoc} */
	@Nonnull
	@Transactional
	public ClogInstance add(@Nonnull ClogInstance clogInstance) {

		/** create group from groupclient */
		isGroupSvcSuccess = groupServiceWrapper.add(clogInstance);
		log.info("Group service creation completed with status : "
				+ isGroupSvcSuccess);

		/** create an alias in ElasticSearch for platforminstanceId */
		if (isGroupSvcSuccess) {
			isESSuccess = elasticSearchServiceWrapper.createAlias(clogInstance);
			log.info("Elastic Search Alias creation completed with status : "
					+ isESSuccess);
		}

		if (isESSuccess) {
			/** create Cloginstance record */
			log.info("ClogInstance service creation !!!");
			ClogInstance clogInstance1 = super.add(clogInstance);
			if (null == clogInstance1) {
				groupServiceWrapper.delete(clogInstance);
				elasticSearchServiceWrapper.deleteAlias(clogInstance);
			}
		} else {
			groupServiceWrapper.delete(clogInstance);
		}

		log.debug(clogInstance.toString());
		return clogInstance;
	}
	


	
	private void rollback(ClogInstance clogInstance) {
		// TODO Auto-generated method stub
		
	}


	/** {@inheritDoc} */
	@Override
	public ClogInstance delete(@Nonnull final ClogInstance resource) {
		throw unsupportedDeleteException();
	}
	
	/** {@inheritDoc} */
	@Override
	public ClogInstance delete(String id) {
		throw unsupportedDeleteException();
	}
	
	/**
	 * Creates an unsupported Delete exception for delete methods.
	 */
	private UnsupportedOperationException unsupportedDeleteException() {
		return new UnsupportedOperationException("Only Soft deletes Allowed. Use Update to change the status flag to deleted.");
	}

}
