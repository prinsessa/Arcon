package org.princess.arcon.net.packet;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class ArconPacket {

	private ArconGame game;
	private InetAddress address;
	private int port;
	private String rconPassword;

	public ArconPacket(ArconGame game, InetAddress address, int port,
			String rconPassword) {
		this.game = game;
		this.address = address;
		this.port = port;
		this.rconPassword = rconPassword;
	}

	public DatagramPacket buildPacket(String command)
			throws UnsupportedEncodingException {
		String message = String.format("%s %s %s", game.getPrefix(),
				rconPassword, command);
		// must be set explicitly to "ISO-8859-1" or we'll run into problems
		// sending or receiving...
		byte[] buffer = message.getBytes("ISO-8859-1");
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
				address, port);

		return packet;
	}
}
