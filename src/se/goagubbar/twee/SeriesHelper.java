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
		
		String message = episode.getSummary().equals("") ? context.getString(R.string.message_noplot) : episode.getSummary();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message + "\n\n" + dateHelper.Episodenumber(episode) + " | " + dateHelper.DisplayDate(episode.getAired()))
		       .setCancelable(false)
		       .setTitle(episode.getTitle())
		       .setPositiveButton(R.string.about_close, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       }).create().show();
	}
}
