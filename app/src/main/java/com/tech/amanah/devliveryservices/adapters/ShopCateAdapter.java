package com.tech.amanah.devliveryservices.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.devliveryservices.models.ModelShopCat;
import com.tech.amanah.taxiservices.models.ModelLogin;

import java.util.ArrayList;

public class ShopCateAdapter extends BaseAdapter {

    Context context;
    ArrayList<ModelShopCat.Result> arrayList;
    LayoutInflater inflater ;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    public ShopCateAdapter(Context context, ArrayList<ModelShopCat.Result> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        inflater = (LayoutInflater.from(context));
        sharedPref = SharedPref.getInstance(context);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.item_shop_cate, null);
        TextView names =  view.findViewById(R.id.tvName11);

        if (sharedPref.getLanguage("lan").equals("en")) {
            names.setText(arrayList.get(i).getCategory_name());
        } else {
            names.setText(arrayList.get(i).getCategory_sumali_lang());
        }


        return view;
    }
}