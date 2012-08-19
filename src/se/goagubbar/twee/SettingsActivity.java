package se.goagubbar.twee;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

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
    
}
