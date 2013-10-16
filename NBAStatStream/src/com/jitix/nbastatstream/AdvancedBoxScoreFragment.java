package com.jitix.nbastatstream;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.jitix.nbastatstream.BasketballGame.BoxScoreLine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class AdvancedBoxScoreFragment extends Fragment {
	
	private static final String TAG = "NBAStatStream";
	private static final String BOX_UPDATE = "BOX_UPDATE";
	public static final String TEAM_NUM = "team_number";
	static final int AWAY_TEAM = 0;
	static final int HOME_TEAM = 1;
	
	private int box_update;
	private BasketballGame savedGame;
	
	//
	// newInstance: Returns an instance of the ArchivedGameFragment. Takes i (page_num)
	// 	as argument. It creates a new instance if it doesn't exist otherwise returns
	// 	the reference to the instance.
	//
	public static AdvancedBoxScoreFragment newInstance(int i) {
		AdvancedBoxScoreFragment fragment = new AdvancedBoxScoreFragment();
		
		Log.d(TAG, "newInstance called for AdvancedBoxScoreFragment with i = " + i);
		
		// Put i in the arguments
		Bundle args = new Bundle();
		args.putInt(TEAM_NUM, i);
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate called for AdvancedBoxScoreFragment");
		box_update = 0;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Bundle args = getArguments();
		View myView;
		Log.d(TAG, "onCreateView for team_num = " + args.getInt(TEAM_NUM));
		Log.d(TAG, "onCreateView for box_update = " + args.getInt(BOX_UPDATE, -1));
		myView = inflater.inflate(R.layout.pager_archived_game_adv_box, container, false);
		
		if(args.getInt(BOX_UPDATE, -1) == 1) {
			Log.d(TAG, "found box_update = " + args.getInt(BOX_UPDATE));
			
			// Check if it's a Away or Home Box
			if(args.getInt(TEAM_NUM, -1) == 0) {
				updateAdvBoxScoreView(savedGame, false, myView);
			} else if(args.getInt(TEAM_NUM, -1) == 1) {
				updateAdvBoxScoreView(savedGame, true, myView);
			}
		}

		return myView;
	}
	
	@Override
	public void onStop() {
		super.onStop();
		// Save the update flag in the bundle
		Bundle args = getArguments();
		args.putInt(BOX_UPDATE, box_update);
		Log.d(TAG, "onStop saving box_updated = " + box_update);
	}

	public void queueUpdate(BasketballGame myGame) {
		savedGame = myGame;
		box_update = 1;
		Bundle args = getArguments();
		args.putInt(BOX_UPDATE, box_update);
		Log.d(TAG, "queueUpdate saving box_updated = " + box_update + ", for team_num = " + args.getInt(TEAM_NUM));
	}
	
	public void updateAdvBoxScoreView(BasketballGame myGame, boolean home) {
		updateAdvBoxScoreView(myGame, home, getView());
	}

	public void updateAdvBoxScoreView(BasketballGame myGame, boolean home, View myView) {
		// Set the box updated flag and save the BasketballGame
		box_update = 1;
		savedGame = myGame;
		
		Log.d(TAG, "updateAdvBoxScoreView with home = " + home);
		
		// Get the screen scaling so we can convert px to dp
		final float scale = getActivity().getBaseContext().getResources().getDisplayMetrics().density;
		
		// Get the tables
		TableLayout advBoxTablePlayers = (TableLayout)myView.findViewById(R.id.adv_box_table_players);
		TableLayout advBoxTableStats = (TableLayout)myView.findViewById(R.id.adv_box_table_stats);
		
		// Add the Team Name and Position titles to the first row
		TextView titleTeam = (TextView)myView.findViewById(R.id.adv_box_table_players_title_team);
		
		// Set the Team Name and get the Proper iterator for the AdvancedBoxScore
		Iterator<Map.Entry<String, BoxScoreLine>> it;
		if(home == true) {
			titleTeam.setText(myGame.HomeTeam);
			it = myGame.HomeTeamBox.entrySet().iterator();
		} else {
			titleTeam.setText(myGame.AwayTeam);
			it = myGame.AwayTeamBox.entrySet().iterator();
		}
		
		while(it.hasNext()) {
			Entry<String, BoxScoreLine> pair = it.next();
			String name = pair.getKey();
			BasketballGame.BoxScoreLine box = pair.getValue();
			
			// Only create a row for the player if he actually played
			if(box.Minutes != 0) {
				// Get the advanced box score line
				BasketballGame.AdvancedBoxScoreLine  advbox;
				if(home == true) {
					advbox = myGame.HomeTeamAdvBox.get(name);
				} else {
					advbox = myGame.AwayTeamAdvBox.get(name);
				}
				
				// Create a TableRow and TextView for the Player side of the Table
				TableRow.LayoutParams nameParams = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, .85f);
				int pixels = (int) (1 * scale + 0.5f);
				nameParams.setMargins(pixels, pixels, pixels, pixels);
				TableRow.LayoutParams posParams = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, .15f);
				posParams.setMargins(pixels, pixels, pixels, pixels);
				TableRow playerRow = new TableRow(getActivity());
				TextView playerName = new TextView(getActivity());
				TextView playerPos = new TextView(getActivity());
				
				// Player Name Text
				playerName.setText(name);
				playerName.setLayoutParams(nameParams);
				playerName.setBackgroundColor(getResources().getColor(R.color.WHITE));
				pixels = (int) (5 * scale + 0.5f);
				playerName.setPadding(pixels, pixels, pixels, pixels);
				// Player Position Text
				playerPos.setText(box.Position);
				playerPos.setLayoutParams(posParams);
				playerPos.setBackgroundColor(getResources().getColor(R.color.WHITE));
				playerPos.setPadding(pixels, pixels, pixels, pixels);
				
				// Add the text to the row
				playerRow.addView(playerName);
				playerRow.addView(playerPos);
				advBoxTablePlayers.addView(playerRow);
				
				// Create a TableRow and TextViews for Stats side of the Table
				TableRow.LayoutParams statsParam = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
				pixels = (int) (1 * scale + 0.5f);
				statsParam.setMargins(pixels, pixels, pixels, pixels);
				TableRow statsRow = new TableRow(getActivity());
				TextView mins = new TextView(getActivity());
				TextView tsp = new TextView(getActivity());
				TextView efg = new TextView(getActivity());
				TextView orbp = new TextView(getActivity());
				TextView drbp = new TextView(getActivity());
				TextView trbp = new TextView(getActivity());
				TextView astp = new TextView(getActivity());
				TextView stlp = new TextView(getActivity());
				TextView blkp = new TextView(getActivity());
				TextView tovp = new TextView(getActivity());
				TextView usage = new TextView(getActivity());
				TextView offrating = new TextView(getActivity());
				TextView defrating = new TextView(getActivity());
				
				//
				// Set the text for the Stats
				//
				pixels = (int) (5 * scale + 0.5f);
				// Mins
				mins.setText(Integer.toString(box.Minutes));
				mins.setLayoutParams(statsParam);
				mins.setBackgroundColor(getResources().getColor(R.color.WHITE));
				mins.setPadding(pixels, pixels, pixels, pixels);
				// TS%
				tsp.setText(Float.toString(advbox.TrueShootingPercent));
				tsp.setLayoutParams(statsParam);
				tsp.setBackgroundColor(getResources().getColor(R.color.WHITE));
				tsp.setPadding(pixels, pixels, pixels, pixels);
				//tsp.setPadding(5, 5, 5, 5);
				// eFG%
				efg.setText(Float.toString(advbox.EFGPercent));
				efg.setLayoutParams(statsParam);
				efg.setBackgroundColor(getResources().getColor(R.color.WHITE));
				efg.setPadding(pixels, pixels, pixels, pixels);
				// OFF REB%
				orbp.setText(Float.toString(advbox.ORebPercent));
				orbp.setLayoutParams(statsParam);
				orbp.setBackgroundColor(getResources().getColor(R.color.WHITE));
				orbp.setPadding(pixels, pixels, pixels, pixels);
				// DEF REB%
				drbp.setText(Float.toString(advbox.DRebPercent));
				drbp.setLayoutParams(statsParam);
				drbp.setBackgroundColor(getResources().getColor(R.color.WHITE));
				drbp.setPadding(pixels, pixels, pixels, pixels);
				// Total REB%
				trbp.setText(Float.toString(advbox.TotRebPercent));
				trbp.setLayoutParams(statsParam);
				trbp.setBackgroundColor(getResources().getColor(R.color.WHITE));
				trbp.setPadding(pixels, pixels, pixels, pixels);
				// Assist%
				astp.setText(Float.toString(advbox.AssistPercent));
				astp.setLayoutParams(statsParam);
				astp.setBackgroundColor(getResources().getColor(R.color.WHITE));
				astp.setPadding(pixels, pixels, pixels, pixels);
				// Steal%
				stlp.setText(Float.toString(advbox.StealPercent));
				stlp.setLayoutParams(statsParam);
				stlp.setBackgroundColor(getResources().getColor(R.color.WHITE));
				stlp.setPadding(pixels, pixels, pixels, pixels);
				// Block%
				blkp.setText(Float.toString(advbox.BlockPercent));
				blkp.setLayoutParams(statsParam);
				blkp.setBackgroundColor(getResources().getColor(R.color.WHITE));
				blkp.setPadding(pixels, pixels, pixels, pixels);
				// Turnover%
				tovp.setText(Float.toString(advbox.TOPercent));
				tovp.setLayoutParams(statsParam);
				tovp.setBackgroundColor(getResources().getColor(R.color.WHITE));
				tovp.setPadding(pixels, pixels, pixels, pixels);
				// Usage
				usage.setText(Float.toString(advbox.Usage));
				usage.setLayoutParams(statsParam);
				usage.setBackgroundColor(getResources().getColor(R.color.WHITE));
				usage.setPadding(pixels, pixels, pixels, pixels);
				// Offensive Rating
				offrating.setText(Float.toString(advbox.OffRating));
				offrating.setLayoutParams(statsParam);
				offrating.setBackgroundColor(getResources().getColor(R.color.WHITE));
				offrating.setPadding(pixels, pixels, pixels, pixels);
				// Defensive Rating
				defrating.setText(Float.toString(advbox.DefRating));
				defrating.setLayoutParams(statsParam);
				defrating.setBackgroundColor(getResources().getColor(R.color.WHITE));
				defrating.setPadding(pixels, pixels, pixels, pixels);
				
				// Add the text to the row
				statsRow.addView(mins);
				statsRow.addView(tsp);
				statsRow.addView(efg);
				statsRow.addView(orbp);
				statsRow.addView(drbp);
				statsRow.addView(trbp);
				statsRow.addView(astp);
				statsRow.addView(stlp);
				statsRow.addView(blkp);
				statsRow.addView(tovp);
				statsRow.addView(usage);
				statsRow.addView(offrating);
				statsRow.addView(defrating);
				advBoxTableStats.addView(statsRow);
			}
		}
	}
}