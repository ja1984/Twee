package se.ja1984.twee;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import se.ja1984.twee.utils.Utils;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    String theme = prefs.getString("pref_theme", "2");
	    setTheme(Utils.GetTheme(Integer.parseInt(theme)));
		
		super.onCreate(savedInstanceState);
	}

}