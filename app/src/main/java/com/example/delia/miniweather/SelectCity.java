package com.example.delia.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.delia.app.MyApplication;
import com.example.delia.bean.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by delia on 05/10/2017.
 */
//选择城市界面
public class SelectCity extends Activity implements View.OnClickListener
{
    //返回按钮
    private ImageView mBackBtn;

    private ListView mList;

    private List<City> cityList;

    private ArrayList<String> cityNameList = new ArrayList<>();

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_city);

        //为返回按钮添加加监视器
        mBackBtn = (ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        initViews();

    }

    //编写initViews方法实现城市列表的展示
    private void initViews()
    {
        mList = (ListView)findViewById(R.id.title_list);

        //从数据库表中获取城市列表信息
        MyApplication myApplication = (MyApplication)getApplication();
        cityList = myApplication.getCityList();

        for(City city : cityList)
        {
            cityNameList.add(city.getCity());
        }

        adapter = new ArrayAdapter<>(SelectCity.this,android.R.layout.simple_list_item_1,cityNameList);

        mList.setAdapter(adapter);

        mList.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int index, long l)
                    {

                        Toast.makeText(SelectCity.this , "你选择了"  + mList.getItemAtPosition(index) , Toast.LENGTH_SHORT).show();
                        //Toast.makeText(SelectCity.this , "你单击了"  + index , Toast.LENGTH_SHORT).show();

                        City city = cityList.get(index);

                        Intent i = new Intent();

                        i.putExtra("cityCode" , city.getNumber());

                        setResult(RESULT_OK , i);

                        finish();

                    }
                });
    }





    //处理组件事件
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.title_back:

//              存储城市cityCode信息
//              saveCityCode();

                Intent i = new Intent();

                i.putExtra("cityCode" , "101160101");

                setResult(RESULT_OK , i);

                finish();

                break;
            default:
                break;
        }



    //方法结束标记
    }



}
