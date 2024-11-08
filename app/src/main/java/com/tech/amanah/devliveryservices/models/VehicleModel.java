package com.tech.amanah.devliveryservices.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class VehicleModel {

    @SerializedName("result")
    @Expose
    private List<Result> result;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class Result {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("car_name")
        @Expose
        private String carName;
        @SerializedName("car_image")
        @Expose
        private String carImage;
        @SerializedName("charge")
        @Expose
        private String charge;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("waiting_time_charge")
        @Expose
        private String waitingTimeCharge;
        @SerializedName("type")
        @Expose
        private String type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCarName() {
            return carName;
        }

        public void setCarName(String carName) {
            this.carName = carName;
        }

        public String getCarImage() {
            return carImage;
        }

        public void setCarImage(String carImage) {
            this.carImage = carImage;
        }

        public String getCharge() {
            return charge;
        }

        public void setCharge(String charge) {
            this.charge = charge;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getWaitingTimeCharge() {
            return waitingTimeCharge;
        }

        public void setWaitingTimeCharge(String waitingTimeCharge) {
            this.waitingTimeCharge = waitingTimeCharge;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }


}