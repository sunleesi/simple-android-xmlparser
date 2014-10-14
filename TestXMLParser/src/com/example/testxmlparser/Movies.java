package com.example.testxmlparser;

import java.util.ArrayList;

import com.begentgroup.xmlparser.SerializedName;

public class Movies {
	public String title;
	public String link;
	public String description;
	public int total;
	public int start;
	public int display;
	
	@SerializedName("item")
	public ArrayList<MovieItem> items;
}
