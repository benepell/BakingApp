/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.instantapps.bakingapp.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import it.instantapps.bakingapp.view.SlidingTab;
import it.instantapps.bakingapp.utility.Costants;

@SuppressWarnings("ALL")
public class SlidingTabLayout extends HorizontalScrollView {

    public interface TabColor {

        int getIndicatorColor(int position);

        int getDividerColor(int position);

    }

    private final int mTitleOffset;

    private int mTabViewLayoutId;

    private int mTabViewTextViewId;

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;

    private final SlidingTab mSlidingTab;

    public SlidingTabLayout(Context context) {
        this(context, null);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


        setHorizontalScrollBarEnabled(false);

        setFillViewport(true);

        mTitleOffset = (int) (Costants.TAB_TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);

        mSlidingTab = new SlidingTab(context);
        addView(mSlidingTab, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }


    public void setCustomTabColor(TabColor tabColor) {
        mSlidingTab.setCustomTabColor(tabColor);
    }


    public void setViewPager(ViewPager viewPager) {
        mSlidingTab.removeAllViews();

        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }


    private TextView createDefaultTabView(Context context) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Costants.TAB_VIEW_TEXT_SIZE_SP);
        textView.setTypeface(Typeface.DEFAULT_BOLD);


        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                outValue, true);
        textView.setBackgroundResource(outValue.resourceId);

        textView.setAllCaps(true);

        int padding = (int) (Costants.TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);

        return textView;
    }

    private void populateTabStrip() {
        final PagerAdapter adapter = mViewPager.getAdapter();
        final View.OnClickListener tabClickListener = new TabClickListener();

        for (int i = 0; i < adapter.getCount(); i++) {
            View tabView = null;
            TextView tabTitleView = null;

            if (mTabViewLayoutId != 0) {

                tabView = LayoutInflater.from(getContext()).inflate(mTabViewLayoutId, mSlidingTab,
                        false);
                tabTitleView = tabView.findViewById(mTabViewTextViewId);
            }

            if (tabView == null) {
                tabView = createDefaultTabView(getContext());
            }

            if (tabTitleView == null && TextView.class.isInstance(tabView)) {
                tabTitleView = (TextView) tabView;
            }

            if (tabTitleView != null) {
                tabTitleView.setText(adapter.getPageTitle(i));

            }
            tabView.setOnClickListener(tabClickListener);

            mSlidingTab.addView(tabView);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = mSlidingTab.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = mSlidingTab.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            if (tabIndex > 0 || positionOffset > 0) {

                targetScrollX -= mTitleOffset;
            }

            scrollTo(targetScrollX, 0);
        }
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = mSlidingTab.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }

            mSlidingTab.onViewPagerPageChanged(position, positionOffset);

            View selectedTitle = mSlidingTab.getChildAt(position);

            int extraOffset = (selectedTitle != null)
                    ? (int) (positionOffset * selectedTitle.getWidth())
                    : 0;
            scrollToTab(position, extraOffset);
            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset,
                        positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mSlidingTab.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }
        }

    }

    private class TabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < mSlidingTab.getChildCount(); i++) {
                if (v == mSlidingTab.getChildAt(i)) {
                    mViewPager.setCurrentItem(i);
                    return;
                }
            }
        }
    }

}
