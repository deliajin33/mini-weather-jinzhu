package com.example.delia.miniweather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.delia.util.NetUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by delia on 21/09/2017.
 */

public class MainActivity extends Activity implements View.OnClickListener
{
    private ImageView mUpdateBtn;

    @Override
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
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather" , address);
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
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("myWeather", str);

                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);
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

}
