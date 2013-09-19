package com.jitix.nbastatstream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ArchivedGameDialog extends DialogFragment implements OnEditorActionListener {

	public interface EditNameDialogListener {
		void onFinishedEditDialog(String inputText);
	}
	
	private static final String TAG = "NBAStatStream";
	private EditText box_score_text;
	private static View dialog_view;
	
	public ArchivedGameDialog() {
		// empty constructor required for DialogFragment
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the builder to create a AlertDialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate the dialog and set the layout
		dialog_view = inflater.inflate(R.layout.dialog_fragment_archived_game, null);
		builder.setView(dialog_view);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d(TAG, "onClick: setPositiveButton");
				EditNameDialogListener activity = (EditNameDialogListener) getActivity();
				activity.onFinishedEditDialog(box_score_text.getText().toString());
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do nothing...for now
			}
		});
		
		box_score_text = (EditText) (dialog_view).findViewById(R.id.diag_frag_boxscore_content);
		box_score_text.setOnEditorActionListener(this);
		Bundle args = getArguments();
		if(args != null) {
			String saved_string = args.getString("saved_box_score_address");
			Log.d(TAG, "args SET! saved_string = " + saved_string);
			box_score_text.setText(args.getString("saved_box_score_address"));
		} else {
			Log.d(TAG, "args == null");
		}

		return builder.create();
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		Log.d(TAG, "onEditorAction");
		if(EditorInfo.IME_ACTION_DONE == actionId) {
			// Return the box score text to the activity
			Log.d(TAG, "onEditorAction: DONE");
			EditNameDialogListener activity = (EditNameDialogListener) getActivity();
			activity.onFinishedEditDialog(box_score_text.getText().toString());
			this.dismiss();
			return true;
		}
		return false;
	}
}