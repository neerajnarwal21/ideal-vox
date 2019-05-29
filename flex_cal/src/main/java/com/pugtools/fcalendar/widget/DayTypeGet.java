package com.pugtools.fcalendar.widget;

import android.util.SparseArray;

import com.pugtools.fcalendar.data.Day;
import com.pugtools.fcalendar.data.DayStatus;

import java.util.HashMap;

/**
 * Created by neeraj on 9/1/18.
 */

public interface DayTypeGet {
    void onDayMapFilled(SparseArray<DayStatus> dayTypeHashMap);
}
