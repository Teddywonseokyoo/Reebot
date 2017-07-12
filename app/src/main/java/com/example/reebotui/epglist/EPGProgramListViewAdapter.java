package com.example.reebotui.epglist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.reebotui.interfaceclass.OnEPGItemClickListener;
import com.exp.rb.reebot.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by silver on 2017-06-23.
 */

public class EPGProgramListViewAdapter extends RecyclerView.Adapter<EPGListViewItemViewHolder> {
    private static final String TAG = "ReeBot(EPGPLViewAdapter)";
    private Context context;
    private EPGListViewItem epgListViewItem;
    private ArrayList<EPGItem> listViewItemList = new ArrayList<EPGItem>();
    private final OnEPGItemClickListener onEPGItemClickListener;

    private SimpleDateFormat simpleDateFormat1;
    private SimpleDateFormat simpleDateFormat2;
    private SimpleDateFormat simpleDateFormat3;
//    private LayoutInflater inflater;
//    private int itemWidth = 0;

    public EPGProgramListViewAdapter(Context context, OnEPGItemClickListener onEPGItemClickListener) {
        this.context = context;
        this.onEPGItemClickListener = onEPGItemClickListener;

//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
        simpleDateFormat2 = new SimpleDateFormat("HH:mm");
        simpleDateFormat3 = new SimpleDateFormat("MM/dd");

//        DisplayMetrics dm = context.getResources().getDisplayMetrics();
//        itemWidth = dm.widthPixels / 3 * 2;

    }

    //    ViewGroup parent;
    @Override
    public EPGListViewItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        System.out.println("reebot EPGListViewAdapter onCreateViewHolder ");
//        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.epglistview_item_vertical, parent, false);


        return new EPGListViewItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EPGListViewItemViewHolder holder, int position) {
//        System.out.println("reebot EPGListViewAdapter onBindViewHolder ");

//        final EPGListViewItem epgListViewItem = listViewItemList.get(position);


//        holder.rl_channel_logo.setVisibility(View.GONE);
//        holder.ll_option.setVisibility(View.GONE);


//        holder.iv_bookmark.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listFragmentInteractionListener.onListFragmentInteraction(epgListViewItem);
//            }
//        });
//
//        holder.iv_channel_list.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ProgramListVerticalDialog programListDialog = new ProgramListVerticalDialog(context, epgListViewItem);
//                programListDialog.show();
//            }
//        });

        final EPGItem epgItem = listViewItemList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                listFragmentInteractionListener.onListFragmentInteraction(epgListViewItem);
                onEPGItemClickListener.onEPGItemClickListener(epgItem);
            }
        });

//        holder.iv_channel_logo.setImageDrawable(epgListViewItem.getChlogoiconDrawable());
        holder.tv_programTitle.setText(epgItem.getProgramTitle());
        holder.tv_programTime.setText(epgItem.getProgramTime());
        try {
//            Calendar calendar = Calendar.getInstance(Locale.KOREA);
            Date startDate = simpleDateFormat1.parse(epgItem.getStarttime());
            Date endDate = simpleDateFormat1.parse(epgItem.getEndtime());
//            Date cDate = new Date(calendar.getTimeInMillis());
//            long tTime = endDate.getTime() - startDate.getTime();
//            long cTime = cDate.getTime() - startDate.getTime();
//            tTime = tTime / 1000;
//            cTime = cTime / 1000;
//            holder.pb_time.setMax((int) (tTime));
//            holder.pb_time.setProgress((int) (cTime));
            holder.tv_start_time.setText(simpleDateFormat2.format(startDate));
            holder.tv_end_time.setText(simpleDateFormat2.format(endDate));
            holder.tv_date.setText(simpleDateFormat3.format(startDate));


            if (position + 1 < listViewItemList.size()) {
                EPGItem nextEpgItem = listViewItemList.get(position + 1);

                Date nextStartDate = simpleDateFormat1.parse(nextEpgItem.getStarttime());
//                Date nextEndDate = simpleDateFormat1.parse(nextEpgItem.getEndtime());


                Log.d(TAG, "epgItem.getEndtime(): " + simpleDateFormat2.format(endDate));
                Log.d(TAG, "nextEpgItem.getEndtime(): " + simpleDateFormat2.format(nextStartDate));

                if (simpleDateFormat2.format(endDate).equals(simpleDateFormat2.format(nextStartDate))) {
                    holder.tv_end_time.setVisibility(View.GONE);
                } else {
                    holder.tv_end_time.setVisibility(View.VISIBLE);
                }
            }

            if (position == 0) {
                holder.tv_date.setVisibility(View.VISIBLE);
            }
            else {
                holder.tv_date.setVisibility(View.GONE);
            }
//            else
//            {
//
//                EPGItem preEpgItem = listViewItemList.get(position - 1);
//                Date preStartDate = simpleDateFormat1.parse(preEpgItem.getStarttime());
//
//                Log.d(TAG, "preStartDate.startDate(): " + simpleDateFormat3.format(preStartDate));
//                Log.d(TAG, "epgItem.startDate(): " + simpleDateFormat3.format(startDate));
//
//
//                if (simpleDateFormat3.format(startDate).equals(simpleDateFormat3.format(preStartDate))) {
//                    holder.tv_date.setVisibility(View.GONE);
//                } else {
//                    holder.tv_date.setVisibility(View.VISIBLE);
//                    holder.tv_date.setText(simpleDateFormat3.format(startDate));
//                }
//
////                Calendar preCalendar = Calendar.getInstance();
////                preCalendar.setTime(preStartDate);
////                Calendar calendar = Calendar.getInstance();
////                calendar.setTime(startDate);
////
////                if (preCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
////                    holder.tv_date.setVisibility(View.GONE);
////                } else {
////                    holder.tv_date.setVisibility(View.VISIBLE);
////                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public void addItem(EPGListViewItem epgListViewItem) {
        this.epgListViewItem = epgListViewItem;
        listViewItemList = epgListViewItem.getList();
        notifyDataSetChanged();
    }
}
