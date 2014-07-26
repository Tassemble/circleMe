package com.game.base.dto;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.game.base.Comment;

public class TmallCommentsDto {
	List<Comment>				comments;

	Long						rateCount;
	

	public static final int baseLimitCount = 50;
	
	
	public int commentLimitCount;
	
	public static final int	RANDOM_COMMENT_COUNT_RANGE	= 50;
	
	
	
	public int getCommentLimitCount() {
		return commentLimitCount;
	}

	public void setCommentLimitCount(int commentLimitCount) {
		this.commentLimitCount = commentLimitCount;
	}

	public static final long	commentWordsLength	= 20;

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	
	public boolean isCommentsFull() {
		if (CollectionUtils.isEmpty(comments)) {
			return false;
		}
		if (comments.size() == commentLimitCount)
			return true;
		
		if (comments.size() < commentLimitCount)
			return false;
		return true;
	}

	public Long getRateCount() {
		return rateCount;
	}

	public void setRateCount(Long rateCount) {
		this.rateCount = rateCount;
	}

}
