/**
 * 
 */
package com.game.base;

import java.sql.Timestamp;

import com.netease.framework.dao.sql.annotation.DataProperty;

/**
 * @author Administrator
 * 
 */
public class WPPost {
	public final static String GUID_PREFIX= "http://520wenxiong.com/?p=";
	
	public final static String TYPE_POST = "post";
	public final static String TYPE_PAGE = "page";
	public final static String TYPE_ATTACHMENT = "attachment";
	public final static String TYPE_MENU_ITEM = "nav_menu_item";
	public final static String TYPE_REVISION = "revision";
	
	
	public final static String POST_STATUS_PUBLISH = "publish";
	public final static String POST_STATUS_INHERIT = "inherit";
	
	public final static String POST_TYPE_ATTACHMENT = "attachment";
	public final static String POST_TYPE_POST = "post";
	
	private String postContent;
	private String postTitle;
	private String postExcerpt;
	private String postStatus;
	private String commentStatus;
	private String pingStatus;
	private String postPassword;
	private String postName;
	private String toPing;
	private String pinged;
	private String postContentFiltered;
	private String guid;
	private String postType;
	private String postMimeType;
	private Integer menuOrder;
	private Long id;
	private Long postAuthor;
	private Timestamp postDate;
	private Timestamp postDateGmt;
	private Timestamp postModified;
	private Timestamp postModifiedGmt;
	private Long postParent;
	private Long commentCount;
	
	
	

	@DataProperty(column="post_modified")
	public Timestamp getPostModified() {
		return postModified;
	}

	public void setPostModified(Timestamp postModified) {
		this.postModified = postModified;
	}

	@DataProperty(column = "post_content")
	public String getPostContent() {
		return postContent;
	}

	public void setPostContent(String postContent) {
		this.postContent = postContent;
	}

	@DataProperty(column = "post_title")
	public String getPostTitle() {
		return postTitle;
	}

	public void setPostTitle(String postTitle) {
		this.postTitle = postTitle;
	}

	@DataProperty(column = "post_excerpt")
	public String getPostExcerpt() {
		return postExcerpt;
	}

	public void setPostExcerpt(String postExcerpt) {
		this.postExcerpt = postExcerpt;
	}

	@DataProperty(column = "post_status")
	public String getPostStatus() {
		return postStatus;
	}

	public void setPostStatus(String postStatus) {
		this.postStatus = postStatus;
	}

	@DataProperty(column = "comment_status")
	public String getCommentStatus() {
		return commentStatus;
	}

	public void setCommentStatus(String commentStatus) {
		this.commentStatus = commentStatus;
	}

	@DataProperty(column = "ping_status")
	public String getPingStatus() {
		return pingStatus;
	}

	public void setPingStatus(String pingStatus) {
		this.pingStatus = pingStatus;
	}

	@DataProperty(column = "post_password")
	public String getPostPassword() {
		return postPassword;
	}

	public void setPostPassword(String postPassword) {
		this.postPassword = postPassword;
	}

	@DataProperty(column = "post_name")
	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	@DataProperty(column = "to_ping")
	public String getToPing() {
		return toPing;
	}

	public void setToPing(String toPing) {
		this.toPing = toPing;
	}

	@DataProperty(column = "pinged")
	public String getPinged() {
		return pinged;
	}

	public void setPinged(String pinged) {
		this.pinged = pinged;
	}

	@DataProperty(column = "post_content_filtered")
	public String getPostContentFiltered() {
		return postContentFiltered;
	}

	public void setPostContentFiltered(String postContentFiltered) {
		this.postContentFiltered = postContentFiltered;
	}

	@DataProperty(column = "guid")
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	@DataProperty(column = "post_type")
	public String getPostType() {
		return postType;
	}

	public void setPostType(String postType) {
		this.postType = postType;
	}

	@DataProperty(column = "post_mime_type")
	public String getPostMimeType() {
		return postMimeType;
	}

	public void setPostMimeType(String postMimeType) {
		this.postMimeType = postMimeType;
	}

	@DataProperty(column = "menu_order")
	public Integer getMenuOrder() {
		return menuOrder;
	}

	public void setMenuOrder(Integer menuOrder) {
		this.menuOrder = menuOrder;
	}

	@DataProperty(column = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@DataProperty(column = "post_author")
	public Long getPostAuthor() {
		return postAuthor;
	}

	public void setPostAuthor(Long postAuthor) {
		this.postAuthor = postAuthor;
	}

	

	
	
	

	
	
	@DataProperty(column = "post_date_gmt")
	public Timestamp getPostDateGmt() {
		return postDateGmt;
	}


	@DataProperty(column = "post_modified_gmt")
	public Timestamp getPostModifiedGmt() {
		return postModifiedGmt;
	}

	public void setPostDate(Timestamp postDate) {
		this.postDate = postDate;
	}

	
	@DataProperty(column = "post_date")
	public Timestamp getPostDate() {
		return postDate;
	}

	public void setPostDateGmt(Timestamp postDateGmt) {
		this.postDateGmt = postDateGmt;
	}

	public void setPostModifiedGmt(Timestamp postModifiedGmt) {
		this.postModifiedGmt = postModifiedGmt;
	}

	@DataProperty(column = "post_parent")
	public Long getPostParent() {
		return postParent;
	}

	public void setPostParent(Long postParent) {
		this.postParent = postParent;
	}

	@DataProperty(column = "comment_count")
	public Long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Long commentCount) {
		this.commentCount = commentCount;
	}

}
