package org.princess.arcon.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import android.os.AsyncTask;
import android.widget.TextView;

/**
 * 
 * Simple example class for receiving information about previous send commands.
 * 
 * @author Tessa "Princess"
 * @version 0.3.2, 2/12/2014
 * 
 * */
public class ArconReceive extends AsyncTask<Void, String, Void> {

	// server stuff
	private DatagramSocket socket;
	private TextView tView;
	private final int PACKETSIZE = 256; // we don't need larger packets
	private final String PREFIX_PRINT = "ÿÿÿÿprint";

	public ArconReceive(DatagramSocket socket, TextView tView) {
		this.tView = tView;
		this.socket = socket;
	}

	// let our background task run indefinitely until our socket is closed 
	// (after socket.receive() fails blocking on a closed socket)
	private void run() {
		while (!socket.isClosed()) {
			try {
				receiveCommand();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void receiveCommand() throws IOException, InterruptedException {
		byte[] buf = new byte[PACKETSIZE];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);

		try {
			socket.receive(packet);
			String received = "";
			try {
				// must be set explicitly to "ISO-8859-1" or we'll run into problems sending or receiving...
				received = new String(packet.getData(), "ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			// clean up the mess before updating our textview
			received = received.replaceFirst(PREFIX_PRINT, "");
			received = received.trim();
			publishProgress(received + "\n\n");
		} catch (SocketTimeoutException e) {
			// we do nothing
		}

	}

	protected void onProgressUpdate(String... message) {
		for (String m : message) {
			tView.append(m);
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		run();
		return null;
	}

}
