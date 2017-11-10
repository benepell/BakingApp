
/*
 *  ____        _    _                  _
 * | __ )  __ _| | _(_)_ __   __ _     / \   _ __  _ __
 * |  _ \ / _` | |/ / | '_ \ / _` |   / _ \ | '_ \| '_ \
 * | |_) | (_| |   <| | | | | (_| |  / ___ \| |_) | |_) |
 * |____/ \__,_|_|\_\_|_| |_|\__, | /_/   \_\ .__/| .__/
 *                           |___/          |_|   |_|
 *
 * Copyright (C) 2017 Benedetto Pellerito
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

package it.instantapps.bakingapp.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.instantapps.bakingapp.R;
import it.instantapps.bakingapp.activity.BaseActivity;
import it.instantapps.bakingapp.activity.DetailActivity;
import it.instantapps.bakingapp.adapter.FragmentTabAdapter;
import it.instantapps.bakingapp.utility.Costants;
import it.instantapps.bakingapp.utility.Utility;
import it.instantapps.bakingapp.widget.SlidingTabLayout;

@SuppressWarnings("ALL")
public class TabFragment extends Fragment {

    public static class PagerItem {
        private final CharSequence mTitle;
        private final List<Fragment> mFragmentList;
        private final int mIndicatorColor;
        private final int mDividerColor;

        PagerItem(CharSequence title) {
            mTitle = title;
            mIndicatorColor = Color.GREEN;
            mDividerColor = Color.GRAY;
            mFragmentList = new ArrayList<>();
        }

        public List<Fragment> createFragment(int index) {
            if (index >= 0) {
                mFragmentList.add(IngredientFragment.newInstance(index));
                mFragmentList.add(StepFragment.newInstance(index));
            }
            return mFragmentList;
        }


        public CharSequence getTitle() {
            return mTitle;
        }

        int getIndicatorColor() {
            return mIndicatorColor;
        }

        int getDividerColor() {
            return mDividerColor;
        }
    }

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabLayout;


    private final List<PagerItem> mTabs = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTabs.add(new PagerItem(
                getString(R.string.tab_ingredient_detail)
        ));
        mTabs.add(new PagerItem(
                getString(R.string.tab_step_detail)
        ));


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        int orderTab = -1;
        int index = -1;
        if (bundle != null) {
            index = bundle.getInt(Costants.BUNDLE_TAB_RECIPE_ID, -1);
            orderTab = bundle.getInt(Costants.BUNDLE_TAB_ORDERTAB, -1);
        }
        ViewPager mViewPager = getActivity().findViewById(R.id.viewpager);

        if (mViewPager != null) {
            mViewPager.setAdapter(new FragmentTabAdapter(getChildFragmentManager(), mTabs, index));

            mViewPager.setCurrentItem(orderTab);

            mSlidingTabLayout.setViewPager(mViewPager);

            mSlidingTabLayout.setCustomTabColor(new SlidingTabLayout.TabColor() {

                @Override
                public int getIndicatorColor(int position) {
                    return mTabs.get(position).getIndicatorColor();
                }

                @Override
                public int getDividerColor(int position) {
                    return mTabs.get(position).getDividerColor();
                }

            });
        } else {

            if ((Utility.isTablet(getContext()) && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Costants.EXTRA_RECIPE_ID, index);
                intent.putExtra(Costants.EXTRA_RECIPE_NAME, BaseActivity.getRecipeName());
                intent.putExtra(Costants.EXTRA_TAB_ORDERTAB, Costants.TAB_ORDER_STEP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }

        }

    }

}