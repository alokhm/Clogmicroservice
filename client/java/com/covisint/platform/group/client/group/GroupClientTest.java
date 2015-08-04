/* Copyright (C) 2014 Covisint. All Rights Reserved. */

package com.covisint.platform.group.client.group;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import junit.framework.Assert;

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
import com.covisint.platform.group.core.group.io.SupportedMediaType;
import com.covisint.platform.group.core.group.io.json.GroupReader;
import com.covisint.platform.group.core.group.io.json.GroupWriter;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/** Test class to test {@link GroupClient}. */
public class GroupClientTest {

    /** Supported media type for group. */
    private static final String GROUP_MEDIA_TYPE = SupportedMediaType.GROUP_MT.value();

    /** Unsupported media type. */
    private static final String UNSUPPORTED_MEDIA_TYPE = SupportedMediaType.GROUP_ENTITLEMENT_MT.value();

    /** Mock enabled set to true. */
    private static final Boolean MOCK_ENABLED = Boolean.TRUE;

    /** The wire mock server. */
    private static WireMockServer wireMockServer;

    /** The group client. */
    private static GroupClient client;

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
    private JSONObject getJsonObject(@Nonnull @NotEmpty String resourcePath) throws IOException, ParseException {

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
     * Asserts group object wiith null.
     * 
     * @param responseGroup - the actual group returned
     */
    private void assertNull(Group responseGroup) {
        Assert.assertNotNull(responseGroup);
        Assert.assertNull(responseGroup.getId());
        Assert.assertNull(responseGroup.getCreator());
        Assert.assertNull(responseGroup.getCreatorApplicationId());
        Assert.assertNull(responseGroup.getVersion());
        Assert.assertNull(responseGroup.getRealm());
        Assert.assertNotNull(responseGroup.getName());
        Assert.assertTrue(responseGroup.getName().isEmpty());
        Assert.assertNotNull(responseGroup.getDescription());
        Assert.assertTrue(responseGroup.getDescription().isEmpty());
        Assert.assertNotNull(responseGroup.getEntitlements());
        Assert.assertNull(responseGroup.getOwner());
        Assert.assertNotNull(responseGroup.getMemberships());
        Assert.assertTrue(responseGroup.getMemberships().isEmpty());
        Assert.assertTrue(responseGroup.getEntitlements().isEmpty());
    }

    /**
     * Assert group objects with names and descriptions.
     * 
     * @param expectedGroup the actual group object.
     * @param actualGroup The expected group object.
     */
    private void assertEqualsFullObject(@Nonnull Group expectedGroup, @Nonnull Group actualGroup) {
        Assert.assertNotNull(expectedGroup);
        Assert.assertEquals(expectedGroup.getId(), actualGroup.getId());
        Assert.assertEquals(expectedGroup.getCreator(), actualGroup.getCreator());
        Assert.assertEquals(expectedGroup.getCreatorApplicationId(), actualGroup.getCreatorApplicationId());
        Assert.assertEquals(expectedGroup.getRealm(), actualGroup.getRealm());
        Assert.assertEquals(expectedGroup.getVersion(), actualGroup.getVersion());
        Assert.assertEquals(expectedGroup.getName().toString(), actualGroup.getName().toString());
        Assert.assertEquals(expectedGroup.getDescription().toString(), actualGroup.getDescription().toString());
        Assert.assertEquals(expectedGroup.getOwner().toString(), actualGroup.getOwner().toString());
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
        client = new GroupClientBuilder().addEntityWriter(new GroupWriter()).addEntityReader(new GroupReader())
                .setExecutorService(executor).setHttpClient(httpClient).setServiceBaseUrl("http://localhost:" + port)
                .build();

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
        stubFor(get(urlMatching("/groups/4973cf34-ca70-44ea-82b5-12556cd7b05f")).willReturn(
                aResponse().withHeader("Content-Type", UNSUPPORTED_MEDIA_TYPE).withBody(
                        getJsonObject("json/group-get-or-post.json").toString())));

        // Stub data for scenario which returns a group without entitlements.
        stubFor(get(urlMatching("/groups/fab935ce-babe-4846-b508-5d2af0c4d1a6")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_MEDIA_TYPE).withBody(
                        getJsonObject("json/group-get-or-post.json").toString())));

        // Stub data for scenario which returns a group with empty body.
        stubFor(get(urlMatching("/groups/empty_body")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_MEDIA_TYPE).withBody(
                        getJsonObject("json/empty_body.json").toString())));
        // Stub data for scenario which returns a group with empty values.
        stubFor(get(urlMatching("/groups/empty_values")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_MEDIA_TYPE).withBody(
                        getJsonObject("json/group-get-with-empty-values.json").toString())));

        // Stub data for scenario which adds a group.
        stubFor(post(urlMatching("/groups")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_MEDIA_TYPE).withBody(
                        getJsonObject("json/group-get-or-post.json").toString())));

        // Stub data for scenario which updates a group.
        stubFor(put(urlMatching("/groups/fab935ce-babe-4846-b508-5d2af0c4d1a6")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_MEDIA_TYPE).withBody(
                        getJsonObject("json/group-update.json").toString())));

        // Stub data for scenario where json structure is not supported.
        stubFor(get(urlMatching("/groups/b5213cd0-fcd0-4461-a334-79d7d74d9f91")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_MEDIA_TYPE).withBody("[10,20]")));

        // Stub data for scenario which returns person requests for supplied parameters .
        stubFor(get(
                urlMatching("/groups\\?description=Group%20description%20in%20english&name=Group%20"
                        + "name%20in%20english&realmId=PDM&page=1&pageSize=10")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_MEDIA_TYPE).withBody(
                        getJsonArray("json/group-search.json").toString())));

    }

    /** Stops the wire mock server after running test cases. */
    @AfterClass
    public final void destroy() {
        if (MOCK_ENABLED) {
            wireMockServer.stop();
        }
        executor.shutdown();
    }

    /** Test to get a group with unsupported media type. */
    @Test(expectedExceptions = { UnsupportedResponseContentTypeException.class })
    public final void testGetGroupWithUnsupportedMediaType() {

        client.get("4973cf34-ca70-44ea-82b5-12556cd7b05f", httpContext).checkedGet();
    }

    /** Test case to get a group. */
    @Test
    public final void testGetGroup() {

        final String groupId = "fab935ce-babe-4846-b508-5d2af0c4d1a6";

        // Group name map.
        final Map<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("en", "Group name in english");
        nameMap.put("fr", "Group name in french");

        // Group description map.
        final Map<String, String> descriptionMap = new HashMap<String, String>();
        descriptionMap.put("en", "Group description in english");
        descriptionMap.put("fr", "Group description in french");

        // Group owner
        final ResourceReference ownerReference = new ResourceReference("CRS_ROOT1", "person", "PDM");

        // Create a group with expected details.
        final Group expectedGroup = new Group().setId(groupId).setCreator("CRS_ROOT")
                .setCreatorApplicationId("MONITOR").setRealm("PDM").setVersion(0L).setCreationInstant(1397016000000L);
        expectedGroup.setName(nameMap);
        expectedGroup.setDescription(descriptionMap);
        expectedGroup.setOwner(ownerReference);

        // Get the group by id.
        final Group retrievedGroup = client.get(groupId, httpContext).checkedGet();

        // Assert for returned objec.
        assertEqualsFullObject(expectedGroup, retrievedGroup);
    }

    /** Test case to get a group with empty body. */
    @Test
    public final void testGetGroupWithEmptyBody() {
        // Assert for returned object.
        assertNull(client.get("empty_body", httpContext).checkedGet());
    }

    /** Test case to get a group with empty values. */
    @Test
    public final void testGetGroupWithEmptyValues() {
        // Assert for returned object.
        assertNull(client.get("empty_values", httpContext).checkedGet());
    }

    /** Test to add a group. */
    @Test
    public final void testAddGroup() {

        // Group name map.
        final Map<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("en", "Group name in english");
        nameMap.put("fr", "Group name in french");

        // Group description map.
        final Map<String, String> descriptionMap = new HashMap<String, String>();
        descriptionMap.put("en", "Group description in english");
        descriptionMap.put("fr", "Group description in french");

        // Group owner
        final ResourceReference ownerReference = new ResourceReference("CRS_ROOT1", "person", "PDM");

        // Create a group with expected details.
        final Group group = new Group().setId("fab935ce-babe-4846-b508-5d2af0c4d1a6").setCreator("CRS_ROOT")
                .setCreatorApplicationId("MONITOR").setRealm("PDM").setVersion(0L).setCreationInstant(1397016000000L);
        group.setName(nameMap);
        group.setDescription(descriptionMap);
        group.setOwner(ownerReference);

        // Save the group.
        final Group addedGroup = client.add(group, httpContext).checkedGet();

        // Assert for returned object.
        assertEqualsFullObject(group, addedGroup);
    }

    /** Test to update a group. */
    @Test
    public final void testUpdateGroup() {

        // Get the group.
        final Group group = client.get("fab935ce-babe-4846-b508-5d2af0c4d1a6", httpContext).checkedGet();

        // Update group details.
        // Group name map.
        final Map<String, String> updatedNameMap = new HashMap<String, String>();
        updatedNameMap.put("en", "Group name in english updated");
        updatedNameMap.put("fr", "Group name in french");

        // Group description map.
        final Map<String, String> updatedDescriptionMap = new HashMap<String, String>();
        updatedDescriptionMap.put("en", "Group description in english");
        updatedDescriptionMap.put("fr", "Group description in french updated");

        // Update group details.
        group.setRealm("PEM");
        group.setVersion(1L);
        group.setName(updatedNameMap);
        group.setDescription(updatedDescriptionMap);

        final Group updatedGroupDetails = client.persist(group, httpContext).checkedGet();

        // Assert for returned object.
        assertEqualsFullObject(group, updatedGroupDetails);
    }

    /** Test for missing base service url. */
    @Test(expectedExceptions = { IllegalStateException.class })
    public final void testMissingBaseServiceUrl() {
        new GroupClientBuilder().setHttpClient(httpClient).setExecutorService(executor).build();
    }

    /** Test for malformed base service url. */
    @Test(expectedExceptions = { IllegalStateException.class })
    public final void testMalformedUri() {
        new GroupClientBuilder().setServiceBaseUrl("{http://localhost:8080").setHttpClient(httpClient)
                .setExecutorService(executor).build();
    }

    /** Test for reading unsupported json structure. */
    @Test(expectedExceptions = { EntityReaderException.class })
    public final void testReadingForIncorrectJsonStructure() {

        client.get("b5213cd0-fcd0-4461-a334-79d7d74d9f91", httpContext).checkedGet();
    }

    /** Test for searching groups. */
    @Test
    public final void testSearchGroup() {

        final String groupId1 = "fab935ce-babe-4846-b508-5d2af0c4d1a6";
        final String groupId2 = "fab935ce-babe-4846-b508-5d2af0c4d1a7";

        // Create expected group object 1
        // Group name map.
        final Map<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("en", "Group name in english");
        nameMap.put("fr", "Group name in french");

        // Group description map.
        final Map<String, String> descriptionMap = new HashMap<String, String>();
        descriptionMap.put("en", "Group description in english");
        descriptionMap.put("fr", "Group description in french");

        // Group owner
        final ResourceReference ownerReference1 = new ResourceReference("CRS_ROOT1", "person", "PDM");

        // Create a group with expected details.
        final Group group1 = new Group().setId(groupId1).setCreator("CRS_ROOT1").setCreatorApplicationId("MONITOR1")
                .setRealm("PDM").setVersion(0L).setCreationInstant(1397016000000L);
        group1.setName(nameMap);
        group1.setDescription(descriptionMap);
        group1.setOwner(ownerReference1);

        // Create expected group object 2
        // Group owner
        final ResourceReference ownerReference2 = new ResourceReference("CRS_ROOT2", "person", "PDM");

        // Create a group with expected details.
        final Group group2 = new Group().setId(groupId2).setCreator("CRS_ROOT2").setCreatorApplicationId("MONITOR2")
                .setRealm("PDM").setVersion(0L).setCreationInstant(1397016000000L);
        group2.setName(nameMap);
        group2.setDescription(descriptionMap);
        group2.setOwner(ownerReference2);

        // Add the expected object to a map
        final Map<String, Group> expectedGroups = new HashMap<String, Group>();
        expectedGroups.put(groupId1, group1);
        expectedGroups.put(groupId2, group2);

        // Construct criteria
        final Multimap<String, String> searchCriteria = HashMultimap.create();
        searchCriteria.put("realmId", "PDM");
        searchCriteria.put("name", "Group name in english");
        searchCriteria.put("description", "Group description in english");

        final List<Group> groups = client.search(searchCriteria, SortCriteria.builder().build(), new Page(1, 10),
                httpContext).checkedGet();

        // Assert for null, size and objects
        Assert.assertNotNull(groups);
        Assert.assertEquals(expectedGroups.size(), groups.size());

        for (final Group group : groups) {

            Assert.assertNotNull(group);
            assertEqualsFullObject(expectedGroups.get(group.getId()), group);
        }
    }

    /** Test for searching groups. */
    @Test
    public final void testSearchGroupWithEntitlementsInResponse() {

        final String groupId1 = "fab935ce-babe-4846-b508-5d2af0c4d1a6";
        final String groupId2 = "fab935ce-babe-4846-b508-5d2af0c4d1a7";

        // Create expected group object 1
        // Group name map.
        final Map<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("en", "Group name in english");
        nameMap.put("fr", "Group name in french");

        // Group description map.
        final Map<String, String> descriptionMap = new HashMap<String, String>();
        descriptionMap.put("en", "Group description in english");
        descriptionMap.put("fr", "Group description in french");

        // Group owner
        final ResourceReference ownerReference1 = new ResourceReference("CRS_ROOT1", "person", "PDM");

        // Create a group with expected details.
        final Group group1 = new Group().setId(groupId1).setCreator("CRS_ROOT1").setCreatorApplicationId("MONITOR1")
                .setRealm("PDM").setVersion(0L).setCreationInstant(1397016000000L);
        group1.setName(nameMap);
        group1.setDescription(descriptionMap);
        group1.setOwner(ownerReference1);

        // Create expected group object 2
        // Group owner
        final ResourceReference ownerReference2 = new ResourceReference("CRS_ROOT2", "person", "PDM");

        // Create a group with expected details.
        final Group group2 = new Group().setId(groupId2).setCreator("CRS_ROOT2").setCreatorApplicationId("MONITOR2")
                .setRealm("PDM").setVersion(0L).setCreationInstant(1397016000000L);
        group2.setName(nameMap);
        group2.setDescription(descriptionMap);
        group2.setOwner(ownerReference2);

        // Add the expected object to a map
        final Map<String, Group> expectedGroups = new HashMap<String, Group>();
        expectedGroups.put(groupId1, group1);
        expectedGroups.put(groupId2, group2);

        // Construct criteria
        final Multimap<String, String> searchCriteria = HashMultimap.create();
        searchCriteria.put("realmId", "PDM");
        searchCriteria.put("name", "Group name in english");
        searchCriteria.put("description", "Group description in english");

        // The group object returned does not contain entitlements, as the reader does not have the logic to read
        // entitlements.
        final List<Group> groups = client.search(searchCriteria, SortCriteria.builder().build(), new Page(1, 10), true,
                httpContext).checkedGet();

        // Assert for null, size and objects
        Assert.assertNotNull(groups);
        Assert.assertEquals(expectedGroups.size(), groups.size());

        for (final Group group : groups) {

            Assert.assertNotNull(group);
            assertEqualsFullObject(expectedGroups.get(group.getId()), group);
        }
    }

    /** Test case to get a group with include entitlements set as false. */
    @Test
    public final void testGetGroupWithoutEntitlementsInResponse() {

        final String groupId = "fab935ce-babe-4846-b508-5d2af0c4d1a6";

        // Group name map.
        final Map<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("en", "Group name in english");
        nameMap.put("fr", "Group name in french");

        // Group description map.
        final Map<String, String> descriptionMap = new HashMap<String, String>();
        descriptionMap.put("en", "Group description in english");
        descriptionMap.put("fr", "Group description in french");

        // Group owner
        final ResourceReference ownerReference = new ResourceReference("CRS_ROOT1", "person", "PDM");

        // Create a group with expected details.
        final Group expectedGroup = new Group().setId(groupId).setCreator("CRS_ROOT")
                .setCreatorApplicationId("MONITOR").setRealm("PDM").setVersion(0L).setCreationInstant(1397016000000L);
        expectedGroup.setName(nameMap);
        expectedGroup.setDescription(descriptionMap);
        expectedGroup.setOwner(ownerReference);

        // Get the group by id.
        final Group retrievedGroup = client.get(groupId, false, httpContext).checkedGet();

        // Assert for returned objec.
        assertEqualsFullObject(expectedGroup, retrievedGroup);
    }

    /** Test for deleting group. */
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public final void testDeleteGroup() {
        client.delete("fab935ce-babe-4846-b508-5d2af0c4d1a6", httpContext);
    }

    /** Test case for {@link @Nonnull} annotations. */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public final void testNonnullAnnotation() {
        client.get(null, false, httpContext);
    }

    /** Test case for {@link NotEmpty} annotations. */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public final void testNotEmptyAnnotation() {
        client.get("", false, httpContext);
    }

}
