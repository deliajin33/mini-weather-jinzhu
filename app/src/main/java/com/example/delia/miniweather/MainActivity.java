package com.example.delia.miniweather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.delia.util.NetUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by delia on 21/09/2017.
 */

//一个Activity标识一个具有用户界面的单一屏幕(窗口)
public class MainActivity extends Activity implements View.OnClickListener
{
    private ImageView mUpdateBtn;

    @Override
    //Android 系统初始化它的程序是通过活动中的 onCreate() 回调的调用开始的,类似main()

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        //为更新按钮添加事件,Activity自身为监听器
        mUpdateBtn = (ImageView)findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        //检查网络连接情况
        if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE)
        {
            Log.d("myWeather" , "网络OK");

            //用来显示显示信息的一种机制,过一段时间就会消失
            Toast.makeText(MainActivity.this , "网络OK" , Toast.LENGTH_LONG).show();
        }
        else
        {
            Log.d("myWeather" , "网络挂了");
            Toast.makeText(MainActivity.this , "网络挂了" , Toast.LENGTH_LONG).show();
        }

    }



    //监听器处理点击事件
    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.title_update_btn)
        {
            //SharedPreferences是Android平台上一个轻量级的存储类，用来保存应用的一些常用配置,文件生成为xml文件
            SharedPreferences sharedPreferences = getSharedPreferences("config" , MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code" , "101010100");
            Log.d("myWeather" , cityCode);

            if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE)
            {
                Log.d("myWeather" , "网络OK");

                queryWeatherCode(cityCode);
            }
            else
            {
                Log.d("myWeather" , "网络挂了");
                Toast.makeText(MainActivity.this , "网络挂了" , Toast.LENGTH_LONG).show();
            }
        }
    }

    private void queryWeatherCode(String cityCode)
    {
        //中国天气网API接口
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather" , address);

        //匿名类
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                HttpURLConnection con = null;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);

                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();

                    String str;
                    while ( (str = reader.readLine()) != null ) {
                        response.append(str);
                        Log.d("myWeather", str);

                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);

                    //解析数据
                    parseXML(responseStr);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }

            }


        }).start();
    }

    private void parseXML(String xmldata)
    {
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;

        try
        {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();

            XmlPullParser xmlPullParser= fac.newPullParser();

            //解析方法参数xmldata
            xmlPullParser.setInput(new StringReader(xmldata));

            int eventType = xmlPullParser.getEventType();

            Log.d("myWeather" , "parseXML");

            while(eventType != xmlPullParser.END_DOCUMENT)
            {
                switch(eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        if ( xmlPullParser.getName().equals("city") )
                        {
                            eventType = xmlPullParser.next();

                            Log.d( "myWeather" , "city:  " + xmlPullParser.getText() );

                        }
                        else if ( xmlPullParser.getName().equals("updatetime") )
                        {
                            eventType = xmlPullParser.next();

                            Log.d( "myWeather" , "updatetime:  " + xmlPullParser.getText() );
                        }
                        else if ( xmlPullParser.getName().equals("shidu") )
                        {
                            eventType = xmlPullParser.next();

                            Log.d( "myWeather" , "shidu:  " + xmlPullParser.getText() );
                        }
                        else if ( xmlPullParser.getName().equals("wendu") )
                        {
                            eventType = xmlPullParser.next();

                            Log.d( "myWeather" , "wendu:  " + xmlPullParser.getText() );
                        }
                        else if ( xmlPullParser.getName().equals("pm25") )
                        {
                            eventType = xmlPullParser.next();

                            Log.d( "myWeather" , "pm25:  " + xmlPullParser.getText() );
                        }
                        else if ( xmlPullParser.getName().equals("quality") )
                        {
                            eventType = xmlPullParser.next();

                            Log.d( "myWeather" , "quality:  " + xmlPullParser.getText() );
                        }
                        else if ( xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0 )
                        {
                            eventType = xmlPullParser.next();

                            Log.d( "myWeather" , "fengxiang:  " + xmlPullParser.getText() );

                            fengxiangCount++;

                        }
                        else if ( xmlPullParser.getName().equals("fengli") && fengliCount == 0 )
                        {
                            eventType = xmlPullParser.next();

                            Log.d( "myWeather" , "fengli:  " + xmlPullParser.getText() );

                            fengliCount++;
                        }
                        else if ( xmlPullParser.getName().equals("date") && dateCount == 0 )
                        {
                            eventType = xmlPullParser.next();

                            Log.d( "myWeather" , "date:  " + xmlPullParser.getText() );

                            dateCount++;
                        }
                        else if ( xmlPullParser.getName().equals("high") && highCount == 0 )
                        {
                            eventType = xmlPullParser.next();

                            Log.d( "myWeather" , "high:  " + xmlPullParser.getText() );

                            highCount++;
                        }
                        else if ( xmlPullParser.getName().equals("low") && lowCount == 0 )
                        {
                            eventType = xmlPullParser.next();

                            Log.d( "myWeather" , "low:  " + xmlPullParser.getText() );

                            lowCount++;
                        }
                        else if ( xmlPullParser.getName().equals("type") && typeCount == 0 )
                        {
                            eventType = xmlPullParser.next();

                            Log.d( "myWeather" , "type:  " + xmlPullParser.getText() );

                            typeCount++;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        break;

                }
                //进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();

            }

        }
        catch (XmlPullParserException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    //方法结束标记
    }












}
