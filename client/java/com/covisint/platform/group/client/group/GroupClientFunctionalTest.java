/* Copyright (C) 2014 Covisint. All Rights Reserved. */

package com.covisint.platform.group.client.group;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.covisint.core.http.service.core.ResourceReference;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.core.support.constraint.NotEmpty;
import com.covisint.platform.group.core.group.Group;

/** Sample test class to test the client implementation. */
public final class GroupClientFunctionalTest {
    
    /** Private constructor. */
    private GroupClientFunctionalTest() {
        
    }

    /**
     * Sample resource.
     * 
     * @return Group resource.
     */
    private Group sampleResource() {
        final Group group = new Group();

        Map<String, String> map = new HashMap<String, String>();
        map.put("en", "Group name in english");
        map.put("fr", "Group name in french");
        group.setName(map);

        map = new HashMap<String, String>();
        map.put("en", "Group description in english");
        map.put("fr", "Group description in french");
        group.setDescription(map);

        return group.setCreator("CRS_ROOT").setRealm("DIAMOND").setCreatorApplicationId("SAM")
                .setOwner(new ResourceReference("CRS_ROOT", "person", "DIAMOND"));
    }

    /**
     * Build Group client.
     * 
     * @return Group client.
     */
    @Nonnull
    private GroupClient buildClient() {
        return new GroupClientFactory("http://localhost:8080").create();
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
        context.setAttribute("requestor-app", "HM");
        context.setAttribute("requestor", "Q92AG6V7");
        return context;
    }

    /**
     * Test method.
     * 
     * @param methodName method name.
     */
    private void testMethod(@Nonnull @NotEmpty String methodName) {
        final GroupClient client = buildClient();
        final HttpContext context = buildHttpContext();
        switch (methodName.toLowerCase()) {
            case "add":
                try {
                    System.out.println(client.add(sampleResource(), context).get());
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "persist":
                try {
                    System.out.println(client.persist(
                            sampleResource().setVersion(0L).setId("fe137d53-6a8b-4586-9b95-d48055d95677"), context)
                            .get());
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "get":
                try {
                    System.out.println(client.get(sampleResource().setId("fe137d53-6a8b-4586-9b95-d48055d95677"),
                            context).get());
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
        final GroupClientFunctionalTest test = new GroupClientFunctionalTest();
        test.testMethod("add");
        test.testMethod("persist");
        test.testMethod("get");
    }
}
