package com.game.base.commons.utils.collection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.game.base.commons.utils.reflection.ReflectionUtils;

/*
 * @author hzfjd@corp.netease.com
 * @date 2012-5-8
 */
public class SubGroupUtils {

    /***
     * 根据bean的某个非空属性propertyName进行分组，具有相同的属性值得对象归为一组，所有的分组组成一个map， 其中propertyName首字母必须大写
     * 
     * @param list
     * @param propertyName
     * @return
     */
    public static Map<?, List<Object>> getByPropertyValue(List<? extends Object> list, String propertyName) {
        Map<? super Object, List<Object>> map = new HashMap<Object, List<Object>>();
        if (CollectionUtils.isEmpty(list)) {
            return map;
        }

        Class clz = list.get(0).getClass();
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
            List<Object> subList = (List<Object>) map.get(value);
            if (subList == null) {
                subList = new ArrayList<Object>();
                map.put(value, subList);
            }
            subList.add(item);

        }
        return map;
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

        Map<?, List<Object>> map = SubGroupUtils.getByPropertyValue(list, "CreatorId");

        for (Map.Entry<?, List<Object>> e : map.entrySet()) {
            System.out.println((Long) e.getKey() + "=" + ReflectionToStringBuilder.toString(e.getValue()));
        }

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
