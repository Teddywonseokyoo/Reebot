package com.example.reebotui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reebotui.api.ReeBotApi;
import com.example.reebotui.info.ExtraInfo;
import com.example.reebotui.info.RBAuthData;
import com.example.reebotui.info.SaveEviroment;
import com.example.reebotui.interfaceclass.EventCallback;
import com.example.reebotui.util.AlertDialogUtil;
import com.example.reebotui.util.Encrypt;
import com.exp.rb.reebot.R;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class InitActivity extends AppCompatActivity implements EventCallback {
    private static final String TAG = "ReeBot(InitActivity)";

    public static final int EVENT_REQ_CHECK_VERSION_SUCCESS = 0;
    public static final int EVENT_REQ_CHECK_VERSION_FAIL = 1;
    public static final int EVENT_REQ_CHECK_TOKEN_SUCCESS = 3;
    public static final int EVENT_REQ_CHECK_TOKEN_FAIL = 4;
    public static final int EVENT_REQ_EMAIL_LOGIN_SUCCESS = 5;
    public static final int EVENT_REQ_EMAIL_LOGIN_FAIL = 6;

    public static final int EVENT_REQ_KAKAO_AUTH_SUCCESS = 7;
    public static final int EVENT_REQ_KAKAO_AUTH_FAIL = 8;

    private ReeBotApi reeBotApi;

    private boolean passflag = false;
    private String ldata[];
    private String accesstoken = "";

    private String RbLaunchType;
    private String BookingBody;
    private String BookingChnum;

    private View cl_login;
    private EditText et_email;
    private EditText et_pwd;

    private String email;
    private String atoken;
    private SessionCallback sessionCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("getKeyHash: " + getKeyHash(this));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if ("booking".equals(bundle.getString("RbLaunchType"))) {
                RbLaunchType = bundle.getString("RbLaunchType");
                BookingBody = bundle.getString("BookingBody");
                BookingChnum = bundle.getString("BookingChnum");
            }
        }

        setContentView(R.layout.activity_init);

        cl_login = findViewById(R.id.cl_login);
        cl_login.setVisibility(View.INVISIBLE);


        reeBotApi = new ReeBotApi(this);
        reeBotApi.checkVersion(this);

        //kakao
        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            Log.d(TAG, "onActivityResult kakao session");
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ExtraInfo.INTENT_CODE_JOIN) {
            if (resultCode == RESULT_OK) {
                checkToken();
            }
        } else if (requestCode == ExtraInfo.INTENT_CODE_MAIN) {
            if (data != null) {
                String returnValue = data.getStringExtra("control");
                if (returnValue.equals("erasedata")) {
                    //카카오 로그아웃
                    SaveEviroment.delEnvData(InitActivity.this);
                    setLoginUi();
                } else {
                    finish();
                }
            } else {
                finish();
            }

        }
    }

    private void setLoginUi() {
        Log.d(TAG, "setLoginUi");
        cl_login.setVisibility(View.VISIBLE);

        et_email = (EditText) findViewById(R.id.et_email);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_pwd.setText("");
        View btn_join = findViewById(R.id.btn_join);

        et_pwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    email = et_email.getText().toString();
                    final String pwd = et_pwd.getText().toString();

                    if (email.isEmpty() || pwd.isEmpty()) {
                        AlertDialogUtil.showDialog(InitActivity.this, getString(R.string.error_input_email_pwd));
                    } else {
                        if (!email.contains("@") || !email.contains(".")) {
                            AlertDialogUtil.showDialog(InitActivity.this, getString(R.string.error_input_wrong_email));
                        } else if (pwd.length() < 6) {
                            AlertDialogUtil.showDialog(InitActivity.this, getString(R.string.error_input_wrong_pwd));
                        } else {

                            String encPwd = Encrypt.SHA256(pwd);
                            if (encPwd == null || encPwd.isEmpty()) {
                                AlertDialogUtil.showDialog(InitActivity.this, getString(R.string.error_encrypt_pwd));
                            } else {
                                reqEmailLogin(new RBAuthData(ExtraInfo.AUTH_MODE_EMAIL_LOGIN, email, encPwd, "", "", "", "", "", "", ""));
                            }
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveJoin(false);
            }
        });

    }

    private void moveJoin(boolean isKakao) {
        Intent intent = new Intent(InitActivity.this, JoinActivity.class);
        intent.putExtra("isKakao", isKakao);
        if (isKakao) {
            intent.putExtra("email", email);
            intent.putExtra("atoken", atoken);
        }
        startActivityForResult(intent, ExtraInfo.INTENT_CODE_JOIN);
    }

    @Override
    public void onEvent(int event, String msg) {
        Log.d(TAG, "onEvent event: " + event);
        Log.d(TAG, "onEvent msg: " + msg);

        switch (event) {
            case EVENT_REQ_CHECK_VERSION_SUCCESS:
                checkToken();
                break;
            case EVENT_REQ_CHECK_VERSION_FAIL:
                Toast.makeText(this, "check version error: " + msg, Toast.LENGTH_SHORT).show();
                break;
            case EVENT_REQ_CHECK_TOKEN_SUCCESS:
                rBAuthChecktokenTaskResult(msg, false);
                break;
            case EVENT_REQ_CHECK_TOKEN_FAIL:
                setLoginUi();
                break;
            case EVENT_REQ_EMAIL_LOGIN_SUCCESS:
                rBAuthLoginTaskResult(msg);
                break;
            case EVENT_REQ_EMAIL_LOGIN_FAIL:
                break;
            case EVENT_REQ_KAKAO_AUTH_SUCCESS:
                rBAuthChecktokenTaskResult(msg, true);
                break;
            case EVENT_REQ_KAKAO_AUTH_FAIL:
                setLoginUi();
                break;
        }
    }

    private void checkToken() {
//        passflag = true;

        ldata = SaveEviroment.getUserLoginData(getApplicationContext());
        Log.d(TAG, "PREF_USER_EMAIL : " + ldata[0]);
        Log.d(TAG, "PREF_USER_CODE : " + ldata[1]);

        if (ldata[1] != null) {// && passflag == true) {
            accesstoken = ldata[1];
            Log.d(TAG, "TOKEN : " + accesstoken);
            if (!ldata[1].isEmpty()) {

                reeBotApi.authToken(new RBAuthData(ExtraInfo.AUTH_MODE_CHECK_TOKEN, "", "", "", "", "", "", "", "", accesstoken));
            } else {
                //토큰없음
                setLoginUi();
            }
        } else {
            setLoginUi();
        }
        passflag = true;
    }

    private void reqEmailLogin(RBAuthData rbAuthData) {
        reeBotApi.authToken(rbAuthData);
    }

    private void rBAuthChecktokenTaskResult(String msg, boolean isKakao) {

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
                Log.d(TAG, "rBAuthChecktokenTaskResult type: " + type);
                if (type) {
//                    String accesstoken = "";
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
                    if (!"nomal".equals(jObject.getString("type2")))
                        accesstoken = jObject.getString("token");
                    Log.d(TAG, "RBAuthLoginTaskResult : " + msg);
                    Log.d(TAG, "Auth ok");
                    Intent mainintent = new Intent(InitActivity.this, MainActivity.class);
                    String[] separated = email.split("@");
                    String pemail = separated[0]; // this will contain "Fruit"
                    mainintent.putExtra("email", email); //Optional parameters
                    mainintent.putExtra("sid", rbtoken); //Optional parameters
                    mainintent.putExtra("tvb", tvbrend); //Optional parameters
                    mainintent.putExtra("catvb", catvvendor); //Optional parameters
                    mainintent.putExtra("acesstoken", accesstoken); //Optional parameters //PREF_USER_TOKEN
                    if ("booking".equals(RbLaunchType)) {
                        mainintent.putExtra("RbLaunchType", RbLaunchType);
                        mainintent.putExtra("BookingBody", BookingBody);
                        mainintent.putExtra("BookingChnum", BookingChnum);
                    }
                    startActivityForResult(mainintent, ExtraInfo.INTENT_CODE_MAIN);
                    return;
                } else {
                    //토큰검증 실패
                    if (jObject.getString("data").equals("not found user")) {
                        Log.d(TAG, "카카오 토큰 검증 후 회원 가입 요망");
//                        regMember(2, lemail);
                        if (isKakao) {
                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            moveJoin(true);
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:

                                            break;
                                    }
                                }
                            };

                            AlertDialogUtil.showDialog(InitActivity.this, "", "회원정보가 없습니다. 회원 가입 후 이용해주세요.(kakao)", "취소", "회원가입", onClickListener);
                            Session.getCurrentSession().close();
                        }
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setLoginUi();
    }

    private void rBAuthLoginTaskResult(String msg) {
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
                    Log.d(TAG, "RBAuthLoginTaskResult : " + msg);
                    Log.d(TAG, "Auth ok");

                    Intent mainintent = new Intent(InitActivity.this, MainActivity.class);
                    //String[] separated = lemail.split("@");
                    //String pemail = separated[0]; // this will contain "Fruit"
                    mainintent.putExtra("email", email); //Optional parameters
                    mainintent.putExtra("sid", rbtoken); //Optional parameters
                    mainintent.putExtra("tvb", tvbrend); //Optional parameters
                    mainintent.putExtra("catvb", catvvendor); //Optional parameters
                    mainintent.putExtra("acesstoken", accesstoken); //Optional parameters //PREF_USER_TOKEN
                    if ("booking".equals(RbLaunchType)) {
                        mainintent.putExtra("RbLaunchType", RbLaunchType);
                        mainintent.putExtra("BookingBody", BookingBody);
                        mainintent.putExtra("BookingChnum", BookingChnum);


                    }
                    startActivityForResult(mainintent, ExtraInfo.INTENT_CODE_MAIN);
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
                SaveEviroment.setUserName(getApplicationContext(), email, token);
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

                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
//                                if ("nomal".equals(regmode)) {
//                                    regMember(1, "");
//                                } else {
//                                }
                                moveJoin(false);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

                AlertDialogUtil.showDialog(InitActivity.this, "", "회원정보가 없습니다. 회원 가입 후 이용해주세요.", "취소", "회원가입", onClickListener);

//                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(initActivity);
//                alert_confirm.setMessage("회원정보가 없습니다.").setCancelable(false).setPositiveButton("회원가입",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if ("nomal".equals(regmode)) {
//                                    regMember(1, "");
//                                } else {
//                                }
//                            }
//                        }).setNegativeButton("취소",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if ("nomal".equals(regmode)) {
//                                    return;
//                                } else {
//                                    loginlayout.setVisibility(View.INVISIBLE);
//                                    regmlayout.setVisibility(View.INVISIBLE);
//                                    reglayout.setVisibility(View.INVISIBLE);
//                                    envslayout.setVisibility(View.INVISIBLE);
//                                    loginlayout.setVisibility(View.VISIBLE);
//                                }
//                            }
//                        });
//                AlertDialog alert = alert_confirm.create();
//                alert.show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //kakao
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            Log.d(TAG, "SessionCallback onSessionOpened");
            requestKakao();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.d(TAG, "SessionCallback Session Fail Error is " + exception.getMessage().toString());
        }
    }

    //카카오
    public void requestKakao() {

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

//                Log.d(TAG, "getAccessToken : " + Session.getCurrentSession().getAccessToken());
                Log.d(TAG, "getAccessToken : " + Session.getCurrentSession().getTokenInfo().getAccessToken());
//                Log.d(TAG, "RefreshToken :" + Session.getCurrentSession().getRefreshToken());
                Log.d(TAG, "RefreshToken :" + Session.getCurrentSession().getTokenInfo().getRefreshToken());
                atoken = Session.getCurrentSession().getTokenInfo().getAccessToken();
                email = result.getEmail();

                Log.d(TAG, "request onSuccess getNickname: " + result.getNickname());
                Log.d(TAG, "request onSuccess getUUID: " + result.getUUID());
                Log.d(TAG, "request onSuccess getEmailVerified: " + result.getEmailVerified());
                Log.d(TAG, "request onSuccess getServiceUserId: " + result.getServiceUserId());
                Log.d(TAG, "request onSuccess getProperties: " + result.getProperties().toString());

                //로그인 시도 없으면 회원 가입 페이지로 던짐
                //회원 가입 페이지에 이메일 파라미터 던짐
                //비밀번호 환성화 X 비밀번호 체크 X

                //카카오 토큰 검증
                reeBotApi.authToken(new RBAuthData(ExtraInfo.AUTH_MODE_KAKAO_AUTH, email, "", "", "", "", "", "", "", atoken));
//                if (passflag == true)
//                {
//
//                    reeBotApi.authToken(new RBAuthData(ExtraInfo.AUTH_MODE_KAKAO_AUTH, email, "", "", "", "", "", "", "", atoken));
//
////                    RBAuthManager rbAuthManager = new RBAuthManager(initActivity);
////                    rbAuthManager.execute(new RBAuthData(5, lemail, "", "", "", "", "", "", checkstokenurl, Session.getCurrentSession().getAccessToken()));
//                } else {
//                    Session.getCurrentSession().close();
//                }
                //onClickAccessTokenInfo();
            }
        });
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
