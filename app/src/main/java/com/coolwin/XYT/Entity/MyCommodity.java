package com.coolwin.XYT.Entity;

import java.util.List;

/**
 * Created by Administrator on 2017/6/1.
 */

public class MyCommodity {
    public int Status;
    public String Message;
    public String title;
    public List<Subjects> subjects;
    public class Subjects{
        public int id;
        public String url;
        public String Name;
        public int LowSellPrice;
        public int LowMarketPrice;
        public double discount;
        //public String genres;
        public String Collect;

        @Override
        public String toString() {
            return "Subjects{" +
                    "id=" + id +
                    ", url='" + url + '\'' +
                    ", Name='" + Name + '\'' +
                    ", LowSellPrice=" + LowSellPrice +
                    ", LowMarketPrice=" + LowMarketPrice +
                    ", discount=" + discount +
                    ", Collect='" + Collect + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MyCommodity{" +
                "Status=" + Status +
                ", Message='" + Message + '\'' +
                ", title='" + title + '\'' +
                ", Subjects=" + subjects +
                '}';
    }
}
