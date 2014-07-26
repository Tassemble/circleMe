package com.game.bomb.constant;

import java.util.HashMap;

import com.game.bomb.mobile.dto.DayAward;

public class BombConstant {

	public static final int							NOT_ENOUGH_HEART_CODE			= 99;	// 20个换取1个红心

	public static final int							EXCHANGE_INGOT_TO_HEART_UNIT	= 20;	// 20个换取1个红心
	
	
	public static final int							AIAWARD_AFTER_WIN	= 5; // 5 gold
	
	
	
	
	//元宝换取红心
	public static final HashMap<Integer, Integer>	EXCHANGE_INGOT_TO_HEART_MAPPING								= new HashMap<Integer, Integer>()
    {
		private static final long	serialVersionUID	= -3703929355568521604L;
		{
			put(10, 30);
			put(20, 70);
			put(30, 110);
		}
	};
	
	
	
	//元宝换金币
	public static final HashMap<Integer, Integer>	EXCHANGE_INGOT_TO_GOLD_MAPPING								= new HashMap<Integer, Integer>()
		    {
				private static final long	serialVersionUID	= -3703929355568521604L;
				{
					put(2, 20);
					put(4, 50);
					put(6, 120);
					put(15, 300);
				}
			};
			
		
	//每日奖品
	// 第一次打开红心15+金币5 第二天红心6+金币10 第三天红心7+金币15
	// 第四天红心20+金币20 第五天红心9+金币40 第六天红心10+金币60 第7天红心11+金币80
	public static final HashMap<Integer, DayAward>	EVERYDAY_AWARDS	= new HashMap<Integer, DayAward>()
		    {
		
		
				private static final long	serialVersionUID	= -3703929355568521604L;
				{
					put(1, new DayAward(15, 5));
					put(2, new DayAward(6, 10));
					put(3, new DayAward(7, 15));
					put(4, new DayAward(8, 20));
					put(5, new DayAward(9, 40));
					put(6, new DayAward(10, 60));
					put(7, new DayAward(11, 80));
				}
			};
			
			

			//上线的时候要注意
	public static final int	CONSTANT_FULL_HEART	= 6;
			
			

}
