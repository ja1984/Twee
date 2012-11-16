package se.goagubbar.twee;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import se.goagubbar.twee.dto.Backup;
import se.goagubbar.twee.utils.DatabaseHandler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends BaseActivity {

	SharedPreferences settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_settings);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		settings = getSharedPreferences("Twee", 0);
		int theme = settings.getInt("Theme", R.style.Light);
		int display = settings.getInt("Display", 0);
		Log.d("Display","" + display);
		boolean downloadHeader = settings.getBoolean("downloadHeaderImage", true);

		Button btnAddProfile = (Button)findViewById(R.id.btnAddProfile);


		if(display == 1)
		{
			RadioButton _display = (RadioButton)findViewById(R.id.rdoDisplay2);
			_display.setChecked(true);

		}


		RadioButton oldCheckedButton = null;

		if((int) theme == R.style.Light)
		{
			oldCheckedButton = (RadioButton)findViewById(R.id.radioTheme0);
		}
		else if(theme == R.style.LightDark)
		{
			oldCheckedButton = (RadioButton)findViewById(R.id.radioTheme1);
		}       
		else
		{
			oldCheckedButton = (RadioButton)findViewById(R.id.radioTheme2);
		}

		oldCheckedButton.setChecked(true);

		Button btnBackup = (Button)findViewById(R.id.btnBackup);
		btnBackup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CreateBackup();
			}
		});
		
		Button btnRestore = (Button)findViewById(R.id.btnRestore);
		btnRestore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RestoreBackup();
			}
		});
		

		btnAddProfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				LinearLayout layout = new LinearLayout(getApplicationContext());
				layout.setOrientation(LinearLayout.VERTICAL);

				//layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				final EditText txtProfileName = new EditText(getApplicationContext());
				
				final TextView txtInformation = new TextView(getApplicationContext());
				txtProfileName.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				txtInformation.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				txtInformation.setText(R.string.settings_description_profile_popup);

				layout.addView(txtInformation);
				layout.addView(txtProfileName);

				LayoutInflater inflater = getLayoutInflater();
				final View dialoglayout = inflater.inflate(R.layout.layout_addnewprofile, (ViewGroup) getCurrentFocus());
				
				AlertDialog.Builder addProfile = new AlertDialog.Builder(SettingsActivity.this);
				addProfile.setTitle(R.string.dialog_addprofile_header);
				
				addProfile.setView(dialoglayout);

				addProfile.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText profileName = (EditText)dialoglayout.findViewById(R.id.txtProfileName);
						if(!profileName.getText().toString().equals(""))
						{
							new DatabaseHandler(SettingsActivity.this).AddNewProfile(profileName.getText().toString());	
						}


					}
				});

				addProfile.setNegativeButton(R.string.delete_cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Do nothing	
					}
				});

				addProfile.create().show();


			}
		});


		RadioGroup radioGroup = (RadioGroup)findViewById(R.id.grpTheme);
		RadioGroup radioDisplay = (RadioGroup)findViewById(R.id.grpDisplay);
		final Switch switchHeader	 = (Switch)findViewById(R.id.switchHeader);

		switchHeader.setChecked(downloadHeader);

		switchHeader.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("downloadHeaderImage", switchHeader.isChecked());

				editor.commit();

			}
		});

		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			public void onCheckedChanged(RadioGroup rGroup, int checkedId)
			{

				RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(checkedId);
				boolean isChecked = checkedRadioButton.isChecked();
				if (isChecked)
				{
					saveTheme(checkedId);
				}
			}
		});


		radioDisplay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			public void onCheckedChanged(RadioGroup rGroup, int checkedId)
			{

				RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(checkedId);

				boolean isChecked = checkedRadioButton.isChecked();
				if (isChecked)
				{
					saveDisplayMode(checkedId);
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_settings, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveTheme(Integer selectedTheme)
	{

		int option = 0;

		if(selectedTheme == R.id.radioTheme0)
		{
			option = R.style.Light;
		}
		else if(selectedTheme == R.id.radioTheme1)
		{
			option = R.style.LightDark;
		}	
		else
		{
			option = R.style.Dark;
		}

		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("Theme", option);
		editor.commit();
	}

	private void saveDisplayMode(Integer selectId)
	{
		Log.d("Selected option","" + selectId);
		int option = 0;

		if(selectId == R.id.rdoDisplay2)
			option = 1;

		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("Display", option);
		editor.commit();
	}
	
	private void CreateBackup()
	{
	
		Gson json = new Gson();
				
		Backup backup = new Backup();
		
		backup.Profile = "Jonathan";
		backup.ProfileId = 1;
		backup.Shows = new DatabaseHandler(SettingsActivity.this).BackupShows();
		
		String result = "";
		try {
			Type backupObject = new TypeToken<Backup>(){}.getType();
			result =  json.toJson(backup, backupObject);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		try {
			String externalStoragePath = Environment.getExternalStorageDirectory().getPath();
			File backupFolder = new File(externalStoragePath,"/Tvist");
			
			if(!backupFolder.exists())
				backupFolder.mkdir();
			File backupFile = new File(backupFolder + "/" + "backup.txt");


		    BufferedWriter writer = new BufferedWriter(new FileWriter(backupFile));
		    writer.write(result);
		    writer.flush();
		    writer.close();
		} catch (Exception e) {
			Log.d("Backup", e.getMessage());
			Toast.makeText(SettingsActivity.this, R.string.message_backup_error, Toast.LENGTH_SHORT).show();
		}
		
		Toast.makeText(SettingsActivity.this, R.string.message_backup_complete, Toast.LENGTH_SHORT).show();
		
	}

	private void RestoreBackup()
	{
		Gson json = new Gson();
		Backup backup = new Backup();
		String jsonString = "";
		
		String externalStoragePath = Environment.getExternalStorageDirectory().getPath();
		File backupFolder = new File(externalStoragePath,"/Tvist");
		File backupFile = new File(backupFolder + "/" + "backup.txt");
				    
		try {
			FileInputStream fis = new FileInputStream(backupFile);
		    InputStreamReader isr = new InputStreamReader(fis);
		    BufferedReader br = new BufferedReader(isr, 8192);    // 2nd arg is buffer size
		
		    jsonString = br.readLine();
		    Type backupObject = new TypeToken<Backup>(){}.getType();
			backup = json.fromJson(jsonString, backupObject);
		    
			Log.d("Test",backup.Profile);
			Log.d("Test",backup.Shows.size() + "");
			
			isr.close();
	        fis.close();
	        br.close();
		} catch (Exception e) {
			Toast.makeText(SettingsActivity.this, R.string.message_backup_error, Toast.LENGTH_SHORT).show();
		}

		Toast.makeText(SettingsActivity.this, R.string.message_backup_complete, Toast.LENGTH_SHORT).show();
	}
	
}
