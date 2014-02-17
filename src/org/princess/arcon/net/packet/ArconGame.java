package org.princess.arcon.net.packet;

public enum ArconGame {
	EnemyTerritory("Wolfenstein ET", "ÿÿÿÿrcon");

	private String gameName;
	private String PREFIX;

	private ArconGame(String gameName, String PREFIX) {
		this.gameName = gameName;
		this.PREFIX = PREFIX;
	}

	public String getName() {
		return gameName;
	}

	public String getPrefix() {
		return PREFIX;
	}
}
