package com.sunofbeach.customview.utils;

import com.sunofbeach.customview.App;

public class SizeUtils {

    public static int dip2px( float dpValue) {
        float scale = App.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
