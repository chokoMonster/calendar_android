package de.hama.kalender.kalender;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewEntryActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtDateDisplay;
    private Calendar calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        calendar = Calendar.getInstance();

        txtDateDisplay = findViewById(R.id.txtDateDisplay);
        //txtDateDisplay.setEnabled(false);
        txtDateDisplay.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(calendar.getTime()));
        txtDateDisplay.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view==txtDateDisplay) {//android.R.style.Theme_Holo_Light_Dialog_MinWidth
            new DatePickerDialog(NewEntryActivity.this, AlertDialog.THEME_HOLO_DARK, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
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
}
