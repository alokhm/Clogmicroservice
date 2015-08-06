/**
 * 
 */
package com.covisint.platform.clog.server.wrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.covisint.core.http.service.client.InvalidResponseException;
import com.covisint.core.http.service.core.InvocationContext;
import com.covisint.core.http.service.core.ServiceException;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.platform.clog.core.cloginstance.ClogInstance;
import com.covisint.platform.clog.server.cloginstance.impl.ClogInstanceJsonHelper;

/**
 * @author Lingesh.M
 *
 *
 */
public class ElasticSearchServiceWrapper  {

	/** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ElasticSearchServiceWrapper.class);
	
    private String elasticSearchUrl;
    private String indexName;
    
    /**
     * 
     * @param newGroupClient
     * @param newGroupEntitlementClient
     */
    public ElasticSearchServiceWrapper(@Nonnull String elasticSearchUrl,
    		@Nonnull String indexName){
    	this.elasticSearchUrl = elasticSearchUrl;
    	this.indexName = indexName;
		
	}

    /**
     * 
     * @param clogInstance
     * @return
     * @throws ServiceException
     */
	public boolean createAlias(ClogInstance clogInstance) throws ServiceException {

		HttpResponse response = null;
		/** create an alias in ElasticSearch for platforminstanceId */
		try {
			response = createAliasInElasticSearch(clogInstance);
			validateElasticSearchResponse(response);
		} catch (Exception e) {
			log.error("Error occurred while creating Alias in elastic search",
					e.getMessage());
			return false;
		}

		return true;
	}	
	
	/**
	 * 
	 * @param clogInstance
	 * @throws ServiceException
	 */
	public void deleteAlias(ClogInstance clogInstance) throws ServiceException {

		/** create an alias in ElasticSearch for platforminstanceId */
		try {
			 deleteAliasInElasticSearch(clogInstance);
		} catch (Exception e) {
			log.error("Error occurred while deleting Alias in elastic search",
					e.getMessage());
		}

		
	}	
	/**
	 * 
	 * @param clogInstance
	 * @return
	 * @throws Exception
	 */
	private HttpResponse deleteAliasInElasticSearch(ClogInstance clogInstance) throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		return client.execute(createHttpDelete(clogInstance));
	}

	private HttpUriRequest createHttpDelete(ClogInstance clogInstance) {
		HttpDelete delete=new HttpDelete(elasticSearchUrl+"/"+indexName+"/_alias/"+clogInstance.getPlatformInstanceId());
		log.info(delete.getURI().toString());
		return delete;
	}

	
	
	private HttpResponse createAliasInElasticSearch(ClogInstance clogInstance) throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		return client.execute(createHttpPost(clogInstance));
	}
	
	private boolean validateElasticSearchResponse(HttpResponse response) {
		if (null != response.getStatusLine() && response.getStatusLine().getStatusCode() != 200) {
			log.debug("Elastic Search Alias creation got failed with StatusCode :"+ response
					.getStatusLine().getStatusCode());
			return false;
		}
		return true;
	}

	private HttpUriRequest createHttpPost(ClogInstance clogInstance)
			throws JsonGenerationException, JsonMappingException,
			UnsupportedEncodingException, IOException {
		HttpPost post = new HttpPost(elasticSearchUrl);
		StringEntity params = new StringEntity(generatePayload(clogInstance));
		post.setEntity(params);
		return post;
	}

	private String generatePayload(ClogInstance clogInstance)
			throws JsonGenerationException, JsonMappingException, IOException {

		return new ClogInstanceJsonHelper().createJsonForClogInstance(
				clogInstance.getPlatformInstanceId(), indexName,
				clogInstance.getPlatformInstanceId(),
				InvocationContext.getRealmId());

	}

}
