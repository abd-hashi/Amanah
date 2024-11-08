package com.tech.amanah.shops.activities;

import com.tech.amanah.devliveryservices.models.ModelStoreBooking;

public interface StoreListener {
    void onStore(int position, ModelStoreBooking.Result result);
}
