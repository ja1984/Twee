package se.ja1984.twee.models;

public class Profile {
	private Integer Id;
	private String Name;
	
	public String getName()
	{
		return Name;
	}
	public void setName(String name)
	{
		this.Name = name;
	}
	
	public Integer getId()
	{
		return Id;
	}
	
	public void setId(Integer id)
	{
		this.Id = id;
	}
	
}
