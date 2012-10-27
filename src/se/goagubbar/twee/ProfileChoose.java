package se.goagubbar.twee;

import java.util.HashMap;
import java.util.Map.Entry;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ProfileChoose extends Activity {

	private int selectedProfile;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		SharedPreferences settings = getSharedPreferences("Twee", 0);
	    selectedProfile = settings.getInt("Profile", 1);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_profilechoose);
		RadioGroup radioGroup = (RadioGroup)findViewById(R.id.rdgProfiles);

		HashMap<Integer,String> profiles = new DatabaseHandler(this).GetAllprofiles(); 
		
		Log.d("sa", "" + profiles.size());
		
		for (Entry<Integer, String> entry : profiles.entrySet()) {
			RadioButton radioButton = new RadioButton(this);
			radioButton.setText(entry.getValue());
			radioButton.setId(entry.getKey());		
			radioGroup.addView(radioButton);
		}
		
		radioGroup.check(selectedProfile);
		

		Button btnAddNewProfile = (Button)findViewById(R.id.btnAddProfile);
		
		btnAddNewProfile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new DatabaseHandler(ProfileChoose.this).AddNewProfile("Jonathan");
			}
		});
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_profilechoose, menu);
		return true;
	}
}
