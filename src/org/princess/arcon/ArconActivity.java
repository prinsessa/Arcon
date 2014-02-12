package org.princess.arcon;

import java.io.IOException;
import org.princess.arcon.net.ArconReceive;
import org.princess.arcon.net.Arcon;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

/**
 * 
 * ArconActivity
 * 
 * @author Tessa "Princess"
 * @version 0.2.2, 2/12/2014
 * 
 * */
public class ArconActivity extends Activity {

	private Arcon asock;
	private ArconReceive arec;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arcon);
		// Show the Up button in the action bar.
		setupActionBar();

		// Get the message from the intent
		Intent intent = getIntent();
		String server = intent.getStringExtra(MainActivity.SERVER_INFO);
		String port = intent.getStringExtra(MainActivity.PORT_INFO);
		String password = intent.getStringExtra(MainActivity.PASS_INFO);

		asock = new Arcon(server, Integer.parseInt(port), password);
		arec = new ArconReceive(asock.getSocket(),
				(TextView) findViewById(R.id.editText5));
		arec.execute();
	}

	// Let's not leave a mess (only close on finish! Pressing Home
	// leaves the socket open as the user might return to this activity.)
	public void finish() {
		closeSocket();
		super.finish();
	}

	// Hello? Hello? We're closed! It's soo dark in here...
	private void closeSocket() {
		if (asock.close()) {
			Toast.makeText(getApplicationContext(), "Closing Connection...",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.arcon, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Sends a command (onClick on button1) and updates the GUI if it doesn't
	// fail to do so.
	public void sendCommand(View view) {
		EditText commandText = (EditText) findViewById(R.id.editText4);
		TextView textView = (TextView) findViewById(R.id.editText5);
		try {
			textView.setText("");
			asock.sendCommand(commandText.getText().toString());
			commandText.setText("");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
