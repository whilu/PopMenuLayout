package co.lujun.popmenulayout;

import java.util.List;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-8-16 15:48
 */
public class MenuBean {

    private String index;
    private boolean expandable;
    private String text;
    private List<MenuBean> child;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<MenuBean> getChild() {
        return child;
    }

    public void setChild(List<MenuBean> child) {
        this.child = child;
    }

    // TODO 提供 json 与 MenuBean 之间互转
    public String toJsonString(){
        return null;
    }
}
