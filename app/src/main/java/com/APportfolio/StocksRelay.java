package com.APportfolio;

/**
 * Created by averypozzobon on 2017-05-10.
 */
public class StocksRelay {
        public String symbol;
        public String price;
        public String percentgainprice;
        public String portfoliovalue;
        public String gainvalue;
        public String gainpercent;
        public String gainprice;
        public StocksRelay(){
            super();
        }

        public StocksRelay(String symbol, String price, String percentgainprice, String gainprice, String portfoliovalue, String gainvalue, String gainpercent) {
            super();
            this.symbol = symbol;
            this.price = price;
            this.percentgainprice = percentgainprice;
            this.portfoliovalue = portfoliovalue;
            this.gainvalue = gainvalue;
            this.gainpercent = gainpercent;
            this.gainprice = gainprice;
        }
    }
