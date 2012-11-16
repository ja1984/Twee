package se.goagubbar.twee.fragments;

import java.util.ArrayList;
import java.util.Collections;

import se.goagubbar.twee.R;
import se.goagubbar.twee.SeriesHelper;
import se.goagubbar.twee.adapters.UpcomingEpisodesAdapter;
import se.goagubbar.twee.models.Episode;
import se.goagubbar.twee.models.Series;
import se.goagubbar.twee.utils.DatabaseHandler;
import se.goagubbar.twee.utils.DateHelper;
import se.goagubbar.twee.utils.ImageService;
import android.app.Activity;
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
	
	
	public Overview(Series show){
		this.show = show;
		this.dateHelper = new DateHelper();
		this.imageService = new ImageService();
	}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.view_overview, container, false);
    	        	
    	activity = getActivity();
    	
        final Episode lastAiredApisode = new DatabaseHandler(activity).GetLastAiredEpisodeForShow(show.getSeriesId());
        TextView seriesName = (TextView)v.findViewById(R.id.txtSeriesName);
        TextView seriesRating = (TextView)v.findViewById(R.id.txtSeriesRating);
        TextView seriesStatus = (TextView)v.findViewById(R.id.txtSeriesStatus);
        ImageView seriesHeader = (ImageView)v.findViewById(R.id.imgSeriesHeader);
        
        ListView episodes = (ListView)v.findViewById(R.id.lstEpisodes);
        RelativeLayout lastAiredEpisode = (RelativeLayout)v.findViewById(R.id.rllHeader1);
        TextView lastAiredEpisodeTitle = (TextView)v.findViewById(R.id.txtLastAiredTitle);
        TextView lastAiredEpisodeInformation = (TextView)v.findViewById(R.id.txtLastAiredEpisodeNumber);
        final CheckBox lastAiredEpisodeWatched = (CheckBox)v.findViewById(R.id.chkWatched);
        
        if(lastAiredApisode.getID() != 0)
        {
        	lastAiredEpisodeTitle.setText(lastAiredApisode.getTitle());
        	lastAiredEpisodeInformation.setText(dateHelper.Episodenumber(lastAiredApisode) + " | " + dateHelper.DisplayDate(lastAiredApisode.getAired()));
        	lastAiredEpisodeWatched.setChecked(lastAiredApisode.getWatched().equals("1"));
        	
            lastAiredEpisodeWatched.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					new DatabaseHandler(activity).ToggleEpisodeWatched("" + lastAiredApisode.getID(), lastAiredEpisodeWatched.isChecked());
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
        
        String today = dateHelper.GetTodaysDate();
        

//        for (Episode episode : show.Episodes) {
//			
//			if(dateHelper.CompareDates(episode.getAired(), today) >= 0)
//			{
//				newEps.add(episode);
//			}
//		}
        
        SetProgress(v, show.getSeriesId());
        Collections.reverse(newEps);
        episodes.setAdapter(new UpcomingEpisodesAdapter(getActivity(), R.id.lstEpisodes, episodes,newEps));
        
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
	
}
