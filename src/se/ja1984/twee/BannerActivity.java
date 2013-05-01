package se.ja1984.twee;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nostra13.universalimageloader.core.ImageLoader;

import se.ja1984.twee.HomeActivity.GetMySeries;
import se.ja1984.twee.adapters.BannerAdapter;
import se.ja1984.twee.adapters.SearchAdapter;
import se.ja1984.twee.adapters.TrendingAdapter;
import se.ja1984.twee.models.Series;
import se.ja1984.twee.utils.DatabaseHandler;
import se.ja1984.twee.utils.ImageService;
import se.ja1984.twee.utils.XMLParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class BannerActivity extends BaseActivity {

	static final String KEY_URL = "http://www.thetvdb.com//data/series/75710/banners";
	static final String KEY_TYPE = "BannerType";
	static final String KEY_TYPE2 = "BannerType2";
	static final String KEY_PATH = "BannerPath";
	ListView lstBanners;
	String showId;
	ProgressDialog saveDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_banner);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		lstBanners = (ListView) findViewById(R.id.lstBanners);
		Bundle extras = getIntent().getExtras();
		showId = extras.getString("showId");

		lstBanners.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
				// TODO Auto-generated method stub
				String imagePath = view.getTag(R.string.homeactivity_tag_seriesid).toString();
				ReplaceBanner(imagePath);
			}
		});

		new GetBannersTask().execute(showId);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.layout_banner, menu);
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
			//NavUtils.navigateUpFromSameTask(this);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void ReplaceBanner(final String imagePath){
		new AlertDialog.Builder(this)
		.setMessage(R.string.replace_banner_text)
		.setTitle(R.string.replace_banner_title)
		.setCancelable(false)
		.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				new DownloadBannerTask().execute(imagePath);
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


	public class DownloadBannerTask extends AsyncTask<String, String, Boolean>
	{



		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			saveDialog.cancel();
			if(!result)
			{
				Toast.makeText(BannerActivity.this, R.string.message_downloading_banner_error, Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected void onPreExecute() {
			saveDialog = ProgressDialog.show(BannerActivity.this, getString(R.string.message_download_pleasewait),getString(R.string.message_download_banner),true,false, new DialogInterface.OnCancelListener(){
				@Override
				public void onCancel(DialogInterface dialog) {
				}
			}
					);
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected Boolean doInBackground(String... params) {

			try {
				new ImageService().ReplaceBannerImage(params[0], showId, BannerActivity.this);	
			} catch (Exception e) {
				return false;
			}

			ImageLoader.getInstance().clearMemoryCache();
			ImageLoader.getInstance().clearDiscCache();
			return true;
		}

	}


	public class GetBannersTask extends AsyncTask<String, Void, ArrayList<String>>{

		@Override
		protected ArrayList<String> doInBackground(String... q) {

			//String completeAddress = "http://www.thetvdb.com/DDFA315CB3513C1D/series/"+ q[0] +"/banners.xml";
			String completeAddress = "http://thetvdb.com/api/DDFA315CB3513C1D/series/"+ q[0] +"/banners.xml";

			XMLParser parser = new XMLParser();
			String xml = parser.getXmlFromUrl(completeAddress);

			ArrayList<String> banners = new ArrayList<String>();

			if(xml == null || xml.equals("") || xml.contains("Query failed"))
			{
				return banners;
			}


			if (xml != null)
			{
				Document doc = parser.getDomElement(xml);

				NodeList nl = doc.getElementsByTagName("Banner");

				for (int i = 0; i < nl.getLength(); i++) {
					Element e = (Element) nl.item(i);
					if(parser.getValue(e, KEY_TYPE).equals("series") && parser.getValue(e, KEY_TYPE2).equals("graphical"))
						banners.add(parser.getValue(e, KEY_PATH));					
				}
			}
			return banners;

		}

		@Override
		protected void onPostExecute(ArrayList<String> banners) {
			BannerAdapter adapter = new BannerAdapter(BannerActivity.this, R.layout.listitem_banner, lstBanners, banners);
			lstBanners.setAdapter(adapter);

		}

	}

}
