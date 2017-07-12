package com.example.reebotui.channel;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.reebotui.epglist.EPGListViewAdapter;
import com.example.reebotui.epglist.EPGListViewItem;
import com.example.reebotui.epglist.EPGProgramListViewAdapter;
import com.example.reebotui.interfaceclass.OnEPGItemClickListener;
import com.example.reebotui.interfaceclass.OnEPGListClickListener;
import com.exp.rb.reebot.R;

import java.util.ArrayList;

/**
 * Created by silver on 2017-06-24.
 */

public class ProgramListVerticalDialog extends Dialog {

    private Context context;
    private EPGListViewItem epgListViewItem = null;
    ArrayList<EPGListViewItem> epgListViewItemList = null;

    private OnEPGItemClickListener onEPGItemClickListener;
    private OnEPGListClickListener onEPGListClickListener;

    private View ll_dialog;
    private ProgressBar pb;

    public ProgramListVerticalDialog(@NonNull Context context, EPGListViewItem epgListViewItem, OnEPGItemClickListener onEPGItemClickListener) {
        super(context);
        this.context = context;
        this.epgListViewItem = epgListViewItem;
        this.onEPGItemClickListener = onEPGItemClickListener;
    }

    public ProgramListVerticalDialog(@NonNull Context context, ArrayList<EPGListViewItem> epgListViewItemList, OnEPGListClickListener onEPGListClickListener) {
        super(context);
        this.context = context;
        this.epgListViewItemList = epgListViewItemList;
        this.onEPGListClickListener = onEPGListClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_programlist_vertical);

        ll_dialog = findViewById(R.id.ll_dialog);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_programlist);

//            if (mColumnCount <= 1) {
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            } else {
//                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
//            }
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            recyclerView.setAdapter(new MychannelRecyclerViewAdapter(DummyContent.ITEMS, mListener));

        TextView tv_text = (TextView) findViewById(R.id.tv_text);

        if (epgListViewItemList != null) {
            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

            EPGListViewAdapter epgListViewAdapter = new EPGListViewAdapter(context, onEPGListClickListener);
            epgListViewAdapter.setShowOption(false);
            epgListViewAdapter.addItem(epgListViewItemList);
            recyclerView.setAdapter(epgListViewAdapter);

            if (epgListViewItemList.size() > 0) {
                tv_text.setVisibility(View.GONE);
            }
        } else if (epgListViewItem != null) {
            EPGProgramListViewAdapter epgProgramListViewAdapter = new EPGProgramListViewAdapter(context, onEPGItemClickListener);
            epgProgramListViewAdapter.addItem(epgListViewItem);
            recyclerView.setAdapter(epgProgramListViewAdapter);

            tv_text.setVisibility(View.GONE);
        }


//        addItem();
    }

    @Override
    public void show() {
        super.show();

        try {
            final WindowManager.LayoutParams layoutParams = getWindow().getAttributes();

            DisplayMetrics dm = context.getResources().getDisplayMetrics();

            final int h = dm.heightPixels / 3 * 2;

//        System.out.println("Reebot Dialog dm.heightPixels: " + dm.heightPixels);
//        System.out.println("Reebot Dialog layoutParams.height: " + layoutParams.height);
//        System.out.println("Reebot Dialog ll_dialog.getHeight(): " + ll_dialog.getHeight());

            ll_dialog.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
//                System.out.println("Reebot Dialog getViewTreeObserver ll_dialog.getHeight(): " + ll_dialog.getHeight());
                    int lh = ll_dialog.getHeight();
                    if (lh > h) {
                        layoutParams.height = h;
                        getWindow().setAttributes(layoutParams);
                    } else {

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    private void addItem() {
//        pb.setVisibility(View.VISIBLE);
//
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
//        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm");
//
//        ll_scroll.removeAllViews();
//
//        for (int i = 0; i < epgListViewItem.getList().size(); i++) {
////            System.out.println(TAG + ", " + epgListViewItem.getList().get(i).getProgramTitle() + " : " + i);
//            System.out.println("reebot EPGListViewAdapter onBindViewHolder getProgramTitle" + epgListViewItem.getList().get(i).getProgramTitle() + " : " + i);
//            System.out.println("reebot EPGListViewAdapter onBindViewHolder getChnumber" + epgListViewItem.getList().get(i).getChnumber() + " : " + i);
//
//            View itemView = inflater.inflate(R.layout.epglistview_item_program, null, false);
//            itemView.setLayoutParams(new ViewGroup.LayoutParams(itemWidth, context.getResources().getDimensionPixelSize(R.dimen.list_height)));
////            itemView.getLayoutParams().width = itemWidth;
////            itemView.setTag(epgListViewItem.getSelectedItem().getChnumber() + i);
//            TextView tv_programTitle = (TextView) itemView.findViewById(R.id.tv_programTitle);
//            TextView tv_programTime = (TextView) itemView.findViewById(R.id.tv_programTime);
//            TextView tv_start_time = (TextView) itemView.findViewById(R.id.tv_start_time);
//            TextView tv_end_time = (TextView) itemView.findViewById(R.id.tv_end_time);
//            ProgressBar pb_time = (ProgressBar) itemView.findViewById(R.id.pb_time);
//
//            final String programTitle = epgListViewItem.getList().get(i).getProgramTitle();
//            final String chnumber = epgListViewItem.getList().get(i).getChnumber();
//            final int index = i;
//
//            tv_programTitle.setText(epgListViewItem.getList().get(i).getProgramTitle());
//            tv_programTime.setText(epgListViewItem.getList().get(i).getProgramTime());
//
//
//            try {
//                Calendar calendar = Calendar.getInstance(Locale.KOREA);
//                Date startDate = simpleDateFormat1.parse(epgListViewItem.getList().get(i).getStarttime());
//                Date endDate = simpleDateFormat1.parse(epgListViewItem.getList().get(i).getEndtime());
//                Date cDate = new Date(calendar.getTimeInMillis());
//                long tTime = endDate.getTime() - startDate.getTime();
//                long cTime = cDate.getTime() - startDate.getTime();
//                tTime = tTime / 1000;
//                cTime = cTime / 1000;
//                pb_time.setMax((int) (tTime));
//                pb_time.setProgress((int) (cTime));
//                tv_start_time.setText(simpleDateFormat2.format(startDate));
//                tv_end_time.setText(simpleDateFormat2.format(endDate));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    System.out.println("reebot ProgramListDialog onClick " + index + " " + programTitle);
//                    if (index == 0) {
//                        Toast.makeText(context, "channel 변경 " + chnumber, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(context, "channel 예약 " + chnumber, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//
//            ll_scroll.addView(itemView);
//        }
//
//        pb.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                pb.setVisibility(View.GONE);
//            }
//        }, 1000);
//    }
}
