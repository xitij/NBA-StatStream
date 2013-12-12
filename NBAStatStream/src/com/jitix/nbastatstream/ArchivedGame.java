package com.jitix.nbastatstream;

import java.io.IOException;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ArchivedGame:
 * 	Is the class that handles NBA games that are already completed. This
 *	class takes a URL that contains a box score, as a string. It then uses
 *	an AsyncTask, BoxScoreDownloader, to attempt to a http connection. Once 
 *	a connection is established it users the BoxScoreParser class to parse
 *	the data into a BasketballGame class that holds all the interesting 
 *	information for the game. BasketballGame data is then used to populate
 *	the fragments that make up the Activity. 
 */
public class ArchivedGame extends FragmentActivity implements TaskListener {

	private static final String TAG = "NBAStatStream";
	public static final String AWAY_TEAM = "com.jitix.nbastatstream.away_team";
	public static final String HOME_TEAM = "com.jitix.nbastatstream.home_team";
	public static final String BOX_ID = "com.jitix.nbastatstream.box_id";
	
	//
	// BasketballGame object to hold the important data
	//
	private static BasketballGame myGame;
	
	//
	// Progress Bar
	//
	private static ProgressBar progress;
	
	//
	// Pager Adapter and ViewPager to manage the tabs
	//
	private ArchivedGamePagerAdapter archivedGamePagerAdapter;
	private static ViewPager viewPager;
	
	//
	// onCreate: Sets the view, show the Dialog, tie up views and buttons.
	//	Also sets up the ViewPage and PageAdapter, and creates and setup ActionBar.
	//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_archived_game);
		Log.d(TAG, "ArchivedGame onCreate");
		
		// Get the Progress Bar
		progress = (ProgressBar) findViewById(R.id.game_progress_bar);
		// Get the request strings
		String awayTeam = getIntent().getStringExtra(AWAY_TEAM);
		String homeTeam = getIntent().getStringExtra(HOME_TEAM);
		String box_id = getIntent().getStringExtra(BOX_ID);
		//Integer year = Integer.parseInt(date.substring(0, 4));
		//Integer month = Integer.parseInt(date.substring(4, 6));
		//Integer day = Integer.parseInt(date.substring(6, 8));
		
		//Log.d(TAG, "ArchivedGame received : Away Team = " + awayTeam + ", Home Team = " + homeTeam + ", DATE = " + date);
		Log.d(TAG, "ArchivedGame received : Box ID = " + box_id);
		
		// Start the GameDownloader for the selected box score
		progress.setVisibility(View.VISIBLE);
		new GameDownloader(this).execute(box_id);

		///////////////////////////////////////////////////////////////////////////////////////
		// Setup and initialization goes here...
		///////////////////////////////////////////////////////////////////////////////////////
		// Set up the ViewPager and PagerAdapter
		archivedGamePagerAdapter = new ArchivedGamePagerAdapter(getSupportFragmentManager());
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(archivedGamePagerAdapter);
		viewPager.setOffscreenPageLimit(2); // ArchivedGamePagerAdapter.size() - 1;
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// When swiping between pages, select the corresponding tab.
				getActionBar().setSelectedNavigationItem(position);
			}

		});

		// Set up the ActionBar tabs
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		//actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		ActionBar.TabListener tablistener = new ActionBar.TabListener() {

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				Log.d(TAG, "onTabSelected tab.getPosition = " + tab.getPosition());
				viewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}
		};
		// Add 3 tabs
		for(int i=0; i<3; i++) {
			Log.d(TAG, "adding tabs i = " + i);
			actionBar.addTab(actionBar.newTab().setTabListener(tablistener)
					.setText(i == 0  ? R.string.pager_4factors_title
							: (i == 1) ? R.string.pager_box_title : R.string.pager_advbox_title));

		}
	}

	//
	// onStart
	//
	@Override
	protected void onStart() {
		super.onStart();
	}

	//
	// downloadedGames: Stub placeholder function for the TaskListener interface.
	//		Will never be called for an Game
	//
	@Override
	public void downloadedGames(String result) {
		// TODO Auto-generated method stub
	}

	@Override
	public void downloadedBox(String result) {
		Log.d(TAG, "ArchivedGame : downloadedBox called");
		// Handle the error case
		if(result == null) {
			// TODO
		} else {
			try {
				// Map the Box Score into our POJO
				ObjectMapper mapper = new ObjectMapper();
				myGame = mapper.readValue(result, BasketballGame.class);
				
				// Populate and calculate the stats for the game
				myGame.populateGame();
				
				//
				// Update all the Fragments and Views
				//
				ArchivedGameFragment fourFactorfrag = (ArchivedGameFragment) archivedGamePagerAdapter.getFrag(0);
				fourFactorfrag.update4Factors(this.myGame);
				ArchivedGameFragment boxfrag = (ArchivedGameFragment) archivedGamePagerAdapter.getFrag(1);
				boxfrag.updateBox(this.myGame);
				ArchivedGameFragment advBoxfrag = (ArchivedGameFragment) archivedGamePagerAdapter.getFrag(2);
				advBoxfrag.updateAdvBox(this.myGame);
				//ArchivedGameFragment shotfrag = (ArchivedGameFragment) archivedGamePagerAdapter.getFrag(2);
				//shotfrag.updateShotChart(this.myGame);
				
			} catch(IOException e) {
				Log.d(TAG, "downloadedBox : IOException : " + e.getMessage());
			}
		}
	}

	@Override
	public void loadImages(Event event, int viewId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hideProgress() {
		// Remove the loading bar
		progress.setVisibility(View.GONE);
	}
}
