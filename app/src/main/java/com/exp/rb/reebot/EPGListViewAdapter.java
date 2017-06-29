package com.exp.rb.reebot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;																				 
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by freem on 2017-03-06.
 */

public class EPGListViewAdapter extends BaseAdapter {

    private static final String TAG = "ReeBot(EPGListViewAdapter)";
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<EPGListViewItem> listViewItemList = new ArrayList<EPGListViewItem>() ;
    private SimpleDateFormat simpleDateFormat1;
    private SimpleDateFormat simpleDateFormat2;
	private LayoutInflater inflater;
    private Context context;						
    // ListViewAdapter의 생성자
    public EPGListViewAdapter(Context context) {
		this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);							   																				  
        simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
        simpleDateFormat2 = new SimpleDateFormat("HH:mm");
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
		ViewHolder viewHolder;					  
        if (convertView == null) {
            //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.epglistview_item, parent, false);
			 viewHolder = new ViewHolder();

            viewHolder.convertView = convertView;
            viewHolder.EPGL0 = convertView.findViewById(R.id.EPGL0) ;
            viewHolder.chiconImageView = (ImageView) convertView.findViewById(R.id.ChannelLogImg) ;
            viewHolder.bsiconImageView = (ImageView) convertView.findViewById(R.id.BStateImg) ;
            viewHolder.programtitleTextView = (TextView) convertView.findViewById(R.id.programTitleTextView) ;
            viewHolder.programTimeTextView = (TextView) convertView.findViewById(R.id.programTimeTextView) ;
            viewHolder.tv_start_time = (TextView) convertView.findViewById(R.id.tv_start_time);
            viewHolder.tv_end_time = (TextView) convertView.findViewById(R.id.tv_end_time);
            viewHolder.pb_time = (ProgressBar) convertView.findViewById(R.id.pb_time);

            convertView.setTag(viewHolder);							  							   
        }
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }	
		 /*
        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView chiconImageView = (ImageView) convertView.findViewById(R.id.ChannelLogImg) ;
        ImageView bsiconImageView = (ImageView) convertView.findViewById(R.id.BStateImg) ;
        TextView programtitleTextView = (TextView) convertView.findViewById(R.id.programTitleTextView) ;
        TextView programTimeTextView = (TextView) convertView.findViewById(R.id.programTimeTextView) ;
        TextView tv_start_time = (TextView) convertView.findViewById(R.id.tv_start_time);
        TextView tv_end_time = (TextView) convertView.findViewById(R.id.tv_end_time);
        //TextView tv_current_time = (TextView) convertView.findViewById(R.id.tv_current_time);

        ProgressBar pb_time = (ProgressBar) convertView.findViewById(R.id.pb_time);
//        pb_time.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
*/
        /*
        epglayout.setOnClickListener(new   View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("onClick", "onClick");
            }
        } );
        */

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        EPGListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
		viewHolder.chiconImageView.setImageDrawable(listViewItem.getChLogoIcon());
        viewHolder.bsiconImageView.setImageDrawable(listViewItem.getBsLogoIcon());
        viewHolder.programtitleTextView.setText(listViewItem.getProgramTitle());
        viewHolder.programTimeTextView.setText(listViewItem.getProgramTime());
        //chiconImageView.setImageDrawable(listViewItem.getChLogoIcon());
        //bsiconImageView.setImageDrawable(listViewItem.getBsLogoIcon());
        //programtitleTextView.setText(listViewItem.getProgramTitle());
        //programTimeTextView.setText(listViewItem.getProgramTime());

        try {
            Calendar calendar = Calendar.getInstance(Locale.KOREA);
            Date startDate = simpleDateFormat1.parse(listViewItem.getStarttime());
            Date endDate = simpleDateFormat1.parse(listViewItem.getEndtime());
            Date cDate = new Date(calendar.getTimeInMillis());
            long tTime = endDate.getTime() - startDate.getTime();
            long cTime = cDate.getTime() - startDate.getTime();
            tTime = tTime / 1000;
            cTime = cTime / 1000;
			viewHolder.pb_time.setMax((int) (tTime));
            viewHolder.pb_time.setProgress((int) (cTime));
            viewHolder.tv_start_time.setText(simpleDateFormat2.format(startDate));
            viewHolder.tv_end_time.setText(simpleDateFormat2.format(endDate));
            //pb_time.setMax((int) (tTime));
            //pb_time.setProgress((int) (cTime));
            //tv_start_time.setText(simpleDateFormat2.format(startDate));
            //tv_end_time.setText(simpleDateFormat2.format(endDate));
            //tv_current_time.setText(simpleDateFormat2.format(cDate));
        } catch (Exception e) {
            //Log.d(TAG, "Exception : (" + position + ")" + e.getMessage());
            e.printStackTrace();
        }
		if(listViewItem.isPrevious())
        {
            //Log.d(TAG, "getView : (" + position + ")" + "isPrevious");
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_right_out);
            viewHolder.EPGL0.startAnimation(animation);
            listViewItem.setPrevious(false);
        }
        else if(listViewItem.isNext())
        {
            //Log.d(TAG, "getView : (" + position + ")" + "isNext");
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_left_out);
            viewHolder.EPGL0.startAnimation(animation);
            listViewItem.setNext(false);
        }
        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    //@Override
    //public Object getItem(int position) {
    //    return listViewItemList.get(position) ;
    //}
	
	  // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public EPGListViewItem getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    //(getImage(context,thisentry.chlogo),ContextCompat.getDrawable(context, R.drawable.livetv), thisentry.program_title, entryInfo ,thisentry.chnumber,thisentry.category_kr,thisentry.category_en) ;
    public void addItem(Drawable chicon, Drawable bsicon, String programtitle, String programtime , String chnumber , String category_kr, String category_en, String starttime, String endtime,String chname) {
        EPGListViewItem item = new EPGListViewItem();
        item.setChLogoIcon(chicon);
        item.setBsLogoIcon(bsicon);
        item.setProgramTitle(programtitle);
        item.setProgramTime(programtime);
        item.setChnumber(chnumber);
        item.setCategory_kr(category_kr);
        item.setCategory_en(category_en);
    	item.setStarttime(starttime);
        item.setEndtime(endtime);
        item.setChname(chname);
        listViewItemList.add(item);
    }

    public void startRefreshTime() {
        if(!timer)
        {
            timer = true;
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    public void stopRefreshTime() {
        timer = false;
        handler.removeCallbacksAndMessages(null);
    }

    private boolean timer = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //Log.d(TAG, "handler");
            if (timer) {
                notifyDataSetChanged();
                handler.sendEmptyMessageDelayed(0, 10000);
            }
        }
    };
    class ViewHolder
    {
        View convertView;
        View EPGL0;
        ImageView chiconImageView;
        ImageView bsiconImageView;
        TextView programtitleTextView;
        TextView programTimeTextView;
        TextView tv_start_time;
        TextView tv_end_time;
        ProgressBar pb_time;
    }				
}
