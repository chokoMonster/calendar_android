package de.hama.kalender.kalender.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hama.kalender.kalender.CategoryEnum;
import de.hama.kalender.kalender.adapter.EntryAdapter;
import de.hama.kalender.kalender.dialog.EntryDialog;
import de.hama.kalender.kalender.adapter.KalenderAdapter;
import de.hama.kalender.kalender.R;
import de.hama.kalender.kalender.entity.CalendarCollection;

public class KalenderActivity extends AppCompatActivity implements View.OnClickListener { //FragmentActivity {

    private List<CalendarCollection> entries = new ArrayList<>();
    private Calendar calendar = Calendar.getInstance();

    private GridView gridview;
    private Button btnPrevious, btnNext;
    private float positionX=1000;
    private List<String> days;

    private static String DATABASE = "my_database";
    private static String TABLE_ENTRIES = "entries";
    private static String COLUMN_ID = "id";
    private static String COLUMN_USER = "user";
    private static String COLUMN_DATE = "date";
    private static String COLUMN_TYPE = "type";
    private static String COLUMN_INTENSITY = "intensity";
    private static String COLUMN_START = "start";
    private static String COLUMN_END = "end";
    private static String COLUMN_LEAGUE = "league";
    private static String COLUMN_AGE = "age";
    private static String COLUMN_COACH = "coach";
    private static String COLUMN_NOTE = "note";
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalender);

        gridview = findViewById(R.id.gridview);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        setDate(0);

        //loadEntries();
        //createCalendar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuNew:
                startActivity(new Intent(KalenderActivity.this, NewEntryActivity.class));
                return true;
            case R.id.menuOverwriteData:
                AsyncOverwriteTableTask asyncOverwriteTableTask = new AsyncOverwriteTableTask();
                asyncOverwriteTableTask.execute();
                return true;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view==btnPrevious) {
            setDate(-1);
        } else if (view==btnNext) {
            setDate(1);
        }
    }

    private void loadEntries() {
        entries = new ArrayList<>();

        Date d=null, d1=null, d2=null, d3=null;
        try {
            d = new SimpleDateFormat("dd.MM.yyyy").parse("10.07.2018");
            d1 = new SimpleDateFormat("dd.MM.yyyy").parse("15.07.2018");
            d2 = new SimpleDateFormat("dd.MM.yyyy").parse("28.07.2018");
            d3 = new SimpleDateFormat("dd.MM.yyyy").parse("23.07.2018");
        } catch(ParseException e) {}

        CalendarCollection c = new CalendarCollection(1,  "gerkat", d);
        CalendarCollection c1 = new CalendarCollection(1, "test2",  d1);
        CalendarCollection c2 = new CalendarCollection(1,"gerkat", d1);
        CalendarCollection c3 = new CalendarCollection(1, "test2",  d);
        CalendarCollection c4 = new CalendarCollection(1, "test2",  d3);
        CalendarCollection c5 = new CalendarCollection(1, "test2",  d2);
        CalendarCollection c6 = new CalendarCollection(2, "test2",  d2);
        c.setType(CategoryEnum.BASKETBALL);
        c1.setType(CategoryEnum.SWIMMING);
        c2.setType(CategoryEnum.RUNNING);
        c3.setType(CategoryEnum.CYCLING);
        c4.setType(CategoryEnum.POWER);
        c5.setType(CategoryEnum.OTHER);
        c6.setType(CategoryEnum.GAME);
        c.setStart("11:00");
        c.setEnd("12:00");
        c1.setStart("10:00");
        c1.setEnd("11:30");
        c2.setStart("14:00");
        c2.setEnd("15:45");
        c3.setStart("11:00");
        c3.setEnd("12:00");
        c4.setStart("11:00");
        c4.setEnd("12:00");
        c5.setStart("10:00");
        c5.setEnd("11:30");
        c6.setStart("14:00");
        c6.setEnd("15:45");
        entries.add(c);
        entries.add(c1);
        entries.add(c2);
        entries.add(c3);
        entries.add(c4);
        entries.add(c5);
        entries.add(c6);


        //AsyncCalendarTask asyncCalendarTask = new AsyncCalendarTask();
        //asyncCalendarTask.execute("gerkat", calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.YEAR));

        /*try {
            asyncCalendarTask.execute("gerkat", calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.YEAR)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
    }

    public List<CalendarCollection> getEntries() {
        return entries;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createCalendar() {
        days = new ArrayList<>();
        days.addAll(Arrays.asList(getResources().getStringArray(R.array.weekdays)));

        int placeholder = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if(placeholder<0) {
            placeholder=6;
        }
        for (int i=0; i<placeholder; i++) {
            days.add("");
        }
        for (int i=1; i<=calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            days.add(Integer.toString(i));
        }

        gridview.setAdapter(new KalenderAdapter(this, days, entries));
        gridview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN) {
                    positionX=motionEvent.getX();
                    Toast.makeText(getApplicationContext(), "DOWN", Toast.LENGTH_SHORT).show();
                } else if(motionEvent.getAction()==MotionEvent.ACTION_UP) {
                    Toast.makeText(getApplicationContext(), "UP", Toast.LENGTH_SHORT).show();
                    if(motionEvent.getX()>(positionX+300)) {
                        setDate(-1);
                    } else if(motionEvent.getX()<(positionX-300)) {
                        setDate(1);
                    }
                }
                return false;
            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                int selectedDay = position - (4+calendar.get(Calendar.DAY_OF_WEEK));
                if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
                    selectedDay-=7;
                }
                List<CalendarCollection> selectedEntry = new ArrayList<>();
                for (CalendarCollection c: entries) {
                    //c.getDate().getDay()==selectedDay
                    String dayEntry = c.getFormattedDate().split("\\.")[0];
                    if(dayEntry.startsWith("0")) {
                        dayEntry = dayEntry.substring(1);
                    }
                    if (dayEntry.equals(Integer.toString(selectedDay))) {
                        selectedEntry.add(c);
                    }
                }
                showSelectedEntries(selectedEntry);
            }
        });
    }

    private void showSelectedEntries(final List<CalendarCollection> entries) {

        //ArrayAdapter<CalendarCollection> entryAdapter2 = new ArrayAdapter<>(getApplicationContext(), R.layout.list_item_entry, entries);

        ListView lstEntry = findViewById(R.id.lstEntry);
        EntryAdapter entryAdapter = new EntryAdapter(getApplicationContext(), entries);
        lstEntry.setAdapter(entryAdapter);

        lstEntry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), entries.get(i).getType().toString(), Toast.LENGTH_SHORT).show();

                EntryDialog dialog = new EntryDialog();
                Bundle args = new Bundle();
                args.putInt("test", i);
                args.putSerializable("entry", entries.get(i));
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), "ENTRY");
            }
        });
    }

    private void setDate(int value) {
        String[] months = getResources().getStringArray(R.array.arrayMonths);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + value;
        if(month<0) {
            month = 11;
            year--;
        } else if (month>11) {
            month = 0;
            year++;
        }
        calendar.set(year, month, 1);
        TextView lblMonth = findViewById(R.id.lblMonth);
        lblMonth.setText(String.format("%s %d", months[month], year));

        loadEntries();
        createCalendar();
        showSelectedEntries(new ArrayList<CalendarCollection>());
    }

    public List<CalendarCollection> getAllEntries() {
        List<CalendarCollection> collections = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ENTRIES + " WHERE " + COLUMN_USER + " = ? ORDER BY " + COLUMN_DATE + " DESC, " + COLUMN_ID + " ASC";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {"gerkat"});

        if (cursor.moveToFirst()) {
            do {
                try {
                    collections.add(new CalendarCollection(cursor.getInt(0), cursor.getString(1), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(2))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        return collections;
    }

    private void insertEntry(CalendarCollection collection) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, getNextId(collection.getFormattedDate(), collection.getUser()));
        values.put(COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(collection.getOriginalDate()));
        values.put(COLUMN_USER, collection.getUser());
        values.put(COLUMN_TYPE, collection.getType().getValue());
        values.put(COLUMN_INTENSITY, Integer.toString(collection.getIntensity()));
        values.put(COLUMN_START, collection.getStart());
        values.put(COLUMN_END, collection.getEnd());
        values.put(COLUMN_LEAGUE, collection.getLeague());
        values.put(COLUMN_AGE, collection.getAge());
        values.put(COLUMN_COACH, collection.getCoach());
        values.put(COLUMN_NOTE, collection.getNote());
        db.insert(TABLE_ENTRIES, null, values);
    }

    private int getNextId(String date, String user) {
        String selectQuery = "SELECT max(" + COLUMN_ID + ") FROM " + TABLE_ENTRIES + " WHERE " + COLUMN_DATE + " = ? AND " + COLUMN_USER + " = ?";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {date, user});
        cursor.moveToFirst();

        return cursor.getInt(0)+1;
    }

    private void deleteEntry(long id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(TABLE_ENTRIES, COLUMN_ID + " = ? AND " + COLUMN_DATE + " = ? AND " + COLUMN_USER + " = ?",
                new String[] {Integer.toString(entries.get((int) id).getId()), entries.get((int) id).getFormattedDate(), entries.get((int) id).getUser()});
        db.close();
        refreshList();
    }

    private void truncateTable() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(TABLE_ENTRIES, "1", null);
    }

    private void refreshList() {
        days = new ArrayList<>();
        days.addAll(Arrays.asList(getResources().getStringArray(R.array.weekdays)));

        int placeholder = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if(placeholder<0) {
            placeholder=6;
        }
        for (int i=0; i<placeholder; i++) {
            days.add("");
        }
        for (int i=1; i<=calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            days.add(Integer.toString(i));
        }
        gridview.setAdapter(new KalenderAdapter(this, days, getAllEntries()));
    }

    public class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //TODO
            String script = "CREATE TABLE " + TABLE_ENTRIES + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_DATE + " DATETIME PRIMARY KEY, "
                    + COLUMN_USER + " VARCHAR PRIMARY KEY, " + COLUMN_TYPE + " VARCHAR)";

            /*values.put(COLUMN_TYPE, collection.getType().getValue());
        values.put(COLUMN_INTENSITY, Integer.toString(collection.getIntensity()));
        values.put(COLUMN_START, collection.getStart());
        values.put(COLUMN_END, collection.getEnd());
        values.put(COLUMN_LEAGUE, collection.getLeague());
        values.put(COLUMN_AGE, collection.getAge());
        values.put(COLUMN_COACH, collection.getCoach());
        values.put(COLUMN_NOTE, collection.getNote());*/
            db.execSQL(script);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //db.execSQL("DROP TABLE IF EXISTS " + TABLE_GEAR);
            //onCreate(db);
        }
    }


    public class AsyncCalendarTask extends AsyncTask {

        private HttpURLConnection httpURLConnection;
        private BufferedReader bufferedReader;

        @Override
        protected void onPreExecute() {
            //progressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected JSONArray doInBackground(Object[] objects) {

            /*try {
                InetAddress.getByName("192.168.2.102").isReachable(2000);
            } catch (IOException e) {
                return null;
            }*/

            try {
                new Socket().connect(new InetSocketAddress("192.168.2.102", 80), 2000);
            } catch(IOException e) {
                return null;
            }

            String request="http://192.168.2.102/android/php/getEintraege.php?id="+objects[0]+"&monat="+objects[1]+"&jahr="+objects[2];
            try {
                URL url = new URL(request);
                httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream input = httpURLConnection.getInputStream();
                if(input==null) {
                    return null;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(input));
                String line, result="";
                while((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                return new JSONArray(result);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(httpURLConnection!=null) {
                    httpURLConnection.disconnect();
                }
                if(bufferedReader!=null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] progress) {
            //progressBar.setProgress((int) progress[0]);
        }

        @Override
        protected void onPostExecute(Object result) {
            List<CalendarCollection> lstJson = new ArrayList();

            try {
                JSONArray json = (JSONArray) result;
                for(int i=0; i<json.length(); i++) {
                    JSONObject jo = (JSONObject) json.get(i);
                    //TODO
                    CalendarCollection collection = new CalendarCollection(jo.getInt("ID"), jo.getString("BENUTZER"), new SimpleDateFormat("yyyy-MM-dd").parse(jo.getString("DATUM")));
                    collection.setType(CategoryEnum.valueOf(jo.getString("ART")));
                    collection.setNote(jo.getString("BEMERKUNG"));
                    if(collection.getType().equals("SPIEL")) {
                        //TODO Gegner
                    }
                    collection.setLeague(jo.getString("LIGA"));
                    collection.setAge(jo.getString("ALTERSKLASSE"));
                    collection.setCoach(jo.getString("TRAINER"));
                    collection.setStart(jo.getString("BEGINN"));
                    collection.setEnd(jo.getString("ENDE"));
                    collection.setIntensity(jo.getInt("INTENSITAET"));
                    lstJson.add(collection);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            entries = new ArrayList<>(lstJson);
        }
    }


    public class AsyncOverwriteTableTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            //progressBar.setVisibility(ProgressBar.VISIBLE);
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader;
            try {
                new Socket().connect(new InetSocketAddress("192.168.2.102", 80), 2000);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            String address = "http://192.168.2.102/android/php/getData.php";
            try {
                URL url = new URL(address);
                httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream input = httpURLConnection.getInputStream();
                if(input==null) {
                    return null;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(input));
                String line, result="";
                while((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                return new JSONArray(result);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            try {
                JSONArray json = (JSONArray) result;
                if(json!=null) {
                    truncateTable();
                }
                for(int i=0; i<json.length(); i++) {
                    JSONObject jo = (JSONObject) json.get(i);
                    CalendarCollection collection = new CalendarCollection(jo.getInt("id"), jo.getString("user"), new SimpleDateFormat("yyyy-MM-dd").parse(jo.getString("date")));

                    /*collection.setNote(jo.getString("comment"));
                    collection.setLeague(jo.getString("league"));
                    collection.setAge(jo.getString("age"));
                    collection.setCoach(jo.getString("coach"));
                    collection.setStart(jo.getString("start"));
                    collection.setEnd(jo.getString("end"));
                    collection.setType(CategoryEnum.valueOf(jo.getString("type")));
                    collection.setIntensity(jo.getInt("intensity"));*/

                    collection.setType(CategoryEnum.valueOf(jo.getString("KATEGORIE")));
                    collection.setNote(jo.getString("BEMERKUNG"));
                    if(collection.getType().equals("SPIEL")) {
                        //TODO Gegner
                    }
                    collection.setLeague(jo.getString("LIGA"));
                    collection.setAge(jo.getString("ALTERSKLASSE"));
                    collection.setCoach(jo.getString("TRAINER"));
                    collection.setStart(jo.getString("BEGINN"));
                    collection.setEnd(jo.getString("ENDE"));
                    collection.setIntensity(jo.getInt("INTENSITAET"));

                    insertEntry(collection);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //progressBar.setVisibility(ProgressBar.INVISIBLE);
            refreshList();
        }

        @Override
        protected void onProgressUpdate(Object[] progress) {
            //progressBar.setProgress((int) progress[0]);
        }
    }
}
