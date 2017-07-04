package com.coolwin.XYT.Entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dell on 2017/7/1.
 */
public class Order implements Serializable{
    public String status;
    public String ispay;
    public String code;
    public double amount;
    public String id;
    public String name;
    public String address;
    public String tel;
    public List<Goods> goods;
    public class Goods implements Serializable{
        public String title;
        public String num;
    }
}

