package org.princess.arcon.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 
 * Simple example class for sending rcon commands to an Enemy Territory server.
 * 
 * @author Tessa "Princess"
 * @version 0.2.2, 2/12/2014
 * */
public class Arcon {

	// server stuff
	private DatagramSocket socket;
	private InetAddress address;
	private int port;
	private final int TIMEOUT = 5000;
	private final int PACKETSIZE = 256; // we don't need larger packets

	/* command stuff
	 * 
	 * In sv_main.c of the Enemy Territory SC it says 
	 * "A connectionless packet has four leading 0xff characters to distinguish it from a game channel."
	 * */
	private final String PREFIX = "ÿÿÿÿrcon";
	private final String PREFIX_PRINT = "ÿÿÿÿprint";
	private String rconPassword;

	// more stuff
	public Arcon(String address, int port, String rconPassword) {
		try {
			socket = new DatagramSocket();
			this.address = InetAddress.getByName(address);
			this.port = port;
			this.rconPassword = rconPassword;

			socket.setSoTimeout(TIMEOUT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendCommands(String... commands) throws IOException,
			InterruptedException {
		for (String message : commands) {
			sendCommand(message);
		}
	}

	public void sendCommand(String command) throws IOException,
			InterruptedException {
		String message = String.format("%s %s %s", PREFIX, rconPassword,
				command);
		// must be set explicitly to "ISO-8859-1" or we'll run into problems sending or receiving...
		byte[] buffer = message.getBytes("ISO-8859-1");
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
				address, port);

		socket.send(packet);
		log(String.format("Command \"%s\" sent!", command));
	}

	// kill it with fire!
	public boolean close() {
		if (!socket.isClosed() || socket.isConnected()) {
			socket.close();
			return true;
		}
		return false;
	}

	public DatagramSocket getSocket() {
		return socket;
	}

	// log for console debug print
	public void log(String message) {
		System.out.printf(">log: %s\n", message);
	}

	public boolean canConnect() {

		byte[] buf = new byte[PACKETSIZE];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);

		try {
			sendCommand("status");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		String received = "";
		try {
			// must be set explicitly to "ISO-8859-1" or we'll run into problems sending or receiving...
			received = new String(packet.getData(), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (received.startsWith(PREFIX_PRINT)) {
			return true;
		}
		return false;
	}

}
