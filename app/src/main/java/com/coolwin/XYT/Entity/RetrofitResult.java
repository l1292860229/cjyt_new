package com.coolwin.XYT.Entity;

/**
 * Created by dell on 2017/5/25.
 */

public class RetrofitResult<T> {
    private T data;
    private State state;
    private String max;
    private String min;
    private String speakStatus;
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
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getSpeakStatus() {
        return speakStatus;
    }

    public void setSpeakStatus(String speakStatus) {
        this.speakStatus = speakStatus;
    }
}
