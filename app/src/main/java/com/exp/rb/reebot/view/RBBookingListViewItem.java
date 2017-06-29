package com.exp.rb.reebot.view;

import android.graphics.drawable.Drawable;

/**
 * Created by freem on 2017-06-21.
 */

public class RBBookingListViewItem {
    private Drawable iconDrawable ;
    private String textStr ;
    private String id;

    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setId(String id) {
        this.id = id ;
    }
    public void setText(String text) {
        textStr = text ;
    }

    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getText() {
        return this.textStr ;
    }
    public String getId() {
        return this.id ;
    }
}
