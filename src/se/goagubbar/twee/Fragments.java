package se.goagubbar.twee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;

import se.goagubbar.twee.Adapters.EpisodeAdapter;
import se.goagubbar.twee.Adapters.UpcomingEpisodesAdapter;
import se.goagubbar.twee.Models.Episode;
import se.goagubbar.twee.Models.Series;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
        	Button btnImdb = (Button)v.findViewById(R.id.btnOpenImdb);
        	
        	
        	summary.setText(s.getSummary());
        	
        	StringTokenizer separatedActors = new StringTokenizer(s.getActors(),"|");
        	
        	String _actors = "";
        	for (int i = 0; i <= separatedActors.countTokens(); i++) {
        			_actors += separatedActors.nextToken() + "\n";
			}
        	
        	actors.setText(_actors);
        	
        	
        	btnImdb.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					String uri = "imdb:///title/" + s.getImdbId();
					Intent test = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				    if(getActivity().getPackageManager().resolveActivity(test, 0) != null)
				    {
				    	startActivity(test);
				    }
				    else
				    {
				    	String uri2 = "http://m.imdb.com/title/" + s.getImdbId();
				    	Intent imdbIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri2));               
				    	startActivity(imdbIntent);

				    }
				}
			});
        	
        	
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
        		
        	episodes.setAdapter(new EpisodeAdapter(getActivity(), R.layout.listitem_allepisodes, episodes, s.Episodes));
        	
        	TextView t = (TextView) episodes.findViewById(R.id.txtMarkSeasonAsWatched);      	

        	
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
        	        	
        	activity = getActivity();
        	
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
            

            for (Episode episode : s.Episodes) {
    			
    			if(dateHelper.CompareDates(episode.getAired(), today) >= 0)
    			{
    				newEps.add(episode);
    			}
    		}
            
            SetProgress(v, s.getSeriesId());
            Collections.reverse(newEps);
            episodes.setAdapter(new UpcomingEpisodesAdapter(getActivity(), R.id.lstEpisodes, episodes,newEps));
            
            seriesName.setText(s.getName());
            seriesRating.setText(s.getRating());
            seriesStatus.setText("status: " + s.getStatus());

            return v;
        }
    }
    
	
    public static void SetProgress(View view, String seriesId)
    {
    	
    	Log.d("test","" + seriesId);
    	ArrayList<Episode> episodes = new DatabaseHandler(activity).GetAiredEpisodes(seriesId);
    	Log.d("test","" + episodes.size());
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
