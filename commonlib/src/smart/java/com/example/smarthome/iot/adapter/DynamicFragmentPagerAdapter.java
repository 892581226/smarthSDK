package com.example.smarthome.iot.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;


import java.util.List;

/**
 * @author KCrason
 * @date 2018/1/21
 */
public class DynamicFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments;
    private String[] titles;
    private FragmentManager fm;

    public DynamicFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments, String[] titles) {
        super(fm);
        this.mFragments = fragments;
        this.titles = titles;
        this.fm = fm;
    }


    public void update(List<Fragment> fragments, String[] titles) {
//        this.mFragments = fragments;
        this.titles = titles;
//        notifyDataSetChanged();
        setFragments(fragments);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null && position < titles.length) {
//            return titles[position];
            return titles[position].length()>5?(titles[position].substring(0,5)+"..."):titles[position];
        }
        return "主卧";
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.size();
    }

    public void setFragments(List fragments) {
        if(this.mFragments != null){
            FragmentTransaction ft = fm.beginTransaction();
            for(Fragment f:this.mFragments){
                ft.remove(f);
            }
            ft.commitAllowingStateLoss();
            ft=null;
            fm.executePendingTransactions();
        }
        this.mFragments = fragments;
        notifyDataSetChanged();
    }
}
