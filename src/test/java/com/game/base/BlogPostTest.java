package com.game.base;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.game.base.Comment;
import com.game.base.User;
import com.game.base.WPPost;
import com.game.base.dao.BaseTestCase;
import com.game.base.dao.CommentDao;
import com.game.base.dao.UserDao;
import com.game.base.dao.WPPostDao;
import com.game.base.dto.TmallCommentsDto;
import com.game.base.service.WPPostService;
import com.game.crawl.taobao.TmallCrawler;
import com.game.utils.GsonUtils;
import com.game.utils.TmallCrawlerUtils;
import com.google.common.collect.ImmutableMap;

public class BlogPostTest extends BaseTestCase {

	private static final Logger	LOG	= Logger.getLogger(BlogPostTest.class);

	@Autowired
	WPPostDao					blogPostDao;

	@Autowired
	WPPostService				postService;

	@Autowired
	WPPostDao					dao;

	@Autowired
	WPPostService				w;

	@Autowired
	TmallCrawler				tmallCrawler;

	@Autowired
	CommentDao					commentDao;

	@Autowired
	WPPostDao					wpPostDao;

	@Autowired
	UserDao						userDao;

	@Test
	public void test() throws Exception {
		postService.add(new WPPost());
		// WPPost post = blogPostDao.getFirstOneByCondition("id = ?", 148);
		// //
		// post.setId(165L);
		// post.setGuid("http://520wenxiong.com/?p=" + post.getId());
		// Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		// post.setPostDate(timestamp);
		// post.setPostDateGmt(timestamp);
		// post.setPostModified(timestamp);
		// post.setPostModifiedGmt(timestamp);
		// post.setPostTitle("【四周年店庆 98元】黛安芬(Triumph)外贸热销新款聚拢文胸深V性感U型美背品牌内衣文胸单件");
		// post.setPostContent("<div id=\"description\">    	    <div class=\"content\" id=\"J_DivItemDesc\"><p><img align=\"absMiddle\" src=\"http://img03.taobaocdn.com/imgextra/i3/143012796/T2u1bfXm4XXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"><img align=\"absMiddle\" src=\"http://img01.taobaocdn.com/imgextra/i1/143012796/T2T5TfXmFXXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"></p><p style=\"text-align: center;\"><span style=\"color: #0000ff;\"><strong><span style=\"font-size: 18.0px;\">黛安芬支持七天无理由退换</span></strong></span></p><p style=\"text-align: center;\"><span style=\"color: #0000ff;\"><span style=\"font-size: 18.0px;\">文胸是常规标准尺码，因每个人体型/穿戴有差异，建议按平常穿戴标准选择，极少数客户反应偏小，如平时穿薄杯，全罩杯的建议加大一个罩杯，平时穿厚杯或者中厚杯的建议按正常选择<span style=\"background-color: #f4cccc;font-family: 黑体;background-origin: initial;background-clip: initial;\"><span style=\"background-color: #ffffff;\"></span></span></span></span></p><p style=\"text-align: center;\"><span style=\"font-size: 18.0px;\"><span style=\"background-color: #f4cccc;font-family: 黑体;color: #444444;background-origin: initial;background-clip: initial;\"><span style=\"background-color: #ffffff;\">（</span><span style=\"font-family: 黑体;\"><span style=\"background-color: #ffffff;\">不要随意给中差评！介意勿拍！）</span></span></span></span></p><p><img src=\"http://img01.taobaocdn.com/imgextra/i1/143012796/T2uEGZXgRaXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"><img src=\"http://img04.taobaocdn.com/imgextra/i4/143012796/T2fYivXaRcXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"></p><p>&nbsp;</p><p><img align=\"middle\" src=\"http://img03.taobaocdn.com/imgextra/i3/143012796/T2E12DXXVXXXXXXXXX_!!143012796.gif\" data-pinit=\"registered\"><img align=\"middle\" src=\"http://img04.taobaocdn.com/imgextra/i4/143012796/T2hfrCXiJaXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"><img align=\"middle\" src=\"http://img01.taobaocdn.com/imgextra/i1/143012796/T2vUDCXhRXXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"></p><p><img align=\"absMiddle\" src=\"http://img02.taobaocdn.com/imgextra/i2/143012796/T2gjLLXh8aXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"></p><p><img align=\"middle\" src=\"http://img04.taobaocdn.com/imgextra/i4/143012796/T2rJPCXgpaXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"><img align=\"middle\" src=\"http://img04.taobaocdn.com/imgextra/i4/143012796/T2ZL_CXhRaXXXXXXXX_!!143012796.jpg\"></p><p><img align=\"absMiddle\" src=\"http://img01.taobaocdn.com/imgextra/i1/143012796/T2wzrMXcJXXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"></p><p><img align=\"absMiddle\" src=\"http://img04.taobaocdn.com/imgextra/i4/143012796/T2L7YKXjFaXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"></p><p>&nbsp;</p><p><img align=\"middle\" src=\"http://img02.taobaocdn.com/imgextra/i2/143012796/T2DMe8XcFaXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"></p><p>&nbsp;</p><p><img align=\"absMiddle\" src=\"http://img01.taobaocdn.com/imgextra/i1/143012796/T2eqe9Xi8XXXXXXXXX_!!143012796.jpg\" data-pinit=\"registered\"></p><div style=\"visibility: hidden;top: 454.0px;left: 150.0px;\">&nbsp;</div></div></div>");
		// http://gd1.alicdn.com/bao/uploaded/i3/12796020211287767/T1mXoxXaJXXXXXXXXX_!!0-item_pic.jpg_310x310.jpg_.webp
		// LOG.info(gson.toJson(post));
		// post.setPostContent(mysql_real_escape_string(post.getPostContent()));
		// System.out.println(mysql_real_escape_string(post.getPostContent()));
		// blogPostDao.updateSelectiveById(post);
	}

	@Test
	@Rollback
	public void print() {
		System.out.println("\"" + ", " + "\\\"");
		Long id = dao.getId();
		System.out.println("insert id:" + (id + 1));
		dao.testInsert("insert into IDGenerator (ID) values ( " + (id + 1) + " )");
	}

	@Test
	public void addOnePicture() {
		w.postFeatureFileAndUpdateAttachment(1000595L,
				"http://img01.taobaocdn.com/bao/uploaded/i1/12702024626922807/T1sKJnFbXcXXXXXXXX_!!0-item_pic.jpg");
	}

	@Test
	public void addFromTmall() {
		// w.postFeatureFileAndUpdateAttachment(1000275L,
		// "http://img03.taobaocdn.com/bao/uploaded/i3/T11f_pXjBwXXaVAMA0_035327.jpg");
		// w.postFeatureFileAndUpdateAttachment(1000201L,
		// "http://img02.taobaocdn.com/imgextra/i2/1023043723/T2b2AcXd4aXXXXXXXX_!!1023043723.jpg");
		List<String> list = new ArrayList<String>();
		// list.add("httplist.add("http://detail.tmall.com/item.htm?id=10809674717");
		// list.add("http://detail.tmall.com/item.htm?id=23276317000");
		// http://detail.tmall.com/item.htm?id=12477082411
		// 较特殊
		List<String> ids = null;
		// for (int indexPage = 1;; indexPage++) {
		int nextOne = RandomUtils.nextInt(80) + 1;
		ids = tmallCrawler.searchWithPhases("文胸 薄款", nextOne);
		if (CollectionUtils.isEmpty(ids)) {
			return;
		}

		for (String string : ids) {
			list.add(TmallCrawlerUtils.TMALL_URL_PREFIX + string);
		}

		List<User> users = userDao.getAll();

		for (String url : list) {
			try {
				User user = null;
				while (true) {
					user = users.get(RandomUtils.nextInt(users.size()));
					if (user.getId().equals(1L) || user.getId().equals(2L)) {
						continue;
					}
					break;
				}
				Map<String, Object> map = w.addOneArticle(url, user.getId());
				if (map != null && map.get("exist") != null) {
					boolean exist = (Boolean) map.get("exist");
					if (!exist) {
						w.postFeatureFileAndUpdateAttachment((Long) map.get(WPPostService.KEY_ARTICLE_ID),
								(String) map.get("first_picture"));
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		// clean
		list.clear();

		// }
	}

	@Test
	public void testNextInt() {
		List<User> users = userDao.getAll();
		User user = null;
		while (true) {
			user = users.get(RandomUtils.nextInt(users.size()));
			if (user.getId().equals(1L) || user.getId().equals(2L)) {
				continue;
			}
			System.out.println(user.getId());
			break;
		}
	}

	@Test
	public void testAddUsers() {
		Map<String, String> map = ImmutableMap.of("sweet", "sweet", "meme", "meme", "ppGirl", "ppGirl", "xuexu",
				"xuexu", "heyGL", "heyGL");
		for (Entry<String, String> entry : map.entrySet()) {
			User user = new User();
			user.setId(userDao.getId());
			user.setDisplayName(entry.getValue());
			user.setUserEmail("wx520_wx520@163.com");
			user.setUserLogin(entry.getKey());
			user.setUserNicename(entry.getValue());
			user.setUserPass(DigestUtils.md5Hex("wx520__wx520"));
			user.setUserStatus(0);
			user.setUserRegister(new Timestamp(System.currentTimeMillis()));
			userDao.add(user);
		}

	}

	@Test
	public void getComments() {

		TmallCommentsDto commentsDto = tmallCrawler.getComments("http://detail.tmall.com/item.htm?id=12738735995");
		// WordPressUtils.printJson(commentsDto.getComments());

		for (Comment c : commentsDto.getComments()) {
			c.setCommentID(commentDao.getId());
			c.setCommentPostID(1000265L);
			c.setUserId(0L);
			commentDao.add(c);
		}

		long cnt = commentDao.countByCondition("comment_post_ID = ?", 1000265L);
		WPPost changedValue = new WPPost();
		changedValue.setCommentCount(cnt);
		wpPostDao.updateSelectiveByCondition(changedValue, "ID = ?", 1000265L);
	}

	@Test
	public void getScore() {
		Map<String, Object> map = tmallCrawler
				.getProductEvaluation("http://detail.tmall.com/item.htm?spm=a220m.1000858.1000725.176.tqPHGJ&id=9511696318&user_id=655788246&is_b=1&cat_id=50095659&q=%CE%C4%D0%D8&rn=520c73b221605b192168f24d4fc6a3f7");
		for (Entry<String, Object> entry : map.entrySet()) {
			System.err.println(entry.getKey() + ", " + entry.getValue());
		}
	}

	@Test
	public void getPrice() {
		GsonUtils.printJson(tmallCrawler.getTmallProductDto("http://detail.tmall.com/item.htm?id=12738735995"));
	}

}
