package com.exp.rb.reebot;

import android.graphics.drawable.Drawable;

/**
 * Created by freem on 2017-03-06.
 */

public class
EPGListViewItem {

    private Drawable chlogoiconDrawable ;
    private Drawable bsstatusiconDrawable ;
    private String programtitleStr ;
    private String programtimeStr ;
    private String chnumber;
    private String programcategory;
    public  String category_kr;
    public  String category_en;
    public  String rating_value;
    public  String episode_num;;

    private String starttime;
    private String endtime;
    private  String chname;

    private boolean isPrevious = false;
    private boolean isNext = false;

    public boolean isPrevious() {
        return isPrevious;
    }
    public void setPrevious(boolean previous) {
        isPrevious = previous;
    }
    public boolean isNext() {
        return isNext;
    }
    public void setNext(boolean next) {
        isNext = next;
    }

    public void setChLogoIcon(Drawable chlogoicon) {
        chlogoiconDrawable = chlogoicon ;
    }
    public void setProgramTitle(String _programtitleStr) {
        programtitleStr = _programtitleStr ;
    }
    public void setProgramTime(String _programtimeStr) {
        programtimeStr = _programtimeStr ;
    }
    public void setBsLogoIcon(Drawable bslogoicon) {
        bsstatusiconDrawable = bslogoicon ;
    }
    public void setChnumber(String _chnumber) {
        chnumber = _chnumber ;
    }
    public void setProgramcategory( String _programcategory) {programcategory = _programcategory ;}
    public void setCategory_kr(String _category_kr) {category_kr = _category_kr ;}
    public void setCategory_en(String _category_en) {category_en = _category_en ;}
    public void setRating_value(String _rating_value) {
        rating_value = _rating_value ;
    }
    public void setEpisode_num( String _episode_num) {
        episode_num =_episode_num ;
    }
    public void setStarttime(String starttime) {this.starttime = starttime;}
    public void setEndtime(String endtime) {this.endtime = endtime;}

    public void setChname(String endtime) {this.chname = chname;}

    public String getStarttime() {return starttime;}
    public String getEndtime() {return endtime;}
    public Drawable getChLogoIcon() {
        return this.chlogoiconDrawable ;
    }
    public Drawable getBsLogoIcon() {
        return this.bsstatusiconDrawable ;
    }
    public String getProgramTitle() {return programtitleStr ;}
    public String getProgramTime() {
        return programtimeStr ;
    }
    public String getChnumber() {
        return chnumber ;
    }
    public String getProgramcategory() {
        return programcategory ;
    }
    public String getCategory_kr() {
        return category_kr ;
    }
    public String getCategory_en() {
        return category_en ;
    }
    public String getRating_value() {
        return rating_value ;
    }
    public String getEpisode_num() {
        return episode_num ;
    }
    public String getChname() {
        return chname ;
    }

}
