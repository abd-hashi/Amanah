package com.tech.amanah.taxiservices.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ModelCurrentBookingResult implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("driver_id")
    @Expose
    private String driverId;
    @SerializedName("picuplocation")
    @Expose
    private String picuplocation;
    @SerializedName("service_type")
    @Expose
    private Object serviceType;
    @SerializedName("dropofflocation")
    @Expose
    private String dropofflocation;
    @SerializedName("picuplat")
    @Expose
    private String picuplat;
    @SerializedName("pickuplon")
    @Expose
    private String pickuplon;
    @SerializedName("droplat")
    @Expose
    private String droplat;
    @SerializedName("droplon")
    @Expose
    private String droplon;
    @SerializedName("shareride_type")
    @Expose
    private String sharerideType;
    @SerializedName("booktype")
    @Expose
    private String booktype;
    @SerializedName("car_type_id")
    @Expose
    private String carTypeId;

    @SerializedName("car_name")
    @Expose
    private String carName;

    @SerializedName("fare")
    @Expose
    private Integer fare;
    @SerializedName("car_seats")
    @Expose
    private String carSeats;
    @SerializedName("passenger")
    @Expose
    private String passenger;
    @SerializedName("booked_seats")
    @Expose
    private String bookedSeats;
    @SerializedName("req_datetime")
    @Expose
    private String reqDatetime;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("picklatertime")
    @Expose
    private String picklatertime;
    @SerializedName("picklaterdate")
    @Expose
    private String picklaterdate;
    @SerializedName("route_img")
    @Expose
    private String routeImg;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private Object endTime;
    @SerializedName("wt_start_time")
    @Expose
    private Object wtStartTime;
    @SerializedName("wt_end_time")
    @Expose
    private Object wtEndTime;
    @SerializedName("accept_time")
    @Expose
    private Object acceptTime;
    @SerializedName("waiting_status")
    @Expose
    private String waitingStatus;
    @SerializedName("waiting_cnt")
    @Expose
    private Object waitingCnt;
    @SerializedName("reason_id")
    @Expose
    private Object reasonId;
    @SerializedName("card_id")
    @Expose
    private String cardId;
    @SerializedName("apply_code")
    @Expose
    private String applyCode;
    @SerializedName("payment_type")
    @Expose
    private String paymentType;
    @SerializedName("favorite_ride")
    @Expose
    private String favoriteRide;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("user_rating_status")
    @Expose
    private String userRatingStatus;
    @SerializedName("tip_amount")
    @Expose
    private String tipAmount;
    @SerializedName("pay_status")
    @Expose
    private String payStatus;
    @SerializedName("hourdiff")
    @Expose
    private Double hourdiff;
    @SerializedName("driver_details")
    @Expose
    private List<ModelLogin.Result> userDetails = null;
    @SerializedName("type_name")
    @Expose
    private String typeName;
    @SerializedName("type_image")
    @Expose
    private String typeImage;
    @SerializedName("time_diff")
    @Expose
    private String timeDiff;
    @SerializedName("st_milisecond")
    @Expose
    private Integer stMilisecond;
    @SerializedName("ed_milisecond")
    @Expose
    private Integer edMilisecond;
    @SerializedName("milisecond")
    @Expose
    private Integer milisecond;
    @SerializedName("estimate_time")
    @Expose
    private String estimateTime;
    @SerializedName("estimate_distance")
    @Expose
    private String estimateDistance;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("waiting_time")
    @Expose
    private String waiting_time;
    @SerializedName("incentive_amount")
    @Expose
    private String incentive_amount;
    @SerializedName("total_amount")
    @Expose
    private String total_amount;
    @SerializedName("arrived_waiting_time")
    @Expose
    private String arrived_waiting_time;


    @SerializedName("waiting_time_charge")
    @Expose
    private int waitingTimeCharge;

    @SerializedName("waiting_time_minute")
    @Expose
    private String waitingTimeMinute;


    public int getWaitingTimeCharge() {
        return waitingTimeCharge;
    }

    public void setWaitingTimeCharge(int waitingTimeCharge) {
        this.waitingTimeCharge = waitingTimeCharge;
    }

    public String getWaitingTimeMinute() {
        return waitingTimeMinute;
    }

    public void setWaitingTimeMinute(String waitingTimeMinute) {
        this.waitingTimeMinute = waitingTimeMinute;
    }




    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getPicuplocation() {
        return picuplocation;
    }

    public void setPicuplocation(String picuplocation) {
        this.picuplocation = picuplocation;
    }

    public Object getServiceType() {
        return serviceType;
    }

    public void setServiceType(Object serviceType) {
        this.serviceType = serviceType;
    }

    public String getDropofflocation() {
        return dropofflocation;
    }

    public void setDropofflocation(String dropofflocation) {
        this.dropofflocation = dropofflocation;
    }

    public String getPicuplat() {
        return picuplat;
    }

    public void setPicuplat(String picuplat) {
        this.picuplat = picuplat;
    }

    public String getPickuplon() {
        return pickuplon;
    }

    public void setPickuplon(String pickuplon) {
        this.pickuplon = pickuplon;
    }

    public String getDroplat() {
        return droplat;
    }

    public void setDroplat(String droplat) {
        this.droplat = droplat;
    }

    public String getDroplon() {
        return droplon;
    }

    public void setDroplon(String droplon) {
        this.droplon = droplon;
    }

    public String getSharerideType() {
        return sharerideType;
    }

    public void setSharerideType(String sharerideType) {
        this.sharerideType = sharerideType;
    }

    public String getBooktype() {
        return booktype;
    }

    public void setBooktype(String booktype) {
        this.booktype = booktype;
    }

    public String getCarTypeId() {
        return carTypeId;
    }

    public void setCarTypeId(String carTypeId) {
        this.carTypeId = carTypeId;
    }

    public String getCarSeats() {
        return carSeats;
    }

    public void setCarSeats(String carSeats) {
        this.carSeats = carSeats;
    }

    public String getPassenger() {
        return passenger;
    }

    public void setPassenger(String passenger) {
        this.passenger = passenger;
    }

    public String getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(String bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    public String getReqDatetime() {
        return reqDatetime;
    }

    public void setReqDatetime(String reqDatetime) {
        this.reqDatetime = reqDatetime;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getPicklatertime() {
        return picklatertime;
    }

    public void setPicklatertime(String picklatertime) {
        this.picklatertime = picklatertime;
    }

    public String getPicklaterdate() {
        return picklaterdate;
    }

    public void setPicklaterdate(String picklaterdate) {
        this.picklaterdate = picklaterdate;
    }

    public String getRouteImg() {
        return routeImg;
    }

    public void setRouteImg(String routeImg) {
        this.routeImg = routeImg;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Object getEndTime() {
        return endTime;
    }

    public void setEndTime(Object endTime) {
        this.endTime = endTime;
    }

    public Object getWtStartTime() {
        return wtStartTime;
    }

    public void setWtStartTime(Object wtStartTime) {
        this.wtStartTime = wtStartTime;
    }

    public Object getWtEndTime() {
        return wtEndTime;
    }

    public void setWtEndTime(Object wtEndTime) {
        this.wtEndTime = wtEndTime;
    }

    public Object getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(Object acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getWaitingStatus() {
        return waitingStatus;
    }

    public void setWaitingStatus(String waitingStatus) {
        this.waitingStatus = waitingStatus;
    }

    public Object getWaitingCnt() {
        return waitingCnt;
    }

    public void setWaitingCnt(Object waitingCnt) {
        this.waitingCnt = waitingCnt;
    }

    public Object getReasonId() {
        return reasonId;
    }

    public void setReasonId(Object reasonId) {
        this.reasonId = reasonId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getApplyCode() {
        return applyCode;
    }

    public void setApplyCode(String applyCode) {
        this.applyCode = applyCode;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getFavoriteRide() {
        return favoriteRide;
    }

    public void setFavoriteRide(String favoriteRide) {
        this.favoriteRide = favoriteRide;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserRatingStatus() {
        return userRatingStatus;
    }

    public void setUserRatingStatus(String userRatingStatus) {
        this.userRatingStatus = userRatingStatus;
    }

    public String getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(String tipAmount) {
        this.tipAmount = tipAmount;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public Double getHourdiff() {
        return hourdiff;
    }

    public void setHourdiff(Double hourdiff) {
        this.hourdiff = hourdiff;
    }


    public List<ModelLogin.Result> getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(List<ModelLogin.Result> userDetails) {
        this.userDetails = userDetails;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeImage() {
        return typeImage;
    }

    public void setTypeImage(String typeImage) {
        this.typeImage = typeImage;
    }

    public String getTimeDiff() {
        return timeDiff;
    }

    public void setTimeDiff(String timeDiff) {
        this.timeDiff = timeDiff;
    }

    public Integer getStMilisecond() {
        return stMilisecond;
    }

    public void setStMilisecond(Integer stMilisecond) {
        this.stMilisecond = stMilisecond;
    }

    public Integer getEdMilisecond() {
        return edMilisecond;
    }

    public void setEdMilisecond(Integer edMilisecond) {
        this.edMilisecond = edMilisecond;
    }

    public Integer getMilisecond() {
        return milisecond;
    }

    public void setMilisecond(Integer milisecond) {
        this.milisecond = milisecond;
    }

    public String getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(String estimateTime) {
        this.estimateTime = estimateTime;
    }

    public String getEstimateDistance() {
        return estimateDistance;
    }

    public void setEstimateDistance(String estimateDistance) {
        this.estimateDistance = estimateDistance;
    }

    public Integer getFare() {
        return fare;
    }

    public void setFare(Integer fare) {
        this.fare = fare;
    }



    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getWaiting_time() {
        return waiting_time;
    }

    public void setWaiting_time(String waiting_time) {
        this.waiting_time = waiting_time;
    }

    public String getIncentive_amount() {
        return incentive_amount;
    }

    public void setIncentive_amount(String incentive_amount) {
        this.incentive_amount = incentive_amount;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getArrived_waiting_time() {
        return arrived_waiting_time;
    }

    public void setArrived_waiting_time(String arrived_waiting_time) {
        this.arrived_waiting_time = arrived_waiting_time;
    }
}

