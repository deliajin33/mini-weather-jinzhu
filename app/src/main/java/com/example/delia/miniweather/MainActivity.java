package com.example.delia.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.delia.bean.TodayWeather;
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
    private static final int UPDATE_TODAY_WEATHER = 1;

    //各式各样的组件
    private ImageView mUpdateBtn;

    private ImageView mCitySelect;

    private TextView cityTv , timeTv , currentTemperatureTv , humidityTv , weekTv , pmDataTv , pmQualityTv , temperatureTv , climateTv , windTv , city_name_Tv;

    private ImageView weatherImg , pmImg;

    private SharedPreferences sharedPreferences;


    //Handler来根据接收的消息，处理UI更新。子Thread线程发出Handler消息，通知更新UI
    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeahter( (TodayWeather)msg.obj );
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    //Android 系统初始化它的程序是通过活动中的 onCreate() 回调的调用开始的,类似main()

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        //初始化sharedPreferences
        sharedPreferences = getSharedPreferences("config" , MODE_PRIVATE);

        //为更新按钮添加事件,Activity自身为监听器
        mUpdateBtn = (ImageView)findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        //检查网络连接情况
        if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE)
        {
            Log.d("myWeather" , "网络OK");

            //用来显示信息的一种机制,过一段时间就会消失
            Toast.makeText(MainActivity.this , "网络OK" , Toast.LENGTH_LONG).show();
        }
        else
        {
            Log.d("myWeather" , "网络挂了");
            Toast.makeText(MainActivity.this , "网络挂了" , Toast.LENGTH_LONG).show();
        }

        //选择城市按钮
        mCitySelect = (ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);



        //调用方法初始化控件
        initView();

    }


    //初始化组件
    void initView()
    {
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);

        currentTemperatureTv = (TextView)findViewById(R.id.current_temperature);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);

        weatherImg = (ImageView) findViewById(R.id.weather_img);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);


        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        currentTemperatureTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    //方法结束标记
    }



    //监听器处理点击事件
    @Override
    public void onClick(View view)
    {
        //城市选择按钮事件处理
        if(view.getId() == R.id.title_city_manager)
        {
            //跳转activity
            Intent i = new Intent(this , SelectCity.class);

            /**在启动另外一个Activity的时候，有两种方法，
             * 一种是直接使用startActivity，
             * 另外一种就是使用startActivityForResult**/
            //startActivity(i);

            startActivityForResult( i , 1 );

        }

        //更新按钮事件处理
        if(view.getId() == R.id.title_update_btn)
        {
            //SharedPreferences是Android平台上一个轻量级的存储类，用来保存应用的一些常用配置,文件生成为xml文件
            //SharedPreferences sharedPreferences = getSharedPreferences("config" , MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code" , "101010100");
            Log.d("myWeather" , cityCode);

            if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE)
            {
                Log.d("myWeather" , "网络OK");

                //根据citycode查询天气状况
                queryWeatherCode(cityCode);
            }
            else
            {
                //将上次成功获取并存储到sharedPreferences里的数据取出并解析，显示到页面上
                String responseStr_last = sharedPreferences.getString("天气数据","");
                TodayWeather todayWeather = parseXML(responseStr_last);

                if(todayWeather != null)
                {
                    updateTodayWeahter(todayWeather);
                }

                Log.d("myWeather" , "网络挂了");

                Toast.makeText(MainActivity.this , "网络挂了" , Toast.LENGTH_LONG).show();
            }
        }
    //方法结束标记
    }

    /**startActivityForResult的主要作用就是它可以回传数据，假设我们有两个页面，
     * 首先进入第一个页面，里面有一个按钮，用于进入下一个页面，当进入下一个页面时，
     * 进行设置操作，并在其finish()动作或者back动作后，将设置的值回传给第一个页面，
     * 从而第一个页面来显示所得到的值。
     * 这个有一点像回调方法，就是在第二个页面finish()动作或者back动作后，
     * 会回调第一个页面的onActivityResult()方法**/
    protected void onActivityResult(int requestCode , int resultCode , Intent data)
    {
        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            String newCityCode = data.getStringExtra("cityCode");

            //存储城市的cityCode
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("main_city_code" , newCityCode);

            editor.commit();

            //cityCode存不进去
            Log.d("myWeather" , "存储的城市代码是：" + sharedPreferences.getString("main_city_code","123"));

            Log.d("myWeather" , "选择的城市代码是：" + newCityCode);

            if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE)
            {
                Log.d("myWeather" , "网络OK");

                //根据citycode查询天气状况
                queryWeatherCode(newCityCode);


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

                //使用bean获取打印属性
                TodayWeather todayWeather = null;

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

                    //存储最新查询到的天气信息
                    saveCityData(responseStr);

                    Log.d("myWeather", responseStr);

                    //解析数据
                    todayWeather = parseXML(responseStr);

                    //更新UI数据
                    if(todayWeather != null)
                    {
                        Log.d("myWeather" , todayWeather.toString());

                        //子线程与主线程的通信机制
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if (con != null)
                    {
                        con.disconnect();
                    }
                }

            }


        }).start();
    }


    //每次调用完queryCity
    private void saveCityData(String responseStr)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("天气数据" , responseStr);
        editor.commit();
    }



    private TodayWeather parseXML(String xmldata)
    {
        TodayWeather todayWeather = null;

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
                        if ( xmlPullParser.getName().equals("resp") )
                        {

                            todayWeather = new TodayWeather();

                        }
                        if (todayWeather != null)
                        {
                            if ( xmlPullParser.getName().equals("city") )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "city:  " + xmlPullParser.getText() );

                                todayWeather.setCity(xmlPullParser.getText());

                            }
                            else if ( xmlPullParser.getName().equals("updatetime") )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "updatetime:  " + xmlPullParser.getText() );

                                todayWeather.setUpdatetime(xmlPullParser.getText());

                            }
                            else if ( xmlPullParser.getName().equals("shidu") )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "shidu:  " + xmlPullParser.getText() );

                                todayWeather.setShidu(xmlPullParser.getText());

                            }
                            else if ( xmlPullParser.getName().equals("wendu") )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "wendu:  " + xmlPullParser.getText() );

                                todayWeather.setWendu(xmlPullParser.getText());

                            }
                            else if ( xmlPullParser.getName().equals("pm25") )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "pm25:  " + xmlPullParser.getText() );

                                todayWeather.setPm25(xmlPullParser.getText());

                            }
                            else if ( xmlPullParser.getName().equals("quality") )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "quality:  " + xmlPullParser.getText() );

                                todayWeather.setQuality(xmlPullParser.getText());

                            }
                            else if ( xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0 )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "fengxiang:  " + xmlPullParser.getText() );

                                todayWeather.setFengxiang(xmlPullParser.getText());

                                fengxiangCount++;

                            }
                            else if ( xmlPullParser.getName().equals("fengli") && fengliCount == 0 )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "fengli:  " + xmlPullParser.getText() );

                                todayWeather.setFengli(xmlPullParser.getText());

                                fengliCount++;
                            }
                            else if ( xmlPullParser.getName().equals("date") && dateCount == 0 )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "date:  " + xmlPullParser.getText() );

                                todayWeather.setDate(xmlPullParser.getText());

                                dateCount++;
                            }
                            else if ( xmlPullParser.getName().equals("high") && highCount == 0 )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "high:  " + xmlPullParser.getText() );

                                todayWeather.setHigh(xmlPullParser.getText());

                                highCount++;
                            }
                            else if ( xmlPullParser.getName().equals("low") && lowCount == 0 )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "low:  " + xmlPullParser.getText() );

                                todayWeather.setLow(xmlPullParser.getText());

                                lowCount++;
                            }
                            else if ( xmlPullParser.getName().equals("type") && typeCount == 0 )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "type:  " + xmlPullParser.getText() );

                                todayWeather.setType(xmlPullParser.getText());

                                typeCount++;
                            }

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

        return todayWeather;
    //方法结束标记
    }

    //更新窗口中的天气数据
    void updateTodayWeahter(TodayWeather todayWeather)
    {

        city_name_Tv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity() );
        timeTv.setText(todayWeather.getUpdatetime() + "发布");
        currentTemperatureTv.setText("温度："+ todayWeather.getWendu() + "°C");
        humidityTv.setText("湿度：" + todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh() + "~" + todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力：" + todayWeather.getFengli());


        //更新pm图片
        int pm25 = Integer.parseInt(todayWeather.getPm25());
        if(pm25 >= 0 && pm25 <= 50)
        {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
        }
        if(pm25 >= 51 && pm25 <= 100)
        {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
        }
        if(pm25 >= 101 && pm25 <= 150)
        {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
        }
        if(pm25 >= 151 && pm25 <= 200)
        {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
        }
        if(pm25 >= 201 && pm25 <= 300)
        {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
        }


        //更新天气图片
        switch (todayWeather.getType())
        {
            case "暴雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "多云":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "大雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "中雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            case "晴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "阴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "小雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "雨夹雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "中雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            default:
                break;
        }

        Toast.makeText(MainActivity.this , "更新成功！" , Toast.LENGTH_SHORT).show();

    }










    
}
