package com.APportfolio;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by averypozzobon on 2017-05-10.
 */
public class StocksAdapter extends ArrayAdapter<StocksRelay>{
    Context context;
    int layoutResourceId;
    ArrayList<StocksRelay> data = new ArrayList<StocksRelay>();

    public StocksAdapter(Context context, int layoutResourceId, ArrayList<StocksRelay> data) {
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
            holder.Symbol = (TextView)row.findViewById(com.APportfolio.R.id.symbol);
            holder.Price = (TextView)row.findViewById(com.APportfolio.R.id.currentprice);
            holder.PercentGainPrice = (TextView)row.findViewById(com.APportfolio.R.id.percentincrease);
            holder.Value = (TextView)row.findViewById(com.APportfolio.R.id.textView);
            holder.Gain = (TextView)row.findViewById(com.APportfolio.R.id.textView3);
            holder.GainPrice = (TextView)row.findViewById(com.APportfolio.R.id.textView4);
            holder.PercentGainValue = (TextView)row.findViewById(com.APportfolio.R.id.textView2);
            holder.Left = (ImageView)row.findViewById(com.APportfolio.R.id.increasedecrease);
            holder.Right = (ImageView)row.findViewById(com.APportfolio.R.id.increasedevalue);

            row.setTag(holder);
        }
        else
        {
            holder = (GamesHolder) row.getTag();
        }
        DecimalFormat formatter = new DecimalFormat("#,###,###.##");
        StocksRelay stocks = data.get(position);
        holder.Symbol.setText(stocks.symbol);
        holder.Price.setText(stocks.price);
        holder.PercentGainPrice.setText(stocks.percentgainprice);
        holder.Gain.setText(formatter.format(Double.parseDouble(stocks.gainvalue)));
        holder.PercentGainValue.setText(formatter.format(Double.parseDouble(stocks.gainpercent))+ "%");
        holder.Value.setText(formatter.format(Double.parseDouble(stocks.portfoliovalue)));
        holder.GainPrice.setText(stocks.gainprice);
        Drawable myIcon = context.getResources().getDrawable( com.APportfolio.R.drawable.down_triangle );
        Drawable myIcon1 = context.getResources().getDrawable( com.APportfolio.R.drawable.up_triangle );
        if (Double.parseDouble(stocks.percentgainprice.replace("%","")) < 0.00) {

            holder.Left.setBackground(myIcon);
            holder.Left.setScaleY(-1.0f);
        }else {
            holder.Left.setBackground(myIcon1);
        }
        if (Double.parseDouble(stocks.gainpercent.replace("%","")) < 0.00) {
            holder.Right.setBackground(myIcon);
            holder.Right.setScaleY(-1.0f);
        } else {
            holder.Right.setBackground(myIcon1);
        }
        return row;
    }

    static class GamesHolder
    {
        TextView Symbol;
        TextView Price;
        TextView PercentGainPrice;
        TextView Gain;
        TextView PercentGainValue;
        TextView Value;
        TextView GainPrice;
        ImageView Left;
        ImageView Right;
    }
}
