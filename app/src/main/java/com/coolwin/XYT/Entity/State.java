package com.coolwin.XYT.Entity;

/**
 * Created by dell on 2017/7/1.
 */

public class State{
    private int code;
    private String msg;
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    @Override
    public String toString() {
        return "State{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
