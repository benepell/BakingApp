

package it.instantapps.bakingapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import it.instantapps.bakingapp.widget.SlidingTabLayout;

import static it.instantapps.bakingapp.utility.Costants.TAB_DEFAULT_BOTTOM_BORDER_COLOR_ALPHA;
import static it.instantapps.bakingapp.utility.Costants.TAB_DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS;
import static it.instantapps.bakingapp.utility.Costants.TAB_DEFAULT_DIVIDER_COLOR_ALPHA;
import static it.instantapps.bakingapp.utility.Costants.TAB_DEFAULT_DIVIDER_HEIGHT;
import static it.instantapps.bakingapp.utility.Costants.TAB_DEFAULT_DIVIDER_THICKNESS_DIPS;
import static it.instantapps.bakingapp.utility.Costants.TAB_DEFAULT_SELECTED_INDICATOR_COLOR;
import static it.instantapps.bakingapp.utility.Costants.TAB_SELECTED_INDICATOR_THICKNESS_DIPS;

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


public class SlidingTab extends LinearLayout {

    private final int mBottomBorderThickness;
    private final Paint mBottomBorderPaint;

    private final int mSelectedIndicatorThickness;
    private final Paint mSelectedIndicatorPaint;

    private final Paint mDividerPaint;
    private final float mDividerHeight;

    private int mSelectedPosition;
    private float mSelectionOffset;

    private SlidingTabLayout.TabColor mCustomTabColor;
    private final SimpleTabColor mDefaultTabColor;


    public SlidingTab(Context context) {
        super(context, null);
        setWillNotDraw(false);

        final float density = getResources().getDisplayMetrics().density;

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorForeground, outValue, true);
        final int themeForegroundColor = outValue.data;

        int defaultBottomBorderColor = setColorAlpha(themeForegroundColor,
                TAB_DEFAULT_BOTTOM_BORDER_COLOR_ALPHA);

        mDefaultTabColor = new SimpleTabColor();
        mDefaultTabColor.setIndicatorColors(TAB_DEFAULT_SELECTED_INDICATOR_COLOR);
        mDefaultTabColor.setDividerColors(setColorAlpha(themeForegroundColor,
                TAB_DEFAULT_DIVIDER_COLOR_ALPHA));

        mBottomBorderThickness = (int) (TAB_DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS * density);
        mBottomBorderPaint = new Paint();
        mBottomBorderPaint.setColor(defaultBottomBorderColor);

        mSelectedIndicatorThickness = (int) (TAB_SELECTED_INDICATOR_THICKNESS_DIPS * density);
        mSelectedIndicatorPaint = new Paint();

        mDividerHeight = TAB_DEFAULT_DIVIDER_HEIGHT;
        mDividerPaint = new Paint();
        mDividerPaint.setStrokeWidth((int) (TAB_DEFAULT_DIVIDER_THICKNESS_DIPS * density));
    }

    public void setCustomTabColor(SlidingTabLayout.TabColor customTabColor) {
        mCustomTabColor = customTabColor;
        invalidate();
    }

    public void onViewPagerPageChanged(int position, float positionOffset) {
        mSelectedPosition = position;
        mSelectionOffset = positionOffset;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int height = getHeight();
        final int childCount = getChildCount();
        final int dividerHeightPx = (int) (Math.min(Math.max(0f, mDividerHeight), 1f) * height);
        final SlidingTabLayout.TabColor TabColor = mCustomTabColor != null
                ? mCustomTabColor
                : mDefaultTabColor;

        if (childCount > 0) {
            View selectedTitle = getChildAt(mSelectedPosition);
            int left = selectedTitle.getLeft();
            int right = selectedTitle.getRight();
            int color = TabColor.getIndicatorColor(mSelectedPosition);

            if (mSelectionOffset > 0f && mSelectedPosition < (getChildCount() - 1)) {
                int nextColor = TabColor.getIndicatorColor(mSelectedPosition + 1);
                if (color != nextColor) {
                    color = blendColors(nextColor, color, mSelectionOffset);
                }

                View nextTitle = getChildAt(mSelectedPosition + 1);
                left = (int) (mSelectionOffset * nextTitle.getLeft() +
                        (1.0f - mSelectionOffset) * left);
                right = (int) (mSelectionOffset * nextTitle.getRight() +
                        (1.0f - mSelectionOffset) * right);
            }

            mSelectedIndicatorPaint.setColor(color);

            canvas.drawRect(left, height - mSelectedIndicatorThickness, right,
                    height, mSelectedIndicatorPaint);
        }

        canvas.drawRect(0, height - mBottomBorderThickness, getWidth(), height, mBottomBorderPaint);

        int separatorTop = (height - dividerHeightPx) / 2;
        for (int i = 0; i < childCount - 1; i++) {
            View child = getChildAt(i);
            mDividerPaint.setColor(TabColor.getDividerColor(i));
            canvas.drawLine(child.getRight(), separatorTop, child.getRight(),
                    separatorTop + dividerHeightPx, mDividerPaint);
        }
    }


    private static int setColorAlpha(int color, byte alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }


    private static int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
        return Color.rgb((int) r, (int) g, (int) b);
    }

    private static class SimpleTabColor implements SlidingTabLayout.TabColor {
        private int[] mIndicatorColors;
        private int[] mDividerColors;

        @Override
        public final int getIndicatorColor(int position) {
            return mIndicatorColors[position % mIndicatorColors.length];
        }

        @Override
        public final int getDividerColor(int position) {
            return mDividerColors[position % mDividerColors.length];
        }

        void setIndicatorColors(@SuppressWarnings("SameParameterValue") int... colors) {
            mIndicatorColors = colors;
        }

        void setDividerColors(int... colors) {
            mDividerColors = colors;
        }
    }
}