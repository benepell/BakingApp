package it.instantapps.bakingapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import it.instantapps.bakingapp.fragment.TabFragment;

public class FragmentTabAdapter extends FragmentPagerAdapter {

    private final List<TabFragment.PagerItem> mTabs;
    private final int mIndex;

    public FragmentTabAdapter(FragmentManager fm, List<TabFragment.PagerItem> tabs, int index) {
        super(fm);
        mTabs = tabs;
        mIndex = index;
    }

    @Override
    public Fragment getItem(int i) {
        return mTabs.get(i).createFragment(mIndex).get(i);
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).getTitle();
    }

}
