package com.example.reebotui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.ConsumerIrManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.reebotui.api.ReeBotApi;
import com.example.reebotui.info.ExtraInfo;
import com.example.reebotui.info.RBAuthData;
import com.example.reebotui.info.RBIRParamData;
import com.example.reebotui.info.SaveEviroment;
import com.example.reebotui.interfaceclass.EventCallback;
import com.example.reebotui.util.AlertDialogUtil;
import com.example.reebotui.util.Encrypt;
import com.exp.rb.reebot.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JoinActivity extends AppCompatActivity implements View.OnClickListener, EventCallback {
    private static final String TAG = "ReeBot(JoinActivity)";

    public static final int EVENT_REQ_REMOTE_SIGNAL_SUCCESS = 10;
    public static final int EVENT_REQ_REMOTE_SIGNAL_FAIL = 11;
    public static final int EVENT_REQ_REMOTE_IRSIGNAL_SUCCESS = 13;
    public static final int EVENT_REQ_REMOTE_IRSIGNAL_FAIL = 14;
    public static final int EVENT_REQ_EMAIL_JOIN_SUCCESS = 15;
    public static final int EVENT_REQ_EMAIL_JOIN_FAIL = 16;
    public static final int EVENT_REQ_KAKAO_JOIN_SUCCESS = 17;

    private static final boolean demoversion = false;

    private ReeBotApi reeBotApi;

    private CheckBox cb_terms;
    private Dialog terms_dialog;
    private Spinner sp_cabletvbrend;
    private Spinner sp_tvbrand;
    private ArrayAdapter<String> adapter_catv;
    private ArrayAdapter<String> adapter_tv;

    private String tvbrend = "";
    private String catvbrend = "";

    private EditText et_email;
    private EditText et_pwd;
    private EditText et_pwd_check;

    private String email = "";
    private String atoken = "";
    private int RBIRGetSignalmode;
    private boolean isKakao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        Intent intent = getIntent();
        if (intent != null) {
            isKakao = intent.getBooleanExtra("isKakao", false);
            if (isKakao) {
                email = intent.getStringExtra("email");
                atoken = intent.getStringExtra("atoken");
            }

        }

        Log.d(TAG, "onCreate isKakao: " + isKakao);
        Log.d(TAG, "onCreate email: " + email);
        Log.d(TAG, "onCreate atoken: " + atoken);

        reeBotApi = new ReeBotApi(this);

        View btn_title_close = (ImageView) findViewById(R.id.btn_title_close);
        cb_terms = (CheckBox) findViewById(R.id.cb_terms);
        ImageView btn_terms = (ImageView) findViewById(R.id.btn_terms);
        sp_cabletvbrend = (Spinner) findViewById(R.id.sp_cabletvbrend);
        sp_tvbrand = (Spinner) findViewById(R.id.sp_tvbrand);
        et_email = (EditText) findViewById(R.id.et_email);

        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_pwd_check = (EditText) findViewById(R.id.et_pwd_check);

        if (isKakao) {
//            if (!AppUtil.isEmpty(email)) {
//
//            }

            et_email.setText(email);
            et_email.setClickable(false);
            et_email.setFocusable(false);
            et_email.setEnabled(false);
            et_email.clearFocus();
            findViewById(R.id.ll_pwd).setVisibility(View.GONE);
        }


        terms_dialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
        //final Dialog terms_dialog = new Dialog(this); // Context, this, etc.
        terms_dialog.setCanceledOnTouchOutside(false);
        terms_dialog.setContentView(R.layout.terms_view);

        final WebView terms_webview = (WebView) terms_dialog.findViewById(R.id.terms_web);
        terms_webview.setWebViewClient(new WebViewClient());
        terms_webview.getSettings().setJavaScriptEnabled(true);
        terms_webview.loadUrl("http://reebot.io:8083/reebot_terms.html");

        btn_title_close.setOnClickListener(this);
        btn_terms.setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        terms_dialog.findViewById(R.id.terms_disAgree).setOnClickListener(this);
        terms_dialog.findViewById(R.id.terms_Agree).setOnClickListener(this);


        adapter_catv = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item) {
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

        adapter_catv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_cabletvbrend.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.d(TAG, "TV onItemSelected : ");
                Object item = parent.getItemAtPosition(position);
                if (item != null) {

                    if ((item.toString().contains("_"))) {
                        catvbrend = item.toString().split("_")[1];
//                        catvbrend = catvbrend.replaceAll("[0-9]", ""); //숫자 제거
                    } else {
                        catvbrend = item.toString();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapter_tv = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item) {
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

        adapter_tv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_tvbrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                if (item != null) {
                    if ((item.toString().contains("_"))) {
                        tvbrend = item.toString().split("_")[1];
//                        tvbrend = tvbrend.replaceAll("[0-9]", ""); //숫자 제거
                    } else {
                        tvbrend = item.toString();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        reqSignalList();
        reqTvList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_title_close:
                finish();
                break;
            case R.id.btn_confirm:
                signin();
                break;
            case R.id.btn_terms:
                terms_dialog.show();
                break;
            case R.id.terms_disAgree:
                cb_terms.setChecked(false);
                terms_dialog.dismiss();
                break;
            case R.id.terms_Agree:
                cb_terms.setChecked(true);
                terms_dialog.dismiss();
                break;
        }
    }

    private void reqSignalList() {
        reeBotApi.reqSignalList(new RBIRParamData(ExtraInfo.REQ_MODE_REMOTE_SIGNAL, "", "signalsbox"));
//        reeBotApi.reqSignalList(ExtraInfo.REQ_MODE_REMOTE_SIGNAL, "signalsbox");
    }

    private void reqTvList() {
        reeBotApi.reqSignalList(new RBIRParamData(ExtraInfo.REQ_MODE_REMOTE_SIGNAL, "", "signaltv"));
//        reeBotApi.reqSignalList(ExtraInfo.REQ_MODE_REMOTE_SIGNAL, "signaltv");
    }


    @Override
    public void onEvent(int event, String msg) {
        Log.d(TAG, "onEvent event: " + event);
        Log.d(TAG, "onEvent msg: " + msg);

        switch (event) {
            case EVENT_REQ_REMOTE_SIGNAL_SUCCESS:
                rBIRGetListSignaTaskResult(msg);
                break;
            case EVENT_REQ_REMOTE_SIGNAL_FAIL:
                break;
            case EVENT_REQ_REMOTE_IRSIGNAL_SUCCESS:
                rBIRGetSignalTaskResult(msg);
                break;
            case EVENT_REQ_REMOTE_IRSIGNAL_FAIL:

                break;
            case EVENT_REQ_EMAIL_JOIN_SUCCESS:
            case EVENT_REQ_KAKAO_JOIN_SUCCESS:
                rBAuthSinginTaskResult(msg);
                break;

        }
    }

    private void rBIRGetListSignaTaskResult(String msg) {
        Log.d(TAG, "RBIRGetListSignaTaskResult : " + msg);
        try {
            JSONObject jsonobject = new JSONObject(msg);
            if (jsonobject.getBoolean("type") == true) {
                Log.d(TAG, "RBIRGetListSignaTaskResult2 : " + jsonobject.getString("type2"));
                if ("signaltv".equals(jsonobject.getString("type2"))) {
                    if (makelist(jsonobject.getJSONArray("data"), adapter_tv) == true) {
                        adapter_tv.add("TV 제조사 선택");
                        sp_tvbrand.setAdapter(adapter_tv);
                        sp_tvbrand.setSelection(adapter_tv.getCount());
                    }

                } else {
                    if (makelist(jsonobject.getJSONArray("data"), adapter_catv) == true) {
                        adapter_catv.add("TV 방송 사업자 선택");
                        sp_cabletvbrend.setAdapter(adapter_catv);
                        sp_cabletvbrend.setSelection(adapter_catv.getCount());
                    }

                }

            } else {
                //리스트 가져오기 실패
                AlertDialogUtil.showDialog(this, "리스트를 가져오는데 실패했습니다. 다시 시도해주세요.");
            }
        } catch (Exception e) {
            //통신 상태를 확인해주세요.
            AlertDialogUtil.showDialog(this, "인터넷 연결 상태를 확인해주세요.");
//            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(initActivity);
//            alert_confirm.setMessage("인터넷 연결 상태를 확인해주세요.").setNegativeButton("확인",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            loginlayout.setVisibility(View.INVISIBLE);
//                            regmlayout.setVisibility(View.INVISIBLE);
//                            reglayout.setVisibility(View.INVISIBLE);
//                            envslayout.setVisibility(View.INVISIBLE);
//                            loginlayout.setVisibility(View.VISIBLE);
//                            return;
//                        }
//                    });
//            AlertDialog alert = alert_confirm.create();
//            alert.show();
        }
    }

    private boolean makelist(JSONArray jArray, ArrayAdapter<String> adapter) {
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

    private void signin() {

        //약관체크
        if (!cb_terms.isChecked()) {
            AlertDialogUtil.showDialog(this, "약관을 동의 해주세요.");
//            Toast.makeText(initActivity, "약관을 동의 해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        //조건 체크
        if (tvbrend == null || tvbrend.isEmpty() || "TV 제조사 선택".equals(tvbrend)) {
//            Toast.makeText(initActivity, "TV 제조사를 선택해 주세요.", Toast.LENGTH_SHORT).show();
            AlertDialogUtil.showDialog(this, "TV 제조사를 선택해 주세요.");
            return;
        }

        if (catvbrend == null || catvbrend.isEmpty() || "TV 방송 사업자 선택".equals(catvbrend)) {
//            Toast.makeText(initActivity, "TV 방송 사업자 선택를 선택해 주세요.", Toast.LENGTH_SHORT).show();
            AlertDialogUtil.showDialog(this, "TV 방송 사업자 선택를 선택해 주세요.");
            return;
        }


        email = et_email.getText().toString();
        String pwd = et_pwd.getText().toString();
        String pwdCheck = et_pwd_check.getText().toString();

        if (isKakao) {
            if (email.isEmpty()) {
                AlertDialogUtil.showDialog(this, "EMAIL를 입력해주세요.");
                return;
            } else {
                if (!email.contains("@") || !email.contains(".")) {
                    AlertDialogUtil.showDialog(this, "EMAIL을 정확히 입력해주세요.");
                    return;
                } else {
                    FirebaseMessaging.getInstance().subscribeToTopic("reebot");
                    String pushToken = FirebaseInstanceId.getInstance().getToken();

                    reeBotApi.authToken(new RBAuthData(ExtraInfo.AUTH_MODE_KAKAO_JOIN, email, "", "", tvbrend, catvbrend, pushToken, "", "", "", atoken));
//                    rbAuthManager.execute(new RBAuthData(6, remail, "", "", tvbrend, catvbrend, token, "", signinurl, "", atoken));
                }
            }
        } else {
            if (email.isEmpty() || pwd.isEmpty() || pwdCheck.isEmpty()) {
                if (email.isEmpty() && pwd.isEmpty()) {
                    AlertDialogUtil.showDialog(this, "EMAIL , PASSWD를 입력해주세요.");
                } else if (email.isEmpty()) {
                    AlertDialogUtil.showDialog(this, "EMAIL를 입력해주세요.");
                } else {
                    AlertDialogUtil.showDialog(this, "PASSWD를 입력해주세요.");
                }
                return;
            } else {
                if (!email.contains("@") || !email.contains(".")) {
                    AlertDialogUtil.showDialog(this, "EMAIL을 정확히 입력해주세요.");
                    return;
                } else if (pwd.length() < 6) {
                    AlertDialogUtil.showDialog(this, "PASSWORD을 정확히 입력해주세요.");
                    return;
                } else {
                    if (pwd.equals(pwdCheck)) {

                        String encPwd = Encrypt.SHA256(pwd);
                        if (encPwd == null || encPwd.isEmpty()) {
                            AlertDialogUtil.showDialog(this, "PASSWORD 오류. 다시 시도해주세요.");
                            return;
                        }

                        FirebaseMessaging.getInstance().subscribeToTopic("reebot");
                        String pushToken = FirebaseInstanceId.getInstance().getToken();

                        reqEmailJoin(new RBAuthData(ExtraInfo.AUTH_MODE_EMAIL_JOIN, email, encPwd, "", tvbrend, catvbrend, pushToken, "", "", ""));
//                    rbAuthManager.execute(new RBAuthData(2, remail, encPwd, "", tvbrend, catvbrend, token, "", signinurl, ""));

                    } else {
                        AlertDialogUtil.showDialog(this, "PASSWORD을 같게 입력해주세요.");
                        return;
                    }
                }
            }

        }
        //regimember


//        EditText temail = (EditText) findViewById(R.id.editemail);
//        remail = temail.getText().toString();


//        RBAuthManager rbAuthManager = new RBAuthManager(initActivity);
//        if ("kakao".equals(regmode)) {
//            //카카로 회원 가입
//            Log.d(TAG, "atoken : " + atoken);
//            rbAuthManager.execute(new RBAuthData(6, remail, "", "", tvbrend, catvbrend, token, "", signinurl, "", atoken));
//        } else {
//
//        }
    }

    private void reqEmailJoin(RBAuthData rbAuthData) {
        reeBotApi.authToken(rbAuthData);
    }

    private void rBAuthSinginTaskResult(String msg) {
        Log.d(TAG, "RBAuthSinginTaskResult : " + msg);
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
            final String accesstoken = token;
            //로그인 정보 저장
            SaveEviroment.setUserName(getApplicationContext(), email, token);
            SaveEviroment.setUserEnv(getApplicationContext(), tvbrend, catvbrend, "");
            //회원가입 완료

            //내장 IR파악 .
            ConsumerIrManager mCIR = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);
            boolean sirsensor = false;
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
            final boolean fsirsensor = sirsensor;
            final Dialog dialog = new Dialog(this); // Context, this, etc.
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.commember);
            TextView content = (TextView) dialog.findViewById(R.id.dialog_info);
            Button btn_cancle = (Button) dialog.findViewById(R.id.cmdialog_cancel);
            Button btn_ok = (Button) dialog.findViewById(R.id.cmdialog_ok);
            if (sirsensor == true) {
                dialog.setTitle("회원가입 완료");
                content.setText("내장 리모컨 사용이 가능 합니다.");
                btn_cancle.setText("내장리모컨 사용");
                btn_ok.setText("외장 장치 추가(예정)");
            } else {
                dialog.setTitle("회원가입 완료");
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
                    if (fsirsensor == true) {
                        //다운로드 셋탑 시그널
                        RBIRGetSignalmode = 1;
                        String[] separated = email.split("@");


                        reeBotApi.reqSignalList(new RBIRParamData(ExtraInfo.REQ_MODE_REMOTE_IR_SIGNAL_SBOX, "", separated[0], "room1", "signalsbox", catvbrend, ""));
                        reeBotApi.reqSignalList(new RBIRParamData(ExtraInfo.REQ_MODE_REMOTE_IR_SIGNAL_TV, "", separated[0], "room1", "signaltv", tvbrend, ""));

//                        RBIRGetSignal catv_signal = new RBIRGetSignal(initActivity);
//                        catv_signal.execute(new RBIRParamData(2, getsignalurl, separated[0], "room1", "signalsbox", catvbrend, ""));
//                        //다운로드 TV시그널
//                        RBIRGetSignal tv_signal = new RBIRGetSignal(initActivity);
//                        tv_signal.execute(new RBIRParamData(2, getsignalurl, separated[0], "room1", "signaltv", tvbrend, ""));
                    } else {


                    }
                    dialog.dismiss();


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }, 300);



//                    //메인창으로 진입 EPG만 OR 내장 IR로 가능
//                    Intent mainintent = new Intent(getBaseContext(), MainActivity.class);
//                    //내장 IR여부에 따라 플래그 설정 틀리게
//                    mainintent.putExtra("email", email); //Optional parameters
//                    if (fsirsensor == true)
//                        mainintent.putExtra("sid", "nodevice"); //Optional parameters
//                    else
//                        mainintent.putExtra("sid", "rebot_onlyepg"); //Optional parameters
//                    mainintent.putExtra("tvb", tvbrend); //Optional parameters //PREF_USER_TVBREND
//                    mainintent.putExtra("catvb", catvbrend); //Optional parameters //PREF_USER_TVBREND
//                    mainintent.putExtra("acesstoken", accesstoken); //Optional parameters //PREF_USER_TOKEN
//                    startActivityForResult(mainintent, 101);


                }
            });
            //Button cmdialogOKButton = (Button) dialog.findViewById(R.id.cmdialog_ok);
            // if button is clicked, close the custom dialog
            final Dialog reebot_intro_dialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //다운로드 셋탑 시그널
                    RBIRGetSignalmode = 2;
                    String[] separated = email.split("@");

                    //장치 나올때까지만 막는다

                    if (demoversion == true) {
                        reeBotApi.reqSignalList(new RBIRParamData(ExtraInfo.REQ_MODE_REMOTE_IR_SIGNAL_SBOX, "", separated[0], "room1", "signalsbox", catvbrend, ""));
//                        RBIRGetSignal signal = new RBIRGetSignal(initActivity);
//                        signal.execute(new RBIRParamData(2, getsignalurl, separated[0], "room1", "signalsbox", catvbrend, ""));
                    }

                    //다운로드 TV시그널
                    //내장 IR 시그널 파일 다운로드 TV 및 셋톱박스
                    if (fsirsensor == true) {

                    }
                    dialog.dismiss();
                    // setEnvironment();


                    if (demoversion != true) {
                        //장치 나올때까지만 풀어놓는다.
                        //광고 페이지
                        reebot_intro_dialog.setCanceledOnTouchOutside(false);
                        reebot_intro_dialog.setContentView(R.layout.reebot_intro_view);
                        final WebView terms_webview = (WebView) reebot_intro_dialog.findViewById(R.id.intro_web);
                        terms_webview.setWebViewClient(new WebViewClient());
                        terms_webview.getSettings().setJavaScriptEnabled(true);
                        terms_webview.loadUrl("http://reebot.io:8083/reebot_intro.html");
                        reebot_intro_dialog.show();
                        ((Button) reebot_intro_dialog.findViewById(R.id.intro_ok)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                reebot_intro_dialog.dismiss();

//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        //메인창으로 진입 EPG만 OR 내장 IR로 가능
//                                        Intent mainintent = new Intent(getBaseContext(), MainActivity.class);
//                                        //내장 IR여부에 따라 플래그 설정 틀리게
//                                        mainintent.putExtra("email", email); //Optional parameters
//                                        if (fsirsensor == true)
//                                            mainintent.putExtra("sid", "nodevice"); //Optional parameters
//                                        else
//                                            mainintent.putExtra("sid", "rebot_onlyepg"); //Optional parameters
//                                        mainintent.putExtra("tvb", tvbrend); //Optional parameters //PREF_USER_TVBREND
//                                        mainintent.putExtra("catvb", catvbrend); //Optional parameters //PREF_USER_TVBREND
//                                        mainintent.putExtra("acesstoken", accesstoken); //Optional parameters //PREF_USER_TOKEN
//                                        startActivityForResult(mainintent, ExtraInfo.INTENT_CODE_MAIN);
//                                    }
//                                }, 300);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                }, 300);

                            }
                        });
                    }
                }
            });
        } else {
            AlertDialogUtil.showDialog(this, "이미 가입된 회원 정보입니다.");
//            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(initActivity);
//            alert_confirm.setMessage("이미 가입된 회원 정보입니다.").setNegativeButton("확인",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            return;
//                        }
//                    });
//            AlertDialog alert = alert_confirm.create();
//            alert.show();
        }
    }

    private void rBIRGetSignalTaskResult(String msg) {
        Log.d(TAG, "rBIRGetSignalTaskResult RBIRGetSignalmode: " + RBIRGetSignalmode);
        if (msg.equals("error")) {
        } else {
            try {
                JSONArray jArray = new JSONArray(msg);
                if (jArray != null) {
                    if (RBIRGetSignalmode == 2) {
//                        setEnvironment();
                    }

                } else {
                    //리모콘 신호 가져오기 실패 다이얼로그 추가
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }
}
