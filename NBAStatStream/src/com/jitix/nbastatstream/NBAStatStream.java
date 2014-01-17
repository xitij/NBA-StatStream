package com.jitix.nbastatstream;

import java.util.Calendar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NBAStatStream extends FragmentActivity implements TaskListener, OnClickListener, DatePickerDialog.OnDateSetListener {

	private static final String TAG = "NBAStatStream";
	private static final String NO_GAME_RESULTS = "NO_RESULTS";
	private static final float SCALE = Resources.getSystem().getDisplayMetrics().density;
	
	// Progress Bar
	private static ProgressBar progress;
	// Events downloaded
	private static Events myEvents;
	// Object to hold our GameDownloader
	private GameDownloader myDownloader = null;
	// Array to hold our CalendarUpdateTasks
	SparseArray<CalendarUpdateTask> calendarTasks = new SparseArray<CalendarUpdateTask>(15);
	// Array to hold our BitmapWorkerTasks
	SparseArray<BitmapWorkerTask> bitmapTasks = new SparseArray<BitmapWorkerTask>(30);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "NBAStatStream onCreate!");
		Log.d(TAG, "Scale = " + SCALE);
		
		int size = ((NBATeamInfo) getApplicationContext()).getNBATeamInfoSize();
		if(size != 32) {
			((NBATeamInfo) getApplicationContext()).setNBATeamInfo();
		}
		
		///////////////////////////////////////////////////////////////////
		// Setup the calendar layout 
		///////////////////////////////////////////////////////////////////
		
		setContentView(R.layout.calendar_layout);
		
		// Get the Progress Bar
		progress = (ProgressBar) findViewById(R.id.calendar_progress_bar);
		
		// Get the current date or the saved date
		Integer year;
		Integer month;
		Integer day;
		String dateString;
		if(savedInstanceState == null) {
			final Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH) + 1;
			day = cal.get(Calendar.DAY_OF_MONTH);
			dateString = month + "/" + day.toString() + "/" + year.toString();
		} else {
			year = savedInstanceState.getInt("DateYear", -1);
			month = savedInstanceState.getInt("DateMonth", -1);
			day = savedInstanceState.getInt("DateDay", -1);
			dateString = month + "/" + day + "/" + year;
			Log.d(TAG, "NBAStatStream : onCreate : savedDate = " + dateString);
		}
		
		// Set up the date button
		Button dateButton = (Button) findViewById(R.id.calendar_date_picker);
		dateButton.setText(dateString);
		dateButton.setOnClickListener(this);
		// Set up the previous and next buttons
		ImageButton nextButton = (ImageButton) findViewById(R.id.calendar_date_select_button_forward);
		nextButton.setOnClickListener(this);
		ImageButton prevButton = (ImageButton) findViewById(R.id.calendar_date_select_button_back);
		prevButton.setOnClickListener(this);
		
		// Setup a GameCalendar to listen for date changes
		//datePicker.setDateListener(gameCalendar);
		
		// Download the games for the start date
		String monthString = getMonthString(month);
		progress.setVisibility(View.VISIBLE);
		myDownloader = new GameDownloader(this, this);
		myDownloader.execute(year.toString(), monthString, day.toString());
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Store the date from the Button
		Button dateButton = (Button) findViewById(R.id.calendar_date_picker);
		String[] dateString = ((String) dateButton.getText()).split("/");
		String month = dateString[0];
		String day = dateString[1];
		String year = dateString[2];
		Log.d(TAG, "NBAStatStream : onSaveInstanceState : saving date = " + month + "/" + day + "/" + year);
		
		outState.putInt("DateYear", Integer.parseInt(year));
		outState.putInt("DateMonth", Integer.parseInt(month));
		outState.putInt("DateDay", Integer.parseInt(day));
		//String dateString = Integer.toString(month + 1) + "/" + Integer.toString(day) + "/" + Integer.toString(year);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.nbastat_stream, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.calendar_date_picker:
			Bundle b = getDatePickerBundle();
			DialogFragment datePicker = new DatePickerDialogFragment();
			datePicker.setArguments(b);
			datePicker.show(getSupportFragmentManager(), "date_picker_dialog");
			break;
		case R.id.calendar_date_select_button_back:
			// Get the events from the previous day
			downloadPreviousEvents();
			break;
		case R.id.calendar_date_select_button_forward:
			// Get the events from the next day
			downloadNextEvents();
			break;
		default:
			// Check if an event button was clicked...
			checkEventClicked(v);
			break;
		}
	}
	
	private Bundle getDatePickerBundle() {
		Bundle b = new Bundle();
		
		int[] date = getDatePickerDates();
		b.putInt(DatePickerDialogFragment.YEAR, date[0]);
		b.putInt(DatePickerDialogFragment.MONTH, date[1]);
		b.putInt(DatePickerDialogFragment.DATE, date[2]);
		return b;
	}
	
	private int[] getDatePickerDates() {
		int[] dateArray = new int[3];

		// Get the date picker button
		Button dateButton = (Button) findViewById(R.id.calendar_date_picker);
		
		// Convert to date text to numerical value
		String date = (String) dateButton.getText();
		int year = Integer.parseInt(date.split("/")[2]);
		int month = (Integer.parseInt(date.split("/")[0]) - 1);
		int day = Integer.parseInt(date.split("/")[1]);
		
		dateArray[0] = year;
		dateArray[1] = month;
		dateArray[2] = day;
		
		return dateArray;
	}
	
	private void downloadPreviousEvents() {
		// Get the current date from the picker
		Button dateButton = (Button) findViewById(R.id.calendar_date_picker);
		String date = (String) dateButton.getText();
		Integer year = Integer.parseInt(date.split("/")[2]);
		Integer month = (Integer.parseInt(date.split("/")[0]) - 1);
		Integer day = Integer.parseInt(date.split("/")[1]);
		
		// Create a calendar and get the previous date
		Calendar c = Calendar.getInstance();
		c.set(year, month, day);
		c.add(Calendar.DAY_OF_YEAR, -1);
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		// Create a calendar with the start date
		final Calendar startDate = Calendar.getInstance();
		startDate.set(2011, Calendar.DECEMBER, 16);
		
		// Make sure the date isn't before the first date
		if(c.before(startDate)) {
			updateDateButton(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
			Log.d(TAG, "onDateSet : Date selected was before the first date available from the API.");
			Log.d(TAG,  "Date = " + month + "/" + day  + "/" + year);
			// Pop up a toast to tell the user the date isn't acceptable
			String dateErrorEarly = "Game information only goes back to December 16, 2011. Please select a date after that date!";
			Toast.makeText(getBaseContext(), dateErrorEarly, Toast.LENGTH_LONG).show();
		} else {
			updateDateButton(year, month, day);
			// Call the downloader
			String monthString = getMonthString(month+1);
			progress.setVisibility(View.VISIBLE);
			if(myDownloader != null) {
				Log.d(TAG, "NBAStatStream : downloadPreviousEvents : myDownloader != null, cancelling it..");
				if(!myDownloader.cancel(true)) {
					Log.d(TAG, "NBAStatStream : downloadPreviousEvents : myDownloader failed to cancel.");
				}
			}
			removeGameView();
			myDownloader = new GameDownloader(this, this);
			myDownloader.execute(year.toString(), monthString, day.toString());
			// Cancel all the previous calendar update and bitmap worker tasks because we have no games
			cancelBitmapTasks();
			cancelCalendarTasks();
		}
	}
	
	private void downloadNextEvents() {
		// Get the current date from the picker
		Button dateButton = (Button) findViewById(R.id.calendar_date_picker);
		String date = (String) dateButton.getText();
		Integer year = Integer.parseInt(date.split("/")[2]);
		Integer month = (Integer.parseInt(date.split("/")[0]) - 1);
		Integer day = Integer.parseInt(date.split("/")[1]);
		
		// Create a calendar and get the next date
		Calendar c = Calendar.getInstance();
		c.set(year, month, day);
		c.add(Calendar.DAY_OF_YEAR, 1);
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		// Create a calendar with last day of NBA season
		final Calendar endDate = Calendar.getInstance();
		endDate.set(2014, Calendar.APRIL, 16);
		
		// Make sure the date isn't past the end of season
		if(c.after(endDate)) {
			updateDateButton(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
			Log.d(TAG, "onDateSet : Date selected was after last scheduled date for 2013-2014 Season.");
			Log.d(TAG,  "Date = " + month + "/" + day  + "/" + year);
			// Pop up a toast to tell the user the date isn't acceptable
			String dateErrorFuture = "Date selected was after the end of the 2013-2014 season. Please select another date.";
			Toast.makeText(getBaseContext(), dateErrorFuture, Toast.LENGTH_LONG).show();
		} 
		else {
			updateDateButton(year, month, day);
			// Call the downloader
			String monthString = getMonthString(month+1);
			progress.setVisibility(View.VISIBLE);
			if(myDownloader != null) {
				Log.d(TAG, "NBAStatStream : downloadNextEvents : myDownloader != null, cancelling it..");
				if(!myDownloader.cancel(true)) {
					Log.d(TAG, "NBAStatStream : downloadNextEvents : myDownloader failed to cancel.");
				}
			}
			removeGameView();
			myDownloader = new GameDownloader(this, this);
			myDownloader.execute(year.toString(), monthString, day.toString());
			// Cancel all the previous calendar update and bitmap worker tasks because we have no games
			cancelBitmapTasks();
			cancelCalendarTasks();
		}
	}
	
	//
	// checkEventClicked:
	//	-Function to check if a completed event was clicked and launch an activity if so.
	//
	private void checkEventClicked(View v) {
		
		// Get the events date from our variable
		String date = myEvents.getEventsDate();
		date = date.split("T")[0];
		date = date.split("-")[0] + date.split("-")[1] + date.split("-")[2];
		Log.d(TAG, "checkEventClicked: Events date = " + date + ", with View ID = " + v.getId());
		
		// Check which event was clicked
		// -ID for each event button = date + event num
		// -Ex. 2013/10/31 + Event#(0)  = 20131031, Ex. 2013/10/31 + Event#(1) = 20131032, etc...
		int game_idx = v.getId() - Integer.parseInt(date);
		
		if(game_idx > -1 && game_idx < myEvents.getEventList().size()) {
			Event event = myEvents.getEventList().get(game_idx);
			
			// Create the unique event_id for the view
			int event_id = Integer.parseInt(event.getEventId().split("-")[0]) + game_idx;

			//
			// Check if user selected one of the events
			//
			if(v.getId() == event_id) {
				// Box Scores only exist for regular and postseason games check
				if(!event.getSeasonType().equalsIgnoreCase("pre")) {
					// If the event is completed start the Game Activity
					if(event.getEventStatus().equals("completed")) {
						Log.d(TAG, "NBAStatStream: Clicked on event = " + event.getAwayTeam().getFullName() + " vs " + event.getHomeTeam().getFullName());
						Log.d(TAG, "NBAStatStream: Starting ArchivedGame Activity to download the box score and display info");
						
						// Start the GameDownloader for the box score and set the listener as the new class
						Intent intent = new Intent(this, ArchivedGame.class);
						intent.putExtra(ArchivedGame.BOX_ID, event.getEventId());
						intent.putExtra(ArchivedGame.AWAY_TEAM, event.getAwayTeam().getFullName());
						intent.putExtra(ArchivedGame.HOME_TEAM, event.getHomeTeam().getFullName());
						startActivity(intent);
					}
					// Can't get the box score yet, post a Toast
					else {
						Toast.makeText(getBaseContext(), "Game isn't completed yet. Please try again later.", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "NBAStatStream: Clicked on event = " + event.getAwayTeam().getFullName() + " vs " + event.getHomeTeam().getFullName());
						Log.d(TAG, "NBAStatStream: Game isn't completed yet, please try again later.");
					}
				} 
				// Can't get the box score for preseason games, post a Toast
				else {
					Toast.makeText(getBaseContext(), "Box Scores are only available for Regular Season and Playoff games.", Toast.LENGTH_SHORT).show();
					Log.d(TAG, "NBAStatStream: Clicked on event = " + event.getAwayTeam().getFullName() + " vs " + event.getHomeTeam().getFullName());
					Log.d(TAG, "NBAStatStream: Preseason Box Scores are not available!");
				}
			}
		}
			
	}
	
	@Override
	public void downloadedGames(String result) {
		Log.d(TAG, "NBAStatSteam : downloadedGames called!");

		myDownloader = null;
		// Cancel all registered CalnedarUpdateTasks and BitmapWorkerTasks so they populate old views
		cancelCalendarTasks();
		cancelBitmapTasks();
		LinearLayout eventsView = (LinearLayout) findViewById(R.id.calendar_events_layout);
		removeGameView();
		
		// Result has no games
		if(result.equals(NO_GAME_RESULTS)) {
			RelativeLayout emptyView = createEmptyView();
			eventsView.addView(emptyView);
			hideProgress();
			Toast.makeText(getBaseContext(), "There are no games for selected date.", Toast.LENGTH_SHORT).show();
		} 
		// The API call returned some games..
		else {
			try {
				// Map the Events into our POJOs
				ObjectMapper mapper = new ObjectMapper();
				Events events = mapper.readValue(result, Events.class);
				
				// Save to our local variable
				myEvents = events;

				int i=0;
				for(Event event : events.getEventList()) {

					// Create an UpdateCalendarUITask to update the UI
					CalendarUpdateTask calendarTask = new CalendarUpdateTask(this, eventsView, i);
					calendarTask.execute(event);
					//calendarTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, event);
					// Register all the CalendarUpdateTasks
					registerCalendarTask(calendarTask, i);
					i++;
				}
			} catch(IOException e) {
				Log.d(TAG, "downloadedGames : IOException : " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void loadImages(Event event, int id) {
		
		// Determine if this is the last event to load the images for (used to remove progress bar)
		boolean lastEvent = (myEvents.getEventList().size() - 1) == id;
		
		// Set the Image size
		int pixels = dpToPx(60.0f);
		
		// Get the Away ImageView and resource ID
		int eventViewId = Integer.parseInt(event.getEventId().split("-")[0]) + id;
		ImageView awayImageView = (ImageView) findViewById(eventViewId + 1000);
		int awayLogo = ((NBATeamInfo) getApplicationContext()).getTeamLogo(event.getAwayTeam().getFullName());
		loadBitmap(awayLogo, awayImageView, pixels, pixels, id, false);
		
		// Get the Home ImageView and resource ID
		ImageView homeImageView = (ImageView) findViewById(eventViewId + 3000);
		int homeLogo = ((NBATeamInfo) getApplicationContext()).getTeamLogo(event.getHomeTeam().getFullName());
		loadBitmap(homeLogo, homeImageView, pixels, pixels, id+1, lastEvent);
	}

	@Override
	public void downloadedBox(String result) {
		// TODO Auto-generated method stub
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
		// Create a calendar with last day of NBA season
		final Calendar endDate = Calendar.getInstance();
		endDate.set(2014, Calendar.APRIL, 16);
		// Create a calendar for the previously selected date
		final Calendar prevDate = Calendar.getInstance();
		int[] buttonDate = getDatePickerDates();
		prevDate.set(buttonDate[0], buttonDate[1], buttonDate[2]);
	
		// Make sure the date isn't before 12/16/2011 (first date that API provides data for)
		if(selectedDate.before(startDate)) {
			Log.d(TAG, "onDateSet : Date selected was before the first date available from the API.");
			Log.d(TAG,  "Date = " + (month+1) + "/" + day  + "/" + year);
			updateDateButton(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
			year = startDate.get(Calendar.YEAR);
			month = startDate.get(Calendar.MONTH);
			day = startDate.get(Calendar.DAY_OF_MONTH);
			// Pop up a toast to tell the user the date isn't acceptable
			String dateErrorEarly = "Game information only goes back to December 16, 2011. Please select a date after that date!";
			Toast.makeText(getBaseContext(), dateErrorEarly, Toast.LENGTH_LONG).show();
		}
		// Make sure the date isn't in the current date or in the future
		else if(selectedDate.after(endDate)) {
			Log.d(TAG, "onDateSet : Date selected was after last scheduled date for 2013-2014 Season.");
			Log.d(TAG,  "Date = " + (month+1) + "/" + day  + "/" + year);
			updateDateButton(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
			year = endDate.get(Calendar.YEAR);
			month = endDate.get(Calendar.MONTH);
			day = endDate.get(Calendar.DAY_OF_MONTH);
			// Pop up a toast to tell the user the date isn't acceptable
			String dateErrorFuture = "Date selected was after the end of the 2013-2014 season. Please select another date.";
			Toast.makeText(getBaseContext(), dateErrorFuture, Toast.LENGTH_LONG).show();
		}
		// Correct date
		else {
			Log.d(TAG, "onDateSet : Date = " + (month+1) + "/" + day + "/" + year);
			updateDateButton(year, month, day);
		}
		
		//
		// If the current date wasn't the previously selected one, download the event info
		//
		if(prevDate.equals(selectedDate) == false) {
			// Remove all the current views to free up memory
			removeGameView();

			Log.d(TAG, "Selected != previous date. Downloading with date = " + (month+1) + "/" + day + "/" + year);
			// Get the games for the selected date
			String monthString = getMonthString(month+1);
			progress.setVisibility(View.VISIBLE);
			if(myDownloader != null) {
				Log.d(TAG, "NBAStatStream : onDateSet : myDownloader != null, cancelling it..");
				if(!myDownloader.cancel(true)) {
					Log.d(TAG, "NBAStatStream : onDateSet : myDownloader failed to cancel.");
				}
			}
			myDownloader = new GameDownloader(this, this);
			myDownloader.execute(Integer.toString(year), monthString, Integer.toString(day));
			// Cancel all the previous calendar update and bitmap worker tasks because we have no games
			cancelBitmapTasks();
			cancelCalendarTasks();
		}
	}
	
	private void removeGameView() {
		// Remove the previous events from the view
		LinearLayout eventsView = (LinearLayout) findViewById(R.id.calendar_events_layout);
		int count = eventsView.getChildCount();
		eventsView.removeViews(0, count);
	}
	
	private RelativeLayout createEmptyView() {
		
		//
		// Create an empty view that has no games and return it
		//
		RelativeLayout emptyView = new RelativeLayout(getApplicationContext());
		emptyView.setId(R.id.empty_game_view);
		int pixels = dpToPx(150.0f);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, pixels);
		emptyView.setBackgroundColor(getResources().getColor(R.color.ROW_GRAY));
		int marginSmall = dpToPx(2.0f);
		int marginLarge = dpToPx(15.0f);
		params.setMargins(marginSmall, marginLarge, marginSmall, marginSmall);
		emptyView.setLayoutParams(params);
		
		//
		// Add the Text
		//
		TextView emptyText = new TextView(getApplicationContext());
		emptyText.setText(getResources().getString(R.string.empty_games));
		emptyText.setTextSize(25.0f);
		emptyText.setTextColor(getResources().getColor(R.color.BLACK));
		emptyText.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
		RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		emptyText.setLayoutParams(textParams);
		
		emptyView.addView(emptyText);
		
		return emptyView;
	}
	
	private void updateDateButton(int year, int month, int day) {
		// Set the selected date on the button
		Button dateButton = (Button) findViewById(R.id.calendar_date_picker);
		String dateString = Integer.toString(month + 1) + "/" + Integer.toString(day) + "/" + Integer.toString(year);
		dateButton.setText(dateString);
	}
	
	private void loadBitmap(int resId, ImageView imageView, int width, int height, int id, boolean last) {
		BitmapWorkerTask task = new BitmapWorkerTask(this, imageView, last);
		//task.execute(resId, width, height);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, resId, width, height);
		registerBitmapTask(task, id);
	}
	
	private String getMonthString(int month) {
		String monthString = month < 10 ? "0" + Integer.toString(month) : Integer.toString(month);
		return monthString;
	}
	
	// Helper function to convert dp (density-independent pixels) to pixels
	public static int dpToPx(float dp) {
		return (int) (dp * SCALE + 0.5f);
	}
	
	// Helper function to convert pixels to dp (density-independent pixels)
	public static float pxToDp(int px) {
		return (float) (px / SCALE);
	}

	@Override
	public void hideProgress() {
		progress.setVisibility(View.GONE);
	}
	
	private void registerCalendarTask(CalendarUpdateTask task, int idx) {
		calendarTasks.put(idx, task);
	}
	
	private void registerBitmapTask(BitmapWorkerTask task, int idx) {
		bitmapTasks.put(idx, task);
	}
	
	private void cancelCalendarTasks() {
		for(int i=0; i < calendarTasks.size(); i++) {
			if(calendarTasks.valueAt(i) != null) {
				calendarTasks.get(i).cancel(true);
			}
		}
		calendarTasks.clear();
	}
	
	private void cancelBitmapTasks() {
		for(int i=0; i < bitmapTasks.size(); i++) {
			if(bitmapTasks.valueAt(i) != null) {
				bitmapTasks.get(i).cancel(true);
			}
		}
		bitmapTasks.clear();
	}
}
