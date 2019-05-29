package com.pugtools.fcalendar.data;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pugtools.fcalendar.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CalendarAdapter {

    ArrayList<Day> mItemList = new ArrayList<>();
    ArrayList<View> mViewList = new ArrayList<>();
    ArrayList<ImageView> mDayViewList = new ArrayList<>();
    ArrayList<ImageView> mNightViewList = new ArrayList<>();
    ArrayList<Event> mEventList = new ArrayList<>();
    private int mFirstDayOfWeek = 0;
    private Calendar mCal;
    private LayoutInflater mInflater;
    private Context context;

    public CalendarAdapter(Context context, Calendar cal, SparseArray<DayStatus> dayTypeHashMap) {
        this.mCal = (Calendar) cal.clone();
        this.context = context;
//        this.mCal.set(Calendar.DAY_OF_MONTH, 1);

        mInflater = LayoutInflater.from(context);

        refresh(dayTypeHashMap);
    }

    // public methods
    public int getCount() {
        return mItemList.size();
    }

    public Day getItem(int position) {
        return mItemList.get(position);
    }

    public View getView(final int position) {
        return mViewList.get(position);
    }

    public ImageView getDayView(final int position) {
        return mDayViewList.get(position);
    }

    public ImageView getNightView(final int position) {
        return mNightViewList.get(position);
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        mFirstDayOfWeek = firstDayOfWeek;
    }

    public Calendar getCalendar() {
        return mCal;
    }

    public void addEvent(Event event) {
        mEventList.add(event);
    }

    public void refresh(SparseArray<DayStatus> dayTypeHashMap) {
//
//        Log.e("Hash Map >>> ", dayTypeHashMap.size() + "");
        // clear data
        mItemList.clear();
        mViewList.clear();
        mDayViewList.clear();
        mNightViewList.clear();

        // set calendar
        int year = mCal.get(Calendar.YEAR);
        int month = mCal.get(Calendar.MONTH);

        mCal.set(year, month, 1);

        int lastDayOfMonth = mCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayOfWeek = mCal.get(Calendar.DAY_OF_WEEK) - 1;

        // generate day list
        int offset = 0 - (firstDayOfWeek - mFirstDayOfWeek) + 1;
        int length = (int) Math.ceil((float) (lastDayOfMonth - offset + 1) / 7) * 7;
        for (int i = offset; i < length + offset; i++) {
            int numYear;
            int numMonth;
            int numDay = 0;

            Calendar tempCal = Calendar.getInstance();
            if (i <= 0) { // prev month
                if (month == 0) {
                    numYear = year - 1;
                    numMonth = 11;
                } else {
                    numYear = year;
                    numMonth = month - 1;
                }
                tempCal.set(numYear, numMonth, 1);
                numDay = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH) + i;
            } else if (i > lastDayOfMonth) { // next month
                if (month == 11) {
                    numYear = year + 1;
                    numMonth = 0;
                } else {
                    numYear = year;
                    numMonth = month + 1;
                }
                tempCal.set(numYear, numMonth, 1);
                numDay = i - lastDayOfMonth;
            } else {
                numYear = year;
                numMonth = month;
                numDay = i;
            }

            Day day = new Day(numYear, numMonth, numDay, dayTypeHashMap.get(i));

            View view = mInflater.inflate(R.layout.layout_day, null);
            TextView txtDay = view.findViewById(R.id.txt_day);
            ImageView dayV = view.findViewById(R.id.dayV);
            ImageView nightV = view.findViewById(R.id.nightV);
            if (i - 1 < 0 || i > lastDayOfMonth) {
                view.setVisibility(View.INVISIBLE);
            }

            txtDay.setText(String.valueOf(day.getDay()));

//            Log.e("Day val ", "Day val " + String.valueOf(day.getDayStatus()));
//            if (day.getDayStatus() != null)
//                switch (day.getDayStatus().getDay()) {
//                    case AVAILABLE:
//                        dayV.setBackgroundResource(R.drawable.day_view_available);
//                        break;
//                    case UNAVAILABLE:
//                        dayV.setBackgroundResource(R.drawable.day_view_unavailable);
//                        break;
//                    case BOOKED:
//                        dayV.setBackgroundResource(R.drawable.day_view_booked);
//                        break;
//                }
//
//            if (day.getDayStatus() != null)
//                switch (day.getDayStatus().getNight()) {
//                    case AVAILABLE:
//                        nightV.setBackgroundResource(R.drawable.night_view_available);
//                        break;
//                    case UNAVAILABLE:
//                        nightV.setBackgroundResource(R.drawable.night_view_unavailable);
//                        break;
//                    case BOOKED:
//                        nightV.setBackgroundResource(R.drawable.night_view_booked);
//                        break;
//                }

            mItemList.add(day);
            mViewList.add(view);
            mDayViewList.add(dayV);
            mNightViewList.add(nightV);
        }

    }

}
