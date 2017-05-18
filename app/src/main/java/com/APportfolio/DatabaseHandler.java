package com.APportfolio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by averypozzobon on 2017-05-11.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "stockManager";

    // Contacts table name
    private static final String TABLE_STOCKS = "Portfolio";

    // Contacts Table Columns names
    private static final String KEY_SYMBOL = "symbol";
    private static final String KEY_ORIGINAL = "original";
    private static final String KEY_BOOK = "book";
    private static final String KEY_SHARES = "shares";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_STOCKS + "("
                + KEY_SYMBOL + " TEXT PRIMARY KEY," + KEY_ORIGINAL + " TEXT,"
                + KEY_BOOK + " TEXT," + KEY_SHARES + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCKS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    void addStock(StockDataSQL stock) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SYMBOL, stock.getSymbol()); // Contact Name
        values.put(KEY_ORIGINAL, stock.getOriginal()); // Contact Phone
        values.put(KEY_BOOK, stock.getBook());
        values.put(KEY_SHARES, stock.getShares());
        // Inserting Row
        db.insert(TABLE_STOCKS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    StockDataSQL getStock(String Symbol) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_STOCKS, new String[] { KEY_SYMBOL,
                        KEY_ORIGINAL, KEY_BOOK, KEY_SHARES }, KEY_SYMBOL + "=?",
                new String[] { Symbol }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        StockDataSQL stock = new StockDataSQL(cursor.getString(0),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        // return contact
        return stock;
    }

    // Getting All Contacts
    public List<StockDataSQL> getAllStocks() {
        List<StockDataSQL> stockList = new ArrayList<StockDataSQL>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_STOCKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                StockDataSQL stock = new StockDataSQL();
                stock.setSymbol(cursor.getString(0));
                stock.setOriginal(cursor.getString(1));
                stock.setBook(cursor.getString(2));
                stock.setShares(cursor.getString(3));
                // Adding contact to list
                stockList.add(stock);
            } while (cursor.moveToNext());
        }

        // return contact list
        return stockList;
    }

    // Updating single contact
    public int updateStock(StockDataSQL stock) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SYMBOL, stock.getSymbol());
        values.put(KEY_ORIGINAL, stock.getOriginal());
        values.put(KEY_BOOK, stock.getBook());
        values.put(KEY_SHARES, stock.getShares());

        // updating row
        return db.update(TABLE_STOCKS, values, KEY_SYMBOL + " = ?",
                new String[] { stock.getSymbol() });
    }

    // Deleting single contact
    public void deleteStock(StockDataSQL stock) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STOCKS, KEY_SYMBOL + " = ?",
                new String[] { String.valueOf(stock.getSymbol()) });
        db.close();
    }


    // Getting contacts Count
    public int getStocksCount() {
        String countQuery = "SELECT * FROM " + TABLE_STOCKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        // return count
        return cursor.getCount();
    }

}
