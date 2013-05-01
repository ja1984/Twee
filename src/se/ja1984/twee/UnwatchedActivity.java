package se.ja1984.twee;

import java.util.ArrayList;

import se.ja1984.twee.adapters.CalendarAdapter;
import se.ja1984.twee.adapters.UnwatchedAdapter;
import se.ja1984.twee.fragments.DatePickerFragment;
import se.ja1984.twee.models.Episode;
import se.ja1984.twee.models.ExtendedSeries;
import se.ja1984.twee.utils.DatabaseHandler;
import se.ja1984.twee.utils.Utils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;


import android.support.v4.app.NavUtils;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;


public class UnwatchedActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {
	UnwatchedAdapter unwatchedAdapter;
	private static ListView mySeries;
	SharedPreferences prefs;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_unwatched);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mySeries = (ListView)findViewById(R.id.lstUnwatched);
		mySeries.setLongClickable(true);
		mySeries.setEmptyView(findViewById(R.id.emptyView));
		String selectedDate = prefs.getString("backlog_date", "");
		
		if(!selectedDate.equals("")){
			getActionBar().setSubtitle(selectedDate);
			new GetUnwatchedEpisodesTask().execute(selectedDate);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_unwatched, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
			
		case R.id.menu_information:
			displayInformation();
			return true;
			
		case R.id.menu_backlog:
			SetDate();
			return true;
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void SetDate(){
		DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	
	public class GetUnwatchedEpisodesTask extends AsyncTask<String, Void, ArrayList<Episode>>
	{

		@Override
		protected ArrayList<Episode> doInBackground(String... params) {
			ArrayList<Episode> shows = new DatabaseHandler(UnwatchedActivity.this).GetUnwatchedEpisodes(params[0]);
			return shows;
		}

		@Override
		protected void onPostExecute(ArrayList<Episode> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if(result.size() == 0)
			{
				findViewById(R.id.pgrSearch).setVisibility(View.INVISIBLE);
				findViewById(R.id.txtMessage).setVisibility(View.VISIBLE);
			}
			
			unwatchedAdapter = new UnwatchedAdapter(UnwatchedActivity.this, R.layout.listitem_backlog, mySeries, result);
			mySeries.setAdapter(unwatchedAdapter);
			
		}


		
		
		
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		int month = (monthOfYear + 1);
		
		String setDate = year + "-" + (month < 10 ? "0" + month : "" + month)  + "-" + (dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth);
		
		prefs.edit().putString("backlog_date", setDate).apply();
		getActionBar().setSubtitle(setDate);
		
		new GetUnwatchedEpisodesTask().execute(setDate);
	}
	
	private void displayInformation(){
		AlertDialog.Builder alert = new AlertDialog.Builder(UnwatchedActivity.this);
		alert.setTitle(R.string.menu_backlog);
		alert.setMessage(R.string.message_backlog_information);
		alert.setNeutralButton(R.string.dialog_ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
			}
		});
		alert.show();
	}

}
