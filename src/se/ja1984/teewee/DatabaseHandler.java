package se.ja1984.teewee;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 6;
	private static final String DATABASE_NAME = "Teewee";

	private static final String TABLE_SERIES = "Series";
	private static final String TABLE_EPISODES = "Episodes";

	//BaseEntity
	private static final String KEY_ID = "Id";
	private static final String KEY_SUMMARY = "Summary";
	private static final String KEY_LASTUPDATED = "LastUpdated";

	//Series
	private static final String KEY_NAME = "Name";
	private static final String KEY_ACTORS = "Actors";
	private static final String KEY_DAYTIME = "Airs";
	private static final String KEY_GENRE = "Genre";
	private static final String KEY_IMDBID = "ImdbId";
	private static final String KEY_RATING = "Rating";
	private static final String KEY_STATUS = "Status";
	private static final String KEY_IMAGE = "Image";
	private static final String KEY_HEADER = "Header";
	private static final String KEY_FIRSTAIRED = "FirstAired";


	//Episode
	private static final String KEY_SEASON = "Season";
	private static final String KEY_EPISODE = "Episode";
	private static final String KEY_TITLE = "Title";
	private static final String KEY_AIRED = "Aired";
	private static final String	KEY_WATCHED = "Watched";
	private static final String KEY_SERIESID = "SeriesId";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("Build db", "Start");
		String CREATE_SERIES_TABLE = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY autoincrement, %s TEXT,%s TEXT,%s TEXT,%s TEXT,%s TEXT,%s TEXT,%s TEXT,%s TEXT,%s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)", TABLE_SERIES, KEY_ID, KEY_SUMMARY, KEY_NAME,KEY_ACTORS,KEY_DAYTIME,KEY_GENRE,KEY_IMDBID,KEY_RATING,KEY_STATUS,KEY_IMAGE, KEY_FIRSTAIRED, KEY_HEADER, KEY_SERIESID, KEY_LASTUPDATED);
		db.execSQL(CREATE_SERIES_TABLE);

		String CREATE_EPISODE_TABLE = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY autoincrement,%s TEXT,%s TEXT,%s TEXT,%s TEXT,%s TEXT,%s TEXT, %s TEXT, %s TEXT)", TABLE_EPISODES, KEY_ID, KEY_SEASON, KEY_EPISODE, KEY_TITLE, KEY_AIRED, KEY_WATCHED, KEY_SUMMARY, KEY_SERIESID, KEY_LASTUPDATED);
		db.execSQL(CREATE_EPISODE_TABLE);
		Log.d("Build db", "Done");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("Rebuild db", "Start");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EPISODES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERIES);
		onCreate(db);
		Log.d("Rebuild db", "Done");
	}


	//CRUD

	public void addSeries(Series series)
	{
		Log.d("Add series", "Start");
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(KEY_NAME, series.getName());
		values.put(KEY_ACTORS, series.getActors());
		values.put(KEY_SUMMARY, series.getSummary());
		values.put(KEY_DAYTIME, series.getAirs());
		values.put(KEY_GENRE, series.getGenre());
		values.put(KEY_IMDBID, series.getImdbId());
		values.put(KEY_RATING, series.getRating());
		values.put(KEY_STATUS, series.getStatus());
		values.put(KEY_IMAGE, series.getImage());
		values.put(KEY_HEADER, series.getHeader());
		values.put(KEY_FIRSTAIRED, series.getFirstAired());
		values.put(KEY_SERIESID, series.getSeriesId());
		values.put(KEY_LASTUPDATED, series.getLastUpdated());

		Long id = db.insert(TABLE_SERIES, null, values);
		db.close();

		Log.d("Add series", "Done");
	}

	public void addEpisodes(ArrayList<Episode> episodes)
	{
		Log.d("Number of episodes:", "" + episodes.size());
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = null;

		db.beginTransaction();
		for (Episode ep : episodes) {
			values = new ContentValues();
			values.put(KEY_AIRED, ep.getAired());
			values.put(KEY_EPISODE, ep.getEpisode());
			values.put(KEY_SEASON, ep.getSeason());
			values.put(KEY_SERIESID, ep.getSeriesId());
			values.put(KEY_SUMMARY, ep.getSummary());
			values.put(KEY_TITLE, ep.getTitle());
			values.put(KEY_WATCHED, ep.getWatched());
			values.put(KEY_LASTUPDATED, ep.getLastUpdated());

			db.insert(TABLE_EPISODES, null, values);
			Log.d("Ep", "Episode added "  + new Date().toGMTString());
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		
	}

	public ArrayList<Series> getAllSeries()
	{
		ArrayList<Series> series = new ArrayList<Series>();
		String sql = "SELECT * FROM " + TABLE_SERIES + " ORDER BY "+ KEY_NAME +" ASC";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql,null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Series s = new Series();

			s.setActors(cursor.getString(3));
			s.setAirs(cursor.getString(4));
			s.setImage(cursor.getString(9));
			s.setGenre(cursor.getString(5));
			s.setID(Integer.parseInt(cursor.getString(0)));
			s.setImdbId(cursor.getString(6));
			s.setName(cursor.getString(2));			
			s.setRating(cursor.getString(7));
			s.setStatus(cursor.getString(8));
			s.setSummary(cursor.getString(1));
			s.setFirstAired(cursor.getString(10));
			s.setHeader(cursor.getString(11));
			s.setSeriesId(cursor.getString(12));

			s.Episodes = new ArrayList<Episode>();
			
			s.Episodes.add(GetNextEpisodeForSeries(s.getSeriesId()));
			
			series.add(s);
			cursor.moveToNext();
		}

		
		
		cursor.close();
		db.close();
		return series;
	}

	public Episode GetLastAiredEpisodeForSeries(String seriesId)
	{
		Episode e = new Episode();
		try {
			
			String dateWithoutTime  = android.text.format.DateFormat.format("yyyy-MM-dd", new java.util.Date()).toString();
			
			String sql = "SELECT * FROM "+ TABLE_EPISODES +" WHERE date("+ KEY_AIRED +") < date('"+ dateWithoutTime +"') AND "+KEY_SERIESID+" = "+ seriesId +" AND "+KEY_SEASON+" != 0 ORDER BY "+ KEY_AIRED +" DESC LIMIT 1";	
			SQLiteDatabase db = this.getReadableDatabase();
			
			Cursor cursor = db.rawQuery(sql, null);
			
			if(cursor != null)
			{
				if(cursor.moveToFirst())
				{
					e.setAired(cursor.getString(4));
					e.setEpisode(cursor.getString(2));
					e.setID(Integer.parseInt(cursor.getString(0)));
					e.setSeason(cursor.getString(1));
					e.setSeriesId(cursor.getString(7));
					e.setSummary(cursor.getString(6));
					e.setTitle(cursor.getString(3));
					e.setWatched(cursor.getString(5));
				}
			}
			
			cursor.close();
			db.close();
			
		} catch (Exception e2) {
			// TODO: handle exception
			Log.d("Error",e2.getLocalizedMessage());
		}
		
		return e;
		
	}

	
	public Episode GetNextEpisodeForSeries(String seriesId)
	{
		Episode e = new Episode();
		try {
			
			String dateWithoutTime  = android.text.format.DateFormat.format("yyyy-MM-dd", new java.util.Date()).toString();
			
			String sql = "SELECT * FROM "+ TABLE_EPISODES +" WHERE date("+ KEY_AIRED +") > date('"+ dateWithoutTime +"') AND "+KEY_SERIESID+" = "+ seriesId +" AND "+KEY_SEASON+" != 0 ORDER BY "+ KEY_AIRED +" DESC LIMIT 1";	
			SQLiteDatabase db = this.getReadableDatabase();
			
			Cursor cursor = db.rawQuery(sql, null);
			
			if(cursor != null)
			{
				if(cursor.moveToFirst())
				{
					e.setAired(cursor.getString(4));
					e.setEpisode(cursor.getString(2));
					e.setSeason(cursor.getString(1));
					e.setTitle(cursor.getString(3));
				}
			}
			
			cursor.close();
			db.close();
			
		} catch (Exception e2) {
			// TODO: handle exception
			Log.d("Error",e2.getLocalizedMessage());
		}
		
		return e;
		
	}
	
	
	public Episode GetEpisodeById(String episodeId)
	{
		Episode e = new Episode();
		try {
			
						
			String sql = "SELECT * FROM "+ TABLE_EPISODES +" WHERE "+KEY_ID+" = "+ episodeId;	
			SQLiteDatabase db = this.getReadableDatabase();
			
			Cursor cursor = db.rawQuery(sql, null);
			
			if(cursor != null)
			{
				if(cursor.moveToFirst())
				{
					e.setAired(cursor.getString(4));
					e.setEpisode(cursor.getString(2));
					e.setID(Integer.parseInt(cursor.getString(0)));
					e.setSeason(cursor.getString(1));
					e.setSeriesId(cursor.getString(7));
					e.setSummary(cursor.getString(6));
					e.setTitle(cursor.getString(3));
					e.setWatched(cursor.getString(5));
				}
			}
			
			cursor.close();
			db.close();
			
		} catch (Exception e2) {
			// TODO: handle exception
			Log.d("Error",e2.getLocalizedMessage());
		}
		
		return e;
	}
	
	public Series getSeriesById(String id)
	{
		Series s = new Series();
		String sql = "SELECT * FROM " + TABLE_SERIES + " WHERE " + KEY_ID + " = " + id;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);

		if(cursor != null)
		{
			if(cursor.moveToFirst())
			{
				s.setActors(cursor.getString(3));
				s.setAirs(cursor.getString(4));
				s.setImage(cursor.getString(9));
				s.setHeader(cursor.getString(11));
				s.setGenre(cursor.getString(5));
				s.setID(Integer.parseInt(cursor.getString(0)));
				s.setImdbId(cursor.getString(6));
				s.setName(cursor.getString(2));			
				s.setRating(cursor.getString(7));
				s.setStatus(cursor.getString(8));
				s.setSummary(cursor.getString(1));
				s.setFirstAired(cursor.getString(10));
				s.setSeriesId(cursor.getString(12));
			}
			cursor.close();
		}
		s.Episodes = GetEpisodes(s.getSeriesId());

		return s;
	}

	public void ToggleEpisodeWatched(String id, boolean watched)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		String value;

		if(watched)
		{
			value = "1";
		}
		else
		{
			value = "0";
		}

		ContentValues values = new ContentValues();
		values.put(KEY_WATCHED, value);

		db.update(TABLE_EPISODES, values, KEY_ID + " = ?", new String[] { id });
		Log.d("Event","Updatering klar");
		db.close();

	}

	public void MarkSeriesAsWatched(String id)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		String dateWithoutTime  = android.text.format.DateFormat.format("yyyy-MM-dd", new java.util.Date()).toString();
		
		ContentValues values = new ContentValues();
		values.put(KEY_WATCHED, "1");
		//WHERE date("+ KEY_AIRED +") < date('"+ dateWithoutTime +"')
		db.update(TABLE_EPISODES, values, "date(?) < date(?) AND ? = ?", new String[] {KEY_AIRED, dateWithoutTime, KEY_SERIESID, id});
		Log.d("Event","Updatering klar");
		db.close();

	}
	
	private ArrayList<Episode> GetEpisodes(String seriesId)
	{
		ArrayList<Episode> episodes = new ArrayList<Episode>();
		String sql = "SELECT * FROM " + TABLE_EPISODES + " WHERE " + KEY_SEASON + " != 0 AND " + KEY_AIRED + " != '' AND " +  KEY_SERIESID + " = " + seriesId;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);

		cursor.moveToFirst();

		while(!cursor.isAfterLast())
		{
			Episode e = new Episode();

			e.setAired(cursor.getString(4));
			e.setEpisode(cursor.getString(2));
			e.setID(Integer.parseInt(cursor.getString(0)));
			e.setSeason(cursor.getString(1));
			e.setSeriesId(cursor.getString(7));
			e.setSummary(cursor.getString(6));
			e.setTitle(cursor.getString(3));
			e.setWatched(cursor.getString(5));

			episodes.add(e);

			cursor.moveToNext();
		}
		cursor.close();
		db.close();



		return episodes;
	}

}


