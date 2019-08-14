package com.schwifty.bits_delivery;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

class TabsPagerAdapter extends FragmentPagerAdapter
{

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                MenuFragment menuRef = new MenuFragment();

                return menuRef;
            case 1:
                CartFragment cartRef = new CartFragment();
                return cartRef;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        super.getPageTitle(position);

        switch(position)
        {
            case 0 : return "Select Items";
            case 1 : return "Your Cart";
            default:return null;
        }

    }
}

