package se.ja1984.twee.fragments;

import java.util.ArrayList;

import se.ja1984.twee.R;
import se.ja1984.twee.adapters.EpisodeAdapter;
import se.ja1984.twee.models.Episode;
import se.ja1984.twee.utils.DatabaseHandler;
import se.ja1984.twee.utils.DateHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class Episodes extends Fragment{
	DateHelper dateHelper;
	TextView markSeasonAsWatched;
	ListView episodes;
	static String showId;
	static EpisodeAdapter allEpisodes;

	public Episodes(){
		this.dateHelper = new DateHelper();
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	    	
    	View v = inflater.inflate(R.layout.view_episodes, container, false);
    	 episodes = (ListView)v.findViewById(R.id.lstAllEpisodes);        		
    	
    	 showId = getArguments().getString("showId");
    	 
    	new GetEpisodesTask().execute(showId);
    	 	
        return v;
    }
    
    
    public class GetEpisodesTask extends AsyncTask<String, Void, ArrayList<Episode>>{

		@Override
		protected void onPostExecute(ArrayList<Episode> result) {
			super.onPostExecute(result);
			
			allEpisodes = new EpisodeAdapter(getActivity(), R.layout.listitem_allepisodes, episodes, result);
			episodes.setAdapter(allEpisodes);
		}

		@Override
		protected ArrayList<Episode> doInBackground(String... params) {
			ArrayList<Episode> episodes = new DatabaseHandler(getActivity()).GetEpisodes(params[0]);
			return episodes;
		}

    }
	
	
    public static void MarkAllEpisodes()
    {
    	EpisodeAdapter.MarkShowAsWatched();
    	allEpisodes.notifyDataSetChanged();
    }
        
    public static void MarkLastAiredEpisodesAsWatched()
    {
    	allEpisodes.notifyDataSetChanged();
    }
    
}