package se.ja1984.twee;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import se.ja1984.twee.models.Episode;
import se.ja1984.twee.models.WidgetEpisode;
import se.ja1984.twee.utils.DatabaseHandler;
import se.ja1984.twee.utils.DateHelper;
import se.ja1984.twee.utils.ImageService;
import se.ja1984.twee.utils.Utils;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.RemoteViewsService.RemoteViewsFactory;

public class UpdateWidgetService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new WidgetRemoteViewsFactory(this.getApplicationContext(), intent);
	}
}

class WidgetRemoteViewsFactory implements RemoteViewsFactory{

	private Context context;
	private int appWidgetId;
	private DateHelper dateHelper;
	private ArrayList<WidgetEpisode> episodes = new ArrayList<WidgetEpisode>();
	private ImageService imageService;	
	
	public WidgetRemoteViewsFactory(Context context, Intent intent){
		this.context = context;
		appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,  AppWidgetManager.INVALID_APPWIDGET_ID);
		dateHelper = new DateHelper();
		imageService = new ImageService();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return episodes.size();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public RemoteViews getLoadingView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		// TODO Auto-generated method stub
		
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.listitem_widget);
		//remoteViews.setTextViewText(R.id.txtShowName, episodes.get(position).getTitle());
		
		WidgetEpisode e = episodes.get(position);
		
		String showInformation = dateHelper.Episodenumber(e) + " " + e.getTitle();
		Bitmap bm = imageService.GetImage(e.getImage(), context);
		
		if(bm == null)
			bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.noimage);
		
		remoteViews.setImageViewBitmap(R.id.imgSeriesImage, bm);
		remoteViews.setTextViewText(R.id.txtUpcomingEpisode, showInformation);

		Bundle extras = new Bundle();
        extras.putString(MyWidgetProvider.EXTRA_ITEM, e.getSummary());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        remoteViews.setOnClickFillInIntent(R.id.widgetItemWrapper, fillInIntent);
		
		return remoteViews;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onCreate() {
		loadData();
	}

	@Override
	public void onDataSetChanged() {
		loadData();
	}

	@Override
	public void onDestroy() {
		episodes.clear();
		
	}
	
	private void loadData(){
		
		final SharedPreferences settings = context.getSharedPreferences("Twee", 0);
		Utils.selectedProfile = settings.getInt("Profile", 1);
		
		episodes.clear();
		DatabaseHandler databaseHandler = new DatabaseHandler(context);
		ArrayList<WidgetEpisode> dbEpisodes = databaseHandler.GetTodaysEpisodes();
		episodes.addAll(dbEpisodes);
	}
	
}
