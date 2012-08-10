package se.ja1984.teewee;

import java.util.ArrayList;

public class Series extends BaseEntity {


	private String Name;
	private String Actors;
	private String Airs;
	private String Genre;
	private String ImdbId;
	private String Rating;
	private String Status;
	private String Image;
	private String Header;
	private String FirstAired;
	private String SeriesId;
	public ArrayList<Episode> Episodes;
	
	public String getSeriesId()
	{
		return SeriesId;
	}
	
	public void setSeriesId(String seriesId)
	{
		this.SeriesId = seriesId;
	}
	
	public String getName()
	{
		return Name;
	}
	public void setName(String name)
	{
		this.Name = name;
	}
	
	public String getActors()
	{
		return Actors;
	}
	public void setActors(String actors)
	{
		this.Actors = actors;
	}
	
	public String getAirs()
	{
		return Airs;
	}
	public void setAirs(String airs)
	{
		this.Airs = airs;
	}
	
	public String getGenre()
	{
		return Genre;
	}
	public void setGenre(String genre)
	{
		this.Genre = genre;
	}
	
	public String getImdbId()
	{
		return ImdbId;
	}
	public void setImdbId(String imdbid)
	{
		this.ImdbId = imdbid;
	}
	
	public String getRating()
	{
		return Rating;
	}
	public void setRating(String rating)
	{
		this.Rating = rating;
	}

	public String getStatus()
	{
		return Status;
	}
	public void setStatus(String status)
	{
		this.Status = status;
	}
	
	public String getImage()
	{
		return Image;
	}
	public void setImage(String image)
	{
		this.Image = image;
	}
	
	public String getHeader()
	{
		return Header;
	}
	public void setHeader(String header)
	{
		this.Header = header;
	}
	
	public ArrayList<Episode> getEpisodes()
	{
		return Episodes;
	}
	
	public String getFirstAired()
	{
		return FirstAired;
		}
	
	public void setFirstAired(String firstAired)
	{
		this.FirstAired = firstAired;
	}

}
