package com.example.reebotui;

import android.app.Notification;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reebotui.api.ReeBotApi;
import com.example.reebotui.interfaceclass.DeviceCallback;
import com.example.reebotui.util.AlertDialogUtil;
import com.exp.rb.reebot.R;

import java.util.List;

/**
 * Created by freem on 2017-07-26.
 */

public class DeviceActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ReeBot(DeviceActivity)";
    private Spinner spinner_ssid;
    private EditText wifipw;
    private WifiManager wifiManager;
    private ReeBotApi reeBotApi;
    ArrayAdapter<String> adapter;
    private String m_email;
    private String m_ssid;
    private  int addDeviceCount;
    private  String m_rbtoken;
    private  String m_token;
    private  String m_catv;
    private  String m_tv;
    private  String m_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        addDeviceCount = 0;
        Intent intent = getIntent();
        if (intent != null) {
            m_email = intent.getStringExtra("email");
            m_token = intent.getStringExtra("token");
            m_tv    = intent.getStringExtra("tv");
            m_catv  = intent.getStringExtra("catv");
        }
        m_location = "ROOM1";
        reeBotApi = new ReeBotApi(this);
        //1. WIFI 검색 및 연결 Reebot_INIT
        //3. 가저온 리스트를 스피너에 추가
        //2. 연결 후 192.168.4.1 에서 SSID LIST 및 dev token 가져옴
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item) {
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
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_ssid = (Spinner) findViewById(R.id.sp_ssid);
        spinner_ssid.setAdapter(adapter);
        spinner_ssid.setSelection(adapter.getCount());
        spinner_ssid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected : ");
                Object item = parent.getItemAtPosition(position);
                if (item != null) {
                    m_ssid = item.toString();
                    if (m_ssid.equals("Reebot-xxxxxx로 WIFI를 연결 필요.")) {
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
        wifipw = (EditText) findViewById(R.id.et_ssid_pwd);
        connectDecWifi();
        findViewById(R.id.btn_confirm).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_confirm:
                //move mainactivity
                addDevice();
                break;
        }
    }
    private void connectDecWifi()
    {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("장치를 검색중입니다.\n검색하는데 1분 정도 소요됩니다.");
        progressDialog.show();
        final Handler mHandler = new Handler();
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    WifiConfiguration wifiConfiguration = createAPConfiguration("ReeBot-INIT", "reebot!!", "WPA2");
                    int res = wifiManager.addNetwork(wifiConfiguration);
                    if (res == -1) res = getNetworkId("ReeBot-INIT");
                    Log.d(TAG, "# addNetwork returned " + res);
                    while (true) {
                        wifiManager.disconnect();
                        Thread.sleep(1000);
                        boolean b = wifiManager.enableNetwork(res, true);
                        Thread.sleep(1000);
                        boolean c = wifiManager.reconnect();
                        Thread.sleep(1000);
                        Log.d(TAG, "# enableNetwork returned " + b);
                        if (res != -1) {
                            try {
                                Thread.sleep(1000);
                                if (wifiManager.getConnectionInfo().getSSID().matches(".*ReeBot.*")) {
                                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                                    if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                                        getSSIDList();
                                        progressDialog.dismiss();
                                        //RBEnvConnector task = new RBEnvConnector(initActivity, "getssid");
                                        //task.execute("http://192.168.4.1/ssidlist");
                                        break;
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d(TAG, "# Change NOT happen");
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    //실패

                    mHandler.post(new Runnable(){
                        @Override public void run() {
                            AlertDialogUtil.showDialog(DeviceActivity.this, "WIFI 작동을 확인해주세요.");
                        }
                    });

                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    private int getNetworkId(String ssid) {
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                return i.networkId;
            }
        }
        return -1;
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
    private void getSSIDList()
    {
        //getssidlist
        reeBotApi.reqSSIDList(adapter,deviceCallback);
    }
    private void addDevice()
    {

        //4. SSID & WIFI 비밀번호 설정 192.168.4.1/setting 에 비밀번호 전달.
        //device에 WIFI 패스워드 저장
        saveWifiPwToDev(wifipw.getText().toString());

    }
    DeviceCallback deviceCallback = new DeviceCallback()
    {
        @Override
        public void comSSIDList(boolean ret) {
            if(ret == false)
            {
                AlertDialogUtil.showDialog(DeviceActivity.this, "장치를 찾는데 실패하였습니다.(장치 전원을 확인)");
            }
            else
            {

            }
        }
        @Override
        public void resSaveInfoToDevice(String reebottoken) {
            //서버에 등록된 Reebot device token 장치 확인
            m_rbtoken = reebottoken;
            try {
                Thread.sleep(3000);
                if(!m_rbtoken.isEmpty())
                    chkAddDevice();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void resAddDevice(boolean ret) {
            // 5번 재시도(장치가 네트워크에 들어오고 등록 되는 시간)
            if(ret == false && addDeviceCount < 5 ) {
                addDeviceCount++;
                try {
                    Thread.sleep(3000);
                    chkAddDevice();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else if(ret == true)
            {
                //성공 다음 화면 으로 이동
                AlertDialogUtil.showDialog(DeviceActivity.this,"성공 다음 화면 으로 이동");
            }
            else if(addDeviceCount == 5)
            {
                //실패 (횟수 초과)
                AlertDialogUtil.showDialog(DeviceActivity.this, "장치를 찾는데 실패하였습니다.(장치 연결 확인)");
            }
        }
    };
    void saveWifiPwToDev(String wifipw)
    {
        if (m_ssid == null) return;
        if (m_ssid.equals("Reebot-xxxxxx로 WIFI를 연결 필요.") || m_ssid.equals("HOME WIFI SSID 선택") || m_ssid.isEmpty() || wifipw.isEmpty()) {
            //Log.d(TAG, "equals : true");
            AlertDialogUtil.showDialog(DeviceActivity.this, "WIFI 설정값이 잘못되었습니다.");
        }
        else
        {
            reeBotApi.setInfoToDev(m_ssid,wifipw,m_email,deviceCallback);
        }
    }
    void chkAddDevice()
    {
        
        reeBotApi.chkAddDevice(m_email,m_rbtoken,m_token,m_location,m_catv,m_tv,deviceCallback);
    }
}
