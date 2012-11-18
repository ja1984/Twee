package se.goagubbar.twee;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences settings = getSharedPreferences("Twee", 0);
	    int theme = settings.getInt("Theme", R.style.Light);
	    
		setTheme(theme);
		
		super.onCreate(savedInstanceState);
	}

}