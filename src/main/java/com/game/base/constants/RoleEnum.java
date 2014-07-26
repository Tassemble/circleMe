package com.game.base.constants;

public enum RoleEnum {

	BOARD_MASTER("board_master"), ADMIN("admin"), MEMBER("member");

	private String name;

	private RoleEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
