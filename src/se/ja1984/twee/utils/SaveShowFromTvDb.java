package se.ja1984.twee.utils;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import se.ja1984.twee.R;
import se.ja1984.twee.models.Episode;
import se.ja1984.twee.models.Series;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.util.Log;

public class SaveShowFromTvDb extends AsyncTask<String, String, Boolean>
{
	ProgressDialog saveDialog;
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
	final Context context;
	final Boolean downloadHeader;
	
	public SaveShowFromTvDb(Context context, Boolean downloadHeader) {
		this.context = context;
		this.downloadHeader = downloadHeader;
	}

	@Override
	protected Boolean doInBackground(String... q) {
		saveDialog.setMessage(context.getString(R.string.message_download_information));
		String completeAddress = String.format(KEY_FULLURL, q[0]);
		//String completeAddress = "http://www.thetvdb.com/data/series/" + q[0] +"/all/";
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


		publishProgress(context.getString(R.string.message_download_banner));
		String image = parser.getValue(e, KEY_IMAGE);

		if(!image.equals("")){
			try {
				s.setImage(new ImageService().getBitmapFromURL(image, s.getSeriesId(), context));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		if(downloadHeader){

			publishProgress(context.getString(R.string.message_download_header));
			String header = parser.getValue(e, KEY_HEADER);
			
			if(!header.equals("")){
				try {
					s.setHeader(new ImageService().getBitmapFromURL(header,s.getSeriesId() + "_big", context));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		publishProgress(context.getString(R.string.message_download_save_series));

		s.setImdbId(parser.getValue(e, KEY_IMDBID));
		s.setName(parser.getValue(e, KEY_NAME));			
		s.setRating(parser.getValue(e, KEY_RATING));
		s.setStatus(parser.getValue(e, KEY_STATUS));
		s.setSummary(parser.getValue(e, KEY_SUMMARY));
		s.setLastUpdated(parser.getValue(e, KEY_LASTUPDATED));

		new DatabaseHandler(context).AddShow(s);

		publishProgress(context.getString(R.string.message_download_save_episodes));

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
			ep.setEpisodeId(parser.getValue(e,KEY_EPISODEID));
			ep.setWatched("0");


			Episodes.add(ep);

		}

		new DatabaseHandler(context).AddEpisodes(Episodes);


		return true;
	}

	@Override
	protected void onPreExecute(){
		saveDialog = ProgressDialog.show(context, context.getString(R.string.message_download_pleasewait), context.getString(R.string.message_download_information),true,true, new DialogInterface.OnCancelListener(){
			@Override
			public void onCancel(DialogInterface dialog) {
				SaveShowFromTvDb.this.cancel(true);
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
		Log.d("Resultat", "" + result);
		if(result)
		{
			saveDialog.cancel();
		}
		return;
		//super.onPostExecute(result);
	}

}