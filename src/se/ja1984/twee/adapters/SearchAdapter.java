package se.ja1984.twee.adapters;

import java.util.ArrayList;

import se.ja1984.twee.R;
import se.ja1984.twee.models.Series;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SearchAdapter extends ArrayAdapter<Series>
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
		final TextView btnPlot = (TextView)rowView.findViewById(R.id.txtSeriesPlot);

		rowView.setTag(series.get(position).getID());
		btnPlot.setTag(series.get(position).getSummary());
		name.setText(series.get(position).getName());
		year.setText("First aired: " + series.get(position).getAirs());

		btnPlot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage(btnPlot.getTag().toString())
				.setCancelable(false)
				.setTitle(R.string.series_summary)
				.setPositiveButton(R.string.about_close, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).create().show();

			}
		});

		return rowView;
	}

}
