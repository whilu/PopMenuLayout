/**
 * MIT License
 *
 * Copyright (c) 2016 lujun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package co.lujun.popmenulayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import co.lujun.popmenulayout.adapter.MenuAdapter;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-8-16 14:39
 */
public class PopMenuLayout extends RelativeLayout {

    private Context mContext;
    
    private List<MenuBean> mMenus;

    private MenuAdapter m1LevelMenuAdapter;

    private OnMenuClickListener mOnMenuClickListener;

    private RecyclerView recyclerView;

    private LinearLayoutManager mLayoutManager;

    private PopMenuView popMenuView;

    /**
     * Config json string to make menus
     */
    private String mConfigJson;

    /**
     * The orientation for Level 1 menu's LayoutManager.
     */
    private int mLayoutManagerOrientation = LinearLayoutManager.HORIZONTAL;

    /**
     *  PopMenuLayout width.
     */
    private int mWidth;

    /**
     * PopMenuLayout height.
     */
    private int mHeight;

    /**
     * Animation style to use when the child popup menu appears and disappears.
     */
    private int mLevel2MenuAnimStyle = -1;

    /**
     * Level 1 menu height, default 50(dp).
     */
    private float mLevel1MenuItemHeight = 50.0f;

    /**
     * Child popup menu height, default 50(dp).
     */
    private float mChildMenuItemHeight = 50.0f;

    /**
     * Whether the child popup menu's width always equals to level 1 menu's width.
     */
    private boolean isWithLevel1MenuWidth = false;

    /**
     * The divider width(height) between menus, default 1(dp).
     */
    private float mMenuDividerDp = 1.0f;

    /**
     * The left padding for menu's content text, default 10(dp).
     */
    private float mMenuTextPaddingLeft = 10.0f;

    /**
     * The right padding for menu's content text, default 10(dp).
     */
    private float mMenuTextPaddingRight = 10.0f;

    /**
     * The top padding for menu's content text, default 5(dp).
     */
    private float mMenuTextPaddingTop = 5.0f;

    /**
     * The bottom padding for menu's content text, default 5(dp).
     */
    private float mMenuTextPaddingBottom = 5.0f;

    /**
     * The text size for menu's content text, default 14(sp).
     */
    private float mMenuTextSize = 14.0f;

    /**
     * The divider color between menus, default Color.GRAY.
     */
    private int mDividerColor = Color.GRAY;

    /**
     * The icon for expandable menu.
     */
    private int mExpandableIcon = R.drawable.ic_expandable_arrow;

    /**
     * The text color for menu, default Color.BLACK.
     */
    private int mMenuTextColor = Color.BLACK;

    /**
     * The child menu's container layout background color(default white).
     */
    private int mChildMenuLayoutBgColor = Color.WHITE;

    /**
     * The level 1 menu's container layout background color(default white).
     */
    private int mLevel1MenuLayoutBgColor = Color.WHITE;

    /**
     * The background resource for Level 1 menu.
     */
    private int mHorizontalMenuBackgroundRes = R.drawable.shape_default_menu;

    /**
     * The background resource for child menus.
     */
    private int mVerticalMenuBackgroundRes = R.drawable.shape_default_menu;

    /**
     * The max size for child menu item, default 4.
     */
    private int mMaxMenuItemCount = 4;

    /**
     * Divider margin left, default 0(dp).
     */
    private float mDividerMarginLeft = 0.0f;

    /**
     * Divider margin right, default 0(dp).
     */
    private float mDividerMarginRight = 0.0f;

    /**
     * Divider margin top, default 0(dp).
     */
    private float mDividerMarginTop = 0.0f;

    /**
     * Divider margin bottom, default 0(dp).
     */
    private float mDividerMarginBottom = 0.0f;

    private boolean[] mMenuShow;
    
    private static final int SUPPORT_MENU_LEVEL = 3;

    private static final String TAG = "PopMenuLayout";

    public PopMenuLayout(Context context) {
        this(context, null);
    }

    public PopMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr){
        initAttrs(context, attrs, defStyleAttr);
        mMenuShow = new boolean[SUPPORT_MENU_LEVEL];
        for (int i = 0; i < SUPPORT_MENU_LEVEL; i++) {
            mMenuShow[i] = i == 0;
        }
        mContext = context;
        mMenus = new ArrayList<MenuBean>();
        m1LevelMenuAdapter = new MenuAdapter(mContext, mMenus, mLayoutManagerOrientation);
        m1LevelMenuAdapter.setMenuHeight((int) mLevel1MenuItemHeight);
        m1LevelMenuAdapter.setOnMenuClickListener(new OnMenuClickListener() {
            @Override
            public void onMenuClick(int level1Index, int level2Index, int level3Index) {
                dealMenuClickEvent(level1Index, level2Index, level3Index);
            }
        });
        invalidateViewsAttr();

        recyclerView = new RecyclerView(mContext, attrs, defStyleAttr);
        recyclerView.setId(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(mLayoutManagerOrientation);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setBackgroundColor(mLevel1MenuLayoutBgColor);

        LayoutParams params = new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        recyclerView.setLayoutParams(params);

        addView(recyclerView);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr){
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.PopMenuLayout,
                defStyleAttr, 0);
        mConfigJson = attributes.getString(R.styleable.PopMenuLayout_config_json);
        mLevel2MenuAnimStyle = attributes.getResourceId(
                R.styleable.PopMenuLayout_level2_menu_anim_style, mLevel2MenuAnimStyle);
        mLevel1MenuItemHeight = attributes.getDimension(
                R.styleable.PopMenuLayout_level1_menu_item_height,
                Util.dp2px(context, mLevel1MenuItemHeight));
        mChildMenuItemHeight = attributes.getDimension(
                R.styleable.PopMenuLayout_child_menu_item_height,
                Util.dp2px(context, mChildMenuItemHeight));
        isWithLevel1MenuWidth = attributes.getBoolean(
                R.styleable.PopMenuLayout_cmenu_w_follow_level1_menu, isWithLevel1MenuWidth);
        mMenuDividerDp = attributes.getDimension(
                R.styleable.PopMenuLayout_menu_divider_width, Util.dp2px(context, mMenuDividerDp));
        mMenuTextPaddingLeft = attributes.getDimension(
                R.styleable.PopMenuLayout_menu_left_padding,
                Util.dp2px(context, mMenuTextPaddingLeft));
        mMenuTextPaddingRight = attributes.getDimension(
                        R.styleable.PopMenuLayout_menu_right_padding,
                Util.dp2px(context, mMenuTextPaddingRight));
        mMenuTextPaddingTop = attributes.getDimension(
                R.styleable.PopMenuLayout_menu_top_padding,
                Util.dp2px(context, mMenuTextPaddingTop));
        mMenuTextPaddingBottom = attributes.getDimension(
                        R.styleable.PopMenuLayout_menu_bottom_padding,
                Util.dp2px(context, mMenuTextPaddingBottom));
        mDividerColor = attributes.getColor(R.styleable.PopMenuLayout_menu_divider_color,
                mDividerColor);
        mExpandableIcon = attributes.getResourceId(R.styleable.PopMenuLayout_menu_expandable_icon,
                mExpandableIcon);
        mMenuTextColor = attributes.getColor(R.styleable.PopMenuLayout_menu_text_color,
                mMenuTextColor);
        mHorizontalMenuBackgroundRes = attributes.getResourceId(
                R.styleable.PopMenuLayout_horizontal_menu_bg, mHorizontalMenuBackgroundRes);
        mVerticalMenuBackgroundRes = attributes.getResourceId(
                R.styleable.PopMenuLayout_vertical_menu_bg, mVerticalMenuBackgroundRes);
        mMenuTextSize = attributes.getDimension(R.styleable.PopMenuLayout_menu_text_size,
                Util.sp2px(context, mMenuTextSize));
        mMaxMenuItemCount = attributes.getInteger(R.styleable.PopMenuLayout_child_menu_max_count,
                mMaxMenuItemCount);
        mLevel1MenuLayoutBgColor = attributes.getColor(
                R.styleable.PopMenuLayout_level1_menu_layout_bg_color, mLevel1MenuLayoutBgColor);
        mChildMenuLayoutBgColor = attributes.getColor(
                R.styleable.PopMenuLayout_cmenu_layout_bg_color, mChildMenuLayoutBgColor);
        mDividerMarginLeft = attributes.getDimension(R.styleable.PopMenuLayout_divider_margin_left,
                Util.dp2px(context, mDividerMarginLeft));
        mDividerMarginRight = attributes.getDimension(
                R.styleable.PopMenuLayout_divider_margin_right,
                Util.dp2px(context, mDividerMarginRight));
        mDividerMarginTop = attributes.getDimension(R.styleable.PopMenuLayout_divider_margin_top,
                Util.dp2px(context, mDividerMarginTop));
        mDividerMarginBottom = attributes.getDimension(
                R.styleable.PopMenuLayout_divider_margin_bottom,
                Util.dp2px(context, mDividerMarginBottom));
        attributes.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged");
        this.mWidth = w;
        this.mHeight = h;
        initView();
    }

    private void parseJson() throws JSONException{
        if (TextUtils.isEmpty(mConfigJson)){
            return;
        }
        JSONObject object = new JSONObject(mConfigJson);
        JSONArray array = object.getJSONArray("menus");
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            mMenus.add(parseNode(obj));
        }
    }

    private MenuBean parseNode(JSONObject object) throws JSONException{
        Iterator<String> iterator = object.keys();
        MenuBean menu = new MenuBean();
        while (iterator.hasNext()){
            String key = (String) iterator.next();
            if (key.equals("index")){
                menu.setIndex(object.getString("index"));
            }else if (key.equals("expandable")){
                menu.setExpandable(object.getBoolean("expandable"));
            }else if (key.equals("text")){
                menu.setText(object.getString("text"));
            }else if (key.equals("child")){
                JSONArray array = object.getJSONArray("child");
                List<MenuBean> menus = new ArrayList<MenuBean>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    menus.add(parseNode(obj));
                }
                menu.setChild(menus);
            }
        }
        return menu;
    }

    private void dealMenuClickEvent(int level1Index, int level2Index, int level3Index){
        MenuBean menu1 = mMenus.get(level1Index);
        if (menu1.isExpandable() && isChildMenuShow()){
            // deal click event only
            dealClickEventOnly(level1Index, level2Index, level3Index);
        }else if (menu1.isExpandable() && !isChildMenuShow()){
            if (!mMenuShow[1]){
                // show level 2 menu
                expandMenu(level1Index, level2Index, level3Index);
            }else {
                // deal click event only
                dealClickEventOnly(level1Index, level2Index, level3Index);
            }
        }else {
            // level1 menu can not expandable
            onMenuClick(level1Index, level2Index, level3Index);
        }
    }

    private void onMenuClick(int level1Index, int level2Index, int level3Index){
        if (mOnMenuClickListener != null){
            mOnMenuClickListener.onMenuClick(level1Index, level2Index, level3Index);
        }
    }

    private void expandMenu(int level1Index, int level2Index, int level3Index){
        // Set menus data first and then show this menu
        mMenuShow[1] = true;
        int[] location = new int[2];
        recyclerView.getLocationOnScreen(location);
        List<MenuBean> menus = mMenus.get(level1Index).getChild();
        popMenuView.setAnimStyle(mLevel2MenuAnimStyle);
        popMenuView.setMenus(menus);
        popMenuView.showAtLocation(recyclerView, Gravity.NO_GRAVITY,
                mWidth / mMenus.size() * level1Index +
                        (mWidth / mMenus.size() - popMenuView.getWidth()) / 2,
                location[1] - (menus.size() <= mMaxMenuItemCount ?
                        menus.size()  : mMaxMenuItemCount) * (int) mChildMenuItemHeight);
    }

    private void dealClickEventOnly(int level1Index, int level2Index, int level3Index){
        if (mOnMenuClickListener != null){
            mOnMenuClickListener.onMenuClick(level1Index, level2Index, level3Index);
        }
        if (popMenuView.isShowing()){
            popMenuView.dismiss();
        }
    }

    private boolean isChildMenuShow(){
        return mMenuShow[1] || mMenuShow[2];
    }

    private void initView(){
        invalidateData();

        if (popMenuView == null){
            popMenuView = new PopMenuView(mContext, this, mWidth / SUPPORT_MENU_LEVEL, -1);
            popMenuView.setMenuItemHeight(mChildMenuItemHeight);
            popMenuView.setOnMenuClickListener(new OnMenuClickListener() {
                @Override
                public void onMenuClick(int level1Index, int level2Index, int level3Index) {
                    if (mOnMenuClickListener != null){
                        mOnMenuClickListener.onMenuClick(level1Index, level2Index, level3Index);
                    }
                }
            });
            invalidateViewsAttr();
        }
    }
    
    private void invalidateViewsAttr(){
        if (m1LevelMenuAdapter != null){
            m1LevelMenuAdapter.setTextPaddingLeft(mMenuTextPaddingLeft);
            m1LevelMenuAdapter.setTextPaddingBottom(mMenuTextPaddingBottom);
            m1LevelMenuAdapter.setTextPaddingRight(mMenuTextPaddingRight);
            m1LevelMenuAdapter.setTextPaddingTop(mMenuTextPaddingTop);
            m1LevelMenuAdapter.setDividerDp(mMenuDividerDp);
            m1LevelMenuAdapter.setDividerColor(mDividerColor);
            m1LevelMenuAdapter.setExpandableIcon(mExpandableIcon);
            m1LevelMenuAdapter.setMenuTextColor(mMenuTextColor);
            m1LevelMenuAdapter.setHorizontalMenuBackgroundRes(mHorizontalMenuBackgroundRes);
            m1LevelMenuAdapter.setVerticalMenuBackgroundRes(mVerticalMenuBackgroundRes);
            m1LevelMenuAdapter.setMenuTextSize(mMenuTextSize);
            m1LevelMenuAdapter.setDividerMarginLeft(mDividerMarginLeft);
            m1LevelMenuAdapter.setDividerMarginRight(mDividerMarginRight);
            m1LevelMenuAdapter.setDividerMarginTop(mDividerMarginTop);
            m1LevelMenuAdapter.setDividerMarginBottom(mDividerMarginBottom);

        }
        if (popMenuView != null){
            popMenuView.setWithLevel1MenuWidth(isWithLevel1MenuWidth);
            popMenuView.setMenuTextPaddingLeft(mMenuTextPaddingLeft);
            popMenuView.setMenuTextPaddingBottom(mMenuTextPaddingBottom);
            popMenuView.setMenuTextPaddingRight(mMenuTextPaddingRight);
            popMenuView.setMenuTextPaddingTop(mMenuTextPaddingTop);
            popMenuView.setMenuDividerDp(mMenuDividerDp);
            popMenuView.setDividerColor(mDividerColor);
            popMenuView.setExpandableIcon(mExpandableIcon);
            popMenuView.setMenuTextColor(mMenuTextColor);
            popMenuView.setHorizontalMenuBackgroundRes(mHorizontalMenuBackgroundRes);
            popMenuView.setVerticalMenuBackgroundRes(mVerticalMenuBackgroundRes);
            popMenuView.setMenuTextSize(mMenuTextSize);
            popMenuView.setMaxMenuItemCount(mMaxMenuItemCount);
            popMenuView.setMenuLayoutBgColor(mChildMenuLayoutBgColor);
            popMenuView.setDividerMarginLeft(mDividerMarginLeft);
            popMenuView.setDividerMarginRight(mDividerMarginRight);
            popMenuView.setDividerMarginTop(mDividerMarginTop);
            popMenuView.setDividerMarginBottom(mDividerMarginBottom);
        }
    }

    private void invalidateData(){
        try {
            if (mMenus.size() <= 0){
//                mMenus.clear();
                parseJson();
            }
            m1LevelMenuAdapter.setMenuWidth(mWidth / (mMenus.size() > 0 ? mMenus.size() : 1));
            recyclerView.setAdapter(m1LevelMenuAdapter);
            m1LevelMenuAdapter.notifyDataSetChanged();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public boolean[] getMenuShow(){
        return mMenuShow;
    }

    public void setMenus(int pos, boolean expanded){
        if (pos >= 0 && pos < SUPPORT_MENU_LEVEL) {
            mMenuShow[pos] = expanded;
        }
    }

    public void setAllChildLevelMenuDismissFlag(){
        for (int i = 1; i < SUPPORT_MENU_LEVEL; i++) {
            mMenuShow[i] = false;
        }
    }

    public int getLayoutWidth() {
        return mWidth;
    }

    public String getConfigJson() {
        return mConfigJson;
    }

    /**
     * Set the config json for PopMenuLayout.
     *
     * @param configJson
     */
    public void setConfigJson(String configJson) {
        this.mConfigJson = configJson;
        invalidateData();
    }

    /**
     * Set the click listener for every menu.
     *
     * @param listener
     */
    public void setOnMenuClickListener(OnMenuClickListener listener){
        this.mOnMenuClickListener = listener;
    }

    public int getLayoutManagerOrientation() {
        return mLayoutManagerOrientation;
    }

    /**
     * Sets the orientation of the level 1 menu layout.
     *
     * @param layoutManagerOrientation
     */
    public void setLayoutManagerOrientation(int layoutManagerOrientation) {
        this.mLayoutManagerOrientation = layoutManagerOrientation;
    }

    public List<MenuBean> getMenus() {
        return mMenus;
    }

    /**
     * Set the menus with MenuBean object to make PopMenuLayout.
     *
     * @param menus
     * @see #setConfigJson(String)
     */
    public void setMenus(List<MenuBean> menus) {
        this.mMenus.clear();
        for (MenuBean menu : menus) {
            this.mMenus.add(menu);
        }
        invalidateData();
    }

    public float getChildMenuItemHeight() {
        return mChildMenuItemHeight;
    }

    /**
     * Set the child popup menu's height.
     * @param childMenuItemHeight
     */
    public void setChildMenuItemHeight(float childMenuItemHeight) {
        this.mChildMenuItemHeight = childMenuItemHeight;
    }

    public float getLevel1MenuItemHeight() {
        return mLevel1MenuItemHeight;
    }

    /**
     * Set the level 1 menu's height.
     *
     * @param level1MenuItemHeight
     */
    public void setLevel1MenuItemHeight(float level1MenuItemHeight) {
        this.mLevel1MenuItemHeight = level1MenuItemHeight;
    }

    public int getLevel2MenuAnimStyle() {
        return mLevel2MenuAnimStyle;
    }

    /**
     * Set the animation style resource for the child popup menu.
     *
     * @param level2MenuAnimStyle animation style to use when the child popup menu appears
     *        and disappears.  Set to -1 for the default animation, 0 for no
     *        animation, or a resource identifier for an explicit animation.
     */
    public void setLevel2MenuAnimStyle(int level2MenuAnimStyle) {
        this.mLevel2MenuAnimStyle = level2MenuAnimStyle;
    }

    public boolean isWithLevel1MenuWidth() {
        return isWithLevel1MenuWidth;
    }

    /**
     * Set whether child popup menu's width always equals to level 1 menu's width.
     * @param withLevel1MenuWidth
     */
    public void setWithLevel1MenuWidth(boolean withLevel1MenuWidth) {
        isWithLevel1MenuWidth = withLevel1MenuWidth;
    }

    public float getMenuTextPaddingBottom() {
        return mMenuTextPaddingBottom;
    }

    /**
     * Set the content text's bottom padding.
     * @param paddingBottom
     */
    public void setMenuTextPaddingBottom(float paddingBottom) {
        this.mMenuTextPaddingBottom = paddingBottom;
        invalidateViewsAttr();
    }

    public float getMenuTextPaddingTop() {
        return mMenuTextPaddingTop;
    }

    /**
     * Set the content text's top padding.
     * @param paddingTop
     */
    public void setMenuTextPaddingTop(float paddingTop) {
        this.mMenuTextPaddingTop = paddingTop;
        invalidateViewsAttr();
    }

    public float getMenuTextPaddingRight() {
        return mMenuTextPaddingRight;
    }

    /**
     * Set the content text's right padding.
     * @param paddingRight
     */
    public void setMenuTextPaddingRight(float paddingRight) {
        this.mMenuTextPaddingRight = paddingRight;
        invalidateViewsAttr();
    }

    public float getMenuTextPaddingLeft() {
        return mMenuTextPaddingLeft;
    }

    /**
     * Set the content text's left padding.
     * @param paddingLeft
     */
    public void setMenuTextPaddingLeft(float paddingLeft) {
        this.mMenuTextPaddingLeft = paddingLeft;
        invalidateViewsAttr();
    }

    public float getMenuDividerDp() {
        return mMenuDividerDp;
    }

    /**
     * Set divider width(height) between menus.
     * @param menuDividerDp
     */
    public void setMenuDividerDp(float menuDividerDp) {
        this.mMenuDividerDp = menuDividerDp;
        invalidateViewsAttr();
    }

    public int getDividerColor() {
        return mDividerColor;
    }

    /**
     * Set the color of menu's divider.
     * @param dividerColor
     */
    public void setDividerColor(int dividerColor) {
        this.mDividerColor = dividerColor;
        invalidateViewsAttr();
    }

    public int getExpandableIcon() {
        return mExpandableIcon;
    }

    /**
     * Set the icon resource for the expandable menu.
     * @param expandableIcon
     */
    public void setExpandableIcon(int expandableIcon) {
        this.mExpandableIcon = expandableIcon;
        invalidateViewsAttr();
    }

    public int getMenuTextColor() {
        return mMenuTextColor;
    }

    /**
     * Set the menu's text color.
     * @param textColor
     */
    public void setMenuTextColor(int textColor) {
        this.mMenuTextColor = textColor;
        invalidateViewsAttr();
    }

    public int getHorizontalMenuBackgroundRes() {
        return mHorizontalMenuBackgroundRes;
    }

    /**
     * Set the level 1 menu's background resource.
     * @param horizontalMenuBackgroundRes
     */
    public void setHorizontalMenuBackgroundRes(int horizontalMenuBackgroundRes) {
        this.mHorizontalMenuBackgroundRes = horizontalMenuBackgroundRes;
        invalidateViewsAttr();
    }

    public int getVerticalMenuBackgroundRes() {
        return mVerticalMenuBackgroundRes;
    }

    /**
     * Set the child popup menu's background resource.
     * @param verticalMenuBackgroundRes
     */
    public void setVerticalMenuBackgroundRes(int verticalMenuBackgroundRes) {
        this.mVerticalMenuBackgroundRes = verticalMenuBackgroundRes;
        invalidateViewsAttr();
    }

    public float getMenuTextSize() {
        return mMenuTextSize;
    }

    /**
     * Set the menu's text size.
     * @param textSize
     */
    public void setMenuTextSize(float textSize) {
        this.mMenuTextSize = textSize;
        invalidateViewsAttr();
    }

    public int getMaxMenuItemCount() {
        return mMaxMenuItemCount;
    }

    /**
     * Set the max show count of child popup menu layout.
     * @param maxMenuItemCount
     */
    public void setMaxMenuItemCount(int maxMenuItemCount) {
        this.mMaxMenuItemCount = maxMenuItemCount;
    }

    public int getChildMenuLayoutBgColor() {
        return mChildMenuLayoutBgColor;
    }

    /**
     * Set child menu's layout background color.
     *
     * @param childMenuLayoutBgColor
     */
    public void setChildMenuLayoutBgColor(int childMenuLayoutBgColor) {
        this.mChildMenuLayoutBgColor = childMenuLayoutBgColor;
    }

    public int getLevel1MenuLayoutBgColor() {
        return mLevel1MenuLayoutBgColor;
    }

    /**
     * Set the level 1 menu's container layout background color.
     *
     * @param level1MenuLayoutBgColor
     */
    public void setLevel1MenuLayoutBgColor(int level1MenuLayoutBgColor) {
        this.mLevel1MenuLayoutBgColor = level1MenuLayoutBgColor;
    }

    public float getDividerMarginBottom() {
        return mDividerMarginBottom;
    }

    /**
     * Set divider bottom margin.
     *
     * @param dividerMarginBottom
     */
    public void setDividerMarginBottom(float dividerMarginBottom) {
        this.mDividerMarginBottom = dividerMarginBottom;
    }

    public float getDividerMarginTop() {
        return mDividerMarginTop;
    }

    /**
     * Set divider top margin.
     *
     * @param dividerMarginTop
     */
    public void setDividerMarginTop(float dividerMarginTop) {
        this.mDividerMarginTop = dividerMarginTop;
    }

    public float getDividerMarginRight() {
        return mDividerMarginRight;
    }

    /**
     * Set divider right margin.
     *
     * @param dividerMarginRight
     */
    public void setDividerMarginRight(float dividerMarginRight) {
        this.mDividerMarginRight = dividerMarginRight;
    }

    public float getDividerMarginLeft() {
        return mDividerMarginLeft;
    }

    /**
     * Set divider left margin.
     *
     * @param dividerMarginLeft
     */
    public void setDividerMarginLeft(float dividerMarginLeft) {
        this.mDividerMarginLeft = dividerMarginLeft;
    }

}
