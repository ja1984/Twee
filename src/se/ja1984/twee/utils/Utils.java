package se.ja1984.twee.utils;

import android.os.Environment;
import android.util.Log;
import se.ja1984.twee.R;

public class Utils {
	private Utils() {};


	public static int selectedProfile;


	public static int GetTheme(int pref_theme){
		switch (pref_theme) {
		case 0:
			return R.style.Dark;
		case 1:
			return R.style.LightDark;
		case 2:
			return R.style.Light;
		default:
			return R.style.Light;
		}
	}
	

	public static Boolean StoreOnExternal(){
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state))
			return true;

		return false;
	}
}
