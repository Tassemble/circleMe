package com.game.utils;

import java.text.DecimalFormat;
import java.util.List;

import net.sf.json.JSONObject;

import com.game.base.dto.GatherEffect;
import com.game.base.dto.ProductEvaluationDto;
import com.google.gson.reflect.TypeToken;

public class ProductScoreUtils {
	static DecimalFormat	decimalFormat	= new DecimalFormat("0.0");
	static final int BAD_RATIO = 3;
	static final int GOOD_RATIO = 2;
	
	public static final String DEFAULT_SCORE = "7.0";
	/**
	 * 总体得分
	 * 
	 * @param dto
	 * @return
	 */
	public static String getAvarageScore(List<ProductEvaluationDto> dto) {
		float good = 0.0f;
		float bad = 0.0f;
		for (ProductEvaluationDto eva : dto) {
			if (eva.isPosi()) {
				good += eva.getCount() * GOOD_RATIO;
			} else {
				bad += eva.getCount() * BAD_RATIO;
			}
		}
		if (good + bad <= 0) {
			return DEFAULT_SCORE;
		} else {
			float result = 10 * good / (good + bad);
			return decimalFormat.format(result);
		}
	}

	/**
	 * 款式得分
	 * 
	 * @param dto
	 * @return
	 */
	public static String getStyleScore(List<ProductEvaluationDto> dto) {
		ProductEvaluationDto goodOne = ProductEvaluationDto.getById(dto, ProductEvaluationDto.STYLE_ID, true);
		ProductEvaluationDto badOne = ProductEvaluationDto.getById(dto, ProductEvaluationDto.STYLE_ID, false);
		if (goodOne == null || badOne == null) {
			
			//暂未评分
			return "8.0";
		}
		
		float good = goodOne.getCount() * GOOD_RATIO;
		float bad = badOne.getCount() * BAD_RATIO;
		float result = 10 * good / (good + bad);
		return decimalFormat.format(result);
	}

	/**
	 * 性价比 得分
	 * 
	 * @param dto
	 * @return
	 */
	public static String getCostValueScore(List<ProductEvaluationDto> dto) {
		ProductEvaluationDto goodOne = ProductEvaluationDto.getById(dto, ProductEvaluationDto.COST_RATIO_ID, true);
		ProductEvaluationDto badOne = ProductEvaluationDto.getById(dto, ProductEvaluationDto.COST_RATIO_ID, false);
		if (goodOne == null || badOne == null) {
			
			//暂未评分
			return "8.0";
		}
		float good = goodOne.getCount() * GOOD_RATIO;
		float bad = badOne.getCount() * BAD_RATIO;
		float result = 10 * good / (good + bad);
		return decimalFormat.format(result);
	}

	/**
	 * 聚拢效果得分
	 * 
	 * @param gatherEffects
	 * @return
	 */
	public static String getGatherScore(List<GatherEffect> gatherEffects) {
		GatherEffect goodEffect = GatherEffect.chooseByTag(gatherEffects, GatherEffect.TAG_GATHER_GOOD);
		GatherEffect badEffect = GatherEffect.chooseByTag(gatherEffects, GatherEffect.TAG_GATHER_BAD);
		GatherEffect commonEffect = GatherEffect.chooseByTag(gatherEffects, GatherEffect.TAG_GATHER_COMMON);
		if (goodEffect == null || badEffect == null || commonEffect == null) {
			
			//暂未评分
			return "8.0";
		}
		// （2*聚拢效果好+聚拢效果一般）/（2*聚拢效果好+聚拢效果一般+不聚拢*3）
		float good = (goodEffect.getCount() * GOOD_RATIO + commonEffect.getCount());
		float bad = badEffect.getCount() * BAD_RATIO;
		DecimalFormat df = new DecimalFormat("#.#");
		float result = 10 * good / (good + bad);
		return df.format(result);
	}

	public static void main(String[] args) {
	String json = "{\"tags\":{\"dimenSum\":13,\"innerTagCloudList\":[{\"dimenName\":\"尺码\",\"tagScaleList\":[{\"count\":49,\"proportion\":11.0,\"scale\":\"偏小\"},{\"count\":368,\"proportion\":83.0,\"scale\":\"适合\"},{\"count\":29,\"proportion\":6.0,\"scale\":\"偏大\"}],\"total\":446},{\"dimenName\":\"聚拢效果\",\"tagScaleList\":[{\"count\":36,\"proportion\":10.0,\"scale\":\"不聚拢\"},{\"count\":178,\"proportion\":48.0,\"scale\":\"一般\"},{\"count\":154,\"proportion\":42.0,\"scale\":\"好\"}],\"total\":368}],\"rateSum\":820,\"tagClouds\":[{\"count\":240,\"id\":\"55\",\"posi\":true,\"tag\":\"整体感觉不错 \"},{\"count\":89,\"id\":\"1408\",\"posi\":true,\"tag\":\"材质不错\"},{\"count\":70,\"id\":\"1432\",\"posi\":false,\"tag\":\"聚拢效果一般\"},{\"count\":49,\"id\":\"1515\",\"posi\":true,\"tag\":\"颜色漂亮\"},{\"count\":49,\"id\":\"48\",\"posi\":true,\"tag\":\"手感很舒服 \"},{\"count\":48,\"id\":\"1423\",\"posi\":true,\"tag\":\"尺码很正\"},{\"count\":45,\"id\":\"1423\",\"posi\":false,\"tag\":\"要留意尺码问题\"},{\"count\":41,\"id\":\"57\",\"posi\":true,\"tag\":\"性价比很高 \"},{\"count\":39,\"id\":\"46\",\"posi\":true,\"tag\":\"款式很漂亮 \"},{\"count\":35,\"id\":\"1432\",\"posi\":true,\"tag\":\"聚拢效果好\"},{\"count\":32,\"id\":\"1522\",\"posi\":true,\"tag\":\"包装不错\"},{\"count\":11,\"id\":\"55\",\"posi\":false,\"tag\":\"质量一般般 \"},{\"count\":10,\"id\":\"1436\",\"posi\":false,\"tag\":\"不太有弹性\"},{\"count\":10,\"id\":\"45\",\"posi\":true,\"tag\":\"做工非常好 \"},{\"count\":10,\"id\":\"1408\",\"posi\":false,\"tag\":\"料子一般\"},{\"count\":7,\"id\":\"46\",\"posi\":false,\"tag\":\"款式一般般 \"},{\"count\":7,\"id\":\"51\",\"posi\":false,\"tag\":\"不耐洗 \"},{\"count\":6,\"id\":\"57\",\"posi\":false,\"tag\":\"性价比一般 \"},{\"count\":6,\"id\":\"48\",\"posi\":false,\"tag\":\"手感一般 \"},{\"count\":5,\"id\":\"1515\",\"posi\":false,\"tag\":\"颜色一般\"},{\"count\":4,\"id\":\"45\",\"posi\":false,\"tag\":\"细节处理不够 \"},{\"count\":4,\"id\":\"51\",\"posi\":true,\"tag\":\"不会褪色 \"},{\"count\":2,\"id\":\"49\",\"posi\":true,\"tag\":\"感觉挺透气 \"},{\"count\":1,\"id\":\"1522\",\"posi\":false,\"tag\":\"包装一般\"}]}}";
//		JSONObject jsonObject = JSONObject.fromObject(json);
//		List<GatherEffect> effects = WordPressUtils.fromJson(jsonObject.getString("tagScaleList"),
//				new TypeToken<List<GatherEffect>>() {
//				}.getType());
//		System.out.println(getGatherScore(effects));
		
		
		JSONObject jsonObject = JSONObject.fromObject(json);
		String tagClouds = jsonObject.getJSONObject("tags").getString("tagClouds");

		List<ProductEvaluationDto> dtos = GsonUtils.fromJson(tagClouds,
				new TypeToken<List<ProductEvaluationDto>>() {
				}.getType());
		
		System.out.println(getAvarageScore(dtos));
		System.out.println(getCostValueScore(dtos));
		System.out.println(getStyleScore(dtos));
	}
}
