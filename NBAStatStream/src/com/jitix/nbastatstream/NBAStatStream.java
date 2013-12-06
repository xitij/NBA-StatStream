package com.jitix.nbastatstream;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.app.Notification.Style;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;

public class NBAStatStream extends FragmentActivity implements TaskListener, OnClickListener, DatePickerDialog.OnDateSetListener {

	private static final String TAG = "NBAStatStream";
	private static final String NO_GAME_RESULTS = "NO_RESULTS";
	public static final float SCALE = Resources.getSystem().getDisplayMetrics().density;
	
	class TeamInfo {
		String 	abbrev;
		int 	image_resource;
		int		color_main;
		int 	color_secondary;
	}
	
	// Map to hold the Team information
	// Key = Team full name (ex. Detroit Pistons)
	// Object = TeamInfo class
	public static final Map<String, TeamInfo> NBATeamInfo = new Hashtable<String, TeamInfo>();
	
	// Progress Bar
	private static ProgressBar progress;
	// Events downloaded
	private static Events myEvents;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "NBAStatStream onCreate!");
		Log.d(TAG, "Scale = " + SCALE);
		
		///////////////////////////////////////////////////////////////////
		// Setup the calendar layout 
		///////////////////////////////////////////////////////////////////
		
		setContentView(R.layout.calendar_layout);
		
		// Get the Progress Bar
		progress = (ProgressBar) findViewById(R.id.calendar_progress_bar);
		
		// Get the current date
		final Calendar cal = Calendar.getInstance();
		Integer year = cal.get(Calendar.YEAR);
		Integer month = cal.get(Calendar.MONTH) + 1;
		Integer day = cal.get(Calendar.DAY_OF_MONTH);
		String dateString = month + "/" + day.toString() + "/" + year.toString();
		
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
		new GameDownloader(this).execute(year.toString(), monthString, day.toString());
		
		initializeTeamInfo();
	}

	private void initializeTeamInfo() {
		// 30 NBA teams
		TeamInfo team = new TeamInfo();
		team.abbrev = "ATL";
		team.image_resource = R.drawable.atl_logo;
		team.color_main = R.color.HAWKS_RED;
		team.color_secondary = R.color.HAWKS_BLUE;
		NBATeamInfo.put("Atlanta Hawks", team);
		
		team = new TeamInfo();
		team.abbrev = "BOS";
		team.image_resource = R.drawable.bos_logo;
		team.color_main = R.color.CELTICS_GREEN;
		team.color_secondary = R.color.CELTICS_WHITE;
		NBATeamInfo.put("Boston Celtics", team);
		
		team = new TeamInfo();
		team.abbrev = "BRK";
		team.image_resource = R.drawable.brk_logo;
		team.color_main = R.color.NETS_BLACK;
		team.color_secondary = R.color.NETS_WHITE;
		NBATeamInfo.put("Brooklyn Nets", team);
		
		team = new TeamInfo();
		team.abbrev = "CHA";
		team.image_resource = R.drawable.cha_logo;
		team.color_main = R.color.BOBCATS_NAVY;
		team.color_secondary = R.color.BOBCATS_ORANGE;
		NBATeamInfo.put("Charlotte Bobcats", team);
		
		team = new TeamInfo();
		team.abbrev = "CHI";
		team.image_resource = R.drawable.chi_logo;
		team.color_main = R.color.BULLS_RED;
		team.color_secondary = R.color.BULLS_BLACK;
		NBATeamInfo.put("Chicago Bulls", team);
		
		team = new TeamInfo();
		team.abbrev = "CLE";
		team.image_resource = R.drawable.cle_logo;
		team.color_main = R.color.CAVS_RED;
		team.color_secondary = R.color.CAVS_YELLOW;
		NBATeamInfo.put("Cleveland Cavaliers", team);
		
		team = new TeamInfo();
		team.abbrev = "DAL";
		team.image_resource = R.drawable.dal_logo;
		team.color_main = R.color.MAVS_LIGHT_BLUE;
		team.color_secondary = R.color.MAVS_DARK_BLUE;
		NBATeamInfo.put("Dallas Mavericks", team);
		
		team = new TeamInfo();
		team.abbrev = "DEN";
		team.image_resource = R.drawable.den_logo;
		team.color_main = R.color.NUGGETS_LIGHT_BLUE;
		team.color_secondary = R.color.NUGGETS_GOLD;
		NBATeamInfo.put("Denver Nuggets", team);
		
		team = new TeamInfo();
		team.abbrev = "DET";
		team.image_resource = R.drawable.det_logo;
		team.color_main = R.color.PISTONS_BLUE;
		team.color_secondary = R.color.PISTONS_RED;
		NBATeamInfo.put("Detroit Pistons", team);
		
		team = new TeamInfo();
		team.abbrev = "GS";
		team.image_resource = R.drawable.gs_logo;
		team.color_main = R.color.WARRIORS_YELLOW;
		team.color_secondary = R.color.WARRIORS_BLUE;
		NBATeamInfo.put("Golden State Warriors", team);
		
		team = new TeamInfo();
		team.abbrev = "HOU";
		team.image_resource = R.drawable.hou_logo;
		team.color_main = R.color.ROCKETS_RED;
		team.color_secondary = R.color.ROCKETS_SILVER;
		NBATeamInfo.put("Houston Rockets", team);
		
		team = new TeamInfo();
		team.abbrev = "IND";
		team.image_resource = R.drawable.ind_logo;
		team.color_main = R.color.PACERS_YELLOW;
		team.color_secondary = R.color.PACERS_BLUE;
		NBATeamInfo.put("Indiana Pacers", team);
		
		team = new TeamInfo();
		team.abbrev = "LAC";
		team.image_resource = R.drawable.lac_logo;
		team.color_main = R.color.CLIPPERS_RED;
		team.color_secondary = R.color.CLIPPERS_BLUE;
		NBATeamInfo.put("Los Angeles Clippers", team);
		
		team = new TeamInfo();
		team.abbrev = "LAL";
		team.image_resource = R.drawable.lal_logo;
		team.color_main = R.color.LAKERS_PURPLE;
		team.color_secondary = R.color.LAKERS_YELLOW;
		NBATeamInfo.put("Los Angeles Lakers", team);
		
		team = new TeamInfo();
		team.abbrev = "MEM";
		team.image_resource = R.drawable.mem_logo;
		team.color_main = R.color.GRIZZLIES_DARK_BLUE;
		team.color_secondary = R.color.GRIZZLIES_SKY_BLUE;
		NBATeamInfo.put("Memphis Grizzlies", team);
		
		team = new TeamInfo();
		team.abbrev = "MIA";
		team.image_resource = R.drawable.mia_logo;
		team.color_main = R.color.HEAT_RED;
		team.color_secondary = R.color.HEAT_BLACK;
		NBATeamInfo.put("Miami Heat", team);
		
		team = new TeamInfo();
		team.abbrev = "MIL";
		team.image_resource = R.drawable.mil_logo;
		team.color_main = R.color.BUCKS_GREEN;
		team.color_secondary = R.color.BUCKS_RED;
		NBATeamInfo.put("Milwaukee Bucks", team);
		
		team = new TeamInfo();
		team.abbrev = "MIN";
		team.image_resource = R.drawable.min_logo;
		team.color_main = R.color.TWOLVES_BLUE;
		team.color_secondary = R.color.TWOLVES_GREEN;
		NBATeamInfo.put("Minnesota Timberwolves", team);
		
		team = new TeamInfo();
		team.abbrev = "NOR";
		team.image_resource = R.drawable.nor_logo;
		team.color_main = R.color.PELICANS_DARK_BLUE;
		team.color_secondary = R.color.PELICANS_GOLD;
		NBATeamInfo.put("New Orleans Pelicans", team);
		
		team = new TeamInfo();
		team.abbrev = "NY";
		team.image_resource = R.drawable.ny_logo;
		team.color_main = R.color.KNICKS_ORANGE;
		team.color_secondary = R.color.KNICKS_BLUE;
		NBATeamInfo.put("New York Knicks", team);
		
		team = new TeamInfo();
		team.abbrev = "OKC";
		team.image_resource = R.drawable.okc_logo;
		team.color_main = R.color.THUNDER_BLUE;
		team.color_secondary = R.color.THUNDER_ORANGE;
		NBATeamInfo.put("Oklahoma City Thunder", team);
		
		team = new TeamInfo();
		team.abbrev = "ORL";
		team.image_resource = R.drawable.orl_logo;
		team.color_main = R.color.MAGIC_BLUE;
		team.color_secondary = R.color.MAGIC_GRAY;
		NBATeamInfo.put("Orlando Magic", team);
		
		team = new TeamInfo();
		team.abbrev = "PHI";
		team.image_resource = R.drawable.phi_logo;
		team.color_main = R.color.SIXERS_BLUE;
		team.color_secondary = R.color.SIXERS_RED;
		NBATeamInfo.put("Philadelphia 76ers", team);
		
		team = new TeamInfo();
		team.abbrev = "PHO";
		team.image_resource = R.drawable.pho_logo;
		team.color_main = R.color.SUNS_ORANGE;
		team.color_secondary = R.color.SUNS_BLACK;
		NBATeamInfo.put("Phoenix Suns", team);
		
		team = new TeamInfo();
		team.abbrev = "POR";
		team.image_resource = R.drawable.por_logo;
		team.color_main = R.color.BLAZERS_RED;
		team.color_secondary = R.color.BLAZERS_BLACK;
		NBATeamInfo.put("Portland Trail Blazers", team);
		
		team = new TeamInfo();
		team.abbrev = "SAC";
		team.image_resource = R.drawable.sac_logo;
		team.color_main = R.color.KINGS_PURPLE;
		team.color_secondary = R.color.KINGS_GRAY;
		NBATeamInfo.put("Sacramento Kings", team);
		
		team = new TeamInfo();
		team.abbrev = "SA";
		team.image_resource = R.drawable.sa_logo;
		team.color_main = R.color.SPURS_SILVER;
		team.color_secondary = R.color.SPURS_BLACK;
		NBATeamInfo.put("San Antonio Spurs", team);
		
		team = new TeamInfo();
		team.abbrev = "TOR";
		team.image_resource = R.drawable.tor_logo;
		team.color_main = R.color.RAPTORS_RED;
		team.color_secondary = R.color.RAPTORS_BLACK;
		NBATeamInfo.put("Toronto Raptors", team);
		
		team = new TeamInfo();
		team.abbrev = "UTA";
		team.image_resource = R.drawable.uta_logo;
		team.color_main = R.color.JAZZ_BLUE;
		team.color_secondary = R.color.JAZZ_YELLOW;
		NBATeamInfo.put("Utah Jazz", team);
		
		team = new TeamInfo();
		team.abbrev = "WAS";
		team.image_resource = R.drawable.was_logo;
		team.color_main = R.color.WIZARDS_RED;
		team.color_secondary = R.color.WIZARDS_BLUE;
		NBATeamInfo.put("Washington Wizards", team);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nbastat_stream, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.live_game_button:
			//startLiveGame();
			break;
		case R.id.archived_game_button:
			//startArchivedGame();
			break;
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
			new GameDownloader(this).execute(year.toString(), monthString, day.toString());
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
			new GameDownloader(this).execute(year.toString(), monthString, day.toString());
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
			Log.d(TAG, "Trying to match event_id = " + event_id);

			//
			// Check if user selected one of the events
			//
			if(v.getId() == event_id) {
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
					Log.d(TAG, "NBAStatStream: Clicked on event = " + event.getAwayTeam().getFullName() + " vs " + event.getHomeTeam().getFullName());
					Log.d(TAG, "NBAStatStream: Game isn't completed yet, please try again later.");
				}
			}
		}
			
	}
	
	@Override
	public void downloadedGames(String result) {
		Log.d(TAG, "NBAStatSteam : downloadedGames called!");

		LinearLayout eventsView = (LinearLayout) findViewById(R.id.calendar_events_layout);
		removeGameView();
		
		// Result has no games
		if(result.equals(NO_GAME_RESULTS)) {
			RelativeLayout emptyView = createEmptyView();
			eventsView.addView(emptyView);
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
					i++;
				}
			} catch(IOException e) {
				Log.d(TAG, "downloadedGames : IOException : " + e.getMessage());
			}
		}
	}
	
	@Override
	public void loadImages(Event event, int id) {
		
		// Determine if this is the last event to load the images for (used to remove progress bar)
		boolean lastEvent = (myEvents.getEventList().size() - 1) == id;
		Log.d(TAG, "lastEvent = " + lastEvent + ", for event = " + event);
		
		// Set the Image size
		final float scale = getBaseContext().getResources().getDisplayMetrics().density;
		//int pixels = (int) (40.0f * scale + 40.0f);
		int pixels = dpToPx(60.0f);
		
		// Get the Away ImageView and resource ID
		int eventViewId = Integer.parseInt(event.getEventId().split("-")[0]) + id;
		ImageView awayImageView = (ImageView) findViewById(eventViewId + 1000);
		//Log.d(TAG, "Using Base Event View id = " + eventViewId + ", ImageView ID = " + (eventViewId + 1000));
		int awayLogo = getTeamLogo(event.getAwayTeam().getLastName());
		loadBitmap(awayLogo, awayImageView, pixels, pixels, false);
		
		// Get the Home ImageView and resource ID
		ImageView homeImageView = (ImageView) findViewById(eventViewId + 3000);
		//Log.d(TAG, "Using Base Event View id = " + eventViewId + ", ImageView ID = " + (eventViewId + 3000));
		int homeLogo = getTeamLogo(event.getHomeTeam().getLastName());
		loadBitmap(homeLogo, homeImageView, pixels, pixels, lastEvent);
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
			new GameDownloader(this).execute(Integer.toString(year), monthString, Integer.toString(day));
		}
	}
	
	private void removeGameView() {
		// Remove the previous events from the view
		LinearLayout eventsView = (LinearLayout) findViewById(R.id.calendar_events_layout);
		int count = eventsView.getChildCount();
		eventsView.removeViews(0, count);
	}
	
	private RelativeLayout createEmptyView() {
		
		final float scale = getBaseContext().getResources().getDisplayMetrics().density;
		int pixels = (int) (100.0f * scale + 100.0f);
		
		//
		// Create an empty view that has no games and return it
		//
		RelativeLayout emptyView = new RelativeLayout(getApplicationContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, pixels);
		emptyView.setBackgroundColor(getResources().getColor(R.color.ROW_GRAY));
		int marginSmall = (int) (1.0f * scale + 1.0f);
		int marginLarge = (int) (10.0f * scale + 10.0f);
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
	
	public static int getTeamColor(String teamName, boolean primary) {
		int teamColor;
		
		TeamInfo myTeam = NBATeamInfo.get(teamName);
		if(myTeam != null) {
			if(primary) {
				teamColor = myTeam.color_main;
			} else {
				teamColor = myTeam.color_secondary;
			}
		} else {
			Log.d(TAG, "NBAStatStream : getTeamColor : couldn't find team = " + teamName);
			teamColor = R.color.BLACK;
		}
		return teamColor;
	}
	
	public static int getTeamLogo(String teamName) {
		int teamLogo;
		
		TeamInfo myTeam = NBATeamInfo.get(teamName);
		if(myTeam != null) {
			teamLogo = myTeam.image_resource;
		} else {
			Log.d(TAG, "NBAStatStream : getTeamLogo : couldn't find team = " + teamName);
			teamLogo = R.drawable.was_logo;
		}
		return teamLogo;
	}
	
	private void updateDateButton(int year, int month, int day) {
		// Set the selected date on the button
		Button dateButton = (Button) findViewById(R.id.calendar_date_picker);
		String dateString = Integer.toString(month + 1) + "/" + Integer.toString(day) + "/" + Integer.toString(year);
		dateButton.setText(dateString);
	}
	
	private void loadBitmap(int resId, ImageView imageView, int width, int height, boolean last) {
		BitmapWorkerTask task = new BitmapWorkerTask(this, imageView, last);
		task.execute(resId, width, height);
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
}
