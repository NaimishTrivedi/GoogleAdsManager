/*
 * Created by Naimish Trivedi on 09/02/24, 1:20 pm
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 09/02/24, 1:20 pm
 */

package naimishtrivedi.in.googleadsmanager.model;

public class LogPaidImpression {

    private String adsPlatform = "ad_manager";
    private String adsSourceName = "";

    private String adsPlacement = "";
    private String adsUnitId = "";
    private String currencyCode = "";
    private long valueMicros = 0L;

    public void setAdsPlatform(String adsPlatform) {
        this.adsPlatform = adsPlatform;
    }

    public void setAdsPlacement(String adsPlacement) {
        this.adsPlacement = adsPlacement;
    }

    public void setAdsSourceName(String adsSourceName) {
        this.adsSourceName = adsSourceName;
    }

    public void setAdsUnitId(String adsUnitId) {
        this.adsUnitId = adsUnitId;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setValueMicros(long valueMicros) {
        this.valueMicros = valueMicros;
    }

    public String getAdsPlatform() {
        return adsPlatform;
    }

    public String getAdsSourceName() {
        return adsSourceName;
    }

    public String getAdsPlacement() {
        return adsPlacement;
    }

    public String getAdsUnitId() {
        return adsUnitId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public long getValueMicros() {
        return valueMicros;
    }
}
