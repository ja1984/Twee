package se.ja1984.twee;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import se.ja1984.twee.SearchableActivity.FetchAndSaveSeries;
import se.ja1984.twee.adapters.SeriesAdapter;
import se.ja1984.twee.adapters.TrendingAdapter;
import se.ja1984.twee.dto.TraktShow;
import se.ja1984.twee.utils.DatabaseHandler;
import se.ja1984.twee.utils.SaveShowFromTvDb;
import se.ja1984.twee.utils.Utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class TrendingActivity extends BaseActivity {

	ListView lstTrending;
	Boolean downloadHeader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_trending);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		lstTrending = (ListView) findViewById(R.id.lstTrending);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		downloadHeader = prefs.getBoolean("pref_downloadheader", true);

		new GetTrendingListTask().execute();
		lstTrending.setEmptyView(findViewById(R.id.emptyView));
		lstTrending.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {

				String showId = view.getTag(R.string.homeactivity_tag_seriesid).toString();

				if(!new DatabaseHandler(TrendingActivity.this).ShowExists(showId))
				{
					new SaveShowFromTvDb(TrendingActivity.this, downloadHeader).execute(showId);
				}
				else
				{
					Toast.makeText(TrendingActivity.this, R.string.message_series_double, Toast.LENGTH_SHORT).show();	
				}




			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_trending, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
			
		case R.id.menu_refresh:
			new GetTrendingListTask().execute();
			return true;
			
		case R.id.menu_information:
			displayInformation();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void displayInformation(){
		AlertDialog.Builder alert = new AlertDialog.Builder(TrendingActivity.this);
		alert.setTitle(R.string.menu_tredning);
		alert.setMessage(R.string.message_trending_information);
		alert.setNeutralButton(R.string.dialog_ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
			}
		});
		alert.show();
	}
	
	public class GetTrendingListTask extends AsyncTask<Void, Void, List<TraktShow>>
	{

		@Override
		protected void onPostExecute(List<TraktShow> result) {
			TrendingAdapter adapter = new TrendingAdapter(TrendingActivity.this, R.layout.listitem_trending, lstTrending, result);
			lstTrending.setAdapter(adapter);

			super.onPostExecute(result);
		}

		@Override
		protected List<TraktShow> doInBackground(Void... params) {

			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet("http://api.trakt.tv/shows/trending.json/68f70fec5b671144a1f7626b342723b1");
			StringBuilder stringBuilder = new StringBuilder();
			ArrayList<TraktShow> shows = new ArrayList<TraktShow>();
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();

				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));

				String line;
				while((line = reader.readLine()) != null){
					stringBuilder.append(line);
				}

				Gson gson = new Gson();
				Type traktShows = new TypeToken<ArrayList<TraktShow>>(){}.getType();
				shows = gson.fromJson(stringBuilder.toString(), traktShows);

			} catch (Exception e) {
				// TODO: handle exception
			}

			return shows.subList(0, 20);
		}

	}

}
