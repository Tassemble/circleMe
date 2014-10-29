package com.game.base.commons.utils.reflection;

import java.lang.reflect.Method;

import com.game.base.commons.utils.text.NeteaseEduStringUtils;

/*
 * @author 
 * @date 2012-5-8
 */
public class ReflectionUtils {

    public static Method getPropertyMethod(Class clz, String propertyName) {
        Method mth = null;
        propertyName = NeteaseEduStringUtils.upperFirstChar(propertyName);
        try {

            mth = clz.getMethod("get" + propertyName);
        } catch (Exception e) {
            try {
                mth = clz.getMethod("is" + propertyName);
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
        return mth;

    }

}
