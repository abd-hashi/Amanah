package com.tech.amanah.Utils.retrofitutils;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    @FormUrlEncoded
    @POST("change_password")
    Call<ResponseBody> changePass(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("change_password_user")
    Call<ResponseBody> changePass1(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_my_order")
    Call<ResponseBody> myOrderApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("remove_cart_data")
    Call<ResponseBody> removeCartDataApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_my_shop_orders")
    Call<ResponseBody> myShopOrderApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_search_shop_orders")
    Call<ResponseBody> getSearchShopApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("forgot_password")
    Call<ResponseBody> forgotPass(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("apply_code")
    Call<ResponseBody> applyPromoCode(@FieldMap Map<String, String> params);

    @POST("get_category")
    Call<ResponseBody> getShopCategory();

    @FormUrlEncoded
    @POST("get_lat_lon")
    Call<ResponseBody> getDriverLatLonCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> loginApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("place_order")
    Call<ResponseBody> placeDevOrderApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_shop_items")
    Call<ResponseBody> getAllShopItems(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("delete_shop_items")
    Call<ResponseBody> deleteShopItems(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("delete_cart_data")
    Call<ResponseBody> deleteCartItems(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("signup")
    Call<ResponseBody> signUpApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_allshop_list_bycategory")
    Call<ResponseBody> getAllShopApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("delete_cart")
    Call<ResponseBody> deleteStoreItemApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_cart")
    Call<ResponseBody> getStoreCartApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_booking_history")
    Call<ResponseBody> getTaxiHistory(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_to_cart")
    Call<ResponseBody> addToCartApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_to_cart_more")
    Call<ResponseBody> addToCartMoreApiCall(@FieldMap Map<String, String> params);


    @POST("get_support")
    Call<ResponseBody> getSupportApi();

    @FormUrlEncoded
    @POST("get_count_cart")
    Call<ResponseBody> getCartCountApiCall(@FieldMap Map<String, String> params);

    @Multipart
    @POST("add_shop")
    Call<ResponseBody> addShops(@Part("user_id") RequestBody user_id,
                                @Part("shop_name") RequestBody shop_name,
                                @Part("description") RequestBody description,
                                @Part("address") RequestBody address,
                                @Part("shop_category_id") RequestBody shop_category_id,
                                @Part("lat") RequestBody shop_lat,
                                @Part("lon") RequestBody shop_lon,
                                @Part("open_time") RequestBody open_time,
                                @Part("close_time") RequestBody close_time,
                                @Part MultipartBody.Part file1,
                                @Part MultipartBody.Part file2,
                                @Part MultipartBody.Part file3);

    @Multipart
    @POST("add_shop_items")
    Call<ResponseBody> addShopsItems(@Part("user_id") RequestBody shop_id,
                                     @Part("item_name") RequestBody item_name,
                                     @Part("item_price") RequestBody item_price,
                                     @Part("quantity") RequestBody quantity,
                                     @Part("discount") RequestBody discount,
                                     @Part("priceWithDiscount") RequestBody priceWithDiscount,
                                     @Part("item_description") RequestBody item_description,
                                     @Part MultipartBody.Part file1);


    @Multipart
    @POST("update_shop_items")
    Call<ResponseBody> editShopsItems(@Part("item_id") RequestBody shop_id,
                                     @Part("item_name") RequestBody item_name,
                                     @Part("item_price") RequestBody item_price,
                                     @Part("quantity") RequestBody quantity,
                                     @Part("discount") RequestBody discount,
                                     @Part("priceWithDiscount") RequestBody priceWithDiscount,
                                     @Part("item_description") RequestBody item_description,
                                     @Part MultipartBody.Part file1);




    @FormUrlEncoded
    @POST("insert_chat")
    Call<ResponseBody> insertChatBookingCall(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_chat")
    Call<ResponseBody> getChatBookingCall(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("add_rating_review")
    Call<ResponseBody> addFeedbackByUserApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_to_card_other")
    Call<ResponseBody> customOrderApiCall(@FieldMap Map<String, String> params);

    @GET("get_custom_car_list")
    Call<ResponseBody> getAllVehicleApiCall();

    @GET("get_permission_section")
    Call<ResponseBody> getServices();


    @FormUrlEncoded
    @POST("weekly_graph")
    Call<ResponseBody> getGraph(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_account_details")
    Call<ResponseBody> addINfoBa(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("cancel_by_user_shopOrder")
    Call<ResponseBody> cancelShopUserOrder(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_shop_statement")
    Call<ResponseBody> getEarningShopUser(@FieldMap Map<String, String> params);



    @FormUrlEncoded
    @POST("get_notification")
    Call<ResponseBody> getAllNotification(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_notification_counter")
    Call<ResponseBody> getCounter(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_notification_counter_chat")
    Call<ResponseBody> getCounterChat(@FieldMap Map<String, String> params);

}


