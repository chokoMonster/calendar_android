package de.hama.kalender.kalender.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hama.kalender.kalender.entity.CalendarCollection;

public class CalendarDatabaseHandler {

    private static String DATABASE = "my_database";
    private static String TABLE = "entries";
    private static String COLUMN_ID = "id";
    private static String COLUMN_USER = "user";
    private static String COLUMN_DATE = "date";
    private static String COLUMN_TYPE = "type";
    private static String COLUMN_INTENSITY = "intensity";
    private static String COLUMN_START = "start";
    private static String COLUMN_END = "ending";
    private static String COLUMN_LEAGUE = "league";
    private static String COLUMN_AGE = "age";
    private static String COLUMN_COACH = "coach";
    private static String COLUMN_OPPONENT = "opponent";
    private static String COLUMN_NOTE = "note";

    private DatabaseHelper databaseHelper;


    public CalendarDatabaseHandler(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public List<CalendarCollection> getAllEntries() {
        List<CalendarCollection> collections = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + COLUMN_USER + " = ? ORDER BY " + COLUMN_DATE + " DESC, " + COLUMN_ID + " ASC";

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

    public void insertEntry(CalendarCollection collection) {
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
        values.put(COLUMN_OPPONENT, collection.getOpponent());
        values.put(COLUMN_NOTE, collection.getNote());
        db.insert(TABLE, null, values);
    }

    public int getNextId(String date, String user) {
        String selectQuery = "SELECT max(" + COLUMN_ID + ") FROM " + TABLE + " WHERE " + COLUMN_DATE + " = ? AND " + COLUMN_USER + " = ?";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {date, user});
        cursor.moveToFirst();

        return cursor.getInt(0)+1;
    }

    public void deleteEntry(CalendarCollection entry) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(TABLE, COLUMN_ID + " = ? AND " + COLUMN_DATE + " = ? AND " + COLUMN_USER + " = ?",
                new String[] {Integer.toString(entry.getId()), entry.getFormattedDate(), entry.getUser()});
        db.close();
    }

    public void truncateTable() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(TABLE, "1", null);
    }


    public class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //TODO
            String script = "CREATE TABLE " + TABLE + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_DATE + " DATETIME PRIMARY KEY, " + COLUMN_USER + " TEXT PRIMARY KEY, "
                    + COLUMN_TYPE + " TEXT, " + COLUMN_INTENSITY + " INTEGER, " + COLUMN_START + " TEXT, " + COLUMN_END + " TEXT, "
                    + COLUMN_LEAGUE + " TEXT, " + COLUMN_AGE + " TEXT, " + COLUMN_COACH + " TEXT, " + COLUMN_OPPONENT + " TEXT, " + COLUMN_NOTE + " TEXT)";

            db.execSQL(script);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //db.execSQL("DROP TABLE IF EXISTS " + TABLE_GEAR);
            //onCreate(db);
        }
    }
}
