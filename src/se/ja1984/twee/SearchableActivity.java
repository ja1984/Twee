package se.ja1984.twee;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import se.ja1984.twee.R;
import se.ja1984.twee.adapters.SearchAdapter;
import se.ja1984.twee.dto.TraktSearchResult;
import se.ja1984.twee.dto.TraktShow;
import se.ja1984.twee.models.Episode;
import se.ja1984.twee.models.Series;
import se.ja1984.twee.utils.DatabaseHandler;
import se.ja1984.twee.utils.DateHelper;
import se.ja1984.twee.utils.ImageService;
import se.ja1984.twee.utils.Utils;
import se.ja1984.twee.utils.XMLParser;

public class SearchableActivity extends ListActivity {


	private DatabaseHandler db;
	static final String KEY_URL = "http://www.thetvdb.com/api/GetSeries.php?seriesname=";
	static final String KEY_FULLURL = "http://www.thetvdb.com/data/series/%s/all/";
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
	static final String KEY_EPISODEID = "id";

	static final String KEY_EP_AIRED = "FirstAired";
	static final String KEY_EP_EPISODE = "EpisodeNumber";
	static final String KEY_EP_SEASON = "SeasonNumber";
	static final String KEY_EP_SUMMARY = "Overview";
	static final String KEY_EP_TITLE = "EpisodeName";
	RelativeLayout emptyView;
	String searchQuery;
	ImageService imageService;
	ListView searchResult;
	Boolean downloadHeader;
	ProgressDialog saveDialog;
	DateHelper dateHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {


		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String theme = prefs.getString("pref_theme", "2");
		setTheme(Utils.GetTheme(Integer.parseInt(theme)));
		downloadHeader = prefs.getBoolean("pref_downloadheader", true);

		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.layout_searchable);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		imageService = new ImageService();
		searchResult = (ListView)getListView();
		emptyView = (RelativeLayout)findViewById(android.R.id.empty);
		searchResult.setBackgroundColor(Color.parseColor("#e4e4e4"));
		searchResult.setDividerHeight(0);
		dateHelper = new DateHelper();

		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			searchQuery = query;
			actionBar.setTitle( getString(R.string.search_header) +" " + query);
			doSearchQuery(query);
		}

		searchResult.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View rowView, int arg2, long arg3) {
				String seriesId = rowView.getTag().toString();
				String runtime = rowView.getTag(R.string.TAG_RUNTIME).toString();
				String airtime = rowView.getTag(R.string.TAG_AIRTIME).toString();
				String tvRageId = rowView.getTag(R.string.TAG_TVRAGEID).toString();

				if(!db.ShowExists(seriesId))
				{
					FetchAndSaveSeries fas = new FetchAndSaveSeries();
					fas.execute(seriesId, runtime, airtime, tvRageId);
				}
				else
				{
					Toast.makeText(getBaseContext(), R.string.message_series_double, Toast.LENGTH_SHORT).show();	
				}

			}

		});

	}



	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	private void doSearchQuery(String query)
	{
		if(isNetworkAvailable())
		{
			db = new DatabaseHandler(SearchableActivity.this);
			Search search = new Search();
			search.execute(query);
		}
		else
		{
			//emptyView.setText(R.string.message_nointernet);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_searchable, menu);
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


	public class Search extends AsyncTask<String, Void, ArrayList<TraktSearchResult>>{

		@Override
		protected ArrayList<TraktSearchResult> doInBackground(String... q) {

			HttpClient client = new DefaultHttpClient();
			URI uri = null;
			try {
				uri = new URI("http://api.trakt.tv/search/shows.json/68f70fec5b671144a1f7626b342723b1/" + q[0].replace(" ", "+"));
				Log.d("test",uri.toString());
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			HttpGet httpGet = new HttpGet(uri);
			StringBuilder stringBuilder = new StringBuilder();
			ArrayList<TraktSearchResult> shows = new ArrayList<TraktSearchResult>();
			try {
				HttpResponse response = client.execute(httpGet);

				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));

				String line;
				while((line = reader.readLine()) != null){
					stringBuilder.append(line);
				}

				Gson gson = new Gson();
				Type traktShows = new TypeToken<ArrayList<TraktSearchResult>>(){}.getType();
				shows = gson.fromJson(stringBuilder.toString(), traktShows);

			} catch (Exception e) {
				// TODO: handle exception
			}

			return shows;

		}

		@Override
		protected void onPostExecute(ArrayList<TraktSearchResult> series) {
			setProgressBarIndeterminateVisibility(false);

			//emptyView.setText(R.string.message_noresult);

			TextView txtMessage = (TextView) emptyView.findViewById(R.id.txtMessage);
			ProgressBar searchProgress = (ProgressBar) emptyView.findViewById(R.id.pgrSearch);
			searchProgress.setVisibility(View.GONE);
			txtMessage.setText("No results found");


			SearchAdapter sa = new SearchAdapter(SearchableActivity.this,R.layout.listitem_searchresult,series);
			setListAdapter(sa);

		}

	}

	public class FetchAndSaveSeries extends AsyncTask<String, String, Boolean>
	{

		@Override
		protected Boolean doInBackground(String... q) {
			saveDialog.setMessage(getString(R.string.message_download_information));
			String completeAddress = String.format(KEY_FULLURL, TextUtils.htmlEncode(q[0].replaceAll("[^a-zA-Z0-9 ]+", "")));
			String tvRageUrl = "http://services.tvrage.com/feeds/showinfo.php?sid=" + q[3];

			String[] tvRageResponse = GetTimeZone(tvRageUrl);



			//String completeAddress = "http://www.thetvdb.com/data/series/" + q[0] +"/all/";
			XMLParser parser = new XMLParser();

			String xml = parser.getXmlFromUrl(completeAddress);		

			if(xml == null || xml.equals(""))
				return false;

			Document doc = parser.getDomElement(xml);

			if(doc == null || doc.equals(""))
				return false;

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


			publishProgress(getString(R.string.message_download_banner));
			String image = parser.getValue(e, KEY_IMAGE);

			if(!image.equals("")){
				try {
					s.setImage(imageService.getBitmapFromURL(image, s.getSeriesId(), SearchableActivity.this));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			if(downloadHeader){

				publishProgress(getString(R.string.message_download_header));
				String header = parser.getValue(e, KEY_HEADER);

				if(!header.equals("")){
					try {
						s.setHeader(imageService.getBitmapFromURL(header,s.getSeriesId() + "_big", SearchableActivity.this));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

			publishProgress(getString(R.string.message_download_save_series));

			s.setImdbId(parser.getValue(e, KEY_IMDBID));
			s.setName(parser.getValue(e, KEY_NAME));			
			s.setRating(parser.getValue(e, KEY_RATING));
			s.setStatus(parser.getValue(e, KEY_STATUS));
			s.setSummary(parser.getValue(e, KEY_SUMMARY));
			s.setLastUpdated(parser.getValue(e, KEY_LASTUPDATED));
			s.setAirtime(tvRageResponse[1]);
			s.setTimeZone(tvRageResponse[0]);
			
			s.setRuntime(q[1]);
			//Runtime = 1, Airtime = 2
			db.AddShow(s);

			publishProgress(getString(R.string.message_download_save_episodes));

			for(int i = 0;i < episodes.getLength();i++)
			{
				Episode ep = new Episode();
				e = (Element) episodes.item(i);



				ep.setAired(dateHelper.ConvertToLocalTime(parser.getValue(e, KEY_EP_AIRED), q[2]));
				ep.setEpisode(parser.getValue(e, KEY_EP_EPISODE));
				ep.setSeason(parser.getValue(e, KEY_EP_SEASON));
				ep.setSeriesId(q[0].toString());
				ep.setSummary(parser.getValue(e, KEY_EP_SUMMARY));
				ep.setTitle(parser.getValue(e, KEY_EP_TITLE));

				ep.setLastUpdated(parser.getValue(e, KEY_LASTUPDATED));
				ep.setEpisodeId(parser.getValue(e,KEY_EPISODEID));
				ep.setWatched("0");


				Episodes.add(ep);

			}

			db.AddEpisodes(Episodes);


			return true;
		}

		@Override
		protected void onPreExecute(){
			saveDialog = ProgressDialog.show(SearchableActivity.this, getString(R.string.message_download_pleasewait),getString(R.string.message_download_information),true,false, new DialogInterface.OnCancelListener(){
				@Override
				public void onCancel(DialogInterface dialog) {
					//FetchAndSaveSeries.this.cancel(true);
				}
			}
					);

		}

		protected void onProgressUpdate(String... value) {
			super.onProgressUpdate(value);
			saveDialog.setMessage(value[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {

			if(result)
			{
				saveDialog.cancel();
				NavUtils.navigateUpFromSameTask(SearchableActivity.this);
			}
			else{
				saveDialog.cancel();
				Toast.makeText(SearchableActivity.this, R.string.message_downloading_show_error, Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}

	}

	private String[] GetTimeZone(String tvRageUrl){
		XMLParser parser = new XMLParser();

		String[] tvRageInformation = new String[2];

		String xml = parser.getXmlFromUrl(tvRageUrl);		
		Log.d("XML","" + xml);
		if(xml == null || xml.equals(""))
			return tvRageInformation;

		Document doc = parser.getDomElement(xml);

		if(doc == null || doc.equals(""))
			return tvRageInformation;

		NodeList nl = doc.getElementsByTagName("Showinfo");
		Element e = (Element) nl.item(0);

		tvRageInformation[0] =parser.getValue(e, "timezone"); 
		tvRageInformation[1] = parser.getValue(e, "airtime");

		return  tvRageInformation;
	}

}
