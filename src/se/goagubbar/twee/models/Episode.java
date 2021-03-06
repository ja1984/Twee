package se.goagubbar.twee.models;

public class Episode extends BaseEntity {

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