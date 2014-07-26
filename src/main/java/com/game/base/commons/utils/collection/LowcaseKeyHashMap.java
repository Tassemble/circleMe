package com.game.base.commons.utils.collection;

import java.util.HashMap;

public class LowcaseKeyHashMap extends HashMap<String, Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5713923786455917984L;

	@Override
	public boolean containsKey(Object key) {
		key = ((String) key).toLowerCase();
		return super.containsKey(key);
	}

	@Override
	public Object get(Object key) {
		key = ((String) key).toLowerCase();
		return super.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		key = key.toLowerCase();
		return super.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		key = ((String) key).toLowerCase();
		return super.remove(key);
	}
}
