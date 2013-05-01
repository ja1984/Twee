package se.ja1984.twee;

import java.util.Date;

import se.ja1984.twee.utils.Utils;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MyWidgetProvider extends AppWidgetProvider {
	public static final String EXTRA_ITEM = "TweeExtra";
	public static final String REFRESH_ACTION = "RefreshTweeWidget";
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		for(int i = 0; i < appWidgetIds.length; i++){
			Intent svcIntent = new Intent(context, UpdateWidgetService.class);
			
			RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
			widget.setRemoteAdapter(R.id.lstWidgetList, svcIntent);
			
			widget.setEmptyView(R.id.lstWidgetList, R.id.empty_view);
			
			Intent clickIntent = new Intent(context, CalendarActivity.class);
			PendingIntent clickPendingIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			widget.setPendingIntentTemplate(R.id.lstWidgetList, clickPendingIntent);
			
			clickIntent = new Intent(context, MyWidgetProvider.class);
			clickIntent.setAction(REFRESH_ACTION);
			PendingIntent pendingIntentRefresh = PendingIntent.getBroadcast(context, 0, clickIntent, 0);
			widget.setOnClickPendingIntent(R.id.btnRefreshWidget, pendingIntentRefresh);
			
			
			clickIntent = new Intent(context, HomeActivity.class);
			PendingIntent pendingIntentOpenApp = PendingIntent.getActivity(context, 0, clickIntent, 0);
			widget.setOnClickPendingIntent(R.id.btnOpenTwee, pendingIntentOpenApp);
			
			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
			
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);


	}
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		String action = intent.getAction();
			updateWidget(context);		
	}
	
	private void updateWidget(Context context){
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, MyWidgetProvider.class));
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lstWidgetList);
	}

}
