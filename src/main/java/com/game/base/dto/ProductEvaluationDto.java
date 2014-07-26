package com.game.base.dto;

import java.util.List;

public class ProductEvaluationDto {
	

	public static final String TAG_ALL_PRODUCT_EVALUATION = "product_evaluation";
	public static final String TAG_AVARAGE_SCORE = "average_score";
	public static final String TAG_STYLE_SCORE = "style_score";
	public static final String TAG_COST_VALUE_SCORE = "cost_value_score";
	
	

	public static final long STYLE_ID = 46L;
	public static final long COST_RATIO_ID = 57L;

	
	long	count	= 0L;
	long	id;
	boolean	posi	= true;
	String	tag;

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isPosi() {
		return posi;
	}

	public void setPosi(boolean posi) {
		this.posi = posi;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	
	public static ProductEvaluationDto getById(List<ProductEvaluationDto> dtos, long id, boolean posi) {
		for (ProductEvaluationDto dto : dtos) {
			if (dto.getId() == id && dto.isPosi() == posi) {
				return dto;
			}
		}
		return null;
	}
	
}
