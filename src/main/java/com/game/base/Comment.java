package com.game.base;

import java.sql.Timestamp;

import com.netease.framework.dao.sql.annotation.DataProperty;

public class Comment {
	
	public static final String COMMENT_TYPE_TMALL = "tmall";
	
	public static final String TYPE_TMALL = "from_tmall";
	
	
	private Long commentID;
	private Long commentPostID;
	private String commentAuthor;
	private Timestamp commentDate;
	private Timestamp commentDateGmt;
	private String commentContent;
	private String commentType;
	private Long userId;
	private String commentAuthorUrl;
	
	
	
	@DataProperty(column="comment_author_url")
	public String getCommentAuthorUrl() {
		return commentAuthorUrl;
	}
	public void setCommentAuthorUrl(String commentAuthorUrl) {
		this.commentAuthorUrl = commentAuthorUrl;
	}
	@DataProperty(column="comment_ID")
	public Long getCommentID() {
		return commentID;
	}
	public void setCommentID(Long commentID) {
		this.commentID = commentID;
	}
	
	@DataProperty(column="comment_post_ID")
	public Long getCommentPostID() {
		return commentPostID;
	}
	public void setCommentPostID(Long commentPostID) {
		this.commentPostID = commentPostID;
	}
	
	@DataProperty(column="comment_author")
	public String getCommentAuthor() {
		return commentAuthor;
	}
	public void setCommentAuthor(String commentAuthor) {
		this.commentAuthor = commentAuthor;
	}
	
	@DataProperty(column="comment_date")
	public Timestamp getCommentDate() {
		return commentDate;
	}
	public void setCommentDate(Timestamp commentDate) {
		this.commentDate = commentDate;
	}
	
	@DataProperty(column="comment_date_gmt")
	public Timestamp getCommentDateGmt() {
		return commentDateGmt;
	}
	public void setCommentDateGmt(Timestamp commentDateGmt) {
		this.commentDateGmt = commentDateGmt;
	}
	
	@DataProperty(column="comment_content")
	public String getCommentContent() {
		return commentContent;
	}
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	
	@DataProperty(column="comment_type")
	public String getCommentType() {
		return commentType;
	}
	public void setCommentType(String commentType) {
		this.commentType = commentType;
	}
	
	@DataProperty(column="user_id")
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
	
	
}
