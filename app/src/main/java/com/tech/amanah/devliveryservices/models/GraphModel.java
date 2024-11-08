package com.tech.amanah.devliveryservices.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GraphModel {

    @SerializedName("result")
    @Expose
    private List<Result> result;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class Result {

        @SerializedName("day")
        @Expose
        private String day;

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

    }

}

