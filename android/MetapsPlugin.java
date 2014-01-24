package com.tealeaf.plugin.plugins;

import com.tealeaf.*;
import com.tealeaf.plugin.IPlugin;
import com.tealeaf.event.Event;
import com.tealeaf.logger;

import java.io.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.metaps.sdk.*;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class MetapsPlugin implements IPlugin {
	private Activity mActivity;
	private boolean mConsumeBackPressed;

	private String userID = "";
	private String appID = "";
	private String appKey = "";

	public MetapsPlugin() {}
	public void onCreateApplication(Context applicationContext) {}

	public void onCreate(Activity activity, Bundle savedInstanceState) {
		mActivity = activity;

		try {
			String readLine;
			String manifestString = "";
			InputStream is = mActivity.getAssets().open("resources/manifest.json");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			while ((readLine = br.readLine()) != null) {
				manifestString += readLine;
			}
			br.close();

			JSONObject manifest = new JSONObject(manifestString);
			JSONObject android = manifest.getJSONObject("android");
			JSONObject metaps = android.getJSONObject("metaps");
			userID = metaps.getString("userID");
			appID = metaps.getString("appID");
			appKey = metaps.getString("appKey");

			logger.log("~~~ metaps", "Successfully read manifest, keys are:");
			logger.log("~~~ metaps", userID);
			logger.log("~~~ metaps", appID);
			logger.log("~~~ metaps", appKey);
		} catch (Exception e) {
			logger.log("~~~ metaps", "Unable to read userID, appID, and appKey from manifest.");
		}

		new Thread(new Runnable() {
			public void run() {

				try {
					long a = System.currentTimeMillis();
					Factory.sendAction(mActivity, userID, appID, appKey, net.metaps.sdk.Const.SDK_MODE_PRODUCTION);
					logger.log("~~~ metaps", "Successfully called sendAction.");
				} catch(InvalidSettingException e) {
					logger.log("~~~ metaps", "Failed calling sendAction.");
					logger.log("~~~ metaps", "InvalidSettingException!");
					logger.log("~~~ metaps", e.getMessage());
				} catch(Exception e) {
					logger.log("~~~ metaps", "Failed calling sendAction.");
					logger.log("~~~ metaps", e.getMessage());
					// Exception Handling Note
					// Factory.sendAction can throw an exception
					// If an exception is thrown, please use retry logic
					// The following exception classes can be thrown
					//    [InvalidSettingException] : Settings are incorrect
					//    [ServerConnectionException] : Retry logic
					//    [DeviceInfoException] : Using a unsupported device
				}

			}
		}).start();
	}

	public void onResume() {}
	public void onStart() {}
	public void onPause() {}
	public void onStop() {}
	public void onDestroy() {}
	public void onNewIntent(Intent intent) {}
	public void setInstallReferrer(String referrer) {}
	public void onActivityResult(Integer request, Integer result, Intent data) {}

	public boolean consumeOnBackPressed() {
		return mConsumeBackPressed;
	}

	public void onBackPressed() {}
}
