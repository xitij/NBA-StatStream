package com.jitix.nbastatstream;

import java.util.Vector;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
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
	private int activeBox = 0;

	Vector<Fragment> myAdvBoxFragments = new Vector<Fragment>();
	Vector<ViewGroup> myAdvBoxViews = new Vector<ViewGroup>();
	Vector<Fragment> myBoxFragments = new Vector<Fragment>();
	Vector<ViewGroup> myBoxViews = new Vector<ViewGroup>();

	private GameFragmentUpdateTask factorsUpdateTask = null;
	private GameFragmentUpdateTask advBoxUpdateTask = null;
	private GameFragmentUpdateTask boxUpdateTask = null;
	Vector<BitmapWorkerTask> bitmapTasks = new Vector<BitmapWorkerTask>(6);

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "ArchivedGameFragment : onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if(savedInstanceState != null) {
			Log.d(TAG, "ArchivedGameFragment : onCreateView : savedInstanceState != null : trying to getFrags");

			activeBox = savedInstanceState.getInt("boxNum");
			AdvancedBoxScoreFragment awayBox;
			AdvancedBoxScoreFragment homeBox;
			AdvancedBoxScoreFragment awayAdvBox;
			AdvancedBoxScoreFragment homeAdvBox;

			Bundle args = getArguments();
			if(args.getInt(PAGE_NUM) == BOX_SCORE) {
				awayBox = (AdvancedBoxScoreFragment) getChildFragmentManager().getFragment(savedInstanceState, "BoxFragAway");
				homeBox = (AdvancedBoxScoreFragment) getChildFragmentManager().getFragment(savedInstanceState, "BoxFragHome");
				if(awayBox != null) { 
					myBoxFragments.add(0, awayBox);
				}
				if(homeBox != null) { 
					myBoxFragments.add(1, homeBox);
				}
			} else if(args.getInt(PAGE_NUM) == ADV_BOX_SCORE) {
				awayAdvBox = (AdvancedBoxScoreFragment) getChildFragmentManager().getFragment(savedInstanceState, "AdvBoxFragAway");
				homeAdvBox = (AdvancedBoxScoreFragment) getChildFragmentManager().getFragment(savedInstanceState, "AdvBoxFragHome");
				if(awayAdvBox != null) { 
					myAdvBoxFragments.add(0, awayAdvBox);
				}
				if(homeAdvBox != null) { 
					myAdvBoxFragments.add(1, homeAdvBox);
				}
			}
		}

		Bundle args = getArguments();
		View myView;
		Log.d(TAG, "ArchivedGameFragment : onCreateView : page_num = " + args.getInt(PAGE_NUM));
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
		if(factorsUpdateTask != null) {
			Log.d(TAG, "ArchivedGameFragment : onDestroy : cancelling the 4Factors Task");
			factorsUpdateTask.cancel(true);
		}
		if(advBoxUpdateTask != null) {
			Log.d(TAG, "ArchivedGameFragment : onDestroy : cancelling the AdvancedBox Task");
			advBoxUpdateTask.cancel(true);
		}
		if(boxUpdateTask != null) {
			Log.d(TAG, "ArchivedGameFragment : onDestroy : cancelling the Box Task");
			boxUpdateTask.cancel(true);
		}
		cancelBitmapTasks();
		destroyed = true;
		Bundle args = getArguments();
		if(args.getInt(PAGE_NUM) == FOUR_FACTORS) {
			if(getView() != null) {
				unbindBitmap(getView().findViewById(R.id.four_factors_away_logo));
				unbindBitmap(getView().findViewById(R.id.four_factors_home_logo));
			}
		}
		else if(args.getInt(PAGE_NUM) == BOX_SCORE) {
			if(getBoxView(0) != null) {
				unbindBitmap(getBoxView(0).findViewById(R.id.box_away_team_logo));
			}
			if(getBoxView(1) != null) {
				unbindBitmap(getBoxView(1).findViewById(R.id.box_home_team_logo));
			}
		} else if(args.getInt(PAGE_NUM) == ADV_BOX_SCORE) {
			if(getAdvBoxView(0) != null) { 
				unbindBitmap(getAdvBoxView(0).findViewById(R.id.box_away_team_logo));
			}
			if(getAdvBoxView(1) != null) {
				unbindBitmap(getAdvBoxView(1).findViewById(R.id.box_home_team_logo));
			}
		}
		myAdvBoxFragments.clear();
		myAdvBoxViews.clear();
		myBoxFragments.clear();
		myBoxViews.clear();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "ArchivedGameFragment : onSaveInstance");

		Bundle args = getArguments();
		AdvancedBoxScoreFragment awayBox;
		AdvancedBoxScoreFragment homeBox;
		AdvancedBoxScoreFragment awayAdvBox;
		AdvancedBoxScoreFragment homeAdvBox;

		if(args.getInt(PAGE_NUM) == BOX_SCORE) {
			awayBox = (AdvancedBoxScoreFragment) getBoxFrag(0);
			homeBox = (AdvancedBoxScoreFragment) getBoxFrag(1);
			if(awayBox != null) { 
				getChildFragmentManager().putFragment(outState, "BoxFragAway", awayBox);
			}
			if(homeBox != null) {
				getChildFragmentManager().putFragment(outState, "BoxFragHome", homeBox);
			}
		} else if(args.getInt(PAGE_NUM) == ADV_BOX_SCORE) {
			awayAdvBox = (AdvancedBoxScoreFragment) getAdvBoxFrag(0);
			homeAdvBox = (AdvancedBoxScoreFragment) getAdvBoxFrag(1);
			if(awayAdvBox != null) {
				getChildFragmentManager().putFragment(outState, "AdvBoxFragAway", awayAdvBox);
			}
			if(homeAdvBox != null) {
				getChildFragmentManager().putFragment(outState, "AdvBoxFragHome", homeAdvBox);
			}
		}
		outState.putInt("boxNum", activeBox);
	}

	@Override
	public void onClick(View v) {
		Fragment myFrag;
		switch (v.getId()) {
		case R.id.box_away_team_button:
			Log.d(TAG, "clicked on Away Team Box Button");
			myFrag = getBoxFrag(0);
			if(myFrag != null) {
				switchBoxScoreFrag(myFrag, false);
			}
			toggleSelected(v);
			break;
		case R.id.box_home_team_button:
			Log.d(TAG, "clicked on Home Team Box Button");
			myFrag = getBoxFrag(1);
			if(myFrag != null) {
				switchBoxScoreFrag(myFrag, true);
			}
			toggleSelected(v);
			break;
		case R.id.adv_box_away_team_button:
			Log.d(TAG, "clicked on Away Team Advanced Box Button");
			myFrag = getAdvBoxFrag(0);
			if(myFrag != null) {
				switchAdvBoxScoreFrag(myFrag, false);
			}
			toggleSelected(v);
			break;
		case R.id.adv_box_home_team_button:
			Log.d(TAG, "clicked on Home Team Advanced Box Button");
			myFrag = getAdvBoxFrag(1);
			if(myFrag != null) {
				switchAdvBoxScoreFrag(myFrag, true);
			}
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

		if(!destroyed) {
			Log.d(TAG, "ArchivedGameFragment : createAdvBoxScoreFrag : home = " + home);

			// Create AdvancedBoxScoreFragments for home and away teams
			AdvancedBoxScoreFragment AdvBoxScoreFrag;
			if(myAdvBoxFragments.size() < 2) {
				if(home) {
					AdvBoxScoreFrag = AdvancedBoxScoreFragment.newInstance(1, true);
				} else {
					AdvBoxScoreFrag = AdvancedBoxScoreFragment.newInstance(0, true);
				}
				myAdvBoxFragments.add(AdvBoxScoreFrag);
			}

			// Add the View and the Fragment to the vectors for storage
			myAdvBoxViews.add(view);
		}
	}

	@Override
	public void createBoxScoreFrag(ViewGroup view, boolean home) {

		if(!destroyed) {
			Log.d(TAG, "ArchivedGameFragment : createBoxScoreFrag : home = " + home);

			// Create BoxScoreFragments for home and away teams
			AdvancedBoxScoreFragment BoxScoreFrag;
			if(myBoxFragments.size() < 2) {
				if(home) {
					BoxScoreFrag = AdvancedBoxScoreFragment.newInstance(1, false);
				} else {
					BoxScoreFrag = AdvancedBoxScoreFragment.newInstance(0, false);
				}
				myBoxFragments.add(BoxScoreFrag);
			}

			// Add the View and the Fragment to the vectors for storage
			myBoxViews.add(view);
		}
	}

	private void loadAdvBoxFrag(boolean home) {

		Log.d(TAG, "ArchivedGameFragment : loadAdvBoxFrag for home = " + home);
		AdvancedBoxScoreFragment AdvBoxScoreFrag = (AdvancedBoxScoreFragment) getAdvBoxFrag(home ? 1 : 0);
		if(AdvBoxScoreFrag != null) {
			FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
			String fragTag = (home) ? "HomeTeamAdvBoxTag" : "AwayTeamAdvBoxTag";
			//transaction.replace(R.id.adv_box_frag_container, AdvBoxScoreFrag, fragTag);
			transaction.remove(AdvBoxScoreFrag);
			transaction.addToBackStack(null);
			transaction.commit();
			getChildFragmentManager().executePendingTransactions();
			transaction = getChildFragmentManager().beginTransaction();
			transaction.add(R.id.adv_box_frag_container, AdvBoxScoreFrag, fragTag);
			transaction.addToBackStack(null);
			transaction.commit();
			getChildFragmentManager().executePendingTransactions();
			activeBox = (home) ? 1 : 0;
		}
	}

	private void loadBoxFrag(boolean home) {

		Log.d(TAG, "ArchivedGameFragment : loadBoxFrag for home = " + home);
		AdvancedBoxScoreFragment BoxScoreFrag = (AdvancedBoxScoreFragment) getBoxFrag(home ? 1 : 0);
		if(BoxScoreFrag != null) {
			FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
			String fragTag = (home) ? "HomeTeamBoxTag" : "AwayTeamBoxTag";
			//transaction.replace(R.id.box_frag_container, BoxScoreFrag, fragTag);
			transaction.remove(BoxScoreFrag);
			transaction.addToBackStack(null);
			transaction.commit();
			getChildFragmentManager().executePendingTransactions();
			transaction = getChildFragmentManager().beginTransaction();
			transaction.add(R.id.box_frag_container, BoxScoreFrag, fragTag);
			transaction.addToBackStack(null);
			transaction.commit();
			getChildFragmentManager().executePendingTransactions();
			activeBox = (home) ? 1 : 0;
		}
	}

	private void switchAdvBoxScoreFrag(Fragment myFrag, boolean home) {
		if(myFrag.isVisible()) {
			Log.d(TAG, "ArchivedGameFragment : switchAdvBoxScoreFrag : Fragment already visible so doing nothing...");
		} else {
			Log.d(TAG, "ArchivedGameFragment : switchAdvBoxScoreFrag : Replacing the old fragment");
			String fragTag = (home == false) ? "AwayTeamAdvBoxTag" : "HomeTeamAdvBoxTag";
			FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
			transaction.replace(R.id.adv_box_frag_container, myFrag, fragTag);
			transaction.addToBackStack(null);
			transaction.commit();
			getChildFragmentManager().executePendingTransactions();
			activeBox = (home) ? 1 : 0;
		}
	}

	private void switchBoxScoreFrag(Fragment myFrag, boolean home) {
		if(myFrag.isVisible()) {
			Log.d(TAG, "ArchivedGameFragment : switchBoxScoreFrag : Fragment already visible so doing nothing...");
		} else {
			Log.d(TAG, "ArchivedGameFragment : switchBoxScoreFrag : Replacing the old fragment");
			FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
			String fragTag = (home == false) ? "AwayTeamBoxTag" : "HomeTeamBoxTag";
			transaction.replace(R.id.box_frag_container, myFrag, fragTag);
			transaction.addToBackStack(null);
			transaction.commit();
			getChildFragmentManager().executePendingTransactions();
			activeBox = (home) ? 1 : 0;
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
				loadAdvBoxFrag(activeBox == 0 ? false : true);
				connectTeamButtons(getView(), true);
				updateButtonInfo(null, true);
			} else if(page_num == BOX_SCORE) {
				loadBoxImages(myGame, false);
				loadBoxFrag(activeBox == 0 ? false : true);
				connectTeamButtons(getView(), false);
				updateButtonInfo(null, false);
			}
		} else {
			Log.d(TAG, "ArchivedGameFragment has been destroyed, skipping update functions!");
		}
	}

	public void update4Factors(BasketballGame myGame) {

		// Update the view with the data in the BasketballGame object
		ViewGroup myView = (ViewGroup) getView();
		if(myView != null) {
			GameFragmentUpdateTask updateTask = new GameFragmentUpdateTask(this.getActivity(), this, myGame, (ViewGroup) myView.findViewById(R.id.pager_4factors_layout));
			Bundle args = getArguments();
			//updateTask.execute(args.getInt(PAGE_NUM));
			updateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args.getInt(PAGE_NUM));
			factorsUpdateTask = updateTask;
		}
	}

	public void updateBox(BasketballGame myGame) {

		ViewGroup myView = (ViewGroup) getView();
		if(myView != null) {
			// Update the view with the data in the BasketballGame object
			GameFragmentUpdateTask updateTask = new GameFragmentUpdateTask(this.getActivity(), this, myGame, (ViewGroup) myView.findViewById(R.id.pager_box_layout));
			Bundle args = getArguments();
			//updateTask.execute(args.getInt(PAGE_NUM));
			updateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args.getInt(PAGE_NUM));
			boxUpdateTask = updateTask;
		}
	}

	public void updateAdvBox(BasketballGame myGame) {

		ViewGroup myView = (ViewGroup) getView();
		if(myView != null) {
			// Update the view with the data in the BasketballGame object
			GameFragmentUpdateTask updateTask = new GameFragmentUpdateTask(this.getActivity(), this, myGame, (ViewGroup) myView.findViewById(R.id.pager_advbox_layout));
			Bundle args = getArguments();
			//updateTask.execute(args.getInt(PAGE_NUM));
			updateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args.getInt(PAGE_NUM));
			advBoxUpdateTask = updateTask;
		}
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
		awayButton.setBackgroundDrawable(awayList);

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
		homeButton.setBackgroundDrawable(homeList);

		if(activeBox == 0) {
			awayButton.setSelected(true);
		} else {
			homeButton.setSelected(true);
		}
	}

	private void load4FactorsImages(BasketballGame myGame) {

		factorsUpdateTask = null;

		// Set the Image size
		int pixels = NBAStatStream.dpToPx(120.0f);

		// Get the Away ImageView and resource ID
		ImageView awayImageView = (ImageView) getView().findViewById(R.id.four_factors_away_logo);
		int awayLogo = ((NBATeamInfo) getActivity().getApplicationContext()).getTeamLogo(myGame.AwayTeam.getFullName());
		loadBitmap(awayLogo, awayImageView, pixels, pixels, false);

		// Get the Home ImageView and resource ID
		ImageView homeImageView = (ImageView) getView().findViewById(R.id.four_factors_home_logo);
		int homeLogo = ((NBATeamInfo) getActivity().getApplicationContext()).getTeamLogo(myGame.HomeTeam.getFullName());
		loadBitmap(homeLogo, homeImageView, pixels, pixels, false);
	}

	private void loadBoxImages(BasketballGame myGame, boolean adv) {

		Log.d(TAG, "inside loadBoxImages...for PAGE_NUM = " + getArguments().getInt(PAGE_NUM));

		int pixels = NBAStatStream.dpToPx(50.0f);
		ImageView homeImageView;
		ImageView awayImageView;

		if(adv) {
			awayImageView = (ImageView) getView().findViewById(R.id.adv_box_away_team_logo);
			homeImageView = (ImageView) getView().findViewById(R.id.adv_box_home_team_logo);
			advBoxUpdateTask = null;
		} else {
			awayImageView = (ImageView) getView().findViewById(R.id.box_away_team_logo);
			homeImageView = (ImageView) getView().findViewById(R.id.box_home_team_logo);
			boxUpdateTask = null;
		}

		// Get the Away Button ImageView and resource ID
		int awayLogo = ((NBATeamInfo) getActivity().getApplicationContext()).getTeamLogo(myGame.AwayTeam.getFullName());
		loadBitmap(awayLogo, awayImageView, pixels, pixels, false);

		// Get the Home Button ImageView and resource ID
		int homeLogo = ((NBATeamInfo) getActivity().getApplicationContext()).getTeamLogo(myGame.HomeTeam.getFullName());
		loadBitmap(homeLogo, homeImageView, pixels, pixels, true);
	}

	private void loadBitmap(int resId, ImageView imageView, int width, int height, boolean last) {
		BitmapWorkerTask task = new BitmapWorkerTask(getActivity(), imageView, last);
		//task.execute(resId, width, height);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, resId, width, height);
		bitmapTasks.add(task);
	}

	private void cancelBitmapTasks() {
		for(int i=0; i < bitmapTasks.size(); i++) {
			BitmapWorkerTask task = bitmapTasks.get(i);
			if(task != null) {
				task.cancel(true);
			}
		}
		bitmapTasks.clear();
	}

	private void unbindBitmap(View view) {
		if(view != null) {
			Log.d(TAG, "ArchivedGameFragment : unbindBitmap : removing Bitmap from view = " + view.getId());
			((ImageView) view).setImageBitmap(null);
		}
	}
}