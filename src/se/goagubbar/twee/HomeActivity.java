package se.goagubbar.twee;


import java.util.ArrayList;
import java.util.List;

import se.goagubbar.twee.Adapters.SeriesAdapter;
import se.goagubbar.twee.Models.Series;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;

public class HomeActivity extends BaseActivity {

	
	private DatabaseHandler db;
	static final String KEY_URL = "http://www.thetvdb.com/api/GetSeries.php?seriesname=";
	static final String KEY_FULLURL = "http://www.thetvdb.com//data/series/%s/all/";
	static final String KEY_SERIES = "Series";
	static final String KEY_EPISODE = "Episode";
	static final String KEY_ID = "seriesid";
	static final String KEY_NAME = "SeriesName";
	static final String KEY_AIRED = "FirstAired";
	static final String KEY_ACTORS = "Actors";
	static final String KEY_RATING = "Rating";
	static final String KEY_IMAGE = "banner";
	static final String KEY_HEADER = "fanart";
	static final String KEY_STATUS = "Status";
	static final String KEY_AIRSDAY = "Airs_DayOfWeek";
	static final String KEY_AIRTIME = "Airs_Time";
	static final String KEY_GENRE = "Genre";
	static final String KEY_SUMMARY = "Overview";
	static final String KEY_IMDBID = "IMDB_ID";
	static final String KEY_LASTUPDATED = "lastupdated";

	static final String KEY_EP_AIRED = "FirstAired";
	static final String KEY_EP_EPISODE = "EpisodeNumber";
	static final String KEY_EP_SEASON = "SeasonNumber";
	static final String KEY_EP_SUMMARY = "Overview";
	static final String KEY_EP_TITLE = "EpisodeName";

	static List<Series> series;
	private ListView mySeries;
	Object mActionMode;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        db = new DatabaseHandler(this);
        setContentView(R.layout.layout_home);
        
        
        
        new GetMySeries().execute();

        
        
		mySeries = (ListView)findViewById(R.id.lstMySeries);
			
		mySeries.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent intent = new Intent(getBaseContext(), OverviewActivity.class);
				intent.putExtra("SeriesId", view.getTag(R.string.homeactivity_tag_seriesid).toString());
				startActivity(intent);

			}
		});
    }

    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        
        setupSearchView(menu);
        
        return true;
    }

    private void setupSearchView(Menu menu) {
		SearchManager searchManager =                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView =                 (SearchView) menu.findItem(R.id.menu_add).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));
	}
    
    public boolean onOptionsItemSelected(MenuItem item){

		switch(item.getItemId()){
		case android.R.id.home:
			Intent intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;

		case R.id.menu_add:
			return true;

		case R.id.menu_refresh:
			new GetMySeries().execute();
			return true;

		case R.id.menu_calendar:
			startActivity(new Intent(this,CalendarActivity.class));
			return true;
			
		case R.id.menu_settings:
			startActivity(new Intent(this,SettingsActivity.class));
			return true;

		case R.id.menu_about:
			displayAboutDialog();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
    
    private void displayAboutDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.about_text)
		.setCancelable(false)
		.setTitle(R.string.about_title)
		.setPositiveButton(R.string.about_close, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		}).create().show();
	}
    
    public class GetMySeries extends AsyncTask<String, Void, ArrayList<Series>>{

		@Override
		protected ArrayList<Series> doInBackground(String... params) {
			Log.d("Test","Nu skall serierna hamtas");
			ArrayList<Series> series = db.getAllSeries();
			Log.d("Test","Nu har de hamtats!");
			return series;
		}

		@Override
		protected void onPostExecute(ArrayList<Series> result) {
			SeriesAdapter sa = new SeriesAdapter(getApplicationContext(), R.layout.listitem_series, mySeries, result);
			mySeries.setAdapter(sa);

		}

	}
    
    
    
}
