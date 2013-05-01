package se.ja1984.twee;

import java.util.Random;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		
		// Get all ids
	    ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
	    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
	    for (int widgetId : allWidgetIds) {
	      // Create some random data
	      int number = (new Random().nextInt(100));
	      RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
	      
	      
	      Intent intent = new Intent(context, WidgetService.class);
	      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
	      remoteViews.setRemoteAdapter(R.id.lstWidgetList,intent);
	      
//	      Log.w("WidgetExample", String.valueOf(number));
//	      // Set the text
//	      remoteViews.setTextViewText(R.id.txtDate, String.valueOf(number));

	      // Register an onClickListener
//	      Intent intent = new Intent(context, WidgetProvider.class);
//
//	      intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//	      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
//
//	      PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//	      remoteViews.setOnClickPendingIntent(R.id.txtDate, pendingIntent);
	      appWidgetManager.updateAppWidget(widgetId, remoteViews);
	    }
		
		
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

}
