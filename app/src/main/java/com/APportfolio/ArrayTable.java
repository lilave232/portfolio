package com.APportfolio;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by averypozzobon on 2017-05-08.
 */
public class ArrayTable {
    ArrayList<String> columns = new ArrayList<String>();
    ArrayList<String> rows = new ArrayList<String>();
    ArrayList<String> row = new ArrayList<String>();
    String item = "";
    public void AddColumn(String column){
        this.columns.add(column);
    }
    public ArrayList<String> GetAllColumns(){
        return columns;
    }
    public void AddRow(String... args) {
        if (args.length>columns.size()){
            Log.e("ERROR","Too many arguments");
        } else {
            for (int i=0; i<args.length; i++){
                rows.add(args[i]);
            }
        }
    }
    public ArrayList<String> GetRow(int rownum) {
        int x = (int)(rows.size()/(columns.size()+1));

        if (rownum>x) {
            Log.e("ERROR:","Row out of bounds");
        } else {
            Log.e("Rows",rows.toString());
            for (int i=rownum*(columns.size()); i<=(rownum*(columns.size())+(columns.size()-1));i++){
                row.add(rows.get(i).toString());
            }
        }
        return row;
    }
    public int Size(){
        int x = (int)(rows.size()/(columns.size()+1));
        return x;
    }
    public String GetItemByLocation(int rownum, String column) {
        int x = (int)(rows.size()/(columns.size()+1));
        if (rownum>x | columns.indexOf(column)>(columns.size()-1)) {
            Log.e("ERROR:","Row out of bounds");
        } else {
            int location = rownum * columns.size() + (columns.indexOf(column));
            item = rows.get(location);
        }
        return item;
    }
    public String GetItemByRelativeItem(String RelativeItemRow, String column) {
        int x = (int)(rows.size()/(columns.size()+1));
        int rownum = (int)(rows.indexOf(RelativeItemRow)/columns.size());
        Log.e("rownum",Integer.toString(rownum));
        if (rownum>x | columns.indexOf(column)>(columns.size()-1)) {
            Log.e("ERROR:","Row out of bounds");
        } else {
            int location = rownum * columns.size() + (columns.indexOf(column));
            item = rows.get(location);
        }
        return item;
    }
}
