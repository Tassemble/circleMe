package org.tassemble.member.domain;

import com.netease.framework.dao.sql.annotation.DataProperty;
import java.math.BigDecimal;
import com.game.base.commons.domain.BaseDomain;

public class CircleMemberRelation extends BaseDomain{

private static final long serialVersionUID = 1L;

	private Long selfId;
	private Long partnerId;
	private Integer relativeColor;
	
	public void setSelfId(Long selfId) {
		this.selfId = selfId;
	}
	
	@DataProperty(column = "self_id")	
	public Long getSelfId() {
		return selfId;
	}	

	
	public void setPartnerId(Long partnerId) {
		this.partnerId = partnerId;
	}
	
	@DataProperty(column = "partner_id")	
	public Long getPartnerId() {
		return partnerId;
	}	

	
	public void setRelativeColor(Integer relativeColor) {
		this.relativeColor = relativeColor;
	}
	
	@DataProperty(column = "relative_color")	
	public Integer getRelativeColor() {
		return relativeColor;
	}	



}
