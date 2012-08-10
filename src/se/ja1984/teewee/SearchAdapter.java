package se.ja1984.teewee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import se.ja1984.teewee.MainActivity.FetchAndSaveSeries;
import se.ja1984.teewee.MainActivity.GetMySeries;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SearchAdapter extends ArrayAdapter<Series>
{

	private final Context context;
	private final ArrayList<Series> series;
	



	public SearchAdapter(Context context, int resource, ArrayList<Series> objects) {
		super(context, R.layout.search_result, objects);
		this.context = context;
		this.series = objects;
	}
	
	
	
	@Override
	public View getView(int position, View ConvertView, ViewGroup parent){
		LayoutInflater inflater  = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View rowView = inflater.inflate(R.layout.search_result, parent, false);
		
		TextView name = (TextView)rowView.findViewById(R.id.txtName);
		TextView year = (TextView)rowView.findViewById(R.id.txtYear);
		
		rowView.setTag(series.get(position).getID());
		
		name.setText(series.get(position).getName());
		year.setText(series.get(position).getAirs());
		
		return rowView;
	}
	
	
	
	

}
