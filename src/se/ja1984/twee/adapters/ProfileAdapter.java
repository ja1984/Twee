package se.ja1984.twee.adapters;

import java.util.ArrayList;

import se.ja1984.twee.BackupActivity;
import se.ja1984.twee.ProfileActivity;
import se.ja1984.twee.R;
import se.ja1984.twee.models.Profile;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileAdapter extends ArrayAdapter<Profile> {

	Context context;
	ArrayList<Profile> profiles;
	
	public ProfileAdapter(Context context, int resource, ListView listView, ArrayList<Profile> objects) {
		super(context, resource, objects);
		this.context = context;
		this.profiles = objects;
		// TODO Auto-generated constructor stub
	}

	
	static class ViewHolder {
		protected TextView profileName;
		protected ImageView editProfile;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if(convertView == null){
			LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.listitem_profile, null);
		}
		
		convertView.setTag(profiles.get(position).getId().toString());
		
		TextView txtProfileName = (TextView)convertView.findViewById(R.id.txtProfileName);
		
		txtProfileName.setText(profiles.get(position).getName());

		
		return convertView;
		
		//return super.getView(position, convertView, parent);
	}

	
	
}
