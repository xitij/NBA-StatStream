package com.jitix.nbastatstream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
 * BoxScoreLine:
 * 	Class to hold the standard stats for each player. Also
 * 	maps to the xmlstats API JSON object for Basketball Stats 
 */

//Ignore unmapped objects instead of throwing parse exception
@JsonIgnoreProperties(ignoreUnknown=true)
class BoxScoreLine {
	
	@JsonProperty("last_name")
	public String LastName;
	@JsonProperty("first_name")
	private String FirstName;
	@JsonProperty("display_name")
	public String Name;
	@JsonProperty("position")
	public String Position;
	@JsonProperty("team_abbreviation")
	private String TeamAbbrev;
	@JsonProperty("is_starter")
	public boolean isStarter;
	@JsonProperty("minutes")
	public int	Minutes;
	@JsonProperty("field_goals_made")
	public int	FGMade;
	@JsonProperty("field_goals_attempted")
	public int	FGAttempted;
	@JsonProperty("field_goal_percentage")
	public float FGPercent;
	@JsonProperty("three_point_field_goals_made")
	public int ThreePointMade;
	@JsonProperty("three_point_field_goals_attempted")
	public int ThreePointAttempted;
	@JsonProperty("three_point_percentage")
	public float ThreePointPercent;
	@JsonProperty("free_throws_made")
	public int FTMade;
	@JsonProperty("free_throws_attempted")
	public int FTAttempted;
	@JsonProperty("free_throw_percentage")
	public float FTPercent;
	@JsonProperty("offensive_rebounds")
	public int OffReb;
	@JsonProperty("defensive_rebounds")
	public int DefReb;
	public int Rebounds;
	@JsonProperty("assists")
	public int Assists;
	@JsonProperty("steals")
	public int Steals;
	@JsonProperty("blocks")
	public int Blocks;
	public int BlocksAgainst;
	@JsonProperty("turnovers")
	public int	Turnovers;
	@JsonProperty("personal_fouls")
	public int Fouls;
	public int PlusMinus;
	@JsonProperty("points")
	public int Points;

	public String getLastName() {
		return LastName;
	}
	
	public void setLastName(String lastName) {
		this.LastName = lastName;
	}
	
	public String getFirstName() {
		return FirstName;
	}
	
	public void setFirstName(String firstName) {
		this.FirstName = firstName;
	}
	
	public String getName() {
		return Name;
	}
	
	public void setName(String name) {
		this.Name = name;
	}
	
	public String getPosition() {
		return Position;
	}
	
	public void setPosition(String position) {
		this.Position = position;
	}
	
	public String getTeam() {
		return TeamAbbrev;
	}
	
	public void setTeam(String teamAbbrev) {
		this.TeamAbbrev = teamAbbrev;
	}
	
	public int getMinutes() {
		return Minutes;
	}
	
	public void setMinutes(int minutes) {
		this.Minutes = minutes;
	}
	
	public int getFGMade() {
		return FGMade;
	}
	
	public void setFGMade(int fgmade) {
		this.FGMade = fgmade;
	}
	
	public int getFGAttempted() {
		return FGAttempted;
	}
	
	public void setFGAttempted(int fgattempted) {
		this.FGAttempted = fgattempted;
	}
	
	public float getFGPercent() {
		return FGPercent;
	}
	
	public void setFGPercent(float fgpercent) {
		this.FGPercent = fgpercent;
	}
	
	public void setEmpty() {
		Minutes = 0;
		FGMade = 0;
		FGAttempted = 0;
		FGPercent = 0.0f;
		ThreePointMade = 0;
		ThreePointAttempted = 0;
		ThreePointPercent = 0.0f;
		FTMade = 0;
		FTAttempted = 0;
		FTPercent = 0;
		OffReb = 0;
		DefReb = 0;
		Rebounds = 0;
		Assists = 0;
		Steals = 0;
		Blocks = 0;
		BlocksAgainst = 0;
		Turnovers = 0;
		Fouls = 0;
		PlusMinus = 0;
		Points = 0;
	}
}