package com.example.reebotui.interfaceclass;

import com.example.reebotui.epglist.EPGListViewItem;

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
public interface OnEPGListClickListener {
    // TODO: Update argument type and name
    void OnEPGListClickListener(EPGListViewItem epgListViewItem);
    void OnEPGListClickListener(EPGListViewItem epgListViewItem, int id);
}