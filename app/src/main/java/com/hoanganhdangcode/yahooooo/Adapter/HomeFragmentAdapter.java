package com.hoanganhdangcode.yahooooo.Adapter;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hoanganhdangcode.yahooooo.Fragment.ChatFragment;
import com.hoanganhdangcode.yahooooo.Fragment.FeedFragment;
import com.hoanganhdangcode.yahooooo.Fragment.FriendFragment;
import com.hoanganhdangcode.yahooooo.Fragment.MenuFragment;
import com.hoanganhdangcode.yahooooo.Fragment.NotificationFragment;

public class HomeFragmentAdapter extends FragmentStateAdapter {
    private final int NUM_PAGES = 5;

    public HomeFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new ChatFragment();
            case 1: return new FriendFragment();
            case 2: return new FeedFragment();
            case 3: return new NotificationFragment();
            case 4: return new MenuFragment();
            default: return new FeedFragment();
        }

    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }

}
