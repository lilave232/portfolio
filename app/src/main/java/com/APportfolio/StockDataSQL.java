package com.APportfolio;

/**
 * Created by averypozzobon on 2017-05-10.
 */
public class StockDataSQL {
        String Symbol;
        String Original;
        String Book;
        String Shares;

        // Empty constructor
        public StockDataSQL(){

        }
        // constructor
        public StockDataSQL(String Symbol, String Original, String Book, String Shares){
            this.Symbol = Symbol;
            this.Original = Original;
            this.Book = Book;
            this.Shares = Shares;
        }

        // getting name
        public String getSymbol(){
            return this.Symbol;
        }

        // setting name
        public void setSymbol(String Symbol1){
            this.Symbol = Symbol1;
        }

        // getting phone number
        public String getOriginal(){
            return this.Original;
        }

        // setting phone number
        public void setOriginal(String Original1){
            this.Original = Original1;
        }
        // getting phone number
        public String getBook(){
            return this.Book;
        }

        // setting phone number
        public void setBook(String Book1){
            this.Book = Book1;
        }
        // getting phone number
        public String getShares(){
            return this.Shares;
        }

        // setting phone number
        public void setShares(String shares){
            this.Shares = shares;
        }
}
