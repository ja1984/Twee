package se.ja1984.twee.adapters;

import java.util.ArrayList;
//import com.nostra13.universalimageloader.core.ImageLoader;
import se.ja1984.twee.R;
import se.ja1984.twee.dto.TraktSearchResult;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class SearchAdapter extends ArrayAdapter<TraktSearchResult>
{

	private final Context context;
	private final ArrayList<TraktSearchResult> series;
	//ImageLoader imageLoader;

	public SearchAdapter(Context context, int resource, ArrayList<TraktSearchResult> objects) {
		super(context, R.layout.listitem_searchresult, objects);
		this.context = context;
		this.series = objects;
		//this.imageLoader = ImageLoader.getInstance();
	}



	@Override
	public View getView(int position, View ConvertView, ViewGroup parent){
		LayoutInflater inflater  = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View rowView = inflater.inflate(R.layout.listitem_searchresult, parent, false);

		TextView showName = (TextView)rowView.findViewById(R.id.txtShowName);
		TextView showPlot = (TextView)rowView.findViewById(R.id.txtShowPlot);
		//ImageView showBanner = (ImageView)rowView.findViewById(R.id.imgShowImage);

		TextView showNetwork = (TextView)rowView.findViewById(R.id.txtShowNetwork);
		TextView showCountry = (TextView)rowView.findViewById(R.id.txtShowCountry);
		TextView showAirDay = (TextView)rowView.findViewById(R.id.txtAirDay);


		
		final TraktSearchResult show = series.get(position);

		rowView.setTag(show.tvdb_id);
		rowView.setTag(R.string.TAG_AIRTIME, show.air_time);
		rowView.setTag(R.string.TAG_RUNTIME, show.runtime);
		rowView.setTag(R.string.TAG_TVRAGEID, show.tvrage_id);
		
		//imageLoader.displayImage(show.images.poster, showBanner);
		
		String showTitle = show.title + " (" + show.year + ")";
		showName.setText(showTitle);

		String airInformation = show.air_day + " " + show.air_time;
		showAirDay.setText(airInformation);
		
		showCountry.setText(show.country);
		showNetwork.setText(show.network);
		showPlot.setText(getPlot(show.overview));

		return rowView;
	}

	private String getPlot(String plot)
	{
		return plot.length() <= 250 ? plot : (plot.substring(0, 250) + "...");
	}

}
