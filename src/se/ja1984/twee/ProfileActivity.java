package se.ja1984.twee;

import java.util.ArrayList;

import se.ja1984.twee.adapters.ProfileAdapter;
import se.ja1984.twee.models.Profile;
import se.ja1984.twee.utils.DatabaseHandler;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class ProfileActivity extends BaseActivity {

	ListView lstProfiles;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_profile);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		new GetProfilesTask().execute();

		Button btnAddProfile = (Button)findViewById(R.id.btnAddProfile);
		lstProfiles = (ListView)findViewById(R.id.lstProfiles);

		lstProfiles.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Toast.makeText(ProfileActivity.this, R.string.edit_notimplemented, Toast.LENGTH_SHORT).show();
			}
		});


		btnAddProfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

//				LinearLayout layout = new LinearLayout(ProfileActivity.this);
//				layout.setOrientation(LinearLayout.VERTICAL);

				//layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//				final EditText textProfileName = new EditText(ProfileActivity.this);
//
//				final TextView txtInformation = new TextView(ProfileActivity.this);
//				textProfileName.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//				txtInformation.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//				txtInformation.setText(R.string.settings_description_profile_popup);

//				layout.addView(txtInformation);
//				layout.addView(textProfileName);

				LayoutInflater inflater = getLayoutInflater();
				final View dialoglayout = inflater.inflate(R.layout.layout_addnewprofile, null);

				AlertDialog.Builder addProfile = new AlertDialog.Builder(ProfileActivity.this);
				addProfile.setTitle(R.string.dialog_addprofile_header);

				addProfile.setView(dialoglayout);

				addProfile.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText profileName = (EditText)dialoglayout.findViewById(R.id.editProfileName);
						if(!profileName.getText().toString().equals(""))
						{
							new DatabaseHandler(ProfileActivity.this).AddNewProfile(profileName.getText().toString());
							new GetProfilesTask().execute();
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


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.layout_profile, menu);
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


	public class GetProfilesTask extends AsyncTask<String, Void, ArrayList<Profile>>
	{

		@Override
		protected ArrayList<Profile> doInBackground(String... params) {		
			return new DatabaseHandler(ProfileActivity.this).GetAllprofiles();
		}

		@Override
		protected void onPostExecute(ArrayList<Profile> result) {
			ProfileAdapter profileAdapter = new ProfileAdapter(ProfileActivity.this, R.layout.listitem_profile , lstProfiles, result);
			lstProfiles.setAdapter(profileAdapter);
			super.onPostExecute(result);
		}

	}

}
