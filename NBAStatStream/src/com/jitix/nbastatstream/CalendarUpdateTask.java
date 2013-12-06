package com.jitix.nbastatstream;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class CalendarUpdateTask extends AsyncTask<Event, Void, RelativeLayout> {

	private final WeakReference<Activity> activityReference;
	private final WeakReference<ViewGroup> parentViewReference;
	private final int eventId;
	private Event myEvent;
	private static final String TAG = "NBAStatStream";

	public CalendarUpdateTask(Context context, ViewGroup parentView, int eventNum) {
		// Use WeakReference to ensure the ImageView can be garbage collected
		parentViewReference = new WeakReference<ViewGroup>(parentView);
		activityReference = new WeakReference<Activity>((Activity) context);
		eventId = eventNum;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected RelativeLayout doInBackground(Event... params) {
		myEvent = params[0];
		
		if(activityReference != null) {
			return updateGameView(myEvent);
		} else {
			return null;
		}
	}

	@Override
	protected void onPostExecute(RelativeLayout result) {
		super.onPostExecute(result);
		// Add the Game/Event view to the Events View
		if(activityReference != null && activityReference.get() != null) {
			if(parentViewReference != null && result != null) {
				final ViewGroup parentView = parentViewReference.get();
				if(parentView != null) {
					parentView.addView(result);
					Log.d(TAG, "Added View with ID = " + eventId);
					Log.d(TAG, "Calling loadImages from AsyncTask");
					TaskListener listener = (TaskListener) activityReference.get();
					listener.loadImages(myEvent, eventId);
				}
			}
		}
	}
	
	private RelativeLayout updateGameView(Event event) {
		
		int gameSize = NBAStatStream.dpToPx(120.0f);
		
		// Create a layout and set the parameters
		RelativeLayout layout = new RelativeLayout(activityReference.get());
		LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, gameSize);
		int marginSmall = NBAStatStream.dpToPx(2.0f);
		viewParams.setMargins(marginSmall, marginSmall, marginSmall, marginSmall);
		layout.setLayoutParams(viewParams);
		layout.setBackgroundColor(activityReference.get().getResources().getColor(R.color.ROW_GRAY));
		layout.setClickable(true);
		layout.setOnClickListener((OnClickListener) activityReference.get());
		int event_id = Integer.parseInt(event.getEventId().split("-")[0]) + eventId;
		//Log.d(TAG, "CalendarUpdate : setting ID = " + event_id);
		layout.setId(event_id);
		
		// Create LayoutParams for the elements
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		// Create LayoutParams for the logos
		//int pixels = (int) (40.0f * scale + 40.0f);
		int pixels = NBAStatStream.dpToPx(60.0f);
		RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(pixels, pixels);
		
		// Add the Away Team Logo
		ImageView awayTeamLogo = new ImageView(activityReference.get());
		int awayTeamLogoID = event_id + 1000;
		awayTeamLogo.setId(awayTeamLogoID);
		//Log.d(TAG, "CalendarUpdate : updateGameView : Setting AwayLogo ImageView with ID = " + awayTeamLogoID);
		imageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		//int padSmall = (int) (3.0f * scale + 3.0f);
		int padSmall = NBAStatStream.dpToPx(4.0f);
		awayTeamLogo.setPadding(padSmall, padSmall, padSmall, padSmall);
		awayTeamLogo.setLayoutParams(imageParams);
		layout.addView(awayTeamLogo, imageParams);
		
		// Add the Away Team
		TextView awayTeam = new TextView(activityReference.get());
		int awayTeamID = event_id + 2000;
		awayTeam.setId(awayTeamID);
		awayTeam.setTypeface(Typeface.SERIF);
		Spannable span = new SpannableString(event.getAwayTeam().getFirstName() + "\n" + event.getAwayTeam().getLastName());
		awayTeam.setText(span);
		awayTeam.setGravity(Gravity.LEFT);
		awayTeam.setPadding(padSmall, padSmall, padSmall, padSmall);
		params.addRule(RelativeLayout.RIGHT_OF, awayTeamLogoID);
		params.addRule(RelativeLayout.ALIGN_BOTTOM, awayTeamLogoID);
		awayTeam.setLayoutParams(params);
		layout.addView(awayTeam);
		
		// Add the Home Team Logo
		ImageView homeTeamLogo = new ImageView(activityReference.get());
		int homeTeamLogoID = event_id + 3000;
		homeTeamLogo.setId(homeTeamLogoID);
		//Log.d(TAG, "CalendarUpdate : updateGameView : Setting HomeLogo ImageView with ID = " + homeTeamLogoID);
		imageParams = new RelativeLayout.LayoutParams(pixels, pixels);
		imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		homeTeamLogo.setPadding(padSmall, padSmall, padSmall, padSmall);
		homeTeamLogo.setLayoutParams(imageParams);
		layout.addView(homeTeamLogo, imageParams);
		
		// Add the Home Team
		TextView homeTeam = new TextView(activityReference.get());
		int homeTeamID = event_id + 4000;
		homeTeam.setId(homeTeamID);
		homeTeam.setTypeface(Typeface.SERIF);
		span = new SpannableString(event.getHomeTeam().getFirstName() + "\n" + event.getHomeTeam().getLastName());
		homeTeam.setText(span);
		homeTeam.setGravity(Gravity.RIGHT);
		homeTeam.setPadding(padSmall, padSmall, padSmall, padSmall);
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.LEFT_OF, homeTeamLogoID);
		params.addRule(RelativeLayout.ALIGN_BOTTOM, homeTeamLogoID);
		homeTeam.setLayoutParams(params);
		layout.addView(homeTeam);
		
		// Add a color bar for the game status
		// 	Completed = blue, scheduled = grey, active = red
		ImageView statusLine = new ImageView(activityReference.get());
		int statusLineID = event_id + 5000;
		statusLine.setId(statusLineID);
		//int linePixels = (int) (10.0f * scale + 10.0f);
		int linePixels = NBAStatStream.dpToPx(15.0f);
		RelativeLayout.LayoutParams lineParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, linePixels);
		lineParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		statusLine.setLayoutParams(lineParams);
		if(event.getEventStatus().equals("completed")) {
			statusLine.setBackgroundColor(activityReference.get().getResources().getColor(R.color.BACKGROUND_BLUE));
		} else {
			statusLine.setBackgroundColor(activityReference.get().getResources().getColor(R.color.DIM_GRAY));
		}
		layout.addView(statusLine);
		
		// Add the Game status
		TextView status = new TextView(activityReference.get());
		int statusID = event_id + 6000;
		status.setId(statusID);
		status.setText(event.getEventStatus());
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.BELOW, statusLineID);
		status.setLayoutParams(params);
		layout.addView(status);
		
		// Add the Game location
		TextView location = new TextView(activityReference.get());
		int locationID = event_id + 7000;
		location.setId(locationID);
		span = new SpannableString(event.getSite().getName() + "\n" + event.getSite().getCity() + "\n" + event.getSite().getState());
		location.setText(span);
		location.setTextSize(10.0f);
		location.setGravity(Gravity.CENTER_HORIZONTAL);
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.BELOW, statusID);
		//params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, statusID);
		//pixels = (int) (5.0f * scale + 5.0f);
		//params.setMargins(pixels, pixels, pixels, pixels);
		location.setLayoutParams(params);
		layout.addView(location);
		
		return layout;
	}
}