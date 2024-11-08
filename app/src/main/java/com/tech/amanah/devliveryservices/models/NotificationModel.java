package com.tech.amanah.devliveryservices.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationModel {
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
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("mesage")
        @Expose
        private String mesage;
        @SerializedName("date_time")
        @Expose
        private String date_time;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMsg() {
            return mesage;
        }

        public void setMsg(String mesage) {
            this.mesage = mesage;
        }

        public String getDate() {
            return date_time;
        }

        public void setDate(String date) {
            this.date_time = date_time;
        }
    }
}
