package com.jitix.nbastatstream;

import java.util.Calendar;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment {

	private DatePickerDialog.OnDateSetListener listener;
	
	void setDateListener(DatePickerDialog.OnDateSetListener myListener) {
		listener = myListener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		// Use the current date as the default date in the picker
		final Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		// Create a new instance of the DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), listener, year, month, day);
	}
}