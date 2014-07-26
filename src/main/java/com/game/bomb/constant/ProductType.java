package com.game.bomb.constant;

public enum ProductType {

	//10_ingots 25_ingots 60_ingots 350_ingots 150_ingots
	PRODUCT_TYPE_10_INGOTS("10_ingots", 10),
	PRODUCT_TYPE_25_INGOTS("25_ingots", 25), 
	PRODUCT_TYPE_60_INGOTS("60_ingots", 60),
	PRODUCT_TYPE_350_INGOTS("350_ingots", 350),
	PRODUCT_TYPE_150_INGOTS("150_ingots", 150);

	private String name;
	private Integer value;

	private ProductType(String name, Integer value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/*
	 * 获得类型元宝的数量
	 */
	public static long getQuantityByProductType(String name) {
		for (ProductType productType : ProductType.values()) {
			if (productType.getName().equals(name)) {
				return productType.getValue();
			}
		}
		return 0;
	}
	
	

}
