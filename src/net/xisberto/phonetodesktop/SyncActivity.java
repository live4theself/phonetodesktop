package net.xisberto.phonetodesktop;

import android.app.Dialog;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.TasksScopes;

public abstract class SyncActivity extends SherlockFragmentActivity {

	public static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;

	public static final int REQUEST_AUTHORIZATION = 1;

	public static final int REQUEST_ACCOUNT_PICKER = 2;
	
	public static String TAG = "";

	public Preferences preferences;
	
	protected GoogleAccountCredential credential;

	public com.google.api.services.tasks.Tasks client;

	private HttpTransport transport = AndroidHttp.newCompatibleTransport();

	private JsonFactory jsonFactory = new GsonFactory();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TAG = getPackageName();

		preferences = new Preferences(this);
		
		credential = GoogleAccountCredential.usingOAuth2(this,
				TasksScopes.TASKS);
		credential.setSelectedAccountName(preferences.loadAccountName());
		
		client = new com.google.api.services.tasks.Tasks.Builder(
				transport, jsonFactory, credential).setApplicationName(
				"PhoneToDesktop").build();
	}

	public void showGooglePlayServicesAvailabilityErrorDialog(
			final int connectionStatusCode) {
		runOnUiThread(new Runnable() {
			public void run() {
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
						connectionStatusCode, SyncActivity.this,
						REQUEST_GOOGLE_PLAY_SERVICES);
				dialog.show();
			}
		});
	}
	
	public abstract void refreshView();
}