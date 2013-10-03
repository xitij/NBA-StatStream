package com.jitix.nbastatstream;

public interface TaskListener {
	void onTaskStarted();
	
	void onTaskFinished(BasketballGame result);
}
