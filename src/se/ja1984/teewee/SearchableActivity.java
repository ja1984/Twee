package se.ja1984.teewee;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class SearchableActivity extends ListActivity {


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



	@Override
	public void onCreate(Bundle savedInstanceState) {

		SharedPreferences settings = getSharedPreferences("Twee", 0);
		int theme = settings.getInt("Theme", R.style.Light);
		setTheme(theme);

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);	      

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);

			actionBar.setTitle( getString(R.string.search_header) +" " + query);
			doSearchQuery(query);
		}

		ListView searchResult = (ListView)getListView();
		searchResult.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View rowView, int arg2, long arg3) {
				setProgressBarIndeterminateVisibility(true);
				String seriesId = rowView.getTag().toString();
				Toast.makeText(getBaseContext(), R.string.message_series_fetching, Toast.LENGTH_SHORT).show();
				FetchAndSaveSeries fas = new FetchAndSaveSeries();
				fas.execute(seriesId);

			}

		});

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

	@Override
	public void onNewIntent(final Intent newIntent) {
		super.onNewIntent(newIntent);

		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			doSearchQuery(query);
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	private void doSearchQuery(String query)
	{
		db = new DatabaseHandler(SearchableActivity.this);
		Search search = new Search();
		search.execute(query);
	}


	public class Search extends AsyncTask<String, Void, ArrayList<Series>>{

		@Override
		protected ArrayList<Series> doInBackground(String... q) {
			setProgressBarIndeterminateVisibility(true);
			String completeAddress = KEY_URL + q[0];
			XMLParser parser = new XMLParser();
			String xml = parser.getXmlFromUrl(completeAddress);

			ArrayList<Series> series = new ArrayList<Series>();
			
			if (xml != null)
			{
				Document doc = parser.getDomElement(xml);

				NodeList nl = doc.getElementsByTagName(KEY_SERIES);

				for (int i = 0; i < nl.getLength(); i++) {
					Series s = new Series();
					Element e = (Element) nl.item(i);

					s.setName(parser.getValue(e, KEY_NAME));
					s.setID(Integer.parseInt(parser.getValue(e, KEY_ID)));
					s.setAirs(parser.getValue(e, KEY_AIRED));

					series.add(s);
				}
			}
//			else
//			{
//				Toast.makeText(getApplication(), getText(R.string.message_nointernet), Toast.LENGTH_SHORT).show();
//			}

			return series;

		}

		@Override
		protected void onPostExecute(ArrayList<Series> series) {
			setProgressBarIndeterminateVisibility(false);
			SearchAdapter sa = new SearchAdapter(getBaseContext(),R.layout.search_result,series);

			setListAdapter(sa);

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

			Log.d("Event","Laddar ner bild 1");
			String image = parser.getValue(e, KEY_IMAGE);
			Log.d("Test",image.toString());

			if(image != null){
				s.setImage(new ImageHelper().getBitmapFromURL(image, SearchableActivity.this));
			}

			Log.d("Event","Laddar ner bild 2");

			String header = parser.getValue(e, KEY_HEADER);
			Log.d("Test",header.toString());

			if(header != null){
				s.setHeader(new ImageHelper().getBitmapFromURL(header, SearchableActivity.this));
			}
			Log.d("Event","Laddar ner bild 2 - klar!");

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
				//new GetMySeries().execute();
				setProgressBarIndeterminateVisibility(false);

				Toast.makeText(getBaseContext(), R.string.message_series_fetching_done, Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}

	}
}
