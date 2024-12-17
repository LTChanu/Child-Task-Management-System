package com.chanu.childtask;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class PageAdapter extends FragmentPagerAdapter {
    int numCount;
    boolean isParent;
    MySchedule mySchedule = new MySchedule();
    WhoGuideMe whoGuideMe = new WhoGuideMe();
    Notification notification = new Notification();
    AddChild addChild = new AddChild();
    ViewChild viewChild = new ViewChild();
    public PageAdapter(@NonNull FragmentManager fm, int numCount, boolean isParent) {
        super(fm);
        this.numCount = numCount;
        this.isParent = isParent;
    }

    public PageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(isParent)
            switch (position){
                case 0: return viewChild;
                case 1: return addChild;
                case 2: return notification;
            }
        else{
            switch (position){
                case 0: return mySchedule;
                case 1: return whoGuideMe;
                case 2: return notification;
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return numCount;
    }
}
