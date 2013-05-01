package se.ja1984.twee.fragments;

import java.util.Calendar;

import se.ja1984.twee.UnwatchedActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;

import android.util.Log;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment
implements DatePickerDialog.OnDateSetListener {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), (UnwatchedActivity)getActivity(), year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		Log.d("Klar","happ");
	}

}