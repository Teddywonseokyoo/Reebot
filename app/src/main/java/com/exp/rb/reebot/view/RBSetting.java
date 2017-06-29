package com.exp.rb.reebot.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.exp.rb.reebot.IR.RBIRGetSignal;
import com.exp.rb.reebot.IR.RBIRParamData;
import com.exp.rb.reebot.R;
import com.exp.rb.reebot.RBAuthData;
import com.exp.rb.reebot.RBAuthManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RBSetting.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RBSetting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RBSetting extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String checktokenurl = "http://reebot.io:8083/auth_api/checktoken";
    private static final String ARG_PARAM1 = "email";
    private static final String ARG_PARAM2 = "accesstoken";

    private static final String TAG = "ReeBot(RBSetting)";

    // TODO: Rename and change types of parameters
    private String email;
    private String accesstoken;

    private OnFragmentInteractionListener mListener;

    private String getlistsignalurl = "http://reebot.io:8083/api/req_getlistsignal";
    private Spinner spinner_catv;
    private Spinner spinner_tv;
    private ArrayAdapter<String> adapter_catv;
    private ArrayAdapter<String> adapter_tv;
    private  RBSetting rbSetting;

    private TextView v_user_catv;
    private  TextView v_user_tv;
    List<String> userInfolist = new ArrayList<String>();
    String catvbrend ="";
    String tvbrend ="";
    String user_dvice_id;
    String using_tv;
    String using_catv;

    public RBSetting() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RBSetting.
     */
    // TODO: Rename and change types and number of parameters
    public static RBSetting newInstance(String param1, String param2) {
        RBSetting fragment = new RBSetting();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(ARG_PARAM1);
            accesstoken = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rbSetting = this;
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = getActivity().getLayoutInflater().cloneInContext(contextThemeWrapper);
        Log.d(TAG,"Param1 : "+email + " Param2 :"+accesstoken);
        View v = localInflater.inflate(R.layout.fragment_rbsetting, container, false);
        // 사용하는 서비스 리스트 가져 오기

        //사용자 장치 리스트 수정
        spinner_catv = (Spinner) v.findViewById(R.id.rbsetting_catv_spinner);
        adapter_catv = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                   // ((TextView) v.findViewById(android.R.id.text1)).setText("");
                   // ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }
            @Override
            public int getCount() {
                if (super.getCount() == 0) return 0;
                else return super.getCount() - 1; // you dont display last item. It is used as hint.
            }
        };

        spinner_tv = (Spinner) v.findViewById(R.id.rbsetting_tv_spinner);
        adapter_tv = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                 //   ((TextView) v.findViewById(android.R.id.text1)).setText("");
                 //   ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }
            @Override
            public int getCount() {
                if (super.getCount() == 0) return 0;
                else return super.getCount() - 1; // you dont display last item. It is used as hint.
            }
        };
        v_user_catv = (TextView)v.findViewById(R.id.rbsetting_catv);
        v_user_tv  =(TextView)v.findViewById(R.id.rbsetting_tvbreand);
        getservicelist();
        getInfo();

        ((Button)v.findViewById(R.id.rbsetting_change)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if("TV 방송 사업자 선택".equals(catvbrend))
                {
                    catvbrend = using_catv;
                }
                if("TV 제조사 선택".equals(tvbrend))
                {
                    tvbrend =  using_tv;
                }
                RBSettingManager rbSettingManager = new RBSettingManager(getContext(), new ChangeCallback() {
                    @Override
                    public void onTaskDone(String result) {
                        try {
                            JSONObject json = new JSONObject(result);
                            if(json.getBoolean("type"))
                            {
                                mListener.onRBsettingFragmentInteraction( tvbrend+"/"+catvbrend);
                            }
                            else
                            {
                                //변경 실패
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },accesstoken);
                rbSettingManager.execute(new RBSettingData(user_dvice_id,tvbrend,catvbrend));
            }
        });
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public  void getInfo()
    {
        //토큰 검증
        RBAuthManager rbAuthManager = new RBAuthManager(getContext(), new AuthCallback() {
            @Override
            public void onTaskDone(String result) {
                Log.d(TAG, "onTaskDone : " + result);
                RBGetInfo(result);

            }
        });
        rbAuthManager.execute(new RBAuthData(4, "", "", "", "", "", "", "", checktokenurl, accesstoken));

    }

    public void getservicelist()
    {
        RBIRGetSignal signalcatv = new RBIRGetSignal(getContext(),new SignalCallback(){
            @Override
            public void onTaskDone(String result) {
                Log.d(TAG, "RBIRGetListSignaTaskResult : " + result);
                RBIRGetListSignaTaskResult(result);
            }
        });
        signalcatv.execute(new RBIRParamData(1, getlistsignalurl, "signalsbox"));
        adapter_catv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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


        RBIRGetSignal signaltv = new RBIRGetSignal(getContext(),new SignalCallback(){
            @Override
            public void onTaskDone(String result) {
                Log.d(TAG, "RBIRGetListSignaTaskResult : " + result);
                RBIRGetListSignaTaskResult(result);
            }
        });
        signaltv.execute(new RBIRParamData(1, getlistsignalurl, "signaltv"));
        adapter_tv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_tv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.d(TAG, "TV onItemSelected : ");
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
    public void RBGetInfo(String msg)
    {
        int usingdevice = 0;
        try {
            JSONObject jsonobject = new JSONObject(msg);
            if(jsonobject.getBoolean("type") == true)
            {
                JSONArray jArray = jsonobject.getJSONArray("regdevice");
                usingdevice = jsonobject.getInt("usingdevice");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    String _id = oneObject.getString("_id");
                    String location = oneObject.getString( "location");
                    String tvbrend =  oneObject.getString( "tvbrend");
                    String catvvendor =  oneObject.getString( "catvvendor");
                    String rbtoken =  oneObject.getString( "rbtoken");
                    userInfolist.add(_id+"/"+location+"/"+tvbrend+"/"+catvvendor+"/"+rbtoken);
                }
            }
        }
        catch (JSONException e)
        {

        }
        //임시 디바이스 한개로 막음
        String userInfoData = userInfolist.get(usingdevice);
        user_dvice_id = userInfoData.split("/")[0];
        using_tv = userInfoData.split("/")[2];
        using_catv = userInfoData.split("/")[3];
        v_user_tv.setText(" - 현재 "+ userInfoData.split("/")[2] +" 이용중입니다.");
        v_user_catv.setText(" - 현재 "+ userInfoData.split("/")[3]+" 이용중입니다.");
    }

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
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
            alert_confirm.setMessage("인터넷 연결 상태를 확인해주세요.").setNegativeButton("확인",
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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRBsettingFragmentInteraction(String msg);

    }

    public interface SignalCallback {
        public void onTaskDone(String result);
    }

    public interface AuthCallback {
        public void onTaskDone(String result);
    }

    public interface ChangeCallback {
        public void onTaskDone(String result);
    }
}
