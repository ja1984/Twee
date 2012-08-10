package se.ja1984.teewee;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class SeriesService {

	public void displayPlot(Episode episode, Context context)
	{
		
		DateService dateService = new DateService();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(episode.getSummary() + "\n\n" + dateService.Episodenumber(episode) + " | " + dateService.DisplayDate(episode.getAired()))
		       .setCancelable(false)
		       .setTitle(episode.getTitle())
		       .setPositiveButton(R.string.about_close, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       }).create().show();
	}
	
}
