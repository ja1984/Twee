package se.ja1984.teewee;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailsActivity extends Activity {

	String value;
	DateService dateService;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            value = extras.getString("SeriesId");
        }
        
        dateService = new DateService();
        Series s = new DatabaseHandler(this).getSeriesById(value);
        final Episode lastAiredApisode = new DatabaseHandler(this).GetLastAiredEpisodeForSeries(s.getSeriesId());
        getActionBar().setTitle(s.getName());
        TextView seriesName = (TextView)findViewById(R.id.txtSeriesName);
        ImageView seriesHeader = (ImageView)findViewById(R.id.imgSeriesHeader);
        ListView episodes = (ListView)findViewById(R.id.lstEpisodes);
        RelativeLayout test = (RelativeLayout)findViewById(R.id.rllHeader1);
        TextView lastAiredEpisodeTitle = (TextView)findViewById(R.id.txtLastAiredTitle);
        TextView lastAiredEpisodeInformation = (TextView)findViewById(R.id.txtLastAiredEpisodeNumber);
        
        if(lastAiredApisode != null)
        {
        	lastAiredEpisodeTitle.setText(lastAiredApisode.getTitle());
        	lastAiredEpisodeInformation.setText(dateService.Episodenumber(lastAiredApisode) + " | " + dateService.DisplayDate(lastAiredApisode.getAired()));
        }
        
        test.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				new SeriesService().displayPlot(lastAiredApisode,DetailsActivity.this);
			
			}
		});
        
        
        if(s.getHeader() != null){
        	seriesHeader.setImageBitmap(new ImageHelper().GetImage(s.getHeader(), this));
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
        
        TextView textWatched = (TextView)findViewById(R.id.txtWatched);
        ProgressBar progressWatched = (ProgressBar)findViewById(R.id.pgrWatched);
        
        progressWatched.setMax(numberOfEpisodes);
        progressWatched.setProgress(watched);
        
        textWatched.setText(watched + "/" + numberOfEpisodes);
        
        seriesName.setText(s.getName());

        episodes.setAdapter(new EpisodeAdapter(this, R.layout.episode_listitem, episodes, newEps));
        
    }

//    private String isCheckedOrNot(CheckBox checkbox) {
//        if(checkbox.isChecked())
//        return "is checked";
//        else
//        return "is not checked";
//    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_details, menu);
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
    
    
}
