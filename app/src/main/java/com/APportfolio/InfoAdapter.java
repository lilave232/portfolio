package com.APportfolio;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by averypozzobon on 2017-05-10.
 */
public class InfoAdapter extends ArrayAdapter<InfoRelay>{
    Context context;
    int layoutResourceId;
    ArrayList<InfoRelay> data = new ArrayList<InfoRelay>();

    public InfoAdapter(Context context, int layoutResourceId, ArrayList<InfoRelay> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        GamesHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new GamesHolder();
            holder.title = (TextView)row.findViewById(com.APportfolio.R.id.title);
            holder.data = (TextView)row.findViewById(com.APportfolio.R.id.data);

            row.setTag(holder);
        }
        else
        {
            holder = (GamesHolder) row.getTag();
        }
        InfoRelay stocks = data.get(position);
        holder.title.setText(stocks.title);
        holder.data.setText(stocks.data);
        return row;
    }

    static class GamesHolder
    {
        TextView title;
        TextView data;
    }
}
