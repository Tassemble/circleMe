package com.game.base.service;

import java.util.Map;

import com.game.base.WPPost;
import com.game.base.commons.service.BaseService;

public interface WPPostService extends BaseService<WPPost> {

	public String	KEY_USER		= "user";
	public String	KEY_ARTICLE_ID	= "articleId";
	public String	KEY_ORIGIN_URL	= "origin";

	boolean postFeatureFileAndUpdateAttachment(Long postId, String firstPicture);

	Map<String, Object> addOneArticle(String tmallUrl, Long userId);

}
