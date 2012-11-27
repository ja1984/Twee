package se.ja1984.twee;

import java.util.ArrayList;

import se.ja1984.twee.R;
import se.ja1984.twee.adapters.CalendarAdapter;
import se.ja1984.twee.models.Episode;
import se.ja1984.twee.utils.DatabaseHandler;
import se.ja1984.twee.utils.Utils;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CalendarActivity extends FragmentActivity implements ActionBar.OnNavigationListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    ArrayList<Episode> episodes;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    String theme = prefs.getString("pref_theme", "2");
	    setTheme(Utils.GetTheme(Integer.parseInt(theme)));
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_calendar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        episodes = new DatabaseHandler(getBaseContext()).GetAllEpisodesForGivenTimePeriod(2);
                
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[]{
                                getString(R.string.section_dropdown_today),
                                getString(R.string.section_dropdown_tomorrow),
                                getString(R.string.section_dropdown_week),
                        }),
                this);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public boolean onNavigationItemSelected(int position, long id) {
        // When the given tab is selected, show the tab contents in the container
        Fragment fragment = new DummySectionFragment(episodes);
        Bundle args = new Bundle();
        args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        return true;
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
    	ArrayList<Episode> episodes;
        public DummySectionFragment(ArrayList<Episode> ep) {
        	this.episodes = ep;
        }
        
        public static final String ARG_SECTION_NUMBER = "section_number";
               

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	ListView listView = new ListView(getActivity());
            Bundle args = getArguments();
            CalendarAdapter calendarAdapter = new CalendarAdapter(getActivity(), R.id.lstEpisodes, listView, new DatabaseHandler(getActivity()).GetAllEpisodesForGivenTimePeriod(args.getInt(ARG_SECTION_NUMBER)));
            listView.setAdapter(calendarAdapter);
            return listView;
        }
    }
}
