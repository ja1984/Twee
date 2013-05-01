package se.ja1984.twee;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import se.ja1984.twee.R;
import se.ja1984.twee.utils.Utils;

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

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

		if(preference.getKey() != null){
			if(preference.getKey().equals("pref_localizedtime")){
				Utils.useLocalizedTimezone = !Utils.useLocalizedTimezone;
			}
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}


}
