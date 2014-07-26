package com.game.base.dto;

public class TmallProductDto {

	/**
	 * 天猫上的productId
	 */
	private Long	productId;

	private Double	price;
	/**
	 * startTime & endTime 是空 说明是没有优惠 eg:限时特惠
	 */
	private String	promotionType;
	private Long	promotionStartTime;
	private Long	promotionEndTime;
	/**
	 * 促销价格
	 */
	private Double	promotionPrice;

	/**
	 * 是否包邮
	 */
	private Boolean	postageFree	= false;
	
	public TmallProductDto(Long productId) {
		super();
		this.productId = productId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getPromotionType() {
		return promotionType;
	}

	public void setPromotionType(String promotionType) {
		this.promotionType = promotionType;
	}

	public Long getPromotionStartTime() {
		return promotionStartTime;
	}

	public void setPromotionStartTime(Long promotionStartTime) {
		this.promotionStartTime = promotionStartTime;
	}

	public Long getPromotionEndTime() {
		return promotionEndTime;
	}

	public void setPromotionEndTime(Long promotionEndTime) {
		this.promotionEndTime = promotionEndTime;
	}

	public Double getPromotionPrice() {
		return promotionPrice;
	}

	public void setPromotionPrice(Double promotionPrice) {
		this.promotionPrice = promotionPrice;
	}

	public Boolean getPostageFree() {
		return postageFree;
	}

	public void setPostageFree(Boolean postageFree) {
		this.postageFree = postageFree;
	}

	public String getProductInfoUrl() {
		return "http://mdskip.taobao.com/core/initItemDetail.htm?itemId=" + productId;
	}

	public String getProductReferer() {
		return "http://detail.tmall.com/item.htm?id=" + productId;
	}
}
