/* Copyright (C) 2014 Covisint. All Rights Reserved. */

package com.covisint.platform.group.client.group.entitlement;

import java.util.concurrent.ExecutionException;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.covisint.core.support.constraint.Nonnull;
import com.covisint.core.support.constraint.NotEmpty;
import com.covisint.platform.group.core.group.Group;
import com.covisint.platform.group.core.group.GroupEntitlement;

/** Sample test class to test the client implementation. */
public final class GroupEntitlementClientFunctionalTest {

    /** Private constructor. */
    private GroupEntitlementClientFunctionalTest() {

    }

    /**
     * Sample resource.
     * 
     * @return Group entitlement resource.
     */
    private GroupEntitlement sampleResource() {
        return new GroupEntitlement().setCreator("POSTMAN").setCreatorApplicationId("POSTMAN").setName("DELETE_PERSON")
                .setGroup(new Group().setId("fe137d53-6a8b-4586-9b95-d48055d95677"));
    }

    /**
     * Build GroupEntitlement client.
     * 
     * @return GroupEntitlement client.
     */
    @Nonnull
    private GroupEntitlementClient buildClient() {
        return new GroupEntitlementClientFactory("http://localhost:8080").create();
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
        final GroupEntitlementClient client = buildClient();
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
                            "c2443b16-3785-463f-b3ea-8d769b1e0be7", context).get());
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "delete":
                client.delete("", context);
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
        final GroupEntitlementClientFunctionalTest test = new GroupEntitlementClientFunctionalTest();
        test.testMethod("add");
        test.testMethod("get");
    }
}
