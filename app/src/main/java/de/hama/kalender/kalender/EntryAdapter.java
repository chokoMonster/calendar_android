package de.hama.kalender.kalender;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class EntryAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<CalendarCollection> entries;

    public EntryAdapter(Context context, List<CalendarCollection> entries) {
        this.context=context;
        this.entries=entries;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int i) {
        return entries.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_entry, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = view.findViewById(R.id.imageView);
            viewHolder.lblTime = view.findViewById(R.id.lblTime);
            viewHolder.lblDescription = view.findViewById(R.id.lblDescription);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        CalendarCollection entry = entries.get(i);
        viewHolder.lblTime.setText(String.format("%s - %s", entry.getStart(), entry.getEnd()));
        viewHolder.lblDescription.setText(entry.getComment());

        if(entry.getType()!=null) {
            viewHolder.imageView.setImageResource(this.getMipmapIdByName(entry.getType()));
        } else {
            viewHolder.imageView.setImageBitmap(null);
        }
        return view;
    }

    private int getMipmapIdByName(String value)  {
        String packageName = context.getPackageName();
        return context.getResources().getIdentifier(value, "mipmap", packageName);
    }

    static class ViewHolder {
        ImageView imageView;
        TextView lblTime;
        TextView lblDescription;
    }
}
