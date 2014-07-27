package com.game.base.commons.domain;

import java.io.Serializable;
import java.util.Date;

import com.netease.framework.dao.sql.annotation.DataProperty;

public class BaseDomain implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6677452322246013257L;
    private Long              id;
    private Long              gmtCreate;
    private Long              gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @DataProperty(column = "gmt_create")
    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @DataProperty(column = "gmt_modified")
    public Long getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Long gmtModified) {
        this.gmtModified = gmtModified;
    }
    public void fillAllTime(){
    	this.setGmtCreate(new Date().getTime());
    	this.setGmtModified(new Date().getTime());
    }
    public void fillModifedTime(){
        this.setGmtModified(new Date().getTime());
    }

}
