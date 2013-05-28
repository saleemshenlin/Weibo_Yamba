package com.example.weibo_yamba;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PrefsActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Add a button to the header list
		addPreferencesFromResource(R.xml.prefs);
	}

}
