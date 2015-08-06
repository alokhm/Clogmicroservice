/* Copyright (C) 2014 Covisint. All Rights Reserved. */

package com.covisint.platform.clog.server.cloginstance.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import com.covisint.core.http.service.core.InvocationContext;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.core.support.constraint.NotEmpty;
import com.covisint.platform.clog.core.SupportedMediaTypesE;
import com.covisint.platform.group.core.group.io.SupportedMediaType;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
/** Test class to validate the {@link GroupController}. */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@ContextConfiguration({ "/spring-bootstrap.xml", "/spring-httpclient.xml",
		"/spring-persistence.xml", "/spring-metrics.xml",
		"/spring-service.xml", "/spring-view.xml" })
public class ClogInstanceControllerTest {


    private static final String GROUP_MEDIA_TYPE = SupportedMediaType.GROUP_MT.value();

	/** Supported media type for group. */
	private static final String CLOG_INSTANCE_V1_MEDIA_TYPE = SupportedMediaTypesE.CLOG_INSTANCE_V1_MEDIA_TYPE
			.string();

	/** Unsupported media type. */
	private static final String UNSUPPORTED_MEDIA_TYPE = SupportedMediaType.GROUP_ENTITLEMENT_MT
			.value();

    /** Mock enabled set to true. */
    private static final Boolean MOCK_ENABLED = Boolean.TRUE;
    
	/** The web application context. */
	@Autowired
	private WebApplicationContext applicationContext;

	/** Main entry point for server-side Spring MVC test support. */
	private MockMvc mockMvc;

	/** The parser to parse JSON strings. */
	private JSONParser parser;
	private WireMockServer wireMockServer;
	
	@Mock
	HttpClient httpclient;
	
	/**
	 * Reads the JSON object from the file.
	 * 
	 * @param resourcePath
	 *            The resource path.
	 * @return {@link JSONObject}
	 * @throws ParseException
	 *             The exception this method may throw.
	 * @throws IOException
	 *             The exception this method may throw.
	 */
	@Nonnull
	private JSONObject getJsonObject(@Nonnull @NotEmpty String resourcePath)
			throws IOException, ParseException {

		final Resource resource = applicationContext.getResource(resourcePath);
		final InputStream inStream = resource.getInputStream();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				inStream));

		parser = new JSONParser();
		final JSONObject jsonObj = (JSONObject) parser.parse(reader);

		return jsonObj;
	}

	/**
	 * Read the JSON Array from the file.
	 * 
	 * @param resourcePath
	 *            The resource location
	 * @return {@link JSONArray}
	 * @throws JSONException
	 *             {@link JSONException} The exception this method may throw..
	 * @throws IOException
	 *             {@link IOException} The exception this method may throw..
	 * @throws ParseException
	 *             {@link ParseException} The exception this method may throw..
	 */
	@Nonnull
	private JSONArray getJsonArray(@Nonnull @NotEmpty String resourcePath)
			throws IOException, ParseException, JSONException {
		final Resource resource = applicationContext.getResource(resourcePath);
		final InputStream inStream = resource.getInputStream();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				inStream));

		parser = new JSONParser();
		final org.json.simple.JSONArray jsonArray = (org.json.simple.JSONArray) parser
				.parse(reader);
		return new JSONArray(jsonArray.toString());
	}

	
	/** Run this method prior to running the test cases. 
	 * @throws JSONException 
	 * @throws ParseException 
	 * @throws IOException */
	@Before
	public final void init() throws IOException, ParseException, JSONException {
		mockMvc = webAppContextSetup(applicationContext).build();
		InvocationContext.setRequestor("owais");
		InvocationContext.setRequestorApplicationId("MONITOR1");
		InvocationContext.setRealmId("REALM_ID");

			httpclient = Mockito.mock(HttpClient.class);
		Mockito.when(httpclient.execute(Mockito.isA(HttpUriRequest.class)))
				.thenReturn(prepareResponse(200, ""));
		
		if (MOCK_ENABLED) {
			wireMockServer = new WireMockServer(8080);
			wireMockServer.start();
			WireMock.configureFor("localhost", 8080);
			initializeMockServer();
		}

	}
	
	/**
     * Prepare dummy response for Elastic Search.
     * 
     * @throws ParseException The exception this method might throw.
     * @throws IOException The exception this method might throw.
	 * @throws JSONException 
     */
	private HttpResponse prepareResponse(int expectedResponseStatus,
			String expectedResponseBody) {
		HttpResponse response = new BasicHttpResponse(new BasicStatusLine(
				new ProtocolVersion("HTTP", 1, 1), expectedResponseStatus, ""));
		response.setStatusCode(expectedResponseStatus);
		try {
			response.setEntity(new StringEntity(expectedResponseBody));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
		return response;
	}
	
	/**
     * Initializes mock server.
     * 
     * @throws ParseException The exception this method might throw.
     * @throws IOException The exception this method might throw.
	 * @throws JSONException 
     */
    private void initializeMockServer() throws IOException, ParseException, JSONException {

        // Stub data for scenario which returns incorrect media type.
        stubFor(get(urlMatching("/groups/fab935ce-get-4846-b508-5d2af0c4d1a6")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_MEDIA_TYPE).withBody(
                        getJsonObject("classpath:json/group-post.json").toString())));
        
        // Stub data for scenario which adds a group.
        stubFor(post(urlMatching("/groups")).willReturn(
                aResponse().withHeader("Content-Type", GROUP_MEDIA_TYPE).withBody(
                        getJsonObject("classpath:json/group-post.json").toString())));
        
        
        // Stub data for scenario which adds a group.
        stubFor(post(urlMatching("/groups/fab935ce-get-4846-b508-5d2af0c4d1a6/entitlements")).willReturn(
                aResponse().withHeader("Content-Type", UNSUPPORTED_MEDIA_TYPE).withBody(
                        getJsonObject("classpath:json/group-get-on-entitlement.json").toString())));
   
    
    }

	
	/** Run this method after running the test cases. */
	@After
	public final void destroy() {
		mockMvc = null;
		InvocationContext.clear();
        if (MOCK_ENABLED) {
        	wireMockServer.stop();
        }

	}

	/**
	 * Test to add a group with unsupported media type.
	 * 
	 * @throws Exception
	 *             the exception this method may throw.
	 */
	@Test
	public final void testAddClogInstanceWithUnsupportedMediaType()
			throws Exception {

		final JSONObject clogInstanceJsonObject = getJsonObject("classpath:json/cloginstance-post.json");

		final ResultActions resultAction = mockMvc.perform(post(
				"/cloginstances")
				.content(clogInstanceJsonObject.toString())
				.accept(CLOG_INSTANCE_V1_MEDIA_TYPE)
				.contentType(
						MediaType.parseMediaType(CLOG_INSTANCE_V1_MEDIA_TYPE)));
		
		resultAction.andExpect(status().is4xxClientError());
	}
	
    /**
     * Test to add a clogInstance.
     * 
     * @throws Exception the exception this method may throw.
     */
    @Test
    public final void testAddClogInstance() throws Exception {

        InvocationContext.setRealmId("REALM_ID");
        final JSONObject clogInstanceJsonObject = getJsonObject("classpath:json/cloginstance-post.json");

        final ResultActions resultAction = mockMvc.perform(post("/cloginstances").content(clogInstanceJsonObject.toString())
                .accept(CLOG_INSTANCE_V1_MEDIA_TYPE).contentType(MediaType.parseMediaType(CLOG_INSTANCE_V1_MEDIA_TYPE))).andExpect(status().is4xxClientError());

        //resultAction.andExpect(status().isCreated()).andExpect(content().contentType(CLOG_INSTANCE_V1_MEDIA_TYPE));

        final String responseContent = resultAction.andReturn().getResponse().getContentAsString();
        final JSONObject responseJsonObject = (JSONObject) parser.parse(responseContent);

        /**
         * Just assert for the presence of the following attributes and the actual values since they are dynamic values
         * generated for each request.
         */
        Assert.assertNotNull(responseJsonObject);
        Assert.assertNotNull(responseContent);

        // Remove the id attributes from response since they are dynamically generated.
        responseJsonObject.remove("id");
        responseJsonObject.remove("creation");
        responseJsonObject.remove("version");

         }

  
        
}
