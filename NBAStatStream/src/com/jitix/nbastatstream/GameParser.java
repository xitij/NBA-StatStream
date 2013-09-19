package com.jitix.nbastatstream;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import org.jsoup.HttpStatusException;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

	private void parseBoxScore(Document myDoc) {
		// TODO Auto-generated method stub
		
	}

	private void parsePlaybyPlay(Document myDoc) {
		// TODO Auto-generated method stub
		
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
