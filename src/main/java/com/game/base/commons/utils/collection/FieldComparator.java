package com.game.base.commons.utils.collection;

import java.lang.reflect.Method;
import java.util.Comparator;

import com.game.base.commons.utils.reflection.ReflectionUtils;

public class FieldComparator<T> implements Comparator<T> {
	private String propertyName;
	private boolean isAsc;
	
	/**
	 * 用于指定字段的排序，默认为正序
	 * @param propertyName 用来排序的字段名，首字母需大写
	 */
	public FieldComparator(String propertyName) {
		this(propertyName, true);
	}
	
	/**
	 * 
	 * @param propertyName 用来排序的字段名，首字母需大写
	 * @param isAsc true-正序，false-倒序
	 */
	public FieldComparator(String propertyName, boolean isAsc) {
		this.propertyName = propertyName;
		this.isAsc = isAsc;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int compare(T t1, T t2) {
		if (t1 == null || t2 == null)
			return 0;
		
        Method mth = ReflectionUtils.getPropertyMethod(t1.getClass(), propertyName);
        
        try {
			Object o1 = mth.invoke(t1);
			Object o2 = mth.invoke(t2);
			
			if (o1 == null || o2 == null)
				return 0;
			
			Comparable value1 = (Comparable)o1;
			Comparable value2 = (Comparable)o2;
			
			if (isAsc) {
				return value1.compareTo(value2);
			} else {
				return value2.compareTo(value1);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}

    
    public String getPropertyName() {
        return propertyName;
    }

    
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    
    public boolean isAsc() {
        return isAsc;
    }

    
    public void setAsc(boolean isAsc) {
        this.isAsc = isAsc;
    }
}
