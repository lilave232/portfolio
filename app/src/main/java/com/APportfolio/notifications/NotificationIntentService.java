package com.APportfolio.notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.TextView;

import com.APportfolio.ArrayTable;
import com.APportfolio.News;
import com.APportfolio.R;
import com.APportfolio.broadcast_receivers.NotificationEventReceiver;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by klogi
 *
 *
 */
public class NotificationIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    ArrayList newstodisplay = new ArrayList();
    ArrayTable newstable = new ArrayTable();
    int NumItems = 0;
    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
    }

    private void processStartNotification() {
        // Do something. For example, fetch fresh data from backend to create a rich notification?
        if (isNetworkAvailable()) {
            GetTrue get = new GetTrue();
            get.execute();
        }
    }
    private class GetTrue extends AsyncTask<TextView, Void, ArrayTable> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected ArrayTable doInBackground(TextView... title) {
            Document doc = null;
            Document doc1 = null;
            Document doc2 = null;
            ArrayTable value = new ArrayTable();
            try {
                doc1 = Jsoup.connect("http://www.cnbc.com/id/10001147/device/rss/rss.xml").get();
                doc = Jsoup.connect("http://syndication.cbc.ca/partnerrss/business.xml").get();
                doc2 = Jsoup.connect("http://rss.cnn.com/rss/money_latest.rss.xml").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            value.AddColumn("title");
            value.AddColumn("link");
            value.AddColumn("description");
            Log.d("String", doc1.html());
            for (int i=1; i < doc1.select("title").size(); i++){
                value.AddRow(doc1.select("title").get(i).text(),doc1.select("link").get(i).text(),doc1.select("description").get(i).text());
            }
            for (int i=2; i<doc.select("title").size()-1; i++){
                value.AddRow(doc.select("title").get(i).text(),doc.select("link").get(i).text(),doc.select("description").get(i).text().replace("<p>","").replace(" </p>",""));
            }
            for (int i=2; i<doc2.select("title").size()-1; i++){
                value.AddRow(doc2.select("title").get(i).text(),doc2.select("link").get(i).text(),doc2.select("description").get(i).text().substring(0,doc2.select("description").get(i).text().indexOf("img")).replace("<",""));
            }
            return value;
        }
        @Override
        protected void onPostExecute(ArrayTable result) {
            newstable = result;
            Random r = new Random();
            int i1 = r.nextInt(newstable.Size());
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationIntentService.this);
            builder.setContentTitle(newstable.GetItemByLocation(i1,"title"))
                    .setAutoCancel(true)
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setContentText(newstable.GetItemByLocation(i1,"description"))
                    .setSmallIcon(R.drawable.ic_view_headline_black_24dp);

            Intent resultIntent = new Intent(Intent.ACTION_VIEW);
            resultIntent.setData(Uri.parse(newstable.GetItemByLocation(i1,"link")));
            Intent intent12 = new Intent(NotificationIntentService.this, News.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(NotificationIntentService.this,
                    NOTIFICATION_ID,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(NotificationIntentService.this));

            final NotificationManager manager = (NotificationManager) NotificationIntentService.this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
