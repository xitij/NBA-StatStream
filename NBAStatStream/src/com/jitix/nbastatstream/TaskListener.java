package com.jitix.nbastatstream;

public interface TaskListener {
	void downloadedGames(String result);
	
	void downloadedBox(String result);
	
	void loadImages(Event event, int viewId);
	
	void hideProgress();
}
