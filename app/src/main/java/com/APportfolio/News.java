package com.APportfolio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class News extends AppCompatActivity {
    ListView Stocks;
    private String finalUrl="http://articlefeeds.nasdaq.com/nasdaq/categories?category=Business&format=xml";
    private HandleXML obj;
    TextView title;
    ArrayList title1 = new ArrayList();
    private ProgressDialog pdia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.APportfolio.R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(com.APportfolio.R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageButton main = (ImageButton) findViewById(com.APportfolio.R.id.stocks);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent news = new Intent(News.this,MainActivity.class);
                startActivity(news);
                News.this.overridePendingTransition(0, 0);
            }
        });
        Stocks = (ListView) findViewById(com.APportfolio.R.id.StockValues);
        Stocks.setOnTouchListener(new OnSwipeTouchListener(News.this) {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
                Intent intent = new Intent(News.this, MainActivity.class);
                startActivity(intent);
                News.this.overridePendingTransition(com.APportfolio.R.transition.anim_slide_in_right, com.APportfolio.R.transition.anim_slide_out_right);
            }
            public void onSwipeLeft() {
                Intent intent = new Intent(News.this, Search.class);
                startActivity(intent);
                News.this.overridePendingTransition(com.APportfolio.R.transition.anim_slide_in_left, com.APportfolio.R.transition.anim_slide_out_left);
            }
            public void onSwipeBottom() {
            }

        });
        ImageButton search = (ImageButton) findViewById(com.APportfolio.R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(News.this, Search.class);
                startActivity(search);
                News.this.overridePendingTransition(0, 0);
            }
        });
        TextView title = (TextView) findViewById(com.APportfolio.R.id.editText);
        GetTrue g = new GetTrue();
        g.execute();
    }
    private class GetTrue extends AsyncTask<TextView, Void, ArrayList> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pdia = new ProgressDialog(News.this);
            pdia.setMessage("Loading...");
            pdia.show();
        }
        @Override
        protected ArrayList doInBackground(TextView... title) {
            Document doc = null;
            Document doc1 = null;
            Document doc2 = null;
            ArrayList value = new ArrayList();
            try {
                doc1 = Jsoup.connect("http://www.cnbc.com/id/10001147/device/rss/rss.xml").get();
                doc = Jsoup.connect("http://syndication.cbc.ca/partnerrss/business.xml").get();
                doc2 = Jsoup.connect("http://rss.cnn.com/rss/money_latest.rss.xml").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("String", doc1.html());
            for (int i=1; i < doc1.select("title").size(); i++){
                value.add(new NewsRelay(doc1.select("title").get(i).text(),doc1.select("link").get(i).text(),doc1.select("description").get(i).text()));
            }
            for (int i=2; i<doc.select("title").size()-1; i++){
                value.add(new NewsRelay(doc.select("title").get(i).text(),doc.select("link").get(i).text(),doc.select("description").get(i).text().replace("<p>","").replace(" </p>","")));
            }
            for (int i=2; i<doc2.select("title").size()-1; i++){
                value.add(new NewsRelay(doc2.select("title").get(i).text(),doc2.select("link").get(i).text(),doc2.select("description").get(i).text().substring(0,doc2.select("description").get(i).text().indexOf("img")).replace("<","")));
            }
            long seed = System.nanoTime();
            Collections.shuffle(value, new Random(seed));
            return value;
        }
        @Override
        protected void onPostExecute(ArrayList result) {
            title1 = result;
            NewsAdapter news = new NewsAdapter(News.this, com.APportfolio.R.layout.news_row,title1);
            Stocks = (ListView) findViewById(com.APportfolio.R.id.StockValues);
            Stocks.setAdapter(news);
            pdia.dismiss();
        }
    }
}