package com.schwifty.bits_delivery;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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

