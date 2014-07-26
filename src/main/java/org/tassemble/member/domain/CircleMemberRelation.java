package org.tassemble.member.domain;

import com.netease.framework.dao.sql.annotation.DataProperty;
import java.math.BigDecimal;

public class CircleMemberRelation {

private static final long serialVersionUID = 1L;

	private String id;
	private String selfId;
	private String partnerId;
	private Integer relativeColor;
	private Long gmtCreate;
	private Long gmtModified;
	
	public void setId(String id) {
		this.id = id;
	}
	
	@DataProperty(column = "id")	
	public String getId() {
		return id;
	}	

	
	public void setSelfId(String selfId) {
		this.selfId = selfId;
	}
	
	@DataProperty(column = "self_id")	
	public String getSelfId() {
		return selfId;
	}	

	
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	
	@DataProperty(column = "partner_id")	
	public String getPartnerId() {
		return partnerId;
	}	

	
	public void setRelativeColor(Integer relativeColor) {
		this.relativeColor = relativeColor;
	}
	
	@DataProperty(column = "relative_color")	
	public Integer getRelativeColor() {
		return relativeColor;
	}	

	
	public void setGmtCreate(Long gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	
	@DataProperty(column = "gmt_create")	
	public Long getGmtCreate() {
		return gmtCreate;
	}	

	
	public void setGmtModified(Long gmtModified) {
		this.gmtModified = gmtModified;
	}
	
	@DataProperty(column = "gmt_modified")	
	public Long getGmtModified() {
		return gmtModified;
	}	



}
