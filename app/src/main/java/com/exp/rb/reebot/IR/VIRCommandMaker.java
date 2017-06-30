package com.exp.rb.reebot.IR;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VIRCommandMaker  implements VIRCommand.VIRCommandListener {

    private static final String TAG = "ReeBot(VIRCommandMaker)";
    private Context context;
    private String cur_device;
    private String sid;
    private String cur_command;
    private String cur_param;
    private int cur_index;
    private  String cur_url;
    private JSONObject stboxsignal;
    private JSONObject tvsignal;
    private ConsumerIrManager mCIR;
    private Vibrator vib;




    public  VIRCommandMaker(Context context,String cur_url,String email ,String device, String param , String sid)
    {
        cur_index = 0;
        this.cur_url = cur_url;
        this.context = context;
        this.sid = sid;
        cur_device = device;
        cur_param = param;

        vib = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        stboxsignal = new  RBSignalForJSON(context,email,"room1","signalsbox").devjson;
        tvsignal = new RBSignalForJSON(context,email,"room1","signaltv").devjson;
        //stboxsignal = RBSignalForJSON.getInstance(context,email,"room1","signalsbox").devjson;
        //tvsignal = RBSignalForJSON.getInstance(context,email,"room1","signaltv").devjson;
        if(stboxsignal != null)
            NControlMaker(device,param);

        //Toast.makeText(context, "Not Found Commaand", Toast.LENGTH_SHORT).show();

    }
    /*
    public  VIRCommandMaker(Context context,String cur_url, String device, String command, String param , String sid)
    {
        this.cur_url = cur_url;
        this.context = context;
        this.sid = sid;
        cur_device = device;
        cur_command = command;
        cur_param = param;
        if(command.equals("CONTROL"))
        {
            ControlMaker(device,param);
        }
        else if(command.equals("CHNUMBER"))
        {
            ChSignallMaker(device,param,0);
        }
        else
        {
            Toast.makeText(context, "Not Found Commaand", Toast.LENGTH_SHORT).show();
        }
    }
    */
    private void NControlMaker(String device, String param)
    {
        if( device.split("_")[0].equals("TV"))
        {
            String command_signal="";
            if("internelIR".equals(sid))
            {

                try {
                    //채널 넘버
                    if(param.matches("-?\\d+(\\.\\d+)?")) {
                        command_signal = tvsignal.getJSONObject("SIGNAL").getJSONObject(String.valueOf(param.charAt(cur_index))).getString("raw");
                    }
                    //기능키 (엔터 볼륨 등등)
                    else
                    {
                        command_signal = tvsignal.getJSONObject("SIGNAL").getJSONObject(param).getString("raw");
                    }
                    //여기 내부 IR로 수정
                    /*
                    VIRCommand task = new VIRCommand(this);
                    task.execute(new VIRDataValue(cur_url, "nec", command_signal, sid));
                    */
                    //String[] separated =  command_signal.split(",");
                    //int signal[] = new int[ separated.length];
                    //for(int i = 0 ; i <  separated.length ; i++)
                    //{
                    //    signal[i] = Integer.parseInt(separated[i].trim()) ;
                    //}

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !("".equals(command_signal)) ) {
                         int lastIdx = Build.VERSION.RELEASE.lastIndexOf(".");
                        int VERSION_MR = Integer.valueOf(Build.VERSION.RELEASE.substring(lastIdx + 1));
                        mCIR = (ConsumerIrManager) context.getSystemService(Context.CONSUMER_IR_SERVICE);
                        if (VERSION_MR < 3) {
                            //Before version of Android 4.4.2
                            command_signal = duration2count(38028,command_signal);
                            String[] separated =  command_signal.split(",");
                            int signal[] = new int[ separated.length];
                            for(int i = 0 ; i <  separated.length ; i++)
                            {
                                if(!("".equals(separated[0])))
                                    signal[i] = Integer.parseInt(separated[i].trim()) ;
                            }
                            Log.d(TAG, "transmit : " + signal);
                            mCIR.transmit(38028, signal);
                        }
                        else
                        {
                            // Later version of Android 4.4.3
                            String[] separated =  command_signal.split(",");
                            int signal[] = new int[ separated.length];
                            for(int i = 0 ; i <  separated.length ; i++)
                            {
                                if(!("".equals(separated[0])))
                                    signal[i] = Integer.parseInt(separated[i].trim()) ;
                            }

                            Log.d(TAG, "transmit : " + signal);

                            mCIR.transmit(38000, signal);
                        }
                        cur_index++;
                        if(cur_param.matches("-?\\d+(\\.\\d+)?")&&cur_param.length() >  cur_index  )
                        {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            NControlMaker(cur_device, cur_param);
                        }
                        else if(cur_param.matches("-?\\d+(\\.\\d+)?")&&cur_param.length() ==  cur_index  )
                        {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "NextCommand ControlMaker : "+cur_device+"+ ENTER  ");
                            NControlMaker(cur_device,"ENTER");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                vib.vibrate(80);
            }
            else if("nodevice".equals(sid))
            {
                //장치없음
            }
            else
            {
                vib.vibrate(80);
                try {
                    //채널 넘버
                    if (param.matches("-?\\d+(\\.\\d+)?")) {
                        command_signal = tvsignal.getJSONObject("SIGNAL").getJSONObject(String.valueOf(param.charAt(cur_index))).getString("code");
                    }
                    //기능키 (엔터 볼륨 등등)
                    else {
                        command_signal = tvsignal.getJSONObject("SIGNAL").getJSONObject(param).getString("code");
                    }
                    VIRCommand task = new VIRCommand(this);
                    task.execute(new VIRDataValue(cur_url, "nec", command_signal, sid));

                } catch (JSONException e) {

                }
                //VIRCommand task = new VIRCommand(this);
                //task.execute(new VIRDataValue(cur_url,"nec",command_signal,sid));
                Log.d(TAG, "command_signal : " + command_signal);
            }
        }
        else if(device.equals("STBOX")) {
            String command_signal="";
            if("internelIR".equals(sid))
            {
                vib.vibrate(80);
                try {
                    //채널 넘버
                    if(param.matches("-?\\d+(\\.\\d+)?")) {
                        command_signal = stboxsignal.getJSONObject("SIGNAL").getJSONObject(String.valueOf(param.charAt(cur_index))).getString("raw");
                    }
                    //기능키 (엔터 볼륨 등등)
                    else
                    {
                        command_signal = stboxsignal.getJSONObject("SIGNAL").getJSONObject(param).getString("raw");
                    }
                    //여기 내부 IR로 수정
                    /*
                    VIRCommand task = new VIRCommand(this);
                    task.execute(new VIRDataValue(cur_url, "nec", command_signal, sid));
                    */



                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !("".equals(command_signal)) ) {
                        int lastIdx = Build.VERSION.RELEASE.lastIndexOf(".");
                        int VERSION_MR = Integer.valueOf(Build.VERSION.RELEASE.substring(lastIdx + 1));
                        mCIR = (ConsumerIrManager) context.getSystemService(Context.CONSUMER_IR_SERVICE);
                        if (VERSION_MR < 3) {
                             //Before version of Android 4.4.2
                            command_signal = duration2count(38028 ,command_signal);
                            String[] separated =  command_signal.split(",");
                            int signal[] = new int[ separated.length];
                            for(int i = 0 ; i <  separated.length ; i++)
                            {
                                if(!("".equals(separated[0])))
                                    signal[i] = Integer.parseInt(separated[i].trim()) ;
                            }
                            Log.d(TAG, "transmit : " + signal);
                            mCIR.transmit(38028, signal);
                        } else {
                            // Later version of Android 4.4.3
                            String[] separated =  command_signal.split(",");
                            int signal[] = new int[ separated.length];
                            for(int i = 0 ; i <  separated.length ; i++)
                            {
                                if(!("".equals(separated[0])))
                                    signal[i] = Integer.parseInt(separated[i].trim()) ;
                            }

                            Log.d(TAG, "transmit : " + signal);

                            mCIR.transmit(38000, signal);
                        }


                        cur_index++;
                        if(cur_param.matches("-?\\d+(\\.\\d+)?")&&cur_param.length() >  cur_index  )
                        {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            NControlMaker(cur_device, cur_param);
                        }
                        else if(cur_param.matches("-?\\d+(\\.\\d+)?")&&cur_param.length() ==  cur_index  )
                        {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "NextCommand ControlMaker : "+cur_device+"+ ENTER  ");
                            NControlMaker(cur_device,"ENTER");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                vib.vibrate(80);
            }
            else if("nodevice".equals(sid))
            {
                //장치없음
            }
            else {
                vib.vibrate(80);
                try {
                    //채널 넘버
                    if (param.matches("-?\\d+(\\.\\d+)?")) {
                        command_signal = stboxsignal.getJSONObject("SIGNAL").getJSONObject(String.valueOf(param.charAt(cur_index))).getString("code");
                    }
                    //기능키 (엔터 볼륨 등등)
                    else {
                        command_signal = stboxsignal.getJSONObject("SIGNAL").getJSONObject(param).getString("code");
                    }
                    VIRCommand task = new VIRCommand(this);
                    task.execute(new VIRDataValue(cur_url, "nec", command_signal, sid));

                } catch (JSONException e) {

                }
                //VIRCommand task = new VIRCommand(this);
                //task.execute(new VIRDataValue(cur_url,"nec",command_signal,sid));
                Log.d(TAG, "command_signal : " + command_signal);
            }
        }
    }


    private void ControlMaker(String device, String param)
    {
        String command_signal="";
        if( device.equals("SAMSUNGTV"))
        {
            if(param.equals("PW"))
            {
                command_signal = RBIRSignal.SAMSUNGTV_power.toString();
                //VIRCommand task = new VIRCommand(this);
                //task.execute(new VIRDataValue(cur_url,"samsung",command_signal,sid));
            }
            else if(param.equals("VOLUP"))
            {

            }
            else if(param.equals("VOLDOWN"))
            {

            }
            else if(param.equals("MUTE"))
            {

            }
            else if(param.equals("INPUTSIGNAL"))
            {

            }
            else
            {
                Toast.makeText(context, "Not Found Commaand", Toast.LENGTH_SHORT).show();
            }
            if("internelIR".equals(sid))
            {

            }
            else if("nodevice".equals(sid))
            {
                //장치없음
            }
            else
            {
                VIRCommand task = new VIRCommand(this);
                task.execute(new VIRDataValue(cur_url,"samsung",command_signal,sid));
            }

        }
        else if( device.equals("LGTV"))
        {
            if(param.equals("PW"))
            {

            }
            else if(param.equals("VOLUP"))
            {

            }
            else if(param.equals("VOLDOWN"))
            {

            }
            else if(param.equals("MUTE"))
            {

            }
            else if(param.equals("INPUTSIGNAL"))
            {

            }
            else
            {
                Toast.makeText(context, "Not Found Commaand", Toast.LENGTH_SHORT).show();
            }

            if("internelIR".equals(sid))
            {

            }
            else if("nodevice".equals(sid))
            {
                //장치없음
            }
            else
            {
                VIRCommand task = new VIRCommand(this);
                task.execute(new VIRDataValue(cur_url,"nec",command_signal,sid));
            }


        }
        else if( device.equals("S_LG"))
        {
            if(param.equals("PW"))
            {
                command_signal = RBIRSignal.LG_power.toString();
            }
            else if(param.equals("CHUP"))
            {
                 command_signal = RBIRSignal.LG_chup.toString();
            }
            else if(param.equals("CHDOWN"))
            {
                 command_signal = RBIRSignal.LG_chdown.toString();
            }
            else if(param.equals("VOLUP"))
            {
                 command_signal = RBIRSignal.LG_volup.toString();
            }
            else if(param.equals("VOLDOWN"))
            {
                command_signal = RBIRSignal.LG_voldown.toString();
            }
            else if(param.equals("BEFORECH"))
            {
                command_signal = RBIRSignal.LG_bch.toString();
            }
            else if(param.equals("MUTE"))
            {
                command_signal = RBIRSignal.LG_mute.toString();
            }
            else if(param.equals("ENTER"))
            {
                 command_signal = RBIRSignal.LG_enter.toString();
            }
            else
            {
                Toast.makeText(context, "Not Found Commaand", Toast.LENGTH_SHORT).show();
            }

            if("internelIR".equals(sid))
            {

            }
            else if("nodevice".equals(sid))
            {
                //장치없음
            }
            else
            {
                VIRCommand task = new VIRCommand(this);
                task.execute(new VIRDataValue(cur_url,"nec",command_signal,sid));
            }
        }
        else if( device.equals("S_KT"))
        {
            if(param.equals("PW"))
            {
                command_signal = RBIRSignal.KT_power.toString();
            }
            else if(param.equals("CHUP"))
            {
                command_signal = RBIRSignal.KT_chup.toString();
            }
            else if(param.equals("CHDOWN"))
            {
                command_signal = RBIRSignal.KT_chdown.toString();
            }
            else if(param.equals("VOLUP"))
            {
                command_signal = RBIRSignal.KT_volup.toString();
            }
            else if(param.equals("VOLDOWN"))
            {
                command_signal = RBIRSignal.KT_voldown.toString();
            }
            else if(param.equals("BEFORECH"))
            {
                command_signal = RBIRSignal.KT_bch.toString();
            }
            else if(param.equals("MUTE"))
            {
                command_signal = RBIRSignal.KT_mute.toString();
            }
            else if(param.equals("ENTER"))
            {
                command_signal = RBIRSignal.KT_enter.toString();
            }
            else
            {
                Toast.makeText(context, "Not Found Commaand", Toast.LENGTH_SHORT).show();
            }
            if("internelIR".equals(sid))
            {

            }
            else if("nodevice".equals(sid))
            {
                //장치없음
            }
            else
            {
                VIRCommand task = new VIRCommand(this);
                task.execute(new VIRDataValue(cur_url,"nec",command_signal,sid));
            }

        }
        else if( device.equals("S_SK"))
        {
            if(param.equals("PW"))
            {
                command_signal = RBIRSignal.SK_power.toString();
            }
            else if(param.equals("CHUP"))
            {
                command_signal = RBIRSignal.SK_chup.toString();
            }
            else if(param.equals("CHDOWN"))
            {
                command_signal = RBIRSignal.SK_chdown.toString();
            }
            else if(param.equals("VOLUP"))
            {
                command_signal = RBIRSignal.SK_volup.toString();
            }
            else if(param.equals("VOLDOWN"))
            {
                command_signal = RBIRSignal.SK_voldown.toString();
            }
            else if(param.equals("BEFORECH"))
            {
                command_signal = RBIRSignal.SK_bch.toString();
            }
            else if(param.equals("MUTE"))
            {
                command_signal = RBIRSignal.SK_mute.toString();
            }
            else if(param.equals("ENTER"))
            {
                command_signal = RBIRSignal.SK_enter.toString();
            }
            else
            {
                Toast.makeText(context, "Not Found Commaand", Toast.LENGTH_SHORT).show();
            }
            if("internelIR".equals(sid))
            {

            }
            else if("nodevice".equals(sid))
            {
                //장치없음
            }
            else
            {
                VIRCommand task = new VIRCommand(this);
                task.execute(new VIRDataValue(cur_url,"nec",command_signal,sid));
            }
        }
        else if( device.equals("S_DLIVE1"))
        {
            if(param.equals("PW"))
            {
                command_signal = RBIRSignal.DLIVE1_power.toString();
            }
            else if(param.equals("CHUP"))
            {
                command_signal = RBIRSignal.DLIVE1_chup.toString();
            }
            else if(param.equals("CHDOWN"))
            {
                command_signal = RBIRSignal.DLIVE1_chdown.toString();
            }
            else if(param.equals("VOLUP"))
            {
                command_signal = RBIRSignal.DLIVE1_volup.toString();
            }
            else if(param.equals("VOLDOWN"))
            {
                command_signal = RBIRSignal.DLIVE1_voldown.toString();
            }
            else if(param.equals("BEFORECH"))
            {
                command_signal = RBIRSignal.DLIVE1_bch.toString();
            }
            else if(param.equals("MUTE"))
            {
                command_signal = RBIRSignal.DLIVE1_mute.toString();
            }
            else if(param.equals("ENTER"))
            {
                command_signal = RBIRSignal.DLIVE1_enter.toString();
            }
            else
            {
                Toast.makeText(context, "Not Found Commaand", Toast.LENGTH_SHORT).show();
            }

            if("internelIR".equals(sid))
            {

            }
            else if("nodevice".equals(sid))
            {
                //장치없음
            }
            else
            {
                VIRCommand task = new VIRCommand(this);
                task.execute(new VIRDataValue(cur_url,"nec",command_signal,sid));
            }
        }
        else
        {
            if( device.equals("TSSL"))
            {
                if(param.equals("PW")) {
                    Log.d(TAG, "TV POWER_ON");
                    command_signal = RBIRSignal.SAMSUNGTV_power.toString();

                    if ("internelIR".equals(sid)) {

                    } else if ("nodevice".equals(sid)) {
                        //장치없음
                    } else {
                        VIRCommand task = new VIRCommand(this);
                        task.execute(new VIRDataValue(cur_url, "samsung", command_signal, sid));
                    }

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "ST POWER_ON");
                    command_signal = RBIRSignal.LG_power.toString();

                    if("internelIR".equals(sid))
                    {

                    }
                    else if("nodevice".equals(sid))
                    {
                        //장치없음
                    }
                    else
                    {
                        VIRCommand task = new VIRCommand(this);
                        task.execute(new VIRDataValue(cur_url,"nec",command_signal,sid));
                    }
                }
            }
            else if( device.equals("TSSS"))
            {
                if(param.equals("PW")) {
                    Log.d(TAG, "TV POWER_ON");
                    command_signal = RBIRSignal.SAMSUNGTV_power.toString();

                    if ("internelIR".equals(sid)) {

                    } else if ("nodevice".equals(sid)) {
                        //장치없음
                    } else {
                        VIRCommand task = new VIRCommand(this);
                        task.execute(new VIRDataValue(cur_url,"samsung",command_signal,sid));
                    }

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "ST POWER_ON");
                    command_signal = RBIRSignal.SK_power.toString();
                    if("internelIR".equals(sid))
                    {

                    }
                    else if("nodevice".equals(sid))
                    {
                        //장치없음
                    }
                    else
                    {
                        VIRCommand task = new VIRCommand(this);
                        task.execute(new VIRDataValue(cur_url,"nec",command_signal,sid));
                    }
                }
            }
            else if( device.equals("TSSK"))
            {
                if(param.equals("PW")) {
                    Log.d(TAG, "TV POWER_ON");
                    command_signal = RBIRSignal.SAMSUNGTV_power.toString();

                    if ("internelIR".equals(sid)) {

                    } else if ("nodevice".equals(sid)) {
                        //장치없음
                    } else {
                        VIRCommand task = new VIRCommand(this);
                        task.execute(new VIRDataValue(cur_url,"samsung",command_signal,sid));
                    }

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "ST POWER_ON");
                    command_signal = RBIRSignal.KT_power.toString();
                    if("internelIR".equals(sid))
                    {

                    }
                    else if("nodevice".equals(sid))
                    {
                        //장치없음
                    }
                    else
                    {
                        VIRCommand task = new VIRCommand(this);
                        task.execute(new VIRDataValue(cur_url,"nec",command_signal,sid));
                    }
                }
            }
            else if( device.equals("TLSS"))
            {
                if(param.equals("PW")) {
                    Log.d(TAG, "TV POWER_ON");
                    command_signal = RBIRSignal.LGTV_power.toString();
                    if("internelIR".equals(sid))
                    {

                    }
                    else if("nodevice".equals(sid))
                    {
                        //장치없음
                    }
                    else
                    {
                        VIRCommand task = new VIRCommand(this);
                        task.execute(new VIRDataValue(cur_url,"nec",command_signal,sid));
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "ST POWER_ON");
                    command_signal = RBIRSignal.SK_power.toString();
                    if("internelIR".equals(sid))
                    {

                    }
                    else if("nodevice".equals(sid))
                    {
                        //장치없음
                    }
                    else
                    {
                        VIRCommand task = new VIRCommand(this);
                        task.execute(new VIRDataValue(cur_url,"nec",command_signal,sid));
                    }
                }
            }

            Toast.makeText(context, "Not Found Commaand", Toast.LENGTH_SHORT).show();
        }
    }
    private void ChSignallMaker(String device, String param ,int idex )
    {
        Log.d(TAG, "ChSignallMaker : " + param);
        cur_index = idex;
        String signal_0="";
        String signal_1="";
        String signal_2="";
        String signal_3="";
        String signal_4="";
        String signal_5="";
        String signal_6="";
        String signal_7="";
        String signal_8="";
        String signal_9="";

        if( device.equals("S_LG"))
        {
            signal_0 = RBIRSignal.LG_ch0.toString();
            signal_1 = RBIRSignal.LG_ch1.toString();
            signal_2 = RBIRSignal.LG_ch2.toString();
            signal_3 = RBIRSignal.LG_ch3.toString();
            signal_4 = RBIRSignal.LG_ch4.toString();
            signal_5 = RBIRSignal.LG_ch5.toString();
            signal_6 = RBIRSignal.LG_ch6.toString();
            signal_7 = RBIRSignal.LG_ch7.toString();
            signal_8 = RBIRSignal.LG_ch8.toString();
            signal_9 = RBIRSignal.LG_ch9.toString();
        }
        else if( device.equals("S_KT"))
        {
            signal_0 = RBIRSignal.KT_ch0.toString();
            signal_1 = RBIRSignal.KT_ch1.toString();
            signal_2 = RBIRSignal.KT_ch2.toString();
            signal_3 = RBIRSignal.KT_ch3.toString();
            signal_4 = RBIRSignal.KT_ch4.toString();
            signal_5 = RBIRSignal.KT_ch5.toString();
            signal_6 = RBIRSignal.KT_ch6.toString();
            signal_7 = RBIRSignal.KT_ch7.toString();
            signal_8 = RBIRSignal.KT_ch8.toString();
            signal_9 = RBIRSignal.KT_ch9.toString();
        }
        else if( device.equals("S_SK"))
        {
            signal_0 = RBIRSignal.SK_ch0.toString();
            signal_1 = RBIRSignal.SK_ch1.toString();
            signal_2 = RBIRSignal.SK_ch2.toString();
            signal_3 = RBIRSignal.SK_ch3.toString();
            signal_4 = RBIRSignal.SK_ch4.toString();
            signal_5 = RBIRSignal.SK_ch5.toString();
            signal_6 = RBIRSignal.SK_ch6.toString();
            signal_7 = RBIRSignal.SK_ch7.toString();
            signal_8 = RBIRSignal.SK_ch8.toString();
            signal_9 = RBIRSignal.SK_ch9.toString();
        }
        else if( device.equals("S_DLIVE1"))
        {
            signal_0 = RBIRSignal.DLIVE1_ch0.toString();
            signal_1 = RBIRSignal.DLIVE1_ch1.toString();
            signal_2 = RBIRSignal.DLIVE1_ch2.toString();
            signal_3 = RBIRSignal.DLIVE1_ch3.toString();
            signal_4 = RBIRSignal.DLIVE1_ch4.toString();
            signal_5 = RBIRSignal.DLIVE1_ch5.toString();
            signal_6 = RBIRSignal.DLIVE1_ch6.toString();
            signal_7 = RBIRSignal.DLIVE1_ch7.toString();
            signal_8 = RBIRSignal.DLIVE1_ch8.toString();
            signal_9 = RBIRSignal.DLIVE1_ch9.toString();
        }
        else
        {
            Toast.makeText(context, "Not Found Deivce", Toast.LENGTH_SHORT).show();
        }
        //if(param.length() >  idex) {
        //Log.d(TAG, "ChSignallMaker(paramlenth) : "+ param.length() + "(" +idex+")");
        String scommand = "";

        //Log.d(TAG, "ChSignallMaker(charAt) : "+ param.charAt(idex));
        switch (param.charAt(idex)) {
            case '0':
                scommand = signal_0;
                break;
            case '1':
                scommand = signal_1;
                break;
            case '2':
                scommand = signal_2;
                break;
            case '3':
                scommand = signal_3;
                break;
            case '4':
                scommand = signal_4;
                break;
            case '5':
                scommand = signal_5;
                break;
            case '6':
                scommand = signal_6;
                break;
            case '7':
                scommand = signal_7;
                break;
            case '8':
                scommand = signal_8;
                break;
            case '9':
                scommand = signal_9;
                break;
        }
        if("internelIR".equals(sid))
        {

        }
        else if("nodevice".equals(sid))
        {
            //장치없음
        }
        else
        {
            VIRCommand task = new VIRCommand(this);
            task.execute(new VIRDataValue(cur_url,"nec",scommand,sid));
        }
        //Log.d(TAG, "ChSignallMaker(scommand) : "+ scommand);
        //task.execute(new VIRDataValue(cur_url,"nec",scommand,sid));
        //}
    }
    @Override
    public void VIRCommandListener(String msg) {

        /*
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "VIRCommandListener : finish"+cur_command+" / "+ cur_param.length() +" / " + msg);
        cur_index++;

        if(cur_command.equals("CHNUMBER") && cur_param.length() >  cur_index &&  msg.equals("200") == true )
        {
            Log.d(TAG, "NextCommand : "+cur_device+" / "+ cur_param +" / "+cur_index );
            ChSignallMaker( cur_device, cur_param ,cur_index );
            //NControlMaker(cur_device, cur_param);
        }
        else if(cur_command.equals("CHNUMBER") && cur_param.length() ==  cur_index && msg.equals("200")== true )
        {
            Log.d(TAG, "NextCommand ControlMaker : "+cur_device+"+ ENTER  ");
            ControlMaker(cur_device,"ENTER");
        }
        else  if(msg.equals("error") == true)
        {
            Toast.makeText(context, "Send error", Toast.LENGTH_LONG).show();
        }
//        else if(cur_command.equals("CHNUMBER") && cur_param.length()+1 == cur_index && msg.equals("200"))
//        {
//            Log.d(TAG, "NextCommand ControlMaker : "+cur_device+"+ ENTER  ");
//            ControlMaker(cur_device,"ENTER");
//        }
        else
        {
            Toast.makeText(context, "Send signal", Toast.LENGTH_SHORT).show();
        }
         */
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cur_index++;

        if(cur_param.matches("-?\\d+(\\.\\d+)?")&&cur_param.length() >  cur_index &&  msg.equals("200") == true )
        {
            NControlMaker(cur_device, cur_param);
        }
        else if(cur_param.matches("-?\\d+(\\.\\d+)?")&&cur_param.length() ==  cur_index && msg.equals("200")== true )
        {
            Log.d(TAG, "NextCommand ControlMaker : "+cur_device+"+ ENTER  ");
            NControlMaker(cur_device,"ENTER");
        }
    }

    protected String duration2count(double frequency,String countPattern) {
        List<String> list = new ArrayList<String>(Arrays.asList(countPattern.split(",")));
        double pulses = 1000000 / frequency;
        int count;
        double duration;
        //list.remove(0);
        for (int i = 0; i < list.size(); i++) {
            count = Integer.parseInt(list.get(i).trim());
            duration = count / pulses;
            Double value = new Double(duration);
            list.set(i, Integer.toString(value.intValue()));
        }

        String durationPattern = "";
        for (String s : list) {
            durationPattern += s + ",";
        }

        Log.d(TAG, "Frequency: " + frequency);
        Log.d(TAG, "Duration Pattern: " + durationPattern);

        return durationPattern;
    }
}
