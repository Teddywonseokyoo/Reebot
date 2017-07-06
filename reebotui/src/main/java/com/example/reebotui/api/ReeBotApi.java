package com.example.reebotui.api;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.reebotui.InitActivity;
import com.example.reebotui.JoinActivity;
import com.example.reebotui.SettingActivity;
import com.example.reebotui.epglist.EPGItem;
import com.example.reebotui.info.EPGParamData;
import com.example.reebotui.info.ExtraInfo;
import com.example.reebotui.info.RBAuthData;
import com.example.reebotui.info.RBIRParamData;
import com.example.reebotui.interfaceclass.ApiManagerListener;
import com.example.reebotui.interfaceclass.EventCallback;
import com.example.reebotui.util.AlertDialogUtil;
import com.example.reebotui.util.AppUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by silver on 2017-06-26.
 */

public class ReeBotApi {
    private static final String TAG = "ReeBot(ReeBotApi)";

    private final String URL_CHECK_VERSION = "http://reebot.io:8083/api/getversion";


    private final String URL_LOGIN = "http://reebot.io:8083/auth_api/authenticate";
    private final String URL_SIGN_IN = "http://reebot.io:8083/auth_api/signin";
    private final String URL_CHECK_AND_ADD_DEVICE = "http://reebot.io:8083/auth_api/checkandadddevice";
    private final String URL_CHECK_TOKEN = "http://reebot.io:8083/auth_api/checktoken";
    private final String URL_CHECK_KAKAO_TOKEN = "http://reebot.io:8083/auth_api/checkktoken";

    private final String URL_REQ_SIGNAL_LIST = "http://reebot.io:8083/api/req_getlistsignal";
    private final String URL_REQ_IRSIGNAL = "http://reebot.io:8083/api/req_getirsignal";
    private final String URL_REQ_CHANGE_SERVICE = "http://reebot.io:8083/api/changeservice";

    private final String URL_REQ_EPG_ALL = "http://reebot.io:8083/api/req_epg2json";
    private final String URL_REQ_EPG_CHANNEL = "http://reebot.io:8083/api/req_nextepg2json";
    private final String URL_REQ_EPG_BOOKMARK = "http://reebot.io:8083/api/req_fepg2json";
    private final String URL_REQ_EPG_SEARCH = "http://reebot.io:8083/api/searchprogram";


    private final String URL_REQ_BOOKING_LIST = "http://reebot.io:8083/api/getbookinglist";
    private final String URL_REQ_BOOKING_ADD = "http://reebot.io:8083/api/addbooking";
    private final String URL_REQ_BOOKING_REMOVE = "http://reebot.io:8083/api/removebooking";


    private Context context;
    //    private ApiManager apiManager;
    private EventCallback eventCallback;

//    private String signalFileName = "";

    public ReeBotApi(Context context) {
        this.context = context;
//        apiManager = new ApiManager(context);
        if (context instanceof EventCallback) {
            eventCallback = (EventCallback) context;
        }

    }

    public void setEventCallback(EventCallback eventCallback) {
        this.eventCallback = eventCallback;
    }

    public void checkVersion(final Activity activity) {
        Log.d(TAG, "checkVersion");

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

        ApiManager apiManager = new ApiManager(context);
        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
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
                                            if (eventCallback != null) {
                                                eventCallback.onEvent(InitActivity.EVENT_REQ_CHECK_VERSION_SUCCESS, "");
                                            }

                                            break;
                                    }
                                }
                            };
                            AlertDialogUtil.showDialog(activity, "", "업데이트가 있습니다.\n기능 확장을 위해서 업데이트가 필요합니다.", "건너 뛰기", "업데이트", onClickListener);
                        } else {
                            if (eventCallback != null) {
                                eventCallback.onEvent(InitActivity.EVENT_REQ_CHECK_VERSION_SUCCESS, "");
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (eventCallback != null) {
                        eventCallback.onEvent(InitActivity.EVENT_REQ_CHECK_VERSION_FAIL, e.toString());
                    }
                }

            }
        });
        apiManager.setDialogMsg("버전 검사 중입니다..");
        apiManager.execute(URL_CHECK_VERSION, "", "");

    }

    public void authToken(RBAuthData rbAuthData) {
        final int mode = rbAuthData.getMode();
        ApiManager apiManager = new ApiManager(context);
//        if (mode == 3)
//            asyncDialog.setMessage("장치 연결 중입니다.");
        if (mode == ExtraInfo.AUTH_MODE_EMAIL_JOIN || mode == ExtraInfo.AUTH_MODE_KAKAO_JOIN) {
            apiManager.setDialogMsg("회원 가입 중입니다..");
        } else {
            apiManager.setDialogMsg("인증 요청 중입니다..");
        }
//        RBAuthData rbAuthData = new RBAuthData(AUTH_MODE_4444, "", "", "", "", "", "", "", URL_CHECK_TOKEN, accesstoken);

        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
                if (mode == ExtraInfo.AUTH_MODE_EMAIL_LOGIN) {
//                    Log.d(TAG, "RBAuthLoginTaskResult : " + result);
//                    rbAuthManagerListener.RBAuthLoginTaskResult(result);
                    eventCallback.onEvent(InitActivity.EVENT_REQ_EMAIL_LOGIN_SUCCESS, respose);
                } else if (mode == ExtraInfo.AUTH_MODE_EMAIL_JOIN) {
//                    rbAuthManagerListener.RBAuthSinginTaskResult(result);
                    eventCallback.onEvent(JoinActivity.EVENT_REQ_EMAIL_JOIN_SUCCESS, respose);
                } else if (mode == ExtraInfo.AUTH_MODE_CHECK_AND_ADD_DEVICE) {
//                    rbAuthManagerListener.RBAuthCheckdevTaskResult(result);
                } else if (mode == ExtraInfo.AUTH_MODE_CHECK_TOKEN) {
                    eventCallback.onEvent(InitActivity.EVENT_REQ_CHECK_TOKEN_SUCCESS, respose);
//                    if (callback != null) callback.onTaskDone(result);
                } else if (mode == ExtraInfo.AUTH_MODE_KAKAO_AUTH) {

                    eventCallback.onEvent(InitActivity.EVENT_REQ_KAKAO_AUTH_SUCCESS, respose);
//                    rbAuthManagerListener.RBAuthChecktokenTaskResult(result);
                } else if (mode == ExtraInfo.AUTH_MODE_KAKAO_JOIN) {
                    eventCallback.onEvent(JoinActivity.EVENT_REQ_KAKAO_JOIN_SUCCESS, respose);

//                    rbAuthManagerListener.RBAuthSinginTaskResult(result);
                } else {

                }
            }
        });

        switch (mode) {
            case ExtraInfo.AUTH_MODE_EMAIL_LOGIN:
                //URL_LOGIN
//                ret = regMember(makeparam(params[0].email, params[0].password), params[0].url, params[0].token);
                apiManager.execute(URL_LOGIN, makeAuthParam(rbAuthData.getEmail(), rbAuthData.getPassword()), rbAuthData.getToken());
                break;
            case ExtraInfo.AUTH_MODE_EMAIL_JOIN:
                //URL_SIGN_IN
                apiManager.execute(URL_SIGN_IN, makeJoinParam(mode, rbAuthData.getEmail(), rbAuthData.getPassword(), rbAuthData.getLocation(), rbAuthData.getTvbrend(), rbAuthData.getCatvvendor(), rbAuthData.getPushtoken()), rbAuthData.getToken());
//                ret = regMember(makeparam(params[0].email, params[0].password, params[0].location, params[0].tvbrend, params[0].catvvendor, params[0].pushtoken), params[0].url, params[0].token);
                break;
            case ExtraInfo.AUTH_MODE_CHECK_AND_ADD_DEVICE:
                //URL_CHECK_AND_ADD_DEVICE
                break;
            case ExtraInfo.AUTH_MODE_CHECK_TOKEN:
                apiManager.execute(URL_CHECK_TOKEN, "", rbAuthData.getToken());
                break;
            case ExtraInfo.AUTH_MODE_KAKAO_AUTH:
                //URL_CHECK_KAKAO_TOKEN
                String param = "email=" + rbAuthData.getEmail();
                apiManager.execute(URL_CHECK_KAKAO_TOKEN, param, rbAuthData.getToken());

                break;
            case ExtraInfo.AUTH_MODE_KAKAO_JOIN:
                //URL_SIGN_IN
                apiManager.execute(URL_SIGN_IN, makeJoinParam(mode, rbAuthData.getEmail(), rbAuthData.getPassword(), rbAuthData.getLocation(), rbAuthData.getTvbrend(), rbAuthData.getCatvvendor(), rbAuthData.getPushtoken()), rbAuthData.getAtoken());
                break;

        }

    }

    public void reqSignalList(RBIRParamData rbirParamData) {
        ApiManager apiManager = new ApiManager(context);
        final int mode = rbirParamData.mode;

        if (mode == ExtraInfo.REQ_MODE_REMOTE_SIGNAL) {
            apiManager.setDialogMsg("리모컨 신호 다운로드 중입니다.");
        } else {
            apiManager.setDialogMsg("리모컨 목록을 가져오는 중입니다.");
        }

        final String signalFileName = makefilename(rbirParamData.id, rbirParamData.location, rbirParamData.param1);

        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String response) {
                if (mode == ExtraInfo.REQ_MODE_REMOTE_SIGNAL) {
                    eventCallback.onEvent(JoinActivity.EVENT_REQ_REMOTE_SIGNAL_SUCCESS, response);
//                    if(mode == 1){
//                        rbIRGetSignalListener.RBIRGetListSignaTaskResult(result);
//                        if(callback != null)callback.onTaskDone(result);
//                    }
//                    else
//                    {
//                        rbIRGetSignalListener.RBIRGetSignaTaskResult(result);
//                        if(callback != null) callback.onTaskDone(result);
//                    }
                } else if (mode == ExtraInfo.REQ_MODE_REMOTE_IR_SIGNAL_SBOX || mode == ExtraInfo.REQ_MODE_REMOTE_IR_SIGNAL_TV) {
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
                        } catch (IOException e) {
                            e.printStackTrace();
                            response = "error";
                            Log.e(TAG + " Exception", "File write failed: " + e.toString());
//                            eventCallback.onEvent(mode, response);
                        }
                    } else {
                        Log.d(TAG, "response is null");
//                        eventCallback.onEvent(mode, response);
                    }
                    eventCallback.onEvent(mode, response);
                }
            }
        });

        switch (mode) {
            case ExtraInfo.REQ_MODE_REMOTE_SIGNAL:
                apiManager.execute(URL_REQ_SIGNAL_LIST, makeSignalParam(rbirParamData.param1, "", ""), null);
                break;
            case ExtraInfo.REQ_MODE_REMOTE_IR_SIGNAL_SBOX:
            case ExtraInfo.REQ_MODE_REMOTE_IR_SIGNAL_TV:
//                signalFileName = makefilename(rbirParamData.id, rbirParamData.location, rbirParamData.param1);
//                Log.d(TAG, "makeParameters : " + params[0].mode + params[0].param1 + params[0].param2);
//                ret = getsiganl(params[0].url, params[0].mode, makeParameters(params[0].mode, params[0].param1, params[0].param2, params[0].param3));
                apiManager.execute(URL_REQ_IRSIGNAL, makeSignalParam(rbirParamData.param1, rbirParamData.param2, rbirParamData.param3), null);
                break;
        }
    }

    public void reqChangeService(String id, String tvb, String catv, String token) {
        ApiManager apiManager = new ApiManager(context);

        apiManager.setDialogMsg("변경 중입니다.");

        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String response) {
                eventCallback.onEvent(SettingActivity.EVENT_REQ_CHANGE_SERVICE, response);
            }
        });

        String param = "id=" + id + "&tv=" + tvb + "&catv=" + catv;

        apiManager.execute(URL_REQ_CHANGE_SERVICE, param, token);
    }

    public void reqEpgList(EPGParamData epgParamData) {
        final int mode = epgParamData.mode;

        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("편성표 가져오기 중입니다.");

        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String response) {
//                eventCallback.onEvent(JoinActivity.EVENT_REQ_REMOTE_SIGNAL_SUCCESS, response);
                if (mode == ExtraInfo.REQ_MODE_EPGLIST_ALL) {
                    eventCallback.onEvent(ExtraInfo.REQ_MODE_EPGLIST_ALL, response);
                } else if (mode == ExtraInfo.REQ_MODE_EPGLIST_CHANNEL) {
                    eventCallback.onEvent(ExtraInfo.REQ_MODE_EPGLIST_CHANNEL, response);
                } else if (mode == ExtraInfo.REQ_MODE_EPGLIST_BOOKMARK) {
                    eventCallback.onEvent(ExtraInfo.REQ_MODE_EPGLIST_BOOKMARK, response);
                } else if (mode == ExtraInfo.REQ_MODE_EPGLIST_SEARCH) {
                    eventCallback.onEvent(ExtraInfo.REQ_MODE_EPGLIST_SEARCH, response);
                }
            }
        });

        System.out.println("reqEpgList catvb: " + epgParamData.catvb);
        System.out.println("reqEpgList chnum: " + epgParamData.chnum);

        switch (mode) {
            case ExtraInfo.REQ_MODE_EPGLIST_ALL:
                apiManager.execute(URL_REQ_EPG_ALL, makeEpgParameters(epgParamData.catvb, "", "", ""), null);
                break;
            case ExtraInfo.REQ_MODE_EPGLIST_CHANNEL:
                apiManager.execute(URL_REQ_EPG_CHANNEL, makeEpgParameters(epgParamData.catvb, epgParamData.chnum, "", ""), null);
                break;
            case ExtraInfo.REQ_MODE_EPGLIST_BOOKMARK:
                apiManager.execute(URL_REQ_EPG_BOOKMARK, makeEpgParameters(epgParamData.catvb, epgParamData.chnum, epgParamData.bookmarklist, ""), null);
                break;
            case ExtraInfo.REQ_MODE_EPGLIST_SEARCH:
                apiManager.execute(URL_REQ_EPG_SEARCH, makeEpgParameters(epgParamData.catvb, "", "", epgParamData.searchstring), null);
                break;
        }

    }


    public void reqBookingList(String email, String token) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("예약 정보 가져오는 중");


        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
                eventCallback.onEvent(ExtraInfo.REQ_MODE_BOOKING_LIST, respose);
            }
        });

        String param = "email=" + email;
        apiManager.execute(URL_REQ_BOOKING_LIST, param, token);
    }

    public void reqBookingAdd(EPGItem epgItem, String email, String catv, String pushtoken, String token) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("예약 정보 등록 중");

        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
                eventCallback.onEvent(ExtraInfo.REQ_MODE_BOOKING_ADD, respose);
            }
        });
//        String param = makeParameters(email,catv, chnum, pushtoken, ptitle, chname, category, bookingtime, programendtime);
        apiManager.execute(URL_REQ_BOOKING_ADD, makeBookingAddParameters(epgItem, email, catv, pushtoken), token);
    }

    public void reqBookingRemove(String id, String token) {
        ApiManager apiManager = new ApiManager(context);
        apiManager.setDialogMsg("예약 정보 삭제 중");

        apiManager.setApiManagerListener(new ApiManagerListener() {
            @Override
            public void onRespose(String respose) {
                eventCallback.onEvent(ExtraInfo.REQ_MODE_BOOKING_REMOVE, respose);
            }
        });
        String param = "id=" + id;

        apiManager.execute(URL_REQ_BOOKING_REMOVE, param, token);
    }


    private String makeAuthParam(String email, String password) {
        return "email=" + email + "&password=" + password;
    }

    private String makeAuthParam(String email, String password, String location, String tvbrend, String catvvendor, String pushtoken, String devtoken) {
        return "email=" + email + "&password=" + password + "&location=" + location + "&tvbrend=" + tvbrend + "&catvvendor=" + catvvendor + "&pushtoken=" + pushtoken + "&devtoken=" + devtoken;
    }

    private String makeJoinParam(int mode, String email, String password, String location, String tvbrend, String catvvendor, String pushtoken) {
        if (mode == ExtraInfo.AUTH_MODE_KAKAO_JOIN) {
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
//        if (mode == 1)//for get list
//        {
//            ret = "dev=" + dev;
//        } else if (mode == 2)  //for get siganl
//        {
//            if (brend2.isEmpty()) {
//                ret = "dev=" + dev + "&brend1=" + brend1;
//            } else {
//                ret = "dev=" + dev + "&brend1=" + brend1 + "&brend2=" + brend2;
//            }
//        } else {
//            ret = "";
//        }
        //Log.d(TAG, "makeParameters : " + ret );
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
        ret = "catvb=" + catvb + "&reqtime=" + dateFormatter.format(date).toString();
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

    private String makeBookingAddParameters(EPGItem epgItem, String email, String catv, String pushtoken) {//String email, String catv, String chnum, String pushtoken, String ptitle, String chname, String category, String bookingtime, String programendtime) {
        String ret = null;
        //Date date = new Date();
        //DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        //dateFormatter.format(date).toString();
        ret = "email=" + email + "&catv=" + catv + "&chnum=" + epgItem.getChnumber() + "&pushtoken=" + pushtoken + "&ptitle=" + epgItem.getProgramTitle() + "&chname=" + epgItem.getChname() + "&category=" + epgItem.getCategory_kr() + "&bookingtime=" + epgItem.getStarttime() + "&programendtime=" + epgItem.getEndtime();
        Log.d(TAG, "queryEPGListJSON :" + ret);
        return ret;
    }

//    private String makeEpgParameters(String catvb, String chnum) {
//        String ret = null;
//        Date date = new Date();
//        DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
//        //dateFormatter.format(date).toString();
//        String[] catv = catvb.split("_");
//        //수정 필요 (통합)
////        ret = "catvb=epgfor"+catv[1]+"&reqtime=20170524200000"+"&chnum="+chnum;
//
//        String tempCatv = catvb;
//
//        if (catv.length > 1) {
//            tempCatv = catv[1];
//        }
//        ret = "catvb=epgfor" + tempCatv + "&reqtime=" + dateFormatter.format(date).toString() + "&chnum=" + chnum;
//        Log.d(TAG, "queryEPGListJSON :" + ret);
//        return ret;
//    }

    private String makefilename(String id, String location, String devicename) {
        String filename = id + "_" + location + "_" + devicename + ".json";
        return filename;
    }

}
