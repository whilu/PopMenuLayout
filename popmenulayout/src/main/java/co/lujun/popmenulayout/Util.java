package co.lujun.popmenulayout;

import android.content.Context;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-8-17 16:16
 */
public class Util {

    public static float dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
