/**
 * 
 */
package com.covisint.platform.clog.server.wrapper;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.covisint.core.http.service.core.ServiceException;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.platform.clog.core.cloginstance.ClogInstance;

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
    private HttpClientWrapper httpClientWrapper;
    
    /**
     * 
     * @param newGroupClient
     * @param newGroupEntitlementClient
     */
    public ElasticSearchServiceWrapper(HttpClientWrapper httpClientWrapper,
    		@Nonnull String elasticSearchUrl,
    		@Nonnull String indexName ){
    	this.elasticSearchUrl = elasticSearchUrl;
    	this.indexName = indexName;
    	this.httpClientWrapper = httpClientWrapper;
		
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
			response = httpClientWrapper.post(clogInstance, elasticSearchUrl, indexName);
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
			httpClientWrapper.delete(elasticSearchUrl+"/"+indexName+"/_alias/"+clogInstance.getPlatformInstanceId());
		} catch (Exception e) {
			log.error("Error occurred while deleting Alias in elastic search",
					e.getMessage());
		}

		
	}	

	/**
	 * 
	 * @param response
	 * @return
	 */
	private boolean validateElasticSearchResponse(HttpResponse response) {
		if (null != response.getStatusLine() && response.getStatusLine().getStatusCode() != 200) {
			log.debug("Elastic Search Alias creation got failed with StatusCode :"+ response
					.getStatusLine().getStatusCode());
			return false;
		}
		return true;
	}

}
