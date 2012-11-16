package se.goagubbar.twee;

import se.goagubbar.twee.models.Episode;
import se.goagubbar.twee.utils.DateHelper;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class SeriesHelper {
	public void displayPlot(Episode episode, Context context)
	{
		
		DateHelper dateHelper = new DateHelper();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(episode.getSummary() + "\n\n" + dateHelper.Episodenumber(episode) + " | " + dateHelper.DisplayDate(episode.getAired()))
		       .setCancelable(false)
		       .setTitle(episode.getTitle())
		       .setPositiveButton(R.string.about_close, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       }).create().show();
	}
}
