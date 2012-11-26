package se.ja1984.twee.fragments;

import java.util.StringTokenizer;

import se.ja1984.twee.R;
import se.ja1984.twee.models.Series;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Summary extends Fragment {
	Series show;
	
	public Summary(Series show){
		this.show = show;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	View v = inflater.inflate(R.layout.view_summary, container, false);
    	TextView summary = (TextView)v.findViewById(R.id.txtSummary);
    	TextView actors = (TextView)v.findViewById(R.id.txtActors);
    	Button btnImdb = (Button)v.findViewById(R.id.btnOpenImdb);
    	
    	
    	summary.setText(show.getSummary());
    	
    	StringTokenizer separatedActors = new StringTokenizer(show.getActors(),"|");
    	
    	String _actors = "";
    	for (int i = 0; i <= separatedActors.countTokens(); i++) {
    			_actors += separatedActors.nextToken() + "\n";
		}
    	
    	actors.setText(_actors);
    	
    	
    	btnImdb.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String uri = "imdb:///title/" + show.getImdbId();
				Intent test = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			    if(getActivity().getPackageManager().resolveActivity(test, 0) != null)
			    {
			    	startActivity(test);
			    }
			    else
			    {
			    	String uri2 = "http://m.imdb.com/title/" + show.getImdbId();
			    	Intent imdbIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri2));               
			    	startActivity(imdbIntent);

			    }
			}
		});
    	
    	
        return v;
    }
}
