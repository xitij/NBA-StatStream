package com.jitix.nbastatstream;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

public class ArchivedGamePagerAdapter extends FragmentPagerAdapter {

	private static final String TAG = "NBAStatStream";
	
	SparseArray<Fragment> myFragments = new SparseArray<Fragment>(); 
	
	public ArchivedGamePagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		
		Fragment fragment = ArchivedGameFragment.newInstance(i);
		Log.d(TAG, "ArchivedGamePagerAdapter : getItem : i = " + i);
		
		return fragment;
	}

	@Override
	public int getCount() {
		return 3;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		Log.d(TAG, "ArchivedGamePagerAdapter : destroyItem called for position " + position);
		myFragments.remove(position);
		super.destroyItem(container, position, object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Log.d(TAG, "ArchivedGamePagerAdapter : instantiateItem called for position " + position);
		Fragment frag = (Fragment) super.instantiateItem(container, position);
		myFragments.put(position, frag);
		Log.d(TAG, "ArchivedGamePagerAdapter : Placing the Fragment in Array index = " + position + ", vector.size() = " + myFragments.size());
		return frag;
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