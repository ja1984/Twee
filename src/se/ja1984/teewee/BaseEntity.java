package se.ja1984.teewee;

public class BaseEntity {

	private int Id;
	private String Summary;
	private String LastUpdated;
	
	
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
