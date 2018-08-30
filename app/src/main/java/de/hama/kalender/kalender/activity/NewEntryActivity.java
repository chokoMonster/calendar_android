package de.hama.kalender.kalender.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TimePicker;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.hama.kalender.kalender.CategoryEnum;
import de.hama.kalender.kalender.dialog.EntryDialog;
import de.hama.kalender.kalender.R;
import de.hama.kalender.kalender.entity.CalendarCollection;

public class NewEntryActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtComment;
    private Button txtDateDisplay, txtStart, txtEnd, txtType, btnSave, btnCancel;
    private SeekBar sliderIntensity;
    private Calendar calendar;
    private boolean start;
    private CalendarCollection entry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        calendar = Calendar.getInstance();

        txtDateDisplay = findViewById(R.id.txtDateDisplay);
        txtDateDisplay.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(calendar.getTime()));
        txtDateDisplay.setOnClickListener(this);

        txtStart = findViewById(R.id.txtStart);
        txtEnd = findViewById(R.id.txtEnd);
        txtStart.setOnClickListener(this);
        txtEnd.setOnClickListener(this);

        txtType = findViewById(R.id.txtType);
        txtType.setOnClickListener(this);

        sliderIntensity = findViewById(R.id.sliderIntensity);
        txtComment = findViewById(R.id.txtComment);

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        try {
            entry = (CalendarCollection) getIntent().getExtras().get(EntryDialog.KEY);
            if (entry != null) {
                setValues();
            }
        } catch(NullPointerException e) {}
    }

    @Override
    public void onClick(View view) {
        if(view==txtDateDisplay) {//android.R.style.Theme_Holo_Light_Dialog_MinWidth
            new DatePickerDialog(NewEntryActivity.this, AlertDialog.THEME_HOLO_DARK, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        } else if(view==txtStart) {
            start=true;
            new TimePickerDialog(NewEntryActivity.this, AlertDialog.THEME_HOLO_DARK, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        } else if(view==txtEnd) {
            start=false;
            new TimePickerDialog(NewEntryActivity.this, AlertDialog.THEME_HOLO_DARK, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        } else if(view==txtType) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NewEntryActivity.this).setTitle("Kategorie wählen").setCancelable(false);
            final String[] categories = getResources().getStringArray(R.array.arrayTypes);
            dialogBuilder.setItems(categories, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    //btnType.setText(Arrays.asList(getResources().getStringArray(R.array.arrayTankstellen)).get(i));
                    txtType.setText(categories[i]);
                }
            });
            dialogBuilder.create().show();
        } else if(view==btnSave) {
            saveEntry();
        } else if(view==btnCancel) {
            finish();
        }
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            txtDateDisplay.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(calendar.getTime()));
        }
    };

    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            String min = minute<10 ? "0"+minute : Integer.toString(minute);
            String h = hour<10 ? "0"+hour : Integer.toString(hour);
            if(start) {
                txtStart.setText(h+":"+min);
            } else {
                txtEnd.setText(h+":"+min);
            }
        }
    };

    private void saveEntry() {
        CalendarCollection entry = new CalendarCollection(1,"gerkat", calendar.getTime());
        for (CategoryEnum e: CategoryEnum.values()) {
            if(e.getValue().equals(txtType.getText().toString())) {
                entry.setType(e);
            }
        }
        entry.setStart(txtStart.getText().toString());
        entry.setEnd(txtEnd.getText().toString());
        entry.setIntensity(sliderIntensity.getProgress());
        entry.setNote(txtComment.getText().toString());

        Gson gson = new Gson();
        String json = gson.toJson(entry);
    }

    private void setValues() {
        txtDateDisplay.setText(entry.getFormattedDate());
        txtStart.setText(entry.getStart());
        txtEnd.setText(entry.getEnd());
        txtType.setText(entry.getType().getValue());
        //sliderIntensity.setProgress(entry.getIntensity());
        txtComment.setText(entry.getNote());
    }
}
