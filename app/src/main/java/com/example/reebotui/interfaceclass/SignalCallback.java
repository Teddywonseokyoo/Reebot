package com.example.reebotui.interfaceclass;

/**
 * Created by silver on 2017-06-23.
 */


import com.example.reebotui.info.RBAuthData;

import java.util.ArrayList;

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
public interface SignalCallback {
    // TODO: Update argument type and name
    void getSignalList(boolean result, String msg, ArrayList<String> list);
    void getIRSignal(boolean result, String msg);
    void changeService(boolean result, String msg);
}