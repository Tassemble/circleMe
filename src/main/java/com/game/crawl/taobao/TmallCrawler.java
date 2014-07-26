package com.game.crawl.taobao;

import java.util.List;
import java.util.Map;

import com.game.base.dto.TmallCommentsDto;
import com.game.base.dto.TmallProductDto;

public interface TmallCrawler {
	
	
	public static final String	KEY_TITLE			= "title";
	/**
	 * title上的第一张图片
	 */
	public static final String	KEY_FIRST_PICTURE	= "first_picture";

	/**
	 * title上的图片集
	 */
	public static final String	KEY_GALLERY			= "gallery";
	/**
	 * 详情内容
	 */
	public static final String	KEY_CONTENT			= "content";

	/** 获取默认前50条按推荐排序的、超过20个字的优质评论 , 返回一个list */
	public static final String	KEY_COMMENTS		= "comments";

	/**
	 * 专柜价格
	 */
	public static final String	KEY_PRICE			= "price";

	/**
	 * 促销价格
	 */
	public static final String	KEY_PROMOTE_PRICE	= "promote_price";

	public static final String	KEY_PRODUCT			= "product";
	

	Map<String, Object> getKeyValue(String url);

	String getTitleValue(String html);

	String getDetailContent(String html);

	List<String> getTitlePics(String html);

	TmallCommentsDto getComments(String url);
	
	Map<String, Object> getProductEvaluation(String tmallUrl);

	TmallProductDto getTmallProductDto(String tmallUrl);
	
	
	List<String> searchWithPhases(String phases, int pageIndex);

}
