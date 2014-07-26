package com.game.utils;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public interface HttpDataProvider {

	
	public String getUrl();
	public HttpEntity getHttpEntity();
	public List<Header> getHeaders();
	
	
	
}
