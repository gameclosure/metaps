package com.tealeaf.plugin.plugins;

import com.tealeaf.*;
import com.tealeaf.plugin.IPlugin;
import com.tealeaf.event.Event;

import java.io.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.metaps.sdk.*;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.os.Bundle;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class MetapsPlugin implements IPlugin {
	private Activity mActivity;
	private boolean mConsumeBackPressed;
	private boolean bInitialized = false;

	private class MetapsEvent extends Event {
		public int points = 0;

		public MetapsEvent(int points) {
			super("MetapsPoints");
			this.points = points;
		}
	}

	private class MetapsReceiver implements Receiver {
		public boolean retrieve(int points, Offer offer) {
			EventQueue.pushEvent(new MetapsEvent(points));
			return true;
		}

		public boolean finalizeOnError(Offer offer) {
			return true;
		}
	}

	public MetapsPlugin() {}
	public void onCreateApplication(Context applicationContext) {}

	public void onCreate(Activity activity, Bundle savedInstanceState) {
		mActivity = activity;
	}

	public void onResume() {
		if (!bInitialized) {
			bInitialized = true;

			String userID = "";
			String appID = "";
			String appKey = "";

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
			} catch (Exception e) {
				Log.d("metaps", "Unable to read userID, appID, and appKey from manifest");
			}

			try {
				Receiver receiver = new MetapsReceiver();
				Factory.initialize(
					mActivity,
					receiver,
					userID,
					appID,
					appKey,
					net.metaps.sdk.Const.SDK_MODE_TEST
				);
			} catch(Exception e) {
				// Exception Handling Note
				// Factory.initialize can throw an exception
				// If an exception is thrown, please use retry logic
				// The following exception classes can be thrown
				// [InvalidSettingException] : Settings are incorrect
				// [ServerConnectionException] : Retry logic
				// [DeviceInfoException] : Using an unsupported device
			}
		}
	}

	public void showOfferwall() {
		String playerID = Device.getDeviceID(mActivity, TeaLeaf.get().getSettings());
		String scenario = "";

		try {
			Factory.launchOfferWall(mActivity, playerID, scenario);
		} catch(Exception e) {}
	}

	public void showFeaturedApp() {
		String playerID = Device.getDeviceID(mActivity, TeaLeaf.get().getSettings());
		String scenario = "";

		try {
			FeaturedAppDialog.prepare(playerID, scenario);
			FeaturedAppDialog.show(mActivity, null);
		} catch(Exception e) {
			// Exception Handling Note
			// FeaturedAppDialog.prepare / FeaturedAppDialog.show
			// can throw an exception
			// The following exception classes can be thrown
			// [UninitializedException]： Initialization not performed
		}
	}

	public void checkOffers() {
		try {
			Factory.runInstallReport();
		} catch(Exception e) {}
	}

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