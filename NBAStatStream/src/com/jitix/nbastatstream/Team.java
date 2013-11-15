package com.jitix.nbastatstream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
 * Team:
 * 	please see erikberg.com/api
 * 	Class to that is mapped to hold the JSON Events received
 * 	from the xmlstats API for a specific Team.
 */

// Ignore unmapped objects instead of throwing parse exception
@JsonIgnoreProperties(ignoreUnknown=true)
public class Team {
	
	@JsonProperty("team_id")
	private String teamId;
	
	@JsonProperty("abbreviation")
	private String abbrev;
	
	@JsonProperty("first_name")
	private String firstName;
	
	@JsonProperty("last_name")
	private String lastName;
	
	@JsonProperty("conference")
	private String conference;
	
	@JsonProperty("division")
	private String division;
	
	@JsonProperty("site_name")
	private String siteName;
	
	@JsonProperty("city")
	private String city;
	
	@JsonProperty("state")
	private String state;

	@JsonProperty("full_name")
	private String fullName;
	
	public String getTeamId() {
		return teamId;
	}
	
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	
	public String getAbbrev() {
		return abbrev;
	}
	
	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getConference() {
		return conference;
	}
	
	public void setConference(String conference) {
		this.conference = conference;
	}
	
	public String getDivision() {
		return division;
	}
	
	public void setDivision(String division) {
		this.division = division;
	}
	
	public String getSiteName() {
		return siteName;
	}
	
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
