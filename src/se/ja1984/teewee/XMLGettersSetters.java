package se.ja1984.teewee;

import java.util.ArrayList;

import android.util.Log;

public class XMLGettersSetters {

	private ArrayList<String> company = new ArrayList<String>();
	public ArrayList<String> getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company.add(company);
		Log.i("This is the company:", company);
	}
}