package se.ja1984.twee.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.tz.DateTimeZoneBuilder;

import android.text.format.DateUtils;
import android.util.Log;

import se.ja1984.twee.models.Episode;

public class DateHelper {



	public String Episodenumber(Episode episode)
	{
		String s = episode.getSeason();
		String e = episode.getEpisode();

		e = e.length() < 2 ? "0" + e : e;
		s = s.length() < 2 ? "0" + s : s;

		return "S" + s + "E" + e;
	}

	public int CompareDates(String d1, String d2)
	{
		SimpleDateFormat df = new SimpleDateFormat ("yyyy-MM-dd");

		Date date1 = null;
		Date date2 = null;

		try {
			date1 = df.parse(d1);
			date2 = df.parse(d2);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return date1.compareTo(date2);
	}

	public String DisplayDate(String date)
	{
		String returndate = null;
		Date today = setTimeToMidnight(new Date());
		Date d1 = null;
		SimpleDateFormat df = new SimpleDateFormat ("yyyy-MM-dd");
		DateFormat format = DateFormat.getDateInstance();

		try {
			d1 = df.parse(date);
		} catch (Exception e) {
			// TODO: handle exception
		}


		if(d1 == null)
			return date;

		switch (d1.compareTo(today)) {
		case -1:
			returndate = "Aired " + format.format(d1);
			break;
		case 0:
			returndate = "Airs today";
			break;
		default:
			returndate = "Airs " + format.format(d1);
			break;
		}


		return returndate;
	}

	public String ShortDisplayDate(String date)
	{
		Date today = setTimeToMidnight(new Date());
		Date d1 = null;
		SimpleDateFormat df = new SimpleDateFormat ("yyyy-MM-dd");
		DateFormat format = DateFormat.getDateInstance();

		try {
			d1 = df.parse(date);
		} catch (Exception e) {
			// TODO: handle exception
		}

		if(d1 == null)
			return date;

		return format.format(d1);
	}

	public String DaysTilNextEpisode(String date)
	{


		Date today = setTimeToMidnight(new Date());
		Date d1 = null;
		SimpleDateFormat df = new SimpleDateFormat ("yyyy-MM-dd");

		try {
			d1 = df.parse(date);
		} catch (Exception e) {
			// TODO: handle exception
		}


		if(d1 == null)
			return date;

		int daysBetween = Days.daysBetween(new DateTime(today), new DateTime(d1)).getDays();

		if(daysBetween == 0)
		{
			return "Airs today";
		}
		else if(daysBetween == 1)
		{
			return "Airs tomorrow";
		}
		else
		{
			return "Airs in " + daysBetween + " days";
		}

	}

	public String ConvertToLocalTimeTest(String airDate, String airTime){

		if(airTime.equals("") || airTime == null)
			return airDate;

		String result = airDate;
		if(Utils.useLocalizedTimezone){
			try {

				String dateString = airDate + " " + airTime;
			
				SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd H:mm");
				sourceFormat.setTimeZone(TimeZone.getTimeZone("GMT-5"));
				
				
				Date parsed;

				parsed = sourceFormat.parse(dateString);
				TimeZone tz = TimeZone.getDefault();
				SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				destFormat.setTimeZone(tz);

				result = destFormat.format(parsed);
			} catch (ParseException e) {
				return airDate;
			}
		}
		return result;
	}
	
	public String ConvertToLocalTime(String airDate, String airTime){

		if(airTime.equals("") || airTime == null)
			return airDate;

		String result = airDate;
		if(Utils.useLocalizedTimezone){
			try {

				String dateString = airDate + " " + airTime.toUpperCase();

				SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd K:mma");
				sourceFormat.setTimeZone(TimeZone.getTimeZone("GMT-8"));

				Date parsed;

				parsed = sourceFormat.parse(dateString);
				TimeZone tz = TimeZone.getDefault();
				SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd");
				destFormat.setTimeZone(tz);

				result = destFormat.format(parsed);
			} catch (ParseException e) {
				return airDate;
			}
		}
		return result;
	}

	public String DaysTilNextEpisode(Episode episode)
	{
		return DaysTilNextEpisode(episode.getAired(), episode.getAirtime() == null ? "" : episode.getAirtime());
	}

	public String DaysTilNextEpisode(String date, String airtime)
	{

		//		if(airtime.equals("") || airtime == null)
		//			airtime = "8:00pm";
		//
		//		String dateString = date + " " + airtime.toUpperCase();
		//
		//		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd K:mma");
		//		sourceFormat.setTimeZone(TimeZone.getTimeZone("GMT-8"));
		//
		//		Date parsed;
		//		String result = "";
		//		try {
		//			parsed = sourceFormat.parse(dateString);
		//			TimeZone tz = TimeZone.getDefault();
		//			SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd");
		//			destFormat.setTimeZone(tz);
		//
		//			result = destFormat.format(parsed);
		//		} catch (ParseException e) {
		//
		//		}

		Date today = setTimeToMidnight(new Date());
		int daysBetween = Days.daysBetween(new DateTime(today), new DateTime(date)).getDays();

		if(daysBetween == 0)
		{
			return "Airs today";
		}
		else if(daysBetween == 1)
		{
			return "Airs tomorrow";
		}
		else if(daysBetween < 0){
			return "Aired " + date;
		}
		else
		{
			return "Airs in " + daysBetween + " days";
		}

	}




	private static Date setTimeToMidnight(Date date) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime( date );
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	public String GetTodaysDate()
	{
		return android.text.format.DateFormat.format("yyyy-MM-dd", new java.util.Date()).toString();
	}

}