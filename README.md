# Game Closure DevKit Plugin: Metaps

This plugin adds advertising support from the [Metaps service](http://www.metaps.com) for Android.

## Usage

Install the addon with `basil install metaps`.

Include it in the `manifest.json` file under the "addons" section for your game:

~~~
"addons": [
	"metaps"
],
~~~

Under the Android section, you can configure the Metaps plugin:

~~~
	"android": {
		"versionCode": 1,
		"icons": {
			"36": "resources/icons/android36.png",
			"48": "resources/icons/android48.png",
			"72": "resources/icons/android72.png",
			"96": "resources/icons/android96.png"
		},
		"metaps": {
			"userID": "IZEBWMZQHZ0101",
			"appID": "BXMTHFAVAA0101",
			"appKey": "y17yllge9u123te"
		}
	},
~~~

Note that the key names are case-sensitive.

You can test for successful integration on the [Metaps website](http://www.metaps.com).