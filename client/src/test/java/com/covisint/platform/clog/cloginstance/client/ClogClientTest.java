package com.covisint.platform.clog.cloginstance.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.json.JsonObject;

import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import com.covisint.core.support.constraint.Nonnull;
import com.covisint.core.support.constraint.NotEmpty;
import com.covisint.platform.clog.core.SupportedMediaTypesE;
import com.fasterxml.jackson.core.JsonParser;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.util.concurrent.ListeningExecutorService;

public class ClogClientTest {
	
	

    /** Supported media type for group. */
    private static final String CLOG_MEDIA_TYPE = SupportedMediaTypesE.CLOG_INSTANCE_V1_MEDIA_TYPE.string();

      /** Mock enabled set to true. */
    private static final Boolean MOCK_ENABLED = Boolean.TRUE;

    /** The wire mock server. */
    private static WireMockServer wireMockServer;

    /** The group client. */
    private static ClogClient client;

    /** The http context. */
    private static HttpContext httpContext;
    
    /** The http client. */
    private static HttpClient httpClient;

    /** The executor service. */
    private static ListeningExecutorService executor;
    
    /** The parser to parse JSON strings. */
    private JsonParser parser;
    
    /**
     * Reads the JSON object from the file.
     * 
     * @param resourcePath The resource path.
     * @return {@link JSONObject}
     * @throws ParseException The exception this method may throw.
     * @throws IOException The exception this method may throw.
     */
   /* @Nonnull
    private JsonObject getJsonObject(@Nonnull @NotEmpty String resourcePath) throws IOException, ParseException {

        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        final JsonObject jsonObject = (JsonObject) parser

        return jsonObject;
    }
*/
    
    
   
    }


