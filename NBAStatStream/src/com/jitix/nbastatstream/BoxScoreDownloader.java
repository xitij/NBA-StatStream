package com.jitix.nbastatstream;

import android.os.AsyncTask;
import android.util.Log;

/**
 * BoxScoreDownloader:
 * 	Is a class that extends the AsyncTask. It attempts to make a http
 * 	connection to the URL the users provided. It will print an error
 * 	if the URL is invalid, otherwise it will use the BasketballParser
 * 	class to parse the data into a BasketballGame object and return it
 * 	to the calling Activity.
 */
public class BoxScoreDownloader extends AsyncTask<String, Void, BasketballGame> {

	private static final String TAG = "NBAStatStream";
	
	// TaskLisntener class
	private final TaskListener listener;
	
	// BasketballGame object to return
	BasketballGame parsedGame;
	
	// GameParser object
	private GameParser gameParser;
	
	public BoxScoreDownloader(TaskListener listener) {
		this.listener = listener; 
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		parsedGame = new BasketballGame();
		gameParser = new GameParser();
	}

	@Override
	protected BasketballGame doInBackground(String... params) {

		// Get the string URL
		String url = params[0];
		
		// Attempt to connect to the URL and get the HTML
		try 
		{
			// Connect and get the HTML
			parsedGame = gameParser.connectAndGet(url);
		}
		/*catch (MalformedURLException e) {
			Log.d(TAG, "malformed URL, not a http");
			return null;
		}
		catch (HttpStatusException e) {
			Log.d(TAG, "http response not OK and response error not ignored");
			return null;
		}
		catch (UnsupportedMimeTypeException e) {
			Log.d(TAG, "Response mime type is not supported");
			return null;
		}
		catch (SocketTimeoutException e ) {
			Log.d(TAG,  "Connection timed out");
			return null;
		}
		catch (IOException e) {
			Log.d(TAG, "IOException Error getting HTML file");
			return null;
		}*/
		catch (Exception e) {
			Log.d(TAG, "Exception thrown = " + e);
			return null;
		}
		return parsedGame;
	}
	
	@Override
	protected void onPostExecute(BasketballGame result) {
		super.onPostExecute(result);
		
		// Set the Basketball for the parent class
		// Call the TaskListener finished function
		Log.d(TAG, "Done connecting and parsing the Game");
		listener.onTaskFinished(result);
	}
}