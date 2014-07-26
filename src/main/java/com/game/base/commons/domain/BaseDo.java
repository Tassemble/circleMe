package com.game.base.commons.domain;

import java.io.Serializable;
import java.util.Date;

import com.netease.framework.dao.sql.annotation.DataProperty;


public class BaseDo implements Serializable {
    
    private static final long serialVersionUID                = 4029143262731629903L;

    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @DataProperty(column = "gmt_create")
    public Date getGmtCreate() {
        return gmtCreate;
    }
    
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
    
    @DataProperty(column = "gmt_modified")
    public Date getGmtModified() {
        return gmtModified;
    }
    
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}
