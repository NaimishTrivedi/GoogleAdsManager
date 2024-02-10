/*
 * Created by Naimish Trivedi on 09/02/24, 1:41 pm
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 09/02/24, 1:40 pm
 */

package naimishtrivedi.in.googleadsmanager;

public class AdsPaidModel {
    private String adsSourceName = "";

    private String adsPlacement = "";
    private String adsUnitId = "";
    private String currencyCode = "";
    private long valueMicros = 0L;

    public String getAdsSourceName() {
        return adsSourceName;
    }

    protected void setAdsSourceName(String adsSourceName) {
        this.adsSourceName = adsSourceName;
    }

    public String getAdsPlacement() {
        return adsPlacement;
    }

    protected void setAdsPlacement(String adsPlacement) {
        this.adsPlacement = adsPlacement;
    }

    public String getAdsUnitId() {
        return adsUnitId;
    }

    protected void setAdsUnitId(String adsUnitId) {
        this.adsUnitId = adsUnitId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    protected void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public long getValueMicros() {
        return valueMicros;
    }

    protected void setValueMicros(long valueMicros) {
        this.valueMicros = valueMicros;
    }
}
