package com.tk.animcompat;

import android.content.res.Resources;

/**
 * <pre>
 *     author : TK
 *     time   : 2017/03/28
 *     desc   : 像素转换工具
 * </pre>
 */
public final class DensityUtil {

    /**
     * dp > px
     *
     * @param dpValue
     * @return
     */
    public static int dp2px(float dpValue) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dpValue * density);
    }

    /**
     * px > dp
     *
     * @param pxValue
     * @return
     */
    public static int px2dp(float pxValue) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(pxValue / density);
    }

    /**
     * px > sp
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(float pxValue) {
        float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return Math.round(pxValue / fontScale);
    }

    /**
     * sp > px
     *
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue) {
        float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return Math.round(spValue * fontScale);
    }
}