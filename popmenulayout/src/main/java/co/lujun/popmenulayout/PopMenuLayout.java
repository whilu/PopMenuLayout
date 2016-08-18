package co.lujun.popmenulayout;

import android.content.Context;
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

    private float level1MenuItemHeight = 50.0f;

    private float mChildMenuItemHeight = 50.0f;

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
        mMenuShow = new boolean[SUPPORT_MENU_LEVEL];
        for (int i = 0; i < SUPPORT_MENU_LEVEL; i++) {
            mMenuShow[i] = i == 0;
        }
        mContext = context;
        mMenus = new ArrayList<MenuBean>();
        m1LevelMenuAdapter = new MenuAdapter(mContext, mMenus, mLayoutManagerOrientation);
        m1LevelMenuAdapter.setOnMenuClickListener(new OnMenuClickListener() {
            @Override
            public void onMenuClick(int level1Index, int level2Index, int level3Index) {
                dealMenuClickEvent(level1Index, level2Index, level3Index);
            }
        });

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
        popMenuView.setAnimationStyle(mLevel2MenuAnimStyle);
        popMenuView.setMenus(menus);
        popMenuView.showAtLocation(recyclerView, Gravity.NO_GRAVITY,
                mWidth / mMenus.size() * level1Index +
                        (mWidth / mMenus.size() - popMenuView.getWidth()) / 2,
                location[1] - menus.size() * recyclerView.getHeight());
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
        m1LevelMenuAdapter.setMenuWidth(mWidth / SUPPORT_MENU_LEVEL);
        recyclerView.setAdapter(m1LevelMenuAdapter);

        if (popMenuView == null){
            popMenuView = new PopMenuView(mContext, this, mWidth / SUPPORT_MENU_LEVEL, -1);
            popMenuView.setOnMenuClickListener(new OnMenuClickListener() {
                @Override
                public void onMenuClick(int level1Index, int level2Index, int level3Index) {
                    if (mOnMenuClickListener != null){
                        mOnMenuClickListener.onMenuClick(level1Index, level2Index, level3Index);
                    }
                }
            });
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
        try {
            mMenus.clear();
            parseJson();
            m1LevelMenuAdapter.notifyDataSetChanged();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void setOnMenuClickListener(OnMenuClickListener listener){
        this.mOnMenuClickListener = listener;
    }

    public int getLayoutManagerOrientation() {
        return mLayoutManagerOrientation;
    }

    public void setLayoutManagerOrientation(int mLayoutManagerOrientation) {
        this.mLayoutManagerOrientation = mLayoutManagerOrientation;
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

    public void setChildMenuItemHeight(float mChildMenuItemHeight) {
        this.mChildMenuItemHeight = mChildMenuItemHeight;
    }

    public float getLevel1MenuItemHeight() {
        return level1MenuItemHeight;
    }

    public void setLevel1MenuItemHeight(float level1MenuItemHeight) {
        this.level1MenuItemHeight = level1MenuItemHeight;
    }

    public int getLevel2MenuAnimStyle() {
        return mLevel2MenuAnimStyle;
    }

    public void setLevel2MenuAnimStyle(int mLevel2MenuAnimStyle) {
        this.mLevel2MenuAnimStyle = mLevel2MenuAnimStyle;
    }

}
