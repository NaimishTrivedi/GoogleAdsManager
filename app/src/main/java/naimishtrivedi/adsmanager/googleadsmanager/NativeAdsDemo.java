/*
 * Created by Naimish Trivedi on 06/02/24, 11:13 am
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 02/02/24, 6:19 pm
 */

package naimishtrivedi.adsmanager.googleadsmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import naimishtrivedi.adsmanager.googleadsmanager.databinding.ActivityNativeAdsDemoBinding;
import naimishtrivedi.in.googleadsmanager.AdsManager;
import naimishtrivedi.in.googleadsmanager.AdsPaidModel;
import naimishtrivedi.in.googleadsmanager.interfaces.OnAdRevenueListener;
import naimishtrivedi.in.googleadsmanager.interfaces.OnAdsViewListener;
import naimishtrivedi.in.googleadsmanager.model.LogPaidImpression;

public class NativeAdsDemo extends AppCompatActivity {

    private ActivityNativeAdsDemoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNativeAdsDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mNativeBannerAds.setAdsAnimationLayout(R.layout.ads_native_shimmer_effect);
        binding.mNativeBannerAds.setAdsLayout(R.layout.ads_native_screen_layout);

        binding.mNativeBannerAds.setOnAdsViewListener(new OnAdsViewListener() {
            @Override
            public void onAdsSuccess(String adResource) {
                Log.e("TAG",adResource+" Success");
            }

            @Override
            public void onAdsFailure(String adResource, String errorMsg) {
                Log.e("TAG",adResource+" Error "+errorMsg);
            }
        });

        binding.mNativeBannerAds.setOnAdRevenueListener(new OnAdRevenueListener() {
            @Override
            public void onPaidEvent(AdsPaidModel model) {
                LogPaidImpression logPaidImpression = new LogPaidImpression();
                logPaidImpression.setAdsPlatform("ad_manager"); //Set Ads Platform value
                logPaidImpression.setAdsPlacement(model.getAdsPlacement()); //Set Ads Placement value like (banner, reward, native, interstitial or reward)
                logPaidImpression.setAdsSourceName(model.getAdsSourceName()); //Set Ads source name value from AdsPaidModel
                logPaidImpression.setAdsUnitId(model.getAdsUnitId()); // Set Ads unit id value from AdsPaidModel
                logPaidImpression.setCurrencyCode(model.getCurrencyCode()); //Set Currency code value from AdsPaidModel
                logPaidImpression.setValueMicros(model.getValueMicros()); //Set value micros from AdsPaidModel

                AdsManager.logPaidImpression(NativeAdsDemo.this,logPaidImpression);
            }
        });
        binding.mNativeBannerAds.showAds("/6499/example/native");
    }
}