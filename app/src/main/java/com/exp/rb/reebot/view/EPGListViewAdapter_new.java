package com.exp.rb.reebot.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.exp.rb.reebot.R;
import com.exp.rb.reebot.IR.VIRCommandMaker;
import com.exp.rb.reebot.util.BookmarkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by freem on 2017-03-06.
 */

public class EPGListViewAdapter_new extends BaseAdapter {

    private static final String TAG = "ReeBot(EPGListAdptr)";

    private String surl = "";
    private String q2url = "";
    private String q3url = "";
    private String catvbrend = "";
    private String sid = "";
    private String email = "";
    private String pushtoken = "";
    private JSONObject stboxsignal;
    private JSONObject tvsignal;


    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<EPGListViewItem> listViewItemList = new ArrayList<EPGListViewItem>();
    private SimpleDateFormat simpleDateFormat1;
    private SimpleDateFormat simpleDateFormat2;
    private LayoutInflater inflater;
    private Context context;
    private int itemWidth = 0;
    private BookmarkInfo bookmarkInfo;
    private ArrayList<String> bookmardList;
    private String acesstoken;

    public EPGListViewAdapter_new()
    {

    }
    public EPGListViewAdapter_new(Context context, String surl, String q2url, String q3url, String catvbrend, String sid, String email, String pushtoken,String acesstoken) {
        this.context = context;
        this.surl = surl;
        this.q2url = q2url;
        this.q3url = q3url;
        this.catvbrend = catvbrend;
        this.sid = sid;
        this.email = email;
        this.pushtoken = pushtoken;
        this.stboxsignal = stboxsignal;
        this.tvsignal = tvsignal;
        this.acesstoken = acesstoken;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
        simpleDateFormat2 = new SimpleDateFormat("HH:mm");
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        itemWidth = dm.widthPixels / 3 * 2;
//        epgjson = new EPGManagerForJSON(context);

        bookmarkInfo = new BookmarkInfo(context);
        bookmardList = bookmarkInfo.getBookmarkList();
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.epglistview_item_new, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.convertView = convertView;
            viewHolder.iv_channel_logo = (ImageView) convertView.findViewById(R.id.iv_channel_logo);
            viewHolder.iv_bstate = (ImageView) convertView.findViewById(R.id.iv_bstate);
            viewHolder.hs_scroll = (HorizontalScrollView) convertView.findViewById(R.id.hs_scroll);
            viewHolder.ll_scroll = (LinearLayout) convertView.findViewById(R.id.ll_scroll);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final EPGListViewItem epgListViewItem = listViewItemList.get(position);
        viewHolder.iv_channel_logo = (ImageView) convertView.findViewById(R.id.iv_channel_logo);
        viewHolder.iv_bstate = (ImageView) convertView.findViewById(R.id.iv_bstate);
        viewHolder.iv_channel_logo.setImageDrawable(epgListViewItem.getChlogoiconDrawable());
        viewHolder.iv_bstate.setImageDrawable(epgListViewItem.getBsstatusiconDrawable());
        System.out.println(TAG + ", getChildCount: " + viewHolder.ll_scroll.getChildCount());
        if (viewHolder.ll_scroll.getChildCount() == 0) {

        }


        viewHolder.ll_scroll.removeAllViews();
//        System.out.println(TAG + ", itemWidth: " + itemWidth);
        for (int i = 0; i < epgListViewItem.getList().size(); i++) {
//            System.out.println(TAG + ", " + epgListViewItem.getList().get(i).getProgramTitle() + " : " + i);
//            System.out.println(TAG + ", " + epgListViewItem.getList().get(i).getChnumber() + " : " + i);

            View itemView = inflater.inflate(R.layout.epglistview_item_program, parent, false);
            itemView.getLayoutParams().width = itemWidth;
            itemView.setTag(epgListViewItem.getSelectedItem().getChnumber() + i);
            TextView programTitleTextView = (TextView) itemView.findViewById(R.id.programTitleTextView);
            TextView programTimeTextView = (TextView) itemView.findViewById(R.id.programTimeTextView);
            TextView tv_start_time = (TextView) itemView.findViewById(R.id.tv_start_time);
            TextView tv_end_time = (TextView) itemView.findViewById(R.id.tv_end_time);
            ProgressBar pb_time = (ProgressBar) itemView.findViewById(R.id.pb_time);
            programTitleTextView.setText(epgListViewItem.getList().get(i).getProgramTitle());
            programTimeTextView.setText(epgListViewItem.getList().get(i).getProgramTime());


            try {
                Calendar calendar = Calendar.getInstance(Locale.KOREA);
                Date startDate = simpleDateFormat1.parse(epgListViewItem.getList().get(i).getStarttime());
                Date endDate = simpleDateFormat1.parse(epgListViewItem.getList().get(i).getEndtime());
                Date cDate = new Date(calendar.getTimeInMillis());
                long tTime = endDate.getTime() - startDate.getTime();
                long cTime = cDate.getTime() - startDate.getTime();
                tTime = tTime / 1000;
                cTime = cTime / 1000;
                pb_time.setMax((int) (tTime));
                pb_time.setProgress((int) (cTime));
                tv_start_time.setText(simpleDateFormat2.format(startDate));
                tv_end_time.setText(simpleDateFormat2.format(endDate));
            } catch (Exception e) {
                e.printStackTrace();
            }

            viewHolder.ll_scroll.addView(itemView);
        }

        final GestureDetectorCompat gestureDetector = new GestureDetectorCompat(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                System.out.println(TAG + ", onDown");
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                System.out.println(TAG + ", onShowPress");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                System.out.println(TAG + ", onSingleTapUp");
                if (epgListViewItem.getIndex() == 0) {


                    Log.d(TAG, "Click info: " + epgListViewItem.getSelectedItem().getProgramTitle() + "(" + epgListViewItem.getSelectedItem().getChnumber() + ")");
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date startDate =null ;

                    try {
                        startDate = simpleDateFormat1.parse(epgListViewItem.getSelectedItem().getStarttime());
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    Calendar calendar = Calendar.getInstance(Locale.KOREA);
                    Date cDate = new Date(calendar.getTimeInMillis());
                    // Log.d(TAG," ctime :"+  cDate.getTime() + "starttime :" + startDate.getTime()  );
                    if( startDate.getTime() > cDate.getTime()) {
                        bookingProgram(epgListViewItem);
                        Toast.makeText(context, "예약\n[" + epgListViewItem.getSelectedItem().getChnumber() + "]" + epgListViewItem.getSelectedItem().getProgramTitle(), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        new VIRCommandMaker(context,surl,email,"STBOX",epgListViewItem.getSelectedItem().getChnumber(), sid);
                        //new VIRCommandMaker(context, surl, catvbrend, "CHNUMBER", epgListViewItem.getSelectedItem().getChnumber(), sid);
                    }

                } else {

                    bookingProgram(epgListViewItem);
                    //예약
                    Toast.makeText(context, "예약\n[" + epgListViewItem.getSelectedItem().getChnumber() + "]" + epgListViewItem.getSelectedItem().getProgramTitle(), Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                System.out.println(TAG + ", onScroll");
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                System.out.println(TAG + ", onLongPress");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                System.out.println(TAG + ", onFling");
                if (epgListViewItem.getList().size() < 2) {
                    reqProgramList(epgListViewItem);
                }
                moveScroll(viewHolder.hs_scroll, epgListViewItem);
                return false;
            }
        });

        viewHolder.hs_scroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        if (checkBookmark(epgListViewItem.getList().get(0).getChnumber())) {
            viewHolder.iv_bstate.setImageResource(R.drawable.bookmark);
        } else {
            viewHolder.iv_bstate.setImageResource(R.drawable.unbookmark);
        }

        viewHolder.iv_bstate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBookmark(epgListViewItem.getList().get(0).getChnumber())) {
                    bookmardList.remove(epgListViewItem.getList().get(0).getChnumber());
                } else {
                    bookmardList.add(epgListViewItem.getList().get(0).getChnumber());
                }
                bookmarkInfo.setBookmard(bookmardList);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    //@Override
    //public Object getItem(int position) {
    //    return listViewItemList.get(position) ;
    //}

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public EPGListViewItem getItem(int position) {
        return listViewItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    //(getImage(context,thisentry.chlogo),ContextCompat.getDrawable(context, R.drawable.livetv), thisentry.program_title, entryInfo ,thisentry.chnumber,thisentry.category_kr,thisentry.category_en) ;
    public void addItem(Drawable chicon, Drawable bsicon, String programtitle, String programtime, String chnumber, String category_kr, String category_en, String starttime, String endtime, String chname) {
        EPGItem item = new EPGItem(programtitle, programtime, chnumber, category_kr, category_en, starttime, endtime, chname);
        EPGListViewItem epgListViewItem = new EPGListViewItem();
        epgListViewItem.setChlogoiconDrawable(chicon);
        epgListViewItem.setBsstatusiconDrawable(bsicon);
        epgListViewItem.getList().add(item);
        listViewItemList.add(epgListViewItem);
    }

    public void clearList() {
        listViewItemList.clear();
        notifyDataSetChanged();
    }

    private boolean checkBookmark(String channel) {
        boolean check = false;

        for (int i = 0; i < bookmardList.size(); i++) {
            if (bookmardList.get(i).equals(channel)) {
                check = true;
                break;
            }
        }
        return check;
    }

    public void startRefreshTime() {
        if (!timer) {
            timer = true;
            handler.sendEmptyMessageDelayed(0, 60000);
        }
    }

    public void stopRefreshTime() {
        timer = false;
        handler.removeCallbacksAndMessages(null);
    }

    private void checkTime() {
        System.out.println(TAG + ", checkTime");
        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        Date cDate = new Date(calendar.getTimeInMillis());

        if (listViewItemList != null) {
            int cnt = 0;
            System.out.println(TAG + ", listViewItemList size: " + listViewItemList.size());
            while (cnt < listViewItemList.size()) {

                if (listViewItemList.get(cnt).getList() == null || listViewItemList.get(cnt).getList().size() < 2) {
                    cnt++;
                    continue;
                }

                try {
                    Date endDate = simpleDateFormat1.parse(listViewItemList.get(cnt).getList().get(0).getEndtime());
                    if (cDate.compareTo(endDate) > 0) {
                        listViewItemList.get(cnt).getList().remove(0);
                    } else {
                        cnt++;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    cnt++;
                }

                System.out.println(TAG + ", listViewItemList size: " + listViewItemList.size());
                System.out.println(TAG + ", cnt: " + cnt);
            }
        }
    }

    private boolean timer = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //Log.d(TAG, "handler");

            if (msg.what == 1) {
                notifyDataSetChanged();
                return;
            } else if (msg.what == 0) {
                if (timer) {
                    checkTime();
                    notifyDataSetChanged();
                    handler.sendEmptyMessageDelayed(0, 60000);
                }
            }
            //새로고침 및 다음날 리스트 가져오기 필요
        }
    };

    private void moveScroll(final HorizontalScrollView horizontalScrollView, final EPGListViewItem epgListViewItem) {
        final int scrollX = horizontalScrollView.getScrollX();
        System.out.println(TAG + ", scrollX: " + scrollX);
        horizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                if (scrollX % itemWidth < itemWidth / 2) {
                    horizontalScrollView.smoothScrollTo(itemWidth * (scrollX / itemWidth), horizontalScrollView.getScrollY());
                    System.out.println(TAG + ", position: " + scrollX / itemWidth);
                    epgListViewItem.setIndex(scrollX / itemWidth);
                } else {
                    horizontalScrollView.smoothScrollTo(itemWidth * (scrollX / itemWidth + 1), horizontalScrollView.getScrollY());
                    System.out.println(TAG + ", position: " + (scrollX / itemWidth + 1));
                    epgListViewItem.setIndex(scrollX / itemWidth + 1);
                }
            }
        });
    }

    private void reqProgramList(final EPGListViewItem epgListViewItem) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("채널정보 가져오는 중");
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
                if (epgListViewItem.isComm()) {
                    return;
                }

                String chnum = epgListViewItem.getSelectedItem().getChnumber();
                //System.out.println(TAG + ", chnum: " + chnum);
//                epgjson.execute(new EPGParamData(null, null, EPGListViewAdapter_new.this, q2url, catvbrend, chnum));
                String param = makeParameters(catvbrend, chnum);
                System.out.println(TAG + ", param: "+q2url + param);
                String response = queryEPGListJSON(q2url, param,"");
                System.out.println(TAG + ", response: " + response);
                readProgramlInfo(response, epgListViewItem);
                epgListViewItem.setComm(false);
                progressDialog.dismiss();
            }
        });

        thread.start();
    }

    //teddy/////
    private void bookingProgram(final EPGListViewItem epgListViewItem) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("프로그램 예약 중");
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
                if (epgListViewItem.isComm()) {
                    return;
                }
                String chnum = epgListViewItem.getSelectedItem().getChnumber();
                String category = epgListViewItem.getSelectedItem().getCategory_kr();
                String ptitle = epgListViewItem.getSelectedItem().getProgramTitle();
                String bookingtime = epgListViewItem.getSelectedItem().getStarttime();
                String programendtime = epgListViewItem.getSelectedItem().getEndtime();
                String chname = epgListViewItem.getSelectedItem().getChname();
                String catv = catvbrend;
                //System.out.println(TAG + ", chnum: " + chnum);
                String param = makeParameters(email,catv, chnum, pushtoken, ptitle, chname, category, bookingtime, programendtime);
                //System.out.println(TAG + ", param: " + param);
                String response = queryEPGListJSON(q3url, param, acesstoken);
                //System.out.println(TAG + "send : " + q3url + param);
                progressDialog.dismiss();
            }
        });
        thread.start();
    }

    public void bookingProgram(final String chnum,final String catv,final String category,final String ptitle,final String bookingtime,final String programendtime,final String chname,final String acesstoken) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("프로그램 예약 중");
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

                //System.out.println(TAG + ", chnum: " + chnum);
                String param = makeParameters(email,catv,chnum, pushtoken, ptitle, chname, category, bookingtime, programendtime);
                //System.out.println(TAG + ", param: " + param);
                String response = queryEPGListJSON(q3url, param,acesstoken);
                //System.out.println(TAG + "send : " + q3url + param);
                progressDialog.dismiss();
            }
        });
        thread.start();
    }

    /*
    email: String,
    pushtoken: String,
    ptitle: String,
    chname: String,
    chnum: String,
    category: String,
    bookingtime: String,
    programendtime: String
     */
    private String makeParameters(String email,String catv, String chnum, String pushtoken, String ptitle, String chname, String category, String bookingtime, String programendtime) {
        String ret = null;
        //Date date = new Date();
        //DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        //dateFormatter.format(date).toString();
        ret = "email=" + email + "&catv=" + catv +"&chnum=" + chnum + "&pushtoken=" + pushtoken + "&ptitle=" + ptitle + "&chname=" + chname + "&category=" + category + "&bookingtime=" + bookingtime + "&programendtime=" + programendtime;
        Log.d(TAG, "queryEPGListJSON :" + ret);
        return ret;
    }
/////////

    private String makeParameters(String catvb, String chnum) {
        String ret = null;
        Date date = new Date();
        DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        //dateFormatter.format(date).toString();
        //String[] catv = catvb.split("_");
        //수정 필요 (통합)
//        ret = "catvb=epgfor" + catv[1] + "&reqtime=20170524200000" + "&chnum=" + chnum;
        ret = "catvb=" + catvb + "&reqtime=" + dateFormatter.format(date).toString() + "&chnum=" + chnum;
        Log.d(TAG, "queryEPGListJSON :" + ret);
        return ret;
    }

    private String queryEPGListJSON(String surl, String params,String acesstoken) {
        HttpURLConnection connection;
        OutputStreamWriter request = null;
        URL url = null;
        String response = null;

        Log.d(TAG, "queryEPGListJSON Param : " + params + surl);
        try {
            url = new URL(surl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if(acesstoken != null) connection.setRequestProperty("authorization", acesstoken);
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

            Log.d(TAG, "response : " + response);
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

    private boolean readProgramlInfo(String epgjson, EPGListViewItem epgListViewItem) {
        boolean ret = false;
        Log.d(TAG, "Response JSON :" + epgjson);
        try {
            //JSONObject jObject = new JSONObject(epgjson);
            JSONArray jArray = new JSONArray(epgjson);
            if (jArray.length() == 0) {

            } else {
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    JSONObject channel = oneObject.getJSONObject("channel");
                    JSONObject program = channel.getJSONObject("program");

                    JSONObject isplist = channel.getJSONObject("isplist");
                    JSONObject isp = isplist.getJSONObject(catvbrend);
                    String chnum = Integer.toString(isp.getInt("chnum"));
                   // String chnum = Integer.toString(channel.getInt("chnum"));


                    String chname = channel.getString("name");
                    String chlogoname = channel.getString("imglogoname");
                    String maintitle = program.getString("maintitle");
                    String episode = program.getString("episode");
                    String subtitle = program.getString("subtitle");
                    String category = program.getString("category");
                    String starttime = program.getString("starttime");
                    String endtime = program.getString("endtime");
                    String title = maintitle;
                    if (!(subtitle.isEmpty())) {
                        title = maintitle + " [" + subtitle + "]";
                    }

                    if (episode != null && episode.trim().length() > 0) {
                        title += " (" + episode + "화)";
                    }


                    if (chlogoname.isEmpty()) {
                        chlogoname = "null";
                    }

                    String entryInfo = category + " / " + chnum;

                    String chlongo = chname.toString();
                    chlongo = chlongo.replace(" ", "");
                    epgListViewItem.getList().add(new EPGItem(title, entryInfo, chnum, category, category, starttime, endtime, chname));
                    handler.sendEmptyMessage(1);
//                    adapter.addItem(EPGManager.getImage(context,chlongo,chlogoname), ContextCompat.getDrawable(context, R.drawable.reebot_img_livetv), title, entryInfo ,chnum,category,category, starttime, endtime) ;
                    //Log.d(TAG, "maintitle : ("+ chnum +")" +  maintitle+" / IMG : "+ chname + "/ IMG2 : "+ chlogoname);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    class ViewHolder {
        View convertView;
        ImageView iv_channel_logo;
        ImageView iv_bstate;
        HorizontalScrollView hs_scroll;
        LinearLayout ll_scroll;
    }

    class ItemViewHolder {
        View itemView;
        TextView programTitleTextView;
        TextView programTimeTextView;
        TextView tv_start_time;
        TextView tv_end_time;
        ProgressBar pb_time;
    }
}
