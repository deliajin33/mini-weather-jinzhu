package com.example.delia.miniweather;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
//import android.widget.EditText;




/**
 * Created by delia on 12/11/2017.
 */

public class MyEditText extends android.support.v7.widget.AppCompatEditText implements View.OnFocusChangeListener,TextWatcher
{
    private Drawable myDrawable;

    public MyEditText(Context context)
    {
        this(context , null);
    }

    public MyEditText(Context context , AttributeSet attrs)
    {
        this(context , attrs , android.R.attr.editTextStyle);
    }

    public MyEditText(Context context , AttributeSet attrs , int defStyle)
    {
        super(context, attrs, defStyle);

        init();
    }
    private void init()
    {
        myDrawable = getCompoundDrawables()[2];
        if(myDrawable == null)
        {
            myDrawable = getResources().getDrawable(R.drawable.magnifying_glass);

            myDrawable.setBounds(0 ,0 , myDrawable.getIntrinsicWidth() , myDrawable.getIntrinsicHeight());

            setClearIconVisible(false);
            setOnFocusChangeListener(this);
            addTextChangedListener(this);

        }

    }


    public boolean onTouchEvent(MotionEvent event)
    {

        if(getCompoundDrawables()[2] != null)
        {
            if(event.getAction() == MotionEvent.ACTION_UP)
            {
                boolean touchable = event.getX() > (getWidth()-getPaddingRight()-myDrawable.getIntrinsicHeight()) && (event.getX() < ((getWidth() - getPaddingRight())));
                if(touchable)
                {
                    this.setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void onFocusChange(View v , boolean hasFocus)
    {
        if(hasFocus)
        {
            setClearIconVisible(getText().length() > 0);
        }
        else
        {
            setClearIconVisible(false);
        }
    }

    protected void setClearIconVisible(boolean visible)
    {
        Drawable right = visible ? myDrawable : null;

        setCompoundDrawables(getCompoundDrawables()[0] , getCompoundDrawables()[1] , right , getCompoundDrawables()[3]);
    }


    public void onTextChanged(CharSequence s , int start , int count , int after)
    {
        setClearIconVisible(s.length() > 0);
    }


    public void beforeTextChanged(CharSequence s , int start , int count , int after)
    {

    }

    public void afterTextChanged(Editable editable)
    {

    }

//    public void setShakeAnimation()
//    {
//        this.setAnimation(shakeAnimation(5));
//    }



}
