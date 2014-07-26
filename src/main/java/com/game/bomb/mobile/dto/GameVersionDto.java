package com.game.bomb.mobile.dto;

public class GameVersionDto {

	String	lowestAvailableVersion	= "1.0";
	String	highestVersion			= "1.0";
	String action;
	
	
	
	

	public GameVersionDto(String action) {
		super();
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getLowestAvailableVersion() {
		return lowestAvailableVersion;
	}

	public void setLowestAvailableVersion(String lowestAvailableVersion) {
		this.lowestAvailableVersion = lowestAvailableVersion;
	}

	public String getHighestVersion() {
		return highestVersion;
	}

	public void setHighestVersion(String highestVersion) {
		this.highestVersion = highestVersion;
	}

}
