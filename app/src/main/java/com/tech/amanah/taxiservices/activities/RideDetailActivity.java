package com.tech.amanah.taxiservices.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.tech.amanah.R;
import com.tech.amanah.databinding.ActivityRideDetailBinding;
import com.tech.amanah.fragments.RaiseIssueBottomSheet;
import com.tech.amanah.taxiservices.models.ModelTaxiHistory;

public class RideDetailActivity extends AppCompatActivity {

    Context mContext = RideDetailActivity.this;
    ActivityRideDetailBinding binding;
    ModelTaxiHistory.Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_detail);
        result = (ModelTaxiHistory.Result) getIntent().getSerializableExtra("data");
        binding.tvIncenAmt.setText("E Birr " + result.getIncentive_amount());
        binding.tvWaitingTime.setText( result.getWaiting_time_minute() + " Min");
        binding.tvWaitingCharge.setText("E Birr " + result.getWaiting_time_charge());
        binding.tvTotalRideCost.setText("E Birr " + result.getTotal_amount());


        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.setData(result);
    }

}