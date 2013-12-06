package com.jitix.nbastatstream;

import android.view.ViewGroup;

public interface BoxListener {

	void loadImages(BasketballGame myGame);
	
	void createAdvBoxScoreFrag(ViewGroup view, boolean home);
}
