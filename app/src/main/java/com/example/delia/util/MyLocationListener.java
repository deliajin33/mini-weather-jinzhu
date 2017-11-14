package com.example.delia.util;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

/**
 * Created by delia on 13/11/2017.
 */

public class MyLocationListener implements BDLocationListener {

    String city =null;
    @Override
    public void onReceiveLocation(BDLocation location){
        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取地址相关的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

        String addr = location.getAddrStr();    //获取详细地址信息
        String country = location.getCountry();    //获取国家
        String province = location.getProvince();    //获取省份
        city = location.getCity();    //获取城市
        String district = location.getDistrict();    //获取区县
        String street = location.getStreet();    //获取街道信息

        Log.d("myWeathers" , "city");
//        Log.d("myWeather" , "district");
//        Log.d("myWeather" , "addr");

        Log.d("myWeathers" , city.toString());
        Log.d("myWeathers" , district.toString());
        Log.d("myWeathers" , addr);

    }
    public String LocationInfo()
    {
        return city;
    }
}
