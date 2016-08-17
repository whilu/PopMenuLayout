package co.lujun.popmenulayout;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import java.util.ArrayList;
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
    private PopMenuView mChildPopMenuView, mParentPopMenuView;
    private OnMenuClickListener mOnMenuClickListener;
    private List<MenuBean> mMenus;

    private int mLayoutManagerOrientation = LinearLayoutManager.VERTICAL;

    private int mWidth = -1;
    private int mHeight = -1;

    private static final String TAG = "PopMenuView";

    public PopMenuView(Context context, PopMenuLayout popMenuLayout, int mWidth, int mHeight){
        this(context, popMenuLayout, null, mWidth, mHeight);
    }

    public PopMenuView(Context context, PopMenuLayout popMenuLayout,
                       PopMenuView parentPopMenuView, int mWidth, int mHeight){
        this.mPopMenuLayout = popMenuLayout;
        this.mParentPopMenuView = parentPopMenuView;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        init(context);
    }

    private void init(Context context){
        mContext = context;
        mMenus = new ArrayList<MenuBean>();
        mMenuAdapter = new MenuAdapter(mMenus);
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

        // TODO add show & hide animation

        // TODO finish click event when click position outside of current view

        // TODO add attributes

    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(mPopMenuLayout != null){
            mPopMenuLayout.setMenus(1, false);
            mPopMenuLayout.setMenus(2, false);
        }
        if (getParentPopMenuView() != null && getParentPopMenuView().isShowing()){
            getParentPopMenuView().dismiss();
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
            popMenuView.setMenus(mMenus.get(level2Index).getChild());
            popMenuView.setOnMenuClickListener(mOnMenuClickListener);
            popMenuView.showAtLocation(mPopMenuLayout, Gravity.NO_GRAVITY,
                    (int) mRootView.getX() + mRootView.getWidth(),
                    (int) mRootView.getY() + level2Index * mRootView.getHeight());
        }else {
            if (mOnMenuClickListener != null){
                mOnMenuClickListener.onMenuClick(level1Index, level2Index, level3Index);
            }
            dismiss();
        }
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
        // TODO every menu item height 40dp, update this with an attribute
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mCardView.getLayoutParams();
        params.height = (int)Util.dp2px(mContext, menus.size() * 40.0f);
        mCardView.setLayoutParams(params);
//        setHeight((int)Util.dp2px(mContext, menus.size() * 40.0f));
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


}
