/*
 * Created by Naimish Trivedi on 08/02/24, 6:44 pm
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 08/02/24, 6:44 pm
 */

package naimishtrivedi.in.googleadsmanager;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import naimishtrivedi.in.googleadsmanager.interfaces.OnAdRevenueListener;
import naimishtrivedi.in.googleadsmanager.interfaces.OnAdsListener;

public class InterstitialAds {

    private String adsId = "";
    private Activity mActivity;
    private boolean isAdsReady;
    private InterstitialAd adMobInterstitial;
    private OnAdsListener mOnAdsListener;

    private OnAdRevenueListener mOnAdRevenueListener;

    private static String TAG = AdsManager.class.getSimpleName();

    /*
     * TODO: InterstitialAds constructor take adsId and activity instance
     *  InterstitialAds(String adsId,Activity mActivity)
     */
    public InterstitialAds(String adsId, Activity mActivity) {
        this.adsId = adsId;
        this.mActivity = mActivity;
    }

    /*
     * TODO: loadAd() method use for load interstitial ads
     */
    public void loadAd() {
        adMobInterstitial = null;
        try {
            if (adsId == null || adsId.trim().isEmpty()) {
                if (mOnAdsListener != null) {
                    mOnAdsListener.onAdLoadFailed(adsId, "Ad failed to load :: Ads Unit Id is empty.");
                }
                return;
            }
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(mActivity, adsId, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    adMobInterstitial = null;
                    isAdsReady = false;
                    if (mOnAdsListener != null) {
                        String error = "";
                        if (loadAdError != null && loadAdError.toString() != null) {
                            error = "Ad failed to load :: " + loadAdError.toString();
                        } else {
                            error = "Ad failed to load";
                        }
                        mOnAdsListener.onAdLoadFailed(adsId, error);
                    }
                }

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    adMobInterstitial = interstitialAd;
                    isAdsReady = true;
                    if (mOnAdsListener != null) {
                        mOnAdsListener.onAdLoaded();
                    }

                    adMobInterstitial.setOnPaidEventListener(new OnPaidEventListener() {
                        @Override
                        public void onPaidEvent(@NonNull AdValue adValue) {
                            AdsPaidModel model = new AdsPaidModel();
                            try {
                                AdapterResponseInfo responseInfo = adMobInterstitial.getResponseInfo().getLoadedAdapterResponseInfo();
                                model.setAdsUnitId(adMobInterstitial.getAdUnitId());
                                if(responseInfo != null) {
                                    model.setAdsSourceName(responseInfo.getAdSourceName());
                                }
                                model.setAdsPlacement("interstitial");
                                model.setCurrencyCode(adValue.getCurrencyCode());
                                model.setValueMicros(adValue.getValueMicros());
                            }catch (Exception e){
                                Log.e(TAG,"Error onPaidEvent :: "+e.getMessage());
                            }
                            if (mOnAdRevenueListener != null) {
                                mOnAdRevenueListener.onPaidEvent(model);
                            }
                        }
                    });

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (mOnAdsListener != null) {
                mOnAdsListener.onAdLoadFailed(adsId, "Ad failed to load :: " + e.getMessage());
            }
        }
    }

    /*
     * TODO: isAdsReady() method use for indicate interstitial ads
     *  load success or not success
     */
    public boolean isAdsReady() {
        return isAdsReady && adMobInterstitial != null;
    }

    /*
     * TODO: showAd() method use for show interstitial ads
     */
    public void showAd() {
        try {
            if (adMobInterstitial != null) {
                adMobInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        adMobInterstitial = null;
                        isAdsReady = false;
                        if (mOnAdsListener != null) {
                            mOnAdsListener.onAdDismiss();
                        }
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                        adMobInterstitial = null;
                        isAdsReady = false;
                        if (mOnAdsListener != null) {
                            String error = "";
                            if (adError != null && adError.toString() != null) {
                                error = "Ad failed to show :: " + adError.toString();
                            } else {
                                error = "Ad failed to show";
                            }
                            mOnAdsListener.onAdFailedToShow(error);
                        }
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        if (mOnAdsListener != null) {
                            mOnAdsListener.onAdShowed();
                        }
                    }

                    @Override
                    public void onAdClicked() {
                        if (mOnAdsListener != null) {
                            mOnAdsListener.onAdClicked();
                        }
                        super.onAdClicked();
                    }
                });
                adMobInterstitial.show(mActivity);
            } else {
                isAdsReady = false;
                if (mOnAdsListener != null) {
                    mOnAdsListener.onAdFailedToShow("Ad failed to show");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            adMobInterstitial = null;
            isAdsReady = false;
            if (mOnAdsListener != null) {
                mOnAdsListener.onAdFailedToShow("Ad failed to show :: " + e.getMessage());
            }
        }
    }

    /*
     * TODO: setOnAdsListener(OnAdsListener onAdsListener)
     *  method use for get ads callback
     */
    public void setOnAdsListener(OnAdsListener onAdsListener) {
        this.mOnAdsListener = onAdsListener;
    }

    /*
     * TODO: setOnAdRevenueListener(OnAdRevenueListener mOnAdRevenueListener)
     *  method use for get ads paid callback
     */
    public void setOnAdRevenueListener(OnAdRevenueListener mOnAdRevenueListener) {
        this.mOnAdRevenueListener = mOnAdRevenueListener;
    }

}
