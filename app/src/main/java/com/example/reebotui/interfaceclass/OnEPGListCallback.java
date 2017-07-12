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
public interface OnEPGListCallback {
    // TODO: Update argument type and name

    void requestAllEPGList(ArrayList<EPGListViewItem> epgListViewItemList);

    void requestBookmarkList(ArrayList<EPGListViewItem> epgListViewItemList);

    void requestSearchEPGList(ArrayList<EPGListViewItem> epgListViewItemList);

    void requestEPGProgramList(ArrayList<EPGItem> epgItemList);
}