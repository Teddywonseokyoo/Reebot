package com.example.reebotui;

import android.app.Dialog;
import android.content.Intent;
import android.hardware.ConsumerIrManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.Toast;

import com.example.reebotui.api.ReeBotApi;
import com.example.reebotui.info.RBAuthData;
import com.example.reebotui.info.RBIRParamData;
import com.example.reebotui.interfaceclass.JoinCallback;
import com.example.reebotui.interfaceclass.SignalCallback;
import com.example.reebotui.util.AlertDialogUtil;
import com.example.reebotui.util.Encrypt;
import com.exp.rb.reebot.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class JoinActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ReeBot(JoinActivity)";

//    public static final int EVENT_REQ_REMOTE_SIGNAL_SUCCESS = 10;
//    public static final int EVENT_REQ_REMOTE_SIGNAL_FAIL = 11;
//    public static final int EVENT_REQ_REMOTE_IRSIGNAL_SUCCESS = 13;
//    public static final int EVENT_REQ_REMOTE_IRSIGNAL_FAIL = 14;
//    public static final int EVENT_REQ_EMAIL_JOIN_SUCCESS = 15;
//    public static final int EVENT_REQ_EMAIL_JOIN_FAIL = 16;
//    public static final int EVENT_REQ_KAKAO_JOIN_SUCCESS = 17;

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

        reeBotApi.reqSignalList(new RBIRParamData("", "signalsbox"), signalCallback);
        reeBotApi.reqSignalList(new RBIRParamData("", "signaltv"), signalCallback);
//        reqSignalList();
//        reqTvList();
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

    private JoinCallback joinCallback = new JoinCallback() {

        @Override
        public void joinResult(boolean result, String msg, RBAuthData rbAuthData) {
            if (result) {
                resJoin();
            } else {
                AlertDialogUtil.showDialog(JoinActivity.this, "이미 가입된 회원 정보입니다.");
            }
        }

    };

    private SignalCallback signalCallback = new SignalCallback() {
        @Override
        public void getSignalList(boolean result, String msg, ArrayList<String> list) {
            if (result) {
                if ("signaltv".equals(msg)) {
                    adapter_tv.addAll(list);
                    adapter_tv.add("TV 제조사 선택");
                    sp_tvbrand.setAdapter(adapter_tv);
                    sp_tvbrand.setSelection(adapter_tv.getCount());
                } else {
                    adapter_catv.addAll(list);
                    adapter_catv.add("TV 방송 사업자 선택");
                    sp_cabletvbrend.setAdapter(adapter_catv);
                    sp_cabletvbrend.setSelection(adapter_catv.getCount());
                }

            } else {
                AlertDialogUtil.showDialog(JoinActivity.this, msg);
            }
        }

        @Override
        public void getIRSignal(boolean result, String msg) {
            if (result) {
            } else {
                Toast.makeText(JoinActivity.this, "ir signal error: " + msg, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void changeService(boolean result, String msg) {

        }
    };

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

                    reeBotApi.reqKakaoJoin(new RBAuthData(email, "", "", tvbrend, catvbrend, pushToken, "", "", "", atoken), joinCallback);
//                    reeBotApi.authToken(new RBAuthData(ExtraInfo.AUTH_MODE_KAKAO_JOIN, email, "", "", tvbrend, catvbrend, pushToken, "", "", "", atoken));
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

                        reeBotApi.reqEmailJoin(new RBAuthData(email, encPwd, "", tvbrend, catvbrend, pushToken, "", "", ""), joinCallback);
//                        reqEmailJoin(new RBAuthData(ExtraInfo.AUTH_MODE_EMAIL_JOIN, email, encPwd, "", tvbrend, catvbrend, pushToken, "", "", ""));
//                    rbAuthManager.execute(new RBAuthData(2, remail, encPwd, "", tvbrend, catvbrend, token, "", signinurl, ""));

                    } else {
                        AlertDialogUtil.showDialog(this, "PASSWORD을 같게 입력해주세요.");
                        return;
                    }
                }
            }

        }
        //regimember

    }


    private void resJoin() {
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

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //내장 IR 시그널 파일 다운로드 TV 및 셋톱박스
                if (fsirsensor == true) {
                    //다운로드 셋탑 시그널
                    RBIRGetSignalmode = 1;
                    String[] separated = email.split("@");

                    reeBotApi.reqIRSignal(new RBIRParamData("", separated[0], "room1", "signalsbox", catvbrend, ""), signalCallback);
                    reeBotApi.reqIRSignal(new RBIRParamData("", separated[0], "room1", "signaltv", tvbrend, ""), signalCallback);
//                    reeBotApi.reqSignalList(new RBIRParamData(ExtraInfo.REQ_MODE_REMOTE_IR_SIGNAL_SBOX, "", separated[0], "room1", "signalsbox", catvbrend, ""));
//                    reeBotApi.reqSignalList(new RBIRParamData(ExtraInfo.REQ_MODE_REMOTE_IR_SIGNAL_TV, "", separated[0], "room1", "signaltv", tvbrend, ""));

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

            }
        });

        final Dialog reebot_intro_dialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //다운로드 셋탑 시그널
                RBIRGetSignalmode = 2;
                String[] separated = email.split("@");

                //장치 나올때까지만 막는다

                if (demoversion == true) {
//                    reeBotApi.reqSignalList(new RBIRParamData(ExtraInfo.REQ_MODE_REMOTE_IR_SIGNAL_SBOX, "", separated[0], "room1", "signalsbox", catvbrend, ""));
                    reeBotApi.reqIRSignal(new RBIRParamData("", separated[0], "room1", "signalsbox", catvbrend, ""), signalCallback);
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
    }

    public static class DeviceActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_device);
        }
    }
}
