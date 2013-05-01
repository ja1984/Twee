package se.ja1984.twee;

import java.util.ArrayList;
import java.util.Date;

import se.ja1984.twee.models.Episode;
import se.ja1984.twee.models.WidgetEpisode;
import se.ja1984.twee.utils.DatabaseHandler;
import se.ja1984.twee.utils.DateHelper;
import se.ja1984.twee.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.apps.dashclock.api.ExtensionData;

public class DCExtension extends com.google.android.apps.dashclock.api.DashClockExtension {

	private static final String TAG = "ExampleExtension";
	public static final String PREF_NAME = "pref_name";

	@Override
	protected void onUpdateData(int reason) {

		DatabaseHandler databaseHandler = new DatabaseHandler(this);

		final SharedPreferences settings = getSharedPreferences("Twee", 0);
		Utils.selectedProfile = settings.getInt("Profile", 1);

		ArrayList<WidgetEpisode> episodes = databaseHandler.GetTodaysEpisodes();
		DateHelper dateHelper = new DateHelper();
		String numberOfEpisodes = "" +episodes.size();

		StringBuilder sb = new StringBuilder();

		for (WidgetEpisode episode : episodes) {
			sb.append(dateHelper.Episodenumber(episode) + " " + episode.getsetShowName() + "\n");			
		}

		if(episodes.size() > 0)
			sb.setLength(sb.length() - 1);

		String title = numberOfEpisodes + (episodes.size() == 1 ? " episode airing today" :  " episodes airing today");	

		String episodesToday = sb.toString();


		publishUpdate(new ExtensionData()
		.visible(true)
		.icon(R.drawable.ic_icon_dashclock)
		.visible(!numberOfEpisodes.equals("0"))
		.status(numberOfEpisodes)
		.expandedTitle(title)
		.expandedBody(episodesToday)
		.clickIntent(new Intent("se.ja1984.twee.CalendarActivity_LAUNCH_IT"))
				);

	}


}
