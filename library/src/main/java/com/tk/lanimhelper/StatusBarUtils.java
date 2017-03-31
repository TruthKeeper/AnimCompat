package com.tk.lanimhelper;

import android.content.Context;

/**
 * <pre>
 *     author : TK
 *     time   : 2017/03/28
 *     desc   : 状态栏工具
 * </pre>
 */
public class StatusBarUtils {
    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
}
