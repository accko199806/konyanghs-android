package net.accko.konyanghs.bap;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import toast.library.meal.MealLibrary;

/**
 * Created by whdghks913 on 2015-02-17.
 */
public class BapTool {
    public static final String BAP_PREFERENCE_NAME = "BapData";

    public static final int TYPE_MORNING = 0;
    public static final int TYPE_LUNCH = 1;
    public static final int TYPE_DINNER = 2;

    public static String getBapStringFormat(int year, int month, int day, int type) {
        /**
         * Format : year-month-day-TYPE
         */
        return year + "-" + month + "-" + day + "-" + type;
    }

    /**
     * Pref Name Format : 2015-02-17-TYPE_index
     * ex) 2015-02-17-1_3
     */
    public static void saveBapData(Context mContext, String[] Calender, String[] Morning, String[] Lunch, String[] Dinner) {
        /**
         * Do not Edit : yyyy.MM.dd(E)
         */
        Preference mPref = new Preference(mContext, BAP_PREFERENCE_NAME);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd(E)", Locale.KOREA);

        for (int index = 0; index < Calender.length; index++) {
            try {
                Calendar mDate = Calendar.getInstance();
                mDate.setTime(mFormat.parse(Calender[index]));

                int year = mDate.get(Calendar.YEAR);
                int month = mDate.get(Calendar.MONTH) + 1;
                int day = mDate.get(Calendar.DAY_OF_MONTH);

                String mPrefMorningName = getBapStringFormat(year, month, day, TYPE_MORNING);
                String mPrefLunchName = getBapStringFormat(year, month, day, TYPE_LUNCH);
                String mPrefDinnerName = getBapStringFormat(year, month, day, TYPE_DINNER);

                String mMorning = Morning[index];
                String mLunch = Lunch[index];
                String mDinner = Dinner[index];

                if (!MealLibrary.isMealCheck(mMorning)) mMorning = "";
                if (!MealLibrary.isMealCheck(mLunch)) mLunch = "";
                if (!MealLibrary.isMealCheck(mDinner)) mDinner = "";

                mPref.putString(mPrefMorningName, mMorning);
                mPref.putString(mPrefLunchName, mLunch);
                mPref.putString(mPrefDinnerName, mDinner);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Format : 2015-2-11-2
     */
    public static restoreBapDateClass restoreBapData(Context mContext, int year, int month, int day) {
        /**
         * TODO 디버그를 하려면 Log.d를 활성화 하세요.
         */

        /**
         * TODO 원하는 Date Format이 있으면 'yyyy년 MM월 dd일', 'E요일'을 수정하세요
         */
        Preference mPref = new Preference(mContext, BAP_PREFERENCE_NAME);
        SimpleDateFormat mCalenderFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
        SimpleDateFormat mDayOfWeekFormat = new SimpleDateFormat("E요일", Locale.KOREA);
        Calendar mDate = Calendar.getInstance();
        mDate.set(year, month, day);

//        Log.d("YEAR", "" + mDate.get(Calendar.YEAR));
//        Log.d("MONTH", "" + mDate.get(Calendar.MONTH));
//        Log.d("DAY_OF_MONTH", "" + mDate.get(Calendar.DAY_OF_MONTH));

        restoreBapDateClass mData = new restoreBapDateClass();

        String mPrefMorningName = getBapStringFormat(year, month + 1, day, TYPE_MORNING);
        String mPrefLunchName = getBapStringFormat(year, month + 1, day, TYPE_LUNCH);
        String mPrefDinnerName = getBapStringFormat(year, month + 1, day, TYPE_DINNER);

//        Log.d("mPrefMorningName", "" + mPrefMorningName);
//        Log.d("mPrefLunchName", "" + mPrefLunchName);
//        Log.d("mPrefDinnerName", "" + mPrefDinnerName);

        mData.Calender = mCalenderFormat.format(mDate.getTime());
        mData.DayOfTheWeek = mDayOfWeekFormat.format(mDate.getTime());
        mData.Morning = mPref.getString(mPrefMorningName, null);
        mData.Lunch = mPref.getString(mPrefLunchName, null);
        mData.Dinner = mPref.getString(mPrefDinnerName, null);

//        Log.d("mData.Calender", "" + mData.Calender);
//        Log.d("mData.DayOfTheWeek", "" + mData.DayOfTheWeek);
//        Log.d("mData.Lunch", "" + mData.Lunch);
//        Log.d("mData.Dinner", "" + mData.Dinner);

        /**
         * TODO 아침 데이터를 체크하는 코드는 필요 없다는 판단하에 추가하지 않음
         * 점심과 저녁이 null이 아니라면 아침도 null이 아니므로
         */
        if (mData.Lunch == null && mData.Dinner == null) {
            mData.isBlankDay = true;
        }

        return mData;
    }

    /**
     * restoreBapData()를 실행할때 반환하는 Class입니다.
     * Calender, DayOfTheWeek 에는 날짜 정보가, Lunch, Dinner에는 급식 데이터가 담겨있고
     * isBlankDay=true 이면 그 날은 급식 데이터가 저장되어 있지 않습니다.
     */
    public static class restoreBapDateClass {
        public String Calender;
        public String DayOfTheWeek;
        public String Morning;
        public String Lunch;
        public String Dinner;
        public boolean isBlankDay = false;
    }

    public static boolean mStringCheck(String mString) {
        if (mString == null || "".equals(mString) || " ".equals(mString))
            return true;
        return false;
    }

    public static String replaceString(String mString) {
        String[] mTrash = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "."};
        for (String e : mTrash) {
            mString = mString.replace(e, "");
        }

        mString = mString.replace("\n", "  ");

        return mString;
    }

}
