package de.hama.kalender.kalender;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import de.hama.kalender.kalender.activity.NewEntryActivity;

public class EntryDialog extends DialogFragment {

    private CalendarCollection entry;

    private EditText txtComment;
    private Button txtDateDisplay, txtStart, txtEnd, txtType, btnSave, btnCancel;
    private SeekBar sliderIntensity;

    public static final String KEY = "ENTRY";
    public static final int REQUEST = 1;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int test = getArguments().getInt("test");
        entry = (CalendarCollection) getArguments().getSerializable("entry");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.activity_new_entry, null);


        txtDateDisplay = view.findViewById(R.id.txtDateDisplay);
        txtStart = view.findViewById(R.id.txtStart);
        txtEnd = view.findViewById(R.id.txtEnd);
        txtType = view.findViewById(R.id.txtType);
        sliderIntensity = view.findViewById(R.id.sliderIntensity);
        txtComment = view.findViewById(R.id.txtComment);

        txtDateDisplay.setText(entry.getFormattedDate());
        txtStart.setText(entry.getStart());
        txtEnd.setText(entry.getEnd());
        txtType.setText(entry.getType().getValue());
        //sliderIntensity.setProgress(entry.getIntensity());
        txtComment.setText(entry.getComment());

        btnSave = view.findViewById(R.id.btnSave);
        /*btnSave.setText("Bearbeiten");
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        btnSave.setVisibility(View.INVISIBLE);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnCancel.setVisibility(View.INVISIBLE);
        /*btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EntryDialog.this.getDialog().cancel();
            }
        });*/

        builder.setView(view)
                .setTitle(entry.getType().getValue())
                .setIcon(getContext().getResources().getIdentifier(entry.getType().toString().toLowerCase(), "mipmap", getContext().getPackageName()))
                .setPositiveButton("Bearbeiten", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //startActivity(new Intent(getActivity(), NewEntryActivity.class));
                        Intent intent = new Intent(getActivity(), NewEntryActivity.class);
                        intent.putExtra(KEY, entry);
                        startActivityForResult(intent, REQUEST);
                    }
                })
                .setNegativeButton("Zurück", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EntryDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
