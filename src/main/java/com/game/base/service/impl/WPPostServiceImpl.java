package com.game.base.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.game.base.Comment;
import com.game.base.WPPost;
import com.game.base.WPPostMeta;
import com.game.base.commons.service.impl.BaseServiceImpl;
import com.game.base.dao.CommentDao;
import com.game.base.dao.WPPostDao;
import com.game.base.dao.WPPostMetaDao;
import com.game.base.dto.ProductEvaluationDto;
import com.game.base.dto.TmallCommentsDto;
import com.game.base.dto.TmallProductDto;
import com.game.base.service.WPPostService;
import com.game.crawl.taobao.TmallCrawler;
import com.game.crawl.taobao.TmallCrawlerImpl;
import com.game.utils.GsonUtils;
import com.game.utils.HtmlTagsUtils;
import com.game.utils.HttpClientUtils;
import com.game.utils.HttpDataProviderCandidate;
import com.game.utils.ProductScoreUtils;
import com.game.utils.TmallCrawlerUtils;

@Service("wpPostService")
public class WPPostServiceImpl extends BaseServiceImpl<WPPostDao, WPPost> implements WPPostService {

	WPPostDao					wpPostDao;

	@Autowired
	WPPostMetaDao				wpPostMetaDao;

	@Autowired
	CommentDao					commentDao;

	private static final Logger	LOG	= Logger.getLogger("crawl_name");

	@Autowired
	TmallCrawlerImpl			tmallCrawler;

	@Autowired
	HttpClientUtils				httpClientUtils;

	public WPPostDao getWpPostDao() {
		return wpPostDao;
	}

	@Autowired
	public void setWpPostDao(WPPostDao wpPostDao) {
		super.setBaseDao(wpPostDao);
		this.wpPostDao = wpPostDao;
	}

	//
	@Override
	public Map<String, Object> addOneArticle(String tmallUrl, Long userId) {
		tmallUrl = tmallUrl.trim();

		WPPostMeta query = new WPPostMeta();
		query.setMetaKey(WPPostMeta.META_Key_TMALL_URL);
		query.setMetaValue(tmallUrl);
		List<WPPostMeta> results = wpPostMetaDao.getByDomainObjectSelective(query);

		Map<String, Object> map = tmallCrawler.getKeyValue(tmallUrl);
		map.put(KEY_ORIGIN_URL, tmallUrl);
		map.put(KEY_USER, userId);

		if (map.get(KEY_USER) == null || map.get("title") == null || map.get("gallery") == null
				/*|| map.get("content") == null*/) {
			LOG.info("user or title or gallery or content may be null, for tamll url:" + tmallUrl);
			throw new RuntimeException("crawl data failed!!!");
		}

		if (!CollectionUtils.isEmpty(results)) {
			// update
			Long articeId = Long.valueOf(TmallCrawlerUtils.fetchIDfromTmallURL(results.get(0).getMetaValue()));
			map.put(KEY_ARTICLE_ID, articeId);
			map.put("exist", true);
			// first remove all average_score
			wpPostMetaDao.deleteByCondition("meta_key = ? and post_id = ?",
					WPPostMeta.META_KEY_AVERAGE_SCORE, articeId);

			Long postId = addWPPostByTmall(map);
			TmallCommentsDto tmallCommentsDto = tmallCrawler.getComments(tmallUrl);
			if (!CollectionUtils.isEmpty(tmallCommentsDto.getComments())) {
				commentDao.deleteByCondition("comment_post_ID = ?", results.get(0).getMetaValue());
				addComments(tmallCommentsDto, postId);
			}
			LOG.info("finished update posted:" + postId + ", tmall url:" + tmallUrl);
		} else {
			// add new one
			map.put("exist", false);
			LOG.info("add new  an acticle, tmall url:" + tmallUrl);
			Long postId = addWPPostByTmall(map);
			map.put(KEY_ARTICLE_ID, postId);
			TmallCommentsDto tmallCommentsDto = (TmallCommentsDto) map.get(TmallCrawler.KEY_COMMENTS);//tmallCrawler.getComments(tmallUrl);
			if (!CollectionUtils.isEmpty(tmallCommentsDto.getComments())) {
				addComments(tmallCommentsDto, postId);
			}
			// postFeatureFileAndUpdateAttachment(postId, (String)
			// map.get("first_picture"));
			LOG.info("finished posted:" + postId + ", tmall url:" + tmallUrl);
		}

		return map;
	}

	/**
	 * @param tmallCommentsDto
	 */
	private void addComments(TmallCommentsDto tmallCommentsDto, Long postId) {
		List<Comment> comments = tmallCommentsDto.getComments();
		for (Comment c : comments) {
			c.setCommentID(commentDao.getId());
			c.setCommentPostID(postId);
			c.setUserId(0L);
			commentDao.add(c);
		}
		long cnt = commentDao.countByCondition("comment_post_ID = ?", postId);
		WPPost changedValue = new WPPost();
		changedValue.setCommentCount(cnt);
		wpPostDao.updateSelectiveByCondition(changedValue, "ID = ?", postId);
	}

	@SuppressWarnings("unchecked")
	private Long addWPPostByTmall(Map<String, Object> map) {
		Long author = (Long) map.get(KEY_USER);
		if (author == null) {
			author = 1L;
		}
		if (map.get(KEY_ARTICLE_ID) != null) {
			// update
			WPPost post = new WPPost();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			post.setPostModified(timestamp);
			post.setPostModifiedGmt(timestamp);

			return Long.valueOf(map.get(KEY_ARTICLE_ID).toString());
		} else {

			WPPost post = new WPPost();
			List<String> gallery = (List<String>) map.get("gallery");
			final Long newPostId = this.getId();
			post.setId(newPostId);
			post.setPostTitle((String) map.get("title") + "");
			StringBuilder builder = new StringBuilder();
//			for (int i = 1; i < gallery.size(); i++) {
//				builder.append(HtmlTagsUtils.PICTURE_PREFIX + gallery.get(i)
//						+ HtmlTagsUtils.PICTURE_POSTFIX);
//			}
//			String content = (String) map.get("content");
//
//			builder.append(content);

			// this.add(post);
			// post.setPostContent("");
			if (map.get(ProductEvaluationDto.TAG_AVARAGE_SCORE) != null) {
				builder.append("基于用户评价后的产品综合智能得分：" + map.get(ProductEvaluationDto.TAG_AVARAGE_SCORE) + " 分");
			} else {
				builder.append("基于用户评价后的产品综合智能得分：8.0 分");
			}
			if ( map.get(ProductEvaluationDto.TAG_COST_VALUE_SCORE) != null) {
				builder.append("<br></br>基于用户评价后的性价比智能得分：" + map.get(ProductEvaluationDto.TAG_COST_VALUE_SCORE)+ " 分");
			} else {
				builder.append("<br></br>基于用户评价后的性价比智能得分：8.0 分");
			}
			if (map.get(ProductEvaluationDto.TAG_STYLE_SCORE)!= null) {
				builder.append("<br></br>基于用户评价后的产品款式智能得分：" + map.get(ProductEvaluationDto.TAG_STYLE_SCORE) + " 分");
			}else {
				builder.append("<br></br>基于用户评价后的产品款式智能得分：8.0 分");
			}
			builder.append(HtmlTagsUtils.JUMP_TO_BUY_prefix + (String) map.get("origin")
					+ HtmlTagsUtils.JUMP_TO_BUY_postfix);
			
			
			TmallCommentsDto dtos = (TmallCommentsDto) map.get(TmallCrawler.KEY_COMMENTS);
			if (!CollectionUtils.isEmpty(dtos.getComments())) {
				builder.append("<br></br>" + dtos.getComments().get(0).getCommentAuthor());
				builder.append(":" + dtos.getComments().get(0).getCommentContent());
			}
			
/**
 * 			map.put(ProductEvaluationDto.TAG_ALL_PRODUCT_EVALUATION, dtos);
			map.put(ProductEvaluationDto.TAG_AVARAGE_SCORE, ProductScoreUtils.getAvarageScore(dtos));
			map.put(ProductEvaluationDto.TAG_COST_VALUE_SCORE, ProductScoreUtils.getCostValueScore(dtos));
			map.put(ProductEvaluationDto.TAG_STYLE_SCORE, ProductScoreUtils.getStyleScore(dtos));
 */
			
			post.setPostContent(builder.toString());
			post.setCommentCount(0L);
			post.setCommentStatus("open");
			post.setGuid(WPPost.GUID_PREFIX + newPostId);
			post.setMenuOrder(0);
			post.setPingStatus("open");
			post.setPostAuthor(author);
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			post.setPostDate(timestamp);
			post.setPostDateGmt(timestamp);
			post.setPostModified(timestamp);
			post.setPostModifiedGmt(timestamp);
			post.setPostName("post_name");
			post.setPostParent(0L);
			post.setPostStatus("publish");
			post.setPostType("post");
			post.setToPing("");
			post.setPinged("");
			post.setPostExcerpt("");
			post.setPostContentFiltered("");
			post.setPostPassword("");
			post.setPostMimeType("");
			this.add(post);

			WPPostMeta wpPostMeta = new WPPostMeta();
			wpPostMeta.setMetaId(wpPostMetaDao.getId());
			wpPostMeta.setPostId(post.getId());
			wpPostMeta.setMetaKey(WPPostMeta.META_Key_TMALL_URL);
			wpPostMeta.setMetaValue(String.valueOf(map.get("origin")));
			wpPostMetaDao.add(wpPostMeta);

			WPPostMeta averageScore = new WPPostMeta();
			averageScore.setMetaId(wpPostMetaDao.getId());
			averageScore.setPostId(post.getId());
			averageScore.setMetaKey(WPPostMeta.META_KEY_AVERAGE_SCORE);
			averageScore.setMetaValue(String.valueOf(map
					.get(ProductEvaluationDto.TAG_AVARAGE_SCORE)));
			wpPostMetaDao.add(averageScore);

			TmallProductDto dto = (TmallProductDto) map.get(TmallCrawler.KEY_PRODUCT);
			if (dto != null) {
				WPPostMeta productData = new WPPostMeta();
				productData.setMetaId(wpPostMetaDao.getId());
				productData.setPostId(post.getId());
				productData.setMetaKey(WPPostMeta.META_KEY_PRODUCT_DATA);
				String productDataString = String.format("<div style=\"margin-top:20px\"><div style=\"text-decoration:line-through; color:#FF0000\"><span><A style=\"color:white\">专柜价：￥%s</a></span><span style=\"float:right\"><span style=\"color:red\">%s：￥%s</span></div></div>",
						String.valueOf(dto.getPrice()), dto.getPromotionType(), String.valueOf(dto.getPromotionPrice()));
				if (dto.getPromotionEndTime() != null) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yy/mm/dd hh:mm");
					String time = dateFormat.format(new Date());
					productDataString += String.format("<div><span style=\"padding: 7px 0 0 132px\"><a href=\"%s\">直达连接</a></span> <span style=\"color:red;float:right\">活动截止：%s</span></div>", time, String.valueOf(map.get(KEY_ORIGIN_URL)));
				} else {
					productDataString += String.format("<div style=\"margin-top:10px\"><span><a href=\"%s\">直达连接</a></span></div>", String.valueOf(map.get(KEY_ORIGIN_URL)));
				}
				productData.setMetaValue(productDataString);
				wpPostMetaDao.add(productData);
			}

			return newPostId;
		}

	}

	@Override
	public boolean postFeatureFileAndUpdateAttachment(Long postId, String firstPicture) {
		LOG.info("add feature for " + postId);
		HttpClient httpClient = httpClientUtils.getTaobaoHttpManager();
		HttpClient getWPPage  = httpClientUtils.getTaobaoHttpManager();
		HttpClient postClient  = httpClientUtils.getTaobaoHttpManager();
		
		int retry = 3;
		if (StringUtils.isNotBlank(firstPicture)) {
			Long fileMetaIdValue = null;
			while (retry-- > 0) {
				try {
					// add a picture to server
					if (StringUtils.isBlank(HttpDataProviderCandidate.cookie)) {
						//reset cookie
						resetCookie(httpClient);
					}
					
					String html = HttpClientUtils.getHtmlByGetMethod(
							getWPPage,
							HttpDataProviderCandidate.getHomePage(GsonUtils.origin
									+ "/wp-admin/post.php?post=" + postId + "&action=edit", HttpDataProviderCandidate.cookie));
					String nonce = GsonUtils.getUploadFileNonce(html);
					String result = HttpClientUtils.getHtmlByPostMethod(
							postClient,
							HttpDataProviderCandidate.getPostPictureData(firstPicture,
									String.valueOf(postId), nonce, HttpDataProviderCandidate.cookie));

					JSONObject jsonObject = JSONObject.fromObject(result);
					if (new Boolean(true).equals(jsonObject.getBoolean("success"))) {
						fileMetaIdValue = jsonObject.getJSONObject("data").getLong("id");
						WPPostMeta feature = new WPPostMeta();
						Long metaId = wpPostMetaDao.getId();
						feature.setMetaId(metaId);
						feature.setMetaKey(WPPostMeta.META_Key_SET_FEATURE);
						feature.setPostId(postId);
						feature.setMetaValue(String.valueOf(fileMetaIdValue));
						wpPostMetaDao.add(feature);
					}
				} catch (Exception e) {
					if (retry <= 0) {
						LOG.error(e.getMessage() + ",add feature for " + postId + " failed!", e);
						resetCookie(httpClient);
						throw new RuntimeException("post data failed!!!");
					}
					continue;
				}
				// no exception
				break;
			}
		}
		return true;
	}

	/**
	 * @param httpClient
	 * @throws Exception
	 */
	private static void resetCookie(HttpClient httpClient)  {
		try {
			HttpClientUtils.getHtmlByPostMethod(httpClient, HttpDataProviderCandidate.getLoginData());
			CookieStore store = ((DefaultHttpClient)httpClient).getCookieStore();
			StringBuilder sb = new StringBuilder();
			for (Cookie c : store.getCookies()) {
				sb.append(c.getName()+ "=" + c.getValue()+";");
			}
			HttpDataProviderCandidate.cookie = sb.toString();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public static void main(String[] args) throws Exception {
		DefaultHttpClient httpClient  = new DefaultHttpClient();
		resetCookie(httpClient);
		String html = HttpClientUtils.getHtmlByGetMethod(
				new DefaultHttpClient(),
				HttpDataProviderCandidate.getHomePage(GsonUtils.origin
						+ "/wp-admin/post.php?post=1000005&action=edit", HttpDataProviderCandidate.cookie));
		String nonce = GsonUtils.getUploadFileNonce(html);
		System.out.println(nonce);
//		String result = HttpClientUtils
//				.getHtmlByPostMethod(
//						httpClient,
//						HttpDataProviderCandidate
//								.getPostPictureData(
//										"http://f.hiphotos.baidu.com/album/w%3D2048/sign=a06e8c9991ef76c6d0d2fc2ba92efcfa/b03533fa828ba61eed6096974034970a314e59ff.jpg",
//										"1000005", nonce));

//		System.out.println(result);
	}
}
