package se.ja1984.teewee;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OverviewActivity extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
     * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    ArrayList<Fragment> fragments;
    Series series;
    
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	SharedPreferences settings = getSharedPreferences("Twee", 0);
	    int theme = settings.getInt("Theme", R.style.Light);
		setTheme(theme);
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        Bundle extras = getIntent().getExtras();
        
        String seriesId = extras.getString("SeriesId");
        
        series = new DatabaseHandler(getBaseContext()).getSeriesById(seriesId);
        
        getActionBar().setTitle(series.getName());
        
        fragments = new ArrayList<Fragment>();
        fragments.add(new SummaryFragment(series));
        fragments.add(new OverviewFragment(series));
        fragments.add(new EpisodesFragment(series));
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_overview, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
                
            case R.id.menu_markseries:
            	new DatabaseHandler(getBaseContext()).MarkSeriesAsWatched(series.getSeriesId());
            	Toast.makeText(getBaseContext(), R.string.message_series_watched, Toast.LENGTH_SHORT).show();
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
                case 0: return getString(R.string.title_section1).toUpperCase();
                case 1: return getString(R.string.title_section2).toUpperCase();
                case 2: return getString(R.string.title_section3).toUpperCase();
            }
            return null;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    
    public static class SummaryFragment extends Fragment{
    	Series s;
    	public SummaryFragment(Series s){
    		this.s = s;
    	}
    	
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	
        	View v = inflater.inflate(R.layout.summary_layout, container, false);
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
    	
    	public EpisodesFragment(Series s){
    		this.s = s;
    	}
    	
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	View v = inflater.inflate(R.layout.episodes_layout, container, false);
        	
        	ListView episodes = (ListView)v.findViewById(R.id.lstAllEpisodes);
        		
        	episodes.setAdapter(new EpisodeAdapter(getActivity(), R.layout.episode_listitem, episodes, s.Episodes));
        	
            return v;
        }
    	
    }
    
    public static class OverviewFragment extends Fragment{
    	Series s;
    	public OverviewFragment(Series s){
    		this.s = s;
    	}
    	
    	@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        	View v = inflater.inflate(R.layout.activity_details, container, false);
        	        	
        	final Activity activity = getActivity();
        	
        	DateService dateService = new DateService();
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
            	lastAiredEpisodeInformation.setText(dateService.Episodenumber(lastAiredApisode) + " | " + dateService.DisplayDate(lastAiredApisode.getAired()));
            	lastAiredEpisodeWatched.setChecked(lastAiredApisode.getWatched().equals("1"));
            	
            }
            
            lastAiredEpisodeWatched.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					new DatabaseHandler(activity).ToggleEpisodeWatched("" + lastAiredApisode.getID(), lastAiredEpisodeWatched.isChecked());
				}
			});
            
            lastAiredEpisode.setOnClickListener(new View.OnClickListener() {
    			
    			public void onClick(View v) {
    				
    				new SeriesService().displayPlot(lastAiredApisode, activity);
    			
    			}
    		});
            
            
            if(s.getHeader() != null){
            	seriesHeader.setImageBitmap(new ImageHelper().GetImage(s.getHeader(), activity));
            }
            else
            {
            	if(s.getImage() != null){
                	seriesHeader.setImageBitmap(new ImageHelper().GetImage(s.getImage(), activity));
                }
            }

            
            ArrayList<Episode> newEps = new ArrayList<Episode>();
            
            String today = dateService.GetTodaysDate();
            
            int watched = 0;
            int numberOfEpisodes = s.Episodes.size();
            for (Episode episode : s.Episodes) {
    			if(episode.getWatched().equals("1"))
    			{
    				watched ++;
    			}
    			
    			if(dateService.CompareDates(episode.getAired(), today) >= 0)
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
