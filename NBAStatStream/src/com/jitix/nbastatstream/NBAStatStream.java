package com.jitix.nbastatstream;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class NBAStatStream extends Activity implements OnClickListener {

	private static final String TAG = "NBAStatStream";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nbastat_stream);
		
		// Set up click listeners for the buttons
		View livegame = findViewById(R.id.live_game_button);
		livegame.setOnClickListener(this);
		View archivedgame = findViewById(R.id.archived_game_button);
		archivedgame.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nbastat_stream, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.live_game_button:
			startLiveGame();
			break;
		case R.id.archived_game_button:
			startArchivedGame();
			break;
		default:
			break;
		}
	}
	
	private void startLiveGame() {
		Log.d(TAG, "Clicked on live game.");
		Intent intent = new Intent(this, LiveGame.class);
		startActivity(intent);
	}
	
	private void startArchivedGame() {
		Log.d(TAG, "Clicked on archived game.");
		Intent intent = new Intent(this, ArchivedGame.class);
		startActivity(intent);
	}

}
