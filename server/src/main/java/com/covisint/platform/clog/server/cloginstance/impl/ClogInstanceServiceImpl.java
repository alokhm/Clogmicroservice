/* Copyright (C) 2014 Covisint. All Rights Reserved. */
package com.covisint.platform.clog.server.cloginstance.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.covisint.core.http.service.core.InvocationContext;
import com.covisint.core.http.service.core.ResourceReference;
import com.covisint.core.http.service.server.service.BaseResourceService;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.core.support.constraint.NotEmpty;
import com.covisint.platform.clog.core.cloginstance.ClogInstance;
import com.covisint.platform.clog.server.cloginstance.ClogInstanceDAO;
import com.covisint.platform.clog.server.cloginstance.ClogInstanceService;
import com.covisint.platform.group.client.group.GroupClient;
import com.covisint.platform.group.client.group.entitlement.GroupEntitlementClient;
import com.covisint.platform.group.core.group.Group;
import com.covisint.platform.group.core.group.GroupEntitlement;

/**
 * @since Jul 13, 2015
 *
 */
public final class ClogInstanceServiceImpl extends
		BaseResourceService<ClogInstance, ClogInstanceDAO> implements
		ClogInstanceService {

	/** Group client instance. */
	private GroupClient groupClient;

	/** Group entitlement client instance. */
	private GroupEntitlementClient groupEntitlementClient;
	private HttpClient client;
	private String elasticSearchUrl;
	
	
	/** realmId. */
	private final static String REALM_ID = "realmId";
	
	/** requestor-app */
	private final static String REQUESTOR_APP = "requestor-app";
	
	/** requestor. */
	private final static String REQUESTOR = "requestor";
	
	/** CLOG INSTANCE. */
	private final static String CLOG_INSTANCE = "CLOG INSTANCE";
	
	/** ViewLogs. */
	private final static String VIEW_LOGS = "view_logs";
	
	
	/** ViewLogs. */
	//private final static String ELASTIC_SEARCH_URL = "http://localhost:9200/_aliases";
	
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
	
	private String INDEX_NAME = "cov_logs";
	
	public ClogInstanceServiceImpl(@Nonnull final ClogInstanceDAO dao,
			@Nonnull GroupClient newGroupClient,
			@Nonnull GroupEntitlementClient newGroupEntitlementClient, 
			@Nonnull @NotEmpty String newElasticSearchUrl) {
		super(dao);
		groupClient = newGroupClient;
		groupEntitlementClient = newGroupEntitlementClient;
		elasticSearchUrl = newElasticSearchUrl;
		
	}

	/**
	 * Build http context.
	 * 
	 * @return HttpContext.
	 */
	@Nonnull
	private HttpContext buildHttpContext() {

		final HttpContext context = new BasicHttpContext();
		context.setAttribute(REALM_ID, InvocationContext.getRealmId());
		context.setAttribute(REQUESTOR_APP,
				InvocationContext.getRequestorApplicationId());
		context.setAttribute(REQUESTOR, InvocationContext.getRequestor());
		return context;
	}

	/** {@inheritDoc} */
	@Nonnull
	@Transactional
	public ClogInstance add(@Nonnull ClogInstance clogInstance) {
		
		/** create group from groupclient */
		groupAdd(clogInstance);
		
			
		/** create Cloginstance record */
		clogInstance = super.add(clogInstance);
		
		/** create an alias in ElasticSearch for platforminstanceId */
		try {
			createAliasInElasticSearch(clogInstance);
			//createAliasFromRestTemplate(clogInstance); 
		} catch (Exception e) {
			log.error("Error occurred while creating Alias in elastic search", e);
		}
		
		return clogInstance;
	}

	/*private void createAliasFromRestTemplate(ClogInstance clogInstance) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		HttpResponse response = restTemplate.postForObject(ELASTIC_SEARCH_URL, createHttpPost(clogInstance), HttpResponse.class);
		validateElasticSearchResponse(response);
		
	}*/

	private void createAliasInElasticSearch(ClogInstance clogInstance)
			throws Exception {
		client = HttpClientBuilder.create().build();
		HttpResponse response = null;
		try {
			response = client.execute(createHttpPost(clogInstance));
			validateElasticSearchResponse(response);

		} catch (ClientProtocolException e) {
			log.error("error occurs when calling elastic search rest endpoint ",e);
		} catch (IOException e) {
		}
 
	}
	
	private void validateElasticSearchResponse(HttpResponse response)
			throws Exception {
		if (!(null != response && null != response.getStatusLine() && response
				.getStatusLine().getStatusCode() == 200)) {
			throw new Exception("Elastic Search Alias creation got failed");
		}
	}

	private HttpUriRequest createHttpPost(ClogInstance clogInstance)
			throws JsonGenerationException, JsonMappingException,
			UnsupportedEncodingException, IOException {
		HttpPost post = new HttpPost(elasticSearchUrl);
		StringEntity params = new StringEntity(generatePayload(clogInstance));
		post.setEntity(params);
		return post;
	}

	private String generatePayload(ClogInstance clogInstance)
			throws JsonGenerationException, JsonMappingException, IOException {

		return new ClogInstanceJsonHelper().createJsonForClogInstance(
				clogInstance.getPlatformInstanceId(), INDEX_NAME,
				clogInstance.getPlatformInstanceId(),
				InvocationContext.getRealmId());

	}

	private void groupAdd(ClogInstance clogInstance) {
		final Group group = new Group();
		final HttpContext context = buildHttpContext();
		
		final Map<String, String> names = new HashMap<>();
		names.put("en", clogInstance.getPlatformInstanceId());
		final Map<String, String> descriptions = new HashMap<>();
		descriptions.put("en", clogInstance.getPlatformInstanceId());

		group.setName(names)
				.setDescription(descriptions)
				.setCreator(InvocationContext.getRequestorId())
				.setCreatorApplicationId(
						InvocationContext.getRequestorApplicationId())
				.setOwner(
						new ResourceReference(clogInstance
								.getPlatformInstanceId(), CLOG_INSTANCE,
								InvocationContext.getRealmId()))
				.setRealm(InvocationContext.getRealmId());
		// Creating Group
		try {
			final Group addedGroup = groupClient.add(group, context).get();
			final GroupEntitlement groupEntitlement = new GroupEntitlement()
					.setGroup(addedGroup)
					.setName(VIEW_LOGS)
					.setCreator(InvocationContext.getRequestorId())
					.setCreatorApplicationId(
							InvocationContext.getRequestorApplicationId());
			groupEntitlementClient.add(addedGroup.getId(), groupEntitlement,
					context).get();
			clogInstance.setPlatformGroupId(addedGroup.getId());
		} catch (InterruptedException | ExecutionException e) {
			log.error("error occurs when adding group", e);
		}

		
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
