package com.example.delia.miniweather;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class Guide extends Activity implements ViewPager.OnPageChangeListener
{
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private List<View> viewList = null;
    private View view1,view2,view3;

    private ImageView[] dots;
    private int[] ids = {R.id.dot_focused_guide,R.id.dot_unfocused_guide};


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        initView();
        initDots();
    }

    public void initView()
    {
        viewPager = (ViewPager)findViewById(R.id.viewpager_guide);
        //Adapter数据准备
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        view1 = layoutInflater.inflate(R.layout.guide_1 , null);
        view2 = layoutInflater.inflate(R.layout.guide_2 , null);
        view3 = layoutInflater.inflate(R.layout.guide_3 , null);
        viewList = new ArrayList<>();
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        viewPagerAdapter = new ViewPagerAdapter(viewList,this);
        viewPager.setAdapter(viewPagerAdapter);
    }

    public void initDots()
    {
        dots = new ImageView[viewList.size()];
        for(int i = 0 ; i < viewList.size() ; i++)
        {
            dots[i] = findViewById(ids[i]);
        }
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
