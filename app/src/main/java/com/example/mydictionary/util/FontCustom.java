package com.example.mydictionary.util;

import android.content.Context;
import android.graphics.Typeface;

/**
 *  更换自定义字体
 */

public class FontCustom {
    private static String fontUrl = "STXINGKA.TTF";
    private static Typeface tf;

    /**
     * 设置字体
     */

    public static Typeface setFont(Context context){
        if(tf==null){
            tf = Typeface.createFromAsset(context.getAssets(),fontUrl);
        }
        return tf;
    }

}
