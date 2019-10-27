package com.example.mydictionary.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class MyTextView extends TextView {
    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    /**
     *  初始化字体
     * @param context
     */
    private void init(Context context){
        //设置字体样式
        setTypeface(FontCustom.setFont(context));
    }
}
