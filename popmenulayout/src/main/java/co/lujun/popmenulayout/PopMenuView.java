package co.lujun.popmenulayout;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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
    private List<MenuBean> mMenus;

    private int mLayoutManagerOrientation = LinearLayoutManager.VERTICAL;

    private int mWidth, mHeight;

    public PopMenuView(Context context){
        init(context);
    }

    private void init(Context context){
        mContext = context;
        mMenus = new ArrayList<MenuBean>();
        mMenuAdapter = new MenuAdapter(mMenus);
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

    private void dealMenuClickEvent(int level1Index, int level2Index, int level3Index){
        // TODO deal click event in PopMenuView

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

    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public List<MenuBean> getMenus() {
        return mMenus;
    }

    public void setMenus(List<MenuBean> mMenus) {
        this.mMenus = mMenus;
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
}
