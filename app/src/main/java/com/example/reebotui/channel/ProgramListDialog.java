package com.example.reebotui.channel;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reebotui.epglist.EPGListViewItem;
import com.exp.rb.reebot.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by silver on 2017-06-24.
 */

public class ProgramListDialog extends Dialog {

    private Context context;
    private EPGListViewItem epgListViewItem;

    private HorizontalScrollView hs_scroll;
    private LinearLayout ll_scroll;
    private ProgressBar pb;
    private int itemWidth = 0;

    public ProgramListDialog(@NonNull Context context, EPGListViewItem epgListViewItem) {
        super(context);
        this.context = context;
        this.epgListViewItem = epgListViewItem;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        itemWidth = dm.widthPixels / 3 * 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_programlist);
        hs_scroll = (HorizontalScrollView) findViewById(R.id.hs_scroll);
        ll_scroll = (LinearLayout) findViewById(R.id.ll_scroll);
        pb = (ProgressBar) findViewById(R.id.pb);

        addItem();
    }

    private void addItem() {
        pb.setVisibility(View.VISIBLE);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm");

        ll_scroll.removeAllViews();

        for (int i = 0; i < epgListViewItem.getList().size(); i++) {
//            System.out.println(TAG + ", " + epgListViewItem.getList().get(i).getProgramTitle() + " : " + i);
            System.out.println("reebot EPGListViewAdapter onBindViewHolder getProgramTitle" + epgListViewItem.getList().get(i).getProgramTitle() + " : " + i);
            System.out.println("reebot EPGListViewAdapter onBindViewHolder getChnumber" + epgListViewItem.getList().get(i).getChnumber() + " : " + i);

            View itemView = inflater.inflate(R.layout.epglistview_item_program, null, false);
            itemView.setLayoutParams(new ViewGroup.LayoutParams(itemWidth, context.getResources().getDimensionPixelSize(R.dimen.list_height)));
//            itemView.getLayoutParams().width = itemWidth;
//            itemView.setTag(epgListViewItem.getSelectedItem().getChnumber() + i);
            TextView tv_programTitle = (TextView) itemView.findViewById(R.id.tv_programTitle);
            TextView tv_programTime = (TextView) itemView.findViewById(R.id.tv_programTime);
            TextView tv_start_time = (TextView) itemView.findViewById(R.id.tv_start_time);
            TextView tv_end_time = (TextView) itemView.findViewById(R.id.tv_end_time);
            ProgressBar pb_time = (ProgressBar) itemView.findViewById(R.id.pb_time);

            final String programTitle = epgListViewItem.getList().get(i).getProgramTitle();
            final String chnumber = epgListViewItem.getList().get(i).getChnumber();
            final int index = i;

            tv_programTitle.setText(epgListViewItem.getList().get(i).getProgramTitle());
            tv_programTime.setText(epgListViewItem.getList().get(i).getProgramTime());


            try {
                Calendar calendar = Calendar.getInstance(Locale.KOREA);
                Date startDate = simpleDateFormat1.parse(epgListViewItem.getList().get(i).getStarttime());
                Date endDate = simpleDateFormat1.parse(epgListViewItem.getList().get(i).getEndtime());
                Date cDate = new Date(calendar.getTimeInMillis());
                long tTime = endDate.getTime() - startDate.getTime();
                long cTime = cDate.getTime() - startDate.getTime();
                tTime = tTime / 1000;
                cTime = cTime / 1000;
                pb_time.setMax((int) (tTime));
                pb_time.setProgress((int) (cTime));
                tv_start_time.setText(simpleDateFormat2.format(startDate));
                tv_end_time.setText(simpleDateFormat2.format(endDate));
            } catch (Exception e) {
                e.printStackTrace();
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("reebot ProgramListDialog onClick " + index + " " + programTitle);
                    if (index == 0) {
                        Toast.makeText(context, "channel 변경 " + chnumber, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "channel 예약 " + chnumber, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ll_scroll.addView(itemView);
        }

        pb.postDelayed(new Runnable() {
            @Override
            public void run() {
                pb.setVisibility(View.GONE);
            }
        }, 1000);
    }
}
