package com.jitix.nbastatstream;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GameDownloader extends AsyncTask<String, Void, String> {

	private static final String TAG = "NBAStatStream";
	private static final String NO_GAME_RESULTS = "NO_RESULTS";

	//
	// Please see: erikberg.com
	//
	// Strings needed to make an API request
	//
	private static final String ACCESS_TOKEN 	= "fac55d01-3ad7-4e4f-b852-02642b0babe5";
	private static final String USER_AGENT_NAME = "NBAStatStream/1.0 (tlourchane@gmail.com)";
	private static final String AUTHORIZATION 	= "Authorization";
	private static final String BEARER 			= "Bearer " + ACCESS_TOKEN;
	private static final String USER_AGENT 		= "User-agent";
	private static final String ACCEPT_ENCODING = "Accept-encoding";
	private static final String GZIP 			= "gzip";
	private static       String EVENTS_URL 		= "https://erikberg.com/events.json?sport=nba";
	private static       String BOXSCORE_URL 	= "https://erikberg.com/nba/boxscore/";

	// TaskLisntener class
	private final TaskListener listener;

	// Flag to determine if the download was Game Events or Box Score
	private boolean boxScore;

	public GameDownloader(TaskListener listener) {
		this.listener = listener; 
	}

	@Override
	protected String doInBackground(String... params) {

		// Determine if its a call to download list of games or box score
		// -list of games will have 3 args: year, month, date
		// -box score will have 1 arg: BoxScore ID
		boxScore = (params.length == 1);

		// Construct the request URL string
		String requestURL;

		if(boxScore) {
			String boxId = params[0];
			requestURL = BOXSCORE_URL + boxId + ".json";
		} else {
			// Get the date
			String year = params[0];
			String month = params[1];
			String day = params[2];
			requestURL = EVENTS_URL + "&date=" + year + month + day;
		}

		Log.d(TAG, "GameDownloader: Request for boxscore = " + boxScore + ", requestURL = " + requestURL);

		StringBuilder sb = null;
		try {
			sb = requestAPIData(requestURL);
		} catch (IOException e) {
			Log.d(TAG, "GameDownloader: requestGameEvents : Exception thrown e = " + e);
			e.printStackTrace();
		}

		if(sb != null) {
			// Convert to a string and return it
			return sb.toString();
		} else {
			String error = "Error String";
			return error;
		}
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);

		// Call the TaskListener finished function
		Log.d(TAG, "GameDownloader: Successfully called the API and returning the string");
		if(boxScore) {
			listener.downloadedBox(result);
		} else {
			listener.downloadedGames(result);
		}
	}

	private StringBuilder requestAPIData(String requestURL) throws IOException {

		InputStream in = null;
		String encoding = null;
		try 
		{
			// Attempt to connect to the URL
			URL url = new URL(requestURL);
			URLConnection connection = url.openConnection();
			// Set Authorization header
			connection.setRequestProperty(AUTHORIZATION, BEARER);
			// Set User agent header
			connection.setRequestProperty(USER_AGENT, USER_AGENT_NAME);
			// Let server know we can handle gzip
			connection.setRequestProperty(ACCEPT_ENCODING, GZIP);

			in = connection.getInputStream();
			// Check if response was sent gzipped and decompress it
			encoding = connection.getContentEncoding();
			if(GZIP.equals(encoding)) {
				in = new GZIPInputStream(in);
			} else {
				Log.d(TAG, "GameDownloader: requestGameEvents : encoding not GZIP");
				Log.d(TAG, "Encoding = " + encoding);
			}
		}
		catch (IOException e) {
			Log.d(TAG, "GameDownloader: requestGameEvents : while connecting Exception thrown = " + e);
			e.printStackTrace();
			return null;
		}

		StringBuilder sb = new StringBuilder();
		if(in != null && encoding != null) {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			try{
				String line;
				while((line = br.readLine()) != null) {
					sb.append(line);
				}
			} 
			finally {
				Log.d(TAG, "GameDownloader: closing BufferedReader");
				br.close();
			}
		} else {
			Log.d(TAG, "GameDownloader: InputStream in == null");
			sb.append(NO_GAME_RESULTS); 
		}
		return sb;
	}
}