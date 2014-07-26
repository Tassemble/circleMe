package com.game.base;

import com.netease.framework.dao.sql.annotation.DataProperty;

public class WPPostMeta {

	public static final String META_Key_SET_FEATURE = "_thumbnail_id";
	public static final String META_Key_TMALL_URL = "tmall_url";
	public static final String META_KEY_AVERAGE_SCORE = "average_score";
	public static final String META_KEY_PRODUCT_DATA = "product_data";
	
	
	
	Long metaId;
	Long postId;
	String metaKey;
	String metaValue;
	
	
	@DataProperty(column="meta_id")
	public Long getMetaId() {
		return metaId;
	}
	public void setMetaId(Long metaId) {
		this.metaId = metaId;
	}
	
	@DataProperty(column="post_id")
	public Long getPostId() {
		return postId;
	}
	public void setPostId(Long postId) {
		this.postId = postId;
	}
	
	@DataProperty(column="meta_key")
	public String getMetaKey() {
		return metaKey;
	}
	public void setMetaKey(String metaKey) {
		this.metaKey = metaKey;
	}
	
	@DataProperty(column="meta_value")
	public String getMetaValue() {
		return metaValue;
	}
	public void setMetaValue(String metaValue) {
		this.metaValue = metaValue;
	}
	
	
	
}
