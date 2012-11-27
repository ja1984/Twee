package se.ja1984.twee;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import se.ja1984.twee.R;

public class PreferenceFragm extends PreferenceFragment    {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preference);

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}


}