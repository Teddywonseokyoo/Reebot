package com.example.reebotui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reebotui.api.ReeBotApi;
import com.example.reebotui.info.ExtraInfo;
import com.example.reebotui.info.SaveEviroment;
import com.example.reebotui.info.UserInfo;
import com.example.reebotui.interfaceclass.InitCallback;
import com.example.reebotui.util.AlertDialogUtil;
import com.example.reebotui.util.AppUtil;
import com.example.reebotui.util.Encrypt;
import com.exp.rb.reebot.R;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

public class InitActivity extends AppCompatActivity {
    private static final String TAG = "ReeBot(InitActivity)";
    //    public static final int EVENT_REQ_CHECK_VERSION_SUCCESS = 0;
//    public static final int EVENT_REQ_CHECK_VERSION_FAIL = 1;
//    public static final int EVENT_REQ_CHECK_TOKEN_SUCCESS = 3;
//    public static final int EVENT_REQ_CHECK_TOKEN_FAIL = 4;
//    public static final int EVENT_REQ_EMAIL_LOGIN_SUCCESS = 5;
//    public static final int EVENT_REQ_EMAIL_LOGIN_FAIL = 6;
//    public static final int EVENT_REQ_KAKAO_AUTH_SUCCESS = 7;
//    public static final int EVENT_REQ_KAKAO_AUTH_FAIL = 8;

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

        Intent intent = new Intent(InitActivity.this, DeviceActivity.class);
        intent.putExtra("email", email); //Optional parameters
        intent.putExtra("sid", ""); //Optional parameters
        startActivityForResult(intent, ExtraInfo.INTENT_CODE_DEVICE);

        /*
        //System.out.println("getKeyHash: " + AppUtil.getKeyHash(this));

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
        reeBotApi.checkVersion(this, initCallback);

        //kakao
        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        //Session.getCurrentSession().checkAndImplicitOpen();

        */
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

    private InitCallback initCallback = new InitCallback() {
        @Override
        public void checkVersionResult(boolean result, String msg) {
            if (result) {
                checkToken();
            } else {
                Toast.makeText(InitActivity.this, "check version error: " + msg, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void checkTokenResult(boolean result, String msg, UserInfo userInfo, boolean isKakao) {
            if (result) {
                Intent mainintent = new Intent(InitActivity.this, MainActivity.class);
                if (!AppUtil.isEmpty(userInfo.getAccesstoken())) {
                    accesstoken = userInfo.getAccesstoken();
                }
                if(!isKakao)
                {
                    email = userInfo.getEmail();
                }
                mainintent.putExtra("email", email); //Optional parameters
                mainintent.putExtra("sid", userInfo.getRbtoken()); //Optional parameters
                mainintent.putExtra("tvb", userInfo.getTvbrend()); //Optional parameters
                mainintent.putExtra("catvb", userInfo.getCatvvendor()); //Optional parameters
                mainintent.putExtra("accesstoken", accesstoken); //Optional parameters //PREF_USER_TOKEN

                if ("booking".equals(RbLaunchType)) {
                    mainintent.putExtra("RbLaunchType", RbLaunchType);
                    mainintent.putExtra("BookingBody", BookingBody);
                    mainintent.putExtra("BookingChnum", BookingChnum);
                }

                startActivityForResult(mainintent, ExtraInfo.INTENT_CODE_MAIN);
            } else {

                if (isKakao) {
                    if ("not found user".equals(msg)) {
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

                    } else if ("not match user".equals(msg)) {
                        AlertDialogUtil.showDialog(InitActivity.this, "로그인 실패. 다시 시도해주세요.");
                        UserManagement.requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {
                                Log.d(TAG, "KAKAO_LOGOUT : OK");
                            }
                        });
                    }

                    Session.getCurrentSession().close();
                }

                Toast.makeText(InitActivity.this, "check token error: " + msg, Toast.LENGTH_SHORT).show();
                setLoginUi();
            }
        }

        @Override
        public void emailLoginResult(boolean result, String msg, UserInfo userInfo) {
            if (result) {

                Intent mainintent = new Intent(InitActivity.this, MainActivity.class);
                //String[] separated = lemail.split("@");
                //String pemail = separated[0]; // this will contain "Fruit"
                mainintent.putExtra("email", email); //Optional parameters
                mainintent.putExtra("sid", userInfo.getRbtoken()); //Optional parameters
                mainintent.putExtra("tvb", userInfo.getTvbrend()); //Optional parameters
                mainintent.putExtra("catvb", userInfo.getCatvvendor()); //Optional parameters
                mainintent.putExtra("accesstoken", accesstoken); //Optional parameters //PREF_USER_TOKEN

                if ("booking".equals(RbLaunchType)) {
                    mainintent.putExtra("RbLaunchType", RbLaunchType);
                    mainintent.putExtra("BookingBody", BookingBody);
                    mainintent.putExtra("BookingChnum", BookingChnum);
                }

                startActivityForResult(mainintent, ExtraInfo.INTENT_CODE_MAIN);
            } else {
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                moveJoin(false);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

                AlertDialogUtil.showDialog(InitActivity.this, "", "회원정보가 없습니다. 회원 가입 후 이용해주세요.", "취소", "회원가입", onClickListener);
            }
        }

        @Override
        public void resetPwdResult(boolean result, String msg) {
            if (result) {
                Toast.makeText(InitActivity.this, msg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(InitActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    };

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
                                reeBotApi.reqEmailLogin(email, encPwd, initCallback);
//                                reqEmailLogin(new RBAuthData(ExtraInfo.AUTH_MODE_EMAIL_LOGIN, email, encPwd, "", "", "", "", "", "", ""));
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

        View btn_findpw = findViewById(R.id.btn_findpw);
        btn_findpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findpw();
            }
        });
    }

    private void findpw() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.reebot_resetpass_view, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);
        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("요청", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // ToDo get user input here

                        if ("".equals(userInputDialogEditText.getText()) || !userInputDialogEditText.getText().toString().contains("@")) {
                            Toast.makeText(getApplicationContext(), "이메일 입력 오류", Toast.LENGTH_SHORT).show();
                        } else {
                            reeBotApi.reqResetPwd(userInputDialogEditText.getText().toString(), initCallback);
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


    private void moveJoin(boolean isKakao) {


        Intent intent = new Intent(InitActivity.this, JoinActivity.class);
        intent.putExtra("isKakao", isKakao);
        if (isKakao) {
            intent.putExtra("email", email);
            intent.putExtra("atoken", atoken);
        }
        startActivityForResult(intent, ExtraInfo.INTENT_CODE_JOIN);



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

                reeBotApi.checkToken(accesstoken, initCallback);
//                reeBotApi.authToken(new RBAuthData(ExtraInfo.AUTH_MODE_CHECK_TOKEN, "", "", "", "", "", "", "", "", accesstoken));
            } else {
                if(!Session.getCurrentSession().checkAndImplicitOpen()) {
                    setLoginUi();
                }
            }
        } else {
            if(!Session.getCurrentSession().checkAndImplicitOpen()) {
                setLoginUi();
            }
        }
        passflag = true;
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
    private void requestKakao() {

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
                reeBotApi.checkTokenKakao(email, atoken, initCallback);
            }
        });
    }
}
