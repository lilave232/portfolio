package com.APportfolio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Search extends AppCompatActivity {
    ListView relative;
    ArrayList<String> arraylist;
    ArrayList<InfoRelay> arrayInfo = new ArrayList<InfoRelay>();
    ListView Info;
    private ProgressDialog pdia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.APportfolio.R.layout.activity_search);
        relative = (ListView) findViewById(com.APportfolio.R.id.listView);
        relative.setOnTouchListener(new OnSwipeTouchListener(Search.this) {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
                Intent intent = new Intent(Search.this, News.class);
                startActivity(intent);
                Search.this.overridePendingTransition(com.APportfolio.R.transition.anim_slide_in_right, com.APportfolio.R.transition.anim_slide_out_right);
            }
            public void onSwipeLeft() {
            }
            public void onSwipeBottom() {
            }

        });
        ImageButton main = (ImageButton) findViewById(com.APportfolio.R.id.stocks);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(Search.this,MainActivity.class);
                startActivity(main);
                Search.this.overridePendingTransition(0, 0);
            }
        });
        ImageButton news = (ImageButton) findViewById(com.APportfolio.R.id.news);
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(Search.this,News.class);
                startActivity(search);
                Search.this.overridePendingTransition(0, 0);
            }
        });
        //Getting the instance of AutoCompleteTextView
        final AutoCompleteTextView actv= (AutoCompleteTextView)findViewById(com.APportfolio.R.id.autoCompleteTextView1);
        actv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(actv.length() == 1) {
                    arraylist = new ArrayList<String>();
                    DownloadJSON jsonresults = new DownloadJSON();
                    jsonresults.execute(actv.getText().toString());
                } else if (actv.length() > 1) {
                    DownloadJSON jsonresults = new DownloadJSON();
                    jsonresults.execute(actv.getText().toString());
                } else {
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ImageButton search = (ImageButton) findViewById(com.APportfolio.R.id.imageButton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetTrue getstockexists = new GetTrue();
                if (actv.getText().toString().contains(" ")){
                    String ticker = actv.getText().toString().substring(0,actv.getText().toString().indexOf(" "));
                    getstockexists.execute(ticker);
                } else {
                    String ticker = actv.getText().toString();
                    getstockexists.execute(ticker);
                }
            }
        });
    }

    private class DownloadJSON extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            // Create the array
            // YQL JSON URL
            String url = "http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=" + params[0] + "&region=1&lang=en&callback=";
            arraylist = new ArrayList<String>();
            try {
                // Retrive JSON Objects from the given URL in JSONfunctions.class
                JSONObject json_data = JSONfunctions.getJSONfromURL(url);
                if (json_data == null) {

                } else {
                    JSONObject json_query = json_data.getJSONObject("ResultSet");
                    JSONArray json_results = json_query.getJSONArray("Result");
                    if (json_data == null | json_query == null | json_results == null) {

                    } else {
                        for (int i = 0; i < json_results.length(); i++) {
                            JSONObject c = json_results.getJSONObject(i);
                            arraylist.add(c.getString("symbol") + " | " + c.getString("name"));
                        }
                        Log.e("ArrayList", arraylist.toString());
                    }
                }
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (Search.this, com.APportfolio.R.layout.support_simple_spinner_dropdown_item,arraylist);
            AutoCompleteTextView actv= (AutoCompleteTextView)findViewById(com.APportfolio.R.id.autoCompleteTextView1);
            actv.setThreshold(1);//will start working from first character
            actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        }
    }
    private class GetTrue extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected ArrayList<String> doInBackground(String... urls) {
            Document doc = null;
            Document doc1 = null;
            ArrayList<String> value = new ArrayList<String>();
            try {
                doc1 = Jsoup.connect("http://www.marketwatch.com/investing/stock/" + urls[0]).get();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (doc1.select("span.company__ticker").text().contains(urls[0])){
                value.add("true");
                value.add(urls[0]);
            } else {
                value.add("false");
            }
            return value;
        }
        @Override
        protected void onPostExecute(ArrayList result) {
            if (result.get(0) == "true") {
                GetStockData getdata = new GetStockData();
                getdata.execute(result.get(1).toString());
            } else {
                Toast.makeText(Search.this, "Cannot Find Ticker", Toast.LENGTH_LONG).show();
            }
        }
    }
    private class GetStockData extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected ArrayList<String> doInBackground(String... urls) {
            Document doc = null;
            Document doc1 = null;
            ArrayList<String> stock_data1 = new ArrayList<String>();
            try {
                doc1 = Jsoup.connect("http://www.nasdaq.com/symbol/" + urls[0]).get();

            } catch (IOException e) {
                e.printStackTrace();
            }
            String symbol = urls[0];
            String value = doc1.select("div#qwidget_lastsale.qwidget-dollar").first().text();
            String gainvalue = doc1.select("div#qwidget_netchange.qwidget-cents").get(0).text();
            String gainvalue1 = doc1.select("div#qwidget_percent.qwidget-percent").get(0).text() + "%";
            String company = doc1.select("h1").get(0).text().substring(0,doc1.select("h1").get(0).text().indexOf("Common Stock"));
            stock_data1.add(symbol);
            stock_data1.add(company);
            stock_data1.add(value);
            stock_data1.add(gainvalue);
            stock_data1.add(gainvalue1);
            return stock_data1;
        }
        @Override
        protected void onPostExecute(ArrayList<String> result) {
            TextView symbol1 = (TextView) findViewById(com.APportfolio.R.id.textView11);
            TextView company1 = (TextView) findViewById(com.APportfolio.R.id.textView12);
            TextView price = (TextView) findViewById(com.APportfolio.R.id.textView13);
            TextView gainpercentvalue = (TextView) findViewById(com.APportfolio.R.id.textView14);
            symbol1.setText(result.get(0));
            company1.setText(result.get(1));
            price.setText(result.get(2));
            if (Double.parseDouble(result.get(3).toString()) > 0) {
                gainpercentvalue.setTextColor(Color.GREEN);
                gainpercentvalue.setText(result.get(3) + " | " + result.get(4));
            } else {
                gainpercentvalue.setTextColor(Color.RED);
                gainpercentvalue.setText(result.get(3) + " | " + result.get(4));
            }
            GetStockInfo getinfo = new GetStockInfo();
            getinfo.execute(result.get(0).toString());
        }
    }
    private class GetStockInfo extends AsyncTask<String, Void, ArrayList<InfoRelay>> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pdia = new ProgressDialog(Search.this);
            pdia.setMessage("Loading...");
            pdia.show();
        }
        @Override
        protected ArrayList<InfoRelay> doInBackground(String... urls) {
            Document doc = null;
            ArrayList<InfoRelay> arrayInfo = new ArrayList<InfoRelay>();
            try {
                doc = Jsoup.connect("http://www.nasdaq.com/symbol/" + urls[0]).get();

            } catch (IOException e) {
                e.printStackTrace();
            }
            Element table1 = doc.select("table#quotes_content_left_InfoQuotesResults.widthF").get(0); //select the first table.
            Element table = table1.select("table").get(0);
            Elements rows = table.select("tr");
            String HighLowTitle = "High/Low";
            String HighLow = rows.get(3).select("td").get(1).text();
            arrayInfo.add(new InfoRelay(HighLowTitle,HighLow));
            String VolumeTitle = "Volume";
            String Volume = rows.get(4).select("td").get(1).text();
            arrayInfo.add(new InfoRelay(VolumeTitle,Volume));
            String PreviousTitle = "Previous Close";
            String Previous = rows.get(6).select("td").get(1).text();
            arrayInfo.add(new InfoRelay(PreviousTitle,Previous));
            String weektitle = "52 Week High/Low";
            String week = rows.get(7).select("td").get(1).text();
            arrayInfo.add(new InfoRelay(weektitle,week));
            String captitle = "Market Cap";
            String cap = rows.get(8).select("td").get(1).text();
            arrayInfo.add(new InfoRelay(captitle,cap));
            String PEtitle = "Price Earnings Ratio (P/E)";
            String PE = rows.get(9).select("td").get(1).text();
            arrayInfo.add(new InfoRelay(PEtitle,PE));
            String EPStitle = "Earnings Per Share (EPS)";
            String EPS = rows.get(11).select("td").get(1).text();
            arrayInfo.add(new InfoRelay(EPStitle,EPS));
            String yieldtitle = "Current Yield";
            String yield = rows.get(15).select("td").get(1).text();
            arrayInfo.add(new InfoRelay(yieldtitle,yield));
            return arrayInfo;
        }
        @Override
        protected void onPostExecute(ArrayList result) {
            InfoAdapter info = new InfoAdapter(Search.this, com.APportfolio.R.layout.info_row,result);
            Info = (ListView) findViewById(com.APportfolio.R.id.listView);
            Info.setAdapter(info);
            pdia.dismiss();
        }
    }
}
