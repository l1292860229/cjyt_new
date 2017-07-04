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

    @Override
    public String toString() {
        return "RetrofitResult{" +
                "data=" + data +
                ", state=" + state +
                ", max='" + max + '\'' +
                ", min='" + min + '\'' +
                ", speakStatus='" + speakStatus + '\'' +
                '}';
    }
}
