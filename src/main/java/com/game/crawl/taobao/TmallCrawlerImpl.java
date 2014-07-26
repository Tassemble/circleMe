package com.game.crawl.taobao;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.base.Comment;
import com.game.base.dto.GatherEffect;
import com.game.base.dto.ProductEvaluationDto;
import com.game.base.dto.TmallCommentURLDto;
import com.game.base.dto.TmallCommentsDto;
import com.game.base.dto.TmallProductDto;
import com.game.utils.GsonUtils;
import com.game.utils.HttpClientUtils;
import com.game.utils.HttpDataProviderCandidate;
import com.game.utils.ProductScoreUtils;
import com.game.utils.TmallCrawlerUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

@Component("tmallCrawler")
public class TmallCrawlerImpl implements TmallCrawler {

	private static final Logger	LOG			= Logger.getLogger(TmallCrawlerImpl.class);
	public static Pattern		pattern		= Pattern
													.compile("^background-image:url\\((http://.*.jpg)_[0-9]+x[0-9]+.jpg\\)$");
	public static Pattern		imgPattern	= Pattern.compile("^(http://.*.jpg)_[0-9]+x[0-9]+.jpg$");

	@Autowired
	HttpClientUtils				httpClientUtils;

	public static void main3(String[] args) {
		System.out
				.println(TmallCrawlerUtils
						.reWrapTmallURL("http://detail.tmall.com/item.htm?id=12477082411&spm=a220m.1000858.1000725.9.RriLD8&user_id=373079721&is_b=1&cat_id=50095659&q=%CE%C4%D0%D8&rn=fb1b35c323de7e78297ba8989671ec10"));
		System.out
				.println(TmallCrawlerUtils
						.isStartedWithTmallURL("http://detail.tmall.com/item.htm?id=12477082411&spm=a220m.1000858.1000725.9.RriLD8&user_id=373079721&is_b=1&cat_id=50095659&q=%CE%C4%D0%D8&rn=fb1b35c323de7e78297ba8989671ec10"));
		System.out
				.println(TmallCrawlerUtils
						.fetchIDfromTmallURL("http://detail.tmall.com/item.htm?id=12477082411&spm=a220m.1000858.1000725.9.RriLD8&user_id=373079721&is_b=1&cat_id=50095659&q=%CE%C4%D0%D8&rn=fb1b35c323de7e78297ba8989671ec10"));
	}

	/**
	 * 获取数据入口
	 * 
	 * @param url
	 * @return
	 */
	@Override
	public Map<String, Object> getKeyValue(String url) {
		HttpClient httpClient = httpClientUtils.getTaobaoHttpManager();
		String html = HttpClientUtils.getHtmlByGetMethod(httpClient, url);
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("title", getTitleValue(html));
		List<String> pictures = getTitlePics(html);
		if (!CollectionUtils.isEmpty(pictures)) {
			map.put("first_picture", pictures.get(0));
			map.put("gallery", getTitlePics(html));
		}

		map.put("content", getDetailContent(html));

		map.put(KEY_COMMENTS, getComments(url));

		// put all scores

		map.putAll(getProductEvaluation(url));

		map.put(KEY_PRODUCT, getTmallProductDto(url));

		return map;
	}

	@Override
	public TmallCommentsDto getComments(String tmallURL) {

		Long productId = TmallCrawlerUtils.fetchIDfromTmallURL(tmallURL);
		if (productId == null)
			throw new RuntimeException("product id is null, for url:" + tmallURL);

		TmallCommentsDto commentsDto = new TmallCommentsDto();
		List<Comment> comments = Lists.newArrayList();
		commentsDto.setComments(comments);
		commentsDto.setCommentLimitCount(TmallCommentsDto.baseLimitCount
				+ RandomUtils.nextInt(TmallCommentsDto.RANDOM_COMMENT_COUNT_RANGE));

		TmallCommentURLDto commentURLDto = new TmallCommentURLDto();
		commentURLDto.setCurrentPage(1);
		commentURLDto.setItemId(productId);
		try {
			queryComments(commentsDto, comments, commentURLDto);
			for (int i = commentURLDto.getCurrentPage() + 1; i <= commentURLDto.getTotalPages(); i++) {
				if (commentsDto.isCommentsFull()) {
					return commentsDto;
				}
				commentURLDto.setCurrentPage(i);
				queryComments(commentsDto, comments, commentURLDto);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage() + ", for url:" + tmallURL, e);
		}
		return commentsDto;
	}

	/**
	 * @param tmallURL
	 * @param httpClient
	 * @param commentsDto
	 * @param comments
	 * @param commentURLDto
	 */
	private void queryComments(TmallCommentsDto commentsDto, List<Comment> comments, TmallCommentURLDto commentURLDto) {
		HttpClient httpClient = httpClientUtils.getTaobaoHttpManager();
		try {
			TimeUnit.MILLISECONDS.sleep(400);
		} catch (InterruptedException e) {
		}
		String commentResult = HttpClientUtils.getHtmlByGetMethod(httpClient, commentURLDto.getUrl());
		// 获取评论
		if (StringUtils.isBlank(commentResult)) {
			// throw new RuntimeException("comment result is null, for url:" +
			// tmallURL);
			throw new RuntimeException("comment result is null");
		}

		commentResult = TmallCrawlerUtils.doFilterNoise(commentResult);
		JSONObject jsonObject = JSONObject.fromObject(commentResult);
		JSONObject paginator = jsonObject.getJSONObject("rateDetail").getJSONObject("paginator");
		commentsDto.setRateCount(paginator.getLong("items"));
		JSONArray rateList = jsonObject.getJSONObject("rateDetail").getJSONArray("rateList");
		processComments(comments, rateList, commentsDto);

		long totalPages = paginator.getLong("lastPage");
		commentURLDto.setTotalPages(totalPages);
	}

	/**
	 * @param comments
	 * @param rateList
	 */
	private void processComments(List<Comment> comments, JSONArray rateList, TmallCommentsDto commentsDto) {
		for (int i = 0; i < rateList.size(); i++) {
			if (commentsDto.isCommentsFull()) {
				break;
			}
			Comment comment = new Comment();
			JSONObject tcomment = JSONObject.fromObject(rateList.get(i));
			if (StringUtils.isBlank(tcomment.getString("rateContent"))) {
				continue;
			}
			if (tcomment.getString("rateContent").length() < TmallCommentsDto.commentWordsLength) {
				continue;
			}
			if (StringUtils.isBlank(tcomment.getString("displayUserLink"))) {
				comment.setCommentAuthorUrl("");
			} else {
				comment.setCommentAuthorUrl(tcomment.getString("displayUserLink"));
			}
			comment.setCommentContent(tcomment.getString("rateContent"));
			comment.setCommentAuthor(tcomment.getString("displayUserNick"));
			comment.setCommentDate(new Timestamp(GsonUtils.parseTime(tcomment.getString("rateDate"))));
			comment.setCommentDateGmt(new Timestamp(GsonUtils.parseTime(tcomment.getString("rateDate"))));
			comment.setCommentType(Comment.TYPE_TMALL);
			comments.add(comment);
		}
	}

	public static void main4(String[] args) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = df.parse("2013-06-20 10:21:46");
		System.out.println(d.getTime());
	}

	/**
	 * @param html
	 */
	@Override
	public String getTitleValue(String html) {
		Document document = Jsoup.parse(html);
		Elements titleDiv = document.select("div.tb-detail-hd");
		Elements title = titleDiv.select("h3 > a");
		return title.get(0).text();
	}

	private String getDetailLocation(String html) {
		Pattern pattern = Pattern.compile("\\(window, document,\\['(.*?)'\\]\\);</script>");
		Matcher detailUrlMatcher = pattern.matcher(html);
		if (detailUrlMatcher.find()) {
			return detailUrlMatcher.group(1);
			// String content = HttpClientUtils.getHtmlByGetMethod(
			// httpClientUtils.getTaobaoHttpManager(),
			// detailUrlMatcher.group(1));
			// return getDetailContent(content);
		}
		return null;
	}

	/**
	 * @param content
	 */
	@Override
	public String getDetailContent(String html) {
		HttpClient httpClient = httpClientUtils.getTaobaoHttpManager();
		String detailUrl = getDetailLocation(html);
		String content = HttpClientUtils.getHtmlByGetMethod(httpClient, detailUrl);
		if (StringUtils.isNotBlank(content)) {
			Pattern divContentPattern = Pattern.compile("^var desc='(.*)';$");
			Matcher matcher = divContentPattern.matcher(content);
			if (matcher.find()) {
				return matcher.group(1);
			}
		}
		return null;
	}

	@Override
	public List<String> getTitlePics(String html) {
		Document document = Jsoup.parse(html);
		Elements galleryDiv = document.select("div.tb-gallery");
		Elements lis = galleryDiv.select("ul#J_UlThumb > li");
		List<String> urls = new ArrayList<String>();
		for (Element element : lis) {
			String unfilteredString = element.attr("style");
			String filteredUrl = null;
			if (StringUtils.isBlank(unfilteredString)) {
				Elements pics = element.select("li > a > img");
				if (pics != null && pics.size() > 0) {
					unfilteredString = pics.get(0).attr("src");
					Matcher matcher = imgPattern.matcher(unfilteredString);
					if (matcher.find()) {
						filteredUrl = matcher.group(1);
					}
				}
			} else {
				Matcher matcher = pattern.matcher(unfilteredString);
				if (matcher.find()) {
					filteredUrl = matcher.group(1);
				}
			}
			if (StringUtils.isNotBlank(filteredUrl)) {
				urls.add(filteredUrl);
			}
		}
		return urls;
	}

	public static void main1(String[] args) throws ClientProtocolException, IOException {
		TmallCrawlerImpl crawler = new TmallCrawlerImpl();
		List<String> list = Arrays.asList("14813675863", "15551958002", "12201055754");
		for (String string : list) {
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(new HttpGet("http://detail.tmall.com/item.htm?id=" + string));
			String html = EntityUtils.toString(response.getEntity());

			client = new DefaultHttpClient();
			String url = crawler.getDetailLocation(html);
			response = client.execute(new HttpGet(url));
			html = EntityUtils.toString(response.getEntity());
			System.out.println(crawler.getDetailContent(html));
		}

	}

	@Override
	public Map<String, Object> getProductEvaluation(String tmallUrl) {
		Map<String, Object> map = null;
		try {
			Long itemId = TmallCrawlerUtils.fetchIDfromTmallURL(tmallUrl);

			map = Maps.newHashMap();
			String evaluationUrl = TmallCrawlerUtils.getRateURL(itemId);

			HttpClient httpClient = httpClientUtils.getTaobaoHttpManager();
			String html = HttpClientUtils.getHtmlByGetMethod(httpClient, evaluationUrl);
			if (StringUtils.isBlank(html)) {
				return null;
			}
			String json = TmallCrawlerUtils.doFilterNoise(html);
			JSONObject jsonObject = JSONObject.fromObject(json);
			String tagClouds = jsonObject.getJSONObject("tags").getString("tagClouds");

			List<ProductEvaluationDto> dtos = GsonUtils.fromJson(tagClouds,
					new TypeToken<List<ProductEvaluationDto>>() {
					}.getType());
			map.put(ProductEvaluationDto.TAG_ALL_PRODUCT_EVALUATION, dtos);
			map.put(ProductEvaluationDto.TAG_AVARAGE_SCORE, ProductScoreUtils.getAvarageScore(dtos));
			map.put(ProductEvaluationDto.TAG_COST_VALUE_SCORE, ProductScoreUtils.getCostValueScore(dtos));
			map.put(ProductEvaluationDto.TAG_STYLE_SCORE, ProductScoreUtils.getStyleScore(dtos));

			JSONArray jsonArray = jsonObject.getJSONObject("tags").getJSONArray("innerTagCloudList");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				if ("聚拢效果".equals(object.getString("dimenName"))) {
					List<GatherEffect> effects = GsonUtils.fromJson(object.getString("tagScaleList"),
							new TypeToken<List<GatherEffect>>() {
							}.getType());
					map.put(GatherEffect.TAG_GATHER, effects);
					map.put(GatherEffect.TAG_GATHER_SCORE, ProductScoreUtils.getGatherScore(effects));
					break;
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}

		return map;
	}

	@Override
	public TmallProductDto getTmallProductDto(String tmallUrl) {
		TmallProductDto dto = null;
		try {
			Long itemId = TmallCrawlerUtils.fetchIDfromTmallURL(tmallUrl);
			if (itemId == null)
				return null;
			dto = new TmallProductDto(itemId);
			HttpClient httpClient = httpClientUtils.getTaobaoHttpManager();

			String html = HttpClientUtils.getHtmlByGetMethod(httpClient,
					HttpDataProviderCandidate.getWithReferer(dto.getProductInfoUrl(), dto.getProductReferer()));
			if (StringUtils.isBlank(html)) {
				LOG.error("http request:" + dto.getProductInfoUrl() + ", return null");
				return null;
			}

			JSONObject jsonObject = JSONObject.fromObject(html);
			if (StringUtils.isBlank(jsonObject.getString("isSuccess"))
					&& jsonObject.getString("isSuccess").equalsIgnoreCase("true")) {
				LOG.error("http request:" + dto.getProductInfoUrl() + " get data failed");
				return null;
			}

			JSONObject items = jsonObject.getJSONObject("defaultModel").getJSONObject("itemPriceResultDO")
					.getJSONObject("priceInfo");
			Set set = items.entrySet();
			for (Object object : set) {
				jsonObject = JSONObject.fromObject(object).getJSONObject("value");
				dto.setPrice(jsonObject.getDouble("price"));
				if (jsonObject.getJSONArray("promotionList") != null) {
					JSONObject promotion = JSONObject.fromObject(jsonObject.getJSONArray("promotionList").get(0));
					dto.setPostageFree(promotion.getBoolean("postageFree"));
					dto.setPromotionPrice(promotion.getDouble("price"));
					if (!promotion.getString("endTime").equals("null")
							&& !promotion.getString("startTime").equals("null")) {
						dto.setPromotionEndTime(promotion.getLong("endTime"));
						dto.setPromotionStartTime(promotion.getLong("startTime"));
					}
					dto.setPromotionType(promotion.getString("type"));
				}
				break;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
		return dto;
	}

	static boolean	isMock	= false;

	@Override
	public List<String> searchWithPhases(String phases, int pageIndex) {
		List<String> list = Lists.newArrayList();
		try {
			int totalPage = 0;
			int pageSize = 60;
			int from = pageIndex * pageSize;

			String tmallSearchUrl = String.format(
					"http://list.tmall.com/search_product.htm?s=%s&n=%s&q=%s&sort=s&style=g&type=pc", from, pageSize,
					URLEncoder.encode(phases, "GB2312"));
			String html;
			if (isMock) {
				HttpClient client = new DefaultHttpClient();
				html = EntityUtils.toString(client.execute(new HttpGet(tmallSearchUrl)).getEntity());
			} else {
				html = HttpClientUtils.getHtmlByGetMethod(httpClientUtils.getTaobaoHttpManager(), tmallSearchUrl);
			}
			Document document = Jsoup.parse(html);
			Elements hiddens = document.select("form[name=filterPageForm] > input[name=totalPage]");
			for (Element element : hiddens) {
				if (element.attr("value") != null) {
					totalPage = Integer.valueOf(element.attr("value"));
					break;
				}
			}
			LOG.info("current page/total page is " + pageIndex + "/" + totalPage);
			if (totalPage < pageIndex) {
				return list;
			}

			Elements products = document.select("div#J_ItemList > div");

			for (Element product : products) {
				list.add(product.attr("data-id"));
			}
			return list;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return list;
		}
	}

	public static void main(String[] args) {
		isMock = true;
		TmallCrawlerImpl crawlerImpl = new TmallCrawlerImpl();
		List<String> ids = crawlerImpl.searchWithPhases("文胸 薄款", 1);
		GsonUtils.printJson(ids);
	}
}
