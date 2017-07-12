package com.example.reebotui.epglist;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by freem on 2017-03-06.
 */

public class EPGListViewItem {

    private ArrayList<EPGItem> list = new ArrayList<EPGItem>();
    private int index = 0;
    private Drawable chlogoiconDrawable;
    private Drawable bsstatusiconDrawable;

    private String bookingId = "";

    private boolean isComm = false;
    private boolean isBookmark = false;

    public EPGListViewItem() {

    }

    public EPGListViewItem(Drawable chlogoiconDrawable, Drawable bsstatusiconDrawable, EPGItem epgItem) {
        this.chlogoiconDrawable = chlogoiconDrawable;
        this.bsstatusiconDrawable = chlogoiconDrawable;
        this.list.add(epgItem);
    }


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

    public void clearChannelList() {
        EPGItem epgItem = list.get(0);
        list.clear();
        list.add(epgItem);
        index = 0;
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

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public boolean isBookmark() {
        return isBookmark;
    }

    public void setBookmark(boolean bookmark) {
        isBookmark = bookmark;
    }

    public boolean isComm() {
        return isComm;
    }

    public void setComm(boolean comm) {
        isComm = comm;
    }

}
