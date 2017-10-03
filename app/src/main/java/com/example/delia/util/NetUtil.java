package com.example.delia.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * Created by delia on 27/09/2017.
 */

public class NetUtil
{
    public static final int NETWORN_NONE = 0;
    public static final int NETWORN_WIFI = 1;
    public static final int NETWORN_MOBILE = 2;

    public static int getNetworkState(Context context)
    {
        //ConnectivityManager主要用于查看网络状态和管理网络连接相关的操作
        ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //NetworkInfo对象包含网络连接的所有信息
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if(networkInfo == null)
        {
            return NETWORN_NONE;
        }

        int nType = networkInfo.getType();
        if(nType == ConnectivityManager.TYPE_MOBILE)
        {
            return NETWORN_MOBILE;
        }
        else if (nType == ConnectivityManager.TYPE_WIFI)
        {
            return NETWORN_WIFI;
        }
        return NETWORN_NONE;
    }
}
