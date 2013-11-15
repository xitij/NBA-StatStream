package com.jitix.nbastatstream;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

class GameCalendar implements DatePickerDialog.OnDateSetListener {

	private static final String TAG = "NBAStatStream";
	
	Context context;
	TaskListener listener;
	
	public GameCalendar(Context context, TaskListener listener) {
		this.context = context;
		this.listener = listener;
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		
		//
		// Check the date
		//
		// Create a calendar with the selected date
		final Calendar selectedDate = Calendar.getInstance();
		selectedDate.set(year, month, day);
		// Create a calendar with the start date
		final Calendar startDate = Calendar.getInstance();
		startDate.set(2011, Calendar.DECEMBER, 16);
		// Create a calendar with yesterday's date (most recent date available)
		final Calendar curDate = Calendar.getInstance();
		
	
		// Make sure the date isn't before 12/126/2011 (first date that API provides data for)
		if(selectedDate.before(startDate)) {
			Log.d(TAG, "GameCalendar: onDateSet : Date selected was before the first date available from the API.");
			Log.d(TAG,  "Date = " + (month+1) + "/" + day  + "/" + year);
			// Pop up a toast to tell the user the date isn't acceptable
			String dateErrorEarly = "Game information only goes back to December 16, 2011. Please select a date after that date!";
			Toast.makeText(context, dateErrorEarly, Toast.LENGTH_LONG).show();
		}
		// Make sure the date isn't in the current date or in the future
		else if(selectedDate.after(curDate)) {
			Log.d(TAG, "GameCalendar: onDateSet : Date selected was after available information date. Current and future dates aren't supported yet!");
			Log.d(TAG,  "Date = " + (month+1) + "/" + day  + "/" + year);
			// Pop up a toast to tell the user the date isn't acceptable
			String dateErrorFuture = "Cannot get game infomation for current or future dates. Please select a date before the current!";
			Toast.makeText(context, dateErrorFuture, Toast.LENGTH_LONG).show();
		}
		// Correct date
		else {
			Log.d(TAG, "GameCalendar: onDateSet : Date = " + (month+1) + "/" + day + "/" + year);
			
			// Get the games for the selected date
			//new GameDownloader(listener, progress).execute(year, month, day);
		}
	}
}