package com.exp.rb.reebot.view;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by freem on 2017-03-06.
 */

public class EPGListViewItem {

    private ArrayList<EPGItem> list = new ArrayList<EPGItem>();
    private int index = 0;
    private Drawable chlogoiconDrawable ;
    private Drawable bsstatusiconDrawable ;

    private boolean isComm = false;

    public ArrayList<EPGItem> getList() {
        return list;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public EPGItem getSelectedItem() {
        if (list != null && list.size() > index) {
            return list.get(index);
        } else {
            return null;
        }

    }

    public Drawable getChlogoiconDrawable() {
        return chlogoiconDrawable;
    }

    public void setChlogoiconDrawable(Drawable chlogoiconDrawable) {
        this.chlogoiconDrawable = chlogoiconDrawable;
    }

    public Drawable getBsstatusiconDrawable() {
        return bsstatusiconDrawable;
    }

    public void setBsstatusiconDrawable(Drawable bsstatusiconDrawable) {
        this.bsstatusiconDrawable = bsstatusiconDrawable;
    }

    public boolean isComm() {
        return isComm;
    }

    public void setComm(boolean comm) {
        isComm = comm;
    }
}
