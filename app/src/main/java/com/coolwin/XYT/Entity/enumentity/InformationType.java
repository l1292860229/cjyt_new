package com.coolwin.XYT.Entity.enumentity;

import java.io.Serializable;

/**
 * Created by dell on 2017/6/14.
 */

public enum  InformationType implements Serializable {
    commodity("商品"),Information("资讯");
    String value;
    InformationType(String value){
        this.value =value;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}