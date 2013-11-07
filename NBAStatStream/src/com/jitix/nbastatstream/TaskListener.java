package com.jitix.nbastatstream;

public interface TaskListener {
	void onTaskStarted();
	
	void onTaskFinished(BasketballGame result);

	void downloadedGames(String result);
	
	void downloadedBox(String result);
}
