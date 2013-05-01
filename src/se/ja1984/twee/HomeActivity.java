package se.ja1984.twee;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import se.ja1984.twee.R;
import se.ja1984.twee.adapters.SeriesAdapter;
import se.ja1984.twee.models.Episode;
import se.ja1984.twee.models.ExtendedSeries;
import se.ja1984.twee.models.Profile;
import se.ja1984.twee.models.Series;
import se.ja1984.twee.utils.DatabaseHandler;
import se.ja1984.twee.utils.DateHelper;
import se.ja1984.twee.utils.Utils;
import se.ja1984.twee.utils.XMLParser;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class HomeActivity extends BaseActivity {


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
	static final String KEY_EP_ID = "id";

	static List<Series> series;
	private static ListView mySeries;
	protected Object mActionMode;
	public String selectedItem = null;
	public int selectedProfile;
	public int newSelectedProfile;
	View selectedView;
	SeriesAdapter seriesAdapter;
	Menu menu;
	Integer currentPosition;
	Integer currentIndex;
	ProgressDialog saveDialog;
	SharedPreferences settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
		Utils.ignoreTheWhenOrder = prefs.getBoolean("pref_ignorethe", true);
		Utils.useLocalizedTimezone = prefs.getBoolean("pref_localizedtime", true);
		
		
		settings = getSharedPreferences("Twee", 0);
		Utils.selectedProfile = settings.getInt("Profile", 1);
		String lastUpdate = settings.getString("LastUpdate", "");

		Log.d("Test","" + new DateHelper().ConvertToLocalTimeTest("2013-05-01", "21:00"));
		
		if(lastUpdate.equals("")){
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("LastUpdate", DateTime.now().toString());
			editor.apply();
		}

		DisplayUpdateWarning(lastUpdate);

		setContentView(R.layout.layout_home);


		//setBehindContentView(R.layout.layout_sidebar);

		//		getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
		//		getSlidingMenu().setShadowDrawable(R.drawable.shadow);
		//		getSlidingMenu().setBehindOffsetRes(R.dimen.actionbar_home_width);
		//		getSlidingMenu().setBehindScrollScale(0.25f);

		mySeries = (ListView)findViewById(R.id.lstMySeries);
		mySeries.setDividerHeight(1);
		mySeries.setLongClickable(true);

		mySeries.setEmptyView(findViewById(R.id.emptyView));

		registerForContextMenu(mySeries);

		new GetSelectedUserTask().execute();

		//getActionBar().setDisplayHomeAsUpEnabled(true);

		//findViewById(R.id.menu_chooseprofile).


		mySeries.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if(mActionMode != null){
					return false;
				}
				mActionMode = mySeries.startActionMode(mActionModeCallback);
				view.setAlpha((float) .3);
				selectedView = view;
				view.setSelected(true);
				selectedItem = view.getTag(R.string.homeactivity_tag_seriesid).toString();
				return true;
			}

		});

		mySeries.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent intent = new Intent(getBaseContext(), OverviewActivity.class);
				intent.putExtra("SeriesId", view.getTag(R.string.homeactivity_tag_seriesid).toString());
				startActivity(intent);

			}
		});

		new GetMySeries().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}



	@Override
	protected void onResume() {
		super.onResume();

		if(seriesAdapter != null)
		{		
			new GetMySeries().execute();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			selectedView.setAlpha(1);
			selectedView = null;
			selectedItem = null;
			mActionMode = null;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.menu_contextual_home, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.menu_delete:
				deleteSeries(selectedItem);
				mode.finish();
				return true;

			case R.id.menu_refresh:
				new GetNewEpisodes().execute(selectedItem);
				mode.finish();
				return true;

			case R.id.menu_atttocalendar:
				AddNextEpisodeToCalendar(selectedItem);
				mode.finish();
				return true;

			default:
				return false;
			}
		}
	};


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_home, menu);

		setupSearchView(menu);
		this.menu = menu;
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
		//		case android.R.id.home:
		//			Intent intent = new Intent(this, HomeActivity.class);
		//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//			startActivity(intent);
		//			return true;

		case android.R.id.home:
			//toggle();
			return true;

		case R.id.menu_add:
			return true;

		case R.id.menu_refresh:
			//seriesAdapter.notifyDataSetChanged();
			//new GetMySeries().execute();
			new UpdateAllEpisodes().execute();
			return true;

		case R.id.menu_calendar:
			startActivity(new Intent(this,CalendarActivity.class));
			return true;

		case R.id.menu_settings:
			startActivity(new Intent(this,PreferenceActivity.class));
			return true;

		case R.id.menu_about:
			startActivity(new Intent(this,AboutActivity.class));
			return true;

		case R.id.menu_chooseprofile:
			displayProfileChooser();
			return true;

		case R.id.menu_trending:
			startActivity(new Intent(this,TrendingActivity.class));
			return true;

		case R.id.menu_unwatched:
			startActivity(new Intent(this,UnwatchedActivity.class));
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void displayProfileChooser(){


		selectedProfile = Utils.selectedProfile;
		newSelectedProfile = selectedProfile;

		final ArrayList<Profile> profiles = new DatabaseHandler(HomeActivity.this).GetAllprofiles();
		ArrayList<String> availableProfiles = new ArrayList<String>();

		for (int i = 0; i < profiles.size(); i++) {
			availableProfiles.add(profiles.get(i).getName());
			if(profiles.get(i).getId() == selectedProfile)
			{
				selectedProfile = i;
			}
		}


		String[] allProfiles = availableProfiles.toArray(new String[availableProfiles.size()]);

		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
		builder.setTitle(R.string.dialog_selectprofile_header)
		.setSingleChoiceItems(allProfiles, selectedProfile, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				newSelectedProfile = profiles.get(which).getId();
			}
		})

		.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				final SharedPreferences settings = getSharedPreferences("Twee", 0);
				SharedPreferences.Editor editor = settings.edit();
				Utils.selectedProfile = newSelectedProfile;
				editor.putInt("Profile", newSelectedProfile);
				editor.apply();
				new GetMySeries().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				new GetSelectedUserTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		})
		.setNegativeButton(R.string.delete_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// Just close
			}
		}).show();	

	}

	private void deleteSeries(final String seriesId)
	{
		new AlertDialog.Builder(this)
		.setMessage(R.string.delete_text)
		.setTitle(R.string.delete_title)
		.setCancelable(false)
		.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				new DatabaseHandler(HomeActivity.this).DeleteShow(seriesId);
				new GetMySeries().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		})
		.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		})
		.show();


	}


	public class GetMySeries extends AsyncTask<String, Void, ArrayList<ExtendedSeries>>{

		@Override
		protected ArrayList<ExtendedSeries> doInBackground(String... params) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
			Utils.PreferedSortOrder = Integer.parseInt(prefs.getString("pref_sortorder","0"));
			Utils.ShowShows = Integer.parseInt(prefs.getString("pref_showshows","0"));
			ArrayList<ExtendedSeries> series = new ArrayList<ExtendedSeries>();

			series = new DatabaseHandler(HomeActivity.this).GetMyShows();

			return series;
		}

		@Override
		protected void onPostExecute(ArrayList<ExtendedSeries> result) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);

			int selectedView = Integer.parseInt(prefs.getString("pref_display", "0"));
			int viewToDisplay = 0;
			Log.d("Selected view", "" + selectedView);
			switch (selectedView) {
			case 0:
				viewToDisplay = R.layout.listitem_series;
				break;
			case 1:
				viewToDisplay = R.layout.listitem_series_alt;
				break;
			case 2:
				viewToDisplay = R.layout.listitem_series_alt2;
				break;
			case 3:
				mySeries.setDividerHeight(0);
				viewToDisplay = R.layout.listitem_series_alt3;
			default:
				break;
			}

			if(result.size() == 0)
			{
				findViewById(R.id.pgrSearch).setVisibility(View.INVISIBLE);
				findViewById(R.id.txtMessage).setVisibility(View.VISIBLE);
			}


			currentIndex = mySeries.getFirstVisiblePosition();
			View v = mySeries.getChildAt(0);
			currentPosition = (v == null) ? 0 : v.getTop();

			seriesAdapter = new SeriesAdapter(getApplicationContext(), viewToDisplay, mySeries, result);
			mySeries.setAdapter(seriesAdapter);

			if(currentPosition != null && currentIndex != null)
				mySeries.setSelectionFromTop(currentIndex, currentPosition);

		}

	}


	public class GetNewEpisodes extends AsyncTask<String, String, Boolean>
	{

		@Override
		protected void onPreExecute(){
			saveDialog = ProgressDialog.show(HomeActivity.this, getString(R.string.message_download_pleasewait),getString(R.string.message_download_information),true,false, new DialogInterface.OnCancelListener(){
				@Override
				public void onCancel(DialogInterface dialog) {
					//GetNewEpisodes.this.cancel(true);
				}
			}
					);
		}

		protected void onProgressUpdate(String... value) {
			super.onProgressUpdate(value);
			saveDialog.setMessage(value[0]);
		}

		@Override
		protected Boolean doInBackground(String... q) {
			String completeAddress = String.format(KEY_FULLURL, q[0]);
			XMLParser parser = new XMLParser();
			String xml = parser.getXmlFromUrl(completeAddress);

			if(xml == null || xml.equals("") || xml.contains("Query failed"))
				return false;

			publishProgress(getString(R.string.message_episodes_updates));

			Document doc = parser.getDomElement(xml);
			if(doc == null || doc.equals(""))
				return false;

			NodeList nl = doc.getElementsByTagName(KEY_SERIES);

			if(nl == null)
				return false;

			NodeList episodes = doc.getElementsByTagName(KEY_EPISODE);		
			if(episodes == null)
				return false;

			ArrayList<Episode> Episodes = new ArrayList<Episode>();

			//Fetch series and save;

			Element e = (Element) nl.item(0);


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
				ep.setEpisodeId(parser.getValue(e, KEY_EP_ID));
				ep.setWatched("0");
				Episodes.add(ep);
			}
			publishProgress(getString(R.string.message_download_save_episodes));

			new DatabaseHandler(HomeActivity.this).UpdateAndAddEpisodes(Episodes, q[0], true);

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			super.onPostExecute(result);
			saveDialog.cancel();
			new GetMySeries().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			if(!result)
				Toast.makeText(HomeActivity.this, R.string.message_update_show_error, Toast.LENGTH_SHORT).show();

		}

	}


	public class UpdateAllEpisodes extends AsyncTask<String, String, Boolean>
	{

		@Override
		protected void onPreExecute(){
			saveDialog = ProgressDialog.show(HomeActivity.this, getString(R.string.message_download_pleasewait),getString(R.string.message_download_information),true,false, new DialogInterface.OnCancelListener(){
				@Override
				public void onCancel(DialogInterface dialog) {
					//GetNewEpisodes.this.cancel(true);
				}
			}
					);
		}

		protected void onProgressUpdate(String... value) {
			super.onProgressUpdate(value);
			saveDialog.setMessage(value[0]);
		}

		@Override
		protected Boolean doInBackground(String... q) {

			ArrayList<Series> series = new DatabaseHandler(HomeActivity.this).GetAllShows();

			for (int i = 0; i < series.size(); i++) {
				Series show = series.get(i);

				String showId = show.getSeriesId();
				String showName = show.getName();

				publishProgress("Updating " + showName + "("+ (i+1) + "/" + series.size() +")");

				String completeAddress = String.format(KEY_FULLURL, showId);
				XMLParser parser = new XMLParser();
				String xml = parser.getXmlFromUrl(completeAddress);

				if(xml == null || xml.equals("") || xml.contains("Query failed"))
					return false;

				Document doc = parser.getDomElement(xml);
				if(doc == null || doc.equals(""))
					return false;

				NodeList nl = doc.getElementsByTagName(KEY_SERIES);

				if(nl == null)
					return false;

				NodeList episodes = doc.getElementsByTagName(KEY_EPISODE);		
				if(episodes == null)
					return false;

				ArrayList<Episode> Episodes = new ArrayList<Episode>();

				//Fetch series and save;

				Element e = (Element) nl.item(0);


				for(int x = 0;x < episodes.getLength();x++)
				{
					Episode ep = new Episode();
					e = (Element) episodes.item(x);
					ep.setAired(parser.getValue(e, KEY_EP_AIRED));
					ep.setEpisode(parser.getValue(e, KEY_EP_EPISODE));
					ep.setSeason(parser.getValue(e, KEY_EP_SEASON));
					ep.setSeriesId(showId);
					ep.setSummary(parser.getValue(e, KEY_EP_SUMMARY));
					ep.setTitle(parser.getValue(e, KEY_EP_TITLE));
					ep.setLastUpdated(parser.getValue(e, KEY_LASTUPDATED));
					ep.setEpisodeId(parser.getValue(e, KEY_EP_ID));
					ep.setWatched("0");
					Episodes.add(ep);
				}

				new DatabaseHandler(HomeActivity.this).UpdateAndAddEpisodes(Episodes, showId, false);

			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			super.onPostExecute(result);
			saveDialog.cancel();

			//Set the lastUpdate setting
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("LastUpdate", DateTime.now().toString());
			editor.apply();
			new GetMySeries().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

			if(!result)
				Toast.makeText(HomeActivity.this, R.string.message_update_show_error, Toast.LENGTH_SHORT).show();

		}

	}

	private void DisplayUpdateWarning(String lastUpdate){

		if(!lastUpdate.equals("")){
			if(Days.daysBetween(DateTime.parse(lastUpdate), DateTime.now()).getDays() >= 7)
			{
				new AlertDialog.Builder(this)
				.setMessage(R.string.update_shows_message)
				.setTitle(R.string.update_shows_title)
				.setCancelable(false)
				.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						new UpdateAllEpisodes().execute();
					}
				})
				.setNegativeButton(R.string.dialog_remindme_later, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences.Editor editor = settings.edit();
						editor.putString("LastUpdate", DateTime.now().toString());
						editor.apply();
					}
				})
				.show();
			}
		}
	}

	public class GetSelectedUserTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... params) {
			return new DatabaseHandler(getApplicationContext()).GetSelectedProfile();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			getActionBar().setSubtitle(result);
		}
	}

	private void AddNextEpisodeToCalendar(String selectedShow){

		Episode nextEpisode = new DatabaseHandler(getApplicationContext()).GetShowAndNextEpisode(selectedShow);

		if(nextEpisode.getAired() == null){
			Toast.makeText(HomeActivity.this, R.string.message_calendar_noepisodes, Toast.LENGTH_SHORT).show();
			return;
		}


		String title = nextEpisode.getLastUpdated() + " - ";
		title += nextEpisode.getTitle().equals("") ? "TBA" :nextEpisode.getTitle();  

		DateTime date = DateTime.parse(nextEpisode.getAired());


		Calendar beginTime = Calendar.getInstance();
		beginTime.set(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
		Calendar endTime = Calendar.getInstance();
		beginTime.set(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
		Intent intent = new Intent(Intent.ACTION_INSERT)
		.setData(Events.CONTENT_URI)
		.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
		.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
		.putExtra(Events.TITLE, title)
		.putExtra(Events.DESCRIPTION, nextEpisode.getSummary())
		//.putExtra(Events.EVENT_LOCATION, "The gym")
		.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
		//.putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");
		startActivity(intent);
	}

}
