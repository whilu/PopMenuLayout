package co.lujun.popmenulayout.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import co.lujun.popmenulayout.MenuBean;
import co.lujun.popmenulayout.OnMenuClickListener;
import co.lujun.popmenulayout.R;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-8-16 16:27
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<MenuBean> mMenus;

    private OnMenuClickListener onMenuClickListener;

    public MenuAdapter(List<MenuBean> menus){
        this.mMenus = menus;
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
        final MenuBean menu = mMenus.get(position);
        holder.tvMenuText.setText(menu.getText());
        holder.ivMenuIcon.setVisibility(menu.isExpandable() ? View.VISIBLE : View.GONE);
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

    static class MenuViewHolder extends RecyclerView.ViewHolder{

        TextView tvMenuText;
        ImageView ivMenuIcon;

        public MenuViewHolder(View v){
            super(v);
            tvMenuText = (TextView) v.findViewById(R.id.tv_menu_text);
            ivMenuIcon = (ImageView) v.findViewById(R.id.iv_expandable_icon);
        }
    }
}
