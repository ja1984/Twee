package se.ja1984.twee;

import android.os.Bundle;
import android.preference.PreferenceFragment;
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
