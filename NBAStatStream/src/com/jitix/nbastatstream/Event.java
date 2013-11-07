package com.jitix.nbastatstream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * Game:
 * 	Class that is mapped to hold the JSON Event received
 * 	from xmlstats API call to get information for a specific
 * 	Event (or this case a Game). Contains various fields to
 * 	hold Event information.
 */

// Ignore unmapped objects instead of throwing parse exception
@JsonIgnoreProperties(ignoreUnknown=true)
public class Event {
	
	// Event id used by xmlstats nba box score
	@JsonProperty("event_id")
	private String eventId;
	
	// Status of game: scheduled, completed, postponed, suspended, cancelled.
	// If "completed" the game has been processed by the database
	@JsonProperty("event_status")
	private String eventStatus;
	
	// Scheduled date and time: YYYY-MM-DDThh:mm:ss+/-hh:mm ISO 8601 format
	// For TBD events special code of: 03:45:56 with ET (Eastern Time Zone)
	@JsonProperty("start_date_time")
	private String startDateTime;
	
	// Season type: pre, regular, post
	@JsonProperty("season_type")
	private String seasonType;
	
	// Team object
	@JsonProperty("away_team")
	private Team awayTeam;
	
	// Team object
	@JsonProperty("home_team")
	private Team homeTeam;
	
	// Site object
	@JsonProperty("site")
	private Site site;
	
	public Event() { }
	
	public String getEventId() {
		return eventId;
	}
	
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	
	public String getEventStatus() {
		return eventStatus;
	}
	
	public void setEventStatus(String eventStatus) {
		this.eventStatus = eventStatus;
	}
	
	public String getStartDateTime() {
		return startDateTime;
	}
	
	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}
	
	public String getSeasonType() {
		return seasonType;
	}
	
	public void setSeasonType(String seasonType) {
		this.seasonType = seasonType;
	}
	
	public Team getHomeTeam() {
		return homeTeam;
	}
	
	public void setHomeTeam(Team homeTeam) {
		this.homeTeam = homeTeam;
	}
	
	public Team getAwayTeam() {
		return awayTeam;
	}
	
	public void setAwayTeam(Team awayTeam) {
		this.awayTeam = awayTeam;
	}
	
	public Site getSite() {
		return site;
	}
	
	public void setSite(Site site) {
		this.site = site;
	}
}
