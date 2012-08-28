package se.goagubbar.twee;

import java.util.ArrayList;

import se.goagubbar.twee.Models.Episode;
import se.goagubbar.twee.Models.Series;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Adapters {

	static class viewHolder
	{
		ImageView image;
		TextView information;
		String seriesId;
		String season;
	}

	public static class SeriesAdapter extends ArrayAdapter<Series> {

		private final Context context;
		private final ArrayList<Series> series;
		private DateHelper dateHelper;
		private ImageService imageService;
		Object mActionMode;
		
		public SeriesAdapter(Context context, int resource, ListView lv, ArrayList<Series> objects)
		{
			super(context, R.layout.listitem_series, objects);
			this.context = context;
			this.series = objects;
			imageService = new ImageService();
			dateHelper = new DateHelper();			
		}

		
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			
			viewHolder holder;
			Series s = series.get(position);

		
			
			if(convertView == null)
			{
				convertView = View.inflate(context, R.layout.listitem_series, null);
				holder = new viewHolder();

				holder.image = (ImageView)convertView.findViewById(R.id.imgSeriesImage);
				holder.information = (TextView)convertView.findViewById(R.id.txtSeriesName);
				
//				convertView.setOnLongClickListener(new View.OnLongClickListener() {
//
//					public boolean onLongClick(View v) {
//						if(mActionMode != null){
//							return false;
//						}
//
//						mActionMode = getActivity().startActionMode(mActionModeCallback);
//						v.setSelected(true);
//						return true;
//					}
//				});
				
//				ActionMode.Callback mActionModeCallback = new ActionMode.Callback(){
//			    	//Called when the acion mode is creates, startActionMode() was called
//			    	public boolean onCreateActionMode(ActionMode mode, Menu menu)
//			    	{
//			    		MenuInflater inflater = mode.getMenuInflater();
//			    		inflater.inflate(R.menu.menu_home, menu);
//			    		return true;
//			    	}
//			    	
//			    	
//			    	//Called each time the action mode is shown. Always called after onCreateActionmode
//			    	//may be called multiple times if the mode is invalidated.    
//			    	public boolean onPrepareActionMode(ActionMode mode, Menu menu)
//			    	{
//			    		return false;
//			    	}
//			    	
//			    	//Called when the user selects a contextual menu item
//			    	public boolean onActionItemClicked(ActionMode mode, MenuItem item)
//			    	{
//			    		switch (item.getItemId()) {
//						case R.id.menu_add:
//							//Do stuff
//							mode.finish(); //Action picked so close tha CAB
//							return true;
//						default:
//							return false;
//						}
//			    		
//			    	}
//			    	
//			    	
//			    	//Called when user closes the action mode
//					public void onDestroyActionMode(ActionMode mode) {
//						// TODO Auto-generated method stub
//						mActionMode = null;
//					}
//			    	
//			    };
				
				
				//holder.seriesId = (String)convertView.getTag(R.string.homeactivity_tag_seriesid);
				convertView.setTag(holder);
			}
			else
			{
				holder = (viewHolder)convertView.getTag();
			}


			
			
			if(s != null)
			{
				Episode e = series.get(position).Episodes.get(0);

				Bitmap bm = imageService.GetImage(series.get(position).getImage(), getContext());
				convertView.setTag(R.string.homeactivity_tag_id,s.getID());
				convertView.setTag(R.string.homeactivity_tag_seriesid,s.getSeriesId());
				if(bm != null){
					holder.image.setImageBitmap(bm);
				}
				if(e.getAired() != null)
				{
					holder.information.setText(dateHelper.Episodenumber(e) + " " + e.getTitle() + " - " + dateHelper.DisplayDate(e.getAired()));
				}
				else
				{
					holder.information.setText("No information");
				}

			}

			return convertView;
		}
	}

	public static class EpisodeAdapter extends ArrayAdapter<Episode> {

		private final Context context;
		private final ArrayList<Episode> episodes;
		private DateHelper dateHelper;
		private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
		private ArrayList<Boolean> visibleItems = new ArrayList<Boolean>();
		private ArrayList<Boolean> showSeasonBanner = new ArrayList<Boolean>();
		private String season;

		public EpisodeAdapter(Context context, int resource, ListView lv, ArrayList<Episode> objects)
		{
			super(context, R.layout.listitem_allepisodes, objects);
			this.context = context;
			this.episodes = objects;
			this.dateHelper = new DateHelper();
			this.season = "0";

			for (int i = 0; i < objects.size(); i++) {
				itemChecked.add(i, objects.get(i).getWatched().equals("1"));
				visibleItems.add(i, dateHelper.CompareDates(episodes.get(i).getAired(), dateHelper.GetTodaysDate()) >= 0);
				showSeasonBanner.add(i,objects.get(i).getSeason().equals(this.season));
				
				this.season = objects.get(i).getSeason();		
			}	
			

		}

		static class ViewHolder {
			protected TextView title;
			protected TextView information;
			protected CheckBox watched;
			protected LinearLayout seasonWrapper;
			protected TextView seasonNumber;
		}

		@Override
		public View getView(final int pos, View convertView, ViewGroup parent)
		{

			if(convertView == null){
				LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.listitem_allepisodes, null);
			}

			final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.chkWatched);
			final TextView seasonCheckBox = (TextView)convertView.findViewById(R.id.txtMarkSeasonAsWatched);
			final TextView title = (TextView) convertView.findViewById(R.id.txtTitle);
			final TextView information = (TextView) convertView.findViewById(R.id.txtEpisodeNumber);
			final TextView seasonNumber = (TextView) convertView.findViewById(R.id.txtSeasonNumber);
			final RelativeLayout seasonWrapper = (RelativeLayout) convertView.findViewById(R.id.seasonTest);
			
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

			
			seasonCheckBox.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					MarkSeasonAsWatched(episodes.get(pos).getSeriesId(), episodes.get(pos).getSeason());
					
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
			
			
			if(!showSeasonBanner.get(pos))
		{
				seasonWrapper.setVisibility(View.VISIBLE);
			}
			else
			{
				seasonWrapper.setVisibility(View.GONE);
			}
			
			
			seasonNumber.setText("Season " + episodes.get(pos).getSeason());
			title.setText(episodes.get(pos).getTitle());
			information.setText(dateHelper.Episodenumber(episodes.get(pos)) + " | " + dateHelper.DisplayDate(episodes.get(pos).getAired()));

			return convertView;


		}
		
		private void MarkSeasonAsWatched(String seriesId, String season)
		{
			new DatabaseHandler(context).ToggleSeasonWatched("" + seriesId, season, true);
			Toast.makeText(context, R.string.message_season_watched, Toast.LENGTH_SHORT).show();
			
			for (int i = 0; i < episodes.size(); i++) {
				
				if(episodes.get(i).getSeason().equals(season))
				{
					itemChecked.set(i, true);
				}
				notifyDataSetChanged();
			}	
			
		}
		
	}

	public static class UpcomingEpisodesAdapter extends ArrayAdapter<Episode> {

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
		
			

			title.setText(episodes.get(pos).getTitle());
			information.setText(dateHelper.Episodenumber(episodes.get(pos)) + " | " + dateHelper.DisplayDate(episodes.get(pos).getAired()));

			return convertView;


		}
	}

	
	public static class CalendarAdapter extends ArrayAdapter<Episode> {

		private final Context context;
		private final ArrayList<Episode> episodes;
		private DateHelper dateHelper;
		private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
		private ArrayList<Boolean> visibleItems = new ArrayList<Boolean>();

		public CalendarAdapter(Context context, int resource, ListView lv, ArrayList<Episode> objects)
		{
			super(context, R.layout.listitem_episode, objects);
			this.context = context;
			this.episodes = objects;
			this.dateHelper = new DateHelper();

			for (int i = 0; i < objects.size(); i++) {
				itemChecked.add(i, objects.get(i).getWatched().equals("1"));
			}

			for (int i = 0; i < objects.size(); i++) {
				visibleItems.add(i, dateHelper.CompareDates(episodes.get(i).getAired(), dateHelper.GetTodaysDate()) > 0);
			}

		}

		static class ViewHolder {
			protected TextView title;
			protected TextView information;
			protected CheckBox watched;
			protected TextView extra;
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
			final TextView extra = (TextView)convertView.findViewById(R.id.txtExtra);
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
			extra.setVisibility(View.VISIBLE);
			extra.setText(episodes.get(pos).getLastUpdated());
			title.setText(episodes.get(pos).getTitle());
			information.setText(dateHelper.Episodenumber(episodes.get(pos)) + " | " +dateHelper.DisplayDate(episodes.get(pos).getAired()));

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
