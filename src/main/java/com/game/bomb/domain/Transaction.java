package com.game.bomb.domain;

import com.game.base.commons.domain.BaseDo;
import com.game.bomb.constant.ProductType;
import com.netease.framework.dao.sql.annotation.DataProperty;

public class Transaction extends BaseDo {

	/**
	 * 
	 */
	private static final long	serialVersionUID		= -8262623129130110008L;

	public static final String	PRODUCT_TYPE_10_INGOTS	= ProductType.PRODUCT_TYPE_10_INGOTS.getName();
	public static final String	PRODUCT_TYPE_25_INGOTS	= ProductType.PRODUCT_TYPE_25_INGOTS.getName();
	public static final String	PRODUCT_TYPE_60_INGOTS	= ProductType.PRODUCT_TYPE_60_INGOTS.getName();
	public static final String	PRODUCT_TYPE_350_INGOTS	= ProductType.PRODUCT_TYPE_350_INGOTS.getName();
	public static final String	PRODUCT_TYPE_150_INGOTS	= ProductType.PRODUCT_TYPE_150_INGOTS.getName();

	Long						uid;

	String						productId;

	Integer						quantity;

	String						transactionId;

	Long						purchaseDateMs;

	String						uniqueIdentifier;

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	@DataProperty(column = "product_id")
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@DataProperty(column = "transaction_id")
	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	@DataProperty(column = "purchase_date_ms")
	public Long getPurchaseDateMs() {
		return purchaseDateMs;
	}

	public void setPurchaseDateMs(Long purchaseDateMs) {
		this.purchaseDateMs = purchaseDateMs;
	}

	@DataProperty(column = "unique_identifier")
	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}

}
