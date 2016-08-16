package co.lujun.popmenulayout;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
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

    private int mLayoutManagerOrientation = LinearLayoutManager.HORIZONTAL;

    private int mWidth, mHeight;

    private boolean[] mMenuShow;

    private static final int SUPPORT_MENU_LEVEL = 3;

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
        mMenuShow = new boolean[]{true, false, false};
        mContext = context;
        mMenus = new ArrayList<MenuBean>();
        m1LevelMenuAdapter = new MenuAdapter(mMenus);
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
        recyclerView.setAdapter(m1LevelMenuAdapter);

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
        this.mWidth = w;
        this.mHeight = h;
    }

    private void parseJson() throws JSONException{
        if (TextUtils.isEmpty(mConfigJson)){
            return;
        }
        JSONObject object = new JSONObject(mConfigJson);
        JSONArray array = object.getJSONArray("menus");
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            mMenus.add(parseChild(obj));
        }
    }

    private MenuBean parseChild(JSONObject object) throws JSONException{
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
                    menus.add(parseChild(obj));
                }
                menu.setChild(menus);
            }
        }
        return menu;
    }

    private void dealMenuClickEvent(int level1Index, int level2Index, int level3Index){
        MenuBean menu1 = mMenus.get(level1Index);
        if (menu1.isExpandable() && isChildMenuShow()){
            // dismiss
            hideAllChildMenu();
        }else if (menu1.isExpandable() && !isChildMenuShow()){
            if (!mMenuShow[1] || !mMenuShow[2]){
                // show level 2 menu or level 3 menu
                expandMenu(level1Index, level2Index, level3Index);
            }else {
                // dismiss
                hideAllChildMenu();
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
        // TODO finish expand logic
        PopMenuView popMenuView = new PopMenuView(mContext);
        popMenuView.showAsDropDown(recyclerView, mWidth / mMenus.size(), 0);
        popMenuView.setMenus(null);
    }

    private void hideAllChildMenu(){
        // TODO hide all PopMenuView
    }

    private boolean isChildMenuShow(){
        return mMenuShow[1] || mMenuShow[2];
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

    public void setmOnMenuClickListener(OnMenuClickListener listener){
        this.mOnMenuClickListener = listener;
    }

    public int getLayoutManagerOrientation() {
        return mLayoutManagerOrientation;
    }

    public void setLayoutManagerOrientation(int mLayoutManagerOrientation) {
        this.mLayoutManagerOrientation = mLayoutManagerOrientation;
    }

    public float dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
