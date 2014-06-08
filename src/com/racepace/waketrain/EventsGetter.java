package com.racepace.waketrain;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class EventsGetter implements Serializable {
	 private static final long serialVersionUID = -8959832007991513854L;
	
	private String title;
	private String content;
	private ArrayList<String> location;
	
	// Variables for startTime
	private ArrayList<String> startTime;
	private String start;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String startParse;
	
	ISO8601DateParser DateParser = new ISO8601DateParser();
	
	public String getTitle() {
		
		return title;
	}
	
	public String getContent() {
		return content;
	}
	
	public String getLocation() {
		return TextUtils.join(", ", location);
	}
	
	public String getStartMonth() {
		startParse = (String) startTime.get(0);
		try {
			start = DateParser.parse(startParse).toString();
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Return month
		return (start.substring(4, 8));
	}
	
	public String getStartDate() {
		startParse = (String) startTime.get(0);
		try {
			start = DateParser.parse(startParse).toString();
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Return day of the month
		return (start.substring(8, 10));

	}
	
	public String getStartTime() {
		startParse = (String) startTime.get(0);
		try {
			start = DateParser.parse(startParse).toString();
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int time = Integer.parseInt(start.substring(11,13));

		// check if time is more or less than 12 (noon), and return string that adds AM or PM
		if( time <= 12) {
			return (start.substring(11, 16) + " AM");
		} else {
			time = time - 12;
			return (time + start.substring(13,16) +" PM");
		} 
	}

	public static EventsGetter fromJson(JSONObject jsonObject) {
		EventsGetter e = new EventsGetter();
		try {
			e.title = jsonObject.getJSONObject("title").getString("$t");
			e.content = jsonObject.getJSONObject("content").getString("$t");
			e.location = new ArrayList<String>();
			// Getting location from array
			JSONArray where = jsonObject.getJSONArray("gd$where");
			 for (int i = 0; i < where.length(); i++) {
	                e.location.add(where.getJSONObject(i).getString("valueString"));
	            }
			
			 e.startTime = new ArrayList<String>();
			// Getting start time from array
			JSONArray beginTime = jsonObject.getJSONArray("gd$when");
			for (int i = 0; i < beginTime.length(); i++) {
				 e.startTime.add(beginTime.getJSONObject(i).getString("startTime"));
			 }
		
		} catch (JSONException ex) {
			ex.printStackTrace();
			return null;
			}
		
		return e;
	}
	
	// Decodes array of EventsGetter results into business model objects
	public static ArrayList<EventsGetter> fromJson(JSONArray jsonArray) {
		ArrayList<EventsGetter> businesses = new ArrayList<EventsGetter>(jsonArray.length());
		// process each result in jsonArray, decode and convert to business object
		
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject businessJson = null;
			
			try {
				businessJson = jsonArray.getJSONObject(i);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			
			EventsGetter business = EventsGetter.fromJson(businessJson);
			if (business != null) {
				businesses.add(business);
			}
		}
		
		return businesses;
	}

}
