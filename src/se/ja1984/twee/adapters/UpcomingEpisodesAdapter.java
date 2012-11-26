package se.ja1984.twee.adapters;

import java.util.ArrayList;

import se.ja1984.twee.R;
import se.ja1984.twee.models.Episode;
import se.ja1984.twee.utils.DateHelper;
import se.ja1984.twee.utils.SeriesHelper;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class UpcomingEpisodesAdapter extends ArrayAdapter<Episode> {

	private final Context context;
	private final ArrayList<Episode> episodes;
	private DateHelper dateHelper;

	public UpcomingEpisodesAdapter(Context context, int resource, ListView lv, ArrayList<Episode> objects)
	{
		super(context, R.layout.listitem_episode, objects);
		this.context = context;
		this.episodes = objects;
		this.dateHelper = new DateHelper();		

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
			convertView = inflator.inflate(R.layout.listitem_episode, null);
		}

		final TextView title = (TextView) convertView.findViewById(R.id.txtTitle);
		final TextView information = (TextView) convertView.findViewById(R.id.txtEpisodeNumber);
		final CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.chkWatched);
		checkBox.setVisibility(View.GONE);
		convertView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				new SeriesHelper().displayPlot(episodes.get(pos), getContext());
			}
		});


		String _title = episodes.get(pos).getTitle(); 
		title.setText(_title.equals("") ? "TBA" : _title);
		information.setText(dateHelper.Episodenumber(episodes.get(pos)) + " | " + dateHelper.DisplayDate(episodes.get(pos).getAired()));

		return convertView;


	}
}
