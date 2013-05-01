package se.ja1984.twee.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se.ja1984.twee.R;
import se.ja1984.twee.UnwatchedActivity;
import se.ja1984.twee.models.Episode;
import se.ja1984.twee.utils.DatabaseHandler;
import se.ja1984.twee.utils.DateHelper;
import se.ja1984.twee.utils.ImageService;
import se.ja1984.twee.utils.SeriesHelper;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UnwatchedAdapter extends ArrayAdapter<Episode> {


	public static Map<String, Bitmap> cache;

	private final Context context;
	private final ArrayList<Episode> episodes;
	private DateHelper dateHelper;
	private ArrayList<Boolean> displayShowName = new ArrayList<Boolean>();
	int resource;
	private String lastShow;

	public UnwatchedAdapter(Context context, int resource, ListView lv, ArrayList<Episode> objects)
	{
		super(context, R.layout.listitem_backlog, objects);
		this.context = context;
		this.episodes = objects;
		this.dateHelper = new DateHelper();
		this.resource = resource;

		this.lastShow = "";
		for (int i = 0; i < objects.size(); i++) {
			displayShowName.add(i,objects.get(i).getSeriesId().equals(lastShow));		
			this.lastShow = objects.get(i).getSeriesId();
		}

	}

	static class ViewHolder {
		protected RelativeLayout showSeparator;
		protected TextView txtShowName;
		protected TextView txtEpisodeInformation;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent)
	{
		ViewHolder holder;

		final Episode e = episodes.get(pos);

		if(convertView == null){

			convertView = View.inflate(context, resource, null);
			holder = new ViewHolder();

			holder.showSeparator = (RelativeLayout)convertView.findViewById(R.id.showSeparator);
			holder.txtShowName = (TextView)convertView.findViewById(R.id.txtShowName);
			holder.txtEpisodeInformation = (TextView)convertView.findViewById(R.id.txtEpisodeInformation);	

			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		convertView.setTag(R.string.homeactivity_tag_id,e.getID());
		

		if(displayShowName.get(pos)){
			holder.showSeparator.setVisibility(View.GONE); 
		}
		else{
			holder.showSeparator.setVisibility(View.VISIBLE);
			holder.txtShowName.setText(e.getSeriesId());
		}

				
		holder.txtEpisodeInformation.setText(new DateHelper().Episodenumber(e) + " - " + e.getTitle() + " (" + new DateHelper().ShortDisplayDate(e.getAired()) + ")");

		holder.txtEpisodeInformation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			new SeriesHelper().displayPlot(e, getContext());
			
			}
		});	
		
		
		holder.txtEpisodeInformation.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				new DatabaseHandler(getContext()).ToggleEpisodeWatched(e.getID() + "", true);
				v.setVisibility(View.GONE);
				return true;
			}
		});
		
		
		return convertView;


	}

}
