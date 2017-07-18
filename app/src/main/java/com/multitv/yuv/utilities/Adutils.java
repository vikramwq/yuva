package com.multitv.yuv.utilities;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiView;
import com.admofi.sdk.lib.and.AdmofiViewCallback;
import com.vmax.android.ads.api.VmaxAdView;
import com.vmax.android.ads.common.VmaxAdListener;
import com.multitv.yuv.R;


/**
 * Created by mkr on 11/9/2016.
 */

public class Adutils {
    private static final String STORE = "AdUtils.STORE";
    private static final String KEY_SDK_ID = "AdUtils.KEY_SDK_ID";
    private static final String TAG = "AdUtils";
    public static final String SDK_NAME_VMAX = "vmax";
    public static final String SDK_NAME_ADMOFI = "admofi";
    private static final int SDK_VMAX = 1;
    private static final int SDK_ADMOFI = 2;
    private Activity mActivity;
    private int mSdkUsed;
    private AdmofiView mAdmofiBanner;
    private AdmofiView mAdmofiInterstitial;
    private VmaxAdView mVmaxAdViewBanner, mVmaxAdViewInterstitial;

    /**
     * Method to get the Sdk Used to show the Ad
     *
     * @param context
     * @return
     */
    public static int getUsedSdk(Context context) {
        Tracer.error(TAG, "getUsedSdk: ");
        return context.getSharedPreferences(STORE, Context.MODE_PRIVATE).getInt(KEY_SDK_ID, SDK_ADMOFI);
    }

    /**
     * Method to set the Sdk Used to show the Ad
     *
     * @param context
     * @param name    Name of sdk used
     */
    public static void setUsedSdk(Context context, String name) {
        Tracer.error(TAG, "setUsedSdk: " + name);
        if (name.equalsIgnoreCase(SDK_NAME_ADMOFI)) {
            context.getSharedPreferences(STORE, Context.MODE_PRIVATE).edit().putInt(KEY_SDK_ID, SDK_ADMOFI).commit();
        } else if (name.equalsIgnoreCase(SDK_NAME_VMAX)) {
            context.getSharedPreferences(STORE, Context.MODE_PRIVATE).edit().putInt(KEY_SDK_ID, SDK_VMAX).commit();
        } else {
            context.getSharedPreferences(STORE, Context.MODE_PRIVATE).edit().putInt(KEY_SDK_ID, SDK_ADMOFI).commit();
        }
    }

    /**
     * Constructor
     *
     * @param activity
     * @param sdkUsed  Define the parent SDK whose banner is need to shown
     */
    public Adutils(Activity activity, int sdkUsed) {
        Tracer.error(TAG, "AdUtils:" + sdkUsed + " " + activity);
        mActivity = activity;
        mSdkUsed = sdkUsed;
    }

    /**
     * Method to notify that Activity is resumed<br>
     * This should be called from Activity
     */
    public void onResume() {
        Tracer.error(TAG, "onResume: ");
        if (mAdmofiBanner != null) {
            mAdmofiBanner.onAdmEventResume();
        }
        if (mAdmofiInterstitial != null) {
            mAdmofiInterstitial.onAdmEventResume();
        }
        if (mVmaxAdViewBanner != null) {
            mVmaxAdViewBanner.onResume();
        }
    }

    /**
     * Method to notify that Activity is stopped<br>
     * This should be called from Activity
     */
    public void onPause() {
        Tracer.error(TAG, "onPause: ");
        if (mAdmofiBanner != null) {
            mAdmofiBanner.onAdmEventPause();
        }
        if (mAdmofiInterstitial != null) {
            mAdmofiInterstitial.onAdmEventPause();
        }
        if (mVmaxAdViewBanner != null) {
            mVmaxAdViewBanner.onPause();
        }
    }

    /**
     * Method called to show the Interstitial Ad.
     */
    public void showInterstitial() {
        Tracer.error(TAG, "showInterstitial: " + mSdkUsed);
        switch (mSdkUsed) {
            case SDK_VMAX:
                showInterstitialVMax();
                break;
            default:
                showInterstitialAdMofi();
        }
    }

    /**
     * Method called to show the Banner Add, Call this Method from Activity.OnCreate()
     *
     * @param parentrelativeLayout View Group in which banner add is shown Used.<BR> <b>PARAM Width.WRAP_CONTENT, Height.WRAP_CONTENT</b>
     * @param layoutParams         Param of the parent relative layout
     */
    public void showBanner(RelativeLayout parentrelativeLayout, RelativeLayout.LayoutParams layoutParams) {
        Tracer.error(TAG, "showBanner: " + mSdkUsed);
        switch (mSdkUsed) {
            case SDK_VMAX:
                showBannerVMax(parentrelativeLayout, layoutParams);
                break;
            default:
                showBannerAdMofi(parentrelativeLayout, layoutParams);
        }

    }

    /**
     * Method to show the banner ad of Ad Mofi Sdk
     *
     * @param parentRelativeLayout
     * @param layoutParams
     */
    private void showBannerAdMofi(RelativeLayout parentRelativeLayout, RelativeLayout.LayoutParams layoutParams) {
        Tracer.error(TAG, "showBannerAdMofi: ");
        mAdmofiBanner = new AdmofiView(mActivity, mActivity.getString(R.string.admofi_banner), AdmofiConstants.AD_TYPE_BANNER, null);
        final FrameLayout frameLayout = new FrameLayout(mActivity);
        FrameLayout.LayoutParams framLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        frameLayout.addView(mAdmofiBanner, framLayoutParams);
        frameLayout.setVisibility(View.GONE);
        parentRelativeLayout.addView(frameLayout, layoutParams);
        mAdmofiBanner.setAdmofiViewCallbackListener(new AdmofiViewCallback() {
            @Override
            public void onAdmAdLoaded(AdmofiView admofiView, boolean b) {
                Tracer.error(TAG, "Banner.onAdmAdLoaded: ");
            }

            @Override
            public void onAdmAdReady(AdmofiView admofiView) {
                Tracer.error(TAG, "Banner.onAdmAdReady: ");
                try {
                    frameLayout.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Tracer.error(TAG, "onAdmAdReady: " + e.getMessage());
                }
            }

            @Override
            public void onAdmErrorNoNetwork(AdmofiView admofiView) {
            }

            @Override
            public void onAdmException(AdmofiView admofiView, Exception e) {
            }

            @Override
            public void onAdmIllegalHttpStatusCode(AdmofiView admofiView, int i, String s) {
            }

            @Override
            public void onAdmAdClicked(AdmofiView admofiView) {
            }

            @Override
            public void onAdmCompleted(AdmofiView admofiView) {
                Tracer.error(TAG, "Banner.onAdmCompleted: ");
            }

            @Override
            public void onAdmNoAd(AdmofiView admofiView) {
                Tracer.error(TAG, "Banner.onAdmNoAd: ");
            }
        });
        mAdmofiBanner.startfetch();
    }

    /**
     * Method to show the banner ad of VMax Sdk
     *
     * @param parentrelativeLayout
     * @param layoutParams
     */
    private void showBannerVMax(final RelativeLayout parentrelativeLayout, final RelativeLayout.LayoutParams layoutParams) {
        Tracer.error(TAG, "showBannerVMax: ");
        mVmaxAdViewBanner = new VmaxAdView(mActivity, "288fdfe9", VmaxAdView.UX_BANNER);
        mVmaxAdViewBanner.setAdListener(new VmaxAdListener() {
            @Override
            public VmaxAdView didFailedToLoadAd(String s) {
                return null;
            }

            @Override
            public VmaxAdView didFailedToCacheAd(String s) {
                return null;
            }

            @Override
            public void adViewDidLoadAd(VmaxAdView adView) {
                try {
                    parentrelativeLayout.removeAllViews();
                    parentrelativeLayout.addView(adView, layoutParams);
                    parentrelativeLayout.setVisibility(View.VISIBLE);
                    adView.showAd();
                } catch (Exception e) {
                    Tracer.error(TAG, "showBannerVMax().willPresentAd: " + e.getMessage());
                }
            }

            @Override
            public void adViewDidCacheAd(VmaxAdView adView) {
            }

            @Override
            public void didInteractWithAd(VmaxAdView adView) {
            }

            @Override
            public void willDismissAd(VmaxAdView adView) {
                parentrelativeLayout.removeAllViews();
            }

            @Override
            public void willPresentAd(VmaxAdView adView) {

            }

            @Override
            public void willLeaveApp(VmaxAdView adView) {
            }

            @Override
            public void onVideoView(boolean b, int i, int i1) {
            }

            @Override
            public void onAdExpand() {
            }

            @Override
            public void onAdCollapsed() {
            }
        });
        mVmaxAdViewBanner.loadAd();
    }

    /**
     * Method to show the AdMofi Interstitial
     */
    private void showInterstitialAdMofi() {
        Tracer.error(TAG, "showInterstitialAdMofi: ");
        if (mAdmofiInterstitial != null) {
            mAdmofiInterstitial.removeAdmofiViewCallbackListener();
        }
        mAdmofiInterstitial = new AdmofiView(mActivity, mActivity.getString(R.string.admofi_interstitial), AdmofiConstants.AD_TYPE_INTERSTITIAL, null);
        mAdmofiInterstitial.setAdmofiViewCallbackListener(new AdmofiViewCallback() {
            @Override
            public void onAdmAdLoaded(AdmofiView admofiView, boolean b) {
                Tracer.error(TAG, "Inter.onAdmAdLoaded: ");
            }

            @Override
            public void onAdmAdReady(AdmofiView admofiView) {
                Tracer.error(TAG, "Inter.onAdmAdReady: ");
                try {
                    mAdmofiInterstitial.showInterstitial();
                } catch (Exception e) {
                    e.printStackTrace();
                    Tracer.error(TAG, "onAdmAdLoaded: " + e.getMessage());
                }
            }

            @Override
            public void onAdmErrorNoNetwork(AdmofiView admofiView) {
                Tracer.error(TAG, "Inter.onAdmErrorNoNetwork: ");
            }

            @Override
            public void onAdmException(AdmofiView admofiView, Exception e) {
                Tracer.error(TAG, "Inter.onAdmException: ");
            }

            @Override
            public void onAdmIllegalHttpStatusCode(AdmofiView admofiView, int i, String s) {
                Tracer.error(TAG, "Inter.onAdmIllegalHttpStatusCode: ");
            }

            @Override
            public void onAdmAdClicked(AdmofiView admofiView) {
                Tracer.error(TAG, "Inter.onAdmAdClicked: ");
            }

            @Override
            public void onAdmCompleted(AdmofiView admofiView) {
                Tracer.error(TAG, "Inter.onAdmCompleted: ");
            }

            @Override
            public void onAdmNoAd(AdmofiView admofiView) {
                Tracer.error(TAG, "Inter.onAdmNoAd: ");
            }
        });
        mAdmofiInterstitial.startfetch();
    }


    /**
     * Method to show the VMAX Interstitial
     */
    private void showInterstitialVMax() {
        Tracer.error(TAG, "showInterstitialVMax: ");
        if (mVmaxAdViewInterstitial != null) {
            mVmaxAdViewInterstitial.cancelAd();
        }
        mVmaxAdViewInterstitial = new VmaxAdView(mActivity, "b1868e53", VmaxAdView.UX_INTERSTITIAL);
        mVmaxAdViewInterstitial.setAdListener(new VmaxAdListener() {

            @Override
            public void adViewDidCacheAd(VmaxAdView adView) {
            }

            @Override
            public void adViewDidLoadAd(VmaxAdView vmaxAdView) {

            }

            @Override
            public VmaxAdView didFailedToLoadAd(String s) {
                return null;
            }

            @Override
            public VmaxAdView didFailedToCacheAd(String s) {
                return null;
            }

            @Override
            public void didInteractWithAd(VmaxAdView vmaxAdView) {
            }

            @Override
            public void willPresentAd(VmaxAdView vmaxAdView) {

            }

            @Override
            public void willDismissAd(VmaxAdView vmaxAdView) {

            }

            @Override
            public void onVideoView(boolean b, int i, int i1) {
            }

            @Override
            public void onAdExpand() {
            }

            @Override
            public void onAdCollapsed() {

            }
        });
        mVmaxAdViewInterstitial.cacheAd(); // This will request Ad in background
        mVmaxAdViewInterstitial.loadAd();
    }
}
