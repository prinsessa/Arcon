package org.princess.arcon;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.princess.arcon.net.Arcon;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * 
 * Login Activity
 * 
 * @author Tessa "Princess"
 * @version 0.3.2, 2/12/2014
 * 
 * */
public class MainActivity extends Activity {

	// package scope
	final static String SERVER_INFO = "org.princess.arcon.serverinfo";
	final static String PORT_INFO = "org.princess.arcon.portinfo";
	final static String PASS_INFO = "org.princess.arcon.passinfo";
	private static final String PREFS_NAME = "PREFSARCON";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter
				.createFromResource(this, R.array.games_array,
						android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		restoreSettings();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void restoreSettings() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		String server = settings.getString("SERVER", "");
		String port = settings.getString("PORT", "");
		String pass = settings.getString("PASS", "");
		boolean save = settings.getBoolean("SAVE", false);

		EditText serverText = (EditText) findViewById(R.id.editText5);
		EditText portText = (EditText) findViewById(R.id.editText4);
		EditText passwordText = (EditText) findViewById(R.id.editText3);
		CheckBox check = (CheckBox) findViewById(R.id.checkBox1);

		serverText.setText(server);
		portText.setText(port);
		passwordText.setText(pass);
		check.setChecked(save);
	}

	private void saveSettings(String server, String port, String pass,
			boolean save) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.putString("SERVER", server);
		editor.putString("PORT", port);
		editor.putString("PASS", pass);
		editor.putBoolean("SAVE", save);

		editor.commit();
	}

	// TODO: clean this up a bit more
	public void connectServer(View view) {
		Intent intent = new Intent(this, ArconActivity.class);

		EditText serverText = (EditText) findViewById(R.id.editText5);
		EditText portText = (EditText) findViewById(R.id.editText4);
		EditText passwordText = (EditText) findViewById(R.id.editText3);

		String server = serverText.getText().toString();
		String port = portText.getText().toString();
		String password = passwordText.getText().toString();

		// saviorself
		CheckBox check = (CheckBox) findViewById(R.id.checkBox1);
		if (check.isChecked()) {
			saveSettings(server, port, password, true);
		} else {
			saveSettings("", "", "", false);
		}

		intent.putExtra(SERVER_INFO, server);
		intent.putExtra(PORT_INFO, port);
		intent.putExtra(PASS_INFO, password);

		Arcon asock = null;

		if (validateServer(server, port, password)) {
			asock = new Arcon(server, Integer.parseInt(port), password);
			if (asock.canConnect()) {
				startActivity(intent);
			} else {
				Toast.makeText(
						getApplicationContext(),
						String.format("Unable to connect to: %s:%s", server,
								port), Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(
					getApplicationContext(),
					String.format(
							"The provided server information doesn't appear to be valid: %s:%s",
							server, port), Toast.LENGTH_SHORT).show();
		}
		if (asock != null) {
			asock.close();
		}
	}

	private boolean validateServer(String server, String port, String password) {
		if (port.isEmpty() || server.isEmpty() || password.isEmpty()) {
			return false;
		}
		try {
			InetAddress.getByName(server);
			Integer.parseInt(port);
		} catch (UnknownHostException e) {
			return false;
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
