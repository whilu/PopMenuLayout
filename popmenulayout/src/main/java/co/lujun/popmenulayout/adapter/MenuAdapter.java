package co.lujun.popmenulayout.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import co.lujun.popmenulayout.MenuBean;
import co.lujun.popmenulayout.OnMenuClickListener;
import co.lujun.popmenulayout.R;
import co.lujun.popmenulayout.Util;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-8-16 16:27
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private Context mContext;

    private List<MenuBean> mMenus;

    private OnMenuClickListener onMenuClickListener;

    private int mLayoutManagerOrientation;

    private int mMenuWidth;

    private int mMenuHeight;

    private int mDividerColor = android.R.color.darker_gray;

    private int mExpandableIcon = R.drawable.ic_expandable_arrow;

    private int mMenuTextColor = Color.BLACK;

    private int mHorizontalMenuBackgroundRes = R.drawable.shape_default_menu;

    private int mVerticalMenuBackgroundRes = R.drawable.shape_default_menu;

    private float mMenuTextSize = 14.0f; // 14sp

    private float mDividerDp = 1.0f;

    private float mTextPaddingLeft = 10.0f; // 10dp

    private float mTextPaddingRight = 10.0f; // 10dp

    private float mTextPaddingTop = 5.0f; // 5dp

    private float mTextPaddingBottom = 5.0f; // 5dp

    private static final String TAG = "MenuAdapter";

    public MenuAdapter(Context context, List<MenuBean> menus, int orientation){
        this(context, menus, -1, -1, orientation);
    }

    public MenuAdapter(Context context, List<MenuBean> menus, int width, int height,
                       int orientation){
        this.mContext = context;
        this.mMenus = menus;
        this.mMenuWidth = width;
        this.mMenuHeight = height;
        this.mLayoutManagerOrientation = orientation;
        this.mTextPaddingLeft = Util.dp2px(mContext, mTextPaddingLeft);
        this.mTextPaddingTop = Util.dp2px(mContext, mTextPaddingTop);
        this.mTextPaddingRight = Util.dp2px(mContext, mTextPaddingRight);
        this.mTextPaddingBottom = Util.dp2px(mContext, mTextPaddingBottom);
        this.mDividerDp = Util.dp2px(mContext, mDividerDp);
    }

    @Override
    public int getItemCount() {
        return mMenus.size();
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MenuViewHolder(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.view_menu_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        final MenuBean menu = mMenus.get(position);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                holder.rlRootView.getLayoutParams();
        if (mMenuWidth != -1) {
            params.width = mMenuWidth;
        }
        if (mMenuHeight != -1) {
            params.height = mMenuHeight;
        }
        holder.rlRootView.setLayoutParams(params);

        holder.tvMenuText.setTextColor(mMenuTextColor);
        holder.tvMenuText.setTextSize(mMenuTextSize);
        holder.tvMenuText.setText(menu.getText());
        holder.tvMenuText.setPadding((int) mTextPaddingLeft, (int) mTextPaddingTop,
                (int) mTextPaddingRight, (int) mTextPaddingBottom);

        if (menu.isExpandable()){
            holder.ivMenuIcon.setImageResource(mExpandableIcon);
            holder.ivMenuIcon.setVisibility(View.VISIBLE);
        }else {
            holder.ivMenuIcon.setVisibility(View.GONE);
        }

        if (mLayoutManagerOrientation == LinearLayoutManager.HORIZONTAL){
            holder.rlRootView.setBackgroundResource(mHorizontalMenuBackgroundRes);
            holder.viewDividerBottom.setVisibility(View.GONE);

            RelativeLayout.LayoutParams dividerRightParams = (RelativeLayout.LayoutParams)
                    holder.viewDividerRight.getLayoutParams();
            dividerRightParams.width = (int) mDividerDp;
            holder.viewDividerRight.setLayoutParams(dividerRightParams);
            holder.viewDividerRight.setVisibility(position == mMenus.size() - 1 ?
                    View.GONE : View.VISIBLE);
        }else {
            holder.rlRootView.setBackgroundResource(mVerticalMenuBackgroundRes);
            holder.viewDividerRight.setVisibility(View.GONE);

            RelativeLayout.LayoutParams bottomRightParams = (RelativeLayout.LayoutParams)
                    holder.viewDividerBottom.getLayoutParams();
            bottomRightParams.height = (int) mDividerDp;
            holder.viewDividerBottom.setLayoutParams(bottomRightParams);
            holder.viewDividerBottom.setVisibility(position == mMenus.size() - 1 ?
                    View.GONE : View.VISIBLE);
        }

        holder.tvMenuText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMenuClickListener != null){
                    char[] indexes = menu.getIndex().toCharArray();
                    int level1Index = indexes.length >= 1 ?
                            Character.getNumericValue(indexes[0]) : -1;
                    int level2Index = indexes.length >= 2 ?
                            Character.getNumericValue(indexes[1]) : -1;
                    int level3Index = indexes.length >= 3 ?
                            Character.getNumericValue(indexes[2]) : -1;
                    onMenuClickListener.onMenuClick(level1Index, level2Index, level3Index);
                }
            }
        });
    }

    public void setOnMenuClickListener(OnMenuClickListener listener){
        this.onMenuClickListener = listener;
    }

    public int getMenuHeight() {
        return mMenuHeight;
    }

    public void setMenuHeight(int mMenuHeight) {
        this.mMenuHeight = mMenuHeight;
    }

    public int getMenuWidth() {
        return mMenuWidth;
    }

    public void setMenuWidth(int mMenuWidth) {
        this.mMenuWidth = mMenuWidth;
    }

    public float getDividerDp() {
        return mDividerDp;
    }

    public void setDividerDp(float mDividerDp) {
        this.mDividerDp = mDividerDp;
    }

    public int getDividerColor() {
        return mDividerColor;
    }

    public void setDividerColor(int mDividerColor) {
        this.mDividerColor = mDividerColor;
    }

    public int getExpandableIcon() {
        return mExpandableIcon;
    }

    public void setExpandableIcon(int mExpandableIcon) {
        this.mExpandableIcon = mExpandableIcon;
    }

    public float getMenuTextSize() {
        return Util.sp2px(mContext, mMenuTextSize);
    }

    public void setMenuTextSize(float mMenuTextSize) {
        this.mMenuTextSize = mMenuTextSize;
    }

    public int getMenuTextColor() {
        return mMenuTextColor;
    }

    public void setMenuTextColor(int mMenuTextColor) {
        this.mMenuTextColor = mMenuTextColor;
    }

    public int getHorizontalMenuBackgroundRes() {
        return mHorizontalMenuBackgroundRes;
    }

    public void setHorizontalMenuBackgroundRes(int mHorizontalMenuBackgroundRes) {
        this.mHorizontalMenuBackgroundRes = mHorizontalMenuBackgroundRes;
    }

    public int getVerticalMenuBackgroundRes() {
        return mVerticalMenuBackgroundRes;
    }

    public void setVerticalMenuBackgroundRes(int mVerticalMenuBackgroundRes) {
        this.mVerticalMenuBackgroundRes = mVerticalMenuBackgroundRes;
    }

    public float getTextPaddingBottom() {
        return mTextPaddingBottom;
    }

    public void setTextPaddingBottom(float mTextPaddingBottom) {
        this.mTextPaddingBottom = mTextPaddingBottom;
    }

    public float getTextPaddingTop() {
        return mTextPaddingTop;
    }

    public void setTextPaddingTop(float mTextPaddingTop) {
        this.mTextPaddingTop = mTextPaddingTop;
    }

    public float getTextPaddingRight() {
        return mTextPaddingRight;
    }

    public void setTextPaddingRight(float mTextPaddingRight) {
        this.mTextPaddingRight = mTextPaddingRight;
    }

    public float getTextPaddingLeft() {
        return mTextPaddingLeft;
    }

    public void setTextPaddingLeft(float mTextPaddingLeft) {
        this.mTextPaddingLeft = mTextPaddingLeft;
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder{

        TextView tvMenuText;
        ImageView ivMenuIcon;
        RelativeLayout rlRootView;
        View viewDividerBottom;
        View viewDividerRight;

        public MenuViewHolder(View v){
            super(v);
            tvMenuText = (TextView) v.findViewById(R.id.tv_menu_text);
            ivMenuIcon = (ImageView) v.findViewById(R.id.iv_expandable_icon);
            rlRootView = (RelativeLayout) v.findViewById(R.id.menuItemRootView);
            viewDividerBottom = (View) v.findViewById(R.id.view_divider_bottom);
            viewDividerRight = (View) v.findViewById(R.id.view_divider_right);
        }
    }
}
