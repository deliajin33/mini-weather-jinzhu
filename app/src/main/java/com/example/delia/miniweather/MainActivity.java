package com.example.delia.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;

import com.baidu.location.LocationClientOption;
import com.example.delia.app.MyApplication;
import com.example.delia.bean.City;
import com.example.delia.bean.ForecastWeather;
import com.example.delia.bean.TodayWeather;
import com.example.delia.util.MyLocationListener;
import com.example.delia.util.NetUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by delia on 21/09/2017.
 */

//一个Activity标识一个具有用户界面的单一屏幕(窗口)
public class MainActivity extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener
{
    private static final int UPDATE_TODAY_WEATHER = 1;
    private static final int UPDATE_FORECAST_WEATHER = 2;

    //各式各样的组件
    private ImageView mUpdateBtn;

    private View view1,view2;

    private ProgressBar mProgressBar;

    private ImageView mCitySelect;

    private ImageView mCityLocation;

    private TextView cityTv , timeTv , currentTemperatureTv , humidityTv , weekTv , pmDataTv , pmQualityTv , temperatureTv , climateTv , windTv , city_name_Tv;

    private ImageView weatherImg , pmImg;

    private SharedPreferences sharedPreferences;

    //定位
    public LocationClient mLocationClient = null;

    private MyLocationListener myListener = new MyLocationListener();

    //未来6天天气
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private List<View> viewList = null;
    private ImageView[] dots;
    private int[] ids = {R.id.dot_focused,R.id.dot_unfocused};

    private List<TodayWeather> forecastWeather=null;
    private TextView weekTv1,temperatureTv1,climateTv1,windTv1;
    private TextView weekTv2,temperatureTv2,climateTv2,windTv2;
    private TextView weekTv3,temperatureTv3,climateTv3,windTv3;
    private TextView weekTv4,temperatureTv4,climateTv4,windTv4;
    private TextView weekTv5,temperatureTv5,climateTv5,windTv5;
    private TextView weekTv6,temperatureTv6,climateTv6,windTv6;
    private ImageView weatherImg1;
    private ImageView weatherImg2;
    private ImageView weatherImg3;
    private ImageView weatherImg4;
    private ImageView weatherImg5;
    private ImageView weatherImg6;


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
                case UPDATE_FORECAST_WEATHER:

                    updateForecastWeahter();

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

        //为更新进度按钮添加事件,Activity自身为监听器
        mProgressBar = (ProgressBar)findViewById(R.id.title_update_progress);

        mProgressBar.setOnClickListener(this);

        mProgressBar.setVisibility(View.INVISIBLE);

        //选择城市按钮
        mCitySelect = (ImageView)findViewById(R.id.title_city_manager);

        mCitySelect.setOnClickListener(this);

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

        //定位按钮
        mCityLocation = (ImageView)findViewById(R.id.title_location);

        mCityLocation.setOnClickListener(this);

        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());

        //注册监听函数
        mLocationClient.registerLocationListener(myListener);


        /**可选，是否需要地址信息，默认为不需要，即参数为false
         如果开发者需要获得当前点的地址信息，此处必须为true**/
        LocationClientOption option = new LocationClientOption();

        option.setIsNeedAddress(true);

        option.setOpenGps(true);

        option.setAddrType("all");

        option.setPriority(LocationClientOption.GpsFirst);

        option.disableCache(false);

        mLocationClient.setLocOption(option);

        mLocationClient.start();

        //调用方法初始化控件
        initView();
        initForecastView();
        initDots();
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

        //初始化
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

    public void initForecastView()
    {
        //viewPager
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        //Adapter数据准备
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        view1 = layoutInflater.inflate(R.layout.weather_info_1 , null);
        view2 = layoutInflater.inflate(R.layout.weather_info_2 , null);
        viewList = new ArrayList<>();
        viewList.add(view1);
        viewList.add(view2);
        viewPagerAdapter = new ViewPagerAdapter(viewList,this);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnClickListener(this);

        //未来六天天气1
        weekTv1 = (TextView) view1.findViewById(R.id.week_today_1);
        temperatureTv1 = (TextView) view1.findViewById(R.id.temperature_1);
        climateTv1 = (TextView) view1.findViewById(R.id.climate_1);
        windTv1 = (TextView) view1.findViewById(R.id.wind_1);
        weatherImg1 = (ImageView) view1.findViewById(R.id.weather_img_1);
        //未来六天天气2
        weekTv2 = (TextView) view1.findViewById(R.id.week_today_2);
        temperatureTv2 = (TextView) view1.findViewById(R.id.temperature_2);
        climateTv2 = (TextView) view1.findViewById(R.id.climate_2);
        windTv2 = (TextView) view1.findViewById(R.id.wind_2);
        weatherImg2 = (ImageView) view1.findViewById(R.id.weather_img_2);
        //未来六天天气3
        weekTv3 = (TextView) view1.findViewById(R.id.week_today_3);
        temperatureTv3 = (TextView) view1.findViewById(R.id.temperature_3);
        climateTv3 = (TextView) view1.findViewById(R.id.climate_3);
        windTv3 = (TextView) view1.findViewById(R.id.wind_3);
        weatherImg3 = (ImageView) view1.findViewById(R.id.weather_img_3);
        //未来六天天气4
        weekTv4 = (TextView) view2.findViewById(R.id.week_today_4);
        temperatureTv4 = (TextView) view2.findViewById(R.id.temperature_4);
        climateTv4 = (TextView) view2.findViewById(R.id.climate_4);
        windTv4 = (TextView) view2.findViewById(R.id.wind_4);
        weatherImg4 = (ImageView) view2.findViewById(R.id.weather_img_4);
        //未来六天天气5
        weekTv5 = (TextView) view2.findViewById(R.id.week_today_5);
        temperatureTv5 = (TextView) view2.findViewById(R.id.temperature_5);
        climateTv5 = (TextView) view2.findViewById(R.id.climate_5);
        windTv5 = (TextView) view2.findViewById(R.id.wind_5);
        weatherImg5 = (ImageView) view2.findViewById(R.id.weather_img_5);
        //未来六天天气6
//        weekTv1 = (TextView) findViewById(R.id.week_today_6);
//        temperatureTv1 = (TextView) findViewById(R.id.temperature_6);
//        climateTv1 = (TextView) findViewById(R.id.climate_6);
//        windTv1 = (TextView) findViewById(R.id.wind_6);
//        weatherImg1 = (ImageView) findViewById(R.id.weather_img_6);

    }

    public void initDots()
    {
        dots = new ImageView[viewList.size()];
        for(int i = 0 ; i < viewList.size() ; i++)
        {
            dots[i] = findViewById(ids[i]);
        }
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
        if(view.getId() == R.id.title_update_btn || view.getId() == R.id.title_update_progress)
        {
            mUpdateBtn.setVisibility(View.INVISIBLE);

            mProgressBar.setVisibility(View.VISIBLE);


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

        //城市定位按钮事件处理
        if(view.getId() == R.id.title_location)
        {
            //声明LocationClient类
            mLocationClient = new LocationClient(getApplicationContext());

            //注册监听函数
            mLocationClient.registerLocationListener(myListener);


            /**可选，是否需要地址信息，默认为不需要，即参数为false
             如果开发者需要获得当前点的地址信息，此处必须为true**/
            LocationClientOption option = new LocationClientOption();

            option.setIsNeedAddress(true);

            option.setOpenGps(true);

            option.setAddrType("all");

            option.setPriority(LocationClientOption.GpsFirst);

            option.disableCache(false);

            mLocationClient.setLocOption(option);

            mLocationClient.start();

            String cityCode = myListener.getCityCode();

            Log.d("myWeatherl" , cityCode);

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

        //直接使用Thread类创建线程对象，并向构造方法Thread(Runnable target)的参数传递一个实现了该接口的实例，
        //在这里都使用了匿名类,并实现了run()
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

                    InputStream is = new ByteArrayInputStream(responseStr.getBytes());
                    forecastWeather=parseForecastXML(is);

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
                    if(forecastWeather != null  )
                    {
                        Log.d("myWeather_future" , forecastWeather.toString());

                        //子线程与主线程的通信机制
                        Message msg = new Message();
                        msg.what = UPDATE_FORECAST_WEATHER;
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


        }).start();//将该线程加入资源等待队列
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
                            //未来6天天气情况数据是否获得由count控制
                            else if ( xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0 )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "fengxiang:  " + xmlPullParser.getText() );

                                todayWeather.setFengxiang(xmlPullParser.getText());

//                                switch (fengxiangCount)
//                                {
//                                    case 1:
//                                        forecastWeather.get(0).setFengxiang(xmlPullParser.getText());
//                                        break;
//                                    case 2:
//                                        forecastWeather.get(1).setFengxiang(xmlPullParser.getText());
//                                        break;
//                                    case 3:
//                                        forecastWeather.get(2).setFengxiang(xmlPullParser.getText());
//                                        break;
//                                    case 4:
//                                        forecastWeather.get(3).setFengxiang(xmlPullParser.getText());
//                                        break;
//                                    case 5:
//                                        forecastWeather.get(4).setFengxiang(xmlPullParser.getText());
//                                        break;
//                                    default:
//                                        break;
//                                }

                                fengxiangCount++;

                            }
                            else if ( xmlPullParser.getName().equals("fengli") && fengliCount == 0 )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "fengli:  " + xmlPullParser.getText() );

                                todayWeather.setFengli(xmlPullParser.getText());

//                                switch (fengliCount)
//                                {
//                                    case 1:
//                                        forecastWeather.get(0).setFengli(xmlPullParser.getText());
//                                        break;
//                                    case 2:
//                                        forecastWeather.get(1).setFengli(xmlPullParser.getText());
//                                        break;
//                                    case 3:
//                                        forecastWeather.get(2).setFengli(xmlPullParser.getText());
//                                        break;
//                                    case 4:
//                                        forecastWeather.get(3).setFengli(xmlPullParser.getText());
//                                        break;
//                                    case 5:
//                                        forecastWeather.get(4).setFengli(xmlPullParser.getText());
//                                        break;
//                                    default:
//                                        break;
//                                }

                                fengliCount++;
                            }
                            else if ( xmlPullParser.getName().equals("date") && dateCount == 0 )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "date:  " + xmlPullParser.getText() );

                                todayWeather.setDate(xmlPullParser.getText());

//                                switch (fengliCount)
//                                {
//                                    case 1:
//                                        forecastWeather.get(0).setDate(xmlPullParser.getText());
//                                        break;
//                                    case 2:
//                                        forecastWeather.get(1).setDate(xmlPullParser.getText());
//                                        break;
//                                    case 3:
//                                        forecastWeather.get(2).setDate(xmlPullParser.getText());
//                                        break;
//                                    case 4:
//                                        forecastWeather.get(3).setDate(xmlPullParser.getText());
//                                        break;
//                                    case 5:
//                                        forecastWeather.get(4).setDate(xmlPullParser.getText());
//                                        break;
//                                    default:
//                                        break;
//                                }

                                dateCount++;
                            }
                            else if ( xmlPullParser.getName().equals("high") && highCount == 0 )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "high:  " + xmlPullParser.getText() );

                                todayWeather.setHigh(xmlPullParser.getText());

//                                switch (highCount)
//                                {
//                                    case 1:
//                                        forecastWeather.get(0).setHigh(xmlPullParser.getText());
//                                        break;
//                                    case 2:
//                                        forecastWeather.get(1).setHigh(xmlPullParser.getText());
//                                        break;
//                                    case 3:
//                                        forecastWeather.get(2).setHigh(xmlPullParser.getText());
//                                        break;
//                                    case 4:
//                                        forecastWeather.get(3).setHigh(xmlPullParser.getText());
//                                        break;
//                                    case 5:
//                                        forecastWeather.get(4).setHigh(xmlPullParser.getText());
//                                        break;
//                                    default:
//                                        break;
//                                }

                                highCount++;
                            }
                            else if ( xmlPullParser.getName().equals("low") && lowCount == 0 )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "low:  " + xmlPullParser.getText() );

                                todayWeather.setLow(xmlPullParser.getText());

//                                switch (lowCount)
//                                {
//                                    case 1:
//                                        forecastWeather.get(0).setLow(xmlPullParser.getText());
//                                        break;
//                                    case 2:
//                                        forecastWeather.get(1).setLow(xmlPullParser.getText());
//                                        break;
//                                    case 3:
//                                        forecastWeather.get(2).setLow(xmlPullParser.getText());
//                                        break;
//                                    case 4:
//                                        forecastWeather.get(3).setLow(xmlPullParser.getText());
//                                        break;
//                                    case 5:
//                                        forecastWeather.get(4).setLow(xmlPullParser.getText());
//                                        break;
//                                    default:
//                                        break;
//                                }

                                lowCount++;
                            }
                            else if ( xmlPullParser.getName().equals("type") && typeCount == 0 )
                            {
                                eventType = xmlPullParser.next();

                                Log.d( "myWeather" , "type:  " + xmlPullParser.getText() );

                                todayWeather.setType(xmlPullParser.getText());

//                                switch (typeCount)
//                                {
//                                    case 1:
//                                        forecastWeather.get(0).setType(xmlPullParser.getText());
//                                        break;
//                                    case 2:
//                                        forecastWeather.get(1).setType(xmlPullParser.getText());
//                                        break;
//                                    case 3:
//                                        forecastWeather.get(2).setType(xmlPullParser.getText());
//                                        break;
//                                    case 4:
//                                        forecastWeather.get(3).setType(xmlPullParser.getText());
//                                        break;
//                                    case 5:
//                                        forecastWeather.get(4).setType(xmlPullParser.getText());
//                                        break;
//                                    default:
//                                        break;
//                                }

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

    public List<TodayWeather> parseForecastXML(InputStream is)//获取未来四天天气的列表
    {
        List<TodayWeather> forecastWeather=new ArrayList<TodayWeather>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            // DocumentBuilder对象
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 获取文档对象
            Document document = builder.parse(is);

            // 获取文档对象的root
            Element root = document.getDocumentElement();

            // 获取weathers根节点中所有的weather节点对象

            NodeList weatherNodes = root.getElementsByTagName("weather");

            // 遍历所有的weather节点

            for (int i = 1; i < weatherNodes.getLength(); i++) {
                TodayWeather temp=new TodayWeather();
                // 根据item(index)获取该索引对应的节点对象
                Element weatherNode = (Element) weatherNodes.item(i); // 具体的weather节点


                // 获取该节点下面的所有字节点
                NodeList weatherChildNodes = weatherNode.getChildNodes();

                // 遍历weather的字节点
                for (int index = 0; index < weatherChildNodes.getLength(); index++) {
                    // 获取子节点
                    Node node = weatherChildNodes.item(index);

                    // 判断node节点是否是元素节点
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        //把节点转换成元素节点
                        Element element = (Element) node;
                        if ("date".equals(element.getNodeName()))
                        {
                            temp.setDate(element.getFirstChild().getNodeValue());
                        }
                        else if ("high".equals(element.getNodeName()))
                        { //判断是否是age节点
                            temp.setHigh(element.getFirstChild().getNodeValue().substring(2).trim());
                        }
                        else  if("low".equals(element.getNodeName()))
                        {
                            temp.setLow(element.getFirstChild().getNodeValue().substring(2).trim());
                        }
                    }
                    if(node.getNodeName().equals("day")) {
                        NodeList daynode=node.getChildNodes();
                        for(int j=0;j<daynode.getLength();j++) {
                            Node nodes=daynode.item(j);
                            Element element=(Element) nodes;
                            if(element.getNodeName().equals("type"))
                                temp.setType(element.getFirstChild().getNodeValue());
                            if(element.getNodeName().equals("fengxiang"))
                                temp.setFengxiang(element.getFirstChild().getNodeValue());
                            if(element.getNodeName().equals("fengli"))
                                temp.setFengli(element.getFirstChild().getNodeValue());
                        }
                    }

                }

                // 把weather对象加入到集合中
                forecastWeather.add(temp);

            }
            //关闭输入流
            //is.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return forecastWeather;
    }

    //更新窗口中的天气数据
    void updateTodayWeahter(TodayWeather todayWeather)
    {
        if(todayWeather.getCity() != null)
        {
            city_name_Tv.setText(todayWeather.getCity() + "天气");
        }
        if(todayWeather.getCity() != null)
        {
            cityTv.setText(todayWeather.getCity() );
        }
        if(todayWeather.getUpdatetime() != null)
        {
            timeTv.setText(todayWeather.getUpdatetime() + "发布");
        }
        if(todayWeather.getWendu() != null)
        {
            currentTemperatureTv.setText("温度："+ todayWeather.getWendu() + "°C");
        }
        if(todayWeather.getShidu() != null)
        {
            humidityTv.setText("湿度：" + todayWeather.getShidu());
        }
        if(todayWeather.getPm25() != null)
        {
            pmDataTv.setText(todayWeather.getPm25());
        }
        if(todayWeather.getQuality() != null)
        {
            pmQualityTv.setText(todayWeather.getQuality());
        }
        if(todayWeather.getDate() != null)
        {
            weekTv.setText(todayWeather.getDate());
        }
        if(todayWeather.getHigh() != null && todayWeather.getLow()!= null)
        {
            temperatureTv.setText(todayWeather.getHigh() + "~" + todayWeather.getLow());
        }
        if(todayWeather.getType() != null)
        {
            climateTv.setText(todayWeather.getType());
        }
        if(todayWeather.getFengli() != null)
        {
            windTv.setText("风力：" + todayWeather.getFengli());
        }
        
        //更新pm图片
        if(todayWeather.getPm25()!=null)
        {
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

        mProgressBar.setVisibility(View.INVISIBLE);
        mUpdateBtn.setVisibility(View.VISIBLE);

    }
    void updateForecastWeahter()
    {
        //weather1
        if(forecastWeather.get(0).getDate() != null)
        {
            Log.d("myWeather_date" , forecastWeather.get(0).getDate());
            weekTv1.setText(forecastWeather.get(0).getDate());
            //weekTv1.setText("天气好！！！！！");
        }
        if(forecastWeather.get(0).getHigh() != null && forecastWeather.get(0).getLow()!= null)
        {
            temperatureTv1.setText(forecastWeather.get(0).getHigh() + "~" + forecastWeather.get(0).getLow());
        }
        if(forecastWeather.get(0).getType() != null)
        {
            climateTv1.setText(forecastWeather.get(0).getType());
        }
        if(forecastWeather.get(0).getFengli() != null)
        {
            windTv1.setText("风力：" + forecastWeather.get(0).getFengli());
        }

        //更新天气图片
        switch (forecastWeather.get(0).getType())
        {
            case "暴雪":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "多云":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "大雨":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "中雨":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            case "晴":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "阴":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "小雨":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "雨夹雪":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "中雪":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "阵雨":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            default:
                break;
        }

        //weather2
        if(forecastWeather.get(1).getDate() != null)
        {
            weekTv2.setText(forecastWeather.get(1).getDate());
        }
        if(forecastWeather.get(1).getHigh() != null && forecastWeather.get(1).getLow()!= null)
        {
            temperatureTv2.setText(forecastWeather.get(1).getHigh() + "~" + forecastWeather.get(1).getLow());
        }
        if(forecastWeather.get(1).getType() != null)
        {
            climateTv2.setText(forecastWeather.get(1).getType());
        }
        if(forecastWeather.get(1).getFengli() != null)
        {
            windTv2.setText("风力：" + forecastWeather.get(1).getFengli());
        }

        //更新天气图片
        switch (forecastWeather.get(1).getType())
        {
            case "暴雪":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "多云":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "大雨":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "中雨":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            case "晴":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "阴":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "小雨":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "雨夹雪":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "中雪":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "阵雨":
                weatherImg2.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            default:
                break;
        }

        //weather3
        if(forecastWeather.get(2).getDate() != null)
        {
            weekTv3.setText(forecastWeather.get(2).getDate());
        }
        if(forecastWeather.get(2).getHigh() != null && forecastWeather.get(2).getLow()!= null)
        {
            temperatureTv3.setText(forecastWeather.get(2).getHigh() + "~" + forecastWeather.get(2).getLow());
        }
        if(forecastWeather.get(2).getType() != null)
        {
            climateTv3.setText(forecastWeather.get(2).getType());
        }
        if(forecastWeather.get(2).getFengli() != null)
        {
            windTv3.setText("风力：" + forecastWeather.get(2).getFengli());
        }

        //更新天气图片
        switch (forecastWeather.get(2).getType())
        {
            case "暴雪":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "多云":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "大雨":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "中雨":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            case "晴":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "阴":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "小雨":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "雨夹雪":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "中雪":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "阵雨":
                weatherImg3.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            default:
                break;
        }

        //weather4
        if(forecastWeather.get(3).getDate() != null)
        {
            weekTv4.setText(forecastWeather.get(3).getDate());
        }
        if(forecastWeather.get(3).getHigh() != null && forecastWeather.get(3).getLow()!= null)
        {
            temperatureTv4.setText(forecastWeather.get(3).getHigh() + "~" + forecastWeather.get(3).getLow());
        }
        if(forecastWeather.get(3).getType() != null)
        {
            climateTv4.setText(forecastWeather.get(3).getType());
        }
        if(forecastWeather.get(3).getFengli() != null)
        {
            windTv4.setText("风力：" + forecastWeather.get(3).getFengli());
        }

        //更新天气图片
        switch (forecastWeather.get(3).getType())
        {
            case "暴雪":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "多云":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "大雨":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "中雨":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            case "晴":
                weatherImg1.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "阴":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "小雨":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "雨夹雪":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "中雪":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "阵雨":
                weatherImg4.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            default:
                break;
        }

        //weather5
//        if(forecastWeather.get(4).getDate() != null)
//        {
//            weekTv5.setText(forecastWeather.get(4).getDate());
//        }
//        if(forecastWeather.get(4).getHigh() != null && forecastWeather.get(4).getLow()!= null)
//        {
//            temperatureTv5.setText(forecastWeather.get(4).getHigh() + "~" + forecastWeather.get(4).getLow());
//        }
//        if(forecastWeather.get(4).getType() != null)
//        {
//            climateTv5.setText(forecastWeather.get(4).getType());
//        }
//        if(forecastWeather.get(4).getFengli() != null)
//        {
//            windTv5.setText("风力：" + forecastWeather.get(4).getFengli());
//        }
//
//        //更新天气图片
//        switch (forecastWeather.get(4).getType())
//        {
//            case "暴雪":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_baoxue);
//                break;
//            case "暴雨":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_baoyu);
//                break;
//            case "大暴雨":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
//                break;
//            case "大雪":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_daxue);
//                break;
//            case "多云":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_duoyun);
//                break;
//            case "雷阵雨":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
//                break;
//            case "雷阵雨冰雹":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
//                break;
//            case "大雨":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_dayu);
//                break;
//            case "中雨":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
//                break;
//            case "晴":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_qing);
//                break;
//            case "沙尘暴":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
//                break;
//            case "特大暴雨":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
//                break;
//            case "雾":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_wu);
//                break;
//            case "小雪":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
//                break;
//            case "阴":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_yin);
//                break;
//            case "小雨":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
//                break;
//            case "雨夹雪":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
//                break;
//            case "阵雪":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
//                break;
//            case "中雪":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
//                break;
//            case "阵雨":
//                weatherImg5.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
//                break;
//            default:
//                break;
//        }

        //weather6
//        if(weather6.getDate() != null)
//        {
//            weekTv6.setText(weather6.getDate());
//        }
//        if(weather6.getHigh() != null && weather6.getLow()!= null)
//        {
//            temperatureTv6.setText(weather6.getHigh() + "~" + weather6.getLow());
//        }
//        if(weather6.getType() != null)
//        {
//            climateTv6.setText(weather6.getType());
//        }
//        if(weather6.getFengli() != null)
//        {
//            windTv6.setText("风力：" + weather6.getFengli());
//        }
//
//        //更新天气图片
//        switch (weather6.getType())
//        {
//            case "暴雪":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_baoxue);
//                break;
//            case "暴雨":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_baoyu);
//                break;
//            case "大暴雨":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
//                break;
//            case "大雪":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_daxue);
//                break;
//            case "多云":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_duoyun);
//                break;
//            case "雷阵雨":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
//                break;
//            case "雷阵雨冰雹":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
//                break;
//            case "大雨":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_dayu);
//                break;
//            case "中雨":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
//                break;
//            case "晴":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_qing);
//                break;
//            case "沙尘暴":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
//                break;
//            case "特大暴雨":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
//                break;
//            case "雾":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_wu);
//                break;
//            case "小雪":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
//                break;
//            case "阴":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_yin);
//                break;
//            case "小雨":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
//                break;
//            case "雨夹雪":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
//                break;
//            case "阵雪":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
//                break;
//            case "中雪":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
//                break;
//            case "阵雨":
//                weatherImg6.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
//                break;
//            default:
//                break;
//        }


        Toast.makeText(MainActivity.this , "更新成功！" , Toast.LENGTH_SHORT).show();

        mProgressBar.setVisibility(View.INVISIBLE);
        mUpdateBtn.setVisibility(View.VISIBLE);

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for(int i = 0 ; i < ids.length ; i++)
        {
            if(position == i)
            {
                dots[position].setImageResource(R.drawable.page_indicator_focused);
            }
            else
            {
                dots[position].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
