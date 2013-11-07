package com.jitix.nbastatstream;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Set;
import org.jsoup.HttpStatusException;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class GameParser {
	
	private static final String TAG = "NBAStatStream";
	
	// Jsoup object to parse the html
	private static Jsoup myParser;
	
	// BasketballGame object to set and return
	BasketballGame parsedGame = new BasketballGame();
	
	// Flag for where the URL came from
	private static int sourcePage;
	
	// Defines for the source type
	private static final int IS_ESPN = 10;
	private static final int IS_NBA = 11;
	// Defines for the game URL type
	private static final int PLAY_BY_PLAY = 0;
	private static final int BOX_SCORE = 1;
	
	// String to hold the URL to be parsed
	String myURL;
	
	// Object to contain the Document that is parsed
	Document gameDocument;
	
	GameParser() {
		myURL = null;
	}
	
	public BasketballGame connectAndGet(String url) {
		
		int gameType;
		
		// Set and check the URL
		try {
			setAndCheckUrl(url);
			Log.d(TAG, "Document was returned properly...");
			
			// Check whether the Document is a Play-by-Play or BoxScore
			gameType = checkGameType(gameDocument);
			
			// Call the proper parse function
			if(gameType == PLAY_BY_PLAY) {
				parsePlaybyPlay(gameDocument);
			} else if(gameType == BOX_SCORE){
				parseBoxScore(gameDocument);
			} else {
				Log.d(TAG, "Unknown gameType = " + gameType);
			}
			
			// Close the connection
			
			// Append the other boxscore or PbP URL
			String newUrl = appendNewUrl(url);
			
			// call setAndCheck
			setAndCheckUrl(newUrl);
			Log.d(TAG, "Second Document was returned properly...");
			
			// Call the opposite parse function
			if(gameType == PLAY_BY_PLAY) {
				parseBoxScore(gameDocument);
			} else if(gameType == BOX_SCORE) {
				parsePlaybyPlay(gameDocument);
			} else {
				Log.d(TAG, "Unknown gameType = " + gameType);
			}
			
			
		} catch (Exception e) {
			Log.d(TAG, "connectAndGet received Exception from setAndCheckUrl", e);
		}
		
		// return the finished parsedGame
		return parsedGame;
	}
	
	//
	// This function takes the previous stored URL for the 
	// parser and converts it from box score to play-by-play
	// or vice versa. It current works for ESPN.com and
	// NBA.com (more to implement later). This can be done 
	// because the URLs are similar and change the same every
	// time.
	//
	private String appendNewUrl(String url) {
		
		Log.d(TAG, "old url = " + url);
		
		StringBuilder urlBuilder = new StringBuilder(url);
		
		///////////////////////////////////////////////////////////////////
		// ESPN URL case
		///////////////////////////////////////////////////////////////////
		if(sourcePage == IS_ESPN) {
			// check whether is a box or play-by-play
			boolean is_box = url.toLowerCase().contains("boxscore");
			
			if(is_box) {
				urlBuilder.replace(urlBuilder.indexOf("boxscore"), 
									urlBuilder.indexOf("boxscore") + "boxscore".length(), 
									"playbyplay");
				urlBuilder.append("&period=0");
			} else {
				urlBuilder.replace(urlBuilder.indexOf("playbyplay"), 
									urlBuilder.indexOf("playbyplay") + "playbyplay".length(),
									"boxscore");
			}
		}
		///////////////////////////////////////////////////////////////////
		// NBA URL case
		///////////////////////////////////////////////////////////////////
		else {
			// TODO: NBA.com case
		}
		
		Log.d(TAG, "new url = " + urlBuilder.toString());
		return urlBuilder.toString();
	}

	// 
	// This function does most of the heavy lifting. It will
	// parse a box score HTML page and populate the BasketballGame
	// object.
	//
	private void parseBoxScore(Document myDoc) {
		
		// Set the team names
		String gameTitle = gameDocument.title().toString();
		parsedGame.parseDate( gameTitle.split("-")[2] );
		parsedGame.AwayTeam = gameTitle.substring(0, gameTitle.indexOf("vs.") - 1);
		parsedGame.HomeTeam = gameTitle.substring(gameTitle.indexOf("vs.") + 4, gameTitle.indexOf("-") - 1);
		
		// Determine if the game went to OT and set the amount it so
		Elements gameState = myDoc.body().getElementsByClass("game-state");
		Log.d(TAG, "gameState = " + gameState.text());
		if(gameState.text().contains("OT")) {
			// OT game
			if(gameState.text().split("/")[1].trim().startsWith("OT", 0)) {
				parsedGame.OT = 1;
				Log.d(TAG, "Game went to OT, setting OT = " + parsedGame.OT);
			}
			// Multiple OT game
			else {
				parsedGame.OT = Integer.parseInt(gameState.text().split("/")[1].trim().substring(0, gameState.text().split("/")[1].length() - 2));
				Log.d(TAG, "Game went to multiple OTs, setting OT = " + parsedGame.OT);
			}
		} else {
			parsedGame.OT = 0;
			Log.d(TAG, "Game Final, setting OT = " + parsedGame.OT);
		}
		
		// Begin parsing to the box score, get the class (ESPN)
		Elements boxscore = myDoc.body().getElementsByClass("story-container");
		
		//
		// Parse the box score table
		// Away Team Starters: 	tbody = 1
		// Away Team Bench:		tbody = 3
		// Away Team Totals:	tbody = 5
		// Home Team Starters:	tbody = 7
		// Home Team Bench:		tbody = 9
		// Home Team Totals:	tbody = 11
		//
		for(int tbody=1; tbody < 12; tbody +=2) {
			// Parse the players
			if(tbody != 5 && tbody != 11) {
				for(Element row : boxscore.select("table > tbody:eq("+ tbody + ") > tr")) {
					// Get the player name
					String[] boxLine = row.text().split(",");
					String playerName = boxLine[0];
					String[] playerLine = boxLine[1].trim().split(" ");
					// Parse the player line string into the BasketballGame object
					BoxScoreLine playerBox = parsePlayerBoxScore(playerLine);
					// Insert the starting player into the correct Team box score
					if(tbody < 6) {
						parsedGame.AwayTeamBox.put(playerName, playerBox);
					} else {
						parsedGame.HomeTeamBox.put(playerName, playerBox);
					}
				}
			}
			// Parse the totals
			else {
				for(Element row : boxscore.select("table > tbody:eq("+ tbody + ") > tr:eq(2) > td > div:eq(0)")) {
					String[] totalrow = row.text().split(":");
					
					String fbp = totalrow[1].split("P")[0];
					// replace the &nbsp with nothing so we can parse
					fbp = fbp.replace("\u00a0", "");
					if(tbody == 5) {
						parsedGame.AwayTeamStats.put(BasketballGame.StatName.FASTBREAKPOINTS, Float.parseFloat(fbp));
					} else {
						parsedGame.HomeTeamStats.put(BasketballGame.StatName.FASTBREAKPOINTS, Float.parseFloat(fbp));
					}
					
					String pip = totalrow[2].split("T")[0];
					pip = pip.replace("\u00a0", "");
					if(tbody == 5) {
						parsedGame.AwayTeamStats.put(BasketballGame.StatName.POINTSINTHEPAINT, Float.parseFloat(pip));
					} else {
						parsedGame.HomeTeamStats.put(BasketballGame.StatName.POINTSINTHEPAINT, Float.parseFloat(pip));
					}
					
					String ttt = totalrow[3].split("\\(")[0];
					ttt = ttt.replace("\u00a0", "");
					if(tbody == 5) {
						parsedGame.AwayTeamStats.put(BasketballGame.StatName.TOTALTEAMTURNOVERS, Float.parseFloat(ttt));
					} else {
						parsedGame.HomeTeamStats.put(BasketballGame.StatName.TOTALTEAMTURNOVERS, Float.parseFloat(ttt));
					}
					
					String pot = totalrow[3].split("\\(")[1].substring(0, totalrow[3].split("\\(")[1].length()-1);
					if(tbody == 5) {
						parsedGame.AwayTeamStats.put(BasketballGame.StatName.POINTSOFFTURNOVERS, Float.parseFloat(pot));
					} else {
						parsedGame.HomeTeamStats.put(BasketballGame.StatName.POINTSOFFTURNOVERS, Float.parseFloat(pot));
					}
				}
			}
		}
		Log.d(TAG, "# of players parsed = " + (parsedGame.AwayTeamBox.size() + parsedGame.HomeTeamBox.size()));
		
		// Calculate the total team stats and player advanced stats
		parsedGame.calculateTeamTotals();
		parsedGame.calculatePlayerAdvStats();
	}

	private void parsePlaybyPlay(Document myDoc) {
		// TODO Auto-generated method stub
		
	}
	
	private BoxScoreLine parsePlayerBoxScore(String[] playerLine) {
		
		BoxScoreLine playerBox = new BoxScoreLine();
		
		// See ESPN.com box score for the order of these...
		// Position
		playerBox.Position = playerLine[0];
		// Minutes
		// Check for DNP...
		if(playerLine[1].matches("DNP")) {
			playerBox.setEmpty();
		} else {
			playerBox.Minutes = Integer.parseInt(playerLine[1]);
			// FG Made
			playerBox.FGMade = Integer.parseInt(playerLine[2].split("-")[0]);
			// FG Attempted
			playerBox.FGAttempted = Integer.parseInt(playerLine[2].split("-")[1]);
			// FG %
			if(playerBox.FGAttempted == 0) { playerBox.FGPercent = 0.0f; }
			else { playerBox.FGPercent = (float) playerBox.FGMade / playerBox.FGAttempted; }
			// 3P Made
			playerBox.ThreePointMade = Integer.parseInt(playerLine[3].split("-")[0]);
			// 3P Attempted
			playerBox.ThreePointAttempted = Integer.parseInt(playerLine[3].split("-")[1]);
			// 3P %
			if(playerBox.ThreePointAttempted == 0) { playerBox.ThreePointPercent = 0.0f; }
			else { playerBox.ThreePointPercent = (float) playerBox.ThreePointMade / playerBox.ThreePointAttempted; }
			// FT Made
			playerBox.FTMade = Integer.parseInt(playerLine[4].split("-")[0]);
			// FT Attempted
			playerBox.FTAttempted = Integer.parseInt(playerLine[4].split("-")[1]);
			// FT %
			if(playerBox.FTAttempted == 0) { playerBox.FTPercent = 0.0f; }
			else { playerBox.FTPercent = (float) playerBox.FTMade / playerBox.FTAttempted; }
			// Offensive Reb
			playerBox.OffReb = Integer.parseInt(playerLine[5]);
			// Defensive Reb
			playerBox.DefReb = Integer.parseInt(playerLine[6]);
			// Rebounds
			playerBox.Rebounds = Integer.parseInt(playerLine[7]);
			// Assists
			playerBox.Assists = Integer.parseInt(playerLine[8]);
			// Steals
			playerBox.Steals = Integer.parseInt(playerLine[9]);
			// Blocks
			playerBox.Blocks = Integer.parseInt(playerLine[10]);
			// Blocks Against
			playerBox.BlocksAgainst = 0;
			// Turnovers
			playerBox.Turnovers = Integer.parseInt(playerLine[11]);
			// Personal Fouls
			playerBox.Fouls = Integer.parseInt(playerLine[12]);
			// +/-
			if(playerLine[13].contains("+")) { playerBox.PlusMinus = Integer.parseInt(playerLine[13].substring(1)); } 
			else { playerBox.PlusMinus = Integer.parseInt(playerLine[13]); }
			// Points
			playerBox.Points = Integer.parseInt(playerLine[14]);
		}
		return playerBox;
	}

	private int checkGameType(Document myDoc) {
		
		// ESPN case
		if(sourcePage == IS_ESPN) {
			// Parse the Document for the title
			if( gameDocument.title().contains("Box Score") ) {
				Log.d(TAG, "gameType = BOX_SCORE");
				return BOX_SCORE;
			} else {
				Log.d(TAG, "gameType = PLAY_BY_PLAY");
				return PLAY_BY_PLAY;
			}
		} 
		// NBA case
		else {
			return BOX_SCORE;
		}
	}

	//
	// This function sets the string URL in the class. Next it 
	// appends "http://" to the URL if needed and  uses the Parser
	// to check the URL. It throws an exception if it's invalid. 
	// Otherwise it grabs the html in a Document using the parser.
	//
	private void setAndCheckUrl(String url) throws Exception {
		
		StringBuilder urlBuilder = new StringBuilder(url);
		
		// Make sure the string begins with "http://" otherwise append it
		if(url.startsWith("http://") == false) {
			urlBuilder.insert(0, "http://");
		}
		
		// Check and set URL source
		sourcePage = url.toLowerCase().contains("espn") ? IS_ESPN : IS_NBA;
		
		Log.d(TAG, "myUrl = " + urlBuilder.toString());
		myURL = urlBuilder.toString();

		// Attempt to connect to the URL
		try {
			gameDocument = Jsoup.connect(myURL).get();
		}
		catch (Exception e) {
			Log.d(TAG, "Exception thrown from connect or get. Exception = " + e);
			throw e;
		}
	}
}
