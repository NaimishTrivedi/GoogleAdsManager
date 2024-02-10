/*
 * Created by Naimish Trivedi on 05/02/24, 5:41 pm
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 05/02/24, 5:41 pm
 */

package naimishtrivedi.in.googleadsmanager;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Collections;

import naimishtrivedi.in.googleadsmanager.model.LogPaidImpression;

public class AdsManager {

    enum AdResource {
        BANNER,
        NATIVE
    }

 /*   public static void initialize(Context context) {
        MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(Collections.singletonList("1556FFC41A3C318C805A1F084C6DC460")).build());
        MobileAds.initialize(context);
    }*/

    public static void logPaidImpression(Context context, LogPaidImpression logPaidImpression) {
        try {
            Bundle params = new Bundle();
            params.putString(FirebaseAnalytics.Param.AD_PLATFORM, logPaidImpression.getAdsPlatform());
            params.putString(FirebaseAnalytics.Param.AD_SOURCE, logPaidImpression.getAdsSourceName());
            params.putString(FirebaseAnalytics.Param.AD_FORMAT, logPaidImpression.getAdsPlacement());
            params.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, logPaidImpression.getAdsUnitId());
            params.putString(FirebaseAnalytics.Param.CURRENCY, logPaidImpression.getCurrencyCode());
            params.putDouble(FirebaseAnalytics.Param.VALUE, logPaidImpression.getValueMicros() / 1000000.0);
            FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
