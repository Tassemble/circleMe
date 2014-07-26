package com.game.base.commons.utils.text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class DateFormatUtil {

	public static String getCrossStyleTextFromDate(Date date) {
        if (date == null) {
            return null;
        }

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
	
	public static String getYearMonthDayHourMinFromDate(Date date) {
	    if (date == null) {
	        return null;
	    }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return sdf.format(date);
    }
	
	public static String getYearMonthDayFromDate(Date date) {
        if (date == null) {
            return null;
        }
        
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		return sdf.format(date);
	}

	public static Date getCrossStyleDateFromText(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(text);
		} catch (ParseException e) {

		}
		return date;

	}

	public static void main(String[] args) {
		System.out.println(getCrossStyleTextFromDate(new Date()));
		System.out.println(getCrossStyleDateFromText("2011-12-6 11:54:00"));
	}
}
