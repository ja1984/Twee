package se.ja1984.twee.adapters;

import java.util.ArrayList;

import se.ja1984.twee.R;
import se.ja1984.twee.models.Episode;
import se.ja1984.twee.utils.DateHelper;
import se.ja1984.twee.utils.ImageService;
import se.ja1984.twee.utils.SeriesHelper;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CalendarAdapter extends ArrayAdapter<Episode> {

	private final Context context;
	private final ArrayList<Episode> episodes;
	private DateHelper dateHelper;
	private ImageService imageService;
	private ArrayList<Boolean> displayDateWrapper = new ArrayList<Boolean>();

	public CalendarAdapter(Context context, int resource, ListView lv, ArrayList<Episode> objects)
	{
		super(context, R.layout.listitem_calendar, objects);
		this.context = context;
		this.episodes = objects;
		this.dateHelper = new DateHelper();
		this.imageService = new ImageService();

		String lastDate = "";
		for (int i = 0; i < objects.size(); i++) {
			displayDateWrapper.add(objects.get(i).getAired().equals(lastDate));
			lastDate = objects.get(i).getAired();
		}

	}

	static class ViewHolder {
		protected TextView date;
		protected ImageView image;
		protected TextView dateSeparator;
		protected TextView episodeInformation;
		protected RelativeLayout dateSeparatorWrapper;
		protected RelativeLayout layoutEpisode;
	}

	@Override
	public View getView(final int pos, View convertView, ViewGroup parent)
	{

		if(convertView == null){
			LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.listitem_calendar, null);
		}

		final ImageView image = (ImageView) convertView.findViewById(R.id.imgSeriesImage);
		final TextView episodeInformation = (TextView)convertView.findViewById(R.id.txtEpisodeInformation);
		final TextView dateSeparator = (TextView)convertView.findViewById(R.id.txtDate);
		final RelativeLayout dateSeparatorWrapper = (RelativeLayout)convertView.findViewById(R.id.daySeparator);
		final RelativeLayout episodeImage = (RelativeLayout)convertView.findViewById(R.id.layoutEpisode);
		episodeImage.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				new SeriesHelper().displayPlot(episodes.get(pos), getContext());
			}
		});

		Bitmap bm = imageService.GetImage(episodes.get(pos).getSeriesId(), getContext());
		if(bm != null){
			image.setImageBitmap(bm);
		}
		else
		{
			image.setVisibility(View.INVISIBLE);
		}

		dateSeparator.setText(dateHelper.DaysTilNextEpisode(episodes.get(pos).getAired()) + " (" + dateHelper.DisplayDate(episodes.get(pos).getAired()).substring(4) + ")");
		episodeInformation.setText( dateHelper.Episodenumber(episodes.get(pos)) + " | " + episodes.get(pos).getTitle());

		if(displayDateWrapper.get(pos))
		{
			dateSeparatorWrapper.setVisibility(View.GONE);
		}
		else
		{
			dateSeparatorWrapper.setVisibility(View.VISIBLE);
		}

		return convertView;


	}
}
