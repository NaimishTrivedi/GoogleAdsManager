/*
 * Created by Naimish Trivedi on 08/02/24, 5:49 pm
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 08/02/24, 5:49 pm
 */

package naimishtrivedi.adsmanager.googleadsmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import naimishtrivedi.adsmanager.googleadsmanager.databinding.ActivityInterstitialAdDemoBinding;
import naimishtrivedi.in.googleadsmanager.AdsManager;
import naimishtrivedi.in.googleadsmanager.InterstitialAds;
import naimishtrivedi.in.googleadsmanager.interfaces.OnAdRevenueListener;
import naimishtrivedi.in.googleadsmanager.interfaces.OnAdsListener;
import naimishtrivedi.in.googleadsmanager.AdsPaidModel;
import naimishtrivedi.in.googleadsmanager.model.LogPaidImpression;

public class InterstitialAdDemo extends AppCompatActivity implements OnAdsListener, OnAdRevenueListener {

    private ActivityInterstitialAdDemoBinding binding;
    private InterstitialAds mInterstitialAds;

    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInterstitialAdDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mInterstitialAds = new InterstitialAds("/6499/example/interstitial",this);

        mInterstitialAds.setOnAdsListener(this);

        mInterstitialAds.setOnAdRevenueListener(this);

        binding.mBtnLoadAds.setOnClickListener(view -> {
            if(!mInterstitialAds.isAdsReady()){
                showAdsLoadDialog();
                mInterstitialAds.loadAd();
            }else{
                Toast.makeText(InterstitialAdDemo.this,"Interstitial ads already loaded",Toast.LENGTH_SHORT).show();
            }
        });

        binding.mBtnShowAds.setOnClickListener(view -> {
            if(mInterstitialAds.isAdsReady()){
                mInterstitialAds.showAd();
            }else{
                Toast.makeText(InterstitialAdDemo.this,"Interstitial ads not loaded",Toast.LENGTH_SHORT).show();
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
        Log.e("TAG", "onAdLoadFailed :: "+"adsId => "+adsId+" Error => "+error);
        hideAdsLoadDialog();
    }

    @Override
    public void onAdFailedToShow(String error) {
        Log.e("TAG", "onAdFailedToShow :: "+" Error => "+error);
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

    private void showAdsLoadDialog(){
        if(progressDialog == null){
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

    private void hideAdsLoadDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
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

        AdsManager.logPaidImpression(this,logPaidImpression);
    }
}