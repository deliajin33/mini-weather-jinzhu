package com.example.delia.miniweather;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.delia.util.NetUtil;

/**
 * Created by delia on 21/09/2017.
 */

public class MainActivity extends Activity
{
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

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
}
