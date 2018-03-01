package net.accko.konyanghs.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import net.accko.konyanghs.ui.MainActivity;
import net.accko.konyanghs.R;
import net.accko.konyanghs.bap.BapTool;
import net.accko.konyanghs.bap.Preference;
import net.accko.konyanghs.bap.ProcessTask;
import net.accko.konyanghs.bap.Tools;

import java.util.Calendar;

public class BapWidget extends AppWidgetProvider {

    static void updateSmallAppWidget(Context mContext, AppWidgetManager appWidgetManager, int appWidgetId, boolean ifNotUpdate) {
        RemoteViews mViews = new RemoteViews(mContext.getPackageName(), R.layout.item_widget_bap);

        Calendar mCalendar = Calendar.getInstance();
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        BapTool.restoreBapDateClass savedBapData = BapTool.restoreBapData(mContext, year, month, day);

        mViews.setTextViewText(R.id.mCalender, savedBapData.Calender);

        if (savedBapData.isBlankDay) {
            // 데이터 없음
            if (Tools.isOnline(mContext)) {
                // Only Wifi && Not Wifi
                if (new Preference(mContext).getBoolean("updateWiFi", true) && !Tools.isWifi(mContext)) {
                    mViews.setTextViewText(R.id.mLunch, mContext.getString(R.string.widget_no_data));
                } else if (ifNotUpdate) {
                    // 급식 데이터 받아옴
                    BapDownloadTask mProcessTask = new BapDownloadTask(mContext);
                    mProcessTask.execute(year, month, day);
                }
            } else {
                mViews.setTextViewText(R.id.mLunch, mContext.getString(R.string.widget_no_data));
            }
        } else {
            // 데이터 있음

            /**
             * hour : 0~23
             * 0~8 : Morning
             * 8~13 : Lunch
             * 14~23 : Lunch
             */
            String mTitle = null, mTodayMeal = null;
            int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
            if (0 <= hour && hour <= 8) {
                mTitle = "아침";
                mTodayMeal = savedBapData.Morning;
                if (BapTool.mStringCheck(mTodayMeal)) {
                    mTodayMeal = mContext.getString(R.string.no_data_lunch);
                } else {
                    mTodayMeal = BapTool.replaceString(mTodayMeal);
                }
            }
            if (8 < hour && hour <= 13) {
                mTitle = "점심";
                mTodayMeal = savedBapData.Lunch;
                if (BapTool.mStringCheck(mTodayMeal)) {
                    mTodayMeal = mContext.getString(R.string.no_data_lunch);
                } else {
                    mTodayMeal = BapTool.replaceString(mTodayMeal);
                }
            }
            if (13 < hour && hour <= 24) {
                mTitle = "저녁";
                mTodayMeal = savedBapData.Dinner;
                if (BapTool.mStringCheck(mTodayMeal)) {
                    mTodayMeal = mContext.getString(R.string.no_data_dinner);
                } else {
                    mTodayMeal = BapTool.replaceString(mTodayMeal);
                }
            }

            mViews.setTextViewText(R.id.mLunchTitle, mTitle);
            mViews.setTextViewText(R.id.mLunch, mTodayMeal);
        }

        PendingIntent layoutPendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext, MainActivity.class), 0);
        mViews.setOnClickPendingIntent(R.id.mWidgetLayout, layoutPendingIntent);

        Intent mIntent = new Intent(mContext, WidgetBroadCast.class);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(mContext, 0, mIntent, 0);
        mViews.setOnClickPendingIntent(R.id.mUpdateLayout, updatePendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, mViews);
    }

    @Override
    public void onUpdate(Context mContext, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateSmallAppWidget(mContext, appWidgetManager, appWidgetId, false);
        }
    }

    @Override
    public void onReceive(Context mContext, Intent mIntent) {
        super.onReceive(mContext, mIntent);

        String mAction = mIntent.getAction();
        if (mAction.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            updateAllBapWidget(mContext);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static class BapDownloadTask extends ProcessTask {
        Context mContext;

        public BapDownloadTask(Context mContext) {
            super(mContext);
            this.mContext = mContext;
        }

        @Override
        public void onPreDownload() {
        }

        @Override
        public void onUpdate(int progress) {
        }

        @Override
        public void onFinish(long result) {
            Intent mIntent = new Intent(mContext, WidgetBroadCast.class);
            mContext.sendBroadcast(mIntent);
        }
    }

    public static void updateAllBapWidget(Context mContext) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, BapWidget.class));
        for (int appWidgetId : appWidgetIds) {
            updateSmallAppWidget(mContext, appWidgetManager, appWidgetId, false);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context mContext, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(mContext, appWidgetManager, appWidgetId, newOptions);

        updateSmallAppWidget(mContext, appWidgetManager, appWidgetId, false);
    }
}