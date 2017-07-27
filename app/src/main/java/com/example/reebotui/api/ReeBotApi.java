package com.example.reebotui.api;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.reebotui.epglist.EPGItem;
import com.example.reebotui.epglist.EPGListViewItem;
import com.example.reebotui.info.RBAuthData;
import com.example.reebotui.info.RBIRParamData;
import com.example.reebotui.info.SaveEviroment;
import com.example.reebotui.info.UserInfo;
import com.example.reebotui.interfaceclass.ApiManagerListener;
import com.example.reebotui.interfaceclass.BookingCallback;
import com.example.reebotui.interfaceclass.InitCallback;
import com.example.reebotui.interfaceclass.JoinCallback;
import com.example.reebotui.interfaceclass.OnEPGListCallback;
import com.example.reebotui.interfaceclass.DeviceCallback;
import com.example.reebotui.interfaceclass.SignalCallback;
import com.example.reebotui.util.AlertDialogUtil;
import com.example.reebotui.util.AppUtil;
import com.exp.rb.reebot.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by silver on 2017-06-26.
 */

public class ReeBotApi {
    private static final String TAG = "ReeBot(ReeBotApi)";
    private final String URL_CHECK_VERSION = "https://reebot.io/api/getversion";
    private final String URL_LOGIN = "https://reebot.io/auth_api/authenticate";
    private final String URL_SIGN_IN = "https://reebot.io/auth_api/signin";
    private final String URL_CHECK_AND_ADD_DEVICE = "https://reebot.io/auth_api/checkandadddevice";
    private final String URL_CHECK_TOKEN = "https://reebot.io/auth_api/checktoken";
    private final String URL_CHECK_KAKAO_TOKEN = "https://reebot.io/auth_api/checkktoken";
    private final String URL_REQ_SIGNAL_LIST = "https://reebot.io/api/req_getlistsignal";
    private final String URL_REQ_IRSIGNAL = "https://reebot.io/api/req_getirsignal";
    private final String URL_REQ_CHANGE_SERVICE = "https://reebot.io/api/changeservice";
    private final String URL_REQ_EPG_ALL = "https://reebot.io/api/req_epg2json";
    private final String URL_REQ_EPG_CHANNEL = "https://reebot.io/api/req_nextepg2json";
    private final String URL_REQ_EPG_BOOKMARK = "https://reebot.io/api/req_fepg2json";
    private final String URL_REQ_EPG_SEARCH = "https://reebot.io/api/searchprogram";
    private final String URL_REQ_BOOKING_LIST = "https://reebot.io/api/getbookinglist";
    private final String URL_REQ_BOOKING_ADD = "https://reebot.io/api/addbooking";
    private final String URL_REQ_BOOKING_REMOVE = "https://reebot.io/api/removebooking";
    private final String URL_REQ_RESET_PWD = "https://reebot.io/auth_api/pwchange";
    private final String URL_REQ_REG_CHECK_DEVICE = "https://reebot.io/auth_api/checkandadddevice";

    private  final String URL_REQ_SSID_LIST ="http://192.168.4.1/ssidlist";
    private  final String URL_SET_INFO_DEVICE ="http://192.168.4.1/setting";


    private Context context;
    //    private ApiManager apiManager;
//    private EventCallback eventCallback;
//    private String signalFileName = "";
    public ReeBotApi(Context context) {
        this.context = context;
//        apiManager = new ApiManager(context);
//        if (context instanceof EventCallback) {
//            eventCallback = (EventCallback) context;
//        }

    }

//    public void setEventCallback(EventCallback eventCallback) {
//        this.eventCallback = eventCallback;
//    }

    public void checkVersion(final Activity activity, final InitCallback initCallback) {
        Log.d(TAG, "checkVersion");

        ApiManager apiManager = new ApiManager(context);
        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
                resCheckVersion(activity, respose, initCallback);
            }
        });

        apiManager.setDialogMsg("버전 검사 중입니다..");
        apiManager.execute(URL_CHECK_VERSION, "", "");

    }

    public void checkToken(String token, final InitCallback initCallback) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("인증 요청 중입니다..");

        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
//                eventCallback.onEvent(InitActivity.EVENT_REQ_CHECK_TOKEN_SUCCESS, respose);
                resCheckToken(respose, initCallback, false);
            }
        });

        apiManager.execute(URL_CHECK_TOKEN, "", token);
    }

    public void checkTokenKakao(String email, String token, final InitCallback initCallback) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("인증 요청 중입니다..");

        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
//                eventCallback.onEvent(InitActivity.EVENT_REQ_CHECK_TOKEN_SUCCESS, respose);
                resCheckToken(respose, initCallback, true);
            }
        });

        String param = "email=" + email;
        apiManager.execute(URL_CHECK_KAKAO_TOKEN, param, token);
    }

    public void reqEmailLogin(final String email, String pwd, final InitCallback initCallback) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("인증 요청 중입니다..");

        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
//                eventCallback.onEvent(InitActivity.EVENT_REQ_EMAIL_LOGIN_SUCCESS, respose);
                resEmailLogin(email, respose, initCallback);
            }
        });

        apiManager.execute(URL_LOGIN, makeAuthParam(email, pwd), "");

    }

    public void reqEmailJoin(final RBAuthData rbAuthData, final JoinCallback joinCallback) {

//        final int mode = rbAuthData.getMode();
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("회원 가입 중입니다..");

        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
//                eventCallback.onEvent(JoinActivity.EVENT_REQ_EMAIL_JOIN_SUCCESS, respose);
                resJoin(rbAuthData, respose, joinCallback);
            }
        });

        apiManager.execute(URL_SIGN_IN, makeJoinParam(false, rbAuthData.getEmail(), rbAuthData.getPassword(), rbAuthData.getLocation(), rbAuthData.getTvbrend(), rbAuthData.getCatvvendor(), rbAuthData.getPushtoken()), rbAuthData.getToken());
    }

    public void reqKakaoJoin(final RBAuthData rbAuthData, final JoinCallback joinCallback) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("회원 가입 중입니다..");
        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
                resJoin(rbAuthData, respose, joinCallback);
            }
        });

        apiManager.execute(URL_SIGN_IN, makeJoinParam(true, rbAuthData.getEmail(), rbAuthData.getPassword(), rbAuthData.getLocation(), rbAuthData.getTvbrend(), rbAuthData.getCatvvendor(), rbAuthData.getPushtoken()), rbAuthData.getAtoken());

    }


    public void reqResetPwd(String email, final InitCallback initCallback) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("비밀번호 재설정 메일 요청중.");

        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
//                resJoin(rbAuthData, respose, joinCallback);
                try {
                    JSONObject json = new JSONObject(respose);
                    if (json.getBoolean("type")) {
                        initCallback.resetPwdResult(true, "정상적으로 이메일을 발송하였습니다. 확인 바랍니다.");
//                        Toast.makeText(initActivity, "정상적으로 이메일을 발송하였습니다. 확인 바랍니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        if ("simple login user".equals(json.getString("data"))) {
                            initCallback.resetPwdResult(false, "카카오 로그인을 이용해 주세요.");
//                            Toast.makeText(initActivity, "카카오 로그인을 이용해 주세요.", Toast.LENGTH_SHORT).show();
                        } else if ("Not found user".equals(json.getString("data"))) {
                            initCallback.resetPwdResult(false, "등록 되어 있지 않은 이메일 입니다. 회원가입 후 이용바랍니다.");
//                            Toast.makeText(initActivity, "등록 되어 있지 않은 이메일 입니다. 회원가입 후 이용바랍니다. ", Toast.LENGTH_SHORT).show();
                        } else {
                            initCallback.resetPwdResult(false, "이메일을 전송에 실패 하였습니다. 다시 시도 바랍니다.");
//                            Toast.makeText(initActivity, "이메일을 전송에 실패 하였습니다. 다시 시도 바랍니다.", Toast.LENGTH_SHORT).show();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    initCallback.resetPwdResult(false, "reset error: " + e.toString());
                }
            }
        });

        String param = "email=" + email;
        apiManager.execute(URL_REQ_RESET_PWD, param, "");

    }

    public void reqSignalList(RBIRParamData rbirParamData, final SignalCallback signalCallback) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("리모컨 목록을 가져오는 중입니다.");
        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String response) {
                resSignalList(response, signalCallback);
            }
        });
        apiManager.execute(URL_REQ_SIGNAL_LIST, makeSignalParam(rbirParamData.param1, "", ""), null);
    }


    public void reqIRSignal(RBIRParamData rbirParamData, final SignalCallback signalCallback) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("리모컨 신호 다운로드 중입니다.");
        final String signalFileName = makefilename(rbirParamData.id, rbirParamData.location, rbirParamData.param1);
        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String response) {
                resIRSignal(response, signalFileName, signalCallback);
            }
        });
        apiManager.execute(URL_REQ_IRSIGNAL, makeSignalParam(rbirParamData.param1, rbirParamData.param2, rbirParamData.param3), null);
    }


    public void reqChangeService(String id, String tvb, String catv, String token, final SignalCallback signalCallback) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("변경 중입니다.");
        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String response) {
//                eventCallback.onEvent(SettingActivity.EVENT_REQ_CHANGE_SERVICE, response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getBoolean("type")) {
                        signalCallback.changeService(true, "");
                    } else {
                        //변경 실패
                        signalCallback.changeService(false, "change service fail");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    signalCallback.changeService(false, "change service error: " + e.toString());
                }

            }
        });

        String param = "id=" + id + "&tv=" + tvb + "&catv=" + catv;

        apiManager.execute(URL_REQ_CHANGE_SERVICE, param, token);
    }

    public void reqAllEpgList(final String catvb, final OnEPGListCallback onEPGListCallback) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("편성표 가져오기 중입니다.");
        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String response) {

                onEPGListCallback.requestAllEPGList(getEPGListViewList(context, response, catvb));
            }
        });

        System.out.println("reqAllEpgList catvb: " + catvb);

        apiManager.execute(URL_REQ_EPG_ALL, makeEpgParameters(catvb, "", "", ""), null);

    }

    public void reqBookmarkList(final String catvb, String bookmarklist, final OnEPGListCallback onEPGListCallback) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("편성표 가져오기 중입니다.");
        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String response) {
                onEPGListCallback.requestBookmarkList(getEPGListViewList(context, response, catvb));
            }
        });

        System.out.println("reqBookmarkList catvb: " + catvb);
        System.out.println("reqBookmarkList bookmarklist: " + bookmarklist);
        apiManager.execute(URL_REQ_EPG_BOOKMARK, makeEpgParameters(catvb, "", bookmarklist, ""), null);

    }

    public void reqEpgProgramList(final String catvb, String chnum, final OnEPGListCallback onEPGListCallback) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("편성표 가져오기 중입니다.");
        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String response) {

                onEPGListCallback.requestEPGProgramList(getProgramList(response, catvb));
            }
        });

        System.out.println("reqEpgProgramList catvb: " + catvb);
        System.out.println("reqEpgProgramList chnum: " + chnum);
        apiManager.execute(URL_REQ_EPG_CHANNEL, makeEpgParameters(catvb, chnum, "", ""), null);

    }

    public void reqSearchEpgList(final String catvb, String searchstring, final OnEPGListCallback onEPGListCallback) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("편성표 가져오기 중입니다.");
        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String response) {
                onEPGListCallback.requestSearchEPGList(getEPGListViewList(context, response, catvb));
            }
        });

        System.out.println("reqSearchEpgList catvb: " + catvb);
        System.out.println("reqSearchEpgList searchstring: " + searchstring);
        apiManager.execute(URL_REQ_EPG_SEARCH, makeEpgParameters(catvb, "", "", searchstring), null);
    }

    public void reqBookingList(String email, String token, final BookingCallback bookingCallback) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("예약 정보 가져오는 중");
        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
//                eventCallback.onEvent(ExtraInfo.REQ_MODE_BOOKING_LIST, respose);
                bookingCallback.requestBookingList(getBookinglist(respose));
            }
        });

        String param = "email=" + email;
        apiManager.execute(URL_REQ_BOOKING_LIST, param, token);
    }

    public void reqBookingAdd(final EPGItem epgItem, String email, String catv, String pushtoken, String token, final BookingCallback bookingCallback) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("예약 정보 등록 중");
        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
//                eventCallback.onEvent(ExtraInfo.REQ_MODE_BOOKING_ADD, respose);
                bookingCallback.addBookingChannel(epgItem, true);
            }
        });
//        String param = makeParameters(email,catv, chnum, pushtoken, ptitle, chname, category, bookingtime, programendtime);
        apiManager.execute(URL_REQ_BOOKING_ADD, makeBookingAddParameters(epgItem, email, catv, pushtoken), token);
    }

    public void reqBookingRemove(final EPGListViewItem epgListViewItem, String token, final BookingCallback bookingCallback) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("예약 정보 삭제 중");
        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
                bookingCallback.removeBookingChannel(epgListViewItem, true);
            }
        });
        String param = "id=" + epgListViewItem.getBookingId();
        apiManager.execute(URL_REQ_BOOKING_REMOVE, param, token);
    }
    public void reqSSIDList(final ArrayAdapter<String> adapterfinal,final DeviceCallback deviceCallback)
    {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("장치 검색중");
        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
                deviceCallback.comSSIDList(makSSIDdList(adapterfinal,respose));
            }
        });
        apiManager.execute(URL_REQ_SSID_LIST,"", "");
    }
    public void setInfoToDev(String ssid,String pass,String pemail,final DeviceCallback deviceCallback)
    {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("Reebot Network 설정중");
        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
                deviceCallback.resSaveInfoToDevice(getReebotToken(respose));
            }
        });
        String params = "?ssid=" + ssid + "&pass=" + pass + "&tid=" + pemail;
        apiManager.execute(URL_SET_INFO_DEVICE+params,"","");
    }

    public void chkAddDevice(String email,String rbtoken,String token,String location,String catv,String tv,final DeviceCallback deviceCallback)
    {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("Reebot Network 설정중");
        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
                deviceCallback.resAddDevice(resChkAddDevice(respose));
            }
        });
        // "email=" + email + "&password=" + password + "&location=" + location + "&tvbrend=" + tvbrend + "&catvvendor=" + catvvendor + "&pushtoken=" + pushtoken + "&devtoken=" + devtoken;
        String params = "email=" + email+ "&location=" + location + "&tvbrend=" + tv + "&catvvendor=" + catv + "&devtoken=" + rbtoken;
        apiManager.execute(URL_REQ_REG_CHECK_DEVICE,params,token);
    }

    private void resCheckVersion(final Activity activity, String respose, final InitCallback initCallback) {

        //업데이트 체크
        String version = "";
        try {
            PackageInfo i = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            version = i.versionName;
            Log.d(TAG, "current version :" + version);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "error get version");
        }
        final String finalVersion = version;


        Log.d(TAG, "get server version respose: " + respose);
        try {
            JSONObject json = new JSONObject(respose);
            if (json.getBoolean("type")) {
                JSONArray jArray = json.getJSONArray("data");
                double sversion = 0;
                double cversion = Double.parseDouble(finalVersion);
                boolean forceupdate = false;
                if (jArray.length() == 0) {

                } else {
                    for (int i = 0; i < jArray.length(); i++) {
                        sversion = jArray.getJSONObject(i).getDouble("version");
                        forceupdate = jArray.getJSONObject(i).getBoolean("forceupdate");
                    }
                }
                Log.d(TAG, "checkVersion cversion: " + cversion);
                Log.d(TAG, "checkVersion sversion: " + sversion);

                if (cversion < sversion && forceupdate == true) {
                    //강업
                    Log.d(TAG, "You must Need Update");
                    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                        }
                    };
                    AlertDialogUtil.showDialog(activity, "", "중요 업데이트가 있습니다.\n업데이트가 반드시 필요합니다.", "업데이트", onClickListener);

                } else if (cversion < sversion && forceupdate == false) {
                    //권고
                    Log.d(TAG, "May you Need Update");

                    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    activity.finish();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
//                                    if (eventCallback != null) {
//                                        eventCallback.onEvent(InitActivity.EVENT_REQ_CHECK_VERSION_SUCCESS, "");
//                                    }
                                    initCallback.checkVersionResult(true, "");
                                    break;
                            }
                        }
                    };
                    AlertDialogUtil.showDialog(activity, "", "업데이트가 있습니다.\n기능 확장을 위해서 업데이트가 필요합니다.", "건너 뛰기", "업데이트", onClickListener);
                } else {
//                    if (eventCallback != null) {
//                        eventCallback.onEvent(InitActivity.EVENT_REQ_CHECK_VERSION_SUCCESS, "");
//                    }
                    initCallback.checkVersionResult(true, "");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            initCallback.checkVersionResult(false, e.toString());
        }
    }

    private void resCheckToken(String msg, InitCallback initCallback, boolean isKakao) {

        boolean type = false;
        String location = "";
        String tvbrend = "";
        String catvvendor = "";
        String rbtoken = "";
        String token = "";
        String email = "";
        String accesstoken = "";
        String _id = "";

        JSONObject jObject = null;
        JSONArray jArray = null;
        if (msg.equals("error")) {
            initCallback.checkTokenResult(false, msg, null, isKakao);
            return;
        } else {
            try {
                jObject = new JSONObject(msg);
                type = jObject.getBoolean("type");
                Log.d(TAG, "resCheckToken type: " + type);
                if (type) {
                    jArray = jObject.getJSONArray("regdevice");
                    email = jObject.getString("email");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        _id = oneObject.getString("_id");
                        location = oneObject.getString("location");
                        tvbrend = oneObject.getString("tvbrend");
                        catvvendor = oneObject.getString("catvvendor");
                        rbtoken = oneObject.getString("rbtoken");
                    }
                    if (!"nomal".equals(jObject.getString("type2"))) {
                        accesstoken = jObject.getString("token");
                    }
                    Log.d(TAG, "resCheckToken : " + msg);
                    Log.d(TAG, "Auth ok");

                    UserInfo userInfo = new UserInfo(_id, email, tvbrend, catvvendor, token, accesstoken, rbtoken, location);
                    initCallback.checkTokenResult(true, msg, userInfo, isKakao);
                    return;
                } else {
                    //토큰검증 실패
                    String data = jObject.getString("data");
                    initCallback.checkTokenResult(false, data, null, isKakao);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                initCallback.checkTokenResult(false, e.toString(), null, isKakao);
                return;
            }
        }

        initCallback.checkTokenResult(false, "CheckToken Error", null, isKakao);
    }

    private void resEmailLogin(String email, String msg, InitCallback initCallback) {
        boolean type = false;
        String location = "";
        String tvbrend = "";
        String catvvendor = "";
        String rbtoken = "";
        String token = "";
        JSONObject jObject = null;
        JSONArray jArray = null;
        try {
            jObject = new JSONObject(msg);
            type = jObject.getBoolean("type");
            if (type) {
                if ("nomallogin".equals(jObject.getString("type2"))) //일반 ID / PW 로그인
                {
                    //JSONObject data = jObject.getJSONObject("data");
                    jArray = jObject.getJSONArray("Regdevice");
                    token = jObject.getString("token");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        location = oneObject.getString("location");
                        tvbrend = oneObject.getString("tvbrend");
                        catvvendor = oneObject.getString("catvvendor");
                        rbtoken = oneObject.getString("rbtoken");
                    }
                    Log.d(TAG, "rbtoken : " + rbtoken);
                    Log.d(TAG, "resEmailLogin : " + msg);
                    Log.d(TAG, "Auth ok");
                    UserInfo userInfo = new UserInfo(email, tvbrend, catvvendor, token, "", rbtoken, location);
                    initCallback.emailLoginResult(true, "", userInfo);

                } else if ("signin_nomal".equals(jObject.getString("type2"))) {
                    JSONObject data = jObject.getJSONObject("data");
                    jArray = data.getJSONArray("Regdevice");
                    token = jObject.getString("token");
                } else if ("signin_kakao".equals(jObject.getString("type2"))) {
                    JSONObject data = jObject.getJSONObject("data");
                    jArray = data.getJSONArray("Regdevice");

                } else {
                    JSONObject data = jObject.getJSONObject("data");
                    jArray = data.getJSONArray("Regdevice");

                }
                //로그인 유지 이면 저장
                SaveEviroment.setUserName(context, email, token);
                // Log.d(TAG, "RBAuthLoginToken: "+token);
                //수정 필요 메이크 파일
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    location = oneObject.getString("location");
                    tvbrend = oneObject.getString("tvbrend");
                    catvvendor = oneObject.getString("catvvendor");
                    rbtoken = oneObject.getString("rbtoken");
                }
                Log.d(TAG, "rbtoken : " + rbtoken);
                Log.d(TAG, "resEmailLogin : " + msg);
                Log.d(TAG, "Auth ok");
            } else {
                initCallback.emailLoginResult(false, "", null);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void resJoin(RBAuthData rbAuthData, String msg, JoinCallback joinCallback) {
        Log.d(TAG, "resEmailJoin : " + msg);
        boolean type = false;
        String token = "";
        String data = "";
        JSONObject jObject = null;
        JSONArray jArray = null;
        try {
            jObject = new JSONObject(msg);
            type = jObject.getBoolean("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (type) {
            try {
                token = jObject.getString("token");
                data = jObject.getString("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            final String accesstoken = token;
            //로그인 정보 저장
            SaveEviroment.setUserName(context, rbAuthData.getEmail(), token);
            SaveEviroment.setUserEnv(context, rbAuthData.getTvbrend(), rbAuthData.getCatvvendor(), "");
            //회원가입 완료
            joinCallback.joinResult(true, "", rbAuthData);
        } else {
//            AlertDialogUtil.showDialog(context, "이미 가입된 회원 정보입니다.");
            joinCallback.joinResult(false, "", rbAuthData);
        }
    }


    private void resSignalList(String msg, SignalCallback signalCallback) {
        Log.d(TAG, "resSignalList : " + msg);
        try {
            JSONObject jsonobject = new JSONObject(msg);
            if (jsonobject.getBoolean("type") == true) {
                Log.d(TAG, "resSignalList type2: " + jsonobject.getString("type2"));

                signalCallback.getSignalList(true, jsonobject.getString("type2"), makelist(jsonobject.getJSONArray("data")));


            } else {
                //리스트 가져오기 실패
                signalCallback.getSignalList(false, "리스트를 가져오는데 실패했습니다. 다시 시도해주세요.", null);
//                AlertDialogUtil.showDialog(this, "리스트를 가져오는데 실패했습니다. 다시 시도해주세요.");
            }
        } catch (Exception e) {
            //통신 상태를 확인해주세요.
            e.printStackTrace();
            signalCallback.getSignalList(false, "인터넷 연결 상태를 확인해주세요.", null);
//            AlertDialogUtil.showDialog(this, "인터넷 연결 상태를 확인해주세요.");
        }
    }




    private void resIRSignal(String response, String signalFileName, SignalCallback signalCallback) {
        if (!AppUtil.isEmpty(response)) {
            String extr = context.getFilesDir().getPath().toString();
            File mFolder = new File(extr + "/irsignal");
            if (!mFolder.exists()) {
                Log.d(TAG, "Make Folder");
                mFolder.mkdir();
            } else {
                Log.d(TAG, "Folder");
            }
            Log.d(TAG, "filenmae : " + signalFileName);
            File file = new File(mFolder.getAbsolutePath(), signalFileName);
            try {
                Log.d(TAG, "JSON : " + response);
                file.createNewFile();
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(response);
                myOutWriter.close();
                fOut.flush();
                fOut.close();
//                            eventCallback.onEvent(mode, response);
                signalCallback.getIRSignal(true, signalFileName);
            } catch (IOException e) {
                e.printStackTrace();
                signalCallback.getIRSignal(false, "File write failed: " + e.toString());
//                response = "error";
//                Log.e(TAG + " Exception", "File write failed: " + e.toString());
//                            eventCallback.onEvent(mode, response);
            }
        } else {
            Log.d(TAG, "response is null");
            signalCallback.getIRSignal(false, "response is null");
//                        eventCallback.onEvent(mode, response);
        }
    }


    private ArrayList<String> makelist(JSONArray jArray) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            if (jArray.length() == 0) {
                list.add("장치없음");
            } else {
                //adapter.clear();
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    String SIGNALINFO = oneObject.getString("INFO");
                    list.add(SIGNALINFO);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }


    private ArrayList<EPGListViewItem> getEPGListViewList(Context context, String epgjson, String catv) {
//        Log.d(TAG, "Response JSON :" + epgjson);
        ArrayList<EPGListViewItem> list = new ArrayList<EPGListViewItem>();

        if (AppUtil.isEmpty(epgjson)) {
            return list;
        }

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

    private ArrayList<EPGItem> getProgramList(String epgjson, String catvb) {
//        boolean ret = false;
        Log.d(TAG, "Response JSON :" + epgjson);

        ArrayList<EPGItem> list = new ArrayList<EPGItem>();

        if (AppUtil.isEmpty(epgjson)) {
            return list;
        }

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
                    JSONObject isp = isplist.getJSONObject(catvb);
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
                    list.add(new EPGItem(title, entryInfo, chnum, category, category, starttime, endtime, chname));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    private ArrayList<EPGListViewItem> getBookinglist(String msg) {
        Log.d(TAG, "readBookinglist msg: " + msg);
        ArrayList<EPGListViewItem> epgListViewList = new ArrayList<EPGListViewItem>();

//        boolean ret = false;
//        if (msg == null) {
//            return ret;
//        }
        if (AppUtil.isEmpty(msg)) {
            return epgListViewList;
        }

        try {
            JSONObject jObject = new JSONObject(msg);
            if (jObject.getBoolean("type")) {
                JSONArray jArray = jObject.getJSONArray("data");
                if (jArray.length() == 0) {

                } else {
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        String starttime = oneObject.getString("bookingtime");
                        String endtime = oneObject.getString("programendtime");



                        String id = oneObject.getString("_id");
                        String title = oneObject.getString("ptitle");
                        String category = oneObject.getString("category");
                        String chnum = oneObject.getString("chnum");
                        String chname = oneObject.getString("chname");
//                        String chlogoname = oneObject.getString("imglogoname");
                        String chlogoname = "";
                        String entryInfo = category + " / " + chnum;

                        String chlongo = chname.toString();
                        chlongo = chlongo.replace(" ", "");



                        EPGItem item = new EPGItem(title, entryInfo, chnum, category, category, starttime, endtime, chname);
                        EPGListViewItem epgListViewItem = new EPGListViewItem();
                        epgListViewItem.setBookingId(id);
                        epgListViewItem.setChlogoiconDrawable(AppUtil.getImage(context, chlongo, chlongo));
                        epgListViewItem.setBsstatusiconDrawable(ContextCompat.getDrawable(context, R.drawable.icon_fav_normal));
                        epgListViewItem.getList().add(item);
                        epgListViewList.add(epgListViewItem);

                    }
                }
//                handler.sendEmptyMessage(1);

            } else {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


//        epgListViewAdapter.addItem(epgListViewList);

        return epgListViewList;
    }


    private String makeAuthParam(String email, String password) {
        return "email=" + email + "&password=" + password;
    }

    private String makeAuthParam(String email, String password, String location, String tvbrend, String catvvendor, String pushtoken, String devtoken) {
        return "email=" + email + "&password=" + password + "&location=" + location + "&tvbrend=" + tvbrend + "&catvvendor=" + catvvendor + "&pushtoken=" + pushtoken + "&devtoken=" + devtoken;
    }

    private String makeJoinParam(boolean isKakao, String email, String password, String location, String tvbrend, String catvvendor, String pushtoken) {
        if (isKakao) {
            return "email=" + email + "&password=" + password + "&location=" + location + "&tvbrend=" + tvbrend + "&catvvendor=" + catvvendor + "&pushtoken=" + pushtoken + "&authtype=kakao";
        } else {
            return "email=" + email + "&password=" + password + "&location=" + location + "&tvbrend=" + tvbrend + "&catvvendor=" + catvvendor + "&pushtoken=" + pushtoken;
        }

    }

    private String makeSignalParam(String dev, String brend1, String brend2) {
        String ret = "";

        if (dev != null && !dev.isEmpty()) {
            ret += "dev=" + dev;
            if (brend1 != null && !brend1.isEmpty()) {
                ret += "&brend1=" + brend1;
                if (brend2 != null && !brend2.isEmpty()) {
                    ret += "&brend2=" + brend2;
                }
            }
        }
        return ret;
    }

    private String makeEpgParameters(String catvb, String chnum, String fchnum, String search) {
        String ret = null;
        Date date = new Date();
        DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        //dateFormatter.format(date).toString();
        //String []catv = catvb.split("_");
        //수정 필요 (통합)
        //ret = "catvb=epgfor"+catv[1]+"&reqtime="+dateFormatter.format(date).toString();
        ret = "catvb=" + catvb + "&reqtime="+ dateFormatter.format(date).toString();

        if (chnum != null && !chnum.isEmpty()) {
            ret += "&chnum=" + chnum;
        }

        if (fchnum != null && !fchnum.isEmpty()) {
            ret += "&fchnum=" + fchnum;
        }

        if (search != null && !search.isEmpty()) {
            ret += "&search=" + search;
        }

        Log.d(TAG, "queryEPGListJSON :" + ret);
        return ret;
    }

    private  boolean makSSIDdList( ArrayAdapter<String> adapter,String data)
    {
        boolean ret = true;
        if (data.contains("error")) {
            //error : Check
            if (data.contains("timeout")) {
                adapter.add("Reebot-xxxxxx로 WIFI를 연결 필요.");
                adapter.add("Reebot-xxxxxx로 WIFI를 연결 필요.");
            } else {
                adapter.add("Reebot-xxxxxx로 WIFI를 연결 필요.");
                adapter.add("Reebot-xxxxxx로 WIFI를 연결 필요.");
            }
            ret = false;
        }
        else {
            try {
                JSONObject jObject = new JSONObject(data);
                JSONArray jArray = jObject.getJSONArray("SSID");
                if (jArray.length() == 0) {
                    adapter.add("Reebot-xxxxxx로 WIFI를 연결 필요.");
                    adapter.add("Reebot-xxxxxx로 WIFI를 연결 필요.");
                } else {
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        Log.e(TAG, "aJsonString Key : " + oneObject.names().getString(0));
                        adapter.add(oneObject.names().getString(0));
                    }
                    adapter.add("HOME WIFI SSID 선택");
                }
            } catch (JSONException e) {
                ret = false;
                e.printStackTrace();
            }
        }
        return ret;
    }

    private String getReebotToken(String data) {
        String rbtoken ="";
        if (AppUtil.isEmpty(data)) {
            //rbtoken ="";
        }
        else {
            try {
                JSONObject jObject = new JSONObject(data);
                rbtoken = jObject.getString("rbtoken");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  rbtoken;
    }
    private boolean resChkAddDevice(String data)
    {
        boolean ret=true;
        if (AppUtil.isEmpty(data)) {
            ret = false;
        }
        else {
            try {
                JSONObject jObject = new JSONObject(data);
                ret = jObject.getBoolean("type");
            } catch (JSONException e) {
                ret = false;
                e.printStackTrace();
            }
        }

        return ret;
    }

    private String makeBookingAddParameters(EPGItem epgItem, String email, String catv, String pushtoken) {//String email, String catv, String chnum, String pushtoken, String ptitle, String chname, String category, String bookingtime, String programendtime) {
        String ret = null;
        //Date date = new Date();
        //DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        //dateFormatter.format(date).toString();
        ret = "email=" + email + "&catv=" + catv + "&chnum=" + epgItem.getChnumber() + "&pushtoken=" + pushtoken + "&ptitle=" + epgItem.getProgramTitle() + "&chname=" + epgItem.getChname() + "&category=" + epgItem.getCategory_kr() + "&bookingtime=" + epgItem.getStarttime() + "&programendtime=" + epgItem.getEndtime();
        Log.d(TAG, "queryEPGListJSON :" + ret);
        return ret;
    }

    private String makefilename(String id, String location, String devicename) {
        String filename = id + "_" + location + "_" + devicename + ".json";
        return filename;
    }



}
