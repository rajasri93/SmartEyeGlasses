package com.example.smarteyeglasses.Activities.Login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by aj on 10/14/16.
 */
public class LoginPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;
    private final int LOGIN_TAB = 0;
    private final int SIGNUP_TAB = 1;

    public LoginPagerAdapter(FragmentManager fm, int numOfTabs){
        super(fm);
        this.mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case LOGIN_TAB:
                return new LoginFragment();
            case SIGNUP_TAB:
                return new SignUpFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
