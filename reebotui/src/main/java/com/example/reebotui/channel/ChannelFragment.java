package com.example.reebotui.channel;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.example.reebotui.MainActivity;
import com.example.reebotui.api.ReeBotApi;
import com.example.reebotui.epglist.EPGItem;
import com.example.reebotui.epglist.EPGListViewAdapter;
import com.example.reebotui.epglist.EPGListViewItem;
import com.example.reebotui.info.BookmarkInfo;
import com.example.reebotui.info.EPGParamData;
import com.example.reebotui.info.ExtraInfo;
import com.example.reebotui.interfaceclass.EventCallback;
import com.example.reebotui.interfaceclass.OnEPGItemClickListener;
import com.example.reebotui.interfaceclass.OnEPGListClickListener;
import com.example.reebotui.ir.VIRCommandMaker;
import com.example.reebotui.util.AppUtil;
import com.exp.rb.reebot.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnEPGListClickListener}
 * interface.
 */
public class ChannelFragment extends Fragment implements EventCallback {

    private static final String TAG = "ReeBot(ChannelFragment)";

    // TODO: Customize parameter argument names
//    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_LIST_TYPE = "listType";
    public static final int ARG_LIST_TYPE_BOOKMARK = 0;
    public static final int ARG_LIST_TYPE_ALL = 1;
    public static final int ARG_LIST_TYPE_SEARCH = 2;

    // TODO: Customize parameters
//    private int mColumnCount = 1;
    private int listType = 0;
    private OnEPGListClickListener onEPGListClickListener;

    private EPGListViewAdapter epgListViewAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ReeBotApi reeBotApi;
    private EPGListViewItem channelEpgListViewItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChannelFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ChannelFragment newInstance(int listType) {
        Log.d(TAG, "ChannelFragment newInstance: " + listType);
        ChannelFragment fragment = new ChannelFragment();
        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_LIST_TYPE, listType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "ChannelFragment onAttach");
//        if (context instanceof OnEPGListClickListener) {
//            onEPGListClickListener = (OnEPGListClickListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnEPGListClickListener");
//        }

        onEPGListClickListener = new OnEPGListClickListener() {
            @Override
            public void OnEPGListClickListener(EPGListViewItem epgListViewItem) {
                System.out.println("reebot ChannelFragment OnEPGListClickListener " + epgListViewItem.getIndex());
                clickEpgItem(epgListViewItem.getSelectedItem());
            }

            @Override
            public void OnEPGListClickListener(EPGListViewItem epgListViewItem, int id) {
                Log.d(TAG, "OnEPGListClickListener id: " + id);
                switch (id) {
                    case R.id.iv_bookmark:
                        ((MainActivity) getActivity()).notifyDataSetChangedList();
                        break;

                    case R.id.iv_channel_list:
//                        ProgramListVerticalDialog programListDialog = new ProgramListVerticalDialog(getActivity(), epgListViewItem, onEPGItemClickListener);
//                        programListDialog.show();
                        getChannelEpg(epgListViewItem);
                        break;
                }
            }
        };
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "ChannelFragment onDetach");
        onEPGListClickListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ChannelFragment onCreate");

        if (getArguments() != null) {
            listType = getArguments().getInt(ARG_LIST_TYPE);
        }

        if (!checkAccessibilityPermissions()) {
            //setAccessibilityPermissions();
            Toast.makeText(getActivity(), "Not Enough Accessibility Permissions ", Toast.LENGTH_LONG).show();
        }

        reeBotApi = new ReeBotApi(getActivity());
        reeBotApi.setEventCallback(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "ChannelFragment onCreateView");
        View view = inflater.inflate(R.layout.fragment_channel_list, container, false);

        Context context = view.getContext();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_programlist);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        epgListViewAdapter = new EPGListViewAdapter(getActivity(), onEPGListClickListener);
        recyclerView.setAdapter(epgListViewAdapter);


        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("reebot onRefresh ");
//                getEpgList();
                refreshEpgList();
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);

            }
        });

//        if (listType == ARG_LIST_TYPE_ALL) {
//            mSwipeRefreshLayout.setEnabled(true);
//        } else {
//            mSwipeRefreshLayout.setEnabled(false);
//        }

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "ChannelFragment onActivityCreated");
        refreshEpgList();

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "ChannelFragment onStart");


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "ChannelFragment onResume");


    }


    @Override
    public void onEvent(int event, String msg) {
        Log.d(TAG, "onEvent event: " + event);
        Log.d(TAG, "onEvent msg: " + msg);

        switch (event) {
            case ExtraInfo.REQ_MODE_EPGLIST_ALL: {
                ArrayList<EPGListViewItem> list = getEPGListViewList(getActivity(), msg, ((MainActivity) getActivity()).getCatvRemoveNumber());
                epgListViewAdapter.addItem(list);
                mSwipeRefreshLayout.setRefreshing(false);
            }
            break;
            case ExtraInfo.REQ_MODE_EPGLIST_CHANNEL: {
                channelEpgListViewItem.clearChannelList();
                getProgramList(msg, channelEpgListViewItem);
                ProgramListVerticalDialog programListDialog = new ProgramListVerticalDialog(getActivity(), channelEpgListViewItem, onEPGItemClickListener);
                programListDialog.show();
            }
            break;
            case ExtraInfo.REQ_MODE_EPGLIST_BOOKMARK: {
                ArrayList<EPGListViewItem> list = getEPGListViewList(getActivity(), msg, ((MainActivity) getActivity()).getCatvRemoveNumber());
                epgListViewAdapter.addItem(list);
                mSwipeRefreshLayout.setRefreshing(false);
            }
            break;
            case ExtraInfo.REQ_MODE_EPGLIST_SEARCH: {
                ArrayList<EPGListViewItem> list = getEPGListViewList(getActivity(), msg, ((MainActivity) getActivity()).getCatvRemoveNumber());
                ProgramListVerticalDialog programListDialog = new ProgramListVerticalDialog(getActivity(), list, onEPGListClickListener);
                programListDialog.show();
            }
            break;
            case ExtraInfo.REQ_MODE_BOOKING_ADD: {
//                reeBotApi.reqBookingList(((MainActivity) getActivity()).getEmail(), ((MainActivity) getActivity()).getAcesstoken());
            }
            break;
        }


    }

    OnEPGItemClickListener onEPGItemClickListener = new OnEPGItemClickListener() {
        @Override
        public void onEPGItemClickListener(EPGItem epgItem) {
            System.out.println("reebot onEPGItemClickListener " + epgItem.getProgramTitle());
            clickEpgItem(epgItem);
        }
    };

    public void refreshEpgList() {
        if (listType == ARG_LIST_TYPE_ALL) {
            getEpgList();
        } else if (listType == ARG_LIST_TYPE_BOOKMARK) {
            getBookmarkEpgList();
        }
    }

    public void refreshListAdapter() {
//        if (listType == ARG_LIST_TYPE_ALL) {
//            if (epgListViewAdapter != null) {
//                epgListViewAdapter.notifyDataSetChanged();
//            } else {
//                getEpgList();
//            }
//        } else

        Log.d(TAG, "refreshListAdapter listType: " + listType);
        if (listType == ARG_LIST_TYPE_BOOKMARK) {
            getBookmarkEpgList();
        } else if (listType == ARG_LIST_TYPE_ALL) {
            Log.d(TAG, "refreshListAdapter epgListViewAdapter: " + epgListViewAdapter);
            if (epgListViewAdapter != null) {
                epgListViewAdapter.refreshBookmark();
            }
        }

    }

    private void clickEpgItem(EPGItem epgItem) {

        final String surl = "http://reebot.io:8085";

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

            reeBotApi.reqBookingAdd(epgItem, ((MainActivity) getActivity()).getEmail(), ((MainActivity) getActivity()).getCatvRemoveNumber(), pushToken, ((MainActivity) getActivity()).getAcesstoken());
            Toast.makeText(getActivity(), "예약\n[" + epgItem.getChnumber() + "]" + epgItem.getProgramTitle(), Toast.LENGTH_SHORT).show();
        } else {
            new VIRCommandMaker(getActivity(), surl, ((MainActivity) getActivity()).getEmail(), "STBOX", epgItem.getChnumber(), ((MainActivity) getActivity()).getSid());
            //new VIRCommandMaker(context, surl, catvbrend, "CHNUMBER", epgListViewItem.getSelectedItem().getChnumber(), sid);
        }
    }

//    public void setSearchView(final SearchView searchView) {
////        final SearchView searchView = ((MainActivity) getActivity()).getSearchView();
//        if (searchView != null) {
//            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//                @Override
//                public boolean onQueryTextSubmit(String s) {
//                    System.out.println("reebot onQueryTextSubmit " + s);
//
//                    searchProgram(s);
//
//                    searchView.onActionViewCollapsed();
//                    ((MainActivity) getActivity()).setSearchViewExpanded(false);
//                    return false;
//                }
//
//                @Override
//                public boolean onQueryTextChange(String s) {
//                    System.out.println("reebot onQueryTextChange " + s);
//                    return false;
//                }
//            });
//        }
//
//    }

    private void getBookmarkEpgList() {

        Log.d(TAG, "getBookmarkEpgList getActivity(): " + getActivity());
        Log.d(TAG, "getBookmarkEpgList getBookmarkInfo(): " + ((MainActivity) getActivity()).getBookmarkInfo());

        String nbookmardList = "";
        BookmarkInfo bookmarkInfo = new BookmarkInfo(getActivity());
        ArrayList<String> bookmardList = bookmarkInfo.getBookmarkList();
//                ArrayList<String> bookmardList = ((MainActivity) getActivity()).getBookmarkInfo().getBookmarkList();
        nbookmardList = bookmardList.toString().replace("[", " ");
        nbookmardList = nbookmardList.toString().replace("]", " ");

        EPGParamData epgParamData = new EPGParamData(ExtraInfo.REQ_MODE_EPGLIST_BOOKMARK, ((MainActivity) getActivity()).getCatvRemoveNumber());
        epgParamData.setBookmarklist(nbookmardList);

        reeBotApi.reqEpgList(epgParamData);
    }

    private void getEpgList() {
        reeBotApi.reqEpgList(new EPGParamData(ExtraInfo.REQ_MODE_EPGLIST_ALL, ((MainActivity) getActivity()).getCatvRemoveNumber()));
    }

    private void getChannelEpg(EPGListViewItem epgListViewItem) {
        channelEpgListViewItem = epgListViewItem;

        EPGParamData epgParamData = new EPGParamData(ExtraInfo.REQ_MODE_EPGLIST_CHANNEL, ((MainActivity) getActivity()).getCatvRemoveNumber());
        epgParamData.setChnum(epgListViewItem.getList().get(0).getChnumber());

        reeBotApi.reqEpgList(epgParamData);
    }

//    private void searchProgram(String text) {
//        reeBotApi.reqEpgList(new EPGParamData(ExtraInfo.REQ_MODE_EPGLIST_SEARCH, ((MainActivity) getActivity()).getCatv(), "", text));
//
//    }

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

    private boolean getProgramList(String epgjson, EPGListViewItem epgListViewItem) {
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
                    JSONObject isp = isplist.getJSONObject(((MainActivity) getActivity()).getCatvRemoveNumber());
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
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }


    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public boolean checkAccessibilityPermissions() {
        AccessibilityManager accessibilityManager = (AccessibilityManager) getActivity().getSystemService(Context.ACCESSIBILITY_SERVICE);
        // getEnabledAccessibilityServiceList는 현재 접근성 권한을 가진 리스트를 가져오게 된다
        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.DEFAULT);
        for (int i = 0; i < list.size(); i++) {
            AccessibilityServiceInfo info = list.get(i);
            // 접근성 권한을 가진 앱의 패키지 네임과 패키지 네임이 같으면 현재앱이 접근성 권한을 가지고 있다고 판단함
            if (info.getResolveInfo().serviceInfo.packageName.equals(getActivity().getPackageName())) {
                return true;
            }
        }
        return false;
    }


}
