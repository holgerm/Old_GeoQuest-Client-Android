package com.qeevee.gq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import android.content.res.AssetManager;
import android.util.Log;
import edu.bonn.mobilegaming.geoquest.GeoQuestApp;

public class Configuration {

	private static final String CONFIG_FILE = "gq.conf";
	private static final String CONFIG_COMMENT = "//";
	private static final String CONFIG_KEY_VALUE_DELIMITERS = ":=";

	private static final String TAG = Configuration.class.getCanonicalName();

	private static Map<String, String> configs;

	public static final String CONFIG_AUTOSTART = "autostartquest";

	public static void initialize() {
		configs = new HashMap<String, String>();
		AssetManager assetManager = GeoQuestApp.getContext().getAssets();
		String line = null;
		try {
			InputStream is = assetManager.open(CONFIG_FILE);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			do {
				line = reader.readLine();
				if (line != null)
					addConfig(line);
			} while (line != null);
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void addConfig(String line) {
		if (line.startsWith(CONFIG_COMMENT))
			return;
		StringTokenizer tokenizer = new StringTokenizer(line,
				CONFIG_KEY_VALUE_DELIMITERS);
		if (tokenizer.countTokens() < 2)
			return;
		try {
			configs.put(tokenizer.nextToken().trim(), tokenizer.nextToken()
					.trim());
		} catch (NoSuchElementException noTokenException) {
			Log.w(TAG,
					"Config line not readable. "
							+ noTokenException.getMessage());
			return;
		}
	}

	public static String get(String configKey) {
		return configs.get(configKey);
	}

}
