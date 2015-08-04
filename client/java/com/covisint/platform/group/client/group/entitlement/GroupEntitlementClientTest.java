/* Copyright (C) 2014 Covisint. All Rights Reserved. */

package com.covisint.platform.group.client.group.entitlement;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import junit.framework.Assert;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.covisint.core.http.service.client.UnsupportedResponseContentTypeException;
import com.covisint.core.http.service.core.Page;
import com.covisint.core.http.service.core.SortCriteria;
import com.covisint.core.http.service.core.io.EntityReaderException;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.core.support.httpclient.HttpClientBuilder;
import com.covisint.platform.group.client.group.util.WireMockSupport;
import com.covisint.platform.group.core.group.GroupEntitlement;
import com.covisint.platform.group.core.group.io.SupportedMediaType;
import com.covisint.platform.group.core.group.io.json.GroupEntitlementReader;
import com.covisint.platform.group.core.group.io.json.GroupEntitlementWriter;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.MediaType;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/** Test class to test {@link GroupEntitlementClient}. */
public class GroupEntitlementClientTest {

    /** Supported media type for group entitlement. */
    private static final String GROUP_ENTITLEMENT_MEDIA_TYPE = SupportedMediaType.GROUP_ENTITLEMENT_MT.value();

    /** Unsupported media type. */
    private static final String UNSUPPORTED_MEDIA_TYPE = SupportedMediaType.GROUP_MT.value();

    /** Mock enabled set to true. */
    private static final Boolean MOCK_ENABLED = Boolean.TRUE;

    /** The wire mock server. */
    private static WireMockServer wireMockServer;

    /** The group entitlement client. */
    private static GroupEntitlementClient client;

    /** The http context. */
    private static HttpContext httpContext;

    /** The Json parser. */
    private static JSONParser parser;

    /** The http clinet. */
    private static HttpClient httpClient;

    /** The executor service. */
    private static ListeningExecutorService executor;

    /**
     * Create group entitlement.
     * 
     * @return GroupEntitlement - Returns the group entitlement.
     */
    @Nonnull
    private GroupEntitlement sampleGroupEntitlementOne() {
        return new GroupEntitlement().setId("1234").setCreationInstant(12345678L).setCreator("Person")
                .setCreatorApplicationId("Group-entitlement").setVersion(0L).setName("User Admin");
    }

    /**
     * Create group entitlement.
     * 
     * @return GroupEntitlement - Returns the group entitlement.
     */
    @Nonnull
    private GroupEntitlement sampleGroupEntitlementTwo() {
        return new GroupEntitlement().setId("5678").setCreationInstant(12345678L).setCreator("Person")
                .setCreatorApplicationId("Group-entitlement").setVersion(0L).setName("Test Admin");
    }

    /**
     * Create group entitlements.
     * 
     * @return List<GroupEntitlements> Returns the list of group entitlements.
     */
    @Nonnull
    private List<GroupEntitlement> getGroupEntitlements() {
        final List<GroupEntitlement> groupEntitlements = new ArrayList<>();
        groupEntitlements.add(sampleGroupEntitlementOne());
        groupEntitlements.add(sampleGroupEntitlementTwo());
        return groupEntitlements;
    }

    /**
     * Assert two group entitlements objects with name.
     * 
     * @param responseEntitlement The response group entitlement object to assert.
     * @param expectedEntitlement The expected group entitlement object to assert.
     */
    private void assertEquals(@Nonnull GroupEntitlement responseEntitlement,
            @Nonnull GroupEntitlement expectedEntitlement) {
        Assert.assertNotNull(responseEntitlement);
        Assert.assertEquals(responseEntitlement.getId(), expectedEntitlement.getId());
        Assert.assertEquals(responseEntitlement.getCreator(), expectedEntitlement.getCreator());
        Assert.assertEquals(responseEntitlement.getCreatorApplicationId(),
                expectedEntitlement.getCreatorApplicationId());
        Assert.assertEquals(responseEntitlement.getVersion(), expectedEntitlement.getVersion());
        Assert.assertEquals(responseEntitlement.getName(), expectedEntitlement.getName());
    }

    /**
     * Assert group entitlement objects with null values.
     * 
     * @param responseEntitlement The response group entitlement object to assert.
     */
    private void assertNull(@Nonnull GroupEntitlement responseEntitlement) {
        Assert.assertNotNull(responseEntitlement);
        Assert.assertNull(responseEntitlement.getId());
        Assert.assertNull(responseEntitlement.getCreator());
        Assert.assertNull(responseEntitlement.getCreatorApplicationId());
        Assert.assertNull(responseEntitlement.getVersion());
        Assert.assertNull(responseEntitlement.getName());
    }

    /**
     * Returns the constructed JSON object.
     * 
     * @param resourcePath The resource path.
     * @return Returns the JSON object
     * @throws ParseException The exception this method may throw.
     * @throws IOException The exception this method may throw.
     */
    @Nonnull
    private JSONObject getJsonObject(@Nonnull String resourcePath) throws ParseException, IOException {

        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        return (JSONObject) parser.parse(reader);
    }

    /**
     * Returns the constructed JSON array.
     * 
     * @param resourcePath The resource path.
     * @return Returns the JSON array.
     * @throws ParseException The exception this method may throw.
     * @throws IOException The exception this method may throw.
     */
    @Nonnull
    private JSONArray getJsonArray(@Nonnull String resourcePath) throws ParseException, IOException {

        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        return (JSONArray) parser.parse(reader);
    }

    /**
     * Initializes required fields.
     * 
     * @throws IOException - This method may throw this exception.
     * @throws ParseException - This method may throw this exception.
     */
    @BeforeClass
    public final void init() throws ParseException, IOException {

        httpClient = new HttpClientBuilder().setMaxConnections(1024).setConnectionClosedAfterResponse(true)
                .addXRequestorInterceptor().build();

        executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1024));

        /** Look for a free port. */
        final int port = WireMockSupport.findFreePort();
        client = new GroupEntitlementClientBuilder().addEntityWriter(new GroupEntitlementWriter())
                .addEntityReader(new GroupEntitlementReader()).setExecutorService(executor).setHttpClient(httpClient)
                .setServiceBaseUrl("http://localhost:" + port).build();

        httpContext = new BasicHttpContext();
        parser = new JSONParser();
        if (MOCK_ENABLED) {
            wireMockServer = new WireMockServer(port);
            wireMockServer.start();
            WireMock.configureFor("localhost", port);
            initializeMockServer();
        }

    }

    /** Stops the wire mock server after running test cases. */
    @AfterClass
    public final void destroy() {
        if (MOCK_ENABLED) {
            wireMockServer.stop();
        }
        executor.shutdown();
    }

    /**
     * Initializes mock server.
     * 
     * @throws IOException - This method may throw this exception.
     * @throws ParseException - This method may throw this exception.
     */
    public final void initializeMockServer() throws ParseException, IOException {

        // Stub data for scenario which returns a group entitlement.
        stubFor(get(urlMatching("/groups/group_001/entitlements/1234")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_ENTITLEMENT_MEDIA_TYPE).withBody(
                        getJsonObject("json/get_group_entitlement.json").toString())));

        // Stub data for scenario which returns a group entitlement with empty body.
        stubFor(get(urlMatching("/groups/group_001/entitlements/empty_body")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_ENTITLEMENT_MEDIA_TYPE).withBody(
                        getJsonObject("json/empty_body.json").toString())));

        // Stub data for scenario which returns a group entitlement with empty values.
        stubFor(get(urlMatching("/groups/group_001/entitlements/empty_values")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_ENTITLEMENT_MEDIA_TYPE).withBody(
                        getJsonObject("json/get_group_entitlement_with_empty_values.json").toString())));

        // Stub data for scenario which returns incorrect media type.
        stubFor(get(urlMatching("/groups/group_001/entitlements/5678")).willReturn(
                aResponse().withHeader("Content-Type", UNSUPPORTED_MEDIA_TYPE).withBody(
                        getJsonObject("json/get_group_entitlement.json").toString())));

        // Stub data for scenario which returns incorrect media type.
        stubFor(get(urlMatching("/groups/group_001/entitlements")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_ENTITLEMENT_MEDIA_TYPE).withBody(
                        getJsonArray("json/get_group_entitlements.json").toString())));

        // Stub data for scenario which adds a group entitlement.
        stubFor(post(urlMatching("/groups/group_001/entitlements")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_ENTITLEMENT_MEDIA_TYPE)
                        .withHeader("Accept", GROUP_ENTITLEMENT_MEDIA_TYPE)
                        .withBody(getJsonObject("json/post_group_entitlement.json").toString())));

        // Stub data for scenario which deletes a group entitlement.
        stubFor(delete(urlMatching("/groups/group_001/entitlements/del123")).willReturn(
                aResponse().withStatus(HttpStatus.SC_NO_CONTENT)));

        // Stub data for scenario where json structure is not supported.
        stubFor(get(urlMatching("/groups/group_001/entitlements/3456")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_ENTITLEMENT_MEDIA_TYPE).withBody("[10,20]")));

        // Stub data for scenario which returns incorrect media type.
        stubFor(get(urlMatching("/groups/group_001/entitlements\\?id=5678&id=1234&page=1&pageSize=10")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_ENTITLEMENT_MEDIA_TYPE).withBody(
                        getJsonArray("json/get_group_entitlements.json").toString())));
    }

    /** Test to get a group entitlement with unsupported media type. */
    @Test(expectedExceptions = { UnsupportedResponseContentTypeException.class })
    public final void testGetGroupEntitlementWithUnsupportedMediaType() {

        client.get("group_001", "5678", httpContext).checkedGet();
    }

    /** Test case to get a group entitlement. */
    @Test
    public final void testGetGroupEntitlement() {

        /** Get the group entitlement by id. */
        final GroupEntitlement retrievedEntitlement = client.get("group_001", "1234", httpContext).checkedGet();

        /** Assert for returned object */
        Assert.assertNotNull(retrievedEntitlement);
        assertEquals(sampleGroupEntitlementOne(), retrievedEntitlement);
    }

    /** Test case to get a group entitlement with empty body. */
    @Test
    public final void testGetGroupEntitlementWithInvalidEntitlementId() {
        assertNull(client.get("group_001", "empty_body", httpContext).checkedGet());
    }

    /** Test case to get a group entitlement with empty values. */
    @Test
    public final void testGetGroupEntitlementWithEmptyValues() {
        assertNull(client.get("group_001", "empty_values", httpContext).checkedGet());
    }

    /** Test to add a group entitlement. */
    @Test
    public final void testAddGroupEntitlement() {

        /** Create expected group entitlement. */
        final GroupEntitlement expectedGroupEntitlement = sampleGroupEntitlementTwo();

        final GroupEntitlement addedEntitlement = client.add("group_001", expectedGroupEntitlement, httpContext)
                .checkedGet();

        /** Assert for added object */
        Assert.assertNotNull(addedEntitlement);
        assertEquals(expectedGroupEntitlement, addedEntitlement);
    }

    /** Test to delete a group entitlement. */
    @Test
    public final void testDeleteGroupMembership() {
        /** Delete the entitlement. */
        client.delete("group_001", "del123", httpContext).checkedGet();
    }

    /** Test for missing base service url. */
    @Test(expectedExceptions = { IllegalStateException.class })
    public final void testMissingBaseServiceUrl() {
        new GroupEntitlementClientBuilder().setHttpClient(httpClient).setExecutorService(executor).build();
    }

    /** Test for malformed base service url. */
    @Test(expectedExceptions = { IllegalStateException.class })
    public final void testMalformedUri() {
        new GroupEntitlementClientBuilder().setServiceBaseUrl("{http://localhost:8080").setHttpClient(httpClient)
                .setExecutorService(executor).build();
    }

    /** Test for reading unsupported json structure. */
    @Test(expectedExceptions = { EntityReaderException.class })
    public final void testReadingForIncorrectJsonStructure() {

        client.get("group_001", "3456", httpContext).checkedGet();
    }

    /** Test to search group entitlements. */
    @Test
    public final void testSearchGroupEntitlements() {
        final Multimap<String, String> searchCriteria = HashMultimap.create();
        searchCriteria.put("id", "1234");
        searchCriteria.put("id", "5678");
        final List<GroupEntitlement> groupEntitlements = client.search(searchCriteria, SortCriteria.builder().build(),
                new Page(1, 10), "group_001", httpContext).checkedGet();
        Assert.assertNotNull(groupEntitlements);
        Assert.assertEquals(getGroupEntitlements(), groupEntitlements);
    }

    /** Test case for adding group entitlement with unsupported client method. */
    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public final void testAdd() {
        client.add(new GroupEntitlement(), httpContext);
    }

    /** Test case for deleting group entitlement with unsupported client method. */
    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public final void testDelete() {
        client.delete("group_entitlement_1", httpContext);
    }

    /** Test case for getting group entitlement with unsupported client method. */
    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public final void testGetByResource() {
        client.get(new GroupEntitlement(), httpContext);
    }

    /** Test case for getting group entitlement by id with unsupported client method. */
    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public final void testGetbyId() {
        client.get("group_entitlement_1", httpContext);
    }

    /** Test case for persisting group entitlements with unsupported client method. */
    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public final void testPersist() {
        client.persist(new GroupEntitlement(), httpContext);
    }

    /** Test case for searching group entitlements with unsupported client method. */
    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public final void testSearch() {
        final Multimap<String, String> searchCriteria = HashMultimap.create();
        client.search(searchCriteria, SortCriteria.builder().build(), new Page(1, 10), httpContext);
    }

    /** Test case for searching group entitlements with unsupported client method. */
    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public final void testSearchWithMediatypeEndpoint() {
        final Multimap<String, String> searchCriteria = HashMultimap.create();
        client.search(searchCriteria, SortCriteria.builder().build(), new Page(1, 10), httpContext,
                MediaType.parse(GROUP_ENTITLEMENT_MEDIA_TYPE), "/test");
    }

    /** Test case for {@link @Nonnull} annotations. */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public final void testNonnullAnnotation() {
        client.get("group_001", null, httpContext);
    }

    /** Test case for {@link NotEmpty} annotations. */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public final void testNotEmptyAnnotation() {
        client.get("group_001", "", httpContext);
    }

}
