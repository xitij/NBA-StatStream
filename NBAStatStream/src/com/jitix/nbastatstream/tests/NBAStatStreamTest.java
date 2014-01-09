package com.jitix.nbastatstream.tests;

import java.util.Calendar;
import com.jitix.nbastatstream.NBAStatStream;
import com.jitix.nbastatstream.R;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.RelativeLayout;

public class NBAStatStreamTest extends ActivityInstrumentationTestCase2<NBAStatStream> {

	private NBAStatStream mNBAStatStream;
	private RelativeLayout calendarGame;
	private Button mDateButton;

	public NBAStatStreamTest() {
		super(NBAStatStream.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mNBAStatStream = getActivity();
		calendarGame = (RelativeLayout) mNBAStatStream.findViewById(R.id.empty_game_view);
		mDateButton = (Button) mNBAStatStream.findViewById(R.id.calendar_date_picker);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*public void testPreconditions() {
		assertNotNull("mNBAStatStream is null", mNBAStatStream);
		assertNotNull("mDateButton is null", mDateButton);
	}*/
	
	public void testNBAStatStream_initDateButton() {
		
		// Set up the initial date
		final Calendar cal = Calendar.getInstance();
		Integer year = cal.get(Calendar.YEAR);
		Integer month = cal.get(Calendar.MONTH) + 1;
		Integer day = cal.get(Calendar.DAY_OF_MONTH);
		final String expected = month + "/" + day.toString() + "/" + year.toString();
		
		final String actual = mDateButton.getText().toString();
		assertEquals(expected, actual);
	}
	
}

