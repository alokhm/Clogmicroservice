/* Copyright (C) 2014 Covisint. All Rights Reserved. */

package com.covisint.platform.group.client.group.membership;

import java.util.concurrent.ExecutionException;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.covisint.core.http.service.core.ResourceReference;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.core.support.constraint.NotEmpty;
import com.covisint.platform.group.core.group.Group;
import com.covisint.platform.group.core.group.GroupMembership;

/** Sample test class to test the client implementation. */
public final class GroupMembershipClientFunctionalTest {

    /** Private constructor. */
    private GroupMembershipClientFunctionalTest() {

    }

    /**
     * Sample resource.
     * 
     * @return GroupMembership resource.
     */
    private GroupMembership sampleResource() {
        return new GroupMembership().setCreator("POSTMAN").setCreatorApplicationId("POSTMAN")
                .setGroup(new Group().setId("fe137d53-6a8b-4586-9b95-d48055d95677"))
                .setMember(new ResourceReference("72f9339c-4fac-4c33-831d-931ddabd1ac5", "person", "DIAMOND"));
    }

    /**
     * Build GroupMembership client.
     * 
     * @return GroupMembership client.
     */
    @Nonnull
    private GroupMembershipClient buildClient() {
        return new GroupMembershipClientFactory("http://localhost:8080").create();
    }

    /**
     * Build http context.
     * 
     * @return HttpContext.
     */
    @Nonnull
    private HttpContext buildHttpContext() {
        final HttpContext context = new BasicHttpContext();
        context.setAttribute("realmId", "DIAMOND");
        context.setAttribute("requestor-app", "POSTMAN");
        context.setAttribute("requestor", "POSTMAN");
        return context;
    }

    /**
     * Test method.
     * 
     * @param methodName method name.
     */
    private void testMethod(@Nonnull @NotEmpty String methodName) {
        final GroupMembershipClient client = buildClient();
        final HttpContext context = buildHttpContext();
        switch (methodName.toLowerCase()) {
            case "add":
                try {
                    System.out.println(client.add("fe137d53-6a8b-4586-9b95-d48055d95677", sampleResource(), context)
                            .get());
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "get":
                try {
                    System.out.println(client.get("fe137d53-6a8b-4586-9b95-d48055d95677",
                            "2806158e-c654-4ed3-99d9-479a94335bc9", context).get());
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println(e.getMessage());
                }
                break;
            default:
                break;
        }
    }

    /**
     * Main method.
     * 
     * @param args arguments.
     */
    public static void main(String[] args) {
        final GroupMembershipClientFunctionalTest test = new GroupMembershipClientFunctionalTest();
        test.testMethod("add");
        test.testMethod("get");
    }
}
