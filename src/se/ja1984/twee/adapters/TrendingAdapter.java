package se.ja1984.twee.adapters;


import java.net.URL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nostra13.universalimageloader.core.ImageLoader;

import se.ja1984.twee.dto.TraktShow;
import se.ja1984.twee.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TrendingAdapter extends ArrayAdapter<TraktShow>{

	ImageLoader imageLoader;

	static class viewHolder
	{
		ImageView image;
		TextView showName;
		String seriesId;
		TextView rating;
	}
	private final Context context;
	int resource;
	private final List<TraktShow> shows;

	public TrendingAdapter(Context context, int resource, ListView lv, List<TraktShow> objects) {
		super(context, resource, objects);
		this.context = context;
		this.resource = resource;
		this.shows = objects;
		imageLoader = ImageLoader.getInstance();
		//cache = new HashMap<String, Bitmap>();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		viewHolder holder;
		TraktShow traktShow = shows.get(position);
		if(convertView == null)
		{
			convertView = View.inflate(context, resource, null);
			holder = new viewHolder();

			holder.image = (ImageView)convertView.findViewById(R.id.imgSeriesImage);
			holder.showName = (TextView)convertView.findViewById(R.id.txtShowInformation);
			holder.rating = (TextView)convertView.findViewById(R.id.txtRating);
			
			final ImageView bannerImage = (ImageView) convertView.findViewById(R.id.imgSeriesImage);
			//pic.setImageBitmap(null);
			
			try {
				
				
				imageLoader.displayImage(traktShow.images.banner, bannerImage);
				//imageLoader.addImage(new URL(traktShow.images.banner), pic);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			convertView.setTag(holder);
		}
		else{
			holder = (viewHolder)convertView.getTag();
		}
		
		holder.rating.setText("Viewers: " + traktShow.watchers);
		holder.showName.setText(traktShow.title);
		convertView.setTag(R.string.homeactivity_tag_seriesid,traktShow.tvdb_id);

		return convertView;
	}


}
