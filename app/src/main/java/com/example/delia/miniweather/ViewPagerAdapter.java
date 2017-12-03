package com.example.delia.miniweather;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by delia on 03/12/2017.
 */

public class ViewPagerAdapter extends PagerAdapter
{
    List<View> viewContainter;
    Context context;

    public ViewPagerAdapter(List<View> viewContainter, Context context)
    {
        this.viewContainter = viewContainter;
        this.context = context;
    }
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        //return false;
        return arg0 == arg1;
    }

    //viewpager中的组件数量
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return viewContainter.size();
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        // TODO Auto-generated method stub
        //super.destroyItem(container, position, object);
        ((ViewPager)container).removeView(viewContainter.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub
        //return super.instantiateItem(container, position);
        ((ViewPager)container).addView(viewContainter.get(position));
        return viewContainter.get(position);
    }

}
