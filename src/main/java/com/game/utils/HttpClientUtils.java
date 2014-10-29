package com.game.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * Http connection pool
 * 
 * @author Richard
 * @version 1.0
 * @since 2013.1.15
 */
@Component("httpClientUtils")
public class HttpClientUtils implements InitializingBean, DisposableBean {

	private static final Logger				log					= LoggerFactory.getLogger(HttpClientUtils.class);
	private static final int				CONNECTION_TIMEOUT	= 3000;											// 连接超时时间
	private static final int				SO_TIMEOUT			= 5000;											// 等待数据超时时间
	private PoolingClientConnectionManager	taobaoHttpManager	= null;

	private int								taobaoMaxConnection	= 200;	
	
	
	private PoolingClientConnectionManager	commonHttpManager	= null;

	private int								commonMaxConnection	= 200;	// 每条通道最大并发连接数

	
	private PoolingClientConnectionManager	verifyReceiptDataHttpManager	= null;
	private int								verifyReceiptMaxConnection	= 100;		
	static {
		
		
	}

	
	public HttpClient getCommonHttpManager() {
		return new DefaultHttpClient(commonHttpManager, getParams());
	}
	
	
	
	public HttpClient getVerifyReceiptDataHttpManager() {
		return new DefaultHttpClient(verifyReceiptDataHttpManager, getParams());
	}
	
	
	public HttpClient getTaobaoHttpManager() {
		return new DefaultHttpClient(taobaoHttpManager, getParams());
	}

	private HttpParams getParams() {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
		return params;
	}

	public static String getHtmlByGetMethod(HttpClient httpClient, String url) {
		try {
			HttpGet get = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(get);
			return EntityUtils.toString(httpResponse.getEntity());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
	
	
	public static String getHtmlByGetMethod(HttpClient httpClient, HttpDataProvider provider) {
		try {
			HttpGet get = new HttpGet(provider.getUrl());
			
			if (!CollectionUtils.isEmpty(provider.getHeaders())) {
				// solve :Content-Length header already present exception
				for (Header header : provider.getHeaders()) {
					get.addHeader(header);
				}
			}
			
			
			HttpResponse httpResponse = httpClient.execute(get);
			return EntityUtils.toString(httpResponse.getEntity());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	
	public static String getDefaultHtmlByPostMethod(HttpClient client, HttpDataProvider provider) throws Exception {
		// List<NameValuePair> nameValuePairs = provider.getNameValuePairs();
		// UrlEncodedFormEntity params = new
		// UrlEncodedFormEntity(nameValuePairs, "UTF-8");

		
		HttpPost post = new HttpPost(provider.getUrl());
		if (provider.getHttpEntity() != null)
			post.setEntity(provider.getHttpEntity());

		if (!CollectionUtils.isEmpty(provider.getHeaders())) {
			// solve :Content-Length header already present exception
			for (Header header : provider.getHeaders()) {
				post.addHeader(header);
			}
		}

		if (log.isDebugEnabled()) {
		}
		HttpResponse response = client.execute(post);

		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			log.error("response code is not OK, it's " + response.getStatusLine().getStatusCode());
			throw new RuntimeException("response code is not OK, it's " + response.getStatusLine().getStatusCode());
		}
		
		HttpEntity entity = response.getEntity();
		
		String result = EntityUtils.toString(entity, "UTF-8");
		post.abort();
		return result;
		
	}
	public static String getHtmlByPostMethod(HttpClient client, HttpDataProvider provider) throws Exception {
		// List<NameValuePair> nameValuePairs = provider.getNameValuePairs();
		// UrlEncodedFormEntity params = new
		// UrlEncodedFormEntity(nameValuePairs, "UTF-8");

		
		HttpPost post = new HttpPost(provider.getUrl());
		if (provider.getHttpEntity() != null)
			post.setEntity(provider.getHttpEntity());

		if (!CollectionUtils.isEmpty(provider.getHeaders())) {
			// solve :Content-Length header already present exception
			for (Header header : provider.getHeaders()) {
				post.addHeader(header);
			}
		}

		if (log.isDebugEnabled()) {
		}
		HttpResponse response = client.execute(post);

		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			if (log.isDebugEnabled()) {
				log.debug(EntityUtils.toString(response.getEntity()));
			}
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header[] headers = response.getHeaders("Set-Cookie");
				if (!ArrayUtils.isEmpty(headers)) {
					CookieStore cookieStore = new BasicCookieStore();
					for (Header cookieHeader : headers) {
						String value = cookieHeader.getValue().split(";")[0];
						if (!StringUtils.isEmpty(value)) {
							String[] keyValue = value.split("=");
							if (keyValue.length == 2)
							{
								BasicClientCookie cookie = new BasicClientCookie(keyValue[0], keyValue[1]);
								cookie.setVersion(1);
								cookie.setDomain(".520wenxiong.com");
								cookie.setPath("/");
								cookie.setSecure(true);
								// Set attributes EXACTLY as sent by the server 
								cookie.setAttribute(ClientCookie.VERSION_ATTR, "1");
								cookie.setAttribute(ClientCookie.DOMAIN_ATTR, ".520wenxiong.com");
								cookieStore.addCookie(cookie);
							}
						}
					}
					DefaultHttpClient httpClient = (DefaultHttpClient) client;
					httpClient.setCookieStore(cookieStore);
				}
				// log.info(response.getFirstHeader("Set-Cookie").getValue());
				String url = response.getFirstHeader("Location").getValue();
				log.warn("302 redirect:" + url);
				return HttpClientUtils.getHtmlByGetMethod(client, url);
			}
			throw new RuntimeException("execute:" + provider.getUrl() + " failed, response code:"
					+ response.getStatusLine().getStatusCode());
		}

		HttpEntity entity = response.getEntity();
		
		String result = EntityUtils.toString(entity, "UTF-8");
		post.abort();
		return result;
	}

	@Override
	public void destroy() throws Exception {
		log.info("Http connection pool will destory...");
		if (taobaoHttpManager != null) {
			taobaoHttpManager.shutdown();
		}
		
		if (verifyReceiptDataHttpManager != null) {
			verifyReceiptDataHttpManager.shutdown();
		}
		log.info("Http connection pool destroyed!");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
		taobaoHttpManager = new PoolingClientConnectionManager(schemeRegistry);
		taobaoHttpManager.setMaxTotal(taobaoMaxConnection);
		taobaoHttpManager.setDefaultMaxPerRoute(taobaoMaxConnection); // 每条通道最大并发连接数
		log.info("Feedback http connection pool has start up..., max total is:" + taobaoMaxConnection);
		
		
		verifyReceiptDataHttpManager = new PoolingClientConnectionManager(schemeRegistry);;
		verifyReceiptDataHttpManager.setMaxTotal(verifyReceiptMaxConnection);
		verifyReceiptDataHttpManager.setDefaultMaxPerRoute(verifyReceiptMaxConnection);
		log.info("Feedback http connection pool has start up..., max total is:" + verifyReceiptDataHttpManager);
		
		commonHttpManager = new PoolingClientConnectionManager(schemeRegistry);;
		commonHttpManager.setMaxTotal(commonMaxConnection);
		commonHttpManager.setDefaultMaxPerRoute(commonMaxConnection);
		
		
		log.info("Feedback http connection pool has start up..., max total is:" + commonHttpManager);
	}


	


	public void setCommonHttpManager(PoolingClientConnectionManager commonHttpManager) {
		this.commonHttpManager = commonHttpManager;
	}


}
