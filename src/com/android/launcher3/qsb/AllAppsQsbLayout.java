/*
 * Copyright (C) 2018 CypherOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.launcher3.qsb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.launcher3.BaseRecyclerView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsContainerView;
import com.android.launcher3.allapps.AllAppsRecyclerView;
import com.android.launcher3.allapps.AlphabeticalAppsList;
import com.android.launcher3.allapps.SearchUiManager;
import com.android.launcher3.uioverrides.WallpaperColorInfo;
import com.android.launcher3.util.Themes;

public class AllAppsQsbLayout extends AbstractQsbLayout implements SearchUiManager, WallpaperColorInfo.OnChangeListener {

    private AllAppsContainerView mAppsView;
    private AllAppsRecyclerView mRecyclerView;
    private FallbackAppsSearchView mFallback;
    private int mAlpha;
    private Bitmap mBitmap;
    private AlphabeticalAppsList mApps;
    private SpringAnimation mSpring;
    private float mStartY;

    public AllAppsQsbLayout(final Context context) {
        this(context, null);
    }

    public AllAppsQsbLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }

    public AllAppsQsbLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        mAlpha = 0;
        setOnClickListener(this);

        mStartY = getTranslationY();
        setTranslationY(Math.round(mStartY));
        mSpring = new SpringAnimation(this, new FloatPropertyCompat<AllAppsQsbLayout>("allAppsQsbLayoutSpringAnimation") {
            @Override
            public float getValue(AllAppsQsbLayout allAppsQsbLayout) {
                return allAppsQsbLayout.getTranslationY() + mStartY;
            }

            @Override
            public void setValue(AllAppsQsbLayout allAppsQsbLayout, float v) {
                allAppsQsbLayout.setTranslationY(Math.round(mStartY + v));
            }
        }, 0f);
    }

    private void searchFallback() {
        if (mFallback != null) {
            mFallback.showKeyboard();
            return;
        }
        setOnClickListener(null);
        mApps = mAppsView.getApps();
        mFallback = (FallbackAppsSearchView) mLauncher.getLayoutInflater().inflate(R.layout.all_apps_google_search_fallback, this, false);
        mFallback.bu(this, mApps, mAppsView.getActiveRecyclerView());
        addView(mFallback);
        mFallback.showKeyboard();
    }

    public void addOnScrollRangeChangeListener(final SearchUiManager.OnScrollRangeChangeListener onScrollRangeChangeListener) {
        mLauncher.getHotseat().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (mLauncher.getDeviceProfile().isVerticalBarLayout()) {
                    onScrollRangeChangeListener.onScrollRangeChanged(bottom);
                } else {
                    onScrollRangeChangeListener.onScrollRangeChanged(bottom
                            - HotseatQsbWidget.getBottomMargin(mLauncher)
                            - (((ViewGroup.MarginLayoutParams) getLayoutParams()).topMargin
                            + (int) getTranslationY() + getResources().getDimensionPixelSize(R.dimen.qsb_widget_height)));
                }
            }
        });
    }

    @Override
    public void resetSearch() {
    }

    @Override
    public void initialize(AllAppsContainerView appsView) {
        mAppsView = appsView;
        int i = 0;
        mAppsView.mAH[0].recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                useAlpha(((BaseRecyclerView) recyclerView).getCurrentScrollY());
            }
        });
        while (i < mAppsView.mAH.length) {
            mAppsView.mAH[i].applyVerticalFadingEdgeEnabled(true);
            i++;
        }
    }

    void useAlpha(int newAlpha) {
        int normalizedAlpha = Utilities.boundToRange(newAlpha, 0, 255);
        if (mAlpha != normalizedAlpha) {
            mAlpha = normalizedAlpha;
            invalidate();
        }
    }

    @Override
    protected int getWidth(int width) {
        if (mLauncher.getDeviceProfile().isVerticalBarLayout()) {
            return (width - mAppsView.getActiveRecyclerView().getPaddingLeft()) - mAppsView.getActiveRecyclerView().getPaddingRight();
        }
        View view = mLauncher.mHotseat.mContent;
        return (width - view.getPaddingLeft()) - view.getPaddingRight();
    }

    protected void loadBottomMargin() {
    }

    public void draw(final Canvas canvas) {
        if (mAlpha > 0) {
            if (mBitmap == null) {
                mBitmap = createBitmap(getResources().getDimension(R.dimen.hotseat_qsb_scroll_shadow_blur_radius), getResources().getDimension(R.dimen.hotseat_qsb_scroll_key_shadow_offset), 0);
            }
            mShadowPaint.setAlpha(mAlpha);
            loadDimensions(mBitmap, canvas);
            mShadowPaint.setAlpha(255);
        }
        super.draw(canvas);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
         int statusBarHeightId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        ((ViewGroup.MarginLayoutParams) getLayoutParams()).topMargin += getResources().getDimensionPixelSize(statusBarHeightId > 0 ?
                statusBarHeightId :
                R.dimen.status_bar_height);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        WallpaperColorInfo instance = WallpaperColorInfo.getInstance(getContext());
        instance.addOnChangeListener(this);
        onExtractedColorsChanged(instance);
    }

    public void onClick(final View view) {
        super.onClick(view);
        if (view == this) {
            final ConfigBuilder f = new ConfigBuilder(this, true);
            if (!mLauncher.getClient().startSearch(f.build(), f.getExtras())) {
                searchFallback();
            }
        }
    }

    protected void onDetachedFromWindow() {
        WallpaperColorInfo.getInstance(getContext()).removeOnChangeListener(this);
        super.onDetachedFromWindow();
    }

    public void onExtractedColorsChanged(final WallpaperColorInfo wallpaperColorInfo) {
        int color = Themes.getAttrBoolean(mLauncher, R.attr.isMainColorDark) ? 0xEBFFFFFE : 0xCCFFFFFE;
        bz(ColorUtils.compositeColors(ColorUtils.compositeColors(color, Themes.getAttrColor(mLauncher, R.attr.allAppsScrimColor)), wallpaperColorInfo.getMainColor()));
    }

    public void preDispatchKeyEvent(final KeyEvent keyEvent) {
    }

    public void refreshSearchResult() {
        if (mFallback != null) {
            mFallback.refreshSearchResult();
        }
    }

    public void reset() {
        useAlpha(0);
        if (mFallback != null) {
            mFallback.clearSearchResult();
            setOnClickListener(this);
            removeView(mFallback);
            mFallback = null;
        }
    }

    @NonNull
    public SpringAnimation getSpringForFling() {
        return mSpring;
    }
}
