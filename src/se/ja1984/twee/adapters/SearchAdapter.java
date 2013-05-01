package se.ja1984.twee.adapters;

import java.util.ArrayList;

import se.ja1984.twee.R;
import se.ja1984.twee.TweeApplication;
import se.ja1984.twee.SearchableActivity.FetchAndSaveSeries;
import se.ja1984.twee.dto.TraktSearchResult;
import se.ja1984.twee.models.Series;
import se.ja1984.twee.utils.DatabaseHandler;
import se.ja1984.twee.utils.ImageLoader;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchAdapter extends ArrayAdapter<TraktSearchResult>
{

	private final Context context;
	private final ArrayList<TraktSearchResult> series;

	public SearchAdapter(Context context, int resource, ArrayList<TraktSearchResult> objects) {
		super(context, R.layout.listitem_searchresult, objects);
		this.context = context;
		this.series = objects;
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
		//TextView showAirTime = (TextView)rowView.findViewById(R.id.txtAirTime);
		TextView showAirDay = (TextView)rowView.findViewById(R.id.txtAirDay);



		//final TextView btnPlot = (TextView)rowView.findViewById(R.id.txtSeriesPlot);

		final TraktSearchResult show = series.get(position);

		//		ImageTag tag = imageTagFactory.build(show.images.poster,context);
		//		showBanner.setTag(tag);
		//
		//		if(TweeApplication.getImageLoader().getLoader() != null){
		//			TweeApplication.getImageLoader().getLoader().load(showBanner);
		//		}

		rowView.setTag(show.tvdb_id);
		rowView.setTag(R.string.TAG_AIRTIME, show.air_time);
		rowView.setTag(R.string.TAG_RUNTIME, show.runtime);
		rowView.setTag(R.string.TAG_TVRAGEID, show.tvrage_id);
		

		String showTitle = show.title + " (" + show.year + ")";
		showName.setText(showTitle);

		String airInformation = show.air_day + " " + show.air_time;
		showAirDay.setText(airInformation);
		
		showCountry.setText(show.country);
		showNetwork.setText(show.network);
		showPlot.setText(getPlot(show.overview));


		//		btnPlot.setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View v) {
		//
		//				AlertDialog.Builder builder = new AlertDialog.Builder(context);
		//				builder.setMessage(btnPlot.getTag().toString())
		//				.setCancelable(false)
		//				.setTitle(R.string.series_summary)
		//				.setPositiveButton(R.string.about_close, new DialogInterface.OnClickListener() {
		//					public void onClick(DialogInterface dialog, int id) {
		//						dialog.cancel();
		//					}
		//				}).create().show();
		//
		//			}
		//		});

		return rowView;
	}

	private String getPlot(String plot)
	{
		return plot.length() <= 250 ? plot : (plot.substring(0, 250) + "...");
	}

}
