/*
 * Created by Naimish Trivedi on 08/02/24, 7:15 pm
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 08/02/24, 7:15 pm
 */

package naimishtrivedi.in.googleadsmanager.interfaces;

import naimishtrivedi.in.googleadsmanager.RewardEarned;

public interface OnRewardAdsListener {
    public void onAdLoaded();
    public void onAdLoadFailed(String adsId, String error);
    public void onAdFailedToShow(String error);
    public void onAdShowed();
    public void onAdClicked();
    public void onAdDismiss();

    public void onUserRewarded(RewardEarned rewardEarned);
}
