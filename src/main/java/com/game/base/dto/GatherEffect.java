package com.game.base.dto;

import java.util.List;

import org.springframework.util.CollectionUtils;

public class GatherEffect {
	
	public static final String TAG_GATHER_SCORE = "tag_gather_score";
	public static final String TAG_GATHER = "tag_gather";
	public static final String TAG_GATHER_GOOD = "好";
	public static final String TAG_GATHER_BAD = "不聚拢";
	public static final String TAG_GATHER_COMMON = "一般";
	
	
	long	count		= 0;
	int		proportion	= 0;
	String	scale;

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public int getProportion() {
		return proportion;
	}

	public void setProportion(int proportion) {
		this.proportion = proportion;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}
	
	
	public static GatherEffect chooseByTag(List<GatherEffect> effects, String tag) {
		if (!CollectionUtils.isEmpty(effects)) {
			for (GatherEffect gatherEffect : effects) {
				if (gatherEffect.getScale().equals(tag)) {
					return gatherEffect;
				}
			}
		}
		return null;
	}
	
	

}
