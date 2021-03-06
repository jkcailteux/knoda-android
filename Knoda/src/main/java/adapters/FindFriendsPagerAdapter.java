package adapters;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import models.Group;
import views.contacts.FindFriendsActivity;
import views.contacts.FindFriendsContactsFragment;
import views.contacts.FindFriendsFacebookTwitterFragment;

public class FindFriendsPagerAdapter extends FragmentStatePagerAdapter {
    public Group group;
    FindFriendsActivity parent;

    public FindFriendsPagerAdapter(FragmentManager fm, FindFriendsActivity parent) {
        super(fm);
        this.parent = parent;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = FindFriendsContactsFragment.newInstance(parent);
        } else if (position == 1) {
            fragment = FindFriendsFacebookTwitterFragment.newInstance(parent, "facebook");
        } else if (position == 2) {
            fragment = FindFriendsFacebookTwitterFragment.newInstance(parent, "twitter");
        } else
            fragment = new Fragment();
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Contacts";
            case 1:
                return "Facebook";
            case 2:
                return "Twitter";
        }
        return "";
    }
}
