package se.ja1984.twee.fragments;

import java.util.ArrayList;
import se.ja1984.twee.R;
import se.ja1984.twee.adapters.EpisodeAdapter;
import se.ja1984.twee.adapters.UpcomingEpisodesAdapter;
import se.ja1984.twee.models.Episode;
import se.ja1984.twee.models.Series;
import se.ja1984.twee.utils.DatabaseHandler;
import se.ja1984.twee.utils.DateHelper;
import se.ja1984.twee.utils.ImageService;
import se.ja1984.twee.utils.SeriesHelper;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Overview extends Fragment{
	Series show;
	DateHelper dateHelper;
	ImageService imageService;
	static Activity activity;
	ListView episodes;
	View v;
	static Episode lastAiredApisode;
	static CheckBox lastAiredEpisodeWatched;

	public Overview(Series show){
		this.show = show;
		this.dateHelper = new DateHelper();
		this.imageService = new ImageService();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.view_overview, container, false);

		activity = getActivity();

		lastAiredApisode = new DatabaseHandler(activity).GetLastAiredEpisodeForShow(show.getSeriesId());
		TextView seriesName = (TextView)v.findViewById(R.id.txtSeriesName);
		TextView seriesRating = (TextView)v.findViewById(R.id.txtSeriesRating);
		TextView seriesStatus = (TextView)v.findViewById(R.id.txtSeriesStatus);
		ImageView seriesHeader = (ImageView)v.findViewById(R.id.imgSeriesHeader);

		episodes = (ListView)v.findViewById(R.id.lstEpisodes);
		RelativeLayout lastAiredEpisode = (RelativeLayout)v.findViewById(R.id.rllHeader1);
		TextView lastAiredEpisodeTitle = (TextView)v.findViewById(R.id.txtLastAiredTitle);
		TextView lastAiredEpisodeInformation = (TextView)v.findViewById(R.id.txtLastAiredEpisodeNumber);
		lastAiredEpisodeWatched = (CheckBox)v.findViewById(R.id.chkWatched);

		if(lastAiredApisode.getID() != 0)
		{
			lastAiredEpisodeTitle.setText(lastAiredApisode.getTitle());
			lastAiredEpisodeInformation.setText(dateHelper.Episodenumber(lastAiredApisode) + " | " + dateHelper.DisplayDate(lastAiredApisode.getAired()));
			lastAiredEpisodeWatched.setChecked(lastAiredApisode.getWatched().equals("1"));

			lastAiredEpisodeWatched.setOnClickListener(new View.OnClickListener() {

				public void onClick(View view) {
					new DatabaseHandler(activity).ToggleEpisodeWatched("" + lastAiredApisode.getID(), lastAiredEpisodeWatched.isChecked());
					EpisodeAdapter.MarkLastAiredEpisodesAsWatched(lastAiredApisode,lastAiredEpisodeWatched.isChecked());
					Episodes.MarkLastAiredEpisodesAsWatched();
					SetProgress(v, show.getSeriesId());
				}
			});

			lastAiredEpisode.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {

					new SeriesHelper().displayPlot(lastAiredApisode, activity);

				}
			});


		}
		else
		{
			lastAiredEpisode.setVisibility(View.GONE);
			TextView lastAiredHeader = (TextView)v.findViewById(R.id.txtLastAired);
			lastAiredHeader.setVisibility(View.GONE);
		}



		if(show.getHeader() != null){
			seriesHeader.setImageBitmap(imageService.GetImage(show.getHeader(), activity));
		}
		else
		{
			if(show.getImage() != null){
				seriesHeader.setImageBitmap(imageService.GetImage(show.getImage(), activity));
			}
		}


		ArrayList<Episode> newEps = new ArrayList<Episode>();
		
		new UpcomingEpisodesTask().execute();

		SetProgress(v, show.getSeriesId());

		seriesName.setText(show.getName());
		seriesRating.setText(show.getRating());
		seriesStatus.setText("status: " + show.getStatus());

		return v;
	}

	public static void SetProgress(View view, String seriesId)
	{
		ArrayList<Episode> episodes = new DatabaseHandler(activity).GetAiredEpisodes(seriesId);
		int watched = 0;
		int totalEpisodes = episodes.size();

		for (Episode episode : episodes) {		
			if(episode.getWatched().equals("1"))
			{
				watched ++;
			}
		}
				
		ProgressBar progressWatched = (ProgressBar)view.findViewById(R.id.pgrWatched);
		progressWatched.setMax(totalEpisodes);
		progressWatched.setProgress(watched);

		TextView textWatched = (TextView)view.findViewById(R.id.txtWatched);
		textWatched.setText( watched +  "/" + totalEpisodes);
	}

	private class UpcomingEpisodesTask extends AsyncTask<Void, Void, ArrayList<Episode>>
	{

		@Override
		protected ArrayList<Episode> doInBackground(Void... params) {
			ArrayList<Episode> episodes = new DatabaseHandler(activity).GetNextEpisodesForShow(show.getSeriesId());
			return episodes;
		}

		@Override
		protected void onPostExecute(ArrayList<Episode> result) {
			episodes.setAdapter(new UpcomingEpisodesAdapter(getActivity(), R.id.lstEpisodes, episodes,result));
			super.onPostExecute(result);
		}
	}
	
	
	public static void MarkLastAiredEpisodeAsWatched(Episode episode, Boolean isChecked)
	{
		if(episode.getSeason().equals(lastAiredApisode.getSeason()) && episode.getEpisode().equals(lastAiredApisode.getEpisode()))
		{
			MarkLastAiredEpisodeAsWatched(isChecked);
		}
	}
	
	public static void MarkLastAiredEpisodeAsWatched(Boolean isChecked)
	{
			lastAiredEpisodeWatched.setChecked(isChecked);
	}
	
}
