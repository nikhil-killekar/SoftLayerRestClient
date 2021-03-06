package com.ibm.softlayer.client;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.softlayer.util.SLAPIUtil;
import com.ibm.softlayer.util.SLProperties;

/**
 * The Class AbstractSoftLayerClient.
 */
public abstract class AbstractSoftLayerClient {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(AbstractSoftLayerClient.class);

	/** The properties. */
	private final SLProperties properties = SLProperties.getInstance();
	
	/** The use auth token. */
	private boolean useAuthToken = false;
	
	/** The x auth token. */
	private String xAuthToken = null;		
	
	/** The use basic auth. */
	private boolean useBasicAuth = false;		

	/**
	 * Checks if is use basic auth.
	 *
	 * @return the useBasicAuth
	 */
	public boolean isUseBasicAuth() {
		return useBasicAuth;
	}

	/**
	 * Sets the use basic auth.
	 *
	 * @param useBasicAuth the useBasicAuth to set
	 */
	public void setUseBasicAuth(boolean useBasicAuth) {
		this.useBasicAuth = useBasicAuth;
	}

	/**
	 * Checks if is use auth token.
	 *
	 * @return the useAuthToken
	 */
	protected boolean isUseAuthToken() {
		return useAuthToken;
	}

	/**
	 * Sets the use auth token.
	 *
	 * @param useAuthToken the useAuthToken to set
	 */
	protected void setUseAuthToken(boolean useAuthToken) {
		this.useAuthToken = useAuthToken;
	}
	
	/**
	 * Gets the x auth token.
	 *
	 * @return the xAuthToken
	 */
	protected String getxAuthToken() {
		return xAuthToken;
	}

	/**
	 * Sets the x auth token.
	 *
	 * @param xAuthToken the xAuthToken to set
	 */
	protected void setxAuthToken(String xAuthToken) {
		this.xAuthToken = xAuthToken;
	}
	
	/**
	 * Execute head.
	 *
	 * @param url the url
	 * @return the client response
	 */
	public ClientResponse executeHEAD(String url)  {
		logger.info("Executing executeHEAD for following URL: " + url);
		
		RestClient client = new RestClient(getClientConfig());		
		Resource resource = client.resource(url);	
		resource.header("Content-type", "application/json");
		resource.header("Accept", "application/json");
		
		if(useAuthToken) {
			resource.header("X-Auth-Token", getxAuthToken());
		} else if(useBasicAuth) {
			resource.header("Authorization", "Basic "+ getxAuthToken());
		}							
		
		ClientResponse clientResponse = resource.head();
		String response = clientResponse.getEntity(String.class);
		logger.info("Executed executeHEAD for URL: " + url + ", clientResponse: " + clientResponse.getStatusCode() + ", response: " + response);
		return clientResponse;
	}
	
	/**
	 * Execute put.
	 *
	 * @param url the url
	 * @return the client response
	 */
	public ClientResponse executePUT(String url)  {
		return executePUT(url, null);
	}
	
	/**
	 * Execute put.
	 *
	 * @param url the url
	 * @param requestObject the request object
	 * @return the client response
	 */
	public ClientResponse executePUT(String url, String requestObject)  {
		logger.info("Executing executePUT for following URL: " + url + ", requestObject: " + requestObject);
		
		RestClient client = new RestClient(getClientConfig());		
		Resource resource = client.resource(url);	
		resource.header("Content-type", "application/json");
		resource.header("Accept", "application/json");
		
		//adding the authentication header
		if(useAuthToken) {
			resource.header("X-Auth-Token", getxAuthToken());
		} else if(useBasicAuth) {
			resource.header("Authorization", "Basic "+ getxAuthToken());
		}
		
		ClientResponse clientResponse = resource.put(requestObject);
		String response = clientResponse.getEntity(String.class);		
		logger.info("Executed executePUT for URL: " + url + ", clientResponse: " + clientResponse.getStatusCode() + ", response: " + response);		
		return clientResponse;
	}
	
	
	/**
	 * Execute get.
	 *
	 * @param url the url
	 * @return the client response
	 */
	public ClientResponse executeGET(String url) throws Exception {		
		return executeGET(url, null, null, null, null, null);
	}
	
	public ClientResponse executeGET(String url, List<String> objectMasks) throws Exception {		
		return executeGET(url, null, null, null, objectMasks, null);
	}
	
	/**
	 * Execute get.
	 *
	 * @param url the url
	 * @param apiClient the api client
	 * @param filterKey the filter key
	 * @param filterValue the filter value
	 * @param objectMasks the object masks
	 * @return the client response
	 */
	public ClientResponse executeGET(String url, String apiClient, String filterKey, String filterValue, List<String> objectMasks) throws Exception {				
		return executeGET(url, apiClient, filterKey, filterValue, objectMasks, null);
	}
	
	
	/**
	 * Execute get.
	 *
	 * @param url the url
	 * @param requestParamsMap the request params map
	 * @return the client response
	 */
	public ClientResponse executeGET(String url, Map<String, String> requestParamsMap) throws Exception {		
		return executeGET(url, null, null, null, null, requestParamsMap);
	}
	
	private ClientResponse executeGET(String url, String apiClient, String filterKey, String filterValue
			, List<String> objectMasks, Map<String, String> requestParamsMap) throws Exception {
		logger.info("Executing executeGET for following URL: " + url + ", apiClient: " + apiClient + ", filterKey: " + filterKey 
				+ ", filterValue: " + filterValue + ", objectMasks: " + objectMasks + ", requestParamsMap: " + requestParamsMap);				
		
		StringBuffer requestParams = new StringBuffer();
		
		//append the object Masks if exists
		String objectMaskParams = SLAPIUtil.processObjectMasks(objectMasks);
		if(objectMaskParams != null && objectMaskParams.trim().length() > 0){
			if(requestParams.toString().trim().length() == 0){
				requestParams.append("?");
			} else {
				requestParams.append("&");
			}
			requestParams.append("objectMask=").append(objectMaskParams.toString());
		}
		
		//append the object Filter if exists		
		String objectFilter = SLAPIUtil.processObjectFilters(apiClient, filterKey, filterValue);
		if(objectFilter != null && objectFilter.trim().length() > 0){
			if(requestParams.toString().trim().length() == 0){
				requestParams.append("?");
			} else {
				requestParams.append("&");
			}
			requestParams.append("objectFilter=").append(URLEncoder.encode(objectFilter.toString(), "UTF-8"));
		}
		
		//append the request Parameters if exists		
		String requestParameters = SLAPIUtil.processRequestParameters(requestParamsMap);
		if(requestParameters != null && requestParameters.trim().length() > 0){
			if(requestParams.toString().trim().length() == 0){
				requestParams.append("?");
			} else {
				requestParams.append("&");
			}
			requestParams.append(requestParameters);
		}
		
		//append the request params to the URL
		if(requestParams.toString().trim().length() > 0) {
			url += requestParams.toString();
		}
		
		logger.info("Executing GET for following URL: " + url);
		RestClient client = new RestClient(getClientConfig());		
		Resource resource = client.resource(url);	
		
		if(useAuthToken) {
			resource.header("X-Auth-Token", getxAuthToken());
		} else if(useBasicAuth) {
			resource.header("Authorization", "Basic "+ getxAuthToken());
		}
		
		resource.header("Accept", "application/json");
		ClientResponse clientResponse = resource.get();	
		String response = clientResponse.getEntity(String.class);		
		logger.info("Executed GET for following URL: " + url + ", clientResponse: " + clientResponse.getStatusCode() + ", response: " + response);
		return clientResponse;
	}
	
	/**
	 * Execute delete.
	 *
	 * @param url the url
	 * @return the client response
	 */
	public ClientResponse executeDELETE(String url)  {
		logger.info("Executing executeDELETE for following URL: " + url);
		
		RestClient client = new RestClient(getClientConfig());		
		Resource resource = client.resource(url);	
		
		if(useAuthToken) {
			resource.header("X-Auth-Token", getxAuthToken());
		} else if(useBasicAuth) {
			resource.header("Authorization", "Basic "+ getxAuthToken());
		}
		
		ClientResponse clientResponse = resource.delete();
		String response = clientResponse.getEntity(String.class);		
		logger.info("Executed executeDELETE for following URL: " + url + ", clientResponse: " + clientResponse.getStatusCode() + ", response: " + response);
		return clientResponse;
	}
	
	/**
	 * Execute post.
	 *
	 * @param url the url
	 * @param requestObject the request object
	 * @return the client response
	 */
	public ClientResponse executePOST(String url, String requestObject)  {
		return executePOST(url, requestObject, null);
	}
	/**
	 * Execute post.
	 *
	 * @param url the url
	 * @param requestObject the request object
	 * @param additionalHeaders the additional headers
	 * @return the client response
	 */
	public ClientResponse executePOST(String url, String requestObject, Map<String, String> additionalHeaders)  {
		logger.info("Executing processPOST for following URL: " + url + ", requestObject:" + requestObject);
		
		RestClient client = new RestClient(getClientConfig());		
		Resource resource = client.resource(url);	
		resource.header("Content-type", "application/json");
		resource.header("Accept", "application/json");
		
		//add additional headers if provided
		if(additionalHeaders != null && additionalHeaders.size() > 0){
			for(Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
				logger.info("Adding additional header: Key: " + entry.getKey() + ", Value: " + entry.getValue());
				resource.header(entry.getKey(), entry.getValue());
			}
		}
		
		//credentials are used for the Virtual Guest Services
		if(useAuthToken) {
			resource.header("X-Auth-Token", getxAuthToken());
		} else if(useBasicAuth) {
			resource.header("Authorization", "Basic "+ getxAuthToken());
		}
		
		ClientResponse clientResponse = resource.post(requestObject);
		String response = clientResponse.getEntity(String.class);		
		logger.info("Executed executePOST for following URL: " + url + ", clientResponse: " + clientResponse.getStatusCode() + ", response: " + response);
		return clientResponse;
	}
	
	/**
	 * Gets the client config.
	 *
	 * @return the client config
	 */
	protected ClientConfig getClientConfig() {
		ClientConfig config = new ClientConfig();
		config.proxyHost(properties.getProperty(SLProperties.SL_PROXYHOST));
		config.proxyPort(Integer.valueOf(properties.getProperty(SLProperties.SL_PROXYPORT)));
		return config;
	}	
}
