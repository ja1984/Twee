package se.ja1984.teewee;

import java.util.ArrayList;

import android.R.integer;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class EpisodeAdapter extends ArrayAdapter<Episode> {

	private final Context context;
	private final ArrayList<Episode> episodes;
	private static  DateService dateService;
	private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
	private ArrayList<Boolean> visibleItems = new ArrayList<Boolean>();

	public EpisodeAdapter(Context context, int resource, ListView lv, ArrayList<Episode> objects)
	{
		super(context, R.layout.episode_listitem, objects);
		this.context = context;
		this.episodes = objects;
		this.dateService = new DateService();
		
		for (int i = 0; i < objects.size(); i++) {
			itemChecked.add(i, objects.get(i).getWatched().equals("1"));
		}
		
		for (int i = 0; i < objects.size(); i++) {
			visibleItems.add(i, dateService.CompareDates(episodes.get(i).getAired(), dateService.GetTodaysDate()) >= 0);
		}
		
	}

	static class ViewHolder {
		protected TextView title;
		protected TextView information;
		protected CheckBox watched;
	}

	@Override
	public View getView(final int pos, View convertView, ViewGroup parent)
	{

			if(convertView == null){
				LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.episode_listitem, null);
			}
		
			final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.chkWatched);
			final TextView title = (TextView) convertView.findViewById(R.id.txtTitle);
			final TextView information = (TextView) convertView.findViewById(R.id.txtEpisodeNumber);
			
			checkBox.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v.findViewById(R.id.chkWatched);
					new DatabaseHandler(context).ToggleEpisodeWatched("" + episodes.get(pos).getID(), cb.isChecked());
										
		            if (cb.isChecked()) {
		                itemChecked.set(pos, true);
		                // do some operations here
		            } else if (!cb.isChecked()) {
		                itemChecked.set(pos, false);
		                // do some operations here
		            }
					
				}
			});
			
			convertView.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new SeriesService().displayPlot(episodes.get(pos), getContext());
				}
			});
			
//			if(dateService.CompareDates(episodes.get(pos).getAired(), dateService.GetTodaysDate()) >= 0)
//			{
//				checkBox.setVisibility(8);
//			}
			
			checkBox.setChecked(itemChecked.get(pos));
			if(visibleItems.get(pos))
			{
				checkBox.setVisibility(8);
			}
			else
			{
				checkBox.setVisibility(View.VISIBLE);
			}
			
			title.setText(episodes.get(pos).getTitle());
			information.setText(dateService.Episodenumber(episodes.get(pos)) + " | " + dateService.DisplayDate(episodes.get(pos).getAired()));
			
			return convertView;
			       

	}
}
