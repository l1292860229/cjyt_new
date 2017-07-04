package com.coolwin.XYT.Entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/5/31.
 */

public class DataModel implements Serializable {
    public List<Data> datas;
    public List<BannerList> bannerList;
    public class BannerList implements Serializable{
        public String imgUrl;
        public String type;
        public String id;
        public BannerList(){}
        public BannerList(String imgUrl){
            this.imgUrl = imgUrl;
        }
    }
    public class  Data implements Serializable{
        public String shopImageUrl;//图片路径
        public String shopLink;//外链
        public Data() {
        }
        public Data(String imagePath, String link) {
            this.shopImageUrl = imagePath;
            this.shopLink = link;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "shopImageUrl='" + shopImageUrl + '\'' +
                    ", shopLink='" + shopLink + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DataModel{" +
                "datas=" + datas +
                '}';
    }
}