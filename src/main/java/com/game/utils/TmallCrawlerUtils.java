package com.game.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class TmallCrawlerUtils {

	private static final Logger	LOG					= Logger.getLogger(TmallCrawlerUtils.class);

	public static final String	TMALL_URL_PREFIX	= "http://detail.tmall.com/item.htm?id=";

	public static final String	TMALL_RATE_URL_PREFIX	= "http://rate.tmall.com/listTagClouds.htm?itemId=%d&isAll=true&isInner=true&t=%d&_ksTS=%d_3010&callback=jsonp3011";
		
		
	public static String getRateURL(Long itemId) {
		return String.format(TMALL_RATE_URL_PREFIX, itemId, System.currentTimeMillis(), System.currentTimeMillis());
	}
	
	
	public static void main(String[] args) {
		System.out.println(getRateURL(123456789l));
	}
	
	public static Long fetchIDfromTmallURL(String tmallURL) {
		if (StringUtils.isBlank(tmallURL)) {
			throw new RuntimeException("tmall url is blank!!!");
		}

		Pattern pattern = Pattern.compile("[&|\\?]{1,1}id=([0-9]+)");
		Matcher matcher = pattern.matcher(tmallURL.toLowerCase());

		if (matcher.find()) {
			try {
				return Long.valueOf(matcher.group(1));
			} catch (NumberFormatException e) {
				LOG.error(e.getMessage() + ", for url:" + tmallURL, e);
				return null;
			}
		}

		return null;
	}

	public static boolean isStartedWithTmallURL(String url) {
		if (StringUtils.isBlank(url)) {
			return false;
		}

		return url.startsWith(TMALL_URL_PREFIX);
	}

	public static String reWrapTmallURL(String url) {
		if (!isStartedWithTmallURL(url)) {
			return null;
		}
		return TMALL_URL_PREFIX + TmallCrawlerUtils.fetchIDfromTmallURL(url);
	}

	public static String doFilterNoise(String html) {
		int begin = html.indexOf("{");
		int end = html.lastIndexOf("}");
		if (begin <= -1 || end <= -1) {
			throw new RuntimeException("error html result:" + html);
		}
		return html.substring(begin, end + 1);
	}

}
