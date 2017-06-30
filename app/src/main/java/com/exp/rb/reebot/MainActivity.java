package com.exp.rb.reebot;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.hardware.ConsumerIrManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.exp.rb.reebot.IR.RBIRGetSignal;
import com.exp.rb.reebot.IR.RBIRParamData;
import com.exp.rb.reebot.IR.RBSignalForJSON;
import com.exp.rb.reebot.IR.VIRCommandMaker;
import com.exp.rb.reebot.util.BookmarkInfo;
import com.exp.rb.reebot.view.EPGListViewAdapter_new;
import com.exp.rb.reebot.view.RBBookingManager;
import com.exp.rb.reebot.view.RBSetting;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EPGManager.EPGManagerListener, RBIRGetSignal.RBIRGetSignalListener, RBBookingManager.OnFragmentInteractionListener, RBSetting.OnFragmentInteractionListener ,RBAuthManager.RBAuthManagerListener{

    private static final String TAG = "ReeBot(MainActivity)";
    private File mFolder;
    private String mFileName;
    private String sid = "freemanrws";
    private String email = "";
    private String pushtoken;
    final String qurl = "http://reebot.io:8083/api/getepglist";
    //final String qurl = "http://reebot.io:8083/api/req_epg2json";
    final String q2url = "http://reebot.io:8083/api/req_nextepg2json";
    final String q3url = "http://reebot.io:8083/api/addbooking";
    final String q4url = "http://reebot.io:8083/api/req_fepg2json";
    final String q5url = "http://reebot.io:8083/api/searchprogram";
    final String url = "http://reebot.io:8083/api/req_epg";
    final String surl = "http://reebot.io:8085";
    final String getsignalurl = "http://reebot.io:8083/api/req_getirsignal";
    //final String url ="http://teddyandymom.asuscomm.com:8083/api/req_epg";
    //final String surl="http://teddyandymom.asuscomm.com:8085";
    private String tvbrend = "";
    //private String catvbrend = "S_LG";
    private String catv ="";
    private String tv ="";
    private String tvst = "TSSL";
    private MainActivity mainActivity;

    private FrameLayout line1;
    private FrameLayout line2;
    private FrameLayout line3;
//    private FrameLayout line4;
//    private FrameLayout line5;

    private LinearLayout ll_search;
    private ArrayList<String> bookmardList;
    private BookmarkInfo bookmarkInfo;
    private Boolean exit = false;
    private EPGManagerForJSON epgjson;
    private EPGListViewAdapter_new adapter;
    private ConsumerIrManager mCIR;
    private int tabnum = 2;

    private String acesstoken="";
    private  JSONObject stboxsignal;
    private  JSONObject tvsignal;
    EPGManager epgmanager;
    ListView listview;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("reebot");
        pushtoken = FirebaseInstanceId.getInstance().getToken();
        Log.d("TAG", "onTokenRefresh : " + pushtoken);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        GradientDrawable gd1 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {Color.parseColor("#3fa3f5"),Color.parseColor("#80b33ce9")});
        toolbar.setBackgroundDrawable(gd1);

        View headerView = navigationView.getHeaderView(0);
        GradientDrawable gd2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {Color.parseColor("#80b33ce9"),Color.parseColor("#3fa3f5")});
        headerView.setBackgroundDrawable(gd2);

        //setting initdata
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("email") != null) {
                //TODO here get the string stored in the string variable and do
                Log.d(TAG, "email : " + bundle.getString("email"));
                email = bundle.getString("email");
                // setText() on userName
                ((TextView)headerView.findViewById(R.id.txt_email)).setText(email);
            }
            if (bundle.getString("tvb") != null) {

                tv = bundle.getString("tvb");
                Log.d(TAG, "tvb : " + bundle.getString("tvb"));
                //TODO here get the string stored in the string variable and do
                // setText() on userName
                //make combination power management
                tvst = "";
                tvst = "T";
                if (bundle.getString("tvb").equals("LG")) {
                    tvst += "L";
                } else {
                    tvst += "S";
                }
            }
            if (bundle.getString("catvb") != null) {
                Log.d(TAG, "catvb : " + bundle.getString("catvb"));
                catv = bundle.getString("catvb");
                //catv = bundle.getString("catvb");
            } else {

            }
            if (bundle.getString("acesstoken") != null) {
                Log.d(TAG, "acesstoken : " + bundle.getString("acesstoken"));
                acesstoken = bundle.getString("acesstoken");
            } else {

            }

            if (bundle.getString("sid") != null) {
                //String[] separated = email.split("@");
                sid = bundle.getString("sid");
                Log.d(TAG, "sid : " + bundle.getString("sid"));

                if ("nodevice".equals(sid)) {
                    //IR 장치 사용 가능 여부 체크
                    ConsumerIrManager mCIR = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);
                    if (mCIR == null) {

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            if (mCIR.hasIrEmitter()) {
                                //내장 IR 사용 가능
                                sid = "internelIR";
                                Log.d(TAG, "내장 IR 사용 가능");

                            } else {

                            }
                        } else {

                        }
                    }
                }
            }
            //silver20170530
            if ("booking".equals(bundle.getString("RbLaunchType")))
            {
                Log.d(TAG, "booking : " + "GOGO");
                String title = bundle.getString("BookingBody");
                String chnum = bundle.getString("BookingChnum");
                System.out.println(TAG + ", sendNotification: " + title + "  /  " + chnum);
                showBookingDialog(title, chnum);
            }
            //silver20170530
        }
        Log.d(TAG, "All Power signal : " + tvst);
        mainActivity = this;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //checkAccessibilityPermissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (!checkAccessibilityPermissions()) {
                //setAccessibilityPermissions();
                Toast.makeText(getBaseContext(), "Not Enough Accessibility Permissions ", Toast.LENGTH_LONG).show();
            }
        }

        LinearLayout bchannelbtn = (LinearLayout) findViewById(R.id.BeforeChannelBtn);
        bchannelbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "bchannelbtn on");
                new VIRCommandMaker(getApplicationContext(),surl,email,"STBOX","BACK", sid);
            }
        });
        LinearLayout apwbtn = (LinearLayout) findViewById(R.id.AllPowerBtn);
        apwbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "apwbtn on : " + tvst);
                new VIRCommandMaker(getApplicationContext(),surl,email,"TV","POWER", sid);
                new VIRCommandMaker(getApplicationContext(),surl,email,"STBOX","POWER", sid);
            }
        });
        LinearLayout tpwbtn = (LinearLayout) findViewById(R.id.TVPowerBtn);
        tpwbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "tpwbtn on");
                new VIRCommandMaker(getApplicationContext(),surl,email,"TV","POWER", sid);
            }
        });


        TextView btn1 = (TextView) findViewById(R.id.text_btn1);
        TextView btn2 = (TextView) findViewById(R.id.text_btn2);
        TextView btn3 = (TextView) findViewById(R.id.text_btn3);
        line1 = (FrameLayout) findViewById(R.id.btn1_line);
        line2 = (FrameLayout) findViewById(R.id.btn2_line);
        line3 = (FrameLayout) findViewById(R.id.btn3_line);

        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        ll_search.setVisibility(View.GONE);
        final EditText et_search = (EditText) findViewById(R.id.et_search);
        Button btn_search = (Button) findViewById(R.id.btn_search);

        bookmarkInfo = new BookmarkInfo(this);
        bookmardList = bookmarkInfo.getBookmarkList();

        line1.setBackgroundColor(Color.parseColor("#ffffff"));
        line2.setBackgroundColor(Color.parseColor("#3fa3f5"));
        line3.setBackgroundColor(Color.parseColor("#ffffff"));
        ll_search.setVisibility(View.GONE);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "btn1");
                tabnum =1;
                adapter.clearList();
                line1.setBackgroundColor(Color.parseColor("#3fa3f5"));
                line2.setBackgroundColor(Color.parseColor("#ffffff"));
                line3.setBackgroundColor(Color.parseColor("#ffffff"));
                ll_search.setVisibility(View.GONE);

                //silver - 채널정보 어떻게 가져올지, 어떤거 저장할지 확인 필요
                bookmardList = bookmarkInfo.getBookmarkList();
                adapter.clearList();
                //Toast.makeText(MainActivity.this, bookmardList.toString(), Toast.LENGTH_SHORT).show();

                epgjson = new EPGManagerForJSON(mainActivity);
                String nbookmardList ="";
                nbookmardList = bookmardList.toString().replace("["," ");
                nbookmardList = nbookmardList.toString().replace("]"," ");
                epgjson.execute(new EPGParamData(4,null, null, adapter, q4url, catv.replaceAll("[0-9]",""),nbookmardList));

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabnum =2;
                adapter.clearList();
                //Log.d(TAG, "btn2");
                line1.setBackgroundColor(Color.parseColor("#ffffff"));
                line2.setBackgroundColor(Color.parseColor("#3fa3f5"));
                line3.setBackgroundColor(Color.parseColor("#ffffff"));
                ll_search.setVisibility(View.GONE);

                //갱신
                epgjson = new EPGManagerForJSON(mainActivity);
                epgjson.execute(new EPGParamData(null, null, adapter, qurl, catv.replaceAll("[0-9]","")));
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabnum = 3;
                adapter.clearList();
                //Log.d(TAG, "btn3");
                line1.setBackgroundColor(Color.parseColor("#ffffff"));
                line2.setBackgroundColor(Color.parseColor("#ffffff"));
                line3.setBackgroundColor(Color.parseColor("#3fa3f5"));
                ll_search.setVisibility(View.VISIBLE);

                //검색\
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = et_search.getText().toString();
                //Log.d(TAG, "btn_search : " + text);
                //키보드 숨기기
                InputMethodManager mgr  = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(navigationView.getWindowToken(),0);
                adapter.clearList();
                epgjson = new EPGManagerForJSON(mainActivity);
                epgjson.execute(new EPGParamData(5, adapter, q5url, catv.replaceAll("[0-9]",""),text));
            }
        });



        //CATV리모컨 신호 다운로드
        String extr = getApplicationContext().getFilesDir().getPath().toString();
        try {
            File mFolder = new File(extr + "/irsignal");
            Log.d(TAG,"signalsboxForJSON File : " + makefilename(email.split("@")[0],"room1" , "signalsbox"));
            File catvjfile = new File(mFolder.getAbsolutePath(),makefilename(email.split("@")[0],"room1" , "signalsbox"));
            if(!catvjfile.exists())
            {
                Log.d(TAG, "signalsbox 다운 로드 프로세스 : " +catv);
                String[] separated = email.split("@");
                RBIRGetSignal signal = new RBIRGetSignal(this);
                signal.execute(new RBIRParamData(2, getsignalurl, separated[0], "room1", "signalsbox", catv, ""));//버그수정
            }

            File tvjfile = new File(mFolder.getAbsolutePath(),makefilename(email.split("@")[0],"room1" , "signaltv"));
            if(!tvjfile.exists())
            {
                Log.d(TAG, "signaltv 다운 로드 프로세스 : " +tv);
                String[] separated = email.split("@");
                RBIRGetSignal signal = new RBIRGetSignal(this);
                signal.execute(new RBIRParamData(2, getsignalurl, separated[0], "room1", "signaltv", tv, ""));
            }
        }
        catch (Exception e) {
            Log.d(TAG, "다운 로드 프로세스 Exception : " +e);
        }


        adapter = new EPGListViewAdapter_new(this, surl, q2url, q3url, catv.replaceAll("[0-9]",""), sid, email, pushtoken,acesstoken);
        listview = (ListView) findViewById(R.id.EPGListView);
        listview.setAdapter(adapter);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            Log.d(TAG, "LOGOUT START : 101");
            Intent resultIntent = new Intent();
            resultIntent.putExtra("control", "erasedata");
            setResult(101, resultIntent);

            UserManagement.requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout()
                {
                    Log.d(TAG, "KAKAO_LOGOUT : OK");
                    finish();
                }
            });
            Log.d(TAG, "onActivityResult : erasedata");
            finish();
            // Handle the camera action
        }

        else if (id == R.id.nav_setting) {

            findViewById(R.id.SubLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.MainLayout).setVisibility(View.INVISIBLE);
            RBSetting fr = new RBSetting();
            getSupportFragmentManager().beginTransaction().replace(R.id.SubLayout,RBSetting.newInstance(email,acesstoken)).addToBackStack(null).commit();


        }


        else if (id == R.id.nav_manage) {

            findViewById(R.id.SubLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.MainLayout).setVisibility(View.INVISIBLE);
            RBBookingManager fr = new RBBookingManager();
            getSupportFragmentManager().beginTransaction().replace(R.id.SubLayout,RBBookingManager.newInstance(email,acesstoken)).addToBackStack(null).commit();

        }

        //else if (id == R.id.nav_send_bug) {
        //}

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void EPGManagerTaskResult(String msg) {
        if (msg.equals("END|MAKEEPG"))
        {
            //epgprogressDialog.dismiss();
            Log.d(TAG, "END|MAKEEPG");
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListView listView = (ListView) parent;
                    EPGListViewItem item = (EPGListViewItem) listView.getItemAtPosition(position);
                    Log.d(TAG, "Click info: " + item.getProgramTitle() + "(" + item.getChnumber() + ")");
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date startDate =null ;
                    try {
                        startDate = simpleDateFormat1.parse(item.getStarttime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    new VIRCommandMaker(getApplicationContext(), surl, catv, "CHNUMBER", item.getChnumber(), sid);
                }
            });
        }
        else
        {
            EPGListViewAdapter_new adapter = new EPGListViewAdapter_new(this, surl, q2url, q3url, catv.replaceAll("[0-9]",""), sid, email, pushtoken,acesstoken);
            listview = (ListView) findViewById(R.id.EPGListView);
            listview.setAdapter(adapter);
            epgmanager.execute(new EPGParamData(mFolder, mFileName, adapter));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            Log.d(TAG, "KEYCODE_VOLUME_UP" + sid);
            new VIRCommandMaker(getApplicationContext(),surl,email,"STBOX","VOL+", sid);
            //new VIRCommandMaker(getApplicationContext(), surl, catv, "CONTROL", "VOL+", sid);
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            Log.d(TAG, "KEYCODE_VOLUME_DOWN" + sid);
            new VIRCommandMaker(getApplicationContext(),surl,email,"STBOX","VOL-", sid);
            //new VIRCommandMaker(getApplicationContext(), surl, catv, "CONTROL", "VOL-", sid);
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_BACK) {


            if (exit) {
                finish(); // finish activity
                moveTaskToBack(true);
                System.exit(0);
            } else {

                if(getSupportFragmentManager().getBackStackEntryCount() > 0 )
                {
                    getSupportFragmentManager().popBackStack();

                    int count = getSupportFragmentManager().getBackStackEntryCount();
                    Log.d(TAG,"getBackStackEntryCount : " + count);
                    if( count == 1 )
                    {
                        findViewById(R.id.SubLayout).setVisibility(View.INVISIBLE);
                        findViewById(R.id.MainLayout).setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    Toast.makeText(this, "Press Back again to Exit.",
                            Toast.LENGTH_SHORT).show();
                    exit = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exit = false;
                        }
                    }, 3 * 1000);
                }
            }



            return true;
        }
        else
            return super.onKeyDown(keyCode, event);
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public boolean checkAccessibilityPermissions() {
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        // getEnabledAccessibilityServiceList는 현재 접근성 권한을 가진 리스트를 가져오게 된다
        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.DEFAULT);
        for (int i = 0; i < list.size(); i++) {
            AccessibilityServiceInfo info = list.get(i);
            // 접근성 권한을 가진 앱의 패키지 네임과 패키지 네임이 같으면 현재앱이 접근성 권한을 가지고 있다고 판단함
            if (info.getResolveInfo().serviceInfo.packageName.equals(getApplication().getPackageName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...
        Log.d(TAG, "onResume");
        //adapter = new EPGListViewAdapter_new(this) ;
        adapter = new EPGListViewAdapter_new(this, surl, q2url, q3url, catv.replaceAll("[0-9]",""), sid, email, pushtoken, acesstoken);
        listview.setAdapter(adapter);
        epgjson = new EPGManagerForJSON(mainActivity);
        if( tabnum == 2) {
            epgjson.execute(new EPGParamData(null, null, adapter, qurl, catv.replaceAll("[0-9]","")));
        }
        else if(tabnum == 1)
        {
            String nbookmardList ="";
            nbookmardList = bookmardList.toString().replace("["," ");
            nbookmardList = nbookmardList.toString().replace("]"," ");
            epgjson.execute(new EPGParamData(4,null, null, adapter, q4url.replaceAll("[0-9]",""), catv.replaceAll("[0-9]",""),nbookmardList));
        }
        else
        {
            
        }
        adapter.notifyDataSetChanged();
        adapter.startRefreshTime();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adapter != null) {
            adapter.stopRefreshTime();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //case R.id.action_settings:
            // User chose the "Settings" item, show the app settings UI...
            //    return true;

           // case R.id.action_logout:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...



    //            return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    private void showBookingDialog(String title, final String chnum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alert = builder.create();
        alert.setIcon(R.mipmap.ic_launcher);
        alert.setTitle("예약 안내");
        alert.setMessage(title + "\n예약하신 방송을 시청하시겠습니까?");
        alert.setButton(Dialog.BUTTON_POSITIVE, "시청", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG,"VIRCommandMaker"+surl+" / "+ email+" / "+"STBOX"+" / " +chnum+" / "+ sid);
                new VIRCommandMaker(getApplicationContext(),surl,email,"STBOX",chnum, sid);
                alert.dismiss();
            }
        });
        alert.setButton(Dialog.BUTTON_NEGATIVE, "전원+시청", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new VIRCommandMaker(getApplicationContext(),surl,email,"TV","POWER", sid);
                new VIRCommandMaker(getApplicationContext(),surl,email,"STBOX",chnum, sid);
                alert.dismiss();
            }
        });
        alert.setButton(Dialog.BUTTON_NEUTRAL, "취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });
        alert.show();
    }


    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w(TAG, "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }

    private String makefilename(String id, String location, String devicename)
    {
        String filename = id+"_"+location+"_"+devicename+".json";
        return  filename;
    }


    @Override
    public void RBIRGetSignaTaskResult(String msg) {

    }

    @Override
    public void RBIRGetListSignaTaskResult(String msg) {

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

        Log.d(TAG,"onFragmentInteraction"+uri);
    }

    @Override
    public void RBAuthSinginTaskResult(String msg) {

    }

    @Override
    public void RBAuthLoginTaskResult(String msg) {

    }

    @Override
    public void RBAuthCheckdevTaskResult(String msg) {

    }

    @Override
    public void RBAuthChecktokenTaskResult(String msg) {

    }
    @Override
    public void onRBsettingFragmentInteraction(String msg) {
        getSupportFragmentManager().popBackStack();


        tv =msg.split("/")[0];
        catv =msg.split("/")[1];

        //remote signal download

        //CATV리모컨 신호 다운로드
        String extr = getApplicationContext().getFilesDir().getPath().toString();
        try {
            File mFolder = new File(extr + "/irsignal");
            Log.d(TAG,"signalsboxForJSON File : " + makefilename(email.split("@")[0],"room1" , "signalsbox"));
            //File catvjfile = new File(mFolder.getAbsolutePath(),makefilename(email.split("@")[0],"room1" , "signalsbox"));
            //if(!catvjfile.exists())
            //{
            Log.d(TAG, "signalsbox 다운 로드 프로세스 : " +catv);
            String[] separated = email.split("@");
            RBIRGetSignal catvsignal = new RBIRGetSignal(this);
            catvsignal.execute(new RBIRParamData(2, getsignalurl, separated[0], "room1", "signalsbox", catv, ""));
            //}
            //File tvjfile = new File(mFolder.getAbsolutePath(),makefilename(email.split("@")[0],"room1" , "signaltv"));
            //if(!tvjfile.exists())
            //{
            Log.d(TAG, "signaltv 다운 로드 프로세스 : " +tv);
                //String[] separated = email.split("@");
            RBIRGetSignal tvsignal = new RBIRGetSignal(this);
            tvsignal.execute(new RBIRParamData(2, getsignalurl, separated[0], "room1", "signaltv",tv, ""));
            //}
        }
        catch (Exception e) {
            Log.d(TAG, "다운 로드 프로세스 Exception : " +e);
        }

        findViewById(R.id.SubLayout).setVisibility(View.INVISIBLE);
        findViewById(R.id.MainLayout).setVisibility(View.VISIBLE);
        onResume();
    }
}
