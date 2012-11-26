package se.ja1984.twee.models;

public  class ExtendedSeries extends Series{
	private int totalEpisodes;
	private int watchedEpisodes;
	private String nextEpisodeInformation;
	
	public void setTotalEpisodes(int totalEpisodes)
	{
		this.totalEpisodes = totalEpisodes;
	}
	
	public void setwatchedEpisodes(int watchedEpisodes)
	{
		this.watchedEpisodes = watchedEpisodes;
	}
	
	public void setNextEpisodeInformation(String nextEpisodeInformation)
	{
		this.nextEpisodeInformation = nextEpisodeInformation;
	}
	
	public int getTotalEpisodes()
	{
		return totalEpisodes;
	}
	
	public int getWatchedEpisodes()
	{
		return watchedEpisodes;
	}
	
	public String getNextEpisodeInformation()
	{
		return nextEpisodeInformation;
	}
	
}
