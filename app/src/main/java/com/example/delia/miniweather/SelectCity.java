package com.example.delia.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by delia on 05/10/2017.
 */
//选择城市界面
public class SelectCity extends Activity implements View.OnClickListener
{
    //返回按钮
    private ImageView mBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_city);

        //为返回按钮添加加监视器
        mBackBtn = (ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);


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

//    存储当前城市cityCode信息
//    public void saveCityCode()
//    {
//        SharedPreferences sharedPreferences = getSharedPreferences("config" , MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("main_city_code" , "101160101");
//    }

}
