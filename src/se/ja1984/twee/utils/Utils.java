package se.ja1984.twee.utils;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import se.ja1984.twee.HomeActivity;
import se.ja1984.twee.R;

public class Utils {
	private Utils() {};


	public static int selectedProfile;
	public static int activeTheme;
	public static int PreferedSortOrder;
	public static int ShowShows;
	public static Boolean ignoreTheWhenOrder;
	public static Boolean useLocalizedTimezone;
	
	public static int GetTheme(int pref_theme){
		switch (pref_theme) {
		case 0:
			activeTheme = R.style.Dark;
			break;
		case 1:
			activeTheme = R.style.LightDark;
			break;
		case 2:
			activeTheme = R.style.Light;
			break;
		default:
			break;
		}
		return activeTheme;		
	}
	

	
	
	public static Boolean StoreOnExternal(){
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state))
			return true;

		return false;
	}
}
