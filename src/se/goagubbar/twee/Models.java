package se.goagubbar.twee;

import java.util.ArrayList;

public class Models {

	public static class BaseEntity {

		private int Id;
		private String Summary;
		private String LastUpdated;
		private String SeriesId;
		
		
		public String getSeriesId()
		{
			return SeriesId;
		}
		
		public void setSeriesId(String seriesId)
		{
			this.SeriesId = seriesId;
		}
		
		  public int getID(){
		        return this.Id;
		    }
		 
		    // setting id
		    public void setID(int id){
		        this.Id = id;
		    }
		 
		    // getting name
		    public String getSummary(){
		        return this.Summary;
		    }
		 
		    // setting name
		    public void setSummary(String summary){
		        this.Summary = summary;
		    }
		    
		    public String getLastUpdated()
		    {
		    	return LastUpdated;
		    }
		    
		    public void setLastUpdated(String lastUpdated){
		    	this.LastUpdated = lastUpdated;
		    }
		    
		    
		    
	}
	
	public static class Episode extends BaseEntity {

		private String Season;
		private String Episode;
		private String Title;
		private String Aired;
		private String Watched;
		private boolean Selected;
		private String EpisodeId;
		
		
		public boolean isSelected()
		{
			return Selected;
		}
		
	    public void setSelected(boolean selected) {
	        this.Selected = selected;
	    }
		
	    public String getEpisodeId()
	    {
	    	return EpisodeId;
	    }
	    
	    public void setEpisodeId(String episodeId)
	    {
	    	this.EpisodeId = episodeId;
	    }
	    
	    
		public String getSeason()
		{
			return Season;
		}
		public void setSeason(String season)
		{
			this.Season = season;
		}
		
		public String getEpisode()
		{
			return Episode;
		}
		public void setEpisode(String episode)
		{
			this.Episode = episode;
		}
		
		public String getTitle()
		{
			return Title;
		}
		public void setTitle(String title)
		{
			this.Title = title;
		}

		public String getAired()
		{
			return Aired;
		}
		public void setAired(String aired)
		{
			this.Aired = aired;
		}

		public String getWatched()
		{
			return Watched;
		}
		public void setWatched(String watched)
		{
			this.Watched = watched;
		}
		
	}
	
	public static class Series extends BaseEntity {


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
		public ArrayList<Episode> Episodes;
		
		
		
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
	
}

