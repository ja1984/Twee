package se.goagubbar.twee;

import java.util.ArrayList;

import se.goagubbar.twee.Models.Episode;
import se.goagubbar.twee.Models.Series;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Adapters {

	public static class SeriesAdapter extends ArrayAdapter<Series> {

		private final Context context;
		private final ArrayList<Series> series;
		private DateHelper dateHelper;

		public SeriesAdapter(Context context, int resource, ListView lv, ArrayList<Series> objects)
		{
			super(context, R.layout.listitem_series, objects);
			this.context = context;
			this.series = objects;
			dateHelper = new DateHelper();
		}

		@Override
		public View getView(int position, View ConvertView, ViewGroup parent)
		{
			LayoutInflater inflater  = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.listitem_series, parent, false);

			rowView.setTag(series.get(position).getID());
			ImageView img = (ImageView)rowView.findViewById(R.id.imgSeriesImage);
			TextView tv = (TextView)rowView.findViewById(R.id.txtSeriesName);

			Episode e = series.get(position).Episodes.get(0);

			if(e.getAired() != null)
			{
				tv.setText( dateHelper.Episodenumber(e) + " " + e.getTitle() + " - " + dateHelper.DisplayDate(e.getAired()));
			}
			else
			{
				tv.setVisibility(View.GONE);
			}

			Bitmap bm = new ImageService().GetImage(series.get(position).getImage(), getContext());

			if(bm != null){
				img.setImageBitmap(bm);
			}
			//tv.setText(series.get(position).getName());

			return rowView;
		}
	}

	public static class EpisodeAdapter extends ArrayAdapter<Episode> {

		private final Context context;
		private final ArrayList<Episode> episodes;
		private DateHelper dateHelper;
		private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
		private ArrayList<Boolean> visibleItems = new ArrayList<Boolean>();

		public EpisodeAdapter(Context context, int resource, ListView lv, ArrayList<Episode> objects)
		{
			super(context, R.layout.listitem_episode, objects);
			this.context = context;
			this.episodes = objects;
			this.dateHelper = new DateHelper();

			for (int i = 0; i < objects.size(); i++) {
				itemChecked.add(i, objects.get(i).getWatched().equals("1"));
			}

			for (int i = 0; i < objects.size(); i++) {
				visibleItems.add(i, dateHelper.CompareDates(episodes.get(i).getAired(), dateHelper.GetTodaysDate()) >= 0);
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
				convertView = inflator.inflate(R.layout.listitem_episode, null);
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
					new SeriesHelper().displayPlot(episodes.get(pos), getContext());
				}
			});

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
			information.setText(dateHelper.Episodenumber(episodes.get(pos)) + " | " + dateHelper.DisplayDate(episodes.get(pos).getAired()));

			return convertView;


		}
	}

	public static class SearchAdapter extends ArrayAdapter<Series>
	{

		private final Context context;
		private final ArrayList<Series> series;




		public SearchAdapter(Context context, int resource, ArrayList<Series> objects) {
			super(context, R.layout.listitem_searchresult, objects);
			this.context = context;
			this.series = objects;
		}



		@Override
		public View getView(int position, View ConvertView, ViewGroup parent){
			LayoutInflater inflater  = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View rowView = inflater.inflate(R.layout.listitem_searchresult, parent, false);

			TextView name = (TextView)rowView.findViewById(R.id.txtName);
			TextView year = (TextView)rowView.findViewById(R.id.txtYear);

			rowView.setTag(series.get(position).getID());

			name.setText(series.get(position).getName());
			year.setText(series.get(position).getAirs());

			return rowView;
		}

	}
}
