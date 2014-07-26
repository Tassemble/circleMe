package com.game.bomb.domain;

import com.game.base.commons.domain.BaseDo;
import com.netease.framework.dao.sql.annotation.DataProperty;

public class GameAttribute extends BaseDo {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	String	attrValue;
	String	attrName;
	
	
	
	public static final String KEY_DURATION_OF_RENEW_BLOOD = "key_duration_of_renew_blood";  //单位是ms 毫秒

	@DataProperty(column="attr_value")
	public String getAttrValue() {
		return attrValue;
	}

	public void setAttrValue(String attrValue) {
		this.attrValue = attrValue;
	}

	
	@DataProperty(column="attr_name")
	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

}
