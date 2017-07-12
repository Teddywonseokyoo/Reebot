package com.example.reebotui.info;

import android.content.Context;

import com.example.reebotui.util.PreferenceUtil;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by silver on 2017-06-09.
 */

public class BookmarkInfo {
    private final String PREF_NAME_BOOKMARD = "PREF_NAME_BOOKMARD";
    private final String PREF_KEY_BOOKMARD_CHANNEL = "PREF_KEY_USE_WIDGET";

    private Context context;

    public BookmarkInfo(Context context) {
        this.context = context;
    }

    public void setBookmard(ArrayList<String> bookmarkList) {
        PreferenceUtil preference = new PreferenceUtil(context, PREF_NAME_BOOKMARD);
        preference.put(PREF_KEY_BOOKMARD_CHANNEL, makeStringData(bookmarkList));
    }

    public ArrayList<String> getBookmarkList() {
        PreferenceUtil preference = new PreferenceUtil(context, PREF_NAME_BOOKMARD);
        String data = preference.getValue(PREF_KEY_BOOKMARD_CHANNEL, "");
        return makeBookmardList(data);
    }

    private String makeStringData(ArrayList<String> bookmarkList) {
        String result = "";
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < bookmarkList.size(); i++) {
                jsonArray.put(bookmarkList.get(i));
            }

            result = jsonArray.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private ArrayList<String> makeBookmardList(String data) {
        ArrayList<String> list = new ArrayList<String>();

        if (data != null && !data.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.getString(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return list;
    }

}
