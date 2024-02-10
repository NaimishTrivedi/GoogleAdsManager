package naimishtrivedi.in.googleadsmanager.interfaces;

public interface OnAdsViewListener {
    public void onAdsSuccess(String adResource);
    public void onAdsFailure(String adResource,String errorMsg);
}
