package se.ja1984.teewee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

	

public class EpisodeActivity extends Activity {

	String episodeId;
	String seriesId;
	DateService dateService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        Bundle extras = getIntent().getExtras();
        dateService = new DateService();
        if (extras != null) {
            episodeId = extras.getString("EpisodeId");
            seriesId = extras.getString("SeriesId");
            
            Episode e = new DatabaseHandler(this).GetEpisodeById(episodeId);
        
            getActionBar().setTitle(e.getTitle());
            
            TextView plot = (TextView)findViewById(R.id.txtEpisodePlot);
            TextView extra = (TextView)findViewById(R.id.txtEpisodeExtra);
            
            plot.setText(e.getSummary());
            extra.setText(dateService.Episodenumber(e) + " | " + dateService.DisplayDate(e.getAired()));
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_episode, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	Intent intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("SeriesId", seriesId);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
