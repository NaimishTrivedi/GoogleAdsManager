/*
 * Created by Naimish Trivedi on 06/02/24, 11:13 am
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 05/02/24, 5:49 pm
 */

package naimishtrivedi.adsmanager.googleadsmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import naimishtrivedi.adsmanager.googleadsmanager.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mBtnBannerAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,BannerAdsDemo.class));
            }
        });

        binding.mBtnNativeBannerAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,NativeAdsDemo.class));
            }
        });

        binding.mBtnInterstitialAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,InterstitialAdDemo.class));
            }
        });

        binding.mBtnRewardAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,RewardAdsDemo.class));
            }
        });

        binding.mBtnAppOpenAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AppOpenAdsDemo.class));
            }
        });
    }
}