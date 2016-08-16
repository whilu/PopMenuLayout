package co.lujun.popmenulayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-8-16 14:54
 */
public class PopMenuView extends LinearLayout {

    private Context mContext;

    public PopMenuView(Context context){
        this(context, null);
    }

    public PopMenuView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public PopMenuView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr){
        this.mContext = context;
    }
}
