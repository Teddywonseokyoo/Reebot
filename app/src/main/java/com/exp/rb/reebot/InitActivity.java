package com.exp.rb.reebot;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.hardware.ConsumerIrManager;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.exp.rb.reebot.IR.RBIRGetSignal;
import com.exp.rb.reebot.IR.RBIRParamData;
import com.exp.rb.reebot.util.AppUtil;
import com.exp.rb.reebot.version.RBVersionManager;
import com.exp.rb.reebot.view.RBResetPasswordManager;
import com.exp.rb.reebot.view.RBSetting;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class InitActivity extends Activity implements RBEnvConnector.RBEnvConnectorListener, RBEnvConnector.RBSetConnectorListener, RBEnvConnector.RBRegCheckerListener, RBAuthManager.RBAuthManagerListener, RBIRGetSignal.RBIRGetSignalListener {

    private static final boolean demoversion = false;
    private static final String TAG = "ReeBot(InitActivity)";
    //private String checkregurl = "http://reebot.io:8085/checkreg";
    private String signinurl = "http://reebot.io:8083/auth_api/signin";
    private String loginurl = "http://reebot.io:8083/auth_api/authenticate";
    private String checktokenurl = "http://reebot.io:8083/auth_api/checktoken";
    private String checkstokenurl = "http://reebot.io:8083/auth_api/checkktoken";
    private String getsignalurl = "http://reebot.io:8083/api/req_getirsignal";
    private String getlistsignalurl = "http://reebot.io:8083/api/req_getlistsignal";
    private String checkandadddeviceurl = "http://reebot.io:8083/auth_api/checkandadddevice";
    //private String checkurl2;
    FrameLayout loginlayout;
    FrameLayout regmlayout;
    FrameLayout reglayout;
    FrameLayout envslayout;
    FrameLayout progress;
    ImageView loginbtn;
    TextView cattext;
    ImageView nextbtn;
    ImageView wifiresyncbtn;
    ImageView wifisettingsbtn;
    InitActivity initActivity;

    private int rbcheckcount = 0;
    private Spinner spinner_ssis;

    private String lemail = "";
    private String lpass = "";

    private String remail = "";
    private String rpass = "";
    private String rrppass = "";
    private String ssid = "";
    private String tvbrend = "";
    private String catvbrend = "";
    private String ldata[];
    private String edata[];
    private Context mcontext;
    private  String accesstoken="";
    private WifiManager wifiManager;
    private List<ScanResult> scanDatas; // ScanResult List

    private ProgressDialog progressDialog;
    private boolean sirsensor = false;
    private String rbtoken; //리봇장치 토큰
    private int RBIRGetSignalmode;
    ArrayAdapter<String> adapter_catv;
    Spinner spinner_catv;
    ArrayAdapter<String> adapter_tv;
    Spinner spinner_tv;
    private  String curpage;

    private String regmode="nomal";
    private String atoken ="";

    private String RbLaunchType;
    private String BookingBody;
    private String BookingChnum;

    private SessionCallback sessionCallback;
    private CheckBox terms_checkbox;

    private boolean passflag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MultiDex.install(this);
        initActivity = this;
        mcontext = getApplicationContext();
        ldata = SaveEviroment.getUserLoginData(getApplicationContext());
        Log.d(TAG, "PREF_USER_EMAIL : " + ldata[0]);
        Log.d(TAG, "PREF_USER_CODE : " + ldata[1]);
        //예약에서 실행 체크
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if ("booking".equals(bundle.getString("RbLaunchType"))) {
                RbLaunchType = bundle.getString("RbLaunchType");
                BookingBody = bundle.getString("BookingBody");
                BookingChnum = bundle.getString("BookingChnum");
            }
        }
        edata = SaveEviroment.getUserEnvData(getApplicationContext());
        Log.d(TAG, "PREF_USER_TVBREND : " + edata[0]);
        Log.d(TAG, "PREF_USER_CATVBREND : " + edata[1]);
        Log.d(TAG, "PREF_USER_FAVORITE : " + edata[2]);
        setContentView(R.layout.activity_init);

        System.out.println("getKeyHash: " + getKeyHash(this));
        //업데이트 체크
        String version="";
        try {
            PackageInfo i = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            version = i.versionName;
            Log.d(TAG,"current version :" + version);
        }
        catch(PackageManager.NameNotFoundException e)
        {
            Log.d(TAG,"error get version");
        }
        RBVersionManager versionmanager = new RBVersionManager(initActivity);
        final String finalVersion = version;
        versionmanager.setListener(new RBVersionManager.RBVersionManagerListener() {
            @Override
            public void callbackevent(String msg) {
                Log.d(TAG,"get server version : " + msg);
                try {
                    JSONObject json = new JSONObject(msg);
                    if(json.getBoolean("type"))
                    {
                        JSONArray jArray = json.getJSONArray("data");
                        double sversion = 0;
                        double cversion = Double.parseDouble(finalVersion);
                        boolean forceupdate = false;
                        if (jArray.length() == 0) {

                        } else {
                            for (int i = 0; i < jArray.length(); i++) {
                                sversion =  jArray.getJSONObject(i).getDouble("version");
                                forceupdate =  jArray.getJSONObject(i).getBoolean("forceupdate");
                            }
                        }
                        if(cversion < sversion && forceupdate == true)
                        {
                            //강업
                            Log.d(TAG,"You must Need Update");
                            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(initActivity);
                            alert_confirm.setCancelable(false);
                            alert_confirm.setMessage("중요 업데이트가 있습니다.\n업데이트가 반드시 필요합니다.").setPositiveButton("업데이트",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            return;
                                        }
                                    });
                            AlertDialog alert = alert_confirm.create();
                            alert.show();

                        }
                        else if(cversion < sversion && forceupdate == false)
                        {
                            //권고
                            Log.d(TAG,"May you Need Update");
                            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(initActivity);
                            alert_confirm.setCancelable(false);
                            alert_confirm.setMessage("업데이트가 있습니다.\n기능 확장을 위해서 업데이트가 필요합니다.").setNegativeButton("건너 뛰기",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            passflag =true;
                                            request();
                                            if (ldata[1] != null && passflag == true) {
                                                accesstoken = ldata[1];
                                                //token = ldata[1];
                                                Log.d(TAG, "TOKEN : " + accesstoken);
                                                if (!ldata[1].isEmpty()) {
                                                    RBAuthManager rbAuthManager = new RBAuthManager(initActivity);
                                                    //토큰 검증
                                                    rbAuthManager.execute(new RBAuthData(4, "", "", "", "", "", "", "", checktokenurl, accesstoken));
                                                }
                                            }
                                            return;
                                        }
                                    }).setPositiveButton("업데이트", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                            AlertDialog alert = alert_confirm.create();
                            alert.show();
                        }
                        else
                        {
                            Log.d(TAG,"version check pass");
                            passflag =true;

                            if (ldata[1] != null && passflag == true) {
                                accesstoken = ldata[1];
                                //token = ldata[1];
                                Log.d(TAG, "TOKEN : " + accesstoken);
                                if (!ldata[1].isEmpty()) {
                                    RBAuthManager rbAuthManager = new RBAuthManager(initActivity);
                                    //토큰 검증
                                    rbAuthManager.execute(new RBAuthData(4, "", "", "", "", "", "", "", checktokenurl, accesstoken));
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        versionmanager.execute();

/*
        //토큰 검증       //check Auth
        if (ldata[1] != null && passflag == true) {
            accesstoken = ldata[1];
            //token = ldata[1];
            Log.d(TAG, "TOKEN : " + accesstoken);
            if (!ldata[1].isEmpty()) {
                RBAuthManager rbAuthManager = new RBAuthManager(initActivity);
                //토큰 검증
                rbAuthManager.execute(new RBAuthData(4, "", "", "", "", "", "", "", checktokenurl, accesstoken));
            }
        }
*/
        //Not auth
        progress = (FrameLayout) findViewById(R.id.progress); //로그인
        loginlayout = (FrameLayout) findViewById(R.id.login); //로그인
        regmlayout = (FrameLayout) findViewById(R.id.registrymember); //회원가입 메인 창
        reglayout = (FrameLayout) findViewById(R.id.RegistryLayout); //회원가입
        envslayout = (FrameLayout) findViewById(R.id.EsettingLayout); //환경 설정
        loginlayout.setVisibility(View.INVISIBLE);
        regmlayout.setVisibility(View.INVISIBLE);
        reglayout.setVisibility(View.INVISIBLE);
        envslayout.setVisibility(View.INVISIBLE);
        loginlayout.setVisibility(View.VISIBLE);

        cattext = (TextView) findViewById(R.id.cattitle);
        nextbtn = (ImageView) findViewById(R.id.nextbtn);
        wifiresyncbtn = (ImageView) findViewById(R.id.wifiresync);
        wifisettingsbtn = (ImageView) findViewById(R.id.wifisettings);

        //Test
        //setEnvironment();
        TextView regbtn = (TextView) findViewById(R.id.registrymem_btn);
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "loginfacebookbtn");
                //onClickAccessTokenInfo();
                regMember(1,"");
            }
        });

        TextView resetpassbtn = (TextView) findViewById(R.id.resetpassword);
        resetpassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "loginfacebookbtn");
                //onClickAccessTokenInfo();
                // 팝업후
                //regMember(1,"");
                //http://reebot.io:8083/auth_api/pwchangew
                //param email

                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(initActivity);
                View mView = layoutInflaterAndroid.inflate(R.layout.reebot_resetpass_view, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(initActivity);
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("요청", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here

                                if("".equals(userInputDialogEditText.getText()) || !userInputDialogEditText.getText().toString().contains("@"))
                                {
                                    Toast.makeText(initActivity, "이메일 입력 오류", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {

                                    RBResetPasswordManager resetpass = new RBResetPasswordManager(initActivity,new ChangeCallback() {
                                        @Override
                                        public void onTaskDone(String result) {

                                            try {
                                                JSONObject json = new JSONObject(result);
                                                if(json.getBoolean("type"))
                                                {
                                                    Toast.makeText(initActivity, "정상적으로 이메일을 발송하였습니다. 확인 바랍니다.", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    if("simple login user".equals(json.getString("data")))
                                                    {
                                                        Toast.makeText(initActivity, "카카오 로그인을 이용해 주세요.", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else if("Not found user".equals(json.getString("data")))
                                                    {
                                                        Toast.makeText(initActivity, "등록 되어 있지 않은 이메일 입니다. 회원가입 후 이용바랍니다. ", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(initActivity, "이메일을 전송에 실패 하였습니다. 다시 시도 바랍니다.", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    },"");
                                    resetpass.execute(userInputDialogEditText.getText().toString());
                                }
                            }
                        })
                        .setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });
                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();

            }
        });

        loginbtn = (ImageView) findViewById(R.id.loginbtn);
        //로그인 버튼
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //chaeck Auth using our system
                TextView email = (TextView) findViewById(R.id.loginemail);
                EditText pass = (EditText) findViewById(R.id.loginpassword);
                lemail = email.getText().toString();
                lpass = pass.getText().toString();

                if (lemail.isEmpty() || lpass.isEmpty()) {
                    if (lemail.isEmpty() && lpass.isEmpty())
                        Toast.makeText(initActivity, "EMAIL , PASSWD를 입력해주세요. ", Toast.LENGTH_SHORT).show();
                    else if (lemail.isEmpty())
                        Toast.makeText(initActivity, "EMAIL를 입력해주세요. ", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(initActivity, "PASSWD를 입력해주세요. ", Toast.LENGTH_SHORT).show();
                } else {
                    if (!lemail.contains("@") || !lemail.contains("."))
                        Toast.makeText(initActivity, "EMAIL을 정확히 입력해주세요.", Toast.LENGTH_SHORT).show();
                    else if (lpass.length() < 6)
                        Toast.makeText(initActivity, "PASSWORD을 정확히 입력해주세요1.", Toast.LENGTH_SHORT).show();
                    else {

                        String pwd = AppUtil.SHA256(lpass);
                        if (pwd == null || pwd.isEmpty()) {
                            Toast.makeText(initActivity, "PASSWORD 오류. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //auth
                        //If Auth ok ?
                        RBAuthManager rbAuthManager = new RBAuthManager(initActivity);
                        //로그인
                        rbAuthManager.execute(new RBAuthData(1, lemail, pwd, "", "", "", "", "", loginurl, ""));

                    }
                }
            }
        });

        //환경 설정
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //다음 버튼
                if (curpage == "회원가입") {
                    signin();
                }
                //완료 버튼
                else if (curpage == "환경설정") {
                    comEnviroment();
                } else {

                }
            }
        });

        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();
    }

    @Override
    public void RBEnvConnectorTaskResult(String msg) {
        Log.d(TAG, "RBEnvConnectorListener : " + msg);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(initActivity, android.R.layout.simple_spinner_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                if (super.getCount() == 0) return 0;
                else return super.getCount() - 1; // you dont display last item. It is used as hint.
            }
        };
        if (msg.contains("error")) {
            //error : Check
            if (msg.contains("timeout")) {
                Log.e(TAG, "Error Reebot connection : Check connection with reebot1");
                adapter.add("Reebot-xxxxxx로 WIFI를 연결 필요.");
                adapter.add("Reebot-xxxxxx로 WIFI를 연결 필요.");
            } else {
                adapter.add("Reebot-xxxxxx로 WIFI를 연결 필요.");
                adapter.add("Reebot-xxxxxx로 WIFI를 연결 필요.");
                Log.e(TAG, "Error Reebot connection :Etc");
            }
            RBEnvConnector task = new RBEnvConnector(initActivity, "getssid");
            task.execute("http://192.168.4.1/ssidlist");
        } else {
            try {
                JSONObject jObject = new JSONObject(msg);
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
                    progressDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_ssis.setAdapter(adapter);
        spinner_ssis.setSelection(adapter.getCount());
        spinner_ssis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected : ");
                Object item = parent.getItemAtPosition(position);
                if (item != null) {
                    ssid = item.toString();
                    if (ssid.equals("Reebot-xxxxxx로 WIFI를 연결 필요.")) {
                        //Log.d(TAG, "GO ACTION_WIFI_SETTINGS");
                        //Intent intent=new Intent(Settings.ACTION_WIFI_SETTINGS);
                        //startActivity(intent);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void RBSetConnectorTaskResult(String msg) {

        //장치에 SSID 와 PW 셋팅후 토큰을 받아와 저장
        Log.d(TAG, "RBSetConnectorTaskResult : " + msg);
        if (!msg.contains("error")) {
            try {
                JSONObject jObject = new JSONObject(msg);
                rbtoken = jObject.getString("rbtoken");
                Log.d(TAG, "rbtoken : " + rbtoken);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //기기 연동 실패
        }
    }

    private void regMember(int mode , String email) {
        curpage = "회원가입";
        Log.d(TAG, "회원가입");
        loginlayout.setVisibility(View.INVISIBLE);
        regmlayout.setVisibility(View.INVISIBLE);
        envslayout.setVisibility(View.INVISIBLE);
        regmlayout.setVisibility(View.VISIBLE);
        reglayout.setVisibility(View.VISIBLE);

        if(mode == 2)
        {
            regmode = "kakao";
            EditText temail = (EditText) findViewById(R.id.editemail);
            EditText tpw = (EditText) findViewById(R.id.editpw);
            EditText tppw = (EditText) findViewById(R.id.editppw);

            TextView textView7 = (TextView) findViewById(R.id.textView7);
            TextView textView8 = (TextView) findViewById(R.id.textView8);
            temail.setText(email);
            temail.setFocusable(false);
            tpw.setVisibility(View.GONE);
            tppw.setVisibility(View.GONE);
            textView7.setVisibility(View.GONE);
            textView8.setVisibility(View.GONE);
        }
        else
        {
            EditText temail = (EditText) findViewById(R.id.editemail);
            EditText tpw = (EditText) findViewById(R.id.editpw);
            EditText tppw = (EditText) findViewById(R.id.editppw);
            TextView textView7 = (TextView) findViewById(R.id.textView7);
            TextView textView8 = (TextView) findViewById(R.id.textView8);
            temail.setText(email);
            temail.setFocusable(true);
            tpw.setVisibility(View.VISIBLE);
            tppw.setVisibility(View.VISIBLE);
            textView7.setVisibility(View.VISIBLE);
            textView8.setVisibility(View.VISIBLE);
        }
        //약관 보여 주기
        ImageView btn_terms = (ImageView)findViewById(R.id.viewterms);
        final Dialog terms_dialog = new Dialog(this,android.R.style.Theme_Light_NoTitleBar);
        //final Dialog terms_dialog = new Dialog(this); // Context, this, etc.
        terms_dialog.setCanceledOnTouchOutside(false);
        terms_dialog.setContentView(R.layout.terms_view);

        final WebView terms_webview = (WebView)terms_dialog.findViewById(R.id.terms_web);
        terms_checkbox = (CheckBox)findViewById(R.id.terms_checkBox);
        ((Button) terms_dialog.findViewById(R.id.terms_disAgree)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                terms_checkbox.setChecked(false);
                terms_dialog.dismiss();
            }
        });
        ((Button) terms_dialog.findViewById(R.id.terms_Agree)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                terms_checkbox.setChecked(true);
                terms_dialog.dismiss();
            }
        });

        btn_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terms_webview.setWebViewClient(new WebViewClient());
                terms_webview.getSettings().setJavaScriptEnabled(true);
                terms_webview.loadUrl("http://reebot.io:8083/reebot_terms.html");
                terms_dialog.show();
            }
        });

        //Get Surpport CATV Brend
        spinner_catv = (Spinner) findViewById(R.id.cabletvbrend);
        adapter_catv = new ArrayAdapter<String>(initActivity, android.R.layout.simple_spinner_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }
            @Override
            public int getCount() {
                if (super.getCount() == 0) return 0;
                else return super.getCount() - 1; // you dont display last item. It is used as hint.
            }
        };
        //여기 수정 ( 리스트 얻어오기)
        RBIRGetSignal catvsignal = new RBIRGetSignal(initActivity);
        catvsignal.execute(new RBIRParamData(1, getlistsignalurl, "signalsbox"));

        //adapter_catv.add("로딩중.....");
        adapter_catv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner_catv.setAdapter(adapter_catv);
        //spinner_catv.setSelection(adapter_catv.getCount());
        spinner_catv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.d(TAG, "TV onItemSelected : ");
                Object item = parent.getItemAtPosition(position);
                if (item != null) {

                    if((item.toString().contains("_")))
                    {
                        catvbrend = item.toString().split("_")[1];
                        //catvbrend = catvbrend.replaceAll("[0-9]",""); //숫자 제거
                    }
                    else
                    {
                        catvbrend = item.toString();
                    }

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Get Surpport TV Brend
        spinner_tv = (Spinner) findViewById(R.id.tvbrand);
        adapter_tv = new ArrayAdapter<String>(initActivity, android.R.layout.simple_spinner_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                if (super.getCount() == 0) return 0;
                else return super.getCount() - 1; // you dont display last item. It is used as hint.
            }
        };

        RBIRGetSignal tvsignal = new RBIRGetSignal(initActivity);
        tvsignal.execute(new RBIRParamData(1, getlistsignalurl, "signaltv"));

        //adapter_tv.add("TV 브랜드를 선택해 주세요.");
        adapter_tv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        //adapter_tv.add("SAMSUNG");
        //adapter_tv.add("LG");
        //adapter_tv.add("TV 브랜드를 선택해 주세요.");
        adapter_tv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner_tv.setAdapter(adapter_tv);
        //spinner_tv.setSelection(adapter_tv.getCount());
        spinner_tv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "TV onItemSelected : ");
                Object item = parent.getItemAtPosition(position);
                if (item != null) {

                    if((item.toString().contains("_")))
                    {
                        tvbrend = item.toString().split("_")[1];
                        //tvbrend = tvbrend.replaceAll("[0-9]",""); //숫자 제거
                    }
                    else
                    {
                        tvbrend = item.toString();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void signin() {

        //조건 체크
        if("TV 제조사 선택".equals(tvbrend))
        {
            Toast.makeText(initActivity, "TV 제조사를 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if("TV 방송 사업자 선택".equals(catvbrend))
        {
            Toast.makeText(initActivity, "TV 방송 사업자 선택를 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        //약관체크
        if(!terms_checkbox.isChecked())
        {
            Toast.makeText(initActivity, "약관을 동의 해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }



        EditText temail = (EditText) findViewById(R.id.editemail);
        remail = temail.getText().toString();
        FirebaseMessaging.getInstance().subscribeToTopic("reebot");
        String token = FirebaseInstanceId.getInstance().getToken();
        RBAuthManager rbAuthManager = new RBAuthManager(initActivity);
        if("kakao".equals(regmode)) {
            //카카로 회원 가입
            Log.d(TAG,"atoken : "+atoken);
            rbAuthManager.execute(new RBAuthData(6, remail, "", "", tvbrend, catvbrend, token, "", signinurl, "",atoken));
        }
        else
        {
            EditText tpw = (EditText) findViewById(R.id.editpw);
            EditText tppw = (EditText) findViewById(R.id.editppw);
            rpass = tpw.getText().toString();
            rrppass = tppw.getText().toString();
            if (remail.isEmpty() || rpass.isEmpty() || rrppass.isEmpty()) {
                if (remail.isEmpty() && rpass.isEmpty())
                    Toast.makeText(initActivity, "EMAIL , PASSWD를 입력해주세요. ", Toast.LENGTH_SHORT).show();
                else if (remail.isEmpty())
                    Toast.makeText(initActivity, "EMAIL를 입력해주세요. ", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(initActivity, "PASSWD를 입력해주세요. ", Toast.LENGTH_SHORT).show();

                return;
            } else {
                if (!remail.contains("@") || !remail.contains(".")) {
                    Toast.makeText(initActivity, "EMAIL을 정확히 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (rpass.length() < 6) {
                    Toast.makeText(initActivity, "PASSWORD을 정확히 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (rpass.equals(rrppass)) {

                    } else {
                        Toast.makeText(initActivity, "PASSWORD을 같게 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            //regimember

            String pwd = AppUtil.SHA256(rpass);
            if (pwd == null || pwd.isEmpty()) {
                Toast.makeText(initActivity, "PASSWORD 오류. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            rbAuthManager.execute(new RBAuthData(2, remail, pwd, "", tvbrend, catvbrend, token, "", signinurl, ""));
        }
    }

    private boolean devicepairing() {
        Log.d(TAG, "devicepairing");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "checkSelfPermission : NOT");
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    // Explain to the user why we need to write the permission.
                    Toast.makeText(this, "ACCESS_COARSE_LOCATION", Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0x12345);
            } else {
                wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                registerReceiver(receiver, intentFilter);

                Log.d(TAG, "startScan");
                wifiManager.startScan();

            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult");
        if (requestCode == 0x12345) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "PERMISSION_GRANTED");
                    return;
                }
            }
            wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(receiver, intentFilter);

            Log.d(TAG, "startScan");
            wifiManager.startScan();
        }
    }

    private boolean connectn = false;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "BroadcastReceiver");
            //scanDatas = wifiManager.getScanResults();
            //int size = scanDatas.size();

            final String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                scanDatas = wifiManager.getScanResults();
                //setResults(scanDatas);
                Log.d("BroadcastReceiver", "size : " + scanDatas.size());
                for (ScanResult result : scanDatas) {
                    Log.d("BroadcastReceiver", "SSID : " + result.SSID.toString() + " / LEVEL :" + result.level);
                    if (result.SSID.toString().matches(".*ReeBot.*") && connectn == false) {
                        Log.d(TAG, "Founf Reebot");
                        String securityMode = getScanResultSecurity(result);
                        //WifiConfiguration wifiConfiguration = createAPConfiguration( result.SSID , "reebot!!", "WPA2");
                        //int res = wifiManager.addNetwork(wifiConfiguration);
                        int res = getNetworkId(result.SSID);
                        Log.d(TAG, "# addNetwork returned " + res);
                        wifiManager.disconnect();
                        boolean b = wifiManager.enableNetwork(res, true);
                        boolean c = wifiManager.reconnect();
                        if (c == true) connectn = true;
                        Log.d(TAG, "# enableNetwork returned " + b);
                        //wifiManager.setWifiEnabled(true);
                        boolean changeHappen = wifiManager.saveConfiguration();
                        if (res != -1 && changeHappen) {
                            //if (res != -1 ) {
                            Log.d(TAG, "# Change happen");
                            Log.d(TAG, "Get SSID LIST");
                            //RBEnvConnector task = new RBEnvConnector(initActivity,"getssid");
                            //task.execute("http://192.168.4.1/ssidlist");
                            //connectedSsidName = networkSSID;
                        } else {
                            Log.d(TAG, "# Change NOT happen");
                        }
                    }
                    /*
                    if(wifiManager.getConnectionInfo ().getSSID().matches(".*ReeBot.*"))
                    {

                        RBEnvConnector task = new RBEnvConnector(initActivity,"getssid");
                        task.execute("http://192.168.4.1/ssidlist");
                    }
                    */
                }
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
            }
        }
    };

    private int getNetworkId(String ssid) {
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                return i.networkId;
            }
        }
        return -1;
    }

    public String getScanResultSecurity(ScanResult scanResult) {

        final String cap = scanResult.capabilities;
        final String[] securityModes = {"WEP", "PSK", "EAP"};

        for (int i = securityModes.length - 1; i >= 0; i--) {
            if (cap.contains(securityModes[i])) {
                return securityModes[i];
            }
        }

        return "OPEN";
    }

    private WifiConfiguration createAPConfiguration(String networkSSID, String networkPasskey, String securityMode) {

        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "\"" + networkSSID + "\"";
        wifiConfiguration.status = WifiConfiguration.Status.DISABLED;
        wifiConfiguration.priority = 40;

        if (securityMode.equalsIgnoreCase("OPEN")) {

            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        } else if (securityMode.equalsIgnoreCase("WEP")) {

            wifiConfiguration.wepKeys[0] = "\"" + networkPasskey + "\"";
            wifiConfiguration.wepTxKeyIndex = 0;
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

        } else if (securityMode.equalsIgnoreCase("WPA")) {
            //WPA/WPA2 Security
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfiguration.preSharedKey = "\"".concat(networkPasskey).concat("\"");

        } else if (securityMode.equalsIgnoreCase("WPA2")) {
            //WPA/WPA2 Security
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfiguration.preSharedKey = "\"".concat(networkPasskey).concat("\"");
        } else if (securityMode.equalsIgnoreCase("PSK")) {

            wifiConfiguration.preSharedKey = "\"" + networkPasskey + "\"";
            wifiConfiguration.hiddenSSID = true;
            wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

        } else {
            Log.i(TAG, "# Unsupported security mode: " + securityMode);

            return null;
        }

        return wifiConfiguration;

    }


    private void setEnvironment() {
        curpage = "환경설정";
        Log.d(TAG, "환경설정");
        cattext.setText("환경설정");
        nextbtn.setImageResource(R.drawable.reebot_btn_confirm);
        loginlayout.setVisibility(View.INVISIBLE);
        regmlayout.setVisibility(View.VISIBLE);
        reglayout.setVisibility(View.INVISIBLE);
        envslayout.setVisibility(View.VISIBLE);
        //devicepairing();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("장치를 검색중입니다.\n검색하는데 1분 정도 소요됩니다.");
        progressDialog.show();
        /*
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //progressDialog.dismiss();
            }
        }.sendEmptyMessageDelayed(0, 500);
        */
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiConfiguration wifiConfiguration = createAPConfiguration("ReeBot-INIT", "reebot!!", "WPA2");
                int res = wifiManager.addNetwork(wifiConfiguration);
                if (res == -1) res = getNetworkId("ReeBot-INIT");
                Log.d(TAG, "# addNetwork returned " + res);
                while (true)
                {
                    wifiManager.disconnect();
                    boolean b = wifiManager.enableNetwork(res, true);
                    boolean c = wifiManager.reconnect();
                    //if(c == true) connectn = true;
                    Log.d(TAG, "# enableNetwork returned " + b);
                    //wifiManager.setWifiEnabled(true);
                    //boolean changeHappen = wifiManager.saveConfiguration();
                    //if (res != -1 && changeHappen) {
                    if (res != -1) {
                        Log.d(TAG, "# Change happen");
                        Log.d(TAG, "Get SSID LIST");
                        try {
                            Thread.sleep(5000);
                            if (wifiManager.getConnectionInfo().getSSID().matches(".*ReeBot.*")) {
                                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                                if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                                    RBEnvConnector task = new RBEnvConnector(initActivity, "getssid");
                                    task.execute("http://192.168.4.1/ssidlist");

                                    break;
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                        //connectedSsidName = networkSSID;
                    } else {
                        Log.d(TAG, "# Change NOT happen");
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                } catch (Exception e) {

                    //와이 파이를 켜주세요.


                    e.printStackTrace();

                }
            }
        });
        thread.start();


        //Get SSIS LIST
        spinner_ssis = (Spinner) findViewById(R.id.ssid);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(initActivity, android.R.layout.simple_spinner_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                if (super.getCount() == 0) return 0;
                else return super.getCount() - 1; // you dont display last item. It is used as hint.
            }
        };
        adapter.add("Reebot-xxxxxx로 WIFI를 연결 필요.");
        adapter.add("Reebot-xxxxxx로 WIFI를 연결 필요.");

        spinner_ssis.setAdapter(adapter);
        spinner_ssis.setSelection(adapter.getCount());

        //Wifi Resync btn
        wifiresyncbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RBEnvConnector task = new RBEnvConnector(initActivity, "getssid");
                task.execute("http://192.168.4.1/ssidlist");
                Log.d(TAG, "wifiresyncbtn");
            }
        });
        wifisettingsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "GO ACTION_WIFI_SETTINGS");
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivityForResult(intent, 101);
                //startActivity(intent);
            }
        });
    }

    private void comEnviroment() {
        //회원정보 서버 전달
        //tvvendor 케이블제공업체
        //location 리봇위치 (거실,방)
        //pushtoken (푸쉬 토큰)

        Log.d(TAG, "로그인 시작");
        //Check RegInfo
        //Save eviroment file
        //리봇 와이파이 얻어 오기
        //리봇 SSID 얻어오기

        TextView wp = (TextView) findViewById(R.id.editboxwifipw);
        String str_wp = wp.getText().toString();
        if (ssid == null) return;
        if (ssid.equals("Reebot-xxxxxx로 WIFI를 연결 필요.") || ssid.equals("HOME WIFI SSID 선택") || ssid.isEmpty() || str_wp.isEmpty()) {
            Log.d(TAG, "equals : true");
            Toast.makeText(initActivity, "로그인 실패 값 확인", Toast.LENGTH_SHORT).show();
        } else {
            //수정필요
            //Toast.makeText(initActivity, "로그인 완료" + " : " + ssid + " / " + str_wp, Toast.LENGTH_SHORT).show();
            regmlayout.setVisibility(View.INVISIBLE);
            progress.setVisibility(View.VISIBLE);
            //따로 페이지 만들어 시도로 수정 요함
            String[] separated = remail.split("@");
            String pemail = separated[0]; // this will contain "Fruit"
            RBEnvConnector task = new RBEnvConnector(initActivity, "setwifi");
            task.execute("http://192.168.4.1/setting?ssid=" + ssid + "&pass=" + str_wp + "&tid=" + pemail);
            //Log.d(TAG, "tid :" + "http://192.168.4.1/setting?ssid="+ssid+"&pass="+str_wp+"&tid="+pemail);

            //디바이스 등록 확인 로직
            RBEnvConnector regchecktask = null;
            try {

                Thread.sleep(5000);
                //checkurl2 = checkregurl + "?id=" + pemail;
                //regchecktask = new RBEnvConnector(initActivity, "checkreg");
                //regchecktask.execute(checkurl2);
                RBAuthManager rbAuthManager = new RBAuthManager(initActivity);

                //회원가입 리봇 사용
                //TID 리봇 토큰으로 변경
                rbAuthManager.execute(new RBAuthData(3, remail,  AppUtil.SHA256(rpass), "", tvbrend, catvbrend, "", rbtoken, checkandadddeviceurl, accesstoken));
            } catch (Exception e) {
                Log.d(TAG, "RBEnvConnector Exception" + e);
                //regchecktask.execute(checkurl2);
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code
            Log.d(TAG, "onKeyDown : true");

            if (curpage == "환경설정") {
                regMember(1,"");
            } else if (curpage == "회원가입") {
                curpage = "로그인";
                loginlayout.setVisibility(View.INVISIBLE);
                regmlayout.setVisibility(View.INVISIBLE);
                reglayout.setVisibility(View.INVISIBLE);
                envslayout.setVisibility(View.INVISIBLE);
                loginlayout.setVisibility(View.VISIBLE);
            } else {
                initActivity.finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...
        Log.d(TAG, "onResume");
        //if(spinner_ssis !=null) {
        //RBEnvConnector onResumetask = new RBEnvConnector(initActivity, "getssid");
        //onResumetask.execute("http://192.168.4.1/ssidlist");
        //}
    }

    @Override
    public void RBRegCheckerTaskResult(String msg) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult");
        // 로그 아웃시
        if (requestCode == 101) {

            Log.d(TAG, "LOGOUT END : 101");
            progress.setVisibility(View.INVISIBLE);
            loginlayout.setVisibility(View.INVISIBLE);
            regmlayout.setVisibility(View.INVISIBLE);
            reglayout.setVisibility(View.INVISIBLE);
            envslayout.setVisibility(View.INVISIBLE);
            loginlayout.setVisibility(View.VISIBLE);
            // TODO Extract the data returned from the child Activity.
            if (data != null) {
                String returnValue = data.getStringExtra("control");
                if (returnValue.equals("erasedata")) {
                    //카카오 로그아웃

                    SaveEviroment.delEnvData(mcontext);
                }
            }
        }
    }
    @Override
    public void RBAuthSinginTaskResult(String msg) {
        Log.d(TAG, "RBAuthSinginTaskResult : " + msg);
        boolean type = false;
        String token = "";
        String data = "";
        JSONObject jObject = null;
        JSONArray jArray = null;
        try {
            jObject = new JSONObject(msg);
            type = jObject.getBoolean("type");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        if (type)
        {
            try {
                //if("nomal".equals(regmode))
                //{
                    token = jObject.getString("token");
                //}
                //else
                //{

                //}
                data = jObject.getString("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            accesstoken = token;
            //로그인 정보 저장
            SaveEviroment.setUserName(getApplicationContext(), remail, token);
            SaveEviroment.setUserEnv(getApplicationContext(), tvbrend, catvbrend, "");
            //회원가입 완료

            //내장 IR파악 .
            ConsumerIrManager mCIR = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);
            sirsensor = false;
            if (mCIR == null) {

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (mCIR.hasIrEmitter()) {
                        //내장 IR 사용 가능
                        sirsensor = true;
                        Log.d(TAG, "내장 리모컨 사용 가능");
                    } else {

                    }
                } else {

                }
            }
            final Dialog dialog = new Dialog(this); // Context, this, etc.
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.commember);
            TextView content = (TextView) dialog.findViewById(R.id.dialog_info);
            Button btn_cancle = (Button) dialog.findViewById(R.id.cmdialog_cancel);
            Button btn_ok = (Button) dialog.findViewById(R.id.cmdialog_ok);
            if (sirsensor == true) {
                dialog.setTitle(R.string.cmdialog_title);
                content.setText("내장 리모컨 사용이 가능 합니다.");
                btn_cancle.setText("내장리모컨 사용");
                btn_ok.setText("외장 장치 추가(예정)");
            } else {
                dialog.setTitle(R.string.cmdialog_title);
                content.setText("스마트폰에 내장 리모컨이 없거나 사용 할 수 없습니다.");
                btn_cancle.setText("편성표 기능만 사용");
                btn_ok.setText("외장 장치 추가(예정)");
            }
            dialog.show();
            //Button cmdialogCancelButton = (Button) dialog.findViewById(R.id.cmdialog_cancel);
            // if button is clicked, close the custom dialog
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //내장 IR 시그널 파일 다운로드 TV 및 셋톱박스
                    if (sirsensor == true) {
                        //다운로드 셋탑 시그널
                        RBIRGetSignalmode = 1;
                        String[] separated = remail.split("@");
                        RBIRGetSignal catv_signal = new RBIRGetSignal(initActivity);
                        catv_signal.execute(new RBIRParamData(2, getsignalurl, separated[0], "room1", "signalsbox", catvbrend, ""));
                        //다운로드 TV시그널
                        RBIRGetSignal tv_signal = new RBIRGetSignal(initActivity);
                        tv_signal.execute(new RBIRParamData(2, getsignalurl, separated[0], "room1", "signaltv", tvbrend, ""));
                    }
                    else
                    {


                    }
                    dialog.dismiss();
                    //메인창으로 진입 EPG만 OR 내장 IR로 가능
                    Intent mainintent = new Intent(getBaseContext(), MainActivity.class);
                    //내장 IR여부에 따라 플래그 설정 틀리게
                    mainintent.putExtra("email", remail); //Optional parameters
                    if (sirsensor == true)
                        mainintent.putExtra("sid", "nodevice"); //Optional parameters
                    else
                        mainintent.putExtra("sid", "rebot_onlyepg"); //Optional parameters
                    mainintent.putExtra("tvb", tvbrend); //Optional parameters //PREF_USER_TVBREND
                    mainintent.putExtra("catvb", catvbrend); //Optional parameters //PREF_USER_TVBREND
                    mainintent.putExtra("acesstoken", accesstoken); //Optional parameters //PREF_USER_TOKEN
                    startActivityForResult(mainintent, 101);
                }
            });
            //Button cmdialogOKButton = (Button) dialog.findViewById(R.id.cmdialog_ok);
            // if button is clicked, close the custom dialog
            final Dialog reebot_intro_dialog = new Dialog(this,android.R.style.Theme_Light_NoTitleBar);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //다운로드 셋탑 시그널
                    RBIRGetSignalmode = 2;
                    String[] separated = remail.split("@");

                    //장치 나올때까지만 막는다

                    if(demoversion == true)
                    {
                        RBIRGetSignal signal = new RBIRGetSignal(initActivity);
                        signal.execute(new RBIRParamData(2, getsignalurl, separated[0], "room1", "signalsbox", catvbrend, ""));
                    }

                    //다운로드 TV시그널
                    //내장 IR 시그널 파일 다운로드 TV 및 셋톱박스
                    if (sirsensor == true) {

                    }
                    dialog.dismiss();
                   // setEnvironment();


                    if(demoversion != true) {
                        //장치 나올때까지만 풀어놓는다.
                        //광고 페이지
                        reebot_intro_dialog.setCanceledOnTouchOutside(false);
                        reebot_intro_dialog.setContentView(R.layout.reebot_intro_view);
                        final WebView terms_webview = (WebView)reebot_intro_dialog.findViewById(R.id.intro_web);
                        terms_webview.setWebViewClient(new WebViewClient());
                        terms_webview.getSettings().setJavaScriptEnabled(true);
                        terms_webview.loadUrl("http://reebot.io:8083/reebot_intro.html");
                        reebot_intro_dialog.show();
                        ((Button)reebot_intro_dialog.findViewById(R.id.intro_ok)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                reebot_intro_dialog.dismiss();;
                                //메인창으로 진입 EPG만 OR 내장 IR로 가능
                                Intent mainintent = new Intent(getBaseContext(), MainActivity.class);
                                //내장 IR여부에 따라 플래그 설정 틀리게
                                mainintent.putExtra("email", remail); //Optional parameters
                                if (sirsensor == true)
                                    mainintent.putExtra("sid", "nodevice"); //Optional parameters
                                else
                                    mainintent.putExtra("sid", "rebot_onlyepg"); //Optional parameters
                                mainintent.putExtra("tvb", tvbrend); //Optional parameters //PREF_USER_TVBREND
                                mainintent.putExtra("catvb", catvbrend); //Optional parameters //PREF_USER_TVBREND
                                mainintent.putExtra("acesstoken", accesstoken); //Optional parameters //PREF_USER_TOKEN
                                startActivityForResult(mainintent, 101);
                            }
                        });
                    }
                }
            });
        }
        else
        {
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(initActivity);
            alert_confirm.setMessage("이미 가입된 회원 정보입니다.").setNegativeButton("확인",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            AlertDialog alert = alert_confirm.create();
            alert.show();
        }
    }

    @Override
    public void RBAuthLoginTaskResult(String msg) {
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
                if("nomallogin".equals(jObject.getString("type2"))) //일반 ID / PW 로그인
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
                    Log.d(TAG, "RBAuthLoginTaskResult : " + msg);
                    Log.d(TAG, "Auth ok");

                    Intent mainintent = new Intent(getBaseContext(), MainActivity.class);
                    //String[] separated = lemail.split("@");
                    //String pemail = separated[0]; // this will contain "Fruit"
                    mainintent.putExtra("email", lemail); //Optional parameters
                    mainintent.putExtra("sid", rbtoken); //Optional parameters
                    mainintent.putExtra("tvb", tvbrend); //Optional parameters
                    mainintent.putExtra("catvb", catvvendor); //Optional parameters
                    mainintent.putExtra("acesstoken", accesstoken); //Optional parameters //PREF_USER_TOKEN
                    if("booking".equals(RbLaunchType))
                    {
                        mainintent.putExtra("RbLaunchType",RbLaunchType);
                        mainintent.putExtra("BookingBody",BookingBody);
                        mainintent.putExtra("BookingChnum",BookingChnum);


                    }
                    startActivityForResult(mainintent, 101);
                }
                else if("signin_nomal".equals(jObject.getString("type2")))
                {
                    JSONObject data = jObject.getJSONObject("data");
                    jArray = data.getJSONArray("Regdevice");
                    token = jObject.getString("token");
                }
                else if("signin_kakao".equals(jObject.getString("type2")))
                {
                    JSONObject data = jObject.getJSONObject("data");
                    jArray = data.getJSONArray("Regdevice");

                }
                else {
                    JSONObject data = jObject.getJSONObject("data");
                    jArray = data.getJSONArray("Regdevice");

                }
                //로그인 유지 이면 저장
                SaveEviroment.setUserName(getApplicationContext(), lemail, token);
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
                Log.d(TAG, "RBAuthLoginTaskResult : " + msg);
                Log.d(TAG, "Auth ok");
            } else {

                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(initActivity);
                alert_confirm.setMessage("회원정보가 없습니다.").setCancelable(false).setPositiveButton("회원가입",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if ("nomal".equals(regmode)) {
                                    regMember(1, "");
                                } else {
                                }
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {if ("nomal".equals(regmode)) {
                                        return;
                                } else {
                                        loginlayout.setVisibility(View.INVISIBLE);
                                        regmlayout.setVisibility(View.INVISIBLE);
                                        reglayout.setVisibility(View.INVISIBLE);
                                        envslayout.setVisibility(View.INVISIBLE);
                                        loginlayout.setVisibility(View.VISIBLE);
                                }}
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void RBAuthCheckdevTaskResult(String msg) {
        Log.d(TAG, "RBRegCheckerTaskResult " + msg);
        if (msg.contains("error")) {
            String[] separated = remail.split("@");
            String pemail = separated[0]; // this will contain "Fruit"
            RBAuthManager rbAuthManager = new RBAuthManager(initActivity);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            rbAuthManager.execute(new RBAuthData(3, remail,  AppUtil.SHA256(rpass), "", tvbrend, catvbrend, "", rbtoken, checkandadddeviceurl, accesstoken));
            rbcheckcount++;
        } else {
            try {
                JSONObject jObject = new JSONObject(msg);
                if (jObject.getBoolean("type")) {
                    ldata = SaveEviroment.getUserLoginData(getApplicationContext());
                    Log.d(TAG, "PREF_USER_EMAIL : " + ldata[0]);
                    Log.d(TAG, "PREF_USER_CODE : " + ldata[1]);

                    edata = SaveEviroment.getUserEnvData(getApplicationContext());
                    Log.d(TAG, "PREF_USER_TVBREND : " + edata[0]);
                    Log.d(TAG, "PREF_USER_CATVBREND : " + edata[1]);
                    Log.d(TAG, "PREF_USER_FAVORITE : " + edata[2]);
                    //myIntent.putExtra("key", value); //Optional parameters
                    Intent mainintent = new Intent(getBaseContext(), MainActivity.class);

                    if (!ldata[0].isEmpty() && !ldata[1].isEmpty() && !edata[0].isEmpty() && !edata[1].isEmpty()) {

                        Log.d(TAG, "NEED DEBUG1 " + rbtoken);
                        //Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
                        mainintent.putExtra("email", ldata[0]); //Optional parameters
                        mainintent.putExtra("sid", rbtoken); //Optional parameters
                        mainintent.putExtra("tvb", edata[0]); //Optional parameters //PREF_USER_TVBREND
                        mainintent.putExtra("catvb", edata[1]); //Optional parameters //PREF_USER_TVBREND
                        mainintent.putExtra("acesstoken", accesstoken); //Optional parameters //PREF_USER_TOKEN
                    }
                    mainintent.putExtra("tvb", edata[0]); //Optional parameters //PREF_USER_TVBREND
                    mainintent.putExtra("email", ldata[0]); //Optional parameters
                    mainintent.putExtra("catvb", edata[1]); //Optional parameters //PREF_USER_TVBREND
                    mainintent.putExtra("sid", rbtoken); //Optional parameters
                    Log.d(TAG, "NEED DEBUG2 " + rbtoken + "/" + edata[0] + "/" + edata[1] + "/" + ldata[0]);
                    if("booking".equals(RbLaunchType))
                    {
                        mainintent.putExtra("RbLaunchType",RbLaunchType);
                        mainintent.putExtra("BookingBody",BookingBody);
                        mainintent.putExtra("BookingChnum",BookingChnum);
                    }
                    startActivityForResult(mainintent, 101);
                } else {
                    if (rbcheckcount < 10) {
                        Toast.makeText(getBaseContext(), "Trying to connect to Reebot Deivce (" + rbcheckcount + ")", Toast.LENGTH_SHORT).show();
                        String[] separated = remail.split("@");
                        String pemail = separated[0]; // this will contain "Fruit"
                        RBAuthManager rbAuthManager = new RBAuthManager(initActivity);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        rbAuthManager.execute(new RBAuthData(3, remail,  AppUtil.SHA256(rpass), "", tvbrend, catvbrend, "", rbtoken, checkandadddeviceurl, accesstoken));
                        rbcheckcount++;
                    } else {
                        progress.setVisibility(View.GONE);
                        regmlayout.setVisibility(View.VISIBLE);
                        Toast.makeText(getBaseContext(), "Check your AP password and retry.", Toast.LENGTH_LONG).show();
                    }
                    //Log.d(TAG, "RBRegCheckerTaskResult : " + checkregurl);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void RBAuthChecktokenTaskResult(String msg) {

        boolean type = false;
        String location = "";
        String tvbrend = "";
        String catvvendor = "";
        String rbtoken = "";
        String token = "";
        String email = "";
        JSONObject jObject = null;
        JSONArray jArray = null;
        if (msg.equals("error")) {
        } else {
            try {
                jObject = new JSONObject(msg);
                type = jObject.getBoolean("type");
                if (type)
                {
                    //jArray =  jObject.getJSONObject("data").getJSONArray("Regdevice");
                    //email = jObject.getJSONObject("data").getString("email");
                    jArray = jObject.getJSONArray("regdevice");
                    email = jObject.getString("email");
                    // Log.d(TAG, "RBAuthLoginToken: "+token);
                    //수정 필요 메이크 파일
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        location = oneObject.getString("location");
                        tvbrend = oneObject.getString("tvbrend");
                        catvvendor = oneObject.getString("catvvendor");
                        rbtoken = oneObject.getString("rbtoken");
                    }
                    if(!"nomal".equals(jObject.getString("type2"))) accesstoken = jObject.getString("token");
                    Log.d(TAG, "RBAuthLoginTaskResult : " + msg);
                    Log.d(TAG, "Auth ok");
                    Intent mainintent = new Intent(getBaseContext(), MainActivity.class);
                    String[] separated = email.split("@");
                    String pemail = separated[0]; // this will contain "Fruit"
                    mainintent.putExtra("email", email); //Optional parameters
                    mainintent.putExtra("sid", rbtoken); //Optional parameters
                    mainintent.putExtra("tvb", tvbrend); //Optional parameters
                    mainintent.putExtra("catvb", catvvendor); //Optional parameters
                    mainintent.putExtra("acesstoken", accesstoken); //Optional parameters //PREF_USER_TOKEN
                    if("booking".equals(RbLaunchType)) {
                        mainintent.putExtra("RbLaunchType", RbLaunchType);
                        mainintent.putExtra("BookingBody", BookingBody);
                        mainintent.putExtra("BookingChnum", BookingChnum);
                    }
                    startActivityForResult(mainintent, 101);
                    /*
                    if(bundle != null) {
                        Log.d(TAG, "RbLaunchType : " + bundle.getString("RbLaunchType"));
                        if ("booking".equals(bundle.getString("RbLaunchType"))) {
                            mainintent.putExtra("RbLaunchType", "booking");    //silver20170530
                        }
                    }
                    */
                    //startActivity(mainintent);
                } else {
                    //토큰검증 실패
                    if(jObject.getString("data").equals("not found user"))
                    {
                        Log.d(TAG,"카카오 토큰 검증 후 회원 가입 요망");
                        regMember(2,lemail);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void RBIRGetSignaTaskResult(String msg) {

        if (msg.equals("error")) {
        } else {
            try {
                JSONArray jArray = new JSONArray(msg);
                if(jArray != null)
                {
                    if(RBIRGetSignalmode == 2)
                        setEnvironment();
                }
                else
                {
                    //리모콘 신호 가져오기 실패 다이얼로그 추가
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }

    @Override
    public void RBIRGetListSignaTaskResult(String msg) {
        Log.d(TAG, "RBIRGetListSignaTaskResult : " + msg);
        try {
            JSONObject jsonobject = new JSONObject(msg);
            if(jsonobject.getBoolean("type") == true)
            {
                Log.d(TAG, "RBIRGetListSignaTaskResult2 : "  +jsonobject.getString("type2"));
                if("signaltv".equals( jsonobject.getString("type2")) )
                {
                    if(makelist(jsonobject.getJSONArray("data"), adapter_tv) == true)
                    {
                        adapter_tv.add("TV 제조사 선택");
                        spinner_tv.setAdapter(adapter_tv);
                        spinner_tv.setSelection(adapter_tv.getCount());
                    }

                }
                else
                {
                    if(makelist(jsonobject.getJSONArray("data"), adapter_catv) == true)
                    {
                        adapter_catv.add("TV 방송 사업자 선택");
                        spinner_catv.setAdapter(adapter_catv);
                        spinner_catv.setSelection(adapter_catv.getCount());
                    }

                }

            }
            else
            {
                //리스트 가져오기 실패

            }
        }
        catch (Exception e)
        {
            //통신 상태를 확인해주세요.
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(initActivity);
            alert_confirm.setMessage("인터넷 연결 상태를 확인해주세요.").setNegativeButton("확인",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loginlayout.setVisibility(View.INVISIBLE);
                            regmlayout.setVisibility(View.INVISIBLE);
                            reglayout.setVisibility(View.INVISIBLE);
                            envslayout.setVisibility(View.INVISIBLE);
                            loginlayout.setVisibility(View.VISIBLE);
                            return;
                        }
                    });
            AlertDialog alert = alert_confirm.create();
            alert.show();
        }
    }

    private boolean makelist( JSONArray jArray, ArrayAdapter<String> adapter) {
        try {
            adapter.clear();
           // JSONArray jArray = null;
            //jArray = new JSONArray(json);
            if (jArray.length() == 0) {
                adapter.add("장치없음");
            } else {
                //adapter.clear();
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    String SIGNALINFO = oneObject.getString("INFO");
                    adapter.add(SIGNALINFO);
                }
            }
        } catch (JSONException e) {
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    //카카오
    public void request() {

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d(TAG, "request Session Closed Error is " + errorResult.toString());
            }

            @Override
            public void onNotSignedUp() {
                Log.d(TAG, "request onNotSignedUp");
            }

            @Override
            public void onSuccess(UserProfile result) {
                Log.d(TAG, "request onSuccess");
                Log.d(TAG, "request onSuccess getEmail: " + result.getEmail());

                Log.d(TAG, "getAccessToken : "+ Session.getCurrentSession().getAccessToken());
                Log.d(TAG, "RefreshToken :"+ Session.getCurrentSession().getRefreshToken());
                atoken =  Session.getCurrentSession().getAccessToken();
                lemail = result.getEmail();

                Log.d(TAG, "request onSuccess getNickname: " + result.getNickname());
                Log.d(TAG, "request onSuccess getUUID: " + result.getUUID());
                Log.d(TAG, "request onSuccess getEmailVerified: " + result.getEmailVerified());
                Log.d(TAG, "request onSuccess getServiceUserId: " + result.getServiceUserId());
                Log.d(TAG, "request onSuccess getProperties: " + result.getProperties().toString());

                //로그인 시도 없으면 회원 가입 페이지로 던짐
                //회원 가입 페이지에 이메일 파라미터 던짐
                //비밀번호 환성화 X 비밀번호 체크 X

                //카카오 토큰 검증
                if(passflag == true) {
                    RBAuthManager rbAuthManager = new RBAuthManager(initActivity);
                    rbAuthManager.execute(new RBAuthData(5, lemail, "", "", "", "", "", "", checkstokenurl, Session.getCurrentSession().getAccessToken()));
                }
                //onClickAccessTokenInfo();
            }
        });
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            Log.d(TAG, "SessionCallback onSessionOpened");
            request();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.d(TAG, "SessionCallback Session Fail Error is " + exception.getMessage().toString());
        }
    }

    private void onClickAccessTokenInfo() {
        AuthService.requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d(TAG, "onClickAccessTokenInfo onSessionClosed Error is " + errorResult.toString());
            }
            @Override
            public void onNotSignedUp() {
                // not happened
                Log.d(TAG, "SessionCallback onNotSignedUp");
            }
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get access token info. msg=" + errorResult;
                Logger.e(message);
                Log.d(TAG, "SessionCallback onFailure: " + message);
            }
            @Override
            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {
                long userId = accessTokenInfoResponse.getUserId();
               // Log.d(TAG, "getAccessToken : "+ Session.getCurrentSession().getAccessToken());
                //Log.d(TAG, "RefreshToken :"+ Session.getCurrentSession().getRefreshToken());
                Logger.d("this access token is for userId=" + userId);
                Log.d(TAG, "SessionCallback onSuccess userId: " + userId);
                long expiresInMilis = accessTokenInfoResponse.getExpiresInMillis();
                Logger.d("this access token expires after " + expiresInMilis + " milliseconds.");
                Log.d(TAG, "SessionCallback this access token expires after " + expiresInMilis + " milliseconds.");
                //Toast.makeText(getApplicationContext(), "this access token for user(id=" + userId + ") expires after " + expiresInMilis + " milliseconds.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface ChangeCallback {
        public void onTaskDone(String result);
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
}

