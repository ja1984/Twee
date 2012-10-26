package se.goagubbar.twee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
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
		boolean downloadHeader = settings.getBoolean("downloadHeaderImage", true);

		Button btnBackup = (Button)findViewById(R.id.btnBackup);
		Button btnRestore = (Button)findViewById(R.id.btnRestore);

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

		btnBackup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				createBackup();
			}
		});

		btnRestore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				restoreBackup();
			}
		});


		RadioGroup radioGroup = (RadioGroup)findViewById(R.id.grpTheme);
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
					saveTheme(checkedRadioButton.getText().toString());
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

	private void saveTheme(String selectedTheme)
	{

		int option = 0;

		if(selectedTheme.equals("Light theme"))
		{
			option = R.style.Light;
		}
		else if(selectedTheme.equals("Light theme w. dark actionbar"))
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

	private void createBackup()
	{

		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = "/data/data/se.goagubbar.twee/databases/teewee.db";
				String backupDBPath = "backup_twee.db";
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				if (currentDB.exists()) {
					FileChannel src = new FileInputStream(currentDB).getChannel();
					FileChannel dst = new FileOutputStream(backupDB).getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
				} else {
					Log.e("Error", "File does not exist: " + currentDBPath);
				}
			} else {
				Log.e("Error", "SDCard not writable, backup aborted.");
			}
		} catch (Exception ex) {
			Log.e("Error", "Error backing up database to sdcard.", ex);
		}


	}

	private void restoreBackup()
	{


	}


}
