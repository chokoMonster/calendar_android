package de.hama.kalender.kalender.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
import java.util.Calendar;
import java.util.List;

import de.hama.kalender.kalender.R;
import de.hama.kalender.kalender.entity.Gear;

public class MountainStatisticsActivity extends AppCompatActivity {

    private ListView lstMountain;
    private ProgressBar progressBar;
    private DatabaseHelper databaseHelper;
    private Calendar calendar;

    private List<Gear> gearList;

    private static String DATABASE = "my_database";
    private static String TABLE_GEAR = "gear_statistics";
    private static String COLUMN_ID = "id";
    private static String COLUMN_DATE = "date";
    private static String COLUMN_GEAR = "gear";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mountain_statistics);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        calendar = Calendar.getInstance();

        lstMountain = findViewById(R.id.lstMountain);
        registerForContextMenu(lstMountain);

        progressBar = findViewById(R.id.progressBarGear);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        refreshList();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_mountain, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menuDelete:
                deleteEntry(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mountain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuNewMountain:
                new DatePickerDialog(MountainStatisticsActivity.this, AlertDialog.THEME_HOLO_DARK, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.menuGraphic:
                TimeSeries series = new TimeSeries("Gear course - Marienhöhe");
                for (int i = 0; i < gearList.size(); i++) {
                    series.add(i + 1, gearList.get(i).getGear());
                }

                XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                dataset.addSeries(series);

                XYSeriesRenderer renderer = new XYSeriesRenderer();
                renderer.setLineWidth(10);
                renderer.setFillPoints(true);
                renderer.setColor(Color.RED);
                renderer.setPointStyle(PointStyle.CIRCLE);

                XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
                for (int i = 0; i < gearList.size(); i++) {
                    mRenderer.addXTextLabel(i + 1, new SimpleDateFormat("dd.MM.yyyy").format(gearList.get(i).getDate()));
                }
                //mRenderer.setXLabels(0);
                mRenderer.setLabelsTextSize(25);
                mRenderer.setXLabelsAngle((float) 0.5);
                //mRenderer.setXLabelsColor(R.color.Green);
                mRenderer.setLabelsColor(R.color.Black);

                mRenderer.addSeriesRenderer(renderer);
                mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));

                mRenderer.setYAxisMax(27);
                mRenderer.setYAxisMin(0);
                mRenderer.setXTitle("Datum");
                mRenderer.setYTitle("Gang");
                mRenderer.setAxisTitleTextSize(30);

                mRenderer.setChartTitle("Marienhöhe - Gangverlauf");
                mRenderer.setChartTitleTextSize(40);

                mRenderer.setShowLegend(false);
                mRenderer.setShowAxes(true);
                mRenderer.setShowGrid(true);

                Intent intent = ChartFactory.getLineChartIntent(getBaseContext(), dataset, mRenderer);
                startActivity(intent);
                break;
            case R.id.menuSynchronizeGear:
                AsyncSynchronizeGearTask asyncSynchronizeGearTask = new AsyncSynchronizeGearTask();
                asyncSynchronizeGearTask .execute();
                break;
            case R.id.menuOverwriteGear:
                AsyncOverwriteTableTask asyncOverwriteTableTask = new AsyncOverwriteTableTask();
                asyncOverwriteTableTask.execute();
                break;
        }
        return true;
    }

    public List<Gear> getAllEntries() {
        List<Gear> gearList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_GEAR + " ORDER BY " + COLUMN_DATE + " DESC";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    gearList.add(new Gear(cursor.getInt(0), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(1)), cursor.getInt(2)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        return gearList;
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            final Gear gear = new Gear();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            gear.setDate(calendar.getTime());

            final GearDialog gearDialog = new GearDialog();
            gearDialog.setListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    gear.setGear(gearDialog.getNumberPicker().getValue());
                    insertEntry(gear);
                    refreshList();
                    gearDialog.getDialog().cancel();
                }
            });
            gearDialog.show(getSupportFragmentManager(), "GEAR");
        }
    };

    private void insertEntry(Gear gear) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, getNextId());
        values.put(COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(gear.getDate()));
        values.put(COLUMN_GEAR, gear.getGear());
        db.insert(TABLE_GEAR, null, values);
    }

    private int getNextId() {
        String selectQuery = "SELECT max(" + COLUMN_ID + ") FROM " + TABLE_GEAR;

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        return cursor.getInt(0)+1;
    }

    private void deleteEntry(long id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(TABLE_GEAR, COLUMN_ID + " = ?", new String[] {Integer.toString(gearList.get((int) id).getId())});
        db.close();
        refreshList();
    }

    private void truncateTable() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(TABLE_GEAR, "1", null);
    }

    private void refreshList() {
        gearList = getAllEntries();
        List<String> list = new ArrayList<>();
        for (Gear gear : gearList) {
            list.add(new SimpleDateFormat("dd.MM.yyyy").format(gear.getDate()) + "\t\t\t\t\t\t\t" + gear.getGear());
        }
        ArrayAdapter<String> adapterGear = new ArrayAdapter<>(getApplicationContext(), R.layout.list_item_mountain,
                R.id.list_item_mountain_date_gear, list);
        lstMountain.setAdapter(adapterGear);
    }

    public class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String script = "CREATE TABLE " + TABLE_GEAR + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_DATE + " DATETIME,"
                    + COLUMN_GEAR + " INTEGER)";
            db.execSQL(script);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //db.execSQL("DROP TABLE IF EXISTS " + TABLE_GEAR);
            //onCreate(db);
        }
    }

    public static class GearDialog extends DialogFragment {
        private DialogInterface.OnClickListener onClickListener;
        private NumberPicker picker;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_number, null);

            builder.setView(view)
                    .setTitle("Gang")
                    .setPositiveButton("OK", onClickListener);

            picker = view.findViewById(R.id.number_picker);
            picker.setMinValue(1);
            picker.setMaxValue(27);
            picker.setWrapSelectorWheel(false);

            return builder.create();
        }

        private void setListener(DialogInterface.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        private NumberPicker getNumberPicker() {
            return picker;
        }
    }

    public class AsyncSynchronizeGearTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            HttpURLConnection httpURLConnection = null;
            try {
                new Socket().connect(new InetSocketAddress("192.168.2.102", 80), 2000);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            String address = "http://192.168.2.102/android/php/synchronizeGear.php";
            try {
                URL url = new URL(address);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                String json = new Gson().toJson(gearList);
                DataOutputStream os = new DataOutputStream(httpURLConnection.getOutputStream());
                os.writeBytes(json);
                os.flush();
                os.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }
    }

    public class AsyncOverwriteTableTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(ProgressBar.VISIBLE);
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

            String address = "http://192.168.2.102/android/php/getGear.php";
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
                    Gear gear = new Gear(jo.getInt("id"), new SimpleDateFormat("yyyy-MM-dd").parse(jo.getString("date")), jo.getInt("gear"));
                    insertEntry(gear);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            refreshList();
        }

        @Override
        protected void onProgressUpdate(Object[] progress) {
            progressBar.setProgress((int) progress[0]);
        }
    }
}
