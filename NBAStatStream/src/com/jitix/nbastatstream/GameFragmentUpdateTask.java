package com.jitix.nbastatstream;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import com.jitix.nbastatstream.BasketballGame.AdvancedStatName;
import com.jitix.nbastatstream.BasketballGame.StatName;
import com.jitix.nbastatstream.NBAStatStream.TeamInfo;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class GameFragmentUpdateTask extends AsyncTask<Integer, Void, ViewGroup> {

	private final WeakReference<Activity> activityReference;
	private final WeakReference<ViewGroup> parentViewReference;
	private final WeakReference<BoxListener> listenerReference;
	private final BasketballGame myGame;
	private static final String TAG = "NBAStatStream";
	private static int PAGE_NUM;
	private static final int TITLE_ROW = 0;
	private static final int STATS_ROW = 1;
	private static final int STATS_NUM = 13;

	public GameFragmentUpdateTask(Context context, BoxListener listener, BasketballGame myGame, ViewGroup parentView) {
		activityReference = new WeakReference<Activity>((Activity) context);
		parentViewReference = new WeakReference<ViewGroup>(parentView);
		listenerReference = new WeakReference<BoxListener>(listener);
		this.myGame = myGame;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected ViewGroup doInBackground(Integer... params) {
		if(params[0] == ArchivedGameFragment.FOUR_FACTORS) {
			PAGE_NUM = params[0];
			return update4Factors();
		} else if(params[0] == ArchivedGameFragment.BOX_SCORE) {
			PAGE_NUM = params[0];
			return updateAdvBox();
		} else {

		}

		return null;
	}

	@Override
	protected void onPostExecute(ViewGroup result) {
		super.onPostExecute(result);
		// Add the Game/Event view to the Events View
		if(activityReference != null && activityReference.get() != null) {
			//progress.setVisibility(View.GONE);
			if(parentViewReference != null && result != null) {
				final ViewGroup parentView = parentViewReference.get();
				if(parentView != null) {
					Log.d(TAG, "parentView child # = " + parentView.getChildCount());
					Log.d(TAG, "parentViewID = " + parentView.getId());
					Log.d(TAG, "pager_4factors_layoutID = " + R.id.pager_4factors_layout + ", pager_advbox_layoutID = " + R.id.pager_advbox_layout);
					Log.d(TAG, "adv_box_team_button_barID = " + R.id.adv_box_team_button_bar + ", adv_box_frag_containerID = " + R.id.adv_box_frag_container);
					Log.d(TAG, "childCount = " + parentView.getChildCount());
					if(PAGE_NUM == ArchivedGameFragment.BOX_SCORE && parentView.getChildCount() != 0) {
						Log.d(TAG, "BOX_SCORE! Removing old view before adding result");
						parentView.removeViewAt(0);
						parentView.addView(result, 0);
					} else {
						Log.d(TAG, "4Factors! Adding result");
						parentView.addView(result);
					}
					Log.d(TAG, "Calling loadImages from AsyncTask");
					BoxListener listener = listenerReference.get();
					listener.loadImages(myGame);
				}
			}
		}
	}

	private RelativeLayout update4Factors() {

		Log.d(TAG, "GameFragmentUpdateTask : Update4Factors ...");

		RelativeLayout fourFactors = new RelativeLayout(activityReference.get());
		RelativeLayout.LayoutParams factorsParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		fourFactors.setLayoutParams(factorsParams);
		fourFactors.setId(R.id.four_factors_view);

		// Set the game date
		TextView date = new TextView(activityReference.get());
		date.setId(R.id.four_factors_date);
		RelativeLayout.LayoutParams dateParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		dateParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		dateParams.addRule(RelativeLayout.ABOVE, R.id.adv_box_away_team_logo);
		date.setGravity(Gravity.CENTER_HORIZONTAL);
		date.setTextSize(15.0f);
		date.setText(myGame.Date);
		fourFactors.addView(date, dateParams);

		// Set the logos
		int pixels = NBAStatStream.dpToPx(90.0f);
		int marginSmall = NBAStatStream.dpToPx(5.0f);
		int marginLarge = NBAStatStream.dpToPx(10.0f);
		RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(pixels, pixels);
		imageParams.addRule(RelativeLayout.BELOW, R.id.four_factors_date);
		imageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		imageParams.setMargins(marginSmall, marginLarge, 0, 0);
		ImageView homelogo = new ImageView(activityReference.get());
		ImageView awaylogo = new ImageView(activityReference.get());
		//homelogo.setAdjustViewBounds(true);
		//homelogo.setScaleType(ScaleType.FIT_CENTER);
		homelogo.setId(R.id.four_factors_home_logo);
		awaylogo.setId(R.id.four_factors_away_logo);
		fourFactors.addView(awaylogo, imageParams);
		imageParams = new RelativeLayout.LayoutParams(pixels, pixels);
		imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		imageParams.addRule(RelativeLayout.BELOW, R.id.four_factors_date);
		imageParams.setMargins(0, marginLarge, marginSmall, 0);
		fourFactors.addView(homelogo, imageParams);

		// Set the team names
		TextView awayName = new TextView(activityReference.get());
		awayName.setId(R.id.four_factors_away_name);
		RelativeLayout.LayoutParams teamNameParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		teamNameParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.four_factors_away_logo);
		teamNameParams.addRule(RelativeLayout.RIGHT_OF, R.id.four_factors_away_logo);
		Spannable span = new SpannableString(myGame.AwayTeam.getFirstName() + "\n" + myGame.AwayTeam.getLastName());
		awayName.setText(span);
		awayName.setGravity(Gravity.LEFT);
		awayName.setPadding(marginSmall, 0, 0, 0);
		Typeface robotoCondBold = Typeface.createFromAsset(activityReference.get().getAssets(), "RobotoCondensed-Bold.ttf");
		awayName.setTypeface(robotoCondBold);
		fourFactors.addView(awayName, teamNameParams);
		TextView homeName = new TextView(activityReference.get());
		homeName.setId(R.id.four_factors_home_name);
		teamNameParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		teamNameParams.addRule(RelativeLayout.LEFT_OF, R.id.four_factors_home_logo);
		teamNameParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.four_factors_home_logo);
		span = new SpannableString(myGame.HomeTeam.getFirstName() + "\n" + myGame.HomeTeam.getLastName());
		homeName.setText(span);
		homeName.setGravity(Gravity.RIGHT);
		homeName.setPadding(0, 0, marginSmall, 0);
		homeName.setTypeface(robotoCondBold);
		fourFactors.addView(homeName, teamNameParams);

		// Set the Scores
		int awayScoreValue = myGame.AwayTeamStats.get(StatName.POINTS).intValue();
		int homeScoreValue = myGame.HomeTeamStats.get(StatName.POINTS).intValue();
		TextView homeScore = new TextView(activityReference.get());
		homeScore.setId(R.id.four_factors_home_score);
		homeScore.setText(Integer.toString(homeScoreValue));
		RelativeLayout.LayoutParams scoreParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		scoreParams.addRule(RelativeLayout.ABOVE, R.id.four_factors_home_name);
		scoreParams.addRule(RelativeLayout.LEFT_OF, R.id.four_factors_home_logo);
		homeScore.setPadding(0, 0, marginLarge, 0);
		homeScore.setTextSize(30.0f);
		Typeface robotoBold = Typeface.createFromAsset(activityReference.get().getAssets(), "Roboto-Bold.ttf");
		homeScore.setTypeface(robotoBold);
		fourFactors.addView(homeScore, scoreParams);
		TextView awayScore = new TextView(activityReference.get());
		awayScore.setId(R.id.four_factors_away_score);
		awayScore.setText(Integer.toString(awayScoreValue));
		scoreParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		scoreParams.addRule(RelativeLayout.ABOVE, R.id.four_factors_away_name);
		scoreParams.addRule(RelativeLayout.RIGHT_OF, R.id.four_factors_away_logo);
		awayScore.setPadding(marginLarge, 0, 0, 0);
		awayScore.setTextSize(30.0f);
		awayScore.setTypeface(robotoBold);
		fourFactors.addView(awayScore, scoreParams);
		// Highlight the winning team score
		if(homeScoreValue > awayScoreValue) { 
			homeScore.setTextColor(activityReference.get().getResources().getColor(R.color.HIGHLIGHT_BLUE));
			homeScore.setShadowLayer(1, 1, 1, activityReference.get().getResources().getColor(R.color.BLACK));
		}
		else { 
			awayScore.setTextColor(activityReference.get().getResources().getColor(R.color.HIGHLIGHT_BLUE));
			awayScore.setShadowLayer(1, 1, 1, activityReference.get().getResources().getColor(R.color.BLACK));
		}
		
		// Create the Quarter Score Table
		TableLayout quarterScores = new TableLayout(activityReference.get());
		quarterScores.setId(R.id.four_factors_quarter_scores);
		RelativeLayout.LayoutParams quartersParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		quartersParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		quartersParams.addRule(RelativeLayout.BELOW, R.id.four_factors_away_name);
		int marginTop = NBAStatStream.dpToPx(30.0f);
		quartersParams.setMargins(0, marginTop, 0, 0);
		//quarterScores.setPadding(0, marginTop, 0, 0);
		quarterScores.setLayoutParams(quartersParams);
		quarterScores.setBackgroundColor(activityReference.get().getResources().getColor(R.color.WHITE));
		quarterScores = addScoresRow(quarterScores);
		fourFactors.addView(quarterScores);

		// Create the Table Title
		TextView tableTitle = new TextView(activityReference.get());
		tableTitle.setId(R.id.four_factors_table_title);
		RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		titleParams.addRule(RelativeLayout.BELOW, R.id.four_factors_quarter_scores);
		titleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		tableTitle.setPadding(0, marginTop, 0, 0);
		tableTitle.setTextSize(50.0f);
		tableTitle.setText(activityReference.get().getResources().getString(R.string.pager_4factors_title));
		fourFactors.addView(tableTitle, titleParams);

		// Create the 4Factors Table
		TableLayout factorsTable = new TableLayout(activityReference.get());
		factorsTable.setId(R.id.four_factors_table);
		RelativeLayout.LayoutParams tableParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tableParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		tableParams.addRule(RelativeLayout.BELOW, R.id.four_factors_table_title);
		marginLarge = NBAStatStream.dpToPx(15.0f);
		tableParams.setMargins(0, marginLarge, 0, 0);
		factorsTable.setBackgroundColor(activityReference.get().getResources().getColor(R.color.WHITE));

		//
		// Set up the Title Text Columns
		//
		TableRow titleRow = new TableRow(activityReference.get());
		titleRow.setId(R.id.four_factors_table_row_title);
		// Empty Column
		titleRow = createTableColumn(titleRow, null, 0);
		// Pace Title
		titleRow = createTableColumn(titleRow, activityReference.get().getResources().getString(R.string.title_pace), 0);
		// Efficiency Title
		titleRow = createTableColumn(titleRow, activityReference.get().getResources().getString(R.string.title_eff), 0);
		// eFG Title
		titleRow = createTableColumn(titleRow, activityReference.get().getResources().getString(R.string.title_efg), 0);
		// FT/FGA Title
		titleRow = createTableColumn(titleRow, activityReference.get().getResources().getString(R.string.title_ftfg), 0);
		// OREB Title
		titleRow = createTableColumn(titleRow, activityReference.get().getResources().getString(R.string.title_oreb), 0);
		// TO% Title
		titleRow = createTableColumn(titleRow, activityReference.get().getResources().getString(R.string.title_tor), 0);
		// Add Row to the Table
		factorsTable.addView(titleRow);

		//
		// Set up the Away Team Text Columns
		//
		TableRow awayRow = new TableRow(activityReference.get());
		TableRow homeRow = new TableRow(activityReference.get());
		awayRow.setId(R.id.four_factors_table_row_away);
		homeRow.setId(R.id.four_factors_table_row_home);
		//awayRow.setLayoutParams(rowParams);
		//homeRow.setLayoutParams(rowParams);
		// Team Abbreviation
		awayRow = createTableColumn(awayRow, myGame.AwayTeam.getAbbrev(), 1);
		homeRow = createTableColumn(homeRow, myGame.HomeTeam.getAbbrev(), 2);
		// Pace
		awayRow = createTableColumn(awayRow, Float.toString(myGame.AwayTeamAdvStats.get(AdvancedStatName.PACE)), 1);
		homeRow = createTableColumn(homeRow, Float.toString(myGame.HomeTeamAdvStats.get(AdvancedStatName.PACE)), 2);
		// Offensive Efficiency
		awayRow = createTableColumn(awayRow, Float.toString(myGame.AwayTeamAdvStats.get(AdvancedStatName.OFFEFF)), 1);
		homeRow = createTableColumn(homeRow, Float.toString(myGame.HomeTeamAdvStats.get(AdvancedStatName.OFFEFF)), 2);
		// eFG%
		awayRow = createTableColumn(awayRow, Float.toString(myGame.AwayTeamAdvStats.get(AdvancedStatName.EFGPERCENT)), 1);
		homeRow = createTableColumn(homeRow, Float.toString(myGame.HomeTeamAdvStats.get(AdvancedStatName.EFGPERCENT)), 2);
		// FT/FGA
		awayRow = createTableColumn(awayRow, Float.toString(myGame.AwayTeamAdvStats.get(AdvancedStatName.FTFGA)), 1);
		homeRow = createTableColumn(homeRow, Float.toString(myGame.HomeTeamAdvStats.get(AdvancedStatName.FTFGA)), 2);
		// Offensive Rebounding %
		awayRow = createTableColumn(awayRow, Float.toString(myGame.AwayTeamAdvStats.get(AdvancedStatName.OREBPERCENT)), 1);
		homeRow = createTableColumn(homeRow, Float.toString(myGame.HomeTeamAdvStats.get(AdvancedStatName.OREBPERCENT)), 2);
		// Turnover %
		awayRow = createTableColumn(awayRow, Float.toString(myGame.AwayTeamAdvStats.get(AdvancedStatName.TOPERCENT)), 1);
		homeRow = createTableColumn(homeRow, Float.toString(myGame.HomeTeamAdvStats.get(AdvancedStatName.TOPERCENT)), 2);
		// Add the rows to the table
		factorsTable.addView(awayRow);
		factorsTable.addView(homeRow);

		// Add the Table to the View
		fourFactors.addView(factorsTable, tableParams);

		return fourFactors;
	}

	private TableLayout addScoresRow(TableLayout quarterScores) {

		int marginSmall = NBAStatStream.dpToPx(0.5f);
		int paddingSmall = NBAStatStream.dpToPx(5.0f);
		TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		TableRow.LayoutParams textParams = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
		textParams.setMargins(marginSmall, marginSmall, marginSmall, marginSmall);
		
		// Add the Title Row
		TableRow titleRow = new TableRow(activityReference.get());
		titleRow.setLayoutParams(rowParams);
		TextView blankView = new TextView(activityReference.get());
		blankView.setBackgroundColor(activityReference.get().getResources().getColor(R.color.ROW_DARK_SLATE_GRAY));
		blankView.setLayoutParams(textParams);
		blankView.setPadding(paddingSmall, paddingSmall, paddingSmall, paddingSmall);
		titleRow.addView(blankView);
		for(int i=0; i<myGame.homePeriodScores.length; i++) {
			TextView score = new TextView(activityReference.get());
			score.setBackgroundColor(activityReference.get().getResources().getColor(R.color.ROW_DARK_SLATE_GRAY));
			score.setText(Integer.toString(i+1));
			score.setTextColor(activityReference.get().getResources().getColor(R.color.WHITE));
			score.setGravity(Gravity.CENTER);
			score.setLayoutParams(textParams);
			score.setPadding(paddingSmall, paddingSmall, paddingSmall, paddingSmall);
			titleRow.addView(score);
		}
		
		// Add the Away Row
		TableRow awayRow = new TableRow(activityReference.get());
		awayRow.setLayoutParams(rowParams);
		TextView awayAbbrev = new TextView(activityReference.get());
		awayAbbrev.setLayoutParams(textParams);
		awayAbbrev.setText(myGame.AwayTeam.getAbbrev());
		awayAbbrev.setBackgroundColor(activityReference.get().getResources().getColor(R.color.ROW_LIGHT_GRAY));
		awayAbbrev.setPadding(paddingSmall, paddingSmall, paddingSmall, paddingSmall);
		awayRow.addView(awayAbbrev);
		for(int i=0; i<myGame.awayPeriodScores.length; i++) {
			TextView score = new TextView(activityReference.get());
			score.setBackgroundColor(activityReference.get().getResources().getColor(R.color.ROW_LIGHT_GRAY));
			score.setText(Integer.toString(myGame.awayPeriodScores[i]));
			score.setGravity(Gravity.CENTER);
			score.setLayoutParams(textParams);
			score.setPadding(paddingSmall, paddingSmall, paddingSmall, paddingSmall);
			awayRow.addView(score);
		}
		
		// Add the Home Row
		TableRow homeRow = new TableRow(activityReference.get());
		homeRow.setLayoutParams(rowParams);
		TextView homeAbbrev = new TextView(activityReference.get());
		homeAbbrev.setLayoutParams(textParams);
		homeAbbrev.setText(myGame.HomeTeam.getAbbrev());
		homeAbbrev.setBackgroundColor(activityReference.get().getResources().getColor(R.color.ROW_LIGHT_GRAY));
		homeAbbrev.setPadding(paddingSmall, paddingSmall, paddingSmall, paddingSmall);
		homeRow.addView(homeAbbrev);
		for(int i=0; i<myGame.homePeriodScores.length; i++) {
			TextView score = new TextView(activityReference.get());
			score.setBackgroundColor(activityReference.get().getResources().getColor(R.color.ROW_LIGHT_GRAY));
			score.setText(Integer.toString(myGame.homePeriodScores[i]));
			score.setGravity(Gravity.CENTER);
			score.setLayoutParams(textParams);
			score.setPadding(paddingSmall, paddingSmall, paddingSmall, paddingSmall);
			homeRow.addView(score);
		}
		
		quarterScores.addView(titleRow);
		quarterScores.addView(awayRow);
		quarterScores.addView(homeRow);
		
		return quarterScores;
	}

	private LinearLayout updateAdvBox() {

		Log.d(TAG, "GameFragmentUpdateTask : updateAdvBox ...");
		
		// Create the Button interface for the Advanced Box Score Fragment
		LinearLayout buttonLayout = new LinearLayout(activityReference.get());
		LinearLayout.LayoutParams interfaceParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		buttonLayout.setLayoutParams(interfaceParams);
		buttonLayout.setId(R.id.adv_box_team_button_bar);

		// Create and add the Buttons to the layout
		buttonLayout = addTeamButton(buttonLayout, false);
		buttonLayout = addTeamButton(buttonLayout, true);

		// Create the Advanced Box Score TableLayout for the Home and Away Fragments
		Log.d(TAG, "creating Advanced Box Score Tables.....");
		createAdvBoxTable(false);
		createAdvBoxTable(true);

		return buttonLayout;
	}

	private TableRow createTableColumn(TableRow myRow, String text, int type) {

		// Padding and Margin
		int textPad = NBAStatStream.dpToPx(3.0f);
		int textMargin = NBAStatStream.dpToPx(0.5f);

		// TextView to add to Row
		TextView colText = new TextView(activityReference.get());

		// Layout Parameters
		TableRow.LayoutParams textParams = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textParams.setMargins(textMargin, textMargin, textMargin, textMargin);

		// Set up the TextView
		colText.setPadding(textPad, textPad, textPad, textPad);
		if(type == 0) { // Title Row
			colText.setBackgroundColor(activityReference.get().getResources().getColor(R.color.ROW_LIGHT_SLATE_GRAY));
			colText.setTextColor(activityReference.get().getResources().getColor(R.color.WHITE));
		} else if(type == 1) { // Away Row
			colText.setBackgroundColor(activityReference.get().getResources().getColor(R.color.ROW_GRAY));
		} else { // Home Row
			colText.setBackgroundColor(activityReference.get().getResources().getColor(R.color.ROW_LIGHT_GRAY));
		}
		colText.setTextSize(15.0f);
		colText.setText(text);
		colText.setGravity(Gravity.CENTER_HORIZONTAL);
		colText.setLayoutParams(textParams);

		myRow.addView(colText);

		return myRow;
	}

	private LinearLayout addTeamButton(LinearLayout myLayout, boolean home) {

		// Create a LinearLayout for the Team Button
		RelativeLayout teamButton = new RelativeLayout(activityReference.get());
		LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
		teamButton.setLayoutParams(buttonParams);
		teamButton.setClickable(true);

		// ImageView for the team logo
		ImageView teamLogo = new ImageView(activityReference.get());
		int pixels = NBAStatStream.dpToPx(50.0f);
		RelativeLayout.LayoutParams logoParams = new RelativeLayout.LayoutParams(pixels, pixels);
		int marginSmall = NBAStatStream.dpToPx(2.0f);
		logoParams.setMargins(marginSmall, marginSmall, marginSmall, marginSmall);
		//teamLogo.setAdjustViewBounds(true);
		//teamLogo.setScaleType(ScaleType.FIT_CENTER);

		// TextView for the Team Name
		TextView teamName = new TextView(activityReference.get());
		//LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textParams.setMargins(marginSmall, marginSmall, marginSmall, marginSmall);
		
		// ImageView for the Team Color
		ImageView teamColor = new ImageView(activityReference.get());
		int linePixels = NBAStatStream.dpToPx(8.0f);
		RelativeLayout.LayoutParams lineParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, linePixels);
		lineParams.setMargins(0, marginSmall, 0, (2*marginSmall));
		teamColor.setLayoutParams(lineParams);

		// Set the IDs and text
		Spannable span;
		if(home) {
			//Resources resoures = activityReference.get().getResources();
			teamButton.setId(R.id.adv_box_home_team_button);
			teamLogo.setId(R.id.adv_box_home_team_logo);
			teamName.setId(R.id.adv_box_home_team_name);
			teamColor.setId(R.id.adv_box_home_team_color);
			teamColor.setBackgroundColor(activityReference.get().getResources().getColor(NBAStatStream.getTeamColor(myGame.HomeTeam.getFullName(), false)));
			span = new SpannableString(myGame.HomeTeam.getFirstName() + "\n" + myGame.HomeTeam.getLastName());
			teamName.setGravity(Gravity.RIGHT);
			// Set the Params
			logoParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			logoParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			textParams.addRule(RelativeLayout.LEFT_OF, R.id.adv_box_home_team_logo);
			textParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.adv_box_home_team_logo);
			lineParams.addRule(RelativeLayout.BELOW, R.id.adv_box_home_team_logo);
		} else {
			teamButton.setId(R.id.adv_box_away_team_button);
			teamLogo.setId(R.id.adv_box_away_team_logo);
			teamName.setId(R.id.adv_box_away_team_name);
			teamColor.setId(R.id.adv_box_away_team_color);
			teamColor.setBackgroundColor(activityReference.get().getResources().getColor(NBAStatStream.getTeamColor(myGame.AwayTeam.getFullName(), false)));
			span = new SpannableString(myGame.AwayTeam.getFirstName() + "\n" + myGame.AwayTeam.getLastName());
			teamName.setGravity(Gravity.LEFT);
			// Set the Params
			logoParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			logoParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			textParams.addRule(RelativeLayout.RIGHT_OF, R.id.adv_box_away_team_logo);
			textParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.adv_box_away_team_logo);
			lineParams.addRule(RelativeLayout.BELOW, R.id.adv_box_away_team_logo);
		}
		teamLogo.setLayoutParams(logoParams);
		teamName.setLayoutParams(textParams);
		teamName.setText(span);

		// Add the View to the Layout
		teamButton.addView(teamColor);
		teamButton.addView(teamLogo);
		teamButton.addView(teamName);
		myLayout.addView(teamButton);

		return myLayout;
	}

	private void createAdvBoxTable(boolean home) {

		Log.d(TAG, "createAdvBoxTable : home == " + home);
		
		// Create the ScrollView that will hold the Layout
		ScrollView advBox = new ScrollView(activityReference.get());
		FrameLayout.LayoutParams scrollParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		advBox.setLayoutParams(scrollParams);
		advBox.setVerticalScrollBarEnabled(true);
		if(home) {
			advBox.setId(R.id.adv_box_view_home);
		} else {
			advBox.setId(R.id.adv_box_view_away);
		}

		// Create the LinearLayout that holds the 2 Tables
		LinearLayout advBoxTables = new LinearLayout(activityReference.get());
		ScrollView.LayoutParams tablesLayout = new ScrollView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		advBoxTables.setLayoutParams(tablesLayout);
		advBoxTables.setOrientation(LinearLayout.HORIZONTAL);
		advBoxTables.setId(R.id.adv_box_tables);

		//
		// Create the Table for the Player Names
		//
		TableLayout playerTable = new TableLayout(activityReference.get());
		LinearLayout.LayoutParams namesParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
		playerTable.setLayoutParams(namesParams);
		playerTable.setBackgroundColor(activityReference.get().getResources().getColor(R.color.WHITE));
		playerTable.setId(R.id.adv_box_table_players);
		// Create a Table Row for the Title
		TableRow playerTitle = createPlayerRow(TITLE_ROW, null, 0, home);
		playerTable.addView(playerTitle);

		//
		// Create the Horizontal ScrollView that holds the Table for Stats
		//
		HorizontalScrollView statsScroll = new HorizontalScrollView(activityReference.get());
		//LinearLayout.LayoutParams statsScrollParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1.0f);
		LinearLayout.LayoutParams statsScrollParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
		statsScroll.setLayoutParams(statsScrollParams);
		statsScroll.setHorizontalScrollBarEnabled(true);
		// TODO: Stretch
		statsScroll.setFillViewport(true);
		statsScroll.setId(R.id.adv_box_table_stats_scroll);
		
		// LinearLayout for the HorizontalScrollView
		LinearLayout statsLayout = new LinearLayout(activityReference.get());
		HorizontalScrollView.LayoutParams horiParams = new HorizontalScrollView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		statsLayout.setLayoutParams(horiParams);
		statsLayout.setOrientation(LinearLayout.VERTICAL);
		
		// Create the Stats Table (it scrolls horizontal)
		TableLayout statsTable = new TableLayout(activityReference.get());
		//HorizontalScrollView.LayoutParams statsTableParams = new HorizontalScrollView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		//TableLayout.LayoutParams statsTableParams = new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		LinearLayout.LayoutParams statsTableParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		statsTable.setLayoutParams(statsTableParams);
		//statsTable.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		statsTable.setBackgroundColor(activityReference.get().getResources().getColor(R.color.WHITE));
		statsTable.setId(R.id.adv_box_table_stats);
		// Create a Table Row for the Stats Title
		TableRow statsTitle = createStatsRow(TITLE_ROW, null, 0, home);
		statsTable.addView(statsTitle);
		// Create Table Rows for all the Player Stats
		Iterator<Map.Entry<String, BoxScoreLine>> it;
		if(home == true) {
			it = myGame.HomeTeamBox.entrySet().iterator();
		} else {
			it = myGame.AwayTeamBox.entrySet().iterator();
		}
		int index = 0;
		while(it.hasNext()) {

			Entry<String, BoxScoreLine> pair = it.next();
			String name = pair.getKey();
			BoxScoreLine box = pair.getValue();

			if(box.Minutes != 0) {
				// Stats row
				TableRow statsRow = createStatsRow(STATS_ROW, name, index, home);
				statsTable.addView(statsRow);
				// Player name row
				TableRow playerRow = createPlayerRow(STATS_ROW, name, index, home);
				playerTable.addView(playerRow);
				index++;
			}
		}
		
		// Add the Tables to the Layout
		//statsScroll.addView(statsTable);
		statsLayout.addView(statsTable);
		statsScroll.addView(statsLayout);
		advBoxTables.addView(playerTable);
		//advBoxTables.addView(statsTable);
		advBoxTables.addView(statsScroll);
		// Add the LinearLayout to the ScrollView
		advBox.addView(advBoxTables);
		
		
		// Call the listener to load the images
		BoxListener listener = listenerReference.get();
		listener.createAdvBoxScoreFrag(advBox, home);
	}

	private TableRow createStatsRow(int type, String name, int row, boolean home) {

		int marginSmall = NBAStatStream.dpToPx(0.5f);

		TableRow statsRow = new TableRow(activityReference.get());
		//LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 13.0f);
		//TableRow.LayoutParams rowParams = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 13.0f);
		TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		//rowParams.weight = 13.0f;
		rowParams.setMargins(marginSmall, marginSmall, marginSmall, marginSmall);
		statsRow.setLayoutParams(rowParams);

		if(type == TITLE_ROW) {
			statsRow.setId(R.id.adv_box_table_stats_names);
		}

		// Create the TextViews for the row
		for(int col=0; col<STATS_NUM; col++) {
			statsRow = createStatsText(statsRow, type, row, col, name, home);
		}
		return statsRow;
	}

	private TableRow createPlayerRow(int type, String name, int row, boolean home) {

		//Log.d(TAG, "createPlayerRow: type = " + type + ", name = " + name + ", row = " + row + ", home = " + home);
		
		int marginSmall = NBAStatStream.dpToPx(0.5f);

		TableRow playerRow = new TableRow(activityReference.get());
		//LinearLayout.LayoutParams playerParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
		TableLayout.LayoutParams playerParams = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
		playerParams.setMargins(marginSmall, marginSmall, marginSmall, marginSmall);
		playerRow.setLayoutParams(playerParams);

		if(type == TITLE_ROW) {
			playerRow.setId(R.id.adv_box_table_players_title);
		}

		// Create TextViews for the row
		for(int col=0; col<2; col++) {
			playerRow = createPlayerText(playerRow, type, row, col, name, home);
		}
		return playerRow;
	}

	private TableRow createStatsText(TableRow myRow, int type, int rowNum, int colNum, String name, boolean home) {

		//Log.d(TAG, "createStatsText: row = " + rowNum + ", col = " + colNum);
		
		// Pixels for margin and padding
		int marginSmall = NBAStatStream.dpToPx(0.5f);
		int padSmall = NBAStatStream.dpToPx(2.0f);

		// Create the TextView and LayoutParams
		TextView myText = new TextView(activityReference.get());
		//LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
		// TODO: setWidth
		TableRow.LayoutParams textParams = new TableRow.LayoutParams(NBAStatStream.dpToPx(50.0f), NBAStatStream.dpToPx(30.0f), 1.0f);
		textParams.setMargins(marginSmall, marginSmall, marginSmall, marginSmall);
		myText.setLayoutParams(textParams);
		myText.setPadding(padSmall, padSmall, padSmall, padSmall);
		//myText.setLines(1);

		// Get the Team Colors to use
		TeamInfo teamInfo;
		if(home == true) {
			teamInfo = NBAStatStream.NBATeamInfo.get(myGame.HomeTeam.getFullName());
		} else {
			teamInfo = NBAStatStream.NBATeamInfo.get(myGame.AwayTeam.getFullName());
		}

		if(type == TITLE_ROW) {
			myText.setBackgroundColor(activityReference.get().getResources().getColor(teamInfo.color_main));
			myText.setTextColor(activityReference.get().getResources().getColor(R.color.WHITE));
			myText.setGravity(Gravity.CENTER);
			switch(colNum) {
			case 0: myText.setText(activityReference.get().getResources().getString(R.string.title_min)); break;
			case 1: myText.setText(activityReference.get().getResources().getString(R.string.title_tsp)); break;
			case 2: myText.setText(activityReference.get().getResources().getString(R.string.title_efg)); break;
			case 3: myText.setText(activityReference.get().getResources().getString(R.string.title_oreb)); break;
			case 4: myText.setText(activityReference.get().getResources().getString(R.string.title_dreb)); break;
			case 5: myText.setText(activityReference.get().getResources().getString(R.string.title_treb)); break;
			case 6: myText.setText(activityReference.get().getResources().getString(R.string.title_astp)); break;
			case 7: myText.setText(activityReference.get().getResources().getString(R.string.title_stlp)); break;
			case 8: myText.setText(activityReference.get().getResources().getString(R.string.title_blkp)); break;
			case 9: myText.setText(activityReference.get().getResources().getString(R.string.title_tor)); break;
			case 10: myText.setText(activityReference.get().getResources().getString(R.string.title_usage)); break;
			case 11: myText.setText(activityReference.get().getResources().getString(R.string.title_offrating)); break;
			case 12: myText.setText(activityReference.get().getResources().getString(R.string.title_defrating)); break;
			}
		} else {
			// Set the Background color
			int rowColor;
			if(rowNum % 2 == 0) { rowColor = activityReference.get().getResources().getColor(R.color.ROW_LIGHT_GRAY); }
			else { rowColor = activityReference.get().getResources().getColor(R.color.ROW_GRAY); }
			myText.setBackgroundColor(rowColor);
			myText.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
			// Get the BoxScoreLine and AdvancedBoxScoreLine for the player
			AdvancedBoxScoreLine advbox;
			BoxScoreLine box;
			if(home) {
				advbox = myGame.HomeTeamAdvBox.get(name);
				box = myGame.HomeTeamBox.get(name);
			} else {
				advbox = myGame.AwayTeamAdvBox.get(name);
				box = myGame.AwayTeamBox.get(name);
			}
			switch(colNum) {
			case 0: myText.setText(Integer.toString(box.Minutes));
					break;
			case 1: myText.setText(formatString(advbox.TrueShootingPercent)); 
					break;
			case 2: myText.setText(formatString(advbox.EFGPercent)); 
					break;
			case 3: myText.setText(Float.toString(advbox.ORebPercent)); 
					break;
			case 4: myText.setText(Float.toString(advbox.DRebPercent)); 
					break;
			case 5: myText.setText(Float.toString(advbox.TotRebPercent)); 
					break;
			case 6: myText.setText(Float.toString(advbox.AssistPercent)); 
					break;
			case 7: myText.setText(Float.toString(advbox.StealPercent)); 
					break;
			case 8: myText.setText(Float.toString(advbox.BlockPercent)); 
					break;
			case 9: myText.setText(Float.toString(advbox.TOPercent)); 
					break;
			case 10: myText.setText(Float.toString(advbox.Usage)); 
					break;
			case 11: myText.setText(Float.toString(advbox.OffRating)); 
					break;
			case 12: myText.setText(Float.toString(advbox.DefRating)); 
					break;
			}
		}

		// Add the TextView to the row
		myRow.addView(myText);
		return myRow;
	}

	private TableRow createPlayerText(TableRow myRow, int type, int rowNum, int colNum, String name, boolean home) {

		// Pixels for margin and padding
		int marginSmall = NBAStatStream.dpToPx(0.5f);
		int padSmall = NBAStatStream.dpToPx(2.0f);

		// Create the TextView and LayoutParams
		TextView myText = new TextView(activityReference.get());
		//LinearLayout.LayoutParams textParams;
		TableRow.LayoutParams textParams;
		if(colNum == 0) {
			textParams = new TableRow.LayoutParams(0, NBAStatStream.dpToPx(30.0f), .85f);
		} else {
			textParams = new TableRow.LayoutParams(0, NBAStatStream.dpToPx(30.0f), .15f);
		}

		textParams.setMargins(marginSmall, marginSmall, marginSmall, marginSmall);
		myText.setLayoutParams(textParams);
		myText.setPadding(padSmall, padSmall, padSmall, padSmall);
		myText.setGravity(Gravity.CENTER_VERTICAL);
		myText.setLines(1);
		myText.setEllipsize(TruncateAt.END);
		//myText.setMarqueeRepeatLimit(-1);
		//myText.setHorizontallyScrolling(true);
		//myText.setFocusable(true);
		//myText.setFocusableInTouchMode(true);

		// Get the Team Colors to use
		TeamInfo teamInfo;
		if(home == true) {
			teamInfo = NBAStatStream.NBATeamInfo.get(myGame.HomeTeam.getFullName());
		} else {
			teamInfo = NBAStatStream.NBATeamInfo.get(myGame.AwayTeam.getFullName());
		}

		if(type == TITLE_ROW) {
			myText.setBackgroundColor(activityReference.get().getResources().getColor(teamInfo.color_main));
			myText.setTextColor(activityReference.get().getResources().getColor(R.color.WHITE));
		} else {
			// Set the Background color
			int rowColor;
			if(rowNum % 2 == 0) { rowColor = activityReference.get().getResources().getColor(R.color.ROW_LIGHT_GRAY); }
			else { rowColor = activityReference.get().getResources().getColor(R.color.ROW_GRAY); }
			myText.setBackgroundColor(rowColor);
			myText.setText("Title");
			// Get the BoxScoreLine and AdvancedBoxScoreLine for the player
			BoxScoreLine box;
			if(home) {
				box = myGame.HomeTeamBox.get(name);
			} else {
				box = myGame.AwayTeamBox.get(name);
			}
			switch(colNum) {
			case 0: myText.setText(name); break;
			case 1: myText.setGravity(Gravity.CENTER); myText.setText(box.Position); break;
			default: break;
			}
		}
		
		// Add the TextView to the row
		myRow.addView(myText);
		return myRow;
	}
	
	private String formatString(float num) {
		DecimalFormat threeZeroes = new DecimalFormat("#0.000");
		String result = threeZeroes.format(num);
		return result;
	}
}
