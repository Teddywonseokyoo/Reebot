package com.example.reebotui.epglist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.reebotui.info.BookmarkInfo;
import com.example.reebotui.interfaceclass.OnEPGListClickListener;
import com.exp.rb.reebot.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by silver on 2017-06-23.
 */

public class EPGListViewAdapter extends RecyclerView.Adapter<EPGListViewItemViewHolder> {

    public static int LIST_TYPE_BOOKING = 1;

    private Context context;
    private ArrayList<EPGListViewItem> listViewItemList = new ArrayList<EPGListViewItem>();
    private final OnEPGListClickListener onEPGListClickListener;

    private SimpleDateFormat simpleDateFormat1;
    private SimpleDateFormat simpleDateFormat2;
    private SimpleDateFormat simpleDateFormat3;
    private SimpleDateFormat simpleDateFormat4;
//    private LayoutInflater inflater;
//    private int itemWidth = 0;

    private BookmarkInfo bookmarkInfo;
    private ArrayList<String> bookmardList;

    private boolean isShowOption = true;
    private int type = 0;

    public void setShowOption(boolean showOption) {
        isShowOption = showOption;
    }

    public void setType(int type) {
        this.type = type;
    }

    public EPGListViewAdapter(Context context, OnEPGListClickListener listener) {
        this.context = context;
        onEPGListClickListener = listener;

//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
        simpleDateFormat2 = new SimpleDateFormat("HH:mm");
        simpleDateFormat3 = new SimpleDateFormat("MM/dd");
        simpleDateFormat4 = new SimpleDateFormat("MM/dd HH:mm");

//        DisplayMetrics dm = context.getResources().getDisplayMetrics();
//        itemWidth = dm.widthPixels / 3 * 2;

        bookmarkInfo = new BookmarkInfo(context);


    }

    //    ViewGroup parent;
    @Override
    public EPGListViewItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        System.out.println("reebot EPGListViewAdapter onCreateViewHolder ");
//        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.epglistview_item, parent, false);


        return new EPGListViewItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EPGListViewItemViewHolder holder, final int position) {
//        System.out.println("reebot EPGListViewAdapter onBindViewHolder ");

        final EPGListViewItem epgListViewItem = listViewItemList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEPGListClickListener.OnEPGListClickListener(epgListViewItem);
            }
        });


        holder.iv_channel_logo.setImageDrawable(epgListViewItem.getChlogoiconDrawable());
        holder.tv_programTitle.setText(epgListViewItem.getList().get(0).getProgramTitle());
        holder.tv_programTime.setText(epgListViewItem.getList().get(0).getProgramTime());

        if (!isShowOption) {
            holder.ll_option.setVisibility(View.GONE);
        }
//        else

        {
            if (type == LIST_TYPE_BOOKING) {
                holder.ll_time.setVisibility(View.GONE);
                holder.tv_booking_time.setVisibility(View.VISIBLE);
                try {
                    Date startDate = simpleDateFormat1.parse(epgListViewItem.getList().get(0).getStarttime());
                    holder.tv_booking_time.setText(simpleDateFormat4.format(startDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                holder.iv_bookmark.setVisibility(View.GONE);
                holder.iv_channel_list.setVisibility(View.GONE);
                holder.iv_delete.setVisibility(View.VISIBLE);
                holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onEPGListClickListener.OnEPGListClickListener(epgListViewItem, holder.iv_delete.getId());
                    }
                });
            } else {
                holder.ll_time.setVisibility(View.VISIBLE);
                holder.tv_booking_time.setVisibility(View.GONE);
                holder.iv_bookmark.setVisibility(View.VISIBLE);
                holder.iv_channel_list.setVisibility(View.VISIBLE);
                holder.iv_delete.setVisibility(View.GONE);

                holder.iv_bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        bookmardList = bookmarkInfo.getBookmarkList();
                        if (checkBookmark(epgListViewItem.getList().get(0).getChnumber())) {
                            bookmardList.remove(epgListViewItem.getList().get(0).getChnumber());
                            epgListViewItem.setBookmark(false);
                        } else {
                            bookmardList.add(epgListViewItem.getList().get(0).getChnumber());
                            epgListViewItem.setBookmark(true);
                        }
                        bookmarkInfo.setBookmard(bookmardList);
                        notifyDataSetChanged();

                        onEPGListClickListener.OnEPGListClickListener(epgListViewItem, holder.iv_bookmark.getId());
                    }
                });

                holder.iv_channel_list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                ProgramListVerticalDialog programListDialog = new ProgramListVerticalDialog(context, epgListViewItem);
//                programListDialog.show();
                        onEPGListClickListener.OnEPGListClickListener(epgListViewItem, holder.iv_channel_list.getId());
                    }
                });

                if (epgListViewItem.isBookmark()) {
                    holder.iv_bookmark.setImageResource(R.drawable.icon_fav_active);
                } else {
                    holder.iv_bookmark.setImageResource(R.drawable.icon_fav_normal);
                }


                try {
                    Calendar calendar = Calendar.getInstance(Locale.KOREA);
                    Date startDate = simpleDateFormat1.parse(epgListViewItem.getList().get(0).getStarttime());
                    Date endDate = simpleDateFormat1.parse(epgListViewItem.getList().get(0).getEndtime());
                    Date cDate = new Date(calendar.getTimeInMillis());
                    long tTime = endDate.getTime() - startDate.getTime();
                    long cTime = cDate.getTime() - startDate.getTime();
                    tTime = tTime / 1000;
                    cTime = cTime / 1000;
                    holder.pb_time.setMax((int) (tTime));
                    holder.pb_time.setProgress((int) (cTime));
                    holder.tv_start_time.setText(simpleDateFormat2.format(startDate));
                    holder.tv_end_time.setText(simpleDateFormat2.format(endDate));
                    holder.tv_date.setText(simpleDateFormat3.format(endDate));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }


    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public void addItem(EPGListViewItem epgListViewItem) {
        listViewItemList.add(epgListViewItem);
        notifyDataSetChanged();
    }

    public void addItem(ArrayList<EPGListViewItem> listViewItemList) {
        bookmardList = bookmarkInfo.getBookmarkList();
        for (int i = 0; i < listViewItemList.size(); i++) {
            if (checkBookmark(listViewItemList.get(i).getList().get(0).getChnumber())) {
                listViewItemList.get(i).setBookmark(true);
            }
        }
        this.listViewItemList = listViewItemList;
        notifyDataSetChanged();
    }

    public void refreshBookmark() {
        bookmardList = bookmarkInfo.getBookmarkList();

        for (int i = 0; i < listViewItemList.size(); i++) {
            listViewItemList.get(i).setBookmark(false);
        }

        for (int i = 0; i < listViewItemList.size(); i++) {
            if (checkBookmark(listViewItemList.get(i).getList().get(0).getChnumber())) {
                listViewItemList.get(i).setBookmark(true);
            }
        }
        this.listViewItemList = listViewItemList;
        notifyDataSetChanged();
    }

    public void clearItem() {
        listViewItemList.clear();
        notifyDataSetChanged();
    }

    private boolean checkBookmark(String channel) {
        boolean check = false;

        for (int i = 0; i < bookmardList.size(); i++) {
            if (bookmardList.get(i).equals(channel)) {
                check = true;
                break;
            }
        }
        return check;
    }
}
