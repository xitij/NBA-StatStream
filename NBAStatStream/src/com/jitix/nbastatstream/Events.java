package com.jitix.nbastatstream;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/*
 * Events:
 * 	please see erikberg.com/api
 * 	Class to that is mapped to hold the JSON Events received
 * 	from the xmlstats API call to get Events for a specific
 * 	date. Contains a date and a ArrayList of all NBA games
 * 	on the given date.
 */
public class Events {
	
	// String for the date
	@JsonProperty("events_date")
	private String eventsDate;
	
	// ArrayList for the NBA games (events)
	@JsonProperty("event")
	private ArrayList<Event> eventList;
	
	public Events() { }
	
	public String getEventsDate() {
		return eventsDate;
	}
	
	public void setEventsDate(String eventsDate) {
		this.eventsDate = eventsDate;
	}
	
	public ArrayList<Event> getEventList() {
		return eventList;
	}
	
	public void setEventList(ArrayList<Event> eventList) {
		this.eventList = eventList;
	}
}
