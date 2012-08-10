package se.ja1984.teewee;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SeriesAdapter extends ArrayAdapter<Series> {

	private final Context context;
	private final ArrayList<Series> series;

	public SeriesAdapter(Context context, int resource, ListView lv, ArrayList<Series> objects)
	{
		super(context, R.layout.series_listitem, objects);
		this.context = context;
		this.series = objects;
	}

	@Override
	public View getView(int position, View ConvertView, ViewGroup parent)
	{
		LayoutInflater inflater  = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.series_listitem, parent, false);

		rowView.setTag(series.get(position).getID());
		ImageView img = (ImageView)rowView.findViewById(R.id.imageView1);
		TextView tv = (TextView)rowView.findViewById(R.id.txtSeriesName);
		
		Episode e = series.get(position).Episodes.get(0);
		
		if(e.getAired() != null)
		{
			tv.setText( new DateService().Episodenumber(e) + " " + new DateService().DisplayDate(e.getAired()) + " - " + e.getTitle());
		}
		else
		{
			tv.setVisibility(View.GONE);
		}
		
		Bitmap bm = new ImageHelper().GetImage(series.get(position).getImage(), getContext());

		if(bm != null){
			img.setImageBitmap(bm);
		}
		//tv.setText(series.get(position).getName());

		return rowView;
	}
}
