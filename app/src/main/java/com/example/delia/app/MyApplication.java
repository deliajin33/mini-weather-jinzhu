package com.example.delia.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.example.delia.bean.City;
import com.example.delia.db.CityDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by delia on 06/10/2017.
 */

//通过继承Application类来实现应用程序级的全局变量,单例,只有一个，程序共享
public class MyApplication extends Application
{
    private static final String TAG = "MyAPP";

    private static MyApplication myApplication;

    //操作数据库类的对象
    private CityDB mCityDB;
    //城市列表
    private List<City> mCityList;

    @Override
    public void onCreate()
    {
        super.onCreate();

        System.out.print("显示");

        Log.d(TAG , "MyApplication->Oncreate");

        myApplication = this;

        //数据库操作: 初始化一个操作数据库的对象
        mCityDB = openCityDB(); //该方法会返回一个手机上.db文件存储的path，该path是构造方法的参数

        initCityList();
    }

    private void initCityList()
    {
        mCityList = new ArrayList<City>();

        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                // TODO Auto-generated method stub
                prepareCityList();
            }
        }).start();

    }


    private boolean prepareCityList()
    {
        //通过调用数据库对象的方法获得所有城市信息
        mCityList = mCityDB.getAllCity();
        int i=0;

        //遍历城市信息
        for (City city : mCityList) {
            i++;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
            Log.d(TAG,cityCode+":"+cityName);
        }
        Log.d(TAG,"i="+i);
        return true;

    }

    //城市列表信息也可以被其他类使用
    public List<City> getCityList()
    {
        return mCityList;
    }

    public static MyApplication getInstance()
    {
        return myApplication;
    }


    /**将assets下的数据库文件拷贝到手机相应合适的数据库目录下，
    将该目录作为参数返回给数据库类，以便于数据库类操作时可以到相应的目录下读取到该db文件**/
    private CityDB openCityDB()
    {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases1"
                + File.separator
                + CityDB.CITY_DB_NAME;

        File db = new File(path);
        Log.d(TAG , path);

        if(!db.exists())
        {
            String pathfolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "databases1"
                    + File.separator;

            File dirFirstFolder = new File(pathfolder);

            if(!dirFirstFolder.exists())
            {
                dirFirstFolder.mkdirs();
                Log.i("MyApp","mkdirs");
            }
            Log.i("MyApp","db is not exists");

            try
            {
                InputStream is = getAssets().open("city.db");

                FileOutputStream fos = new FileOutputStream(db);

                int len = -1;

                byte[] buffer = new byte[1024];

                while( (len = is.read(buffer) ) != -1 )
                {
                    fos.write(buffer , 0 , len);
                    fos.flush();
                }

                fos.close();
                is.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                System.exit(0);

            }

        //if ends here
        }
        return new CityDB(this , path);
    }

    private String weathertype;
    private String wind;
    private String todaydate;
    private String wendu;

    public String getWeathertype() {
        return weathertype;
    }

    public void setWeathertype(String weathertype) {
        this.weathertype = weathertype;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getTodaydate() {
        return todaydate;
    }

    public void setTodaydate(String todaydate) {
        this.todaydate = todaydate;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }





}
