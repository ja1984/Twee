package se.ja1984.teewee;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

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



	static Activity main;
	static List<Series> series;
	private ListView lV;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		
		
		super.onCreate(savedInstanceState);

		db = new DatabaseHandler(this);

		ActionBar actionBar = getActionBar();

		setContentView(R.layout.activity_main);


		//			String query = intent.getStringExtra(SearchManager.QUERY);
		//			Search search = new Search();
		//			search.execute(query);
		//			//			doMySearch(query);
		//
		//			lV = (ListView)findViewById(R.id.searchResults);
		//			lV.setOnItemClickListener(new OnItemClickListener() {
		//				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//					
		//					String seriesId = view.getTag().toString();
		//					ProgressBar pb = (ProgressBar)view.findViewById(R.id.progressBar1);
		//					pb.setVisibility(0);
		//					FetchAndSaveSeries fas = new FetchAndSaveSeries();
		//					Log.d("Event","Startar fas");
		//					fas.execute(seriesId);
		//
		//				}
		//			});
		//		}
		//		else
		//		{
		new GetMySeries().execute();

		lV = (ListView)findViewById(R.id.searchResults);
		lV.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent intent = new Intent(getBaseContext(), OverviewActivity.class);
				intent.putExtra("SeriesId", view.getTag().toString());
				startActivity(intent);

			}
		});
	}






	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		main = this;

		setupSearchView(menu);

		return true;

	}

	private void setupSearchView(Menu menu) {
		// Get the SearchView and set the searchable configuration
		SearchManager searchManager =                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView =                 (SearchView) menu.findItem(R.id.menu_add).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));
	}

	public boolean onOptionsItemSelected(MenuItem item){

		switch(item.getItemId()){
		case android.R.id.home:
			//app icon in action bar clicked, go home
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;

		case R.id.menu_add:
			return true;

		case R.id.menu_refresh:
			new GetMySeries().execute();
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

	public class FetchAndSaveSeries extends AsyncTask<String, Void, Boolean>
	{

		@Override
		protected Boolean doInBackground(String... q) {

			String completeAddress = String.format(KEY_FULLURL, q[0]);
			XMLParser parser = new XMLParser();

			String xml = parser.getXmlFromUrl(completeAddress);		

			Document doc = parser.getDomElement(xml);
			NodeList nl = doc.getElementsByTagName(KEY_SERIES);
			NodeList episodes = doc.getElementsByTagName(KEY_EPISODE);		

			ArrayList<Episode> Episodes = new ArrayList<Episode>();

			//Fetch series and save;

			Series s = new Series();
			Element e = (Element) nl.item(0);


			s.setActors(parser.getValue(e, KEY_ACTORS));


			s.setAirs(parser.getValue(e, KEY_AIRSDAY) + " " + parser.getValue(e, KEY_AIRTIME));

			s.setFirstAired(parser.getValue(e, KEY_AIRED));

			s.setGenre(parser.getValue(e, KEY_GENRE));


			s.setSeriesId(q[0]);


			s.setImage(new ImageHelper().getBitmapFromURL(parser.getValue(e, KEY_IMAGE), getApplicationContext()));


			s.setHeader(new ImageHelper().getBitmapFromURL(parser.getValue(e, KEY_HEADER), getApplicationContext()));
			s.setImdbId(parser.getValue(e, KEY_IMDBID));
			s.setName(parser.getValue(e, KEY_NAME));
			s.setRating(parser.getValue(e, KEY_RATING));
			s.setStatus(parser.getValue(e, KEY_STATUS));
			s.setSummary(parser.getValue(e, KEY_SUMMARY));
			s.setLastUpdated(parser.getValue(e, KEY_LASTUPDATED));

			db.addSeries(s);



			for(int i = 0;i < episodes.getLength();i++)
			{
				Episode ep = new Episode();
				e = (Element) episodes.item(i);

				ep.setAired(parser.getValue(e, KEY_EP_AIRED));
				ep.setEpisode(parser.getValue(e, KEY_EP_EPISODE));
				ep.setSeason(parser.getValue(e, KEY_EP_SEASON));
				ep.setSeriesId(q[0].toString());
				ep.setSummary(parser.getValue(e, KEY_EP_SUMMARY));
				ep.setTitle(parser.getValue(e, KEY_EP_TITLE));
				ep.setLastUpdated(parser.getValue(e, KEY_LASTUPDATED));
				ep.setWatched("0");


				Episodes.add(ep);

			}

			db.addEpisodes(Episodes);


			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(result)
			{
				new GetMySeries().execute();
				Toast.makeText(getApplicationContext(), "Klart", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}

	}


	public class GetMySeries extends AsyncTask<String, Void, ArrayList<Series>>{

		@Override
		protected ArrayList<Series> doInBackground(String... params) {
			ArrayList<Series> series = db.getAllSeries();
			return series;
		}

		@Override
		protected void onPostExecute(ArrayList<Series> result) {
			ListView lv = (ListView)findViewById(R.id.searchResults);

			SeriesAdapter sa = new SeriesAdapter(main, R.layout.series_listitem, lv, result);
			lv.setAdapter(sa);

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
	

}

