/*
 * Created by Naimish Trivedi on 02/02/24, 3:41 pm
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 02/02/24, 3:41 pm
 */

package naimishtrivedi.in.googleadsmanager;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

import naimishtrivedi.in.googleadsmanager.interfaces.OnAdRevenueListener;
import naimishtrivedi.in.googleadsmanager.interfaces.OnAdsViewListener;

public class NativeBannerAds extends FrameLayout {

    private FrameLayout mFrmShimmerAnimation;
    private FrameLayout mFrmAdsContainer;
    private int mAnimationViewId = 0;
    private int adsLayoutId = 0;
    private boolean showAnimation = true;

    private OnAdsViewListener mOnAdsViewListener;
    private OnAdRevenueListener mOnAdRevenueListener;
    private static String TAG = AdsManager.class.getSimpleName();

    public NativeBannerAds(@NonNull Context context) {
        super(context);
        inIt();
    }

    public NativeBannerAds(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        inIt();
    }

    public NativeBannerAds(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        inIt();
    }

    protected void initAttrs(AttributeSet attrs) {

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.NativeBannerAds);
        try {
            adsLayoutId = a.getResourceId(R.styleable.NativeBannerAds_adsLayoutId, R.layout.layout_native_admob);
        } finally {
            a.recycle();
        }


        TypedArray googleAdsManager = getContext().obtainStyledAttributes(attrs, R.styleable.googleadsmanager);
        try {
            mAnimationViewId = googleAdsManager.getResourceId(R.styleable.googleadsmanager_layout_animation, R.layout.ad_native_shimmer_effect);
            showAnimation = googleAdsManager.getBoolean(R.styleable.googleadsmanager_showAnimation, true);
        } finally {
            googleAdsManager.recycle();
        }
    }

    private void inIt() {
        if (showAnimation) {
            mFrmShimmerAnimation = new FrameLayout(getContext());
            addView(mFrmShimmerAnimation);

            try {
                View animationView = LayoutInflater.from(getContext()).inflate(mAnimationViewId, this, false);

                mFrmShimmerAnimation.addView(animationView);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG,"Animation resource is not a layout file.");
                mAnimationViewId = R.layout.ad_native_shimmer_effect;
                View animationView = LayoutInflater.from(getContext()).inflate(mAnimationViewId, this, false);
                mFrmShimmerAnimation.addView(animationView);
            }
        }

        mFrmAdsContainer = new FrameLayout(getContext());
        addView(mFrmAdsContainer);
    }

    private void setNativeAds(NativeAd nativeAd) {
        NativeAdView adView = (NativeAdView) LayoutInflater.from(getContext()).inflate(R.layout.ads_native_admob, null);

        try {
            if (adsLayoutId != 0) {
                ViewStub nativeAdMobLayout = adView.findViewById(R.id.native_admob);
                nativeAdMobLayout.setLayoutResource(adsLayoutId);
                nativeAdMobLayout.inflate();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"Ads resource is not a layout file.");
            ViewStub nativeAdMobLayout = adView.findViewById(R.id.native_admob);
            nativeAdMobLayout.setLayoutResource(R.layout.layout_native_admob);
            nativeAdMobLayout.inflate();
        }
        inflateAdMobNative(adView, nativeAd);
        mFrmAdsContainer.removeAllViews();
        mFrmAdsContainer.addView(adView);
    }

    /*
     * TODO: show ads using showAds(String adId) method
     *  add google banner ads id as a parameter value
     */
    public void showAds(String adId) {
        try {
            showAdsAnimation();

            if (adId == null || adId.equals("")) {
                if (mOnAdsViewListener != null) {
                    mOnAdsViewListener.onAdsFailure(AdsManager.AdResource.NATIVE.name(),"Ads Unit Id is empty.");
                }
                mFrmAdsContainer.removeAllViews();
                mFrmAdsContainer.setVisibility(View.GONE);
                return;
            }
            AdLoader.Builder builder = new AdLoader.Builder(getContext(), adId);

            builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                @Override
                public void onNativeAdLoaded(NativeAd nativeAd) {
                    setNativeAds(nativeAd);
                    //final ViewGroup viewGroup = null;
                    // NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ads_native_admob, viewGroup);
                    //inflateAdMobNative(adView, nativeAd);
                    // adLoaded.removeAllViews();
                    // adLoaded.addView(adView);
                    nativeAd.setOnPaidEventListener(new OnPaidEventListener() {
                        @Override
                        public void onPaidEvent(@NonNull AdValue adValue) {
                            AdsPaidModel model = new AdsPaidModel();
                            try {
                                AdapterResponseInfo responseInfo = nativeAd.getResponseInfo().getLoadedAdapterResponseInfo();
                                model.setAdsUnitId(adId);
                                if(responseInfo != null) {
                                    model.setAdsSourceName(responseInfo.getAdSourceName());
                                }
                                model.setAdsPlacement("native");
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

            VideoOptions videoOptions = new VideoOptions.Builder()
                    .setStartMuted(true)
                    .build();

            NativeAdOptions adOptions = new NativeAdOptions.Builder()
                    .setVideoOptions(videoOptions)
                    .build();

            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader = builder.withAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    hideAdsAnimation();
                    if(mOnAdsViewListener!=null){
                        mOnAdsViewListener.onAdsSuccess(AdsManager.AdResource.NATIVE.name());
                    }
                    super.onAdLoaded();
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    mFrmAdsContainer.removeAllViews();
                    mFrmAdsContainer.setVisibility(View.GONE);
                    // viewShimmerEffect.setVisibility(View.GONE);
                    if(mOnAdsViewListener!=null){
                        mOnAdsViewListener.onAdsFailure(AdsManager.AdResource.NATIVE.name(),loadAdError.toString());
                    }
                    super.onAdFailedToLoad(loadAdError);
                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } catch (Exception e) {
            e.printStackTrace();
            mFrmAdsContainer.removeAllViews();
            mFrmAdsContainer.setVisibility(View.GONE);
            if(mOnAdsViewListener!=null){
                mOnAdsViewListener.onAdsFailure(AdsManager.AdResource.NATIVE.name(),e.getMessage());
            }
        }
    }

    private void inflateAdMobNative(NativeAdView adView, NativeAd nativeAd) {
        try {
            // Set other ad assets.

            // The headline is guaranteed to be in every UnifiedNativeAd.
            TextView ad_headline = adView.findViewById(R.id.ad_headline);
            if (ad_headline != null) {
                if (nativeAd.getHeadline() != null && !nativeAd.getHeadline().isEmpty()) {
                    ad_headline.setVisibility(View.VISIBLE);
                    ad_headline.setText(nativeAd.getHeadline());
                    adView.setHeadlineView(ad_headline);
                } else {
                    ad_headline.setVisibility(View.GONE);
                }
            }


            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            TextView ad_body = adView.findViewById(R.id.ad_body);
            if (ad_body != null) {
                if (nativeAd.getBody() != null && !nativeAd.getBody().isEmpty()) {
                    ad_body.setVisibility(View.VISIBLE);
                    ad_body.setText(nativeAd.getBody());
                    adView.setBodyView(ad_body);
                } else {
                    ad_body.setVisibility(View.GONE);
                }
            }

            Button ad_call_to_action = adView.findViewById(R.id.ad_call_to_action);
            if (ad_call_to_action != null) {
                if (nativeAd.getCallToAction() != null && !nativeAd.getCallToAction().isEmpty()) {
                    ad_call_to_action.setVisibility(View.VISIBLE);
                    ad_call_to_action.setText(nativeAd.getCallToAction());
                    adView.setCallToActionView(ad_call_to_action);
                } else {
                    ad_call_to_action.setVisibility(View.GONE);
                }
            }

            ImageView ad_app_icon = adView.findViewById(R.id.ad_app_icon);
            if (ad_app_icon != null) {
                if (nativeAd.getIcon() != null && nativeAd.getIcon().getDrawable() != null) {
                    ad_app_icon.setVisibility(View.VISIBLE);
                    ad_app_icon.setImageDrawable(nativeAd.getIcon().getDrawable());
                    adView.setIconView(ad_app_icon);
                } else {
                    ad_app_icon.setVisibility(View.GONE);
                }
            }


            TextView ad_price = adView.findViewById(R.id.ad_price);
            if (ad_price != null) {
                if (nativeAd.getPrice() != null && !nativeAd.getPrice().isEmpty()) {
                    ad_price.setVisibility(View.VISIBLE);
                    ad_price.setText(nativeAd.getPrice());
                    adView.setPriceView(ad_price);
                } else {
                    ad_price.setVisibility(View.GONE);
                }
            }

            RatingBar ad_stars = adView.findViewById(R.id.ad_stars);
            if (ad_stars != null) {
                if (nativeAd.getStarRating() != null) {
                    ad_stars.setVisibility(View.VISIBLE);
                    ad_stars.setRating(nativeAd.getStarRating().floatValue());
                    adView.setStarRatingView(ad_stars);
                } else {
                    ad_stars.setVisibility(View.GONE);
                }
            }

            TextView ad_store = adView.findViewById(R.id.ad_store);
            if (ad_store != null) {
                if (nativeAd.getStore() != null && !nativeAd.getStore().isEmpty()) {
                    ad_store.setVisibility(View.VISIBLE);
                    ad_store.setText(nativeAd.getStore());
                    adView.setStoreView(ad_store);
                } else {
                    ad_store.setVisibility(View.GONE);
                }
            }

            TextView ad_advertiser = adView.findViewById(R.id.ad_advertiser);
            if (ad_advertiser != null) {
                if (nativeAd.getAdvertiser() != null && !nativeAd.getAdvertiser().isEmpty()) {
                    ad_advertiser.setVisibility(View.VISIBLE);
                    ad_advertiser.setText(nativeAd.getAdvertiser());
                    adView.setAdvertiserView(ad_advertiser);
                } else {
                    ad_advertiser.setVisibility(View.GONE);
                }
            }

            MediaView mediaView = adView.findViewById(R.id.ad_media);
            if (mediaView != null) {
                if (nativeAd.getMediaContent() != null) {
                    mediaView.setVisibility(View.VISIBLE);
                    mediaView.setMediaContent(nativeAd.getMediaContent());
                    adView.setMediaView(mediaView);
                } else {
                    mediaView.setVisibility(View.GONE);
                }
            }
            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad viewTop with this native ad. The SDK will populate the adView's MediaView
            // with the media content from this native ad.
            adView.setNativeAd(nativeAd);

            /*MediaContent mediaContent = nativeAd.getMediaContent();
            VideoController vc = mediaContent.getVideoController();
            if (vc.hasVideoContent()) {


                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        super.onVideoEnd();
                    }
                });
            }*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*
     * TODO: set animation layout using called setAdsAnimationLayout(int layoutResIds) method
     *  add animation layout res id as a parameter value
     */
    public void setAdsAnimationLayout(int layoutResIds) {
        mAnimationViewId = layoutResIds;
        if (mFrmShimmerAnimation != null) {
            mFrmShimmerAnimation.removeAllViews();
            try {
                View animationView = LayoutInflater.from(getContext()).inflate(mAnimationViewId, this, false);
                mFrmShimmerAnimation.addView(animationView);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG,"Animation resource is not a layout file.");
                mAnimationViewId = R.layout.ad_native_shimmer_effect;
                View animationView = LayoutInflater.from(getContext()).inflate(mAnimationViewId, this, false);
                mFrmShimmerAnimation.addView(animationView);
            }
        }
    }

    /*
     * TODO: show animation using called showAdsAnimation() method
     */
    public void showAdsAnimation() {
        if (mFrmShimmerAnimation != null) {
            mFrmShimmerAnimation.setVisibility(View.VISIBLE);
        }
    }

    /*
     * TODO: hide animation using called hideAdsAnimation() method
     */
    public void hideAdsAnimation() {
        if (mFrmShimmerAnimation != null) {
            mFrmShimmerAnimation.setVisibility(View.GONE);
        }
    }

    /*
     * TODO: set ads layout using called setAdsLayout(int layoutResId) method
     *  add native ads layout res id as a parameter value
     */
    public void setAdsLayout(int layoutResId) {
        this.adsLayoutId = layoutResId;
    }

    /*
     * TODO: Ads callback using setOnAdsViewListener(OnAdsViewListener onAdsViewListener) method
     *  use OnAdsViewListener interface as a parameter value
     */
    public void setOnAdsViewListener(OnAdsViewListener onAdsViewListener) {
        this.mOnAdsViewListener = onAdsViewListener;
    }

    /*
     * TODO: setOnAdRevenueListener(OnAdRevenueListener mOnAdRevenueListener)
     *  method use for get ads paid callback
     */
    public void setOnAdRevenueListener(OnAdRevenueListener mOnAdRevenueListener) {
        this.mOnAdRevenueListener = mOnAdRevenueListener;
    }
}
