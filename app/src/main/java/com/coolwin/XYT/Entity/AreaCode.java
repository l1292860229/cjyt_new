package com.coolwin.XYT.Entity;

import java.util.Arrays;

/**
 * Created by dell on 2017/6/13.
 */

public class AreaCode {
    public String State;
    public String id;
    public City[] Cities;
    public class City {
        public String id;
        public String City;

        @Override
        public String toString() {
            return  City;
        }
    }

    @Override
    public String toString() {
        return "AreaCode{" +
                "state='" + State + '\'' +
                ", cities=" + Arrays.toString(Cities) +
                '}';
    }
}
