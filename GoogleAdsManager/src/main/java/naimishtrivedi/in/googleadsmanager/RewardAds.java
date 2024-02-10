/*
 * Created by Naimish Trivedi on 09/02/24, 4:58 pm
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 09/02/24, 4:58 pm
 */

package naimishtrivedi.in.googleadsmanager;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import naimishtrivedi.in.googleadsmanager.interfaces.OnAdRevenueListener;
import naimishtrivedi.in.googleadsmanager.interfaces.OnRewardAdsListener;

public class RewardAds {
    private String adsId = "";
    private Activity mActivity;
    private boolean isAdsReady;

    private RewardItem mRewardItem = null;

    private RewardedAd mRewardedAd = null;

    private OnRewardAdsListener mOnRewardAdsListener;
    private OnAdRevenueListener mOnAdRevenueListener;

    private static String TAG = AdsManager.class.getSimpleName();

    /*
     * TODO: RewardAds constructor take adsId and activity instance
     *  RewardAds(String adsId,Activity mActivity)
     */
    public RewardAds(String adsId, Activity mActivity) {
        this.adsId = adsId;
        this.mActivity = mActivity;
    }

    /*
     * TODO: loadAd() method use for load reward ads
     */
    public void loadAd() {
        mRewardedAd = null;
        mRewardItem = null;
        try {
            if (adsId == null || adsId.trim().isEmpty()) {
                if (mOnRewardAdsListener != null) {
                    mOnRewardAdsListener.onAdLoadFailed(adsId, "Ad failed to load :: Ads Unit Id is empty.");
                }
                return;
            }

            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(mActivity, adsId, adRequest, new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mRewardedAd = null;
                    mRewardItem = null;
                    isAdsReady = false;
                    if (mOnRewardAdsListener != null) {
                        String error = "";
                        if (loadAdError != null && loadAdError.toString() != null) {
                            error = "Ad failed to load :: " + loadAdError.toString();
                        } else {
                            error = "Ad failed to load";
                        }
                        mOnRewardAdsListener.onAdLoadFailed(adsId, error);
                    }
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    super.onAdLoaded(rewardedAd);
                    mRewardedAd = rewardedAd;
                    isAdsReady = true;
                    if (mOnRewardAdsListener != null) {
                        mOnRewardAdsListener.onAdLoaded();
                    }

                    mRewardedAd.setOnPaidEventListener(new OnPaidEventListener() {
                        @Override
                        public void onPaidEvent(@NonNull AdValue adValue) {
                            AdsPaidModel model = new AdsPaidModel();
                            try {
                                AdapterResponseInfo responseInfo = mRewardedAd.getResponseInfo().getLoadedAdapterResponseInfo();
                                model.setAdsUnitId(mRewardedAd.getAdUnitId());
                                if (responseInfo != null) {
                                    model.setAdsSourceName(responseInfo.getAdSourceName());
                                }
                                model.setAdsPlacement("reward");
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
            });

        } catch (Exception e) {
            e.printStackTrace();
            if (mOnRewardAdsListener != null) {
                mOnRewardAdsListener.onAdLoadFailed(adsId, "Ad failed to load :: " + e.getMessage());
            }
        }
    }

    /*
     * TODO: isAdsReady() method use for indicate reward ads
     *  load success or not success
     */
    public boolean isAdsReady() {
        return isAdsReady && mRewardedAd != null;
    }

    /*
     * TODO: showAd() method use for show reward ads
     */
    public void showAd() {
        try {
            if (mRewardedAd != null) {
                mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        if (mOnRewardAdsListener != null) {
                            mOnRewardAdsListener.onAdClicked();
                        }
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        mRewardedAd = null;
                        mRewardItem = null;
                        isAdsReady = false;
                        if (mOnRewardAdsListener != null) {
                            mOnRewardAdsListener.onAdDismiss();
                        }
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        mRewardedAd = null;
                        mRewardItem = null;
                        isAdsReady = false;
                        if (mOnRewardAdsListener != null) {
                            String error = "";
                            if (adError != null && adError.toString() != null) {
                                error = "Ad failed to show :: " + adError.toString();
                            } else {
                                error = "Ad failed to show";
                            }
                            mOnRewardAdsListener.onAdFailedToShow(error);
                        }
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        if (mOnRewardAdsListener != null) {
                            mOnRewardAdsListener.onAdShowed();
                        }
                    }
                });
                mRewardedAd.show(mActivity, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        mRewardItem = rewardItem;
                        RewardEarned rewardEarned = new RewardEarned();
                        rewardEarned.setAmount(mRewardItem.getAmount());
                        rewardEarned.setType(mRewardItem.getType());
                        if (mOnRewardAdsListener != null) {
                            mOnRewardAdsListener.onUserRewarded(rewardEarned);
                        }
                    }
                });
            } else {
                mRewardItem = null;
                isAdsReady = false;
                if (mOnRewardAdsListener != null) {
                    mOnRewardAdsListener.onAdFailedToShow("Ad failed to show");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mRewardedAd = null;
            mRewardItem = null;
            isAdsReady = false;
            if (mOnRewardAdsListener != null) {
                mOnRewardAdsListener.onAdFailedToShow("Ad failed to show :: " + e.getMessage());
            }
        }
    }

    /*
     * TODO: setOnRewardAdsListener(OnRewardAdsListener onRewardAdsListener)
     *  method use for get ads callback
     */
    public void setOnRewardAdsListener(OnRewardAdsListener onRewardAdsListener) {
        this.mOnRewardAdsListener = onRewardAdsListener;
    }

    /*
     * TODO: setOnAdRevenueListener(OnAdRevenueListener mOnAdRevenueListener)
     *  method use for get ads paid callback
     */
    public void setOnAdRevenueListener(OnAdRevenueListener mOnAdRevenueListener) {
        this.mOnAdRevenueListener = mOnAdRevenueListener;
    }
}
