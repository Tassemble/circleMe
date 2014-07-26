package com.game.base.dto;

public class TmallCommentURLDto {
	//按推荐排序
	public static final String	CRAWL_URL_PREFIX = "http://rate.tmall.com/list_detail_rate.htm";
	public static final String	crawlUrl = "http://rate.tmall.com/list_detail_rate.htm?itemId=12201055754&order=3&currentPage=2&append=0&content=1&tagId=&posi=&callback=jsonp1281";
	
	private Long itemId;
	private Integer order = new Integer(3);
	private Integer  currentPage = new Integer(1);
	private Long totalPages;
	
	
	
	public Long getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Long totalPages) {
		this.totalPages = totalPages;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public String getUrl() {
		return new StringBuilder().append(CRAWL_URL_PREFIX).append("?itemId=" + itemId).append("&order=" +order)
				.append("&currentPage=" + currentPage).append("&append=0&content=1&tagId=&posi=&callback=jsonp1281").toString();
	}


	
	
}
