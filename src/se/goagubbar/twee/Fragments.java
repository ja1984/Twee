package se.goagubbar.twee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import se.goagubbar.twee.Adapters.EpisodeAdapter;
import se.goagubbar.twee.Adapters.UpcomingEpisodesAdapter;
import se.goagubbar.twee.models.Episode;
import se.goagubbar.twee.models.Series;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Fragments {

	static Activity activity;
	
	public static class SummaryFragment extends Fragment{
    	

    	
    }
    
    public static class EpisodesFragment extends Fragment{
    	String showId;
    	DateHelper dateHelper;
    	TextView markSeasonAsWatched;
    	ListView episodes;
    	
    	
    	
    	public EpisodesFragment(String showId, int totalEpisodes, int watchedEpisodes){
    		this.showId = showId;
    		this.dateHelper = new DateHelper();
    	}
    	
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	View v = inflater.inflate(R.layout.view_episodes, container, false);
        	 episodes = (ListView)v.findViewById(R.id.lstAllEpisodes);        		
        	
        	new GetEpisodesTask().execute(getActivity());
        	
        	TextView t = (TextView) episodes.findViewById(R.id.txtMarkSeasonAsWatched);      	
            return v;
        }

		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
		}
        
        
	    public class GetEpisodesTask extends AsyncTask<Context, Void, ArrayList<Episode>>{



			@Override
			protected void onPostExecute(ArrayList<Episode> result) {
				super.onPostExecute(result);
				episodes.setAdapter(new EpisodeAdapter(getActivity(), R.layout.listitem_allepisodes, episodes, result));
			}

			@Override
			protected ArrayList<Episode> doInBackground(Context... params) {
				// TODO Auto-generated method stub
				ArrayList<Episode> episodes = new DatabaseHandler(getActivity()).GetEpisodes(showId);
				return episodes;
			}

	    }
		
		
    }

    
    
    
    
    
    public static class OverviewFragment extends Fragment{
    	Series show;
    	DateHelper dateHelper;
    	ImageService imageService;
    	
    	
    	public OverviewFragment(Series show){
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
            

//            for (Episode episode : show.Episodes) {
//    			
//    			if(dateHelper.CompareDates(episode.getAired(), today) >= 0)
//    			{
//    				newEps.add(episode);
//    			}
//    		}
            
            SetProgress(v, show.getSeriesId());
            Collections.reverse(newEps);
            episodes.setAdapter(new UpcomingEpisodesAdapter(getActivity(), R.id.lstEpisodes, episodes,newEps));
            
            seriesName.setText(show.getName());
            seriesRating.setText(show.getRating());
            seriesStatus.setText("status: " + show.getStatus());

            return v;
        }
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
