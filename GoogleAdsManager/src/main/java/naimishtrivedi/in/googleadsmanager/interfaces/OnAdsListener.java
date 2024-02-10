/*
 * Created by Naimish Trivedi on 08/02/24, 7:15 pm
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 08/02/24, 7:15 pm
 */

package naimishtrivedi.in.googleadsmanager.interfaces;

public interface OnAdsListener {
    public void onAdLoaded();
    public void onAdLoadFailed(String adsId, String error);
    public void onAdFailedToShow(String error);
    public void onAdShowed();
    public void onAdClicked();
    public void onAdDismiss();
}
