/* Copyright (C) 2014 Covisint. All Rights Reserved. */

package com.covisint.platform.group.client.group.membership;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.covisint.core.http.service.core.ResourceReference;
import com.covisint.core.http.service.core.SortCriteria;
import com.covisint.core.http.service.core.io.EntityReaderException;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.core.support.constraint.NotEmpty;
import com.covisint.core.support.httpclient.HttpClientBuilder;
import com.covisint.platform.group.client.group.util.WireMockSupport;
import com.covisint.platform.group.core.group.Group;
import com.covisint.platform.group.core.group.GroupMembership;
import com.covisint.platform.group.core.group.io.SupportedMediaType;
import com.covisint.platform.group.core.group.io.json.GroupMembershipReader;
import com.covisint.platform.group.core.group.io.json.GroupMembershipWriter;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.net.MediaType;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/** Test class to test {@link GroupMembershipClient}. */
public class GroupMembershipClientTest {

    /** Supported media type for group membership. */
    private static final String GROUP_MEMBERSHIP_MEDIA_TYPE = SupportedMediaType.GROUP_MEMBERSHIP_MT.value();

    /** Unsupported media type. */
    private static final String UNSUPPORTED_MEDIA_TYPE = SupportedMediaType.GROUP_ENTITLEMENT_MT.value();

    /** Mock enabled set to true. */
    private static final Boolean MOCK_ENABLED = Boolean.TRUE;

    /** The wire mock server. */
    private static WireMockServer wireMockServer;

    /** The group client. */
    private static GroupMembershipClient client;

    /** The http context. */
    private static HttpContext httpContext;

    /** The http clinet. */
    private static HttpClient httpClient;

    /** The executor service. */
    private static ListeningExecutorService executor;

    /** The parser to parse JSON strings. */
    private JSONParser parser;

    /**
     * Reads the JSON object from the file.
     * 
     * @param resourcePath The resource path.
     * @return {@link JSONObject}
     * @throws ParseException The exception this method may throw.
     * @throws IOException The exception this method may throw.
     */
    @Nonnull
    private JSONObject getJsonObject(String resourcePath) throws IOException, ParseException {

        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        final JSONObject jsonObject = (JSONObject) parser.parse(reader);

        return jsonObject;
    }

    /**
     * Read the JSON Array from the file.
     * 
     * @param resourcePath The resource location
     * @return {@link JSONArray}
     * @throws IOException {@link IOException} The exception this method may throw..
     * @throws ParseException {@link ParseException} The exception this method may throw..
     */
    @Nonnull
    private JSONArray getJsonArray(@Nonnull @NotEmpty String resourcePath) throws IOException, ParseException {

        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        parser = new JSONParser();
        final JSONArray jsonArray = (JSONArray) parser.parse(reader);
        return jsonArray;
    }

    /**
     * Assert group membership objects.
     * 
     * @param expectedMembership the actual group membership object.
     * @param actualMembership The expected group membership object.
     */
    private void assertGroupMemberships(@Nonnull GroupMembership expectedMembership,
            @Nonnull GroupMembership actualMembership) {

        Assert.assertNotNull(expectedMembership);
        Assert.assertEquals(expectedMembership.getId(), actualMembership.getId());
        Assert.assertEquals(expectedMembership.getCreator(), actualMembership.getCreator());
        Assert.assertEquals(expectedMembership.getCreatorApplicationId(), actualMembership.getCreatorApplicationId());
        Assert.assertEquals(expectedMembership.getVersion(), actualMembership.getVersion());
        Assert.assertEquals(expectedMembership.getMember().getId(), actualMembership.getMember().getId());
        Assert.assertEquals(expectedMembership.getMember().getRealm(), actualMembership.getMember().getRealm());
        Assert.assertEquals(expectedMembership.getMember().getType(), actualMembership.getMember().getType());
        Assert.assertEquals(expectedMembership.getGroup().getId(), actualMembership.getGroup().getId());
        Assert.assertEquals(expectedMembership.getGroup().getRealm(), actualMembership.getGroup().getRealm());
        Assert.assertEquals(expectedMembership.getId(), actualMembership.getId());
        Assert.assertEquals(expectedMembership.getCreator(), actualMembership.getCreator());
        Assert.assertEquals(expectedMembership.getCreatorApplicationId(), actualMembership.getCreatorApplicationId());
        Assert.assertEquals(expectedMembership.getVersion(), actualMembership.getVersion());
    }

    /**
     * Assert the group membership objects which has null values.
     * 
     * @param responseGroupMembership The actual group membership.
     */
    private void assertNull(GroupMembership responseGroupMembership) {
        Assert.assertNotNull(responseGroupMembership);
        Assert.assertNull(responseGroupMembership.getId());
        Assert.assertNull(responseGroupMembership.getCreator());
        Assert.assertNull(responseGroupMembership.getCreatorApplicationId());
        Assert.assertNull(responseGroupMembership.getVersion());
        Assert.assertNull(responseGroupMembership.getMember());
    }

    /**
     * Assert the group membership objects with group which has null values.
     * 
     * @param responseGroupMembership The actual group membership.
     */
    private void assertNullWithGroup(GroupMembership responseGroupMembership) {
        assertNull(responseGroupMembership);
        Assert.assertNotNull(responseGroupMembership.getGroup());
        Assert.assertNull(responseGroupMembership.getGroup().getId());
        Assert.assertNull(responseGroupMembership.getGroup().getCreator());
        Assert.assertNull(responseGroupMembership.getGroup().getCreatorApplicationId());
        Assert.assertNull(responseGroupMembership.getGroup().getVersion());
        Assert.assertNotNull(responseGroupMembership.getGroup().getName());
        Assert.assertTrue(responseGroupMembership.getGroup().getName().isEmpty());
        Assert.assertNotNull(responseGroupMembership.getGroup().getDescription());
        Assert.assertTrue(responseGroupMembership.getGroup().getDescription().isEmpty());
        Assert.assertNull(responseGroupMembership.getGroup().getOwner());
        Assert.assertNotNull(responseGroupMembership.getGroup().getEntitlements());
        Assert.assertTrue(responseGroupMembership.getGroup().getEntitlements().isEmpty());

    }

    /**
     * Initializes required fields.
     * 
     * @throws ParseException The exception this method might throw.
     * @throws IOException The exception this method might throw.
     */
    @BeforeClass
    private void init() throws IOException, ParseException {

        httpClient = new HttpClientBuilder().setMaxConnections(1024).setConnectionClosedAfterResponse(true)
                .addXRequestorInterceptor().build();

        executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1024));
        /** Look for a free port. */
        final int port = WireMockSupport.findFreePort();
        client = new GroupMembershipClientBuilder().addEntityWriter(new GroupMembershipWriter())
                .addEntityReader(new GroupMembershipReader()).setExecutorService(executor).setHttpClient(httpClient)
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

    /**
     * Initializes mock server.
     * 
     * @throws ParseException The exception this method might throw.
     * @throws IOException The exception this method might throw.
     */
    private void initializeMockServer() throws IOException, ParseException {

        // Stub data for scenario which returns incorrect media type.
        stubFor(get(
                urlMatching("/groups/fab935ce-babe-4846-b508-5d2af0c4d1a6/memberships/"
                        + "4ab935ce-babe-4846-b508-5d2af0c4d1a6")).willReturn(
                aResponse().withHeader("Content-Type", UNSUPPORTED_MEDIA_TYPE).withBody(
                        getJsonObject("json/group-membership-get-or-post.json").toString())));

        // Stub data for scenario which returns a group membership.
        stubFor(get(
                urlMatching("/groups/fab935ce-get-4846-b508-5d2af0c4d1a6/memberships/"
                        + "1a993355-7cfe-4506-8657-e33d35210f2b")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_MEMBERSHIP_MEDIA_TYPE).withBody(
                        getJsonObject("json/group-membership-get-or-post.json").toString())));

        // Stub data for scenario which returns a group membership.
        stubFor(get(urlMatching("/groups/fab935ce-get-4846-b508-5d2af0c4d1a6/memberships/empty_body")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_MEMBERSHIP_MEDIA_TYPE).withBody(
                        getJsonObject("json/empty_body.json").toString())));

        // Stub data for scenario which returns a group membership.
        stubFor(get(urlMatching("/groups/fab935ce-get-4846-b508-5d2af0c4d1a6/memberships/empty_values")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_MEMBERSHIP_MEDIA_TYPE).withBody(
                        getJsonObject("json/group-membership-with-empty-values.json").toString())));

        // Stub data for scenario which adds a group membership.
        stubFor(post(urlMatching("/groups/fab935ce-get-4846-b508-5d2af0c4d1a6/memberships")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_MEMBERSHIP_MEDIA_TYPE).withBody(
                        getJsonObject("json/group-membership-get-or-post.json").toString())));

        // Stub data for scenario which deletes a group membership.
        stubFor(delete(
                urlMatching("/groups/fab935ce-get-4846-b508-5d2af0c4d1a6/memberships/"
                        + "2ab935ce-babe-4846-b508-5d2af0c4d1a6")).willReturn(
                aResponse().withStatus(HttpStatus.SC_NO_CONTENT)));

        // Stub data for scenario where json structure is not supported.
        stubFor(get(
                urlMatching("/groups/fab935ce-babe-4846-b508-5d2af0c4d1a6/memberships/"
                        + "b5213cd0-fcd0-4461-a334-79d7d74d9f91")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_MEMBERSHIP_MEDIA_TYPE).withBody("[10,20]")));

        // Stub data for scenario which returns group memberships.
        stubFor(get(
                urlMatching("/memberships\\?member.type=person&member.realmId=PDM&"
                        + "member.id=72f9339c-4fac-4c33-831d-931ddabd1ac5&page=1&pageSize=10")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_MEMBERSHIP_MEDIA_TYPE).withBody(
                        getJsonArray("json/group-membership-search.json").toString())));

        // Stub data for scenario which returns group memberships.
        stubFor(get(urlMatching("/groups/h4560543-del-425a-9021-30f4xedggfjk1/memberships")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_MEMBERSHIP_MEDIA_TYPE).withBody(
                        getJsonArray("json/group-membership-list.json").toString())));

    }

    /** Stops the wire mock server after running test cases. */
    @AfterClass
    public final void destroy() {
        if (MOCK_ENABLED) {
            wireMockServer.stop();
        }
        executor.shutdown();
    }

    /** Test to get a group membership with unsupported media type. */
    @Test(expectedExceptions = { UnsupportedResponseContentTypeException.class })
    public final void testGetGroupMembershipWithUnsupportedMediaType() {

        client.get("fab935ce-babe-4846-b508-5d2af0c4d1a6", "4ab935ce-babe-4846-b508-5d2af0c4d1a6", httpContext)
                .checkedGet();
    }

    /** Test case to get a group membership. */
    @Test
    public final void testGetGroupMembership() {

        final String groupMembershipId = "1a993355-7cfe-4506-8657-e33d35210f2b";
        final String groupId = "fab935ce-get-4846-b508-5d2af0c4d1a6";

        // Create expected group membership.
        final ResourceReference memberReference = new ResourceReference("72f9339c-4fac-4c33-831d-931ddabd1ac5",
                "person", "PDM");
        final Group group = new Group().setId(groupId).setRealm("PDM");
        final GroupMembership membership = new GroupMembership().setId(groupMembershipId).setCreator("CRS_ROOT")
                .setCreatorApplicationId("MONITOR").setVersion(0L).setCreationInstant(1397016000000L);
        membership.setMember(memberReference);
        membership.setGroup(group);

        // Get the group membership by id.
        final GroupMembership retrievedMembership = client.get(groupId, groupMembershipId, httpContext).checkedGet();

        // Assert for returned object.
        assertGroupMemberships(membership, retrievedMembership);

    }

    /** Test case to get a group membership with empty body. */
    @Test
    public final void testGetGroupMembershipWithEmptyBody() {
        // Assert for returned object.
        assertNull(client.get("fab935ce-get-4846-b508-5d2af0c4d1a6", "empty_body", httpContext).checkedGet());

    }

    /** Test case to get a group membership with empty values. */
    @Test
    public final void testGetGroupMembershipWithEmptyValues() {
        assertNullWithGroup(client.get("fab935ce-get-4846-b508-5d2af0c4d1a6", "empty_values", httpContext).checkedGet());
    }

    /** Test to add a group membership. */
    @Test
    public final void testAddGroupMembership() {

        final String groupMembershipId = "1a993355-7cfe-4506-8657-e33d35210f2b";
        final String groupId = "fab935ce-get-4846-b508-5d2af0c4d1a6";

        // Create group membership.
        final ResourceReference memberReference = new ResourceReference("72f9339c-4fac-4c33-831d-931ddabd1ac5",
                "person", "PDM");
        final Group group = new Group().setId(groupId).setRealm("PDM");
        final GroupMembership membership = new GroupMembership().setId(groupMembershipId).setCreator("CRS_ROOT")
                .setCreatorApplicationId("MONITOR").setVersion(0L).setCreationInstant(1397016000000L);
        membership.setMember(memberReference);
        membership.setGroup(group);

        // Add the group membership.
        final GroupMembership addedMembership = client.add(groupId, membership, httpContext).checkedGet();

        // Assert for returned object.
        assertGroupMemberships(membership, addedMembership);

    }

    /** Test to delete a group membership. */
    @Test
    public final void testDeleteGroupMembership() {

        final String groupMembershipId = "2ab935ce-babe-4846-b508-5d2af0c4d1a6";
        final String groupId = "fab935ce-get-4846-b508-5d2af0c4d1a6";

        // Create expected group membership.
        final ResourceReference memberReference = new ResourceReference("72f9339c-4fac-4c33-831d-931ddabd1ac5".toString(),
                "person", "PDM");
        final Group group = new Group().setId(groupId).setRealm("PDM");
        final GroupMembership membership = new GroupMembership().setId(groupMembershipId).setCreator("CRS_ROOT")
                .setCreatorApplicationId("MONITOR").setVersion(0L).setCreationInstant(1397016000000L);
        membership.setMember(memberReference);
        membership.setGroup(group);

        // Delete the membership.
        client.delete(groupId, groupMembershipId, httpContext).checkedGet();
    }

    /** Test for missing base service url. */
    @Test(expectedExceptions = { IllegalStateException.class })
    public final void testMissingBaseServiceUrl() {
        new GroupMembershipClientBuilder().setHttpClient(httpClient).setExecutorService(executor).build();
    }

    /** Test for malformed base service url. */
    @Test(expectedExceptions = { IllegalStateException.class })
    public final void testMalformedUri() {
        new GroupMembershipClientBuilder().setServiceBaseUrl("{http://localhost:8080").setHttpClient(httpClient)
                .setExecutorService(executor).build();
    }

    /** Test for reading unsupported json structure. */
    @Test(expectedExceptions = { EntityReaderException.class })
    public final void testReadingForIncorrectJsonStructure() {

        client.get("fab935ce-babe-4846-b508-5d2af0c4d1a6", "b5213cd0-fcd0-4461-a334-79d7d74d9f91", httpContext)
                .checkedGet();
    }

    /** Test for searching group memberships. */
    @Test
    public final void testSearchGroupMemberships() {

        final String groupMembershipId1 = "1a993355-7cfe-4506-8657-e33d35210f2b";
        final String groupMembershipId2 = "1a993355-7cfe-4506-8657-e33d35210f2c";
        final String groupId = "fab935ce-get-4846-b508-5d2af0c4d1a6";
        final String memberId = "72f9339c-4fac-4c33-831d-931ddabd1ac5";

        // Create expected group membership 1.
        final ResourceReference memberReference1 = new ResourceReference(memberId, "person", "PDM");
        final Group group1 = new Group().setId(groupId).setRealm("PDM");
        final GroupMembership membership1 = new GroupMembership().setId(groupMembershipId1).setCreator("CRS_ROOT1")
                .setCreatorApplicationId("MONITOR1").setVersion(0L).setCreationInstant(1397016000000L);
        membership1.setMember(memberReference1);
        membership1.setGroup(group1);

        // Create expected group membership 2.
        final ResourceReference memberReference2 = new ResourceReference(memberId, "person", "PDM");
        final Group group2 = new Group().setId(groupId).setRealm("PDM");
        final GroupMembership membership2 = new GroupMembership().setId(groupMembershipId2).setCreator("CRS_ROOT2")
                .setCreatorApplicationId("MONITOR2").setVersion(1L).setCreationInstant(1397016000000L);
        membership2.setMember(memberReference2);
        membership2.setGroup(group2);

        // Add the expected object to a map
        final Map<String, GroupMembership> expectedGroupMemeberships = new HashMap<String, GroupMembership>();
        expectedGroupMemeberships.put(groupMembershipId1, membership1);
        expectedGroupMemeberships.put(groupMembershipId2, membership2);

        // Construct criteria
        final Multimap<String, String> searchCriteria = HashMultimap.create();
        searchCriteria.put("member.id", "72f9339c-4fac-4c33-831d-931ddabd1ac5");
        searchCriteria.put("member.type", "person");
        searchCriteria.put("member.realmId", "PDM");

        // Search group memberships.
        final List<GroupMembership> groupMemberships = client.search(searchCriteria, SortCriteria.builder().build(),
                new Page(1, 10), httpContext).checkedGet();

        // Assert for null, size and objects
        Assert.assertNotNull(groupMemberships);
        Assert.assertEquals(expectedGroupMemeberships.size(), groupMemberships.size());
        for (final GroupMembership groupMembership : groupMemberships) {

            Assert.assertNotNull(groupMembership);
            assertGroupMemberships(expectedGroupMemeberships.get(groupMembership.getId()), groupMembership);
        }
    }

    /** Test case to get a group membership. */
    @Test
    public final void testGetGroupMembershipWithEntitlementsInResponse() {

        final String groupMembershipId = "1a993355-7cfe-4506-8657-e33d35210f2b";
        final String groupId = "fab935ce-get-4846-b508-5d2af0c4d1a6";

        // Create expected group membership.
        final ResourceReference memberReference = new ResourceReference("72f9339c-4fac-4c33-831d-931ddabd1ac5", "person",
                "PDM");
        final Group group = new Group().setId(groupId).setRealm("PDM");
        final GroupMembership membership = new GroupMembership().setId(groupMembershipId).setCreator("CRS_ROOT")
                .setCreatorApplicationId("MONITOR").setVersion(0L).setCreationInstant(1397016000000L);
        membership.setMember(memberReference);
        membership.setGroup(group);

        // Get the group membership by id.
        // The group membership object returned does not contain entitlements, as the reader does not have the logic to
        // read entitlements.
        final GroupMembership retrievedMembership = client.get(groupId, groupMembershipId, true, true, httpContext)
                .checkedGet();

        // Assert for returned object.
        assertGroupMemberships(membership, retrievedMembership);

    }

    /** Test for searching group memberships with entitlements in response. */
    @Test
    public final void testSearchGroupMembershipsWithGroupAndWithoutEntitlementsInResponse() {

        final String groupMembershipId1 = "1a993355-7cfe-4506-8657-e33d35210f2b";
        final String groupMembershipId2 = "1a993355-7cfe-4506-8657-e33d35210f2c";
        final String groupId = "fab935ce-get-4846-b508-5d2af0c4d1a6";
        final String memberId = "72f9339c-4fac-4c33-831d-931ddabd1ac5";

        // Create expected group membership 1.
        final ResourceReference memberReference1 = new ResourceReference(memberId, "person", "PDM");
        final Group group1 = new Group().setId(groupId).setRealm("PDM");
        final GroupMembership membership1 = new GroupMembership().setId(groupMembershipId1).setCreator("CRS_ROOT1")
                .setCreatorApplicationId("MONITOR1").setVersion(0L).setCreationInstant(1397016000000L);
        membership1.setMember(memberReference1);
        membership1.setGroup(group1);

        // Create expected group membership 2.
        final ResourceReference memberReference2 = new ResourceReference(memberId, "person", "PDM");
        final Group group2 = new Group().setId(groupId).setRealm("PDM");
        final GroupMembership membership2 = new GroupMembership().setId(groupMembershipId2).setCreator("CRS_ROOT2")
                .setCreatorApplicationId("MONITOR2").setVersion(1L).setCreationInstant(1397016000000L);
        membership2.setMember(memberReference2);
        membership2.setGroup(group2);

        // Add the expected object to a map
        final Map<String, GroupMembership> expectedGroupMemeberships = new HashMap<String, GroupMembership>();
        expectedGroupMemeberships.put(groupMembershipId1, membership1);
        expectedGroupMemeberships.put(groupMembershipId2, membership2);

        // Construct criteria
        final Multimap<String, String> searchCriteria = HashMultimap.create();
        searchCriteria.put("member.id", "72f9339c-4fac-4c33-831d-931ddabd1ac5");
        searchCriteria.put("member.type", "person");
        searchCriteria.put("member.realmId", "PDM");

        // Search group memberships.
        // The group membership object returned does not contain group details, as the reader does not have the logic to
        // read group.
        final List<GroupMembership> groupMemberships = client.search(searchCriteria, SortCriteria.builder().build(),
                new Page(1, 10), true, false, httpContext).checkedGet();

        // Assert for null, size and objects
        Assert.assertNotNull(groupMemberships);
        Assert.assertEquals(expectedGroupMemeberships.size(), groupMemberships.size());
        for (final GroupMembership groupMembership : groupMemberships) {

            Assert.assertNotNull(groupMembership);
            assertGroupMemberships(expectedGroupMemeberships.get(groupMembership.getId()), groupMembership);
        }
    }

    /** Test case for adding group membership with unsupported client method. */
    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public final void testAdd() {
        client.add(new GroupMembership(), httpContext);
    }

    /** Test case for deleting group membership with unsupported client method. */
    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public final void testDelete() {
        client.delete("group_membership_1", httpContext);
    }

    /** Test case for getting group membership with unsupported client method. */
    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public final void testGetByResource() {
        client.get(new GroupMembership(), httpContext);
    }

    /** Test case for getting group membership by id with unsupported client method. */
    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public final void testGetbyId() {
        client.get("group_membership_1", httpContext);
    }

    /** Test case for persisting group memberships with unsupported client method. */
    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public final void testPersist() {
        client.persist(new GroupMembership(), httpContext);
    }

    /** Test case for searching group memberships with unsupported client method. */
    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public final void testSearchWithMediatypeEndpoint() {
        final Multimap<String, String> searchCriteria = HashMultimap.create();
        client.search(searchCriteria, SortCriteria.builder().build(), new Page(1, 10), httpContext,
                MediaType.parse(GROUP_MEMBERSHIP_MEDIA_TYPE), "/test");
    }

    /** Test to list memberships with group id. */
    @Test
    public final void testListGroupMemberships() {
        final List<GroupMembership> groupMemberships = client.listGroupMemberships("h4560543-del-425a-9021-30f4xedggfjk1",
                false, false, httpContext).checkedGet();

        final GroupMembership expectedGroupMembership = new GroupMembership();
        expectedGroupMembership.setId("5thb7f9d-get-45d9-b55c-02ee3e42886t").setVersion(0L).setCreator("CRS_ROOT1")
                .setCreationInstant(1397016000000L).setCreatorApplicationId("MONITOR1");
        expectedGroupMembership
                .setMember(new ResourceReference("41035696-fd5b-4ce9-a452-c04670827c4c", "person", "ABC"));
        expectedGroupMembership.setGroup(new Group().setId("h4560543-del-425a-9021-30f4xedggfjk1").setVersion(0L)
                .setCreator("CRS_ROOT8").setCreationInstant(1397016000000L).setCreatorApplicationId("MONITOR7")
                .setRealm("ABC").setOwner(new ResourceReference("CRS_ROOT2", "person1", "ABC")));

        Assert.assertEquals(Iterables.size(groupMemberships), 1);

        final Iterator<GroupMembership> iterator = groupMemberships.iterator();
        while (iterator.hasNext()) {
            assertGroupMemberships(expectedGroupMembership, iterator.next());
        }
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
