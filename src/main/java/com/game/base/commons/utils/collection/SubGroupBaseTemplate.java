package com.game.base.commons.utils.collection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.springframework.util.Assert;

import com.game.base.commons.utils.reflection.ReflectionUtils;
import com.game.base.commons.utils.text.JsonUtils;

/*
 * @author 
 * @date 2012-5-11
 */
public class SubGroupBaseTemplate {

    /***
     * 根据bean的某个非空属性propertyName进行分组，具有相同的属性值得对象归为一组，所有的分组组成一个map， 其中propertyName首字母必须大写 <PROP属性类型, OBJ对象类型>
     * 
     * @param list
     * @param propertyName 首字母必须大写
     * @return
     */
    public static <PROP, OBJ> Map<PROP, List<OBJ>> getByPropertyValue(List<? extends OBJ> list, String propertyName) {
        Map<PROP, List<OBJ>> map = new HashMap<PROP, List<OBJ>>();
        if (CollectionUtils.isEmpty(list)) {
            return map;
        }

        Class<? extends Object> clz = list.get(0).getClass();
        Method mth = ReflectionUtils.getPropertyMethod(clz, propertyName);

        for (OBJ item : list) {

            PROP value = null;
            try {
                value = (PROP) mth.invoke(item);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (value == null) {
                continue;
            }
            List<OBJ> subList = (List<OBJ>) map.get(value);
            if (subList == null) {
                subList = new ArrayList<OBJ>();
                map.put(value, subList);
            }
            subList.add(item);
        }
        return map;
    }

    /**
     * 获取一个对象数组里面的属性列表
     * 
     * @param objectList
     * @param propertyName
     * @return
     */
    public static <PROP, OBJ> List<PROP> getPropertiesListFromObjectList(List<OBJ> objectList, String propertyName) {

        List<PROP> propList = new ArrayList<PROP>();
        if (CollectionUtils.isEmpty(objectList)) {
            return propList;
        }

        Method propertyMtd = ReflectionUtils.getPropertyMethod(objectList.get(0).getClass(), propertyName);

        for (OBJ obj : objectList) {
            try {
                PROP value = (PROP) propertyMtd.invoke(obj);
                if (value != null) {
                    propList.add(value);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        return propList;

    }

    public static void main(String[] args) {

        List<TestVo> list = new ArrayList<TestVo>();

        for (int i = 0; i < 10; i++) {
            TestVo a = new TestVo();
            a.setId(new Long(i));
            long tt = i % 3;
            a.setCreatorId(tt);
            list.add(a);
        }

        List<List<TestVo>> testSubGrouList = SubGroupBaseTemplate.getSubGroupsByFixedSize(list, 3);
        JsonUtils.printlnJson(testSubGrouList);

        // Map<Long, List<TestVo>> map = SubGroupBaseTemplate.getByPropertyValue(list, "creatorId");

        // List<Long> idLIst = SubGroupBaseTemplate.getPropertiesListFromObjectList(list, "creatorId");

        // JsonUtils.printlnJson(idLIst);

        // for (Map.Entry<Long, List<TestVo>> e : map.entrySet()) {
        // System.out.println((Long) e.getKey() + "=" + ReflectionToStringBuilder.toString(e.getValue()));
        // }

    }

    public static <T> List<List<T>> getSubGroupsByFixedSize(List<T> sourceList, int size) {

        Assert.isTrue(size > 0, "sub group size must bigger than 0");

        List<List<T>> subGroupList = new ArrayList<List<T>>();

        if (CollectionUtils.isEmpty(sourceList)) {
            subGroupList.add(new ArrayList<T>());
            return subGroupList;
        }
        int index = 0;
        List<T> subGroup = null;
        for (T source : sourceList) {
            if (index % size == 0) {
                subGroup = new ArrayList<T>();
                subGroupList.add(subGroup);
            }
            subGroup.add(source);
            index++;
        }
        return subGroupList;
    }

    private static class TestVo {

        private Long creatorId;
        private Long id;

        public Long getCreatorId() {
            return creatorId;
        }

        public void setCreatorId(Long creatorId) {
            this.creatorId = creatorId;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

    }

}
