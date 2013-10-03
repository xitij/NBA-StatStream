package com.jitix.nbastatstream;

import java.util.Hashtable;
import java.util.Map;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class NBAStatStream extends Activity implements OnClickListener {

	private static final String TAG = "NBAStatStream";
	
	class TeamInfo {
		String 	abbrev;
		int 	image_resource;
	}
	
	// Map to hold the Team information
	// Key = Team full name (ex. Detroit Pistons)
	// Object = TeamInfo class
	public static final Map<String, TeamInfo> NBATeamInfo = new Hashtable<String, TeamInfo>(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nbastat_stream);
		
		// Set up click listeners for the buttons
		View livegame = findViewById(R.id.live_game_button);
		livegame.setOnClickListener(this);
		View archivedgame = findViewById(R.id.archived_game_button);
		archivedgame.setOnClickListener(this);
		
		//Initialize the NBATeamInfo Map
		initializeTeamInfo();
	}

	private void initializeTeamInfo() {
		// 30 NBA teams
		TeamInfo team = new TeamInfo();
		team.abbrev = "ATL";
		team.image_resource = R.drawable.atl_logo;
		NBATeamInfo.put("Atlanta Hawks", team);
		
		team = new TeamInfo();
		team.abbrev = "BOS";
		team.image_resource = R.drawable.bos_logo;
		NBATeamInfo.put("Boston Celtics", team);
		
		team = new TeamInfo();
		team.abbrev = "BRK";
		team.image_resource = R.drawable.brk_logo;
		NBATeamInfo.put("Brooklyn Nets", team);
		
		team = new TeamInfo();
		team.abbrev = "CHA";
		team.image_resource = R.drawable.cha_logo;
		NBATeamInfo.put("Charlotte Bobcats", team);
		
		team = new TeamInfo();
		team.abbrev = "CHI";
		team.image_resource = R.drawable.chi_logo;
		NBATeamInfo.put("Chicago Bulls", team);
		
		team = new TeamInfo();
		team.abbrev = "CLE";
		team.image_resource = R.drawable.cle_logo;
		NBATeamInfo.put("Cleveland Cavaliers", team);
		
		team = new TeamInfo();
		team.abbrev = "DAL";
		team.image_resource = R.drawable.dal_logo;
		NBATeamInfo.put("Dallas Mavericks", team);
		
		team = new TeamInfo();
		team.abbrev = "DEN";
		team.image_resource = R.drawable.den_logo;
		NBATeamInfo.put("Denver Nuggets", team);
		
		team = new TeamInfo();
		team.abbrev = "DET";
		team.image_resource = R.drawable.det_logo;
		NBATeamInfo.put("Detroit Pistons", team);
		
		team = new TeamInfo();
		team.abbrev = "GS";
		team.image_resource = R.drawable.gs_logo;
		NBATeamInfo.put("Golden State Warriors", team);
		
		team = new TeamInfo();
		team.abbrev = "HOU";
		team.image_resource = R.drawable.hou_logo;
		NBATeamInfo.put("Houston Rockets", team);
		
		team = new TeamInfo();
		team.abbrev = "IND";
		team.image_resource = R.drawable.ind_logo;
		NBATeamInfo.put("Indiana Pacers", team);
		
		team = new TeamInfo();
		team.abbrev = "LAC";
		team.image_resource = R.drawable.lac_logo;
		NBATeamInfo.put("Los Angeles Clippers", team);
		
		team = new TeamInfo();
		team.abbrev = "LAL";
		team.image_resource = R.drawable.lal_logo;
		NBATeamInfo.put("Los Angeles Lakers", team);
		
		team = new TeamInfo();
		team.abbrev = "MEM";
		team.image_resource = R.drawable.mem_logo;
		NBATeamInfo.put("Memphis Grizzlies", team);
		
		team = new TeamInfo();
		team.abbrev = "MIA";
		team.image_resource = R.drawable.mia_logo;
		NBATeamInfo.put("Miami Heat", team);
		
		team = new TeamInfo();
		team.abbrev = "MIL";
		team.image_resource = R.drawable.mil_logo;
		NBATeamInfo.put("Milwaukee Bucks", team);
		
		team = new TeamInfo();
		team.abbrev = "MIN";
		team.image_resource = R.drawable.min_logo;
		NBATeamInfo.put("Minnesota Timberwolves", team);
		
		team = new TeamInfo();
		team.abbrev = "NOR";
		team.image_resource = R.drawable.nor_logo;
		NBATeamInfo.put("New Orleans Pelicans", team);
		
		team = new TeamInfo();
		team.abbrev = "NY";
		team.image_resource = R.drawable.ny_logo;
		NBATeamInfo.put("New York Knicks", team);
		
		team = new TeamInfo();
		team.abbrev = "OKC";
		team.image_resource = R.drawable.okc_logo;
		NBATeamInfo.put("Oklahoma City Thunder", team);
		
		team = new TeamInfo();
		team.abbrev = "ORL";
		team.image_resource = R.drawable.orl_logo;
		NBATeamInfo.put("Orlando Magic", team);
		
		team = new TeamInfo();
		team.abbrev = "PHI";
		team.image_resource = R.drawable.phi_logo;
		NBATeamInfo.put("Philadelphia 76ers", team);
		
		team = new TeamInfo();
		team.abbrev = "PHO";
		team.image_resource = R.drawable.pho_logo;
		NBATeamInfo.put("Phoenix Suns", team);
		
		team = new TeamInfo();
		team.abbrev = "POR";
		team.image_resource = R.drawable.por_logo;
		NBATeamInfo.put("Portland TrailBlazers", team);
		
		team = new TeamInfo();
		team.abbrev = "SAC";
		team.image_resource = R.drawable.sac_logo;
		NBATeamInfo.put("Sacramento Kings", team);
		
		team = new TeamInfo();
		team.abbrev = "SA";
		team.image_resource = R.drawable.sa_logo;
		NBATeamInfo.put("San Antonio Spurs", team);
		
		team = new TeamInfo();
		team.abbrev = "TOR";
		team.image_resource = R.drawable.tor_logo;
		NBATeamInfo.put("Toronto Raptors", team);
		
		team = new TeamInfo();
		team.abbrev = "UTA";
		team.image_resource = R.drawable.uta_logo;
		NBATeamInfo.put("Utah Jazz", team);
		
		team = new TeamInfo();
		team.abbrev = "WAS";
		team.image_resource = R.drawable.was_logo;
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
			startLiveGame();
			break;
		case R.id.archived_game_button:
			startArchivedGame();
			break;
		default:
			break;
		}
	}
	
	private void startLiveGame() {
		Log.d(TAG, "Clicked on live game.");
		Intent intent = new Intent(this, LiveGame.class);
		startActivity(intent);
	}
	
	private void startArchivedGame() {
		Log.d(TAG, "Clicked on archived game.");
		Intent intent = new Intent(this, ArchivedGame.class);
		startActivity(intent);
	}

}
