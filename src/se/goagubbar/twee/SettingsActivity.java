package se.goagubbar.twee;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.support.v4.app.NavUtils;

public class SettingsActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        SharedPreferences settings = getSharedPreferences("Twee", 0);
	    int theme = settings.getInt("Theme", R.style.Light);
	    
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
        
        
        
     // This will get the radiogroup
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.grpTheme);
        // This will get the radiobutton in the radiogroup that is checked
        //RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        
        
     // This overrides the radiogroup onCheckListener
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup rGroup, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                    // Changes the textview's text to "Checked: example radiobutton text"
                	saveTheme(checkedRadioButton.getText().toString());
                    //Log.d("Checked", checkedRadioButton.getText().toString());
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
    	
    	SharedPreferences settings = getSharedPreferences("Twee", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("Theme", option);

        // Commit the edits!
        editor.commit();
    }
    
}
