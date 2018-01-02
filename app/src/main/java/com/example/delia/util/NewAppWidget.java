package com.example.delia.util;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.delia.app.MyApplication;
import com.example.delia.miniweather.R;


/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
    private static MyApplication myApplication;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        myApplication = MyApplication.getInstance();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setTextViewText(R.id.climate_widget, myApplication.getWeathertype());
        views.setTextViewText(R.id.week_today_widget, myApplication.getTodaydate());
        views.setTextViewText(R.id.wind_widget, "风力:" + myApplication.getWind());
        views.setTextViewText(R.id.temperature_widget, myApplication.getWendu() + "℃");
        switch (myApplication.getWeathertype()) {
            case "暴雪":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_daxue);
                break;
            case "多云":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "大雨":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_dayu);
                break;
            case "中雨":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_zhongyu);
                break;
            case "晴":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "阴":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_yin);
                break;
            case "小雨":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "雨夹雪":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "中雪":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "阵雨":
                views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_zhenyu);
                break;
            default:
                break;
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        Log.d("updatetest","1234");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        Log.d("updatetest","12345");
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("updatetest",action);
        if (action.equals("android.appwidget.action.APPWIDGET_UPDATE")) {
            Log.d("updatetest","123");
            myApplication = MyApplication.getInstance();
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            //views.setTextViewText(R.id.appwidget_text, widgetText);
            views.setTextViewText(R.id.climate_widget, myApplication.getWeathertype());
            views.setTextViewText(R.id.week_today_widget, myApplication.getTodaydate());
            views.setTextViewText(R.id.wind_widget, "风力:" + myApplication.getWind());
            views.setTextViewText(R.id.temperature_widget, myApplication.getWendu() + "℃");
            switch (myApplication.getWeathertype()) {
                case "暴雪":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_baoxue);
                    break;
                case "暴雨":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_baoyu);
                    break;
                case "大暴雨":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_dabaoyu);
                    break;
                case "大雪":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_daxue);
                    break;
                case "多云":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_duoyun);
                    break;
                case "雷阵雨":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_leizhenyu);
                    break;
                case "雷阵雨冰雹":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_leizhenyubingbao);
                    break;
                case "大雨":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_dayu);
                    break;
                case "中雨":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_zhongyu);
                    break;
                case "晴":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_qing);
                    break;
                case "沙尘暴":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_shachenbao);
                    break;
                case "特大暴雨":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_tedabaoyu);
                    break;
                case "雾":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_wu);
                    break;
                case "小雪":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "阴":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_yin);
                    break;
                case "小雨":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_xiaoyu);
                    break;
                case "雨夹雪":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_yujiaxue);
                    break;
                case "阵雪":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_zhenxue);
                    break;
                case "中雪":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_zhongxue);
                    break;
                case "阵雨":
                    views.setImageViewResource(R.id.weather_img_widget, R.drawable.biz_plugin_weather_zhenyu);
                    break;
                default:
                    break;
            }
            AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, NewAppWidget.class), views);
        }
    }
}

