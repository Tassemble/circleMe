package com.game.base.commons.utils.collection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.game.base.commons.utils.reflection.ReflectionUtils;

/*
 * @author rongxz@corp.netease.com
 * @date 2012-8-2
 */
public class PropertyExtractUtils {

    /***
     * 根据bean的某个非空属性propertyName获取数据列表， 其中propertyName首字母必须大写
     * 
     * @param list
     * @param propertyName
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getByPropertyValue(List<? extends Object> list, String propertyName) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        List<T> retList = new ArrayList<T>(list.size());
        java.util.Set<T> set = new java.util.HashSet<T>(list.size());
        Class<?> clz = list.get(0).getClass();
        Method mth = ReflectionUtils.getPropertyMethod(clz, propertyName);

        for (Object item : list) {
            Object value = null;
            try {
                value = mth.invoke(item);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (value == null) {
                continue;
            }
            set.add((T)value);
        }
        retList.addAll(set);
        return retList;
    }
    
    public static <T> List<T> getByPropertyValue(List<? extends Object> list, String propertyName, Class<T> clazz) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        List<T> retList = new ArrayList<T>(list.size());
        java.util.Set<T> set = new java.util.HashSet<T>(list.size());
        Class<?> clz = list.get(0).getClass();
        Method mth = ReflectionUtils.getPropertyMethod(clz, propertyName);

        for (Object item : list) {
            Object value = null;
            try {
                value = mth.invoke(item);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (value == null) {
                continue;
            }
            set.add(clazz.cast(value));
        }
        retList.addAll(set);
        return retList;
    }

}
