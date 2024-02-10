/*
 * Created by Naimish Trivedi on 09/02/24, 5:29 pm
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 09/02/24, 5:29 pm
 */

package naimishtrivedi.adsmanager.googleadsmanager;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import naimishtrivedi.adsmanager.googleadsmanager.databinding.ActivityRewardAdsDemoBinding;
import naimishtrivedi.in.googleadsmanager.AdsManager;
import naimishtrivedi.in.googleadsmanager.AdsPaidModel;
import naimishtrivedi.in.googleadsmanager.RewardAds;
import naimishtrivedi.in.googleadsmanager.RewardEarned;
import naimishtrivedi.in.googleadsmanager.interfaces.OnAdRevenueListener;
import naimishtrivedi.in.googleadsmanager.interfaces.OnRewardAdsListener;
import naimishtrivedi.in.googleadsmanager.model.LogPaidImpression;

public class RewardAdsDemo extends AppCompatActivity implements OnRewardAdsListener, OnAdRevenueListener {

    private ActivityRewardAdsDemoBinding binding;

    private RewardAds mRewardAds;

    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRewardAdsDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mRewardAds = new RewardAds("/6499/example/rewarded", this);

        mRewardAds.setOnRewardAdsListener(this);

        mRewardAds.setOnAdRevenueListener(this);

        binding.mBtnLoadAds.setOnClickListener(view -> {
            if (!mRewardAds.isAdsReady()) {
                showAdsLoadDialog();
                mRewardAds.loadAd();
            } else {
                Toast.makeText(RewardAdsDemo.this, "Reward ads already loaded", Toast.LENGTH_SHORT).show();
            }
        });

        binding.mBtnShowAds.setOnClickListener(view -> {
            if (mRewardAds.isAdsReady()) {
                mRewardAds.showAd();
            } else {
                Toast.makeText(RewardAdsDemo.this, "Reward ads not loaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAdLoaded() {
        Log.e("TAG", "onAdLoaded Success");
        hideAdsLoadDialog();
    }

    @Override
    public void onAdLoadFailed(String adsId, String error) {
        Log.e("TAG", "onAdLoadFailed :: " + "adsId => " + adsId + " Error => " + error);
        hideAdsLoadDialog();
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

    @Override
    public void onUserRewarded(RewardEarned rewardEarned) {
        Log.e("TAG", "onUserRewarded :: Amount => " + rewardEarned.getAmount() + " Type => " + rewardEarned.getType());
    }

    @Override
    public void onPaidEvent(AdsPaidModel model) {
        LogPaidImpression logPaidImpression = new LogPaidImpression();
        logPaidImpression.setAdsPlatform("ad_manager"); //Set Ads Platform value
        logPaidImpression.setAdsPlacement(model.getAdsPlacement()); //Set Ads Placement value like (banner, reward, native, interstitial or reward)
        logPaidImpression.setAdsSourceName(model.getAdsSourceName()); //Set Ads source name value from AdsPaidModel
        logPaidImpression.setAdsUnitId(model.getAdsUnitId()); // Set Ads unit id value from AdsPaidModel
        logPaidImpression.setCurrencyCode(model.getCurrencyCode()); //Set Currency code value from AdsPaidModel
        logPaidImpression.setValueMicros(model.getValueMicros()); //Set value micros from AdsPaidModel

        AdsManager.logPaidImpression(this, logPaidImpression);
    }

    private void showAdsLoadDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.layout_progress_dialog);
            progressDialog.setCancelable(false);
            //progressDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            if (this.progressDialog.getWindow() != null) {
                this.progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }

        progressDialog.show();
    }

    private void hideAdsLoadDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}