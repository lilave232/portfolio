package com.APportfolio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView Stocks;
    private ProgressDialog pdia;
    Context context;
    Boolean value = false;
    private ArrayList<StocksRelay> stock_data = new ArrayList<StocksRelay>();
    ArrayList<StocksRelay> stock_data1 = new ArrayList<StocksRelay>();
    DatabaseHandler db;
    ArrayList arraylist;
    View dialogView;
    private static final int NOTIFICATION_ID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.APportfolio.R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(com.APportfolio.R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DatabaseHandler(MainActivity.this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(com.APportfolio.R.id.fab);
        Stocks = (ListView) findViewById(com.APportfolio.R.id.StockValues);
        Stocks.setEmptyView(findViewById(com.APportfolio.R.id.EmptyText));
        Stocks.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
            }
            public void onSwipeLeft() {
                Intent intent = new Intent(MainActivity.this, News.class);
                startActivity(intent);
                MainActivity.this.overridePendingTransition(com.APportfolio.R.transition.anim_slide_in_left, com.APportfolio.R.transition.anim_slide_out_left);
            }
            public void onSwipeBottom() {
            }

        });
        ImageButton news = (ImageButton) findViewById(com.APportfolio.R.id.news);
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent news = new Intent(MainActivity.this,News.class);
                startActivity(news);
                MainActivity.this.overridePendingTransition(0, 0);
            }
        });
        ImageButton search = (ImageButton) findViewById(com.APportfolio.R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(MainActivity.this, Search.class);
                startActivity(search);
                MainActivity.this.overridePendingTransition(0, 0);
            }
        });
        final StocksAdapter adapter = new StocksAdapter(MainActivity.this,
                com.APportfolio.R.layout.stock_row, stock_data1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(com.APportfolio.R.layout.add_stock_quote, null);
                dialogBuilder.setView(dialogView);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();
                final AutoCompleteTextView Ticker = (AutoCompleteTextView) dialogView.findViewById(com.APportfolio.R.id.editText);
                Ticker.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(Ticker.length() == 2) {
                            arraylist = new ArrayList<String>();
                            DownloadJSON jsonresults = new DownloadJSON();
                            jsonresults.execute(Ticker.getText().toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                final EditText Original = (EditText) dialogView.findViewById(com.APportfolio.R.id.editText2);
                final EditText Book_Value = (EditText) dialogView.findViewById(com.APportfolio.R.id.editText3);
                Button Add = (Button) dialogView.findViewById(com.APportfolio.R.id.allow);
                Button Cancel = (Button) dialogView.findViewById(com.APportfolio.R.id.deny);
                Add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GetTrue true1 = new GetTrue();
                        String shares = String.valueOf(Double.parseDouble(Book_Value.getText().toString()) / Double.parseDouble(Original.getText().toString()));
                        String ticker;
                        if (Ticker.getText().toString().contains(" ")){
                            ticker = Ticker.getText().toString().substring(0,Ticker.getText().toString().indexOf(" "));
                        } else {
                            ticker = Ticker.getText().toString();
                        }
                        true1.execute(ticker, Original.getText().toString(), Book_Value.getText().toString(), shares);
                        stock_data1 = new ArrayList<StocksRelay>();
                        adapter.notifyDataSetChanged();
                        alertDialog.cancel();
                        alertDialog.dismiss();
                    }
                });
                Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                        alertDialog.dismiss();
                    }
                });

            }
        });
        Stocks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(com.APportfolio.R.layout.edit_delete, null);
                dialogBuilder.setView(dialogView);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();
                final EditText Ticker = (EditText) dialogView.findViewById(com.APportfolio.R.id.editText);
                final EditText Original = (EditText) dialogView.findViewById(com.APportfolio.R.id.editText2);
                final EditText Book_Value = (EditText) dialogView.findViewById(com.APportfolio.R.id.editText3);
                final Button Edit = (Button) dialogView.findViewById(com.APportfolio.R.id.edit);
                final Button Delete = (Button) dialogView.findViewById(com.APportfolio.R.id.delete);
                final Button Cancel = (Button) dialogView.findViewById(com.APportfolio.R.id.cancel);
                final Button Done = (Button) dialogView.findViewById(com.APportfolio.R.id.done);
                Ticker.setText(db.getAllStocks().get(i).getSymbol());
                Original.setText(db.getAllStocks().get(i).getOriginal());
                Book_Value.setText(db.getAllStocks().get(i).getBook());
                Edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Ticker.setEnabled(true);
                        Original.setEnabled(true);
                        Book_Value.setEnabled(true);
                        Delete.setVisibility(View.GONE);
                        Edit.setVisibility(View.GONE);
                        Done.setVisibility(View.VISIBLE);
                    }
                });
                Done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String shares = String.valueOf(Double.parseDouble(Book_Value.getText().toString()) / Double.parseDouble(Original.getText().toString()));
                        db.updateStock(new StockDataSQL(Ticker.getText().toString(),Original.getText().toString(),Book_Value.getText().toString(), shares));
                        Ticker.setEnabled(false);
                        Original.setEnabled(false);
                        Book_Value.setEnabled(false);
                        Delete.setVisibility(View.VISIBLE);
                        Edit.setVisibility(View.VISIBLE);
                        Done.setVisibility(View.GONE);
                        stock_data1 = new ArrayList<StocksRelay>();
                        adapter.notifyDataSetChanged();
                        alertDialog.cancel();
                        alertDialog.dismiss();
                    }
                });
                Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                        alertDialog.dismiss();
                    }
                });
                Delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String shares = String.valueOf(Double.parseDouble(Book_Value.getText().toString()) / Double.parseDouble(Original.getText().toString()));
                        if (db.getStocksCount() == 1) {
                            Stocks.setAdapter(null);
                            DeleteTask delete = new DeleteTask();
                            delete.execute(Ticker.getText().toString(), Original.getText().toString(), Book_Value.getText().toString(), shares);
                            alertDialog.cancel();
                            alertDialog.dismiss();
                        } else {
                            DeleteTask delete = new DeleteTask();
                            delete.execute(Ticker.getText().toString(), Original.getText().toString(), Book_Value.getText().toString(), shares);
                            adapter.notifyDataSetChanged();
                            alertDialog.cancel();
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });
        StocksLoad();
    }

    private class DeleteTask extends AsyncTask<String, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg0) {

            //don't touch dialog here it'll break the application
            //do some lengthy stuff like calling login webservice
            db.deleteStock(new StockDataSQL(arg0[0],arg0[1],arg0[2], arg0[3]));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //hide the dialog
            StocksLoad();
            super.onPostExecute(result);
        }

    }
    private class GetStockData extends AsyncTask<String, Void, ArrayList<StocksRelay>> {
        @Override
        protected void onPreExecute(){
            if (pdia != null) {
                pdia.show();
            } else {
                pdia = new ProgressDialog(MainActivity.this);
                pdia.setMessage("Loading...");
                pdia.show();
            }
            super.onPreExecute();
        }
        @Override
        protected ArrayList<StocksRelay> doInBackground(String... urls) {
            Document doc = null;
            Document doc1 = null;
            try {
                doc1 = Jsoup.connect(urls[0]).get();

            } catch (IOException e) {
                e.printStackTrace();
            }
            String symbol = doc1.select("a.qn_ontab").first().text();
            String value = doc1.select("div#qwidget_lastsale.qwidget-dollar").first().text().replace("$","");
            String gainvalue = doc1.select("div#qwidget_netchange.qwidget-cents").get(0).text();
            String gainvalue1 = doc1.select("div#qwidget_percent.qwidget-percent").get(0).text();
            Log.e("Shares", urls[2]);
            Log.e("Value", value);
            String valueFinal = Double.toString(Double.parseDouble(urls[3]) * Double.parseDouble(value));
            String gain = String.valueOf(Double.parseDouble(valueFinal) - Double.parseDouble(urls[2]));
            String gainpercent = String.valueOf(Double.parseDouble(gain)/Double.parseDouble(urls[2]) * 100);
            stock_data1.add(new StocksRelay(symbol,value,gainvalue,gainvalue1,valueFinal,gain,gainpercent));
            Log.e("Quote", value);
            return stock_data1;
        }
        @Override
        protected void onPostExecute(ArrayList<StocksRelay> result) {
            stock_data1 = result;
            StocksAdapter adapter = new StocksAdapter(MainActivity.this,
                    com.APportfolio.R.layout.stock_row, stock_data1);
            Stocks.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            pdia.dismiss();
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
            } else {
                value.add("false");
            }
            value.add(urls[0]);
            value.add(urls[1]);
            value.add(urls[2]);
            value.add(urls[3]);
            return value;
        }
        @Override
        protected void onPostExecute(ArrayList result) {
            if (result.get(0) == "true") {

                db.addStock(new StockDataSQL(result.get(1).toString(), result.get(2).toString(),result.get(3).toString(), result.get(4).toString()));
                Log.e("Data", Integer.toString(db.getStocksCount()));
            } else {
                Toast.makeText(MainActivity.this, "Cannot Find Ticker", Toast.LENGTH_LONG).show();
            }
            StocksLoad();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.APportfolio.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
    public void StocksLoad() {
        if (db.getStocksCount() > 0) {
            for (int i = 0; i < db.getStocksCount(); i++) {
                GetStockData task = new GetStockData();
                task.execute("http://www.nasdaq.com/symbol/" + db.getAllStocks().get(i).getSymbol(), db.getAllStocks().get(i).getOriginal(), db.getAllStocks().get(i).getBook(), db.getAllStocks().get(i).getShares());
            }
            stock_data1 = new ArrayList<StocksRelay>();
        }
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
            LayoutInflater inflater = MainActivity.this.getLayoutInflater();
            final View dialogView = inflater.inflate(com.APportfolio.R.layout.add_stock_quote, null);
            final AutoCompleteTextView Ticker = (AutoCompleteTextView) dialogView.findViewById(com.APportfolio.R.id.editText);
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                    (MainActivity.this, com.APportfolio.R.layout.support_simple_spinner_dropdown_item,arraylist);
            Ticker.setThreshold(1);//will start working from first character
            Ticker.setAdapter(adapter1);//setting the adapter data into the AutoCompleteTextView
            super.onPostExecute(args);
            Toast.makeText(MainActivity.this, "Stocks Loaded", Toast.LENGTH_LONG).show();
        }
    }
}
