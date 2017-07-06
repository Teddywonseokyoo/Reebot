package com.example.reebotui.channel;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.example.reebotui.ir.VIRCommandMaker;
import com.exp.rb.reebot.R;


/**
 * Created by silver on 2017-06-24.
 */

public class RemoteControlDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private String url;
    private String email;
    private String sid;

    public RemoteControlDialog(@NonNull Context context, String url, String email, String sid) {
        super(context);

        this.context = context;
        this.url = url;
        this.email = email;
        this.sid = sid;

        setContentView(R.layout.dialog_remotecontrol);

        findViewById(R.id.btn_ir_power_all).setOnClickListener(this);
        findViewById(R.id.btn_ir_power_tv).setOnClickListener(this);
        findViewById(R.id.btn_ir_power_sbox).setOnClickListener(this);
        findViewById(R.id.btn_ir_channel_pre).setOnClickListener(this);
        findViewById(R.id.btn_channel_up).setOnClickListener(this);
        findViewById(R.id.btn_channel_down).setOnClickListener(this);
        findViewById(R.id.btn_volume_up).setOnClickListener(this);
        findViewById(R.id.btn_volume_down).setOnClickListener(this);
        findViewById(R.id.btn_volume_mute).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ir_power_all:
                new VIRCommandMaker(context, url, email, "TV", "POWER", sid);
                new VIRCommandMaker(context, url, email, "STBOX", "POWER", sid);
                break;
            case R.id.btn_ir_power_tv:
                new VIRCommandMaker(context, url, email, "TV", "POWER", sid);
                break;
            case R.id.btn_ir_power_sbox:
                new VIRCommandMaker(context, url, email, "STBOX", "POWER", sid);
                break;
            case R.id.btn_ir_channel_pre:
                new VIRCommandMaker(context, url, email, "STBOX", "BACK", sid);
                break;

            case R.id.btn_channel_up:
                new VIRCommandMaker(context, url, email, "STBOX", "CH+", sid);
                break;
            case R.id.btn_channel_down:
                new VIRCommandMaker(context, url, email, "STBOX", "CH-", sid);
                break;

            case R.id.btn_volume_up:
                new VIRCommandMaker(context, url, email, "STBOX", "VOL+", sid);
                break;
            case R.id.btn_volume_down:
                new VIRCommandMaker(context, url, email, "STBOX", "VOL-", sid);
                break;
            case R.id.btn_volume_mute:
                new VIRCommandMaker(context, url, email, "STBOX", "MUTE", sid);
                break;
        }
    }
}
