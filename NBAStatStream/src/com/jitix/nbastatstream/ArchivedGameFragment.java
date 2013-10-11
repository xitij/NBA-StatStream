package com.jitix.nbastatstream;

import java.util.Vector;
import com.jitix.nbastatstream.BasketballGame.AdvancedStatName;
import com.jitix.nbastatstream.BasketballGame.StatName;
import com.jitix.nbastatstream.NBAStatStream.TeamInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ArchivedGameFragment extends Fragment implements OnClickListener {

	private static final String TAG = "NBAStatStream";
	public static final String PAGE_NUM = "page_number";
	static final int FOUR_FACTORS = 0;
	static final int BOX_SCORE = 1;
	static final int SHOT_CHART = 2;
	
	Vector<Fragment> myAdvBoxFragments = new Vector<Fragment>();
	
	//
	// newInstance: Returns an instance of the ArchivedGameFragment. Takes i (page_num)
	// 	as argument. It creates a new instance if it doesn't exist otherwise returns
	// 	the reference to the instance.
	//
	public static ArchivedGameFragment newInstance(int i) {
		ArchivedGameFragment fragment = new ArchivedGameFragment();
		
		Log.d(TAG, "newInstance called for ArchivedGameFragment with i = " + i);
		
		// Put i in the arguments
		Bundle args = new Bundle();
		args.putInt(PAGE_NUM, i);
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle args = getArguments();
		View myView;
		Log.d(TAG, "onCreateView for page_num = " + args.getInt(PAGE_NUM));
		switch (args.getInt(PAGE_NUM)) {
		case FOUR_FACTORS:
			myView = inflater.inflate(R.layout.pager_archived_game_4factors, container, false);
			break;
		case BOX_SCORE:
			createAdvBoxScoreFrags();
			myView = inflater.inflate(R.layout.pager_archived_game_adv_box_frame, container, false);
			connectTeamButtons(myView);
			break;
		case SHOT_CHART:
			myView = inflater.inflate(R.layout.pager_archived_game_shotchart, container, false);
			break;
		default:
			myView = inflater.inflate(R.layout.pager_archived_game_4factors, container, false);
			break;
		}

		return myView;
	}
	
	@Override
	public void onClick(View v) {
		Fragment myFrag;
		switch (v.getId()) {
		case R.id.adv_box_away_team_button:
			Log.d(TAG, "clicked on Away Team Button");
			myFrag = getAdvBoxFrag(0);
			switchAdvBoxScoreFrag(myFrag);
			break;
		case R.id.adv_box_home_team_button:
			Log.d(TAG, "clicked on Home Team Button");
			myFrag = getAdvBoxFrag(1);
			switchAdvBoxScoreFrag(myFrag);
			break;
		default:
			// Do Nothing
			break;
		}
	}
	
	private void connectTeamButtons(View myView) {
		View away_button = myView.findViewById(R.id.adv_box_away_team_button);
		away_button.setOnClickListener(this);
		View home_button = myView.findViewById(R.id.adv_box_home_team_button);
		home_button.setOnClickListener(this);
	}
	
	private void createAdvBoxScoreFrags() {
		
		Log.d(TAG, "createAdvBoxScoreFrags");
		
		// Create AdvancedBoxScoreFragments for home and away teams
		AdvancedBoxScoreFragment AwayAdvBoxScoreFrag = AdvancedBoxScoreFragment.newInstance(0);
		AdvancedBoxScoreFragment HomeAdvBoxScoreFrag = AdvancedBoxScoreFragment.newInstance(1);
		// Add them both to the vector for storage
		myAdvBoxFragments.add(AwayAdvBoxScoreFrag);
		myAdvBoxFragments.add(HomeAdvBoxScoreFrag);
		
		// Get the ChildFragmentManager and add the AdvancedBoxScoreFragments to it
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.add(R.id.adv_box_frag_container, AwayAdvBoxScoreFrag, "AwayTeamTag").commit();
	}
	
	private void switchAdvBoxScoreFrag(Fragment myFrag) {
		Log.d(TAG, "Switch Advanced Box Score Fragment called");
		
		if(myFrag.isVisible()) {
			Log.d(TAG, "Fragment already visible so doing nothing...");
		} else {
			Log.d(TAG, "Replacing the old fragment");
			FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
			transaction.replace(R.id.adv_box_frag_container, myFrag);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}
	
	private Fragment getAdvBoxFrag(int index) { 
		try {
			return myAdvBoxFragments.get(index);
		} catch(Exception e) {
			Log.d(TAG, "Error getting Advanced Box Frag with index = " + index + ". Exception thrown, exception = " + e);
			return null;
		}
	}
	
	public void update4Factors(BasketballGame myGame) {
		// Update the view with the data in the BasketballGame object
		Bundle args = getArguments();
		Log.d(TAG, "inside update4Factors Page num = " + args.getInt(PAGE_NUM));
		
		// Look up the TeamInfo
		TeamInfo homeTeamInfo = NBAStatStream.NBATeamInfo.get(myGame.HomeTeam);
		TeamInfo awayTeamInfo = NBAStatStream.NBATeamInfo.get(myGame.AwayTeam);
		// Set the logos
		ImageView homelogo = (ImageView)getView().findViewById(R.id.home_logo);
		homelogo.setImageResource(homeTeamInfo.image_resource);
		ImageView awaylogo = (ImageView)getView().findViewById(R.id.away_logo);
		awaylogo.setImageResource(awayTeamInfo.image_resource);
		// Set the Scores
		TextView homeScore = (TextView) getView().findViewById(R.id.home_score);
		homeScore.setText(Float.toString(myGame.HomeTeamStats.get(StatName.POINTS)));
		TextView awayScore = (TextView) getView().findViewById(R.id.away_score);
		awayScore.setText(Float.toString(myGame.AwayTeamStats.get(StatName.POINTS)));
		// Set the team name abbreviations
		TextView homename = (TextView)getView().findViewById(R.id.home_team_name);
		homename.setText(homeTeamInfo.abbrev);
		TextView awayname = (TextView)getView().findViewById(R.id.away_team_name);
		awayname.setText(awayTeamInfo.abbrev);
		// Set the pace
		TextView pace = (TextView)getView().findViewById(R.id.game_pace);
		pace.setText(Float.toString(myGame.HomeTeamAdvStats.get(AdvancedStatName.PACE)));
		// Set the Efficiency
		TextView homeEff = (TextView)getView().findViewById(R.id.home_eff);
		homeEff.setText(Float.toString(myGame.HomeTeamAdvStats.get(AdvancedStatName.OFFEFF)));
		TextView awayEff = (TextView)getView().findViewById(R.id.away_eff);
		awayEff.setText(Float.toString(myGame.AwayTeamAdvStats.get(AdvancedStatName.OFFEFF)));
		// Set the eFG%
		TextView homeEFG = (TextView)getView().findViewById(R.id.home_efg);
		homeEFG.setText(Float.toString(myGame.HomeTeamAdvStats.get(AdvancedStatName.EFGPERCENT)));
		TextView awayEFG = (TextView)getView().findViewById(R.id.away_efg);
		awayEFG.setText(Float.toString(myGame.AwayTeamAdvStats.get(AdvancedStatName.EFGPERCENT)));
		// Set the FT/FG
		TextView homeFTFG = (TextView)getView().findViewById(R.id.home_ftfg);
		homeFTFG.setText(Float.toString(myGame.HomeTeamAdvStats.get(AdvancedStatName.FTFGA)));
		TextView awayFTFG = (TextView)getView().findViewById(R.id.away_ftfg);
		awayFTFG.setText(Float.toString(myGame.AwayTeamAdvStats.get(AdvancedStatName.FTFGA)));
		// Set the Offensive REB %
		TextView homeOREB = (TextView)getView().findViewById(R.id.home_oreb);
		homeOREB.setText(Float.toString(myGame.HomeTeamAdvStats.get(AdvancedStatName.OREBPERCENT)));
		TextView awayOREB = (TextView)getView().findViewById(R.id.away_oreb);
		awayOREB.setText(Float.toString(myGame.AwayTeamAdvStats.get(AdvancedStatName.OREBPERCENT)));
		// Set the Turnover %
		TextView homeTO = (TextView)getView().findViewById(R.id.home_tor);
		homeTO.setText(Float.toString(myGame.HomeTeamAdvStats.get(AdvancedStatName.TOPERCENT)));
		TextView awayTO = (TextView)getView().findViewById(R.id.away_tor);
		awayTO.setText(Float.toString(myGame.AwayTeamAdvStats.get(AdvancedStatName.TOPERCENT)));
	}

	public void updateAdvBox(BasketballGame myGame) {
		// Update the view with the data in the BasketballGame object
		Bundle args = getArguments();
		Log.d(TAG, "inside updateAdvBox Page num = " + args.getInt(PAGE_NUM));
		
		// Check the Away Team Box
		AdvancedBoxScoreFragment BoxFrag = (AdvancedBoxScoreFragment) getAdvBoxFrag(0);
		if(BoxFrag.isVisible()) {
			Log.d(TAG, "Away Box is visible updating it's view");
			BoxFrag.updateAdvBoxScoreView(myGame, false);
		} else {
			// Queue an update when it's visible
			BoxFrag.queueUpdate(myGame);
		}
		
		// Check the Home Team Box
		BoxFrag = (AdvancedBoxScoreFragment) getAdvBoxFrag(1);
		if(BoxFrag.isVisible()) {
			Log.d(TAG, "Home Box is visible updating it's view");
			BoxFrag.updateAdvBoxScoreView(myGame, true);
		} else {
			// Queue an update when it's visible
			BoxFrag.queueUpdate(myGame);
		}
	}
	
	public void updateShotChart(BasketballGame myGame) {
		// Update the view with the data in the BasketballGame object
		Bundle args = getArguments();
		Log.d(TAG, "inside updateShotChart Page num = " + args.getInt(PAGE_NUM));
		
		TextView tmpText = (TextView)getView().findViewById(R.id.shotchart_test);
		tmpText.setText("Updating this page");
	}
}