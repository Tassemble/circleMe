package com.game.base.commons.utils.text;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsonUtils {

    public static String toJson(Object o) {
        if (o == null) {
            return "";
        }

        if (o instanceof String || o instanceof Boolean || o instanceof Byte || o instanceof Character
            || o instanceof Integer || o instanceof Long || o instanceof Float || o instanceof Double
            || o instanceof BigDecimal) {
            Object[] tempObjectArray = new Object[1];
            tempObjectArray[0] = o;
            return JSONArray.fromObject(tempObjectArray).toString();
        }

        if (o instanceof Collection || o.getClass().isArray()) {
            return JSONArray.fromObject(o).toString();
        }

        return JSONObject.fromObject(o).toString();
    }

    public static void printlnJson(Object o) {
        System.out.println(toJson(o));
    }

    public static void main(String[] args) {

        System.out.println(JSONObject.fromObject(new Integer(111)));

        String[] l = new String[] { "1", "2", "3" };
        printlnJson(l);
        printlnJson("/index/common");
        int i = 22;
        printlnJson(i);
        printlnJson(1L);
        printlnJson(new Date());

    }
}
