package com.game.base;

import com.netease.framework.dao.sql.annotation.DataProperty;

public class WPTermRelationship {
	Long objectId;
	Long termTaxonomyId;
	Integer termOrder;
	
	
	@DataProperty(column="object_id")
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	
	@DataProperty(column="term_taxonomy_id")
	public Long getTermTaxonomyId() {
		return termTaxonomyId;
	}
	public void setTermTaxonomyId(Long termTaxonomyId) {
		this.termTaxonomyId = termTaxonomyId;
	}
	
	@DataProperty(column="term_order")
	public Integer getTermOrder() {
		return termOrder;
	}
	public void setTermOrder(Integer termOrder) {
		this.termOrder = termOrder;
	}
	
	
	
	
}
