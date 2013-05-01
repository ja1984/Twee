package se.ja1984.twee.adapters;


import java.net.URL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nostra13.universalimageloader.core.ImageLoader;

import se.ja1984.twee.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class BannerAdapter extends ArrayAdapter<String>{

	

	static class viewHolder
	{
		ImageView image;
		TextView showName;
		String seriesId;
		TextView rating;
	}
	private final Context context;
	int resource;
	private final List<String> banners;
	ImageLoader imageLoader;
	

	public BannerAdapter(Context context, int resource, ListView lv, List<String> objects) {
		super(context, resource, objects);
		this.context = context;
		this.resource = resource;
		this.banners = objects;
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		viewHolder holder;
		String banner = banners.get(position);
		if(convertView == null)
		{
			convertView = View.inflate(context, resource, null);
			holder = new viewHolder();

			holder.image = (ImageView)convertView.findViewById(R.id.imgSeriesImage);
			holder.showName = (TextView)convertView.findViewById(R.id.txtShowInformation);
			holder.rating = (TextView)convertView.findViewById(R.id.txtRating);
			
			final ImageView pic = (ImageView) convertView.findViewById(R.id.imgSeriesImage);
			pic.setImageBitmap(null);
			try {
				imageLoader.displayImage("http://www.thetvdb.com/banners/" + banner, pic);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			convertView.setTag(holder);
		}
		else{
			holder = (viewHolder)convertView.getTag();
		}
		
		convertView.setTag(R.string.homeactivity_tag_seriesid,banner);

		return convertView;
	}


}
