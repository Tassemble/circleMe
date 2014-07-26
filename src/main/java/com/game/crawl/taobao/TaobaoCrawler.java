package com.game.crawl.taobao;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.utils.HttpClientUtils;

@Component("taobaoCrawler")
public class TaobaoCrawler {
	
	@Autowired
	HttpClientUtils httpClientUtils;
	
	public String getHtmlByGetMethod(String url) throws Exception {
		HttpClient httpClient = httpClientUtils.getTaobaoHttpManager();
		HttpGet get = new HttpGet(url);
		HttpResponse httpResponse = httpClient.execute(get);
		return EntityUtils.toString(httpResponse.getEntity());
	}
	
	
	
	
	
	
	//title pics div? comment? comment tags ?
	
	
	
	
	
}
