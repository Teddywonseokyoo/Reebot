package com.example.reebotui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.reebotui.api.ReeBotApi;
import com.example.reebotui.epglist.EPGItem;
import com.example.reebotui.epglist.EPGListViewAdapter;
import com.example.reebotui.epglist.EPGListViewItem;
import com.example.reebotui.interfaceclass.BookingCallback;
import com.example.reebotui.interfaceclass.OnEPGListClickListener;
import com.example.reebotui.util.AlertDialogUtil;
import com.exp.rb.reebot.R;

import java.util.ArrayList;

public class BookingActivity extends AppCompatActivity implements OnEPGListClickListener {
    private static final String TAG = "ReeBot(BookingActivity)";

    private String email;
    private String accesstoken;
    private EPGListViewAdapter epgListViewAdapter;
    ArrayList<EPGListViewItem> epgListViewList;

    private ReeBotApi reeBotApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("email");
            accesstoken = intent.getStringExtra("accesstoken");

            Log.d(TAG, "onCreate email: " + email);
            Log.d(TAG, "onCreate accesstoken: " + accesstoken);

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_programlist);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            epgListViewAdapter = new EPGListViewAdapter(this, this);
            epgListViewAdapter.setType(EPGListViewAdapter.LIST_TYPE_BOOKING);
            recyclerView.setAdapter(epgListViewAdapter);

            reeBotApi = new ReeBotApi(this);
            reeBotApi.reqBookingList(email, accesstoken, bookingCallback);

        } else {
            Toast.makeText(this, "param error", Toast.LENGTH_SHORT).show();
            finish();
        }

        findViewById(R.id.btn_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

//    @Override
//    public void onEvent(int event, String msg) {
//        Log.d(TAG, "onEvent event: " + event);
//        Log.d(TAG, "onEvent msg: " + msg);
//
//        switch (event) {
//            case ExtraInfo.REQ_MODE_BOOKING_LIST:
//                readBookinglist(msg);
//                break;
//
//        }
//    }

    BookingCallback bookingCallback = new BookingCallback() {
        @Override
        public void requestBookingList(ArrayList<EPGListViewItem> epgListViewItemList) {
            epgListViewList = epgListViewItemList;
            epgListViewAdapter.addItem(epgListViewList);
        }

        @Override
        public void addBookingChannel(EPGItem epgItem, boolean result) {

        }

        @Override
        public void removeBookingChannel(EPGListViewItem epgListViewItem, boolean result) {

        }
    };


//    private boolean readBookinglist(String msg) {
//        Log.d(TAG, "readBookinglist msg: " + msg);
//        epgListViewList = new ArrayList<EPGListViewItem>();
//
//        boolean ret = false;
//        if (msg == null) {
//            return ret;
//        }
//        try {
//            JSONObject jObject = new JSONObject(msg);
//            if (jObject.getBoolean("type")) {
//                JSONArray jArray = jObject.getJSONArray("data");
//                if (jArray.length() == 0) {
//
//                } else {
//                    for (int i = 0; i < jArray.length(); i++) {
//                        JSONObject oneObject = jArray.getJSONObject(i);
//                        String starttime = oneObject.getString("bookingtime");
//                        String endtime = oneObject.getString("programendtime");
//
////                        //20170621121000
////                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
////                        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM/dd hh:mm");
//
////                        try {
////                            Date date = dateFormat.parse(starttime);
////                            starttime = dateFormat2.format(date);
////                        } catch (ParseException e) {
////                        }
//
//
//                        String id = oneObject.getString("_id");
//                        String title = oneObject.getString("ptitle");
//                        String category = oneObject.getString("category");
//                        String chnum = oneObject.getString("chnum");
//                        String chname = oneObject.getString("chname");
////                        String chlogoname = oneObject.getString("imglogoname");
//                        String chlogoname = "";
//                        String entryInfo = category + " / " + chnum;
//
//                        String chlongo = chname.toString();
//                        chlongo = chlongo.replace(" ", "");
//
////                        String bookingprogram = "[" + starttime +"] "+ title +"\n(" +chname  +" / "+chnum  +")";
////                        RBBookingListViewItem item =new RBBookingListViewItem();
////                        item.setId(id);
////                        item.setText(bookingprogram);
////                        bookinglistitems.add(item);
//
//
//                        EPGItem item = new EPGItem(title, entryInfo, chnum, category, category, starttime, endtime, chname);
//                        EPGListViewItem epgListViewItem = new EPGListViewItem();
//                        epgListViewItem.setBookingId(id);
//                        epgListViewItem.setChlogoiconDrawable(AppUtil.getImage(this, chlongo, chlogoname));
//                        epgListViewItem.setBsstatusiconDrawable(ContextCompat.getDrawable(this, R.drawable.icon_fav_normal));
//                        epgListViewItem.getList().add(item);
//                        epgListViewList.add(epgListViewItem);
//
//                    }
//                }
////                handler.sendEmptyMessage(1);
//
//            } else {
//
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//        epgListViewAdapter.addItem(epgListViewList);
//
//        return ret;
//    }

    @Override
    public void OnEPGListClickListener(EPGListViewItem epgListViewItem) {

    }

    @Override
    public void OnEPGListClickListener(EPGListViewItem epgListViewItem, int id) {
        Log.d(TAG, "OnEPGListClickListener id: " + id);
        if (R.id.iv_delete == id) {
            showDeleteDialog(epgListViewItem);
        }
    }

    private void showDeleteDialog(final EPGListViewItem epgListViewItem) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        reeBotApi.reqBookingRemove(epgListViewItem, accesstoken, bookingCallback);
                        epgListViewList.remove(epgListViewItem);
                        epgListViewAdapter.notifyDataSetChanged();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        AlertDialogUtil.showDialog(BookingActivity.this, "", "프로그램 예약을 취소하시겠습니까?.", "취소", "확인", onClickListener);
    }
}
