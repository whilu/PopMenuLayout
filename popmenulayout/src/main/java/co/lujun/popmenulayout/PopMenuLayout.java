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

    private String mConfigJson;

    private List<MenuBean> mMenus;

    private MenuAdapter m1LevelMenuAdapter;

    private OnMenuClickListener mOnMenuClickListener;

    private RecyclerView recyclerView;

    private LinearLayoutManager mLayoutManager;

    private PopMenuView popMenuView;

    private int mLayoutManagerOrientation = LinearLayoutManager.HORIZONTAL;

    private int mWidth; // PopMenuLayout width

    private int mHeight; // PopMenuLayout height

    private int mLevel2MenuAnimStyle = -1;

    private boolean[] mMenuShow;

    private float mLevel1MenuItemHeight = 50.0f;

    private float mChildMenuItemHeight = 50.0f;

    private boolean isWithLevel1MenuWidth = false;

    private static final int SUPPORT_MENU_LEVEL = 3;

    private float mMenuDividerDp = 1.0f;

    private float mMenuTextPaddingLeft = 10.0f; // 10dp

    private float mMenuTextPaddingRight = 10.0f; // 10dp

    private float mMenuTextPaddingTop = 5.0f; // 5dp

    private float mMenuTextPaddingBottom = 5.0f; // 5dp

    private float mMenuTextSize = 14.0f; // 14sp

    private int mDividerColor = Color.GRAY;

    private int mExpandableIcon = R.drawable.ic_expandable_arrow;

    private int mMenuTextColor = Color.BLACK;

    private int mHorizontalMenuBackgroundRes = R.drawable.shape_default_menu;

    private int mVerticalMenuBackgroundRes = R.drawable.shape_default_menu;

    private int mMaxMenuItemCount = 4;

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
        invalidateView();

        recyclerView = new RecyclerView(mContext, attrs, defStyleAttr);
        mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(mLayoutManagerOrientation);
        recyclerView.setLayoutManager(mLayoutManager);

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
            invalidateView();
        }
    }
    
    private void invalidateView(){
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
        }
    }

    private void invalidateData(){
        try {
            mMenus.clear();
            parseJson();
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

    public String getConfigJson() {
        return mConfigJson;
    }

    public void setConfigJson(String configJson) {
        this.mConfigJson = configJson;
    }

    public void setOnMenuClickListener(OnMenuClickListener listener){
        this.mOnMenuClickListener = listener;
    }

    public int getLayoutManagerOrientation() {
        return mLayoutManagerOrientation;
    }

    public void setLayoutManagerOrientation(int layoutManagerOrientation) {
        this.mLayoutManagerOrientation = layoutManagerOrientation;
    }

    public List<MenuBean> getMenus() {
        return mMenus;
    }

    public void setMenus(List<MenuBean> mMenus) {
        this.mMenus = mMenus;
    }

    public float getChildMenuItemHeight() {
        return mChildMenuItemHeight;
    }

    public void setChildMenuItemHeight(float childMenuItemHeight) {
        this.mChildMenuItemHeight = childMenuItemHeight;
    }

    public float getLevel1MenuItemHeight() {
        return mLevel1MenuItemHeight;
    }

    public void setLevel1MenuItemHeight(float level1MenuItemHeight) {
        this.mLevel1MenuItemHeight = level1MenuItemHeight;
    }

    public int getLevel2MenuAnimStyle() {
        return mLevel2MenuAnimStyle;
    }

    public void setLevel2MenuAnimStyle(int level2MenuAnimStyle) {
        this.mLevel2MenuAnimStyle = level2MenuAnimStyle;
    }

    public boolean isWithLevel1MenuWidth() {
        return isWithLevel1MenuWidth;
    }

    public void setWithLevel1MenuWidth(boolean withLevel1MenuWidth) {
        isWithLevel1MenuWidth = withLevel1MenuWidth;
    }

    public float getMenuTextPaddingBottom() {
        return mMenuTextPaddingBottom;
    }

    public void setMenuTextPaddingBottom(float paddingBottom) {
        this.mMenuTextPaddingBottom = paddingBottom;
        invalidateView();
    }

    public float getMenuTextPaddingTop() {
        return mMenuTextPaddingTop;
    }

    public void setMenuTextPaddingTop(float paddingTop) {
        this.mMenuTextPaddingTop = paddingTop;
        invalidateView();
    }

    public float getMenuTextPaddingRight() {
        return mMenuTextPaddingRight;
    }

    public void setMenuTextPaddingRight(float paddingRight) {
        this.mMenuTextPaddingRight = paddingRight;
        invalidateView();
    }

    public float getMenuTextPaddingLeft() {
        return mMenuTextPaddingLeft;
    }

    public void setMenuTextPaddingLeft(float paddingLeft) {
        this.mMenuTextPaddingLeft = paddingLeft;
        invalidateView();
    }

    public float getMenuDividerDp() {
        return mMenuDividerDp;
    }

    public void setMenuDividerDp(float menuDividerDp) {
        this.mMenuDividerDp = menuDividerDp;
        invalidateView();
    }

    public int getDividerColor() {
        return mDividerColor;
    }

    public void setDividerColor(int dividerColor) {
        this.mDividerColor = dividerColor;
        invalidateView();
    }

    public int getExpandableIcon() {
        return mExpandableIcon;
    }

    public void setExpandableIcon(int expandableIcon) {
        this.mExpandableIcon = expandableIcon;
        invalidateView();
    }

    public int getMenuTextColor() {
        return mMenuTextColor;
    }

    public void setMenuTextColor(int textColor) {
        this.mMenuTextColor = textColor;
        invalidateView();
    }

    public int getHorizontalMenuBackgroundRes() {
        return mHorizontalMenuBackgroundRes;
    }

    public void setHorizontalMenuBackgroundRes(int horizontalMenuBackgroundRes) {
        this.mHorizontalMenuBackgroundRes = horizontalMenuBackgroundRes;
        invalidateView();
    }

    public int getVerticalMenuBackgroundRes() {
        return mVerticalMenuBackgroundRes;
    }

    public void setVerticalMenuBackgroundRes(int verticalMenuBackgroundRes) {
        this.mVerticalMenuBackgroundRes = verticalMenuBackgroundRes;
        invalidateView();
    }

    public float getMenuTextSize() {
        return mMenuTextSize;
    }

    public void setMenuTextSize(float textSize) {
        this.mMenuTextSize = textSize;
        invalidateView();
    }

    public int getMaxMenuItemCount() {
        return mMaxMenuItemCount;
    }

    public void setMaxMenuItemCount(int maxMenuItemCount) {
        this.mMaxMenuItemCount = maxMenuItemCount;
    }

}
