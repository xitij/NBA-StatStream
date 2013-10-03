package com.jitix.nbastatstream;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.jitix.nbastatstream.ArchivedGameDialog.EditNameDialogListener;

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
public class ArchivedGame extends FragmentActivity implements OnClickListener, EditNameDialogListener, TaskListener {

	private static final String TAG = "NBAStatStream";
	
	//
	// BasketballGame object to hold the important data
	//
	private static BasketballGame myGame;
	
	//
	// TextView and String for the BoxScore URL
	//
	private TextView boxscore_text;
	private String boxscore_string;
	
	//
	// Pager Adapter and ViewPager to manage the tabs
	//
	private ArchivedGamePagerAdapter archivedGamePagerAdapter;
	private static ViewPager viewPager;
	
	//
	// DialogFragment for the box score to be entered
	//
	ArchivedGameDialog archived_game_dialog = new ArchivedGameDialog();

	//
	// onCreate: Sets the view, show the Dialog, tie up views and buttons.
	//	Also sets up the ViewPage and PageAdapter, and creates and setup ActionBar.
	//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_archived_game);
		Log.d(TAG, "ArchivedGame onCreate");

		///////////////////////////////////////////////////////////////////////////////////////
		// Setup and initialization goes here...
		///////////////////////////////////////////////////////////////////////////////////////
		// Create an AlertDialog for entering box score
		archived_game_dialog.show(getFragmentManager(), "archived_game_dialog");

		// Tie up the TextView and Button
		boxscore_text = (TextView) findViewById(R.id.boxscore_url);
		boxscore_text.setText(R.string.empty_box_score);
		View edit_button = findViewById(R.id.edit_box_score_button);
		edit_button.setOnClickListener(this);
		
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
							: (i == 1) ? R.string.pager_advbox_title : R.string.pager_shotchart_title));

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
	// onClick: Opens the Dialog if the user clicked on the edit button.
	//
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edit_box_score_button:
			Bundle args = new Bundle();
			if(boxscore_string != null) {
				if(!boxscore_string.isEmpty()) {
					Log.d(TAG, "onClick edit: boxscore_string = " + boxscore_string);
					args.putString("saved_box_score_address", boxscore_string);
				}
			}
			archived_game_dialog.setArguments(args);
			archived_game_dialog.show(getFragmentManager(), "archived_game_dialog");
			break;
		}
	}

	//
	// onFinishedEditDialog: called when the user has finished editing the box
	//		score string. BoxScoreDownloader is called to attempt to connect
	//		and parse.
	//
	@Override
	public void onFinishedEditDialog(String inputText) {
		boxscore_string = inputText;
		Log.d(TAG, "onFinishedEditDialog, boxscore = " + boxscore_string);
		boxscore_text.setText(boxscore_string);
		
		// Pass the string as a URL to the BoxScoreDownloader Task to connect and parse...
		Log.d(TAG, "setting the Parser URL with boxscore_string = " + boxscore_string);
		// Launch the AsyncTask
		new BoxScoreDownloader(this).execute(boxscore_string);
	}

	@Override
	public void onTaskStarted() {
		// Might not need this
		// Place holder for now...
	}

	//
	// onTaskFinished: called after the Box Score Downloader has connected
	// 		to the game box score, and parsed and populated the game data into the 
	// 		BasketballGame object. It updated the PagerAdapter with the correct stats
	// 		and information for the game.
	//
	@Override
	public void onTaskFinished(BasketballGame result) {
		Log.d(TAG, "listened onTaskFinished called");
		
		myGame = result;
		
		ArchivedGameFragment newfrag = (ArchivedGameFragment) archivedGamePagerAdapter.getFrag(0);
		newfrag.update4Factors(this.myGame);
	}
}
