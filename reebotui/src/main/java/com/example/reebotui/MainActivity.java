package com.example.reebotui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.ConsumerIrManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reebotui.api.ReeBotApi;
import com.example.reebotui.channel.ChannelFragment;
import com.example.reebotui.channel.ProgramListVerticalDialog;
import com.example.reebotui.channel.RemoteControlDialog;
import com.example.reebotui.epglist.EPGItem;
import com.example.reebotui.epglist.EPGListViewItem;
import com.example.reebotui.info.BookmarkInfo;
import com.example.reebotui.info.EPGParamData;
import com.example.reebotui.info.ExtraInfo;
import com.example.reebotui.info.RBIRParamData;
import com.example.reebotui.interfaceclass.EventCallback;
import com.example.reebotui.interfaceclass.OnEPGListClickListener;
import com.example.reebotui.ir.VIRCommandMaker;
import com.example.reebotui.util.AppUtil;
import com.exp.rb.reebot.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnEPGListClickListener, EventCallback, View.OnClickListener {

    private static final String TAG = "ReeBot(MainActivity)";
    private final String surl = "http://reebot.io:8085";

    private SearchView searchView;
    private boolean isSearchViewExpanded = false;
//    private int selectFragmentPostition = 0;
//    private int prevSelectFragmentPostition = 0;


    private String email = "";
    private String tvst = "TSSL";
    private String tvb = "";
    private String catv = "";
    private String sid = "freemanrws";
    private String acesstoken = "";

    private ReeBotApi reeBotApi;
    private ArrayList<String> bookmardList;
    private BookmarkInfo bookmarkInfo;

    private boolean isChangeService = false;

    private ArrayList<String> tagList = new ArrayList<String>();

    public String getCatv() {
        return catv;
    }

    public String getCatvRemoveNumber() {
        return catv.replaceAll("[0-9]", "");
    }

    public String getEmail() {
        return email;
    }

    public String getSid() {
        return sid;
    }

    public String getAcesstoken() {
        return acesstoken;
    }

    public BookmarkInfo getBookmarkInfo() {
        return bookmarkInfo;
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public ChannelFragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.d(TAG, "SectionsPagerAdapter getItem position: " + position);
            ChannelFragment channelFragment = ChannelFragment.newInstance(position);


            return channelFragment;
//            return ChannelFragment.newInstance(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            Object object = super.instantiateItem(container, position);
            if (object instanceof Fragment) {
                Log.d(TAG, "SectionsPagerAdapter instantiateItem getTag: " + ((Fragment) object).getTag());
                tagList.add(((Fragment) object).getTag());
                tagList = new ArrayList<String>(new HashSet<String>(tagList));
            }

            return object;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case ChannelFragment.ARG_LIST_TYPE_BOOKMARK:
                    return "즐겨찾기";
                case ChannelFragment.ARG_LIST_TYPE_ALL:
                    return "전체";
                case ChannelFragment.ARG_LIST_TYPE_SEARCH:
                    return "프로그램 검색";
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivity onCreate");
        setContentView(R.layout.activity_main);

        getIntentData();

        reeBotApi = new ReeBotApi(this);
        reeBotApi.setEventCallback(this);

        bookmarkInfo = new BookmarkInfo(this);
        bookmardList = bookmarkInfo.getBookmarkList();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        GradientDrawable gd1 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {Color.parseColor("#3fa3f5"),Color.parseColor("#80b33ce9")});
//        toolbar.setBackgroundDrawable(gd1);
        View headerView = navigationView.getHeaderView(0);
//        GradientDrawable gd2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {Color.parseColor("#80b33ce9"),Color.parseColor("#3fa3f5")});
//        headerView.setBackgroundDrawable(gd2);

        ((TextView) headerView.findViewById(R.id.tv_nav_email)).setText(email);

        headerView.findViewById(R.id.btn_nav_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        headerView.findViewById(R.id.btn_nav_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "LOGOUT START : 101");
                Intent resultIntent = new Intent();
                resultIntent.putExtra("control", "erasedata");
                setResult(RESULT_OK, resultIntent);

                UserManagement.requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Log.d(TAG, "KAKAO_LOGOUT : OK");
                        finish();
                    }
                });
                Log.d(TAG, "btn_nav_logout : erasedata");
                finish();
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                System.out.println("reebot onPageSelected position " + position);
                System.out.println("reebot onPageSelected searchView " + searchView);


                if (searchView != null) {
                    if (position == ChannelFragment.ARG_LIST_TYPE_SEARCH) {
//                        prevSelectFragmentPostition = selectFragmentPostition;
                        searchView.onActionViewExpanded();
                    } else {
                        searchView.onActionViewCollapsed();
                    }
                }

//                selectFragmentPostition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (bookmardList == null || bookmardList.size() == 0) {
            mViewPager.setCurrentItem(ChannelFragment.ARG_LIST_TYPE_ALL);
        }


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                RemoteControlDialog remoteControlDialog = new RemoteControlDialog(MainActivity.this, surl, email, sid);
                remoteControlDialog.show();
            }
        });

        reqIrSignalData();

        findViewById(R.id.btn_ir_channel_pre).setOnClickListener(this);
        findViewById(R.id.btn_ir_power_all).setOnClickListener(this);
        findViewById(R.id.btn_ir_power_tv).setOnClickListener(this);
        findViewById(R.id.btn_ir_power_sbox).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "MainActivity onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume");
        if (isChangeService) {
            isChangeService = false;

            Log.d(TAG, "tagList size: " + tagList.size());
            for (int i = 0; i < tagList.size(); i++) {
                Log.d(TAG, i + " tagList : " + tagList.get(i));
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(tagList.get(i));
                if (fragment instanceof ChannelFragment) {
                    ((ChannelFragment) fragment).refreshEpgList();
                }
            }

//            mSectionsPagerAdapter.getItem(0).refreshEpgList();
//            mSectionsPagerAdapter.getItem(1).refreshEpgList();
        }
    }

    public void notifyDataSetChangedList() {
        Log.d(TAG, "tagList size: " + tagList.size());
        for (int i = 0; i < tagList.size(); i++) {
            Log.d(TAG, i + " tagList : " + tagList.get(i));
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(tagList.get(i));
            if (fragment instanceof ChannelFragment) {
                ((ChannelFragment) fragment).refreshListAdapter();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult requestCode: " + requestCode);
        Log.d(TAG, "onActivityResult resultCode: " + resultCode);
        Log.d(TAG, "onActivityResult data: " + data);

        if (requestCode == ExtraInfo.INTENT_CODE_SETTING_CATV) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    tvb = data.getStringExtra("tvb");
                    catv = data.getStringExtra("catv");
                    Log.d(TAG, "onActivityResult tvb: " + tvb);
                    Log.d(TAG, "onActivityResult catv: " + catv);
                    isChangeService = true;
//                    mSectionsPagerAdapter.getItem(0).refreshEpgList();
//                    mSectionsPagerAdapter.getItem(1).refreshEpgList();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (searchView != null && isSearchViewExpanded) {
                searchView.onActionViewCollapsed();
                isSearchViewExpanded = false;
            } else {
                super.onBackPressed();
            }
//            if (selectFragmentPostition == ChannelFragment.ARG_LIST_TYPE_SEARCH) {
//                mViewPager.setCurrentItem(prevSelectFragmentPostition);
//            } else {
//                super.onBackPressed();
//            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "MainActivity onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("검색할 프로그램 이름 입력");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                System.out.println("reebot onQueryTextSubmit " + s);

                searchProgram(s);


                searchView.onActionViewCollapsed();
                isSearchViewExpanded = false;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                System.out.println("reebot onQueryTextChange " + s);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("reebot setOnSearchClickListener ");
                isSearchViewExpanded = true;
//                mViewPager.setCurrentItem(2);

//                mCloseButton.setVisibility(View.VISIBLE);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                isSearchViewExpanded = false;
                System.out.println("reebot setOnCloseListener ");
//                mViewPager.setCurrentItem(prevSelectFragmentPostition);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setting) {

//            findViewById(R.id.SubLayout).setVisibility(View.VISIBLE);
//            findViewById(R.id.MainLayout).setVisibility(View.INVISIBLE);
//            RBSetting fr = new RBSetting();
//            getSupportFragmentManager().beginTransaction().replace(R.id.SubLayout,RBSetting.newInstance(email,acesstoken)).addToBackStack(null).commit();

            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            intent.putExtra("catv", catv);
            intent.putExtra("tvb", tvb);
            intent.putExtra("acesstoken", acesstoken);
            startActivityForResult(intent, ExtraInfo.INTENT_CODE_SETTING_CATV);

        } else if (id == R.id.nav_booking_management) {

//            findViewById(R.id.SubLayout).setVisibility(View.VISIBLE);
//            findViewById(R.id.MainLayout).setVisibility(View.INVISIBLE);
//            RBBookingManager fr = new RBBookingManager();
//            getSupportFragmentManager().beginTransaction().replace(R.id.SubLayout,RBBookingManager.newInstance(email,acesstoken)).addToBackStack(null).commit();
            Intent intent = new Intent(MainActivity.this, BookingActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("acesstoken", acesstoken);
            startActivity(intent);

        } else if (id == R.id.nav_send_bug) {

        }

//        item.setChecked(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ir_channel_pre:
                new VIRCommandMaker(getApplicationContext(), surl, email, "STBOX", "BACK", sid);
                break;
            case R.id.btn_ir_power_all:
                new VIRCommandMaker(getApplicationContext(), surl, email, "TV", "POWER", sid);
                new VIRCommandMaker(getApplicationContext(), surl, email, "STBOX", "POWER", sid);
                break;
            case R.id.btn_ir_power_tv:
                new VIRCommandMaker(getApplicationContext(), surl, email, "TV", "POWER", sid);
                break;
            case R.id.btn_ir_power_sbox:
                new VIRCommandMaker(getApplicationContext(), surl, email, "STBOX", "POWER", sid);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            Log.d(TAG, "KEYCODE_VOLUME_UP" + sid);
            new VIRCommandMaker(getApplicationContext(), surl, email, "STBOX", "VOL+", sid);
            //new VIRCommandMaker(getApplicationContext(), surl, catv, "CONTROL", "VOL+", sid);
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            Log.d(TAG, "KEYCODE_VOLUME_DOWN" + sid);
            new VIRCommandMaker(getApplicationContext(), surl, email, "STBOX", "VOL-", sid);
            //new VIRCommandMaker(getApplicationContext(), surl, catv, "CONTROL", "VOL-", sid);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void OnEPGListClickListener(EPGListViewItem epgListViewItem) {
        System.out.println("reebot MainActivity OnEPGListClickListener " + epgListViewItem.getSelectedItem().getProgramTitle());
        clickEpgItem(epgListViewItem.getSelectedItem());
    }

    @Override
    public void OnEPGListClickListener(EPGListViewItem epgListViewItem, int id) {
        System.out.println("reebot MainActivity OnEPGListClickListener " + id);
        switch (id) {
            case R.id.iv_bookmark:
                break;

            case R.id.iv_channel_list:
//                ProgramListVerticalDialog programListDialog = new ProgramListVerticalDialog(MainActivity.this, epgListViewItem, onEPGItemClickListener);
//                programListDialog.show();
                break;
        }
    }

//    OnEPGItemClickListener onEPGItemClickListener = new OnEPGItemClickListener() {
//        @Override
//        public void onEPGItemClickListener(EPGItem epgItem) {
//            System.out.println("reebot MainActivity onEPGItemClickListener " + epgItem.getProgramTitle());
//        }
//    };

    @Override
    public void onEvent(int event, String msg) {
        Log.d(TAG, "onEvent event: " + event);
        Log.d(TAG, "onEvent msg: " + msg);

        switch (event) {
//            case ExtraInfo.REQ_MODE_EPGLIST_ALL: {
//                ArrayList<EPGListViewItem> list = getEPGListViewList(getActivity(), msg, ((MainActivity) getActivity()).getCatv());
//                epgListViewAdapter.addItem(list);
//                mSwipeRefreshLayout.setRefreshing(false);
//            }
//            break;
//            case ExtraInfo.REQ_MODE_EPGLIST_CHANNEL: {
//                getProgramList(msg, channelEpgListViewItem);
//                ProgramListVerticalDialog programListDialog = new ProgramListVerticalDialog(getActivity(), channelEpgListViewItem, onEPGItemClickListener);
//                programListDialog.show();
//            }
//            break;
//            case ExtraInfo.REQ_MODE_EPGLIST_BOOKMARK:
//                break;
            case ExtraInfo.REQ_MODE_EPGLIST_SEARCH: {
                ArrayList<EPGListViewItem> list = getEPGListViewList(MainActivity.this, msg, getCatvRemoveNumber());
                ProgramListVerticalDialog programListDialog = new ProgramListVerticalDialog(this, list, this);
                programListDialog.show();
            }
            break;
        }

    }


    private void getIntentData() {
        //setting initdata
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("email") != null) {
                //TODO here get the string stored in the string variable and do
                Log.d(TAG, "email : " + bundle.getString("email"));
                email = bundle.getString("email");
                // setText() on userName
//                ((TextView) headerView.findViewById(R.id.txt_email)).setText(email);
            }
            if (bundle.getString("tvb") != null) {

                tvb = bundle.getString("tvb");
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
            if ("booking".equals(bundle.getString("RbLaunchType"))) {
                Log.d(TAG, "booking : " + "GOGO");
                String title = bundle.getString("BookingBody");
                String chnum = bundle.getString("BookingChnum");
                System.out.println(TAG + ", sendNotification: " + title + "  /  " + chnum);
                showBookingDialog(title, chnum);
            }
            //silver20170530
        }
    }


    private void searchProgram(String text) {
        EPGParamData epgParamData = new EPGParamData(ExtraInfo.REQ_MODE_EPGLIST_SEARCH, getCatvRemoveNumber());
        epgParamData.setSearchstring(text);
        reeBotApi.reqEpgList(epgParamData);

    }

    private ArrayList<EPGListViewItem> getEPGListViewList(Context context, String epgjson, String catv) {
//        Log.d(TAG, "Response JSON :" + epgjson);
        ArrayList<EPGListViewItem> list = new ArrayList<EPGListViewItem>();
        try {
            JSONArray jArray = new JSONArray(epgjson);
            System.out.println("getEPGListViewList jArray.length: " + jArray.length());
            if (jArray.length() == 0) {

            } else {
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    JSONObject channel = oneObject.getJSONObject("channel");
                    JSONObject program = channel.getJSONObject("program");
                    JSONObject isplist = channel.getJSONObject("isplist");
                    JSONObject isp = isplist.getJSONObject(catv);
                    String chnum = Integer.toString(isp.getInt("chnum"));
                    String chname = channel.getString("name");
                    String chlogoname = channel.getString("imglogoname");
                    String maintitle = program.getString("maintitle");
                    String subtitle = program.getString("subtitle");
                    String category = program.getString("category");
                    String starttime = program.getString("starttime");
                    String endtime = program.getString("endtime");
                    String episode = program.getString("episode");
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

                    EPGItem item = new EPGItem(title, entryInfo, chnum, category, category, starttime, endtime, chname);
                    EPGListViewItem epgListViewItem = new EPGListViewItem();
                    epgListViewItem.setChlogoiconDrawable(AppUtil.getImage(context, chlongo, chlogoname));
                    epgListViewItem.setBsstatusiconDrawable(ContextCompat.getDrawable(context, R.drawable.icon_fav_normal));
                    epgListViewItem.getList().add(item);
                    list.add(epgListViewItem);
//                    adapter.addItem(getImage(context,chlongo,chlogoname), ContextCompat.getDrawable(context, R.drawable.unbookmark), title, entryInfo ,chnum,category,category, starttime, endtime,chname) ;
                    //Log.d(TAG, "maintitle : ("+ chnum +")" +  maintitle+" / IMG : "+ chname + "/ IMG2 : "+ chlogoname);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("getEPGListViewList list.size: " + list.size());
        return list;
    }

    private void reqIrSignalData() {
        //CATV리모컨 신호 다운로드
        String extr = getApplicationContext().getFilesDir().getPath().toString();
        try {
            File mFolder = new File(extr + "/irsignal");
            Log.d(TAG, "signalsboxForJSON File : " + makefilename(email.split("@")[0], "room1", "signalsbox"));
            File catvjfile = new File(mFolder.getAbsolutePath(), makefilename(email.split("@")[0], "room1", "signalsbox"));
            if (!catvjfile.exists()) {
                Log.d(TAG, "signalsbox 다운 로드 프로세스 : " + catv);
                String[] separated = email.split("@");
//                RBIRGetSignal signal = new RBIRGetSignal(this);
//                signal.execute(new RBIRParamData(2, getsignalurl, separated[0], "room1", "signalsbox", catv, ""));
                reeBotApi.reqSignalList(new RBIRParamData(ExtraInfo.REQ_MODE_REMOTE_IR_SIGNAL_SBOX, "", separated[0], "room1", "signalsbox", catv, ""));
            }

            File tvjfile = new File(mFolder.getAbsolutePath(), makefilename(email.split("@")[0], "room1", "signaltv"));
            if (!tvjfile.exists()) {
                Log.d(TAG, "signaltv 다운 로드 프로세스 : " + tvb);
                String[] separated = email.split("@");
//                RBIRGetSignal signal = new RBIRGetSignal(this);
//                signal.execute(new RBIRParamData(2, getsignalurl, separated[0], "room1", "signaltv", tv, ""));
                reeBotApi.reqSignalList(new RBIRParamData(ExtraInfo.REQ_MODE_REMOTE_IR_SIGNAL_TV, "", separated[0], "room1", "signaltv", tvb, ""));
            }
        } catch (Exception e) {
            Log.d(TAG, "다운 로드 프로세스 Exception : " + e);
        }
    }

    private String makefilename(String id, String location, String devicename) {
        String filename = id + "_" + location + "_" + devicename + ".json";
        return filename;
    }

    private void showBookingDialog(String title, final String chnum) {
        final String surl = "http://reebot.io:8085";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alert = builder.create();
        alert.setIcon(R.mipmap.ic_launcher);
        alert.setTitle("예약 안내");
        alert.setMessage(title + "\n예약하신 방송을 시청하시겠습니까?");
        alert.setButton(Dialog.BUTTON_POSITIVE, "시청", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "VIRCommandMaker" + surl + " / " + email + " / " + "STBOX" + " / " + chnum + " / " + sid);
                new VIRCommandMaker(getApplicationContext(), surl, email, "STBOX", chnum, sid);
                alert.dismiss();
            }
        });
        alert.setButton(Dialog.BUTTON_NEGATIVE, "전원+시청", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new VIRCommandMaker(getApplicationContext(), surl, email, "TV", "POWER", sid);
                new VIRCommandMaker(getApplicationContext(), surl, email, "STBOX", chnum, sid);
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

    private void clickEpgItem(EPGItem epgItem) {


        Log.d(TAG, "Click info: " + epgItem.getProgramTitle() + "(" + epgItem.getChnumber() + ")");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
        Date startDate = null;

        try {
            startDate = simpleDateFormat1.parse(epgItem.getStarttime());
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        Date cDate = new Date(calendar.getTimeInMillis());
        // Log.d(TAG," ctime :"+  cDate.getTime() + "starttime :" + startDate.getTime()  );
        System.out.println("reebot ChannelFragment OnEPGListClickListener check time: " + (startDate.getTime() > cDate.getTime()));

        if (startDate.getTime() > cDate.getTime()) {
//            bookingProgram(epgListViewItem);

            FirebaseMessaging.getInstance().subscribeToTopic("reebot");
            String pushToken = FirebaseInstanceId.getInstance().getToken();

            reeBotApi.reqBookingAdd(epgItem, getEmail(), getCatvRemoveNumber(), pushToken, getAcesstoken());
            Toast.makeText(MainActivity.this, "예약\n[" + epgItem.getChnumber() + "]" + epgItem.getProgramTitle(), Toast.LENGTH_SHORT).show();
        } else {
            new VIRCommandMaker(MainActivity.this, surl, getEmail(), "STBOX", epgItem.getChnumber(), getSid());
            //new VIRCommandMaker(context, surl, catvbrend, "CHNUMBER", epgListViewItem.getSelectedItem().getChnumber(), sid);
        }
    }
}
