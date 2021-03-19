package com.example.smarthome.iot.impl.device.thermostat.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class TherMastatMainAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;
    private FragmentManager fm;

    public TherMastatMainAdapter(FragmentManager fm,List<Fragment> fragments) {
        super(fm);
        this.fm=fm;
        this.mFragments=fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}