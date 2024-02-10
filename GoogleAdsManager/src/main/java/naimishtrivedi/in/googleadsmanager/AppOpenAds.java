/*
 * Created by Naimish Trivedi on 09/02/24, 6:14 pm
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 09/02/24, 6:14 pm
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
import com.google.android.gms.ads.appopen.AppOpenAd;

import naimishtrivedi.in.googleadsmanager.interfaces.OnAdRevenueListener;
import naimishtrivedi.in.googleadsmanager.interfaces.OnAdsListener;

public class AppOpenAds {
    private String adsId = "";
    private Activity mActivity;
    private boolean isAdsReady;
    private AppOpenAd mAppOpenAd;
    private OnAdsListener mOnAdsListener;
    private OnAdRevenueListener mOnAdRevenueListener;

    private static String TAG = AdsManager.class.getSimpleName();

    /*
     * TODO: AppOpenAds constructor take adsId and activity instance
     *  AppOpenAds(String adsId,Activity mActivity)
     */
    public AppOpenAds(String adsId, Activity mActivity) {
        this.adsId = adsId;
        this.mActivity = mActivity;
    }

    /*
     * TODO: loadAd() method use for load App open ads
     */
    public void loadAd() {
        mAppOpenAd = null;
        try {
            if (adsId == null || adsId.trim().isEmpty()) {
                if (mOnAdsListener != null) {
                    mOnAdsListener.onAdLoadFailed(adsId, "Ad failed to load :: Ads Unit Id is empty.");
                }
                return;
            }

            AppOpenAd.AppOpenAdLoadCallback loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mAppOpenAd = null;
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
                public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                    super.onAdLoaded(appOpenAd);
                    mAppOpenAd = appOpenAd;
                    isAdsReady = true;
                    if (mOnAdsListener != null) {
                        mOnAdsListener.onAdLoaded();
                    }

                    mAppOpenAd.setOnPaidEventListener(new OnPaidEventListener() {
                        @Override
                        public void onPaidEvent(@NonNull AdValue adValue) {
                            AdsPaidModel model = new AdsPaidModel();
                            try {
                                AdapterResponseInfo responseInfo = mAppOpenAd.getResponseInfo().getLoadedAdapterResponseInfo();
                                model.setAdsUnitId(mAppOpenAd.getAdUnitId());
                                if (responseInfo != null) {
                                    model.setAdsSourceName(responseInfo.getAdSourceName());
                                }
                                model.setAdsPlacement("app_open");
                                model.setCurrencyCode(adValue.getCurrencyCode());
                                model.setValueMicros(adValue.getValueMicros());
                            } catch (Exception e) {
                                Log.e(TAG, "Error onPaidEvent :: " + e.getMessage());
                            }
                            if (mOnAdRevenueListener != null) {
                                mOnAdRevenueListener.onPaidEvent(model);
                            }
                        }
                    });
                }
            };

            AdRequest request = new AdRequest.Builder().build();
            AppOpenAd.load(mActivity, adsId, request, loadCallback);

        } catch (Exception e) {
            e.printStackTrace();
            if (mOnAdsListener != null) {
                mOnAdsListener.onAdLoadFailed(adsId, "Ad failed to load :: " + e.getMessage());
            }
        }
    }

    /*
     * TODO: isAdsReady() method use for indicate App open ads
     *  load success or not success
     */
    public boolean isAdsReady() {
        return isAdsReady && mAppOpenAd != null;
    }

    /*
     * TODO: showAd() method use for show App open ads
     */
    public void showAd() {
        try {
            if (mAppOpenAd != null) {
                mAppOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        mAppOpenAd = null;
                        isAdsReady = false;
                        if (mOnAdsListener != null) {
                            mOnAdsListener.onAdDismiss();
                        }
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                        mAppOpenAd = null;
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
                        super.onAdClicked();
                        if (mOnAdsListener != null) {
                            mOnAdsListener.onAdClicked();
                        }
                    }
                });
                mAppOpenAd.show(mActivity);
            } else {
                isAdsReady = false;
                if (mOnAdsListener != null) {
                    mOnAdsListener.onAdFailedToShow("Ad failed to show");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mAppOpenAd = null;
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
