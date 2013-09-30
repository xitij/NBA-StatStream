package com.jitix.nbastatstream;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.EnumMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;

/*
 * 	BasketballGame:
 *  Class to hold all the information about a given Basketball Game.
 *  This class holds all the information from the box score, and the
 *  play-by-play. It will contains team specific info and stats, as 
 *  well as player stats.
 */
public class BasketballGame {
	
	private static final String TAG = "NBAStatStream";
	
	static String HomeTeam;
	static String AwayTeam;
	static int OT;
	
	// Object to hold the standard stats for each player
	class BoxScoreLine {
		protected String 	Position;
		protected int 		Minutes;
		protected int 		FGMade;
		protected int 		FGAttempted;
		protected float 	FGPercent;
		protected int 		ThreePointMade;
		protected int 		ThreePointAttempted;
		protected float 	ThreePointPercent;
		protected int 		FTMade;
		protected int 		FTAttempted;
		protected float 	FTPercent;
		protected int 		OffReb;
		protected int 		DefReb;
		protected int 		Rebounds;
		protected int 		Assists;
		protected int 		Steals;
		protected int 		Blocks;
		protected int 		BlocksAgainst;
		protected int		Turnovers;
		protected int 		Fouls;
		protected int 		PlusMinus;
		protected int 		Points;
		
		protected void setEmpty() {
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
	
	// Object to hold the advanced stats for each player
	class AdvancedBoxScoreLine {
		protected float TrueShootingPercent;
		protected float EFGPercent;
		protected float ORebPercent;
		protected float DRebPercent;
		protected float TotRebPercent;
		protected float AssistPercent;
		protected float StealPercent;
		protected float BlockPercent;
		protected float TOPercent;
		protected float Usage;
		protected float OffRating;
		protected float DefRating;
		
		protected void setEmpty() {
			TrueShootingPercent = 0.0f;
			EFGPercent = 0.0f;
			ORebPercent = 0.0f;
			DRebPercent = 0.0f;
			TotRebPercent = 0.0f;
			AssistPercent = 0.0f;
			StealPercent = 0.0f;
			BlockPercent = 0.0f;
			TOPercent = 0.0f;
			Usage = 0.0f;
			OffRating = 0.0f;
			DefRating = 0.0f;
		}
	}
	
	// Standard team stats
	enum StatName {
		// Team stats
		FASTBREAKPOINTS, POINTSINTHEPAINT, TOTALTEAMTURNOVERS, POINTSOFFTURNOVERS,
		// Team box stats
		FG, FGA, FT, FTA, THREEP, THREEPA, OFFREB, DEFREB, REBS, ASSISTS, STEALS, BLOCKS, TURNOVERS, PF, POINTS,
		// Team box percents
		FGPERCENT, THREEPOINTPERCENT, FTPERCENT 
	}
	
	// Advanced team stats
	enum AdvancedStatName {
		// Team Advanced stats
		POSS, PACE, OFFEFF, DEFEFF, 
		// Team OFF 4 Factors
		EFGPERCENT, TOPERCENT, OREBPERCENT, FTFGA,
		// Team DEF 4 Factors
		DEFEFGPERCENT, DEFTOPERCENT, DREBPERCENT, DEFFTFGA
	}
	
	// Data structure to hold all the game information
	Map<String, BoxScoreLine> 			HomeTeamBox = new Hashtable<String, BoxScoreLine>();
	Map<String, BoxScoreLine> 			AwayTeamBox = new Hashtable<String, BoxScoreLine>();
	Map<String, AdvancedBoxScoreLine>	HomeTeamAdvBox = new Hashtable<String, AdvancedBoxScoreLine>();
	Map<String, AdvancedBoxScoreLine>	AwayTeamAdvBox = new Hashtable<String, AdvancedBoxScoreLine>();
	Map<StatName, Float>				HomeTeamStats = new EnumMap<StatName, Float>(StatName.class);
	Map<StatName, Float>				AwayTeamStats = new EnumMap<StatName, Float>(StatName.class);
	Map<AdvancedStatName, Float>		HomeTeamAdvStats = new EnumMap<AdvancedStatName, Float>(AdvancedStatName.class);
	Map<AdvancedStatName, Float>		AwayTeamAdvStats = new EnumMap<AdvancedStatName, Float>(AdvancedStatName.class);
	
	//
	// Function that is used called to populate the Team Total stats after the player
	// box scores have been parsed.
	//
	void calculateTeamTotals() {
		
		// Iterator to pass
		Iterator<Map.Entry<String, BoxScoreLine>> home_it = HomeTeamBox.entrySet().iterator();
		Iterator<Map.Entry<String, BoxScoreLine>> away_it = AwayTeamBox.entrySet().iterator();
		
		// Calculate the home team stats and put them in the map
		float[] stats = new float[18];
		stats = calculateTeamStats(home_it);
		insertHomeTeamStats(stats);
		// Calculate the home team stats and put them in the map
		stats = calculateTeamStats(away_it);
		insertAwayTeamStats(stats);
		
		// Calculate the team advanced stats and put them in the map
		float[] advstats = new float[11];
		advstats = calculateTeamAdvStats(home_it, away_it);
	}
	
	//
	// Function that is used to populate the HomeTeamStats HashTable. It will use the data from
	// HomeTeamBox to populate and calculate the missing stats.
	//
	private float[] calculateTeamStats(Iterator<Map.Entry<String, BoxScoreLine>> it) {
		// Object to return
		float[] teamstats = new float[] {0, 0, 0, 0, 0, 0,
										 0, 0, 0, 0, 0, 0,
										 0, 0, 0, 0, 0, 0};
		
		
		// Calculate the totals for FGs, FTs, and 3PTs
		while(it.hasNext()) {
			BoxScoreLine box = it.next().getValue();
			// Get each player's box score and add it to the totals
			teamstats[0] += box.FGMade;
			teamstats[1] += box.FGAttempted;
			teamstats[3] += box.FTMade;
			teamstats[4] += box.FTAttempted;
			teamstats[6] += box.ThreePointMade;
			teamstats[7] += box.ThreePointAttempted;
			teamstats[9] += box.OffReb;
			teamstats[10] += box.DefReb;
			teamstats[11] += box.Rebounds;
			teamstats[12] += box.Assists;
			teamstats[13] += box.Steals;
			teamstats[14] += box.Blocks;
			teamstats[15] += box.Turnovers;
			teamstats[16] += box.Fouls;
			teamstats[17] += box.Points;
		}
		
		// Calculate the percents
		teamstats[2] = teamstats[0] / teamstats[1];
		teamstats[5] = teamstats[3] / teamstats[4];
		teamstats[8] = teamstats[6] / teamstats[7];
		
		return teamstats;
	}
	
	private float[] calculateTeamAdvStats(Iterator<Map.Entry<String, BoxScoreLine>> home_it, 
										  Iterator<Map.Entry<String, BoxScoreLine>> away_it) {
		// Object to return
		float[] advstats = new float[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		
		
		// Calculate possession and pace: please see basketball-reference.com for an
		// explanation of this calculation: www.basketball-reference.com/about/glossary.html
		// TODO: Check this pace and possession calculations
		float poss = 0.5f * ( // Home Team possessions
							  (HomeTeamStats.get(StatName.FGA) + 0.4f * HomeTeamStats.get(StatName.FTA) 
							  - 1.07f * (HomeTeamStats.get(StatName.OFFREB) / (HomeTeamStats.get(StatName.OFFREB) + AwayTeamStats.get(StatName.DEFREB)))
							  * (HomeTeamStats.get(StatName.FGA) - HomeTeamStats.get(StatName.FG)) + HomeTeamStats.get(StatName.TURNOVERS))
						      // Away Team possessions
							  + (AwayTeamStats.get(StatName.FGA) + 0.4f * AwayTeamStats.get(StatName.FTA)
							  - 1.07f * (AwayTeamStats.get(StatName.OFFREB) / (AwayTeamStats.get(StatName.OFFREB) + HomeTeamStats.get(StatName.DEFREB)))
							  * (AwayTeamStats.get(StatName.FGA) - AwayTeamStats.get(StatName.FG)) + AwayTeamStats.get(StatName.TURNOVERS))
						    );
		HomeTeamAdvStats.put(AdvancedStatName.POSS, poss);
		AwayTeamAdvStats.put(AdvancedStatName.POSS, poss);
		float possA = HomeTeamStats.get(StatName.FGA) + HomeTeamStats.get(StatName.TURNOVERS) + 
				      0.44f * HomeTeamStats.get(StatName.FTA) - HomeTeamStats.get(StatName.OFFREB);
		float possB = AwayTeamStats.get(StatName.FGA) + AwayTeamStats.get(StatName.TURNOVERS) + 
			      0.44f * AwayTeamStats.get(StatName.FTA) - AwayTeamStats.get(StatName.OFFREB);
		float poss2 = 0.976f * (possA + possB) / 2;
		Log.d(TAG, "poss(ref) = " + poss + ", poss (.4 avg) = " + poss2 + ", possA = " + possA + ", possB = " + possB);
		float minsTotal = 48 + (OT * 5);
		float pace = 48 * poss / minsTotal ;
		HomeTeamAdvStats.put(AdvancedStatName.PACE, pace);
		AwayTeamAdvStats.put(AdvancedStatName.PACE, pace);
		
		// Calculate the Offensive and Defensive Efficiency
		float homeOffEff = HomeTeamStats.get(StatName.POINTS) * 100 / poss;
		float homeDefEff = AwayTeamStats.get(StatName.POINTS) * 100 / poss;
		Log.d(TAG, "offEff = " + homeOffEff + ", defEff = " + homeDefEff);
		homeOffEff = precision(1, homeOffEff);
		homeDefEff = precision(1, homeDefEff);
		Log.d(TAG, "After round offEff = " + homeOffEff + ", defEff = " + homeDefEff);
		HomeTeamAdvStats.put(AdvancedStatName.OFFEFF, homeOffEff);
		HomeTeamAdvStats.put(AdvancedStatName.DEFEFF, homeDefEff);
		AwayTeamAdvStats.put(AdvancedStatName.DEFEFF, homeOffEff);
		AwayTeamAdvStats.put(AdvancedStatName.OFFEFF, homeDefEff);
		
		// Calculate the 4 Factors
		// Calculate eFG%
		float eFGpercent = (HomeTeamStats.get(StatName.FG) + 0.5f *HomeTeamStats.get(StatName.THREEP)) / HomeTeamStats.get(StatName.FGA);
		HomeTeamAdvStats.put(AdvancedStatName.EFGPERCENT, eFGpercent);
		AwayTeamAdvStats.put(AdvancedStatName.DEFEFGPERCENT, eFGpercent);
		eFGpercent = (AwayTeamStats.get(StatName.FG) + 0.5f *AwayTeamStats.get(StatName.THREEP)) / AwayTeamStats.get(StatName.FGA);
		AwayTeamAdvStats.put(AdvancedStatName.EFGPERCENT, eFGpercent);
		HomeTeamAdvStats.put(AdvancedStatName.DEFEFGPERCENT, eFGpercent);
		
		// Calculate TOV%
		// TODO: Check TOV%: possession vs bball-ref formula
		float tovpercent = 100 * HomeTeamStats.get(StatName.TURNOVERS) / 
					            (HomeTeamStats.get(StatName.FGA) + 0.44f * HomeTeamStats.get(StatName.FTA) + HomeTeamStats.get(StatName.TURNOVERS));
		HomeTeamAdvStats.put(AdvancedStatName.TOPERCENT, tovpercent);
		AwayTeamAdvStats.put(AdvancedStatName.DEFTOPERCENT, tovpercent);
		tovpercent = AwayTeamStats.get(StatName.TURNOVERS) * 100 / 
			       (AwayTeamStats.get(StatName.FGA) + 0.44f * AwayTeamStats.get(StatName.FTA) + AwayTeamStats.get(StatName.TURNOVERS));
		AwayTeamAdvStats.put(AdvancedStatName.TOPERCENT, tovpercent);
		HomeTeamAdvStats.put(AdvancedStatName.DEFTOPERCENT, tovpercent);
		
		// Calculate Offensive Rebounds %
		float offrebpercent = 100 * HomeTeamStats.get(StatName.OFFREB) / 
								    (HomeTeamStats.get(StatName.OFFREB) + AwayTeamStats.get(StatName.DEFREB));
		HomeTeamAdvStats.put(AdvancedStatName.OREBPERCENT, offrebpercent);
		AwayTeamAdvStats.put(AdvancedStatName.DREBPERCENT, offrebpercent);
		offrebpercent = 100 * AwayTeamStats.get(StatName.OFFREB) /
						      (AwayTeamStats.get(StatName.OFFREB) + HomeTeamStats.get(StatName.DEFREB));
		AwayTeamAdvStats.put(AdvancedStatName.OREBPERCENT, offrebpercent);
		HomeTeamAdvStats.put(AdvancedStatName.DREBPERCENT, offrebpercent);
		
		// Calculate FT per FG Attempted
		float ftfga = 100 * HomeTeamStats.get(StatName.FT) / HomeTeamStats.get(StatName.FGA);
		HomeTeamAdvStats.put(AdvancedStatName.FTFGA, ftfga);
		AwayTeamAdvStats.put(AdvancedStatName.DEFFTFGA, ftfga);
		ftfga = 100 * AwayTeamStats.get(StatName.FT) / AwayTeamStats.get(StatName.FGA);
		AwayTeamAdvStats.put(AdvancedStatName.FTFGA, ftfga);
		HomeTeamAdvStats.put(AdvancedStatName.DEFFTFGA, ftfga);
		
		return advstats;
	}
	
	private void insertHomeTeamStats(float[] myStats) {
		// Set the stats in the Map
		HomeTeamStats.put(StatName.FG, myStats[0]);
		HomeTeamStats.put(StatName.FGA, myStats[1]);
		HomeTeamStats.put(StatName.FGPERCENT, myStats[2]);
		HomeTeamStats.put(StatName.FT, myStats[3]);
		HomeTeamStats.put(StatName.FTA, myStats[4]);
		HomeTeamStats.put(StatName.FTPERCENT, myStats[5]);
		HomeTeamStats.put(StatName.THREEP, myStats[6]);
		HomeTeamStats.put(StatName.THREEPA, myStats[7]);
		HomeTeamStats.put(StatName.THREEPOINTPERCENT, myStats[8]);
		HomeTeamStats.put(StatName.OFFREB, myStats[9]);
		HomeTeamStats.put(StatName.DEFREB, myStats[10]);
		HomeTeamStats.put(StatName.REBS, myStats[11]);
		HomeTeamStats.put(StatName.ASSISTS, myStats[12]);
		HomeTeamStats.put(StatName.STEALS, myStats[13]);
		HomeTeamStats.put(StatName.BLOCKS, myStats[14]);
		HomeTeamStats.put(StatName.TURNOVERS, myStats[15]);
		HomeTeamStats.put(StatName.PF, myStats[16]);
		HomeTeamStats.put(StatName.POINTS, myStats[17]);
	}
	
	private void insertAwayTeamStats(float[] myStats) {
		// Set the stats in the Map
		AwayTeamStats.put(StatName.FG, myStats[0]);
		AwayTeamStats.put(StatName.FGA, myStats[1]);
		AwayTeamStats.put(StatName.FGPERCENT, myStats[2]);
		AwayTeamStats.put(StatName.FT, myStats[3]);
		AwayTeamStats.put(StatName.FTA, myStats[4]);
		AwayTeamStats.put(StatName.FTPERCENT, myStats[5]);
		AwayTeamStats.put(StatName.THREEP, myStats[6]);
		AwayTeamStats.put(StatName.THREEPA, myStats[7]);
		AwayTeamStats.put(StatName.THREEPOINTPERCENT, myStats[8]);
		AwayTeamStats.put(StatName.OFFREB, myStats[9]);
		AwayTeamStats.put(StatName.DEFREB, myStats[10]);
		AwayTeamStats.put(StatName.REBS, myStats[11]);
		AwayTeamStats.put(StatName.ASSISTS, myStats[12]);
		AwayTeamStats.put(StatName.STEALS, myStats[13]);
		AwayTeamStats.put(StatName.BLOCKS, myStats[14]);
		AwayTeamStats.put(StatName.TURNOVERS, myStats[15]);
		AwayTeamStats.put(StatName.PF, myStats[16]);
		AwayTeamStats.put(StatName.POINTS, myStats[17]);
	}
	
	void printPlayerBoxScoreLine(String name, boolean awayTeam) {
		if(awayTeam == true) {
			// Away Team Player
			BoxScoreLine myPlayer = AwayTeamBox.get(name);
			Log.d(TAG, "Printing BoxScoreLine for " + name + ": " + AwayTeam);
			Log.d(TAG, "Mins =  " + myPlayer.Minutes);
			Log.d(TAG, "FGM/FGA =  " + myPlayer.FGMade + "-" + myPlayer.FGAttempted);
			Log.d(TAG, "FG% = " + myPlayer.FGPercent);
			Log.d(TAG, "3PM/3PA = " + myPlayer.ThreePointMade + "-" + myPlayer.ThreePointAttempted);
			Log.d(TAG, "3P% = " + myPlayer.ThreePointPercent);
			Log.d(TAG, "FTM/FTA = " + myPlayer.FTMade + "-" + myPlayer.FTAttempted);
			Log.d(TAG, "FT% = " + myPlayer.FTPercent);
			Log.d(TAG, "Rebounds(offREB/defREB) = " + myPlayer.Rebounds + "(" + myPlayer.OffReb + "/" + myPlayer.DefReb + ")");
			Log.d(TAG, "Assists = " + myPlayer.Assists);
			Log.d(TAG, "Steals = " + myPlayer.Steals);
			Log.d(TAG, "Blocks = " + myPlayer.Blocks);
			Log.d(TAG, "TOs = " + myPlayer.Turnovers);
			Log.d(TAG, "PF = " + myPlayer.Fouls);
			Log.d(TAG, "+/- = " + myPlayer.PlusMinus);
			Log.d(TAG, "Points = " + myPlayer.Points);
		} else {
			// Home Team Player
			BoxScoreLine myPlayer = HomeTeamBox.get(name);
			Log.d(TAG, "Printing BoxScoreLine for " + name + ":" + HomeTeam);
			Log.d(TAG, "Mins =  " + myPlayer.Minutes);
			Log.d(TAG, "FGM/FGA =  " + myPlayer.FGMade + "-" + myPlayer.FGAttempted);
			Log.d(TAG, "FG% = " + myPlayer.FGPercent);
			Log.d(TAG, "3PM/3PA = " + myPlayer.ThreePointMade + "-" + myPlayer.ThreePointAttempted);
			Log.d(TAG, "3P% = " + myPlayer.ThreePointPercent);
			Log.d(TAG, "FTM/FTA = " + myPlayer.FTMade + "-" + myPlayer.FTAttempted);
			Log.d(TAG, "FT% = " + myPlayer.FTPercent);
			Log.d(TAG, "Rebounds(offREB/defREB) = " + myPlayer.Rebounds + "(" + myPlayer.OffReb + "/" + myPlayer.DefReb + ")");
			Log.d(TAG, "Assists = " + myPlayer.Assists);
			Log.d(TAG, "Steals = " + myPlayer.Steals);
			Log.d(TAG, "Blocks = " + myPlayer.Blocks);
			Log.d(TAG, "TOs = " + myPlayer.Turnovers);
			Log.d(TAG, "PF = " + myPlayer.Fouls);
			Log.d(TAG, "+/- = " + myPlayer.PlusMinus);
			Log.d(TAG, "Points = " + myPlayer.Points);
		}
	}
	
	
	void printTeamStats(String teamName) {
		Log.d(TAG, "Printing Team Stats for " + teamName);
		if(teamName == HomeTeam) {
			Log.d(TAG, "Fast Break Points = " + HomeTeamStats.get(StatName.FASTBREAKPOINTS));
			Log.d(TAG, "Points in the paint = " + HomeTeamStats.get(StatName.POINTSINTHEPAINT));
			Log.d(TAG, "Total Team TOs = " + HomeTeamStats.get(StatName.TOTALTEAMTURNOVERS));
			Log.d(TAG, "Points off TOs = " + HomeTeamStats.get(StatName.POINTSOFFTURNOVERS));
			Log.d(TAG, "FGs = " + HomeTeamStats.get(StatName.FG));
			Log.d(TAG, "FGAs = " + HomeTeamStats.get(StatName.FGA));
			Log.d(TAG, "FG% = " + HomeTeamStats.get(StatName.FGPERCENT));
			Log.d(TAG, "FTs = " + HomeTeamStats.get(StatName.FT));
			Log.d(TAG, "FTAs = " + HomeTeamStats.get(StatName.FTA));
			Log.d(TAG, "FT% = " + HomeTeamStats.get(StatName.FTPERCENT));
			Log.d(TAG, "3PTs = " + HomeTeamStats.get(StatName.THREEP));
			Log.d(TAG, "3PTAs = " + HomeTeamStats.get(StatName.THREEPA));
			Log.d(TAG, "3PT% = " + HomeTeamStats.get(StatName.THREEPOINTPERCENT));
			Log.d(TAG, "OFF REB = " + HomeTeamStats.get(StatName.OFFREB));
			Log.d(TAG, "DEF REB = " + HomeTeamStats.get(StatName.DEFREB));
			Log.d(TAG, "REBs = " + HomeTeamStats.get(StatName.REBS));
			Log.d(TAG, "Assists = " + HomeTeamStats.get(StatName.ASSISTS));
			Log.d(TAG, "Steals = " + HomeTeamStats.get(StatName.STEALS));
			Log.d(TAG, "Blocks = " + HomeTeamStats.get(StatName.BLOCKS));
			Log.d(TAG, "TOs = " + HomeTeamStats.get(StatName.TURNOVERS));
			Log.d(TAG, "PF = " + HomeTeamStats.get(StatName.PF));
			Log.d(TAG, "Points = " + HomeTeamStats.get(StatName.POINTS));
		} else if(teamName == AwayTeam) {
			Log.d(TAG, "Fast Break Points = " + AwayTeamStats.get(StatName.FASTBREAKPOINTS));
			Log.d(TAG, "Points in the paint = " + AwayTeamStats.get(StatName.POINTSINTHEPAINT));
			Log.d(TAG, "Total Team TOs = " + AwayTeamStats.get(StatName.TOTALTEAMTURNOVERS));
			Log.d(TAG, "Points off TOs = " + AwayTeamStats.get(StatName.POINTSOFFTURNOVERS));
			Log.d(TAG, "FGs = " + AwayTeamStats.get(StatName.FG));
			Log.d(TAG, "FG% = " + AwayTeamStats.get(StatName.FGPERCENT));
			Log.d(TAG, "FTs = " + AwayTeamStats.get(StatName.FT));
			Log.d(TAG, "FT% = " + AwayTeamStats.get(StatName.FTPERCENT));
			Log.d(TAG, "3PTs = " + AwayTeamStats.get(StatName.THREEP));
			Log.d(TAG, "3PT% = " + AwayTeamStats.get(StatName.THREEPOINTPERCENT));
			Log.d(TAG, "OFF REB = " + AwayTeamStats.get(StatName.OFFREB));
			Log.d(TAG, "DEF REB = " + AwayTeamStats.get(StatName.DEFREB));
			Log.d(TAG, "REBs = " + AwayTeamStats.get(StatName.REBS));
			Log.d(TAG, "Assists = " + AwayTeamStats.get(StatName.ASSISTS));
			Log.d(TAG, "Steals = " + AwayTeamStats.get(StatName.STEALS));
			Log.d(TAG, "Blocks = " + AwayTeamStats.get(StatName.BLOCKS));
			Log.d(TAG, "TOs = " + AwayTeamStats.get(StatName.TURNOVERS));
			Log.d(TAG, "PF = " + AwayTeamStats.get(StatName.PF));
			Log.d(TAG, "Points = " + AwayTeamStats.get(StatName.POINTS));
		}
	}

	void printTeamAdvStats(String teamName) {
		Log.d(TAG, "Printing Team Advanced Stats for " + teamName);
		if(teamName == HomeTeam) {
			Log.d(TAG, "Pace = " + HomeTeamAdvStats.get(AdvancedStatName.PACE));
			Log.d(TAG, "Offensive Efficiency = " + HomeTeamAdvStats.get(AdvancedStatName.OFFEFF));
			Log.d(TAG, "Defensive Efficiency = " + HomeTeamAdvStats.get(AdvancedStatName.DEFEFF));
			Log.d(TAG, "Effective FG% = " + HomeTeamAdvStats.get(AdvancedStatName.EFGPERCENT));
			Log.d(TAG, "Turnover % = " + HomeTeamAdvStats.get(AdvancedStatName.TOPERCENT));
			Log.d(TAG, "Offensive Rebounding % = " + HomeTeamAdvStats.get(AdvancedStatName.OREBPERCENT));
			Log.d(TAG, "FT Made / FG Attempted = " + HomeTeamAdvStats.get(AdvancedStatName.FTFGA));
			Log.d(TAG, "Defensive Effective FG% = " + HomeTeamAdvStats.get(AdvancedStatName.DEFEFGPERCENT));
			Log.d(TAG, "Defensive Turnover % = " + HomeTeamAdvStats.get(AdvancedStatName.DEFTOPERCENT));
			Log.d(TAG, "(Defensive) Offensive Rebounding % Allowed = " + HomeTeamAdvStats.get(AdvancedStatName.DREBPERCENT));
			Log.d(TAG, "Defensive FT Made / FG Attempted = " + HomeTeamAdvStats.get(AdvancedStatName.DEFFTFGA));
		} else if(teamName == AwayTeam) {
			Log.d(TAG, "Pace = " + AwayTeamAdvStats.get(AdvancedStatName.PACE));
			Log.d(TAG, "Offensive Efficiency = " + AwayTeamAdvStats.get(AdvancedStatName.OFFEFF));
			Log.d(TAG, "Defensive Efficiency = " + AwayTeamAdvStats.get(AdvancedStatName.DEFEFF));
			Log.d(TAG, "Effective FG% = " + AwayTeamAdvStats.get(AdvancedStatName.EFGPERCENT));
			Log.d(TAG, "Turnover % = " + AwayTeamAdvStats.get(AdvancedStatName.TOPERCENT));
			Log.d(TAG, "Offensive Rebounding % = " + AwayTeamAdvStats.get(AdvancedStatName.OREBPERCENT));
			Log.d(TAG, "FT Made / FG Attempted = " + AwayTeamAdvStats.get(AdvancedStatName.FTFGA));
			Log.d(TAG, "Defensive Effective FG% = " + AwayTeamAdvStats.get(AdvancedStatName.DEFEFGPERCENT));
			Log.d(TAG, "Defensive Turnover % = " + AwayTeamAdvStats.get(AdvancedStatName.DEFTOPERCENT));
			Log.d(TAG, "(Defensive) Offensive Rebounding % Allowed = " + AwayTeamAdvStats.get(AdvancedStatName.DREBPERCENT));
			Log.d(TAG, "Defensive FT Made / FG Attempted = " + AwayTeamAdvStats.get(AdvancedStatName.DEFFTFGA));
		}
	}
	
	private Float precision(int decimalPlace, Float d) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}
	
}
