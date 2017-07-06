package com.example.reebotui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.reebotui.api.ReeBotApi;
import com.example.reebotui.info.ExtraInfo;
import com.example.reebotui.info.RBAuthData;
import com.example.reebotui.info.RBIRParamData;
import com.example.reebotui.interfaceclass.EventCallback;
import com.example.reebotui.util.AlertDialogUtil;
import com.exp.rb.reebot.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingActivity extends AppCompatActivity implements EventCallback {
    private static final String TAG = "ReeBot(SettingActivity)";

    public static final int EVENT_REQ_CHANGE_SERVICE = 100;

    private ReeBotApi reeBotApi;

    private Spinner sp_cabletvbrend;
    private Spinner sp_tvbrand;
    private ArrayAdapter<String> adapter_catv;
    private ArrayAdapter<String> adapter_tv;

    private String email = "";
    private String _id = "";
    private String tvbrend = "";
    private String catvbrend = "";
    private String acesstoken = "";

    private boolean isSuccessCatv = false;
    private boolean isSuccessTv = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sp_cabletvbrend = (Spinner) findViewById(R.id.sp_cabletvbrend);
        sp_tvbrand = (Spinner) findViewById(R.id.sp_tvbrand);

        Intent intent = getIntent();
        if (intent != null) {
//            tvbrend = intent.getStringExtra("tvb");
//            catvbrend = intent.getStringExtra("catv");
            acesstoken = intent.getStringExtra("acesstoken");
        }


        setSp();


        findViewById(R.id.btn_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "btn_confirm sp_cabletvbrend: " + sp_cabletvbrend.getSelectedItem());
                Log.d(TAG, "btn_confirm sp_tvbrand: " + sp_tvbrand.getSelectedItem());

                String tvb = sp_tvbrand.getSelectedItem().toString();
                String catv = sp_cabletvbrend.getSelectedItem().toString();

                Log.d(TAG, "btn_confirm tvb: " + tvb);
                Log.d(TAG, "btn_confirm catv: " + catv);

                if (tvb.contains("사용중")) {
                    tvb = tvbrend;
                }
                if (catv.contains("사용중")) {
                    catv = catvbrend;
                }

                if ((tvb.toString().contains("_"))) {
                    tvb = tvb.split("_")[1];
//                    tvb = tvb.replaceAll("[0-9]", ""); //숫자 제거
                }

                if ((catv.toString().contains("_"))) {
                    catv = catv.split("_")[1];
                }

                Log.d(TAG, "btn_confirm tvb: " + tvb);
                Log.d(TAG, "btn_confirm catv: " + catv);
                Log.d(TAG, "btn_confirm _id: " + _id);
                Log.d(TAG, "btn_confirm acesstoken: " + acesstoken);
                Log.d(TAG, "btn_confirm email: " + email);

                reeBotApi.reqChangeService(_id, tvb, catv, acesstoken);
                tvbrend = tvb;
                catvbrend = catv;

            }
        });

        reeBotApi = new ReeBotApi(this);
        reeBotApi.authToken(new RBAuthData(ExtraInfo.AUTH_MODE_CHECK_TOKEN, "", "", "", "", "", "", "", "", acesstoken));
//        reeBotApi.reqSignalList(new RBIRParamData(ExtraInfo.REQ_MODE_REMOTE_SIGNAL, "", "signalsbox"));
//        reeBotApi.reqSignalList(new RBIRParamData(ExtraInfo.REQ_MODE_REMOTE_SIGNAL, "", "signaltv"));
    }


    private void setSp() {
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

//                Object item = parent.getItemAtPosition(position);
//                if (item != null) {
//
//                    if ((item.toString().contains("_"))) {
//                        catvbrend = item.toString().split("_")[1];
////                        catvbrend = catvbrend.replaceAll("[0-9]", ""); //숫자 제거
//                    } else {
//                        catvbrend = item.toString();
//                    }
//                }
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
//                Object item = parent.getItemAtPosition(position);
//                if (item != null) {
//                    if ((item.toString().contains("_"))) {
//                        tvbrend = item.toString().split("_")[1];
//                        tvbrend = tvbrend.replaceAll("[0-9]", ""); //숫자 제거
//                    } else {
//                        tvbrend = item.toString();
//                    }
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onEvent(int event, String msg) {
        Log.d(TAG, "onEvent event: " + event);
        Log.d(TAG, "onEvent msg: " + msg);

        switch (event) {
            case InitActivity.EVENT_REQ_CHECK_TOKEN_SUCCESS:
                RBGetInfo(msg);
                reeBotApi.reqSignalList(new RBIRParamData(ExtraInfo.REQ_MODE_REMOTE_SIGNAL, "", "signalsbox"));
                reeBotApi.reqSignalList(new RBIRParamData(ExtraInfo.REQ_MODE_REMOTE_SIGNAL, "", "signaltv"));
                break;

            case EVENT_REQ_CHANGE_SERVICE:
                changeServiceResult(msg);
                break;

            case JoinActivity.EVENT_REQ_REMOTE_SIGNAL_SUCCESS:
                rBIRGetListSignaTaskResult(msg);
                break;
            case ExtraInfo.REQ_MODE_REMOTE_IR_SIGNAL_SBOX:
                isSuccessCatv = true;
                if(isSuccessCatv && isSuccessTv)
                {
                    //                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra("tvb", tvbrend);
                        intent.putExtra("catv", catvbrend);
                        setResult(RESULT_OK, intent);
                        finish();
//                    }
//                }, 200);
                }


                break;
            case ExtraInfo.REQ_MODE_REMOTE_IR_SIGNAL_TV:
                isSuccessTv = true;
                if(isSuccessCatv && isSuccessTv)
                {
                    //                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                    Intent intent = new Intent();
                    intent.putExtra("tvb", tvbrend);
                    intent.putExtra("catv", catvbrend);
                    setResult(RESULT_OK, intent);
                    finish();
//                    }
//                }, 200);
                }
                break;
            case JoinActivity.EVENT_REQ_REMOTE_IRSIGNAL_FAIL:
                break;


        }
    }


    private void RBGetInfo(String msg) {
        int usingdevice = 0;
        try {
            JSONObject jsonobject = new JSONObject(msg);
            if (jsonobject.getBoolean("type") == true) {
                email = jsonobject.getString("email");
                JSONArray jArray = jsonobject.getJSONArray("regdevice");
                usingdevice = jsonobject.getInt("usingdevice");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    _id = oneObject.getString("_id");
                    String location = oneObject.getString("location");
                    tvbrend = oneObject.getString("tvbrend");
                    catvbrend = oneObject.getString("catvvendor");
                    String rbtoken = oneObject.getString("rbtoken");
//                    userInfolist.add(_id+"/"+location+"/"+tvbrend+"/"+catvvendor+"/"+rbtoken);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        //임시 디바이스 한개로 막음
//        String userInfoData = userInfolist.get(usingdevice);
//        user_dvice_id = userInfoData.split("/")[0];
//        using_tv = userInfoData.split("/")[2];
//        using_catv = userInfoData.split("/")[3];
//        v_user_tv.setText(" - 현재 "+ userInfoData.split("/")[2] +" 이용중입니다.");
//        v_user_catv.setText(" - 현재 "+ userInfoData.split("/")[3]+" 이용중입니다.");
    }

    private void rBIRGetListSignaTaskResult(String msg) {
        Log.d(TAG, "RBIRGetListSignaTaskResult : " + msg);
        try {
            JSONObject jsonobject = new JSONObject(msg);
            if (jsonobject.getBoolean("type") == true) {
                Log.d(TAG, "RBIRGetListSignaTaskResult2 : " + jsonobject.getString("type2"));
                if ("signaltv".equals(jsonobject.getString("type2"))) {
                    if (makelist(jsonobject.getJSONArray("data"), adapter_tv) == true) {
                        adapter_tv.add(tvbrend + " 사용중");
                        sp_tvbrand.setAdapter(adapter_tv);
                        sp_tvbrand.setSelection(adapter_tv.getCount());
                    }

                } else {
                    if (makelist(jsonobject.getJSONArray("data"), adapter_catv) == true) {
                        adapter_catv.add(catvbrend + " 사용중");
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


    private void changeServiceResult(String result) {
        try {
            JSONObject json = new JSONObject(result);
            if (json.getBoolean("type")) {
//                mListener.onRBsettingFragmentInteraction( tvbrend+"/"+catvbrend);
                String[] separated = email.split("@");
                reeBotApi.reqSignalList(new RBIRParamData(ExtraInfo.REQ_MODE_REMOTE_IR_SIGNAL_SBOX, "", separated[0], "room1", "signalsbox", catvbrend, ""));
                reeBotApi.reqSignalList(new RBIRParamData(ExtraInfo.REQ_MODE_REMOTE_IR_SIGNAL_TV, "", separated[0], "room1", "signaltv", tvbrend, ""));
            } else {
                //변경 실패
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
