/* Copyright (C) 2014 Covisint. All Rights Reserved. */
package com.covisint.platform.clog.server.cloginstance.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.transaction.annotation.Transactional;

import com.covisint.core.http.service.core.InvocationContext;
import com.covisint.core.http.service.core.Page;
import com.covisint.core.http.service.core.ResourceReference;
import com.covisint.core.http.service.core.SortCriteria;
import com.covisint.core.http.service.server.ResourceNotFoundException;
import com.covisint.core.http.service.server.service.BaseResourceService;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.platform.clog.core.ClogInstanceStatusE;
import com.covisint.platform.clog.core.cloginstance.ClogInstance;
import com.covisint.platform.clog.server.cloginstance.ClogInstanceDAO;
import com.covisint.platform.clog.server.cloginstance.ClogInstanceService;
import com.covisint.platform.group.client.group.GroupClient;
import com.covisint.platform.group.client.group.entitlement.GroupEntitlementClient;
import com.covisint.platform.group.core.group.Group;
import com.covisint.platform.group.core.group.GroupEntitlement;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

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
	private String LOG_GROUP_KEY = "group_id";
	//private String log_group_value = "1234567890";
	private String REALM_ID_KEY = "x_realm";
	public static String realm_id_value = "realm_alok";
//	public static String alias_name = "alok_group";
	
	
	
	public ClogInstanceServiceImpl(@Nonnull final ClogInstanceDAO dao,
			@Nonnull GroupClient newGroupClient,
			@Nonnull GroupEntitlementClient newGroupEntitlementClient) {
		super(dao);
		groupClient = newGroupClient;
		groupEntitlementClient = newGroupEntitlementClient;
	}

	/**
	 * Build http context.
	 * 
	 * @return HttpContext.
	 */
	@Nonnull
	private HttpContext buildHttpContext() {

		final HttpContext context = new BasicHttpContext();
		context.setAttribute("realmId", InvocationContext.getRealmId());
		context.setAttribute("requestor-app",
				InvocationContext.getRequestorApplicationId());
		context.setAttribute("requestor", InvocationContext.getRequestor());
		return context;
	}

	/** {@inheritDoc} */
	@Nonnull
	@Transactional
	public ClogInstance add(@Nonnull ClogInstance clogInstance) {
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
								.getPlatformInstanceId(), "CLOG INSTANCE",
								InvocationContext.getRealmId()))
				.setRealm(InvocationContext.getRealmId());
		// Creating Group
		try {
			final Group addedGroup = groupClient.add(group, context).get();
			final GroupEntitlement groupEntitlement = new GroupEntitlement()
					.setGroup(addedGroup)
					.setName("ViewLogs")
					.setCreator(InvocationContext.getRequestorId())
					.setCreatorApplicationId(
							InvocationContext.getRequestorApplicationId());
			groupEntitlementClient.add(addedGroup.getId(), groupEntitlement,
					context).get();
			clogInstance.setPlatformGroupId(addedGroup.getId());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		
		
		// creating alias with Filter using apache httpclient
		System.out.println("realm id is " +InvocationContext.getRealmId());
		System.out.println("platform id is" +clogInstance.getPlatformInstanceId());
		String json_input = "{\n"
				+    "\"actions\" : [{\n" 
				+        "\"add\" : {\n" 
				+           "\"index\" : \""+INDEX_NAME+"\",\n" 
				+            "\"alias\" : \""+clogInstance.getPlatformInstanceId()+"\",\n"
				+            "\"filter\" : {\n"
				+                "\"bool\": {\n"
				+                    "\"should\" : [\n"
				+                        "{\"term\" : {\""+REALM_ID_KEY+"\" : \""+InvocationContext.getRealmId()+"\"}},\n"
				+                       "{\"term\" : {\""+LOG_GROUP_KEY+"\" : \""+clogInstance.getPlatformInstanceId()+"\"}}\n"
				+                    "]\n"
				+                "}\n"
				+            "}\n"
				+        "}\n"
				+    "}]\n"
				+"}\n";	
		HttpClient client=HttpClientBuilder.create().build();
		HttpPost post=new HttpPost("http://localhost:9200/_aliases");
		HttpResponse response = null;
		try {
			StringEntity params =new StringEntity(json_input);
			post.setEntity(params);
			response = client.execute(post);
			if (null != response && null != response.getStatusLine()&&response.getStatusLine().getStatusCode() == 200) {
				System.out.println("everthing ok");
			}else {
				System.out.println("ok to rollback");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return super.add(clogInstance);

	}

	/** {@inheritDoc} *//*
	@Nonnull
	//@Transactional
	public ClogInstance delete(@Nonnull final String clogInstanceId) {

		final ClogInstance clogInstance = getResourceDao().getById(
				clogInstanceId);
		final HttpContext context = buildHttpContext();
		if (clogInstance == null) {
			throw new ResourceNotFoundException(clogInstanceId);
		}

		final Group group = groupClient.get(clogInstance.getPlatformGroupId(),
				context).checkedGet();
		final String groupId = group.getId();
		final Multimap<String, String> searchCriteria = HashMultimap.create();
		final List<GroupEntitlement> groupEntitlements = groupEntitlementClient
				.search(searchCriteria, SortCriteria.NONE, Page.ALL, groupId,
						context).checkedGet();

		for (final GroupEntitlement groupEntitlement : groupEntitlements) {
			groupEntitlementClient.delete(groupId, groupEntitlement.getId(),
					context);
		}
		groupClient.delete(groupId, context);

		HttpClient client = HttpClientBuilder.create().build();
		HttpDelete delete = new HttpDelete("http://localhost:9200/realm_id");
		HttpResponse response;
		try {
			response = client.execute(delete);
			if (response.getStatusLine().getStatusCode() == 200) {
				//clogInstance.setStatus(ClogInstanceStatusE.DELETED);
				//getResourceDao().update(clogInstance);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return super.delete(clogInstance);
		System.out.println(ClogInstanceStatusE.DELETED.name());
		clogInstance.setStatus("DELETED");
		
		//return new ClogInstance();
		 clogInstance.setStatus(ClogInstanceStatusE.DELETED);
		 ClogInstance  clogInstance1 =  getResourceDao().update(clogInstance);
		 return clogInstance1;
		  
		 //return getResourceDao().update(clogInstance.setStatus(ClogInstanceStatusE.values()[1]));
		//return getResourceDao().update(clogInstance);
	}
*/
	/** {@inheritDoc} *//*
	@Nonnull
	@Transactional
	public ClogInstance update(@Nonnull final ClogInstance clogInstance) {
		//throw unsupportedUpdateException();
		return super.update(clogInstance);
	}*/

	/** {@inheritDoc} */
	/*
	 * @Override public ClogInstance delete(String id) { throw
	 * unsupportedDeleteException(); }
	 *//**
	 * Creates an unsupported Delete exception for delete methods.
	 */
	/*
	 * private UnsupportedOperationException unsupportedDeleteException() {
	 * return new UnsupportedOperationException(
	 * "Only Soft deletes Allowed. Use Update to change the status flag to deleted."
	 * ); }
	 */
	/**
	 * Creates an unsupported Update exception for update methods.
	 */
	private UnsupportedOperationException unsupportedUpdateException() {
		return new UnsupportedOperationException("Update is not allowed here");
	}

}
