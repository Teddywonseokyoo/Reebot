package com.example.reebotui.interfaceclass;

import com.example.reebotui.epglist.EPGItem;
import com.example.reebotui.epglist.EPGListViewItem;

import java.util.ArrayList;

/**
 * Created by silver on 2017-06-23.
 */


/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p/>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
public interface BookmarkCallback {
    // TODO: Update argument type and name

    void requestBookmarkList(ArrayList<EPGListViewItem> epgListViewItemList);

    void addBookmark(EPGItem epgItem, boolean result);

    void removeBookmark(EPGListViewItem epgListViewItem, boolean result);
}