package se.goagubbar.twee;

import java.util.ArrayList;
import java.util.StringTokenizer;

import se.goagubbar.twee.Adapters.EpisodeAdapter;
import se.goagubbar.twee.Models.Episode;
import se.goagubbar.twee.Models.Series;
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

public class Fragments {

	public static class SummaryFragment extends Fragment{
    	Series s;
    	public SummaryFragment(Series s){
    		this.s = s;
    	}
    	
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	
        	View v = inflater.inflate(R.layout.view_summary, container, false);
        	TextView summary = (TextView)v.findViewById(R.id.txtSummary);
        	TextView actors = (TextView)v.findViewById(R.id.txtActors);
        	        	
        	summary.setText(s.getSummary());
        	
        	StringTokenizer separatedActors = new StringTokenizer(s.getActors(),"|");
        	
        	String _actors = "";
        	for (int i = 0; i <= separatedActors.countTokens(); i++) {
        			_actors += separatedActors.nextToken() + "\n";
			}
        	
        	actors.setText(_actors);
        	
            return v;
        }

    	
    }
    
    public static class EpisodesFragment extends Fragment{
    	
    	Series s;
    	DateHelper dateHelper;
    	
    	public EpisodesFragment(Series s, int totalEpisodes, int watchedEpisodes){
    		this.s = s;
    		this.dateHelper = new DateHelper();
    	}
    	
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	View v = inflater.inflate(R.layout.view_episodes, container, false);
        	
        	ListView episodes = (ListView)v.findViewById(R.id.lstAllEpisodes);
        		
        	episodes.setAdapter(new EpisodeAdapter(getActivity(), R.layout.listitem_episode, episodes, s.Episodes));
        	
            return v;
        }
    	
    }
    
    public static class OverviewFragment extends Fragment{
    	Series s;
    	DateHelper dateHelper;
    	ImageService imageService;
    	
    	public OverviewFragment(Series s){
    		this.s = s;
    		this.dateHelper = new DateHelper();
    		this.imageService = new ImageService();
    	}
    	
    	@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        	View v = inflater.inflate(R.layout.view_overview, container, false);
        	        	
        	final Activity activity = getActivity();
        	
            final Episode lastAiredApisode = new DatabaseHandler(activity).GetLastAiredEpisodeForSeries(s.getSeriesId());
            TextView seriesName = (TextView)v.findViewById(R.id.txtSeriesName);
            TextView seriesRating = (TextView)v.findViewById(R.id.txtSeriesRating);
            TextView seriesStatus = (TextView)v.findViewById(R.id.txtSeriesStatus);
            ImageView seriesHeader = (ImageView)v.findViewById(R.id.imgSeriesHeader);
            ListView episodes = (ListView)v.findViewById(R.id.lstEpisodes);
            RelativeLayout lastAiredEpisode = (RelativeLayout)v.findViewById(R.id.rllHeader1);
            TextView lastAiredEpisodeTitle = (TextView)v.findViewById(R.id.txtLastAiredTitle);
            TextView lastAiredEpisodeInformation = (TextView)v.findViewById(R.id.txtLastAiredEpisodeNumber);
            final CheckBox lastAiredEpisodeWatched = (CheckBox)v.findViewById(R.id.chkWatched);
            if(lastAiredApisode != null)
            {
            	lastAiredEpisodeTitle.setText(lastAiredApisode.getTitle());
            	lastAiredEpisodeInformation.setText(dateHelper.Episodenumber(lastAiredApisode) + " | " + dateHelper.DisplayDate(lastAiredApisode.getAired()));
            	lastAiredEpisodeWatched.setChecked(lastAiredApisode.getWatched().equals("1"));
            	
            }
            
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
            
            
            if(s.getHeader() != null){
            	seriesHeader.setImageBitmap(imageService.GetImage(s.getHeader(), activity));
            }
            else
            {
            	if(s.getImage() != null){
                	seriesHeader.setImageBitmap(imageService.GetImage(s.getImage(), activity));
                }
            }

            
            ArrayList<Episode> newEps = new ArrayList<Episode>();
            
            String today = dateHelper.GetTodaysDate();
            
            int watched = 0;
            int numberOfEpisodes = s.Episodes.size();
            for (Episode episode : s.Episodes) {
    			if(episode.getWatched().equals("1"))
    			{
    				watched ++;
    			}
    			
    			if(dateHelper.CompareDates(episode.getAired(), today) >= 0)
    			{
    				newEps.add(episode);
    			}
    		}
            
            TextView textWatched = (TextView)v.findViewById(R.id.txtWatched);
            ProgressBar progressWatched = (ProgressBar)v.findViewById(R.id.pgrWatched);
            
            progressWatched.setMax(numberOfEpisodes);
            progressWatched.setProgress(watched);
            
            textWatched.setText(watched + "/" + numberOfEpisodes);
            
            episodes.setAdapter(new EpisodeAdapter(getActivity(), R.id.lstEpisodes, episodes,newEps));
            
            seriesName.setText(s.getName());
            seriesRating.setText(s.getRating());
            seriesStatus.setText("status: " + s.getStatus());

            return v;
        }
    }
    
	
}
