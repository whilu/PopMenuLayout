package co.lujun.popmenulayout;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.lujun.popmenulayout.adapter.MenuAdapter;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-8-16 14:54
 */
public class PopMenuView extends PopupWindow {

    private Context mContext;

    private View mRootView;

    private CardView mCardView;

    private RecyclerView mRecyclerView;

    private MenuAdapter mMenuAdapter;

    private LinearLayoutManager mLayoutManager;

    private PopMenuLayout mPopMenuLayout;

    private PopMenuView mChildPopMenuView;

    private PopMenuView mParentPopMenuView;

    private OnMenuClickListener mOnMenuClickListener;

    private List<MenuBean> mMenus;

    private int mLayoutManagerOrientation = LinearLayoutManager.VERTICAL;

    private int mWidth = -1;

    private int mHeight = -1;

    private int mAnimStyle = -1;

    private float mMenuItemHeight = 50.0f; // default 50dp

    private boolean isWithLevel1MenuWidth = false;

    private static final String TAG = "PopMenuView";

    public PopMenuView(Context context, PopMenuLayout popMenuLayout, int mWidth, int mHeight){
        this(context, popMenuLayout, null, mWidth, mHeight);
    }

    public PopMenuView(Context context, PopMenuLayout popMenuLayout,
                       PopMenuView parentPopMenuView, int mWidth, int mHeight){
        this.mContext = context;
        this.mPopMenuLayout = popMenuLayout;
        this.mParentPopMenuView = parentPopMenuView;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        init(context);
    }

    private void init(Context context){
        mMenus = new ArrayList<MenuBean>();
        mMenuAdapter = new MenuAdapter(mContext, mMenus, mLayoutManagerOrientation);
        mMenuAdapter.setMenuWidth(mWidth);
        mMenuAdapter.setOnMenuClickListener(new OnMenuClickListener() {
            @Override
            public void onMenuClick(int level1Index, int level2Index, int level3Index) {
                dealMenuClickEvent(level1Index, level2Index, level3Index);
            }
        });
        mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(mLayoutManagerOrientation);

        mRootView = LayoutInflater.from(mContext).inflate(R.layout.view_menu_container, null);
        mCardView = (CardView) mRootView.findViewById(R.id.cardView);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mCardView.getLayoutParams();
        if (mWidth != -1){
            params.width = mWidth;
        }
        if (mHeight != -1){
            params.height = mHeight;
        }
        mCardView.setLayoutParams(params);

        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mMenuAdapter);

        setContentView(mRootView);
        setWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
        setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setAnimationStyle(mAnimStyle);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (getParentPopMenuView() != null && getParentPopMenuView().isShowing()){
            getParentPopMenuView().dismiss();
        }
        if(mPopMenuLayout != null){
            mPopMenuLayout.setAllChildLevelMenuDismissFlag();
        }
    }

    private void dealMenuClickEvent(int level1Index, int level2Index, int level3Index){
        if (mPopMenuLayout != null && mPopMenuLayout.getMenuShow()[1] &&
                !mPopMenuLayout.getMenuShow()[2] && mMenus.get(level2Index).isExpandable()){
            mPopMenuLayout.setMenus(2, true);
            PopMenuView popMenuView;
            if (getChildPopMenuView() == null){
                setChildPopMenuView(new PopMenuView(mContext, mPopMenuLayout, this,
                        mWidth, mHeight));
            }
            popMenuView = getChildPopMenuView();
            List<MenuBean> menus = mMenus.get(level2Index).getChild();
            popMenuView.setMenus(menus);
            popMenuView.setOnMenuClickListener(mOnMenuClickListener);

            int[] location = new int[2];
            int showX, showY;
            mRootView.getLocationOnScreen(location);

            // border menu deal, this level 1 menu layout child's size must >= 2
            if (level1Index >= 1 && level1Index == mPopMenuLayout.getMenus().size() - 1){
                showX = location[0] - mRootView.getWidth();
            }else {
                showX = location[0] + mRootView.getWidth();
            }
            showY = location[1] + (level2Index - menus.size() + 1)
                    * (int) Util.dp2px(mContext, mMenuItemHeight);
            popMenuView.showAtLocation(mPopMenuLayout, Gravity.NO_GRAVITY, showX, showY);
        }else {
            if (mOnMenuClickListener != null){
                mOnMenuClickListener.onMenuClick(level1Index, level2Index, level3Index);
            }
            dismiss();
        }
    }

    private int getSuitableWidth(List<MenuBean> menus){
        if (isWithLevel1MenuWidth){
            return mWidth;
        }
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(mMenuAdapter.getMenuTextSize());
        List<Float> tmpList = new ArrayList<Float>();
        for (MenuBean menu : menus) {
            tmpList.add(paint.measureText(TextUtils.isEmpty(menu.getText()) ? "" : menu.getText()));
        }

        float tmpMaxW = Collections.max(tmpList) + mMenuAdapter.getTextPaddingLeft() +
                mMenuAdapter.getTextPaddingRight();
        if (tmpMaxW < mWidth && tmpMaxW > 0){
            mWidth = (int) tmpMaxW;
        }
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public int getWidth() {
        return mWidth;
    }

    public List<MenuBean> getMenus() {
        return mMenus;
    }

    public void setMenus(List<MenuBean> menus) {
        int suitW = getSuitableWidth(menus);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mCardView.getLayoutParams();
        params.height = menus.size() * (int) Util.dp2px(mContext, mMenuItemHeight);
        params.width = suitW;
        mCardView.setLayoutParams(params);
        mMenuAdapter.setMenuWidth(suitW);
        mMenus.clear();
        for (MenuBean menu : menus) {
            mMenus.add(menu);
        }
        if (mMenuAdapter != null){
            mMenuAdapter.notifyDataSetChanged();
        }
    }

    public int getLayoutManagerOrientation() {
        return mLayoutManagerOrientation;
    }

    public void setLayoutManagerOrientation(int mLayoutManagerOrientation) {
        this.mLayoutManagerOrientation = mLayoutManagerOrientation;
    }

    public void setOnMenuClickListener(OnMenuClickListener listener){
        this.mOnMenuClickListener = listener;
    }

    public PopMenuView getChildPopMenuView() {
        return mChildPopMenuView;
    }

    public void setChildPopMenuView(PopMenuView mChildPopMenuView) {
        this.mChildPopMenuView = mChildPopMenuView;
    }

    public PopMenuView getParentPopMenuView() {
        return mParentPopMenuView;
    }

    public void setParentPopMenuView(PopMenuView mParentPopMenuView) {
        this.mParentPopMenuView = mParentPopMenuView;
    }

    public float getMenuItemHeight() {
        return mMenuItemHeight;
    }

    public void setMenuItemHeight(float mMenuItemHeight) {
        this.mMenuItemHeight = mMenuItemHeight;
    }

    public int getAnimStyle() {
        return mAnimStyle;
    }

    public void setAnimStyle(int mAnimStyle) {
        this.mAnimStyle = mAnimStyle;
    }

    public boolean isWithLevel1MenuWidth() {
        return isWithLevel1MenuWidth;
    }

    public void setWithLevel1MenuWidth(boolean withLevel1MenuWidth) {
        isWithLevel1MenuWidth = withLevel1MenuWidth;
    }
}
