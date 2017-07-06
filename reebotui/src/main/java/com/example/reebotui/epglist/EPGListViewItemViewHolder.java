package com.example.reebotui.epglist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.exp.rb.reebot.R;


/**
 * Created by silver on 2017-06-23.
 */

public class EPGListViewItemViewHolder extends RecyclerView.ViewHolder {

    View itemView;
    View rl_channel_logo;
    View ll_option;
    View ll_time;
    ImageView iv_channel_logo;
    ImageView iv_bookmark;
    ImageView iv_channel_list;
    ImageView iv_delete;

//    HorizontalScrollView hs_scroll;
//    LinearLayout ll_scroll;
    TextView tv_booking_time;
    TextView tv_programTitle;
    TextView tv_programTime;
    TextView tv_start_time;
    TextView tv_end_time;
    TextView tv_date;

    ProgressBar pb_time;

    public EPGListViewItemViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;
        rl_channel_logo = itemView.findViewById(R.id.rl_channel_logo);
        ll_option = itemView.findViewById(R.id.ll_option);
        ll_time = itemView.findViewById(R.id.ll_time);
        iv_channel_logo = (ImageView) itemView.findViewById(R.id.iv_channel_logo);
        iv_bookmark = (ImageView) itemView.findViewById(R.id.iv_bookmark);
        iv_channel_list = (ImageView) itemView.findViewById(R.id.iv_channel_list);
        iv_delete = (ImageView) itemView.findViewById(R.id.iv_delete);
//        hs_scroll = (HorizontalScrollView) itemView.findViewById(R.id.hs_scroll);
//        ll_scroll = (LinearLayout) itemView.findViewById(R.id.ll_scroll);
        tv_booking_time = (TextView) itemView.findViewById(R.id.tv_booking_time);
        tv_programTitle = (TextView) itemView.findViewById(R.id.tv_programTitle);
        tv_programTime = (TextView) itemView.findViewById(R.id.tv_programTime);
        tv_start_time = (TextView) itemView.findViewById(R.id.tv_start_time);
        tv_end_time = (TextView) itemView.findViewById(R.id.tv_end_time);
        tv_date = (TextView) itemView.findViewById(R.id.tv_date);
        pb_time = (ProgressBar) itemView.findViewById(R.id.pb_time);

    }
}
