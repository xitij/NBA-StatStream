package com.jitix.nbastatstream;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class ArchivedGamePagerAdapter extends FragmentPagerAdapter {

	private static final String TAG = "NBAStatStream";
	private static int size;
	
	public ArchivedGamePagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment = new ArchivedGameFragment();
		Bundle args = new Bundle();
		size++;
		Log.d(TAG, "Fragment getItem i = " + i + " size = " + size);
		args.putInt(ArchivedGameFragment.PAGE_NUM, i);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		return 3;
	}
	
}