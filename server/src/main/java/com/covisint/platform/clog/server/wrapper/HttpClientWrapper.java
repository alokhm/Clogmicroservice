/**
 * 
 */
package com.covisint.platform.clog.server.wrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.covisint.core.http.service.core.InvocationContext;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.platform.clog.core.cloginstance.ClogInstance;
import com.covisint.platform.clog.server.cloginstance.impl.ClogInstanceJsonHelper;

/**
 * @author Lingesh.M
 *
 *
 */
public class HttpClientWrapper  {

	/** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HttpClientWrapper.class);
	
    private HttpClient httpClient;
    
    /**
     * 
     * @param newGroupClient
     * @param newGroupEntitlementClient
     */
    public HttpClientWrapper(@Nonnull HttpClient httpClient){
    	this.httpClient = httpClient;
		
	}
    
    /**
     * 
     * @param clogInstance
     * @param elasticSearchUrl
     * @param indexName
     * @return
     * @throws Exception
     */
    public HttpResponse post(ClogInstance clogInstance ,String elasticSearchUrl, String indexName ) throws Exception {
    	    	return httpClient.execute(createHttpPost(clogInstance, elasticSearchUrl, indexName));
        	
    }
    
    
    /**
     * 
     * @param clogInstance
     * @param elasticSearchUrl
     * @param indexName
     * @return
     * @throws Exception
     */
    public HttpResponse delete(String elasticSearchUrl) throws Exception {
    	    	return httpClient.execute(createHttpDelete(elasticSearchUrl));
        	
    }
    
    /**
     * 
     * @param elasticSearchUrl
     * @return
     */
    private HttpUriRequest createHttpDelete(String elasticSearchUrl) {
    		HttpDelete delete=new HttpDelete(elasticSearchUrl);
    		log.info(delete.getURI().toString());
    		return delete;
    	}
	

	/**
     * 
     * @param clogInstance
     * @param elasticSearchUrl
     * @param indexName
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
	private HttpUriRequest createHttpPost(ClogInstance clogInstance, String elasticSearchUrl, String indexName)
			throws JsonGenerationException, JsonMappingException,
			UnsupportedEncodingException, IOException {
		HttpPost post = new HttpPost(elasticSearchUrl);
		StringEntity params = new StringEntity(generatePayload(clogInstance , indexName));
		post.setEntity(params);
		return post;
	}

	/**
	 * 
	 * @param clogInstance
	 * @param indexName
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private String generatePayload(ClogInstance clogInstance, String indexName)
			throws JsonGenerationException, JsonMappingException, IOException {

		return new ClogInstanceJsonHelper().createJsonForClogInstance(
				clogInstance.getPlatformInstanceId(), indexName,
				clogInstance.getPlatformInstanceId(),
				InvocationContext.getRealmId());

	}

    
}
