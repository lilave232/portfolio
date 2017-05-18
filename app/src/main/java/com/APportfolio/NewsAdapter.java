package com.APportfolio;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by averypozzobon on 2017-05-10.
 */
public class NewsAdapter extends ArrayAdapter<NewsRelay>{
    Context context;
    int layoutResourceId;
    ArrayList<NewsRelay> data = new ArrayList<NewsRelay>();

    public NewsAdapter(Context context, int layoutResourceId, ArrayList<NewsRelay> data) {
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
            holder.title = (TextView)row.findViewById(com.APportfolio.R.id.textView8);
            holder.description = (TextView)row.findViewById(com.APportfolio.R.id.textView9);

            row.setTag(holder);
        }
        else
        {
            holder = (GamesHolder) row.getTag();
        }
        final NewsRelay stocks = data.get(position);
        holder.description.setText(Html.fromHtml(stocks.description));
        holder.title.setClickable(true);
        holder.title.setText(Html.fromHtml("<a href='" + stocks.link + "'>" + stocks.title + "</a>"));
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlString= stocks.link;
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed so allow user to choose instead
                    intent.setPackage(null);
                    context.startActivity(intent);
                }
            }
        });
        return row;
    }

    static class GamesHolder
    {
        TextView title;
        TextView description;
    }
}
