package com.jitix.nbastatstream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
 * Official:
 * 	please see erikberg.com/api
 * 	Class to that is mapped to hold the JSON Events received
 * 	from the xmlstats API for a specific Official.
 */

// Ignore unmapped objects instead of throwing parse exception
@JsonIgnoreProperties(ignoreUnknown=true)
public class Official {

	@JsonProperty("first_name")
	private String firstName;
	
	@JsonProperty("last_name")
	private String lastName;
	
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
}
