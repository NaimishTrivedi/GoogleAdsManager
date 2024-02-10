/*
 * Created by Naimish Trivedi on 06/02/24, 11:13 am
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 06/02/24, 11:08 am
 */

package naimishtrivedi.adsmanager.googleadsmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;


import naimishtrivedi.adsmanager.googleadsmanager.databinding.ActivityBannerAdsDemoBinding;
import naimishtrivedi.in.googleadsmanager.AdsManager;
import naimishtrivedi.in.googleadsmanager.AdsPaidModel;
import naimishtrivedi.in.googleadsmanager.interfaces.OnAdRevenueListener;
import naimishtrivedi.in.googleadsmanager.interfaces.OnAdsViewListener;
import naimishtrivedi.in.googleadsmanager.model.LogPaidImpression;

public class BannerAdsDemo extends AppCompatActivity {

    private ActivityBannerAdsDemoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBannerAdsDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Default size banner ads with animation
        binding.mBannerAds.setOnAdsViewListener(new OnAdsViewListener() {
            @Override
            public void onAdsSuccess(String adResource) {
                Log.e("TAG",adResource+" Success");
            }

            @Override
            public void onAdsFailure(String adResource,String errorMsg) {
                Log.e("TAG",adResource+" Error msg "+errorMsg);
            }
        });

        binding.mBannerAds.setOnAdRevenueListener(new OnAdRevenueListener() {
            @Override
            public void onPaidEvent(AdsPaidModel model) {
                LogPaidImpression logPaidImpression = new LogPaidImpression();
                logPaidImpression.setAdsPlatform("ad_manager"); //Set Ads Platform value
                logPaidImpression.setAdsPlacement(model.getAdsPlacement()); //Set Ads Placement value like (banner, reward, native, interstitial or reward)
                logPaidImpression.setAdsSourceName(model.getAdsSourceName()); //Set Ads source name value from AdsPaidModel
                logPaidImpression.setAdsUnitId(model.getAdsUnitId()); // Set Ads unit id value from AdsPaidModel
                logPaidImpression.setCurrencyCode(model.getCurrencyCode()); //Set Currency code value from AdsPaidModel
                logPaidImpression.setValueMicros(model.getValueMicros()); //Set value micros from AdsPaidModel

                AdsManager.logPaidImpression(BannerAdsDemo.this,logPaidImpression);
            }
        });
        binding.mBannerAds.showAds("ca-app-pub-3940256099942544/6300978111");

        //large size banner ads with animation
        binding.mLargeBannerAds.showAds("/6499/example/banner");

        //default size banner ads without animation
        binding.mBannerAdsWithoutAnimation.showAds("/6499/example/banner");
    }
}