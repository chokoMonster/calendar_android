package de.hama.kalender.kalender.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import de.hama.kalender.kalender.CalendarCollection;
import de.hama.kalender.kalender.R;

public class KalenderAdapter extends BaseAdapter {

    private  Context context;
    private List<String> days, weekdays;
    private List<CalendarCollection> entries;

    public KalenderAdapter(Context context, List<String> days, List<CalendarCollection> entries) {
        this.context=context;
        this.days=days;
        this.entries=entries;
        weekdays = Arrays.asList(context.getResources().getStringArray(R.array.weekdays));
    }

    @Override
    public int getCount() {
        return days.size();
    }

    @Override
    public Object getItem(int i) {
        return days.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView;
        if (view == null) {
            textView = new TextView(context);
            textView.setLayoutParams(new ViewGroup.LayoutParams(85, 85));
            textView.setTextSize(14);
            textView.setPadding(25, 5, 5, 5);
        } else {
            textView = (TextView) view;
        }

        if(weekdays.contains(days.get(i))) {
            textView.setTextColor(context.getResources().getColor(R.color.Black));
        } else if(days.get(i)!="") {
            for (CalendarCollection c: entries) {
                //c.getDate().split("\\.")[0].equals(days.get(i))
                String dayEntry = c.getFormattedDate().split("\\.")[0];
                if(dayEntry.startsWith("0")) {
                    dayEntry = dayEntry.substring(1);
                }
                if (dayEntry.equals(days.get(i))) {
                    //ContextCompat.getColor(context, R.color.OrangeRed)
                    textView.setTextColor(context.getResources().getColor(R.color.OrangeRed));
                    break;
                } else {
                    textView.setTextColor(context.getResources().getColor(R.color.DarkBlue));
                }
            }
        }

        textView.setText(days.get(i));
        return textView;
    }
}
