/**
 * 
 */
package com.covisint.platform.clog.server.wrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.covisint.core.http.service.core.InvocationContext;
import com.covisint.core.http.service.core.Page;
import com.covisint.core.http.service.core.ResourceReference;
import com.covisint.core.http.service.core.ServiceException;
import com.covisint.core.http.service.core.SortCriteria;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.platform.clog.core.cloginstance.ClogInstance;
import com.covisint.platform.clog.model.ClogInstancesConstants;
import com.covisint.platform.clog.server.wrapper.GroupServiceWrapper;
import com.covisint.platform.group.client.group.GroupClient;
import com.covisint.platform.group.client.group.entitlement.GroupEntitlementClient;
import com.covisint.platform.group.core.group.Group;
import com.covisint.platform.group.core.group.GroupEntitlement;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Lingesh.M
 *
 *
 */
public class GroupServiceWrapper  {

	/** Class logger. */
    private final Logger log = LoggerFactory.getLogger(GroupServiceWrapper.class);
	
    private GroupClient groupClient;
    
    private GroupEntitlementClient groupEntitlementClient;
    
    /**
     * 
     * @param newGroupClient
     * @param newGroupEntitlementClient
     */
    public GroupServiceWrapper(@Nonnull GroupClient newGroupClient,
			@Nonnull GroupEntitlementClient newGroupEntitlementClient) {
		groupClient = newGroupClient;
		groupEntitlementClient = newGroupEntitlementClient;
		
	}

	public boolean add(ClogInstance clogInstance) throws ServiceException {
		final Group group = new Group();
		final HttpContext context = buildHttpContext();
		
		final Map<String, String> names = new HashMap<>();
		names.put(ClogInstancesConstants.EN, clogInstance.getPlatformInstanceId());
		final Map<String, String> descriptions = new HashMap<>();
		descriptions.put(ClogInstancesConstants.EN, clogInstance.getPlatformInstanceId());

		group.setName(names)
				.setDescription(descriptions)
				.setCreator(InvocationContext.getRequestor())
				.setCreatorApplicationId(
						InvocationContext.getRequestorApplicationId())
				.setOwner(
						new ResourceReference(clogInstance
								.getPlatformInstanceId(), ClogInstancesConstants.CLOG_INSTANCE,
								InvocationContext.getRealmId()))
				.setRealm(InvocationContext.getRealmId());
		// Creating Group
		try {
			final Group addedGroup = groupClient.add(group, context).get();
			final GroupEntitlement groupEntitlement = new GroupEntitlement()
					.setGroup(addedGroup)
					.setName(ClogInstancesConstants.VIEW_LOGS)
					.setCreator(InvocationContext.getRequestor())
					.setCreatorApplicationId(
							InvocationContext.getRequestorApplicationId());
			groupEntitlementClient.add(addedGroup.getId(), groupEntitlement,
					context).get();
			clogInstance.setPlatformGroupId(addedGroup.getId());
			log.debug(clogInstance.toString());
		} catch (Exception e) {
			log.error("error occurs when adding group"+ e.getMessage());
			return false;
		}
		
return true;

	}
	
	
	public void delete(ClogInstance clogInstance) throws ServiceException {
		final HttpContext context = buildHttpContext();
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
		log.info("group service got deleted");
	}
	
	/**
	 * Build http context.
	 * 
	 * @return HttpContext.
	 */
	@Nonnull
	private HttpContext buildHttpContext() {

		final HttpContext context = new BasicHttpContext();
		context.setAttribute(ClogInstancesConstants.REALM_ID, InvocationContext.getRealmId());
		context.setAttribute(ClogInstancesConstants.REQUESTOR_APP,
				InvocationContext.getRequestorApplicationId());
		context.setAttribute(ClogInstancesConstants.REQUESTOR, InvocationContext.getRequestor());
		return context;
	}


}
