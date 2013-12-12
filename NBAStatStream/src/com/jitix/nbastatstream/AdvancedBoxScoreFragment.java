package com.jitix.nbastatstream;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.jitix.nbastatstream.NBAStatStream.TeamInfo;

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
	private static final String ADV_BOX = "advvanced_box";
	public static final String TEAM_NUM = "team_number";
	static final int AWAY_TEAM = 0;
	static final int HOME_TEAM = 1;
	
	private BasketballGame savedGame;
	
	//
	// newInstance: Returns an instance of the ArchivedGameFragment. Takes i (page_num)
	// 	as argument. It creates a new instance if it doesn't exist otherwise returns
	// 	the reference to the instance.
	//
	public static AdvancedBoxScoreFragment newInstance(int i, boolean advancedBox) {
		AdvancedBoxScoreFragment fragment = new AdvancedBoxScoreFragment();
		
		Log.d(TAG, "newInstance called for AdvancedBoxScoreFragment with i = " + i);
		
		// Put i in the arguments
		Bundle args = new Bundle();
		args.putInt(TEAM_NUM, i);
		args.putBoolean(ADV_BOX, advancedBox);
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate called for AdvancedBoxScoreFragment");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Bundle args = getArguments();
		int team_num = args.getInt(TEAM_NUM, -1);
		boolean adv_box = args.getBoolean(ADV_BOX);
		
		ArchivedGameFragment parentFrag = (ArchivedGameFragment) getParentFragment();
		int page_num = parentFrag.getArguments().getInt(ArchivedGameFragment.PAGE_NUM);
		Log.d(TAG, "Parent Fragment page_num = " + page_num + ", team_num = " + team_num + ", advanced box = " + adv_box);
		
		// Get the View for either the Advanced Box or Box
		ViewGroup customView;
		if(adv_box) {
			customView = parentFrag.getAdvBoxView(team_num);
		} else {
			customView = parentFrag.getBoxView(team_num);
		}
		
		if(customView == null) {
			Log.d(TAG, "AdvancedBoxScoreFragment : onCreateView : customView == null");
		} else {
			Log.d(TAG, "AdvancedBoxScoreFragment : onCreateView : viewId = " + customView.getId());
			Log.d(TAG, "adv_box_view_away = " + R.id.adv_box_view_away + ", adv_box_view_home = " + R.id.adv_box_view_home);
			Log.d(TAG, "box_view_away = " + R.id.box_view_away + ", box_view_home = " + R.id.box_view_home);
		}
		
		if(customView.getParent() != null) {
			Log.d(TAG, "AdvancedBoxScoreFragment : onCreateView : customView has parent already, removing it");
			ViewGroup parent = (ViewGroup) customView.getParent();
			parent.removeView(customView);
		}
		
		return customView;
	}
	
	@Override
	public void onStop() {
		super.onStop();
		// Save the update flag in the bundle
		Bundle args = getArguments();
	}
}
