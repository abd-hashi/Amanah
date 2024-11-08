package com.tech.amanah.shops.activities;

import com.tech.amanah.shops.models.ModelShopItems;

public interface ShopListener {
    void onShop(int position, ModelShopItems.Result modelShopItems);
}
