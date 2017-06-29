package com.exp.rb.reebot.view;

/**
 * Created by freem on 2017-06-21.
 */

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.exp.rb.reebot.R;
import com.exp.rb.reebot.view.*;
import com.exp.rb.reebot.view.EPGListViewItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.R.attr.indeterminateTintMode;
import static android.R.attr.resource;

public class RBBookingListViewAdapter extends BaseAdapter {

    private static final String TAG = "ReeBot(RBBookingListViewAdapter)";
    private String getbookinglist_url = "http://reebot.io:8083/api/getbookinglist";
    private String removebooking_url = "http://reebot.io:8083/api/removebooking";

    private ArrayList<RBBookingListViewItem> bookinglistitems = new ArrayList<RBBookingListViewItem>() ;
    String accesstoken ;
    Context context;

    private RemoveListener listener;
    private ListView bookinglistview;
    //private ListBtnClickListener listBtnClickListener ;


    public RBBookingListViewAdapter(Context context,ListView bookinglistview, String email, String accesstoken) {
        this.context = context ;
        this.accesstoken = accesstoken;
        this.bookinglistview = bookinglistview;
        //아이템 로드

        reqProgramList(email,accesstoken);
    }

    private void reqProgramList(final String email,final String acesstoken) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("예약 정보 가져오는 중");
        progressDialog.show();
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                progressDialog.dismiss();
            }
        }.sendEmptyMessageDelayed(0, 500);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                //String chnum = epgListViewItem.getSelectedItem().getChnumber();
                //String param = makeParameters(catvbrend, chnum);
                //System.out.println(TAG + ", param: "+q2url + param);
                String param =  "email=" + email;
                String response = queryBookingListJSON(getbookinglist_url, param,acesstoken);
                //System.out.println(TAG + ", response: " + response);
                readBookinglist(response);
                //epgListViewItem.setComm(false);
                progressDialog.dismiss();
            }
        });
        thread.start();
    }

    @SuppressLint("LongLogTag")
    private String queryBookingListJSON(String surl, String params,String accesstoken) {
        HttpURLConnection connection;
        OutputStreamWriter request = null;
        URL url = null;
        String response = null;

        Log.d(TAG, "queryBookingListJSON Param : " + params + surl);
        try {
            url = new URL(surl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if(accesstoken != null) connection.setRequestProperty("authorization", accesstoken);
            connection.setRequestMethod("POST");
            request = new OutputStreamWriter(connection.getOutputStream());
            request.write(params);
            request.flush();
            request.close();
            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            // Response from server after login process will be stored in response variable.
            response = sb.toString();
            //Log.d(TAG, "response : " + response);
            // You can perform UI operations here
            //Toast.makeText(context, "Message from Server: \n"+ response, Toast.LENGTH_SHORT).show();
            isr.close();
            reader.close();

        } catch (IOException e) {
            // Error
            Log.d(TAG, "queryEPGListJSON :" + e);
        }
        return response;
    }

    private boolean readBookinglist(String bookinglistjson) {
        boolean ret = false;
        try {
            JSONObject jObject = new JSONObject(bookinglistjson);
            if(jObject.getBoolean("type"))
            {
                JSONArray jArray =  jObject.getJSONArray("data");
                if (jArray.length() == 0) {

                } else {
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        String starttime = oneObject.getString("bookingtime");

                        //20170621121000
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
                        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM/dd hh:mm");
                        try {
                            Date date = dateFormat.parse(starttime);
                            starttime = dateFormat2.format(date);
                        } catch (ParseException e) {
                        }


                        String id = oneObject.getString("_id");
                        String title = oneObject.getString("ptitle");
                        String chnum = oneObject.getString("chnum");
                        String chname = oneObject.getString("chname");
                        String bookingprogram = "[" + starttime +"] "+ title +"\n(" +chname  +" / "+chnum  +")";
                        RBBookingListViewItem item =new RBBookingListViewItem();
                        item.setId(id);
                        item.setText(bookingprogram);
                        bookinglistitems.add(item);
                    }
                }
                //epgListViewItem.getList().add(new EPGItem(title, entryInfo, chnum, category, category, starttime, endtime, chname));
                handler.sendEmptyMessage(1);

            }
            else
            {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //Log.d(TAG, "handler");
            if (msg.what == 1) {
                notifyDataSetChanged();
                return;
            }
        }
    };

    @Override
    public int getCount() {
        return bookinglistitems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RBBookingListViewItem getItem(int position) {
        return bookinglistitems.get(position);
    }

    // 새롭게 만든 Layout을 위한 View를 생성하는 코드
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position ;
        final Context context = parent.getContext();
        // 생성자로부터 저장된 resourceId(listview_btn_item)에 해당하는 Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.bookinglist, parent, false);
        }
        // 화면에 표시될 View(Layout이 inflate된)로부터 위젯에 대한 참조 획득
        //final ImageView iconImageView = (ImageView) convertView.findViewById(R.id.bookinglist_chanelime);
        final TextView textTextView = (TextView) convertView.findViewById(R.id.bookinglist_progrma);
        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final RBBookingListViewItem listViewItem = (RBBookingListViewItem) getItem(position);
        // 아이템 내 각 위젯에 데이터 반영
        //iconImageView.setImageDrawable(listViewItem.getIcon());
        textTextView.setText(listViewItem.getText());
        // button1 클릭 시 TextView(textView1)의 내용 변경.
        ImageView btn_remove = (ImageView) convertView.findViewById(R.id.bookinglist_remove);
        btn_remove.setOnClickListener(new Button.OnClickListener() {
            @SuppressLint("LongLogTag")
            public void onClick(View v) {
                //textTextView.setText(Integer.toString(pos + 1) + "번 아이템 선택.");

                //살제 확인 다이얼로그
                Log.d(TAG,"btn_remove : " + Integer.toString(pos + 1) +"_"+ bookinglistitems.get(pos).getId());
                removeBokking(bookinglistview,bookinglistitems.get(pos).getId(),accesstoken,pos);
            }
        });
        return convertView;
    }

    public interface RemoveListener {
        void onRemoveData(int position) ;
    }

    public void setlistener(RemoveListener listener)
    {
        this.listener = listener;

    }

    private void removeBokking(final ListView bookinglistview,final String id,final String acesstoken,final int pos) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("예약 삭제중");
        progressDialog.show();
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                progressDialog.dismiss();
            }
        }.sendEmptyMessageDelayed(0, 500);
        final boolean result = false;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String param =  "id=" + id;
                String response = queryBookingListJSON(removebooking_url, param,acesstoken);
                //System.out.println(TAG + ", response: " + response);

                try {
                    JSONObject jobjec = new JSONObject(response);
                    if(jobjec.getBoolean("type"))
                    {
                        bookinglistitems.remove(pos);
                        listener.onRemoveData(pos);
                        handler.post(new Runnable() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void run() {
                                Log.d(TAG,"invalidate");
                                bookinglistview.invalidateViews();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //epgListViewItem.setComm(false);
                progressDialog.dismiss();
            }
        });
        thread.start();
    }
}

