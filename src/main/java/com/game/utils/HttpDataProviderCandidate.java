package com.game.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.aspectj.apache.bcel.classfile.Field;

public class HttpDataProviderCandidate {

	private static final Logger	LOG		= Logger.getLogger(HttpDataProviderCandidate.class);

	public static String cookie	= null;

	public static final String username = "admin";
	public static final String password = "chq_163";
	
	public static HttpDataProvider getHomePage(final String url, final String cookie) {
		return new HttpDataProvider() {
			@Override
			public String getUrl() {
				return url;
			}

			@Override
			public HttpEntity getHttpEntity() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<Header> getHeaders() {
				Header header = new BasicHeader(
						"Cookie",
						cookie);
				List<Header> headers = new ArrayList<Header>();
				headers.add(header);
				headers.add(new BasicHeader("Host", GsonUtils.host));
				headers.add(new BasicHeader("Origin", GsonUtils.origin));

				return headers;
			}
		};

	}

	public static HttpDataProvider getLoginData() {
		return new HttpDataProvider() {
			@Override
			public String getUrl() {
				return GsonUtils.origin + "/wp-login.php";
			}

			@Override
			public HttpEntity getHttpEntity() {
				try {
					List<NameValuePair> list = new ArrayList<NameValuePair>();
					list.add(new BasicNameValuePair("log", username));
					list.add(new BasicNameValuePair("pwd", password));
					list.add(new BasicNameValuePair("wp-submit", "登录"));
					list.add(new BasicNameValuePair("redirect_to", GsonUtils.origin + "/wp-admin/"));
					list.add(new BasicNameValuePair("testcookie", "1"));
					return new UrlEncodedFormEntity(list, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					LOG.error(e.getMessage(), e);
					return null;
				}
			}

			@Override
			public List<Header> getHeaders() {
				List<Header> headers = new ArrayList<Header>();
				headers.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
				headers.add(new BasicHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3"));
				headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"));
				headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8"));
				headers.add(new BasicHeader("Cache-Control", "no-cache"));
				headers.add(new BasicHeader("Connection", "keep-alive"));
				headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
				headers.add(new BasicHeader("Host", GsonUtils.host));
				headers.add(new BasicHeader("Origin", GsonUtils.origin));
				headers.add(new BasicHeader("Pragma", "no-cache"));
				headers.add(new BasicHeader("Referer", "http://520wenxiong.com/wp-login.php"));
				headers.add(new BasicHeader("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.172 Safari/537.22"));
				return headers;
			}
		};
	}

	public static HttpDataProvider getPostPictureData(final String pictureURL, final String postId, final String wpNonce, final String cookie) {
		return new HttpDataProvider() {

			@Override
			public String getUrl() {
				return GsonUtils.origin + "/wp-admin/async-upload.php";
			}

			// 1000005
			@Override
			public HttpEntity getHttpEntity() {
				try {
					int index = pictureURL.lastIndexOf("/");
					if (index == -1) {
						throw new RuntimeException("last slash can't find for picture url:" + pictureURL);
					}
					final String fileName = pictureURL.substring(index + 1, pictureURL.length());
					File folder = new File("." + File.separator + "picture_fold" +File.separator);
					if (folder.exists()) {
						LOG.error("folder not find!!");
					}
					File tmpFile = new File("." + File.separator + "picture_fold" +File.separator +  fileName);
					FileOutputStream stream = new FileOutputStream(tmpFile);
					IOUtils.copy(new URL(pictureURL).openStream(), stream);
					MultipartEntity entity = new MultipartEntity();
					// ContentBody pictureInputStream = new InputStreamBody(new
					FormBodyPart name = new FormBodyPart("name", new StringBody(fileName, Charset.forName("UTF-8")));
					FormBodyPart action = new FormBodyPart("action", new StringBody("upload-attachment",
							Charset.forName("UTF-8")));
					/**
					 * attention: if upload failed the _wpnonce may be the
					 * cause!!!
					 */
					FormBodyPart wpnonce = new FormBodyPart("_wpnonce", new StringBody(wpNonce,// "90d5a6e709",
							Charset.forName("UTF-8")));
					FormBodyPart postIdPart = new FormBodyPart("post_id", new StringBody(postId,
							Charset.forName("UTF-8")));
					ContentBody fileFromDisk = new FileBody(tmpFile, fileName, "image/jpeg");
					entity.addPart(name);
					entity.addPart(action);
					entity.addPart(wpnonce);
					entity.addPart(postIdPart);
					entity.addPart("async-upload", fileFromDisk);
					return entity;
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					return null;
				}
			}

			@Override
			public List<Header> getHeaders() {

				Header header = new BasicHeader(
						"Cookie",
						cookie);
				List<Header> headers = new ArrayList<Header>();
				headers.add(header);
				headers.add(new BasicHeader("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.172 Safari/537.22"));
				headers.add(new BasicHeader("Referer", GsonUtils.origin
						+ "/wp-admin/post.php?post=1000005&action=edit"));

				headers.add(new BasicHeader("Host", GsonUtils.host));
				headers.add(new BasicHeader("Origin", GsonUtils.origin));
				
				return headers;
			}
		};
	}
	
	public static HttpDataProvider getWithReferer(final String url, final String referUrl) {
		return new HttpDataProvider() {

			@Override
			public String getUrl() {
				return url;
			}

			@Override
			public HttpEntity getHttpEntity() {
				return null;
			}

			@Override
			public List<Header> getHeaders() {
				Header header = new BasicHeader(
						"Referer",
						referUrl);
				List<Header> headers = new ArrayList<Header>();
				headers.add(header);
				return headers;
			}
		};
	}
}
