/*
 * Created by Naimish Trivedi on 06/02/24, 11:13 am
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 05/02/24, 5:51 pm
 */

package naimishtrivedi.adsmanager.googleadsmanager;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import naimishtrivedi.in.googleadsmanager.AppOpenAds;
import naimishtrivedi.in.googleadsmanager.interfaces.OnAdsListener;

public class AdsDemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //registerActivityLifecycleCallbacks(new AdsManagerLifecycleCallbacks());
    }

    /*
     * This class help us for show ads from app open to background to foreground app.
     */
    private static final class AdsManagerLifecycleCallbacks implements ActivityLifecycleCallbacks, OnAdsListener {
        private static AppOpenAds mAppOpenAds;
        private int activityReferences = 0;

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            if (++activityReferences == 1) {
                // App enters foreground
                if (mAppOpenAds != null && mAppOpenAds.isAdsReady()) {
                    mAppOpenAds.showAd();
                }
            }
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            inItAppOpenAds(activity);
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            if (--activityReferences == 0) {
                // App enters background
            }
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }

        @Override
        public void onAdLoaded() {
            Log.e("TAG", "onAdLoaded Success");
        }

        @Override
        public void onAdLoadFailed(String adsId, String error) {
            Log.e("TAG", "onAdLoadFailed :: " + "adsId => " + adsId + " Error => " + error);
        }

        @Override
        public void onAdFailedToShow(String error) {
            Log.e("TAG", "onAdFailedToShow :: " + " Error => " + error);
        }

        @Override
        public void onAdShowed() {
            Log.e("TAG", "onAdShowed Success");
        }

        @Override
        public void onAdClicked() {
            Log.e("TAG", "onAdClicked Success");
        }

        @Override
        public void onAdDismiss() {
            Log.e("TAG", "onAdDismiss Success");
        }

        private void inItAppOpenAds(Activity activity) {
            if (mAppOpenAds == null) {
                mAppOpenAds = new AppOpenAds("/6499/example/app-open", activity);
                mAppOpenAds.setOnAdsListener(this);
            }

            if (!mAppOpenAds.isAdsReady()) {
                mAppOpenAds.loadAd();
            }
        }
    }

}
