package se.ja1984.twee.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se.ja1984.twee.R;
import se.ja1984.twee.adapters.SeriesAdapter.viewHolder;
import se.ja1984.twee.models.ExtendedSeries;
import se.ja1984.twee.utils.ImageService;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SeriesAdapter extends ArrayAdapter<ExtendedSeries> {

	public static Map<String, Bitmap> cache;

	static class viewHolder
	{
		ImageView image;
		TextView information;
		String seriesId;
		String season;
		ProgressBar progress;
		TextView txtSmallView;
	}

	private final Context context;
	private final ArrayList<ExtendedSeries> series;
	Object mActionMode;
	int resource;

	public SeriesAdapter(Context context, int resource, ListView lv, ArrayList<ExtendedSeries> objects)
	{
		super(context, resource, objects);
		this.context = context;
		this.series = objects;
		this.resource = resource;

		cache = new HashMap<String, Bitmap>();
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		viewHolder holder;
		ExtendedSeries s = series.get(position);

		if(convertView == null)
		{
			convertView = View.inflate(context, resource, null);
			holder = new viewHolder();

			holder.image = (ImageView)convertView.findViewById(R.id.imgSeriesImage);
			holder.information = (TextView)convertView.findViewById(R.id.txtUpcomingEpisode);
			holder.progress = (ProgressBar)convertView.findViewById(R.id.pgrWatched);						

			convertView.setTag(holder);
		}
		else
		{
			holder = (viewHolder)convertView.getTag();
		}


		if(s != null)
		{
			holder.seriesId = s.getImage();


			convertView.setTag(R.string.homeactivity_tag_id,s.getID());
			convertView.setTag(R.string.homeactivity_tag_seriesid,s.getSeriesId());

			holder.progress.setMax(s.getTotalEpisodes());
			holder.progress.setProgress(s.getWatchedEpisodes());
			holder.information.setText(s.getNextEpisodeInformation().equals("") ? context.getText(R.string.message_show_ended) : s.getNextEpisodeInformation());

			Bitmap bm = getBitmapFromCache(s.getImage());

			holder.image.setImageBitmap(bm);

		}


		return convertView;

	}


	private Bitmap getBitmapFromCache(String imageName) {  
		if (cache.containsKey(imageName)) {  
			return cache.get(imageName);  
		}  
		else
		{
			Bitmap bm = new ImageService().GetImage(imageName, context);
			if(bm == null)
				bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.noimage);

			cache.put(imageName, bm);
			return bm;
		}

	}
}
