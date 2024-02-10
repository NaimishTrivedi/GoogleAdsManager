/*
 * Created by Naimish Trivedi on 01/02/24, 3:38 pm
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 01/02/24, 3:38 pm
 */

package naimishtrivedi.in.googleadsmanager;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;

import naimishtrivedi.in.googleadsmanager.interfaces.OnAdRevenueListener;
import naimishtrivedi.in.googleadsmanager.interfaces.OnAdsViewListener;

public class BannerAds extends FrameLayout {

    private FrameLayout mFrmShimmerAnimation;
    private FrameLayout mFrmAdsContainer;
    private int mAnimationViewId = 0;
    private boolean showAnimation = true;
    private OnAdsViewListener mOnAdsViewListener;
    private OnAdRevenueListener mOnAdRevenueListener;
    private static String TAG = AdsManager.class.getSimpleName();
    private int bannerSize = 0;

    /*
     * TODO: enum for Ads banner Size
     */
    private enum AdsBannerSize {
        DEFAULT,
        LARGE
    }

    public BannerAds(@NonNull Context context) {
        super(context);
        inIt();
    }

    public BannerAds(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        inIt();
    }

    public BannerAds(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        inIt();
    }

    protected void initAttrs(AttributeSet attrs) {

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BannerAds);
        try {
            bannerSize = a.getInt(R.styleable.BannerAds_adsBannerSize, AdsBannerSize.DEFAULT.ordinal());
        } finally {
            a.recycle();
        }


        TypedArray googleAdsManager = getContext().obtainStyledAttributes(attrs, R.styleable.googleadsmanager);
        try {
            int defaultAnimationId = 0;
            if (bannerSize == AdsBannerSize.LARGE.ordinal()) {
                defaultAnimationId = R.layout.ad_large_banner_animation;
            } else {
                defaultAnimationId = R.layout.ad_banner_animation;
            }
            mAnimationViewId = googleAdsManager.getResourceId(R.styleable.googleadsmanager_layout_animation, defaultAnimationId);
            showAnimation = googleAdsManager.getBoolean(R.styleable.googleadsmanager_showAnimation, true);
        } finally {
            googleAdsManager.recycle();
        }
    }

    private void inIt() {
        if (showAnimation) {
            mFrmShimmerAnimation = new FrameLayout(getContext());
            //mFrmShimmerAnimation.setLayoutParams(this.getLayoutParams());
            addView(mFrmShimmerAnimation);

            try {
                View animationView = LayoutInflater.from(getContext()).inflate(mAnimationViewId, this, false);

                mFrmShimmerAnimation.addView(animationView);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG,"Animation resource is not a layout file.");
                int defaultAnimationId = 0;
                if (bannerSize == AdsBannerSize.LARGE.ordinal()) {
                    defaultAnimationId = R.layout.ad_large_banner_animation;
                } else {
                    defaultAnimationId = R.layout.ad_banner_animation;
                }
                mAnimationViewId = defaultAnimationId;
                View animationView = LayoutInflater.from(getContext()).inflate(mAnimationViewId, this, false);
                mFrmShimmerAnimation.addView(animationView);
            }
        }

        mFrmAdsContainer = new FrameLayout(getContext());
        // mFrmAdsContainer.setLayoutParams(this.getLayoutParams());
        addView(mFrmAdsContainer);
    }

    /*
     * TODO: show ads using showAds(String adId) method
     *  add google banner ads id as a parameter value
     */
    public void showAds(String adId) {
        mFrmAdsContainer.removeAllViews();
        showAdsAnimation();
        try {
            if(adId == null){
                mFrmAdsContainer.setVisibility(View.GONE);
                if (mOnAdsViewListener != null) {
                    mOnAdsViewListener.onAdsFailure(AdsManager.AdResource.BANNER.name(),"Ads Unit Id is empty.");
                }
                return;
            }
            // Request for Ads
            AdRequest adRequest = new AdRequest.Builder().build();

            final AdView adView = new AdView(getContext());
            if (bannerSize == AdsBannerSize.LARGE.ordinal()) {
                adView.setAdSize(AdSize.LARGE_BANNER);
            } else {
                AdSize adSize = getAdSize((Activity) getContext());
                adView.setAdSize(adSize);
            }
            adView.setAdUnitId(adId);
            mFrmAdsContainer.addView(adView);

            final AdListener listener = new AdListener() {

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    hideAdsAnimation();
                    mFrmAdsContainer.setVisibility(View.VISIBLE);
                    if (mOnAdsViewListener != null) {
                        mOnAdsViewListener.onAdsSuccess(AdsManager.AdResource.BANNER.name());
                    }
                    adView.setOnPaidEventListener(new OnPaidEventListener() {
                        @Override
                        public void onPaidEvent(@NonNull AdValue adValue) {
                            AdsPaidModel model = new AdsPaidModel();
                            try {
                                AdapterResponseInfo responseInfo = adView.getResponseInfo().getLoadedAdapterResponseInfo();
                                model.setAdsUnitId(adView.getAdUnitId());
                                if(responseInfo != null) {
                                    model.setAdsSourceName(responseInfo.getAdSourceName());
                                }
                                model.setAdsPlacement("banner");
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

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    // viewShimmerEffect.setVisibility(View.GONE);
                    mFrmAdsContainer.setVisibility(View.GONE);
                    mFrmAdsContainer.removeAllViews();
                    if (mOnAdsViewListener != null) {
                        mOnAdsViewListener.onAdsFailure(AdsManager.AdResource.BANNER.name(),loadAdError.toString());
                    }
                }
            };

            adView.setAdListener(listener);
            if (!adView.getAdUnitId().equals("")) {
                adView.loadAd(adRequest);
            } else {
                mFrmAdsContainer.setVisibility(View.GONE);
                mFrmAdsContainer.removeAllViews();
                if (mOnAdsViewListener != null) {
                    mOnAdsViewListener.onAdsFailure(AdsManager.AdResource.BANNER.name(),"Ads Unit Id is empty.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mFrmAdsContainer.setVisibility(View.GONE);
            if (mOnAdsViewListener != null) {
                mOnAdsViewListener.onAdsFailure(AdsManager.AdResource.BANNER.name(),e.getMessage());
            }
        }
    }

    private AdSize getAdSize(Activity activity) {
        // float widthPixels = activity.getResources().getDisplayMetrics().widthPixels;
        //  float density = activity.getResources().getDisplayMetrics().density;

        // int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, getAdaptiveBannerWidth(activity));
    }

    private int getAdaptiveBannerWidth(Context context) {
        int widthPixels = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = windowManager.getCurrentWindowMetrics();
            Rect applicationBounds = windowMetrics.getBounds();
            widthPixels = applicationBounds.width();
        } else {
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);
            widthPixels = outMetrics.widthPixels;
        }

        //Px to dp convert
        return (int) Math.ceil((double) (((float) widthPixels) / context.getResources().getDisplayMetrics().density));
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
                int defaultAnimationId = 0;
                if (bannerSize == AdsBannerSize.LARGE.ordinal()) {
                    defaultAnimationId = R.layout.ad_large_banner_animation;
                } else {
                    defaultAnimationId = R.layout.ad_banner_animation;
                }
                mAnimationViewId = defaultAnimationId;
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
}
