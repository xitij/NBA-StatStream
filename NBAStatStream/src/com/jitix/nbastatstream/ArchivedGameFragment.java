package com.jitix.nbastatstream;

import com.jitix.nbastatstream.BasketballGame.AdvancedStatName;
import com.jitix.nbastatstream.BasketballGame.StatName;
import com.jitix.nbastatstream.NBAStatStream.TeamInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ArchivedGameFragment extends Fragment {

	private static final String TAG = "NBAStatStream";
	public static final String PAGE_NUM = "page_number";
	public static final String UPDATE_VIEWS = "update_views";
	static final int FOUR_FACTORS = 0;
	static final int BOX_SCORE = 1;
	static final int SHOT_CHART = 2;
	
	private float HomeScore;
	private float AwayScore;
	
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
			myView = inflater.inflate(R.layout.pager_archived_game_adv_box, container, false);
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
}