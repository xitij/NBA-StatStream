package com.jitix.nbastatstream;

import java.util.Vector;
import com.jitix.nbastatstream.BasketballGame.AdvancedStatName;
import com.jitix.nbastatstream.BasketballGame.StatName;
import com.jitix.nbastatstream.NBAStatStream.TeamInfo;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ArchivedGameFragment extends Fragment implements OnClickListener, BoxListener {

	private static final String TAG = "NBAStatStream";
	public static final String PAGE_NUM = "page_number";
	static final int FOUR_FACTORS = 0;
	static final int BOX_SCORE = 1;
	static final int ADV_BOX_SCORE = 2;
	static final int SHOT_CHART = 3;
	private boolean destroyed = false;
	
	Vector<Fragment> myAdvBoxFragments = new Vector<Fragment>();
	Vector<ViewGroup> myAdvBoxViews = new Vector<ViewGroup>();
	Vector<Fragment> myBoxFragments = new Vector<Fragment>();
	Vector<ViewGroup> myBoxViews = new Vector<ViewGroup>();
	
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
		Log.d(TAG, "ArchivedGameFragment onCreateView for page_num = " + args.getInt(PAGE_NUM));
		switch (args.getInt(PAGE_NUM)) {
		case FOUR_FACTORS:
			myView = inflater.inflate(R.layout.pager_archived_game_4factors, container, false);
			break;
		case BOX_SCORE:
			myView = inflater.inflate(R.layout.pager_archived_game_box_frame, container, false);
			break;
		case ADV_BOX_SCORE:
			myView = inflater.inflate(R.layout.pager_archived_game_adv_box_frame, container, false);
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
	public void onPause() {
		super.onPause();
		Log.d(TAG, "ArchivedGameFragment onPause() called!");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "ArchivedGameFragment onStop() called!");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "ArchivedGameFragment onDestroy() called!");
		destroyed = true;
	}

	@Override
	public void onClick(View v) {
		Fragment myFrag;
		switch (v.getId()) {
		case R.id.box_away_team_button:
			Log.d(TAG, "clicked on Away Team Box Button");
			myFrag = getBoxFrag(0);
			switchBoxScoreFrag(myFrag);
			toggleSelected(v);
			break;
		case R.id.box_home_team_button:
			Log.d(TAG, "clicked on Home Team Box Button");
			myFrag = getBoxFrag(1);
			switchBoxScoreFrag(myFrag);
			toggleSelected(v);
			break;
		case R.id.adv_box_away_team_button:
			Log.d(TAG, "clicked on Away Team Advanced Box Button");
			myFrag = getAdvBoxFrag(0);
			switchAdvBoxScoreFrag(myFrag);
			toggleSelected(v);
			break;
		case R.id.adv_box_home_team_button:
			Log.d(TAG, "clicked on Home Team Advanced Box Button");
			myFrag = getAdvBoxFrag(1);
			switchAdvBoxScoreFrag(myFrag);
			toggleSelected(v);
			break;
		default:
			// Do Nothing
			break;
		}
	}
	
	private void toggleSelected(View v) {
		View otherButton;
		switch(v.getId()) {
		case R.id.box_away_team_button:
			v.setSelected(true);
			otherButton = getView().findViewById(R.id.box_home_team_button);
			otherButton.setSelected(false);
			break;
		case R.id.box_home_team_button:
			v.setSelected(true);
			otherButton = getView().findViewById(R.id.box_away_team_button);
			otherButton.setSelected(false);
			break;
		case R.id.adv_box_away_team_button:
			v.setSelected(true);
			otherButton = getView().findViewById(R.id.adv_box_home_team_button);
			otherButton.setSelected(false);
			break;
		case R.id.adv_box_home_team_button:
			v.setSelected(true);
			otherButton = getView().findViewById(R.id.adv_box_away_team_button);
			otherButton.setSelected(false);
			break;
		default:
			// Do Nothing
			break;
		}
	}
	
	private void connectTeamButtons(View myView, boolean adv) {
		
		// Advanced or Box Buttons
		View away_button;
		View home_button;
		if(adv) {
			away_button = myView.findViewById(R.id.adv_box_away_team_button);
			home_button = myView.findViewById(R.id.adv_box_home_team_button);
		} else {
			away_button = myView.findViewById(R.id.box_away_team_button);
			home_button = myView.findViewById(R.id.box_home_team_button);
		}
		away_button.setOnClickListener(this);
		home_button.setOnClickListener(this);
	}
	
	@Override
	public void createAdvBoxScoreFrag(ViewGroup view, boolean home) {
		
		Log.d(TAG, "createAdvBoxScoreFrag : home == " + home);
		
		// Create AdvancedBoxScoreFragments for home and away teams
		AdvancedBoxScoreFragment AdvBoxScoreFrag;
		if(home) {
			AdvBoxScoreFrag = AdvancedBoxScoreFragment.newInstance(1, true);
		} else {
			AdvBoxScoreFrag = AdvancedBoxScoreFragment.newInstance(0, true);
		}
		
		// Add the View and the Fragment to the vectors for storage
		myAdvBoxViews.add(view);
		myAdvBoxFragments.add(AdvBoxScoreFrag);
	}
	
	@Override
	public void createBoxScoreFrag(ViewGroup view, boolean home) {
		
		Log.d(TAG, "createBoxScoreFrag : home == " + home);
		
		// Create BoxScoreFragments for home and away teams
		AdvancedBoxScoreFragment BoxScoreFrag;
		if(home) {
			BoxScoreFrag = AdvancedBoxScoreFragment.newInstance(1, false);
		} else {
			BoxScoreFrag = AdvancedBoxScoreFragment.newInstance(0, false);
		}
		
		// Add the View and the Fragment to the vectors for storage
		myBoxViews.add(view);
		myBoxFragments.add(BoxScoreFrag);
	}
	
	private void loadAdvBoxFrag(boolean home) {
		
		Log.d(TAG, "loadAdvBoxFrag for home = " + home);
		AdvancedBoxScoreFragment AdvBoxScoreFrag = (AdvancedBoxScoreFragment) getAdvBoxFrag(home == false ? 0 : 1);
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		String fragTag = (home == false) ? "AwayTeamAdvBoxTag" : "HomeTeamAdvBoxTag";
		transaction.add(R.id.adv_box_frag_container, AdvBoxScoreFrag, fragTag).commit();
		getChildFragmentManager().executePendingTransactions();
		//Log.d(TAG, "After AdvBoxScoreFrag FT added");
		//Log.d(TAG, "After AdvBoxScoreFrag FT added isShown = " + AdvBoxScoreFrag.getView().isShown() + ", id = " + AdvBoxScoreFrag.getView().getId() + ", childID = " + ((ViewGroup) AdvBoxScoreFrag.getView()).getChildAt(0));
	}
	
	private void loadBoxFrag(boolean home) {

		Log.d(TAG, "loadBoxFrag for home = " + home);
		AdvancedBoxScoreFragment BoxScoreFrag = (AdvancedBoxScoreFragment) getBoxFrag(home == false ? 0 : 1);
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		String fragTag = (home == false) ? "AwayTeamBoxTag" : "HomeTeamBoxTag";
		transaction.add(R.id.box_frag_container, BoxScoreFrag, fragTag).commit();
		getChildFragmentManager().executePendingTransactions();
		//Log.d(TAG, "After AdvBoxScoreFrag FT added");
		//Log.d(TAG, "After AdvBoxScoreFrag FT added isShown = " + AdvBoxScoreFrag.getView().isShown() + ", id = " + AdvBoxScoreFrag.getView().getId() + ", childID = " + ((ViewGroup) AdvBoxScoreFrag.getView()).getChildAt(0));
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
			getChildFragmentManager().executePendingTransactions();
		}
	}
	
	private void switchBoxScoreFrag(Fragment myFrag) {
		Log.d(TAG, "Switch Box Score Fragment called");
		
		if(myFrag.isVisible()) {
			Log.d(TAG, "Fragment already visible so doing nothing...");
		} else {
			Log.d(TAG, "Replacing the old fragment");
			FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
			transaction.replace(R.id.box_frag_container, myFrag);
			transaction.addToBackStack(null);
			transaction.commit();
			getChildFragmentManager().executePendingTransactions();
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
	
	ViewGroup getAdvBoxView(int index) {
		try {
			Log.d(TAG, "getAdvBoxView returning ViewGroup for index = " + index);
			return myAdvBoxViews.get(index);
		} catch(Exception e) {
			Log.d(TAG, "Error getting Advanced Box View with index = " + index + ". Exception thrown, exception = " + e);
			return null;
		}
	}
	
	private Fragment getBoxFrag(int index) { 
		try {
			return myBoxFragments.get(index);
		} catch(Exception e) {
			Log.d(TAG, "Error getting Box Frag with index = " + index + ". Exception thrown, exception = " + e);
			return null;
		}
	}
	
	ViewGroup getBoxView(int index) {
		try {
			Log.d(TAG, "getBoxView returning ViewGroup for index = " + index);
			return myBoxViews.get(index);
		} catch(Exception e) {
			Log.d(TAG, "Error getting Box View with index = " + index + ". Exception thrown, exception = " + e);
			return null;
		}
	}
	
	@Override
	public void loadImages(BasketballGame myGame) {
		
		Bundle args = getArguments();
		int page_num = args.getInt(PAGE_NUM);
		
		if(!destroyed) {
			Log.d(TAG, "ArchivedGameFragment : loadImages : Calling the update functions!");
			if(page_num == FOUR_FACTORS) {
				load4FactorsImages(myGame);
			} else if(page_num == ADV_BOX_SCORE) {
				loadBoxImages(myGame, true);
				loadAdvBoxFrag(false);
				connectTeamButtons(getView(), true);
				updateButtonInfo(null, true);
			} else if(page_num == BOX_SCORE) {
				loadBoxImages(myGame, false);
				loadBoxFrag(false);
				connectTeamButtons(getView(), false);
				updateButtonInfo(null, false);
			}
		} else {
			Log.d(TAG, "ArchivedGameFragment has been destroyed, skipping update functions!");
		}
	}
	
	public void update4Factors(BasketballGame myGame) {
		
		//GameFragmentUpdateTask updateTask = new GameFragmentUpdateTask(this.getActivity(), this, myGame, (ViewGroup) getView(), progress);
		GameFragmentUpdateTask updateTask = new GameFragmentUpdateTask(this.getActivity(), this, myGame, (ViewGroup) getView().findViewById(R.id.pager_4factors_layout));
		Bundle args = getArguments();
		//updateTask.execute(args.getInt(PAGE_NUM));
		updateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args.getInt(PAGE_NUM));
	}
	
	public void updateBox(BasketballGame myGame) {
		
		// Update the view with the data in the BasketballGame object
		GameFragmentUpdateTask updateTask = new GameFragmentUpdateTask(this.getActivity(), this, myGame, (ViewGroup) getView().findViewById(R.id.pager_box_layout));
		Bundle args = getArguments();
		Log.d(TAG, "inside updateBox Page num = " + args.getInt(PAGE_NUM));
		//updateTask.execute(args.getInt(PAGE_NUM));
		updateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args.getInt(PAGE_NUM));
	}

	public void updateAdvBox(BasketballGame myGame) {

		// Update the view with the data in the BasketballGame object
		GameFragmentUpdateTask updateTask = new GameFragmentUpdateTask(this.getActivity(), this, myGame, (ViewGroup) getView().findViewById(R.id.pager_advbox_layout));
		Bundle args = getArguments();
		Log.d(TAG, "inside updateAdvBox Page num = " + args.getInt(PAGE_NUM));
		//updateTask.execute(args.getInt(PAGE_NUM));
		updateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args.getInt(PAGE_NUM));
	}
	
	public void updateShotChart(BasketballGame myGame) {
		// Update the view with the data in the BasketballGame object
		Bundle args = getArguments();
		Log.d(TAG, "inside updateShotChart Page num = " + args.getInt(PAGE_NUM));
		
		TextView tmpText = (TextView)getView().findViewById(R.id.shotchart_test);
		tmpText.setText("Updating this page");
	}
	
	
	//
	// Function used by the AdvancedBoxScore Page to update the button text, color, image
	//
	private void updateButtonInfo(BasketballGame myGame, boolean adv) {
		// Set the Away Team Button
		RelativeLayout awayButton;
		if(adv) {
			awayButton = (RelativeLayout) getView().findViewById(R.id.adv_box_away_team_button); 
		} else {
			awayButton = (RelativeLayout) getView().findViewById(R.id.box_away_team_button);
		}
		// Set the StateList
		StateListDrawable awayList = new StateListDrawable();
		awayList.addState(new int[] { android.R.attr.state_selected }, new ColorDrawable(getResources().getColor(R.color.GRAY)));
		awayButton.setBackground(awayList);
		awayButton.setSelected(true);
		
		// Set the Home Team Button
		RelativeLayout homeButton;
		if(adv) {
			homeButton = (RelativeLayout) getView().findViewById(R.id.adv_box_home_team_button);
		} else {
			homeButton = (RelativeLayout) getView().findViewById(R.id.box_home_team_button);
		}
		// Set the StateList
		StateListDrawable homeList = new StateListDrawable();
		homeList.addState(new int[] { android.R.attr.state_selected }, new ColorDrawable(getResources().getColor(R.color.GRAY)));
		homeButton.setBackground(homeList);
	}
	
	private void load4FactorsImages(BasketballGame myGame) {
		// Set the Image size
		int pixels = NBAStatStream.dpToPx(120.0f);

		// Get the Away ImageView and resource ID
		ImageView awayImageView = (ImageView) getView().findViewById(R.id.four_factors_away_logo);
		int awayLogo = NBAStatStream.getTeamLogo(myGame.AwayTeam.getFullName());
		loadBitmap(awayLogo, awayImageView, pixels, pixels, false);

		// Get the Home ImageView and resource ID
		ImageView homeImageView = (ImageView) getView().findViewById(R.id.four_factors_home_logo);
		int homeLogo = NBAStatStream.getTeamLogo(myGame.HomeTeam.getFullName());
		loadBitmap(homeLogo, homeImageView, pixels, pixels, false);
	}
	
	private void loadBoxImages(BasketballGame myGame, boolean adv) {
		
		Log.d(TAG, "inside loadBoxImages...for PAGE_NUM = " + getArguments().getInt(PAGE_NUM));
		//Log.d(TAG, "ArchivedGameFragment.isVisible() = " + this.isVisible());
		//Log.d(TAG, "Parent Activity = " + this.getActivity());
		//if(this.getActivity() != null) {
		//	Log.d(TAG, "Parent Activity isDestroyed() = " + this.getActivity().isDestroyed());
		//}
		
		int pixels = NBAStatStream.dpToPx(50.0f);
		ImageView homeImageView;
		ImageView awayImageView;
		
		if(adv) {
			awayImageView = (ImageView) getView().findViewById(R.id.adv_box_away_team_logo);
			homeImageView = (ImageView) getView().findViewById(R.id.adv_box_home_team_logo);
		} else {
			awayImageView = (ImageView) getView().findViewById(R.id.box_away_team_logo);
			homeImageView = (ImageView) getView().findViewById(R.id.box_home_team_logo);
		}
		
		// Get the Away Button ImageView and resource ID
		TeamInfo away = NBAStatStream.NBATeamInfo.get(myGame.AwayTeam.getFullName());
		int awayLogo = away.image_resource;
		loadBitmap(awayLogo, awayImageView, pixels, pixels, false);
		
		// Get the Home Button ImageView and resource ID
		TeamInfo home = NBAStatStream.NBATeamInfo.get(myGame.HomeTeam.getFullName());
		int homeLogo = home.image_resource;
		loadBitmap(homeLogo, homeImageView, pixels, pixels, true);
	}
	
	private void loadBitmap(int resId, ImageView imageView, int width, int height, boolean last) {
		BitmapWorkerTask task = new BitmapWorkerTask(getActivity(), imageView, last);
		task.execute(resId, width, height);
	}

}