package se.ja1984.twee;


import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import se.ja1984.twee.R;
import se.ja1984.twee.fragments.Episodes;
import se.ja1984.twee.fragments.Overview;
import se.ja1984.twee.fragments.Summary;
import se.ja1984.twee.models.Series;
import se.ja1984.twee.utils.DatabaseHandler;
import se.ja1984.twee.utils.ImageService;
import se.ja1984.twee.utils.Utils;
import se.ja1984.twee.utils.XMLParser;

public class OverviewActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
	 * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
	 * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
	 * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	static ArrayList<Fragment> fragments;
	Series series;
	int totalEpisodes;
	int watchedEpisodes;
	String seriesId;
	static String tvdbSeriesId;
	static final String KEY_FULLURL = "http://www.thetvdb.com/data/series/%s/all/";
	static final String KEY_SERIES = "Series";
	static final String KEY_IMAGE = "banner";
	static final String KEY_HEADER = "fanart";
	ProgressDialog saveDialog;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String theme = prefs.getString("pref_theme", "2");
		setTheme(Utils.GetTheme(Integer.parseInt(theme)));

		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_overview);
		getActionBar().setDisplayHomeAsUpEnabled(true);


		Log.d("SaveInstanceIsNnull","" + (savedInstanceState == null));
		if(savedInstanceState != null)
		{
			seriesId =  savedInstanceState.getString("SeriesId");
		}

		Bundle extras = getIntent().getExtras();

		if(extras == null)
		{
			finish();
		}
		
		seriesId = extras.getString("SeriesId");

		if(seriesId.equals("") || seriesId == null){
			finish();
		}
		
		tvdbSeriesId = seriesId;
		series = new DatabaseHandler(getBaseContext()).GetShowById(seriesId);
		getActionBar().setTitle(series.getName());

		fragments = new ArrayList<Fragment>();




		fragments.add(new Summary());

		fragments.add(new Overview());

		fragments.add(new Episodes());


		// Create the adapter that will return a fragment for each of the three primary sections
		// of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(1);

	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("SeriesId", seriesId);
		Log.d("onSaveInstanceStad", outState.getString("SeriesId"));
	}




	@Override
	protected void onDestroy() {
		finish();
		super.onDestroy();
	}


	public void Reload()
	{
		//Episodes fragment = (Episodes) fragments.get(2);
	}

	public static void Refresh()
	{

		View v = (View)fragments.get(1).getView();

		if(v != null)
			Overview.SetProgress(v, tvdbSeriesId);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_overview, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;

		case R.id.menu_markseries:
			new DatabaseHandler(getBaseContext()).MarkShowAsWatched(series.getSeriesId());
			Episodes.MarkAllEpisodes();
			Overview.MarkLastAiredEpisodeAsWatched(true);
			Refresh();
			break;

		case R.id.menu_downloadimages:
			new DownloadImagesTask().execute();
			break;

		case R.id.menu_downloadbanner:
			Intent intent = new Intent(this, BannerActivity.class);
			intent.putExtra("showId", seriesId);
			startActivity(intent);
			break;

		}
		return super.onOptionsItemSelected(item);
	}



	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
	 * sections of the app.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = fragments.get(i); //array new DummySectionFragment();
			Bundle args = new Bundle();
			args.putString("showId", seriesId);
			args.putInt("totalEpisodes", totalEpisodes);
			args.putInt("watchedEpisodes", watchedEpisodes);
			//args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0: return getString(R.string.section_title_summary);
			case 1: return getString(R.string.section_title_overview);
			case 2: return getString(R.string.section_title_episodes);
			}
			return null;
		}
	}


	private class DownloadImagesTask extends AsyncTask<Void, String, Boolean> 
	{

		@Override
		protected void onPreExecute(){
			saveDialog = ProgressDialog.show(OverviewActivity.this, getString(R.string.message_download_pleasewait),getString(R.string.message_download_information),true,false, new DialogInterface.OnCancelListener(){
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
		protected Boolean doInBackground(Void... params) {

			boolean success = true;
			String completeAddress = String.format(KEY_FULLURL, seriesId);
			XMLParser parser = new XMLParser();

			String xml = parser.getXmlFromUrl(completeAddress);		

			Document doc = parser.getDomElement(xml);
			NodeList nl = doc.getElementsByTagName(KEY_SERIES);
			//Fetch series and save;

			Series s = new Series();
			Element e = (Element) nl.item(0);

			publishProgress(getString(R.string.message_download_banner));
			s.setSeriesId(seriesId);
			String image = parser.getValue(e, KEY_IMAGE);		
			if(!image.equals("")){
				try {
					s.setImage(new ImageService().getBitmapFromURL(image, s.getSeriesId(), OverviewActivity.this));
				} catch (Exception ex) {
					Log.d("Error downloading image", ex.getMessage());
					success = false;
				}
			}

			publishProgress(getString(R.string.message_download_header));
			String header = parser.getValue(e, KEY_HEADER);
			if(!header.equals("")){
				try {
					s.setHeader(new ImageService().getBitmapFromURL(header,s.getSeriesId() + "_big", OverviewActivity.this));
				} catch (Exception ex) {
					Log.d("Error downloading header", ex.getMessage());
					success = false;
				}
			}

			new DatabaseHandler(OverviewActivity.this).UpdateShowImage(s);

			return success;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			saveDialog.cancel();
			if(!result)
			{
				Toast.makeText(getBaseContext(), R.string.message_downloading_images_error, Toast.LENGTH_SHORT).show();
			}

		}





	}


}
