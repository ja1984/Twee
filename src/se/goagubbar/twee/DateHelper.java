package se.goagubbar.twee;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import se.goagubbar.twee.models.Episode;

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


		long timeOne = today.getTime();
		long timeTwo = d1.getTime();
		long oneDay = 1000 * 60 * 60 * 24;
		long delta = (timeTwo - timeOne) / oneDay;

		if(delta == 0)
		{
			return "Airs today";
		}
		else
		{
			return "Airs in " + delta + " days";
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