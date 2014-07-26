package com.game.utils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.game.base.commons.utils.text.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



public class GsonUtils {
	
	
	private static Gson						g	= new GsonBuilder().serializeNulls().create();
	private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	
	public static final String origin = "http://520wenxiong.com";
	public static final String host = "520wenxiong.com";
	
	
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String getUploadFileNonce(String html) {
		if (StringUtils.isBlank(html)) {
			return null;
		}
		String noncePrefix ="{\"action\":\"upload-attachment\",\"_wpnonce\":\""; 
		String noncePostfix = "\"";
		int index = html.indexOf(noncePrefix);
		if (index == -1)
			return null;
		else 
			index += noncePrefix.length();
		int end = html.indexOf(noncePostfix, index);
		return html.substring(index, end);
	}
	
	public static long parseTime(String time) {
		try {
			return df.parse(time).getTime();
		} catch (ParseException e) {
			throw new RuntimeException("parse time error", e);
		}
	}
	
    public static Object getFromJson(String json, Type type) {
        return g.fromJson(json, type);
    }
	
    public static <T> T getFromJson(String json, Class<T> type) {
        return g.fromJson(json, type);
    }
    
    
    
    public static String toJson(Object o) {
        return gson.toJson(o);
    }

    public static <T> List<T> fromJson(String json, Type type) {
    	if (StringUtils.isBlank(json)) {
    		return null;
    	}
        return g.fromJson(json, type);
    }

    public static void printJson(Object o) {
        System.out.println(toJson(o));
    }

    // new TypeToken<List<OpenCoursePlay>>(){}.getType()
    public static void printJson(Object o, Type type) {
        System.out.println(g.toJson(o, type));
    }

    public static boolean isFirstBiggerOrEqualsThanSecond(Long a, Long b) {
        if (a == null || b == null) {
            return false;
        }
        return a >= b;
    }

    public static boolean isFirstBiggerThanSecond(Long a, Long b) {
        if (a == null || b == null) {
            return false;
        }
        return a > b;
    }

    public static boolean isFirstBiggerThanSecond(Integer a, Integer b) {
        if (a == null || b == null) {
            return false;
        }
        return a > b;
    }

    public static boolean equals(Integer a, Integer b) {
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }

    public static boolean isFirstBiggerOrEqualsThanSecond(Integer a, Integer b) {
        if (a == null || b == null) {
            return false;
        }
        return a >= b;
    }
    
    
}
