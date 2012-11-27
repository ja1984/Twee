package se.ja1984.twee;

import se.ja1984.twee.utils.DatabaseHandler;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class ProfileActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        Button btnAddProfile = (Button)findViewById(R.id.btnAddProfile);
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
				
				AlertDialog.Builder addProfile = new AlertDialog.Builder(ProfileActivity.this);
				addProfile.setTitle(R.string.dialog_addprofile_header);
				
				addProfile.setView(dialoglayout);

				addProfile.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText profileName = (EditText)dialoglayout.findViewById(R.id.txtProfileName);
						if(!profileName.getText().toString().equals(""))
						{
							new DatabaseHandler(ProfileActivity.this).AddNewProfile(profileName.getText().toString());	
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

}
