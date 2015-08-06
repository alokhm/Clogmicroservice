package com.covisint.platform.clog.server.wrapper;

import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.params.HttpParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.covisint.platform.clog.core.cloginstance.ClogInstance;

public class HttpClientWrapperTest {
	@Mock 
	HttpClientWrapper httpClientWrapper;

	@Before
	public void setUp() throws Exception {
		httpClientWrapper = Mockito.mock(HttpClientWrapper.class);
		Mockito.when(httpClientWrapper.post(Mockito.isA(ClogInstance.class), Mockito.isA(String.class),Mockito.isA(String.class))).thenReturn(createHttpResponse());
		Mockito.when(httpClientWrapper.delete(Mockito.isA(String.class))).thenReturn(createHttpResponse());
	}

	/**
	 * 
	 * @return
	 */
	private HttpResponse createHttpResponse() {
		// TODO Auto-generated method stub
		return new HttpResponse() {
			
			@Override
			public void setParams(HttpParams arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setHeaders(Header[] arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setHeader(String arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setHeader(Header arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void removeHeaders(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void removeHeader(Header arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public HeaderIterator headerIterator(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HeaderIterator headerIterator() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ProtocolVersion getProtocolVersion() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HttpParams getParams() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Header getLastHeader(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Header[] getHeaders(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Header getFirstHeader(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Header[] getAllHeaders() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean containsHeader(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void addHeader(String arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addHeader(Header arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setStatusLine(ProtocolVersion arg0, int arg1, String arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setStatusLine(ProtocolVersion arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setStatusLine(StatusLine arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setStatusCode(int arg0) throws IllegalStateException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setReasonPhrase(String arg0) throws IllegalStateException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setLocale(Locale arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setEntity(HttpEntity arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public StatusLine getStatusLine() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Locale getLocale() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HttpEntity getEntity() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPost() throws Exception {
		HttpResponse response = httpClientWrapper.post(new ClogInstance(), "elasticSearchUrl", "indexName");
		assertNotNull(response);
	}
	
	@Test
	public void testDelete() throws Exception {
		HttpResponse response = httpClientWrapper.delete("indexName");
		assertNotNull(response);
	}


}
