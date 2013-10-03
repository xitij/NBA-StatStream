package com.jitix.nbastatstream;

import java.util.Vector;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class ArchivedGamePagerAdapter extends FragmentPagerAdapter {

	private static final String TAG = "NBAStatStream";
	
	Vector<Fragment> myFragments = new Vector<Fragment>(); 
	
	public ArchivedGamePagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		
		Fragment fragment = ArchivedGameFragment.newInstance(i);
		Log.d(TAG, "Fragment getItem i = " + i);
		
		myFragments.add(fragment);
		Log.d(TAG, "Placing the Fragment in the Vector at index = " + i + ", vector.size() = " + myFragments.size());
		
		return fragment;
	}

	@Override
	public int getCount() {
		return 3;
	}
	
	public Fragment getFrag(int index) {
		try {
			return myFragments.get(index);
		} catch(Exception e) {
			Log.d(TAG, "Error getting Frag with index = " + index + ". Exception thrown, exception = " + e);
			return null;
		}
	}
	
}