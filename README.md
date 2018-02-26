# PopMenuLayout

[![Build Status](https://travis-ci.org/whilu/PopMenuLayout.svg)](https://travis-ci.org/whilu/PopMenuLayout) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-PopMenuLayout-green.svg?style=true)](https://android-arsenal.com/details/1/4219)

A multi level menu view(like WeChat subscription Accounts) library for Android.

## Screenshots

<img src="/screenshots/device-2016-08-22-232610.png" alt="screenshots/device-2016-08-22-232610.png" width="270" height="486" /> <img src="/screenshots/device-2016-08-22-232719.png" alt="screenshots/device-2016-08-22-232719.png" width="270" height="486" /> <img src="/screenshots/device-2016-08-22-232953.png" alt="screenshots/device-2016-08-22-232953.png" width="270" height="486" /> 

## Usage

### Step 1

Add below dependency in your **build.gradle** file.

```groovy
dependencies {
    compile 'co.lujun:popmenulayout:0.9.3'
}
```

### Step 2

Use the PopMenuLayout in layout file, you can add customized attributes here.

```xml
<co.lujun.popmenulayout.PopMenuLayout
    android:id="@+id/popMenuLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

or using with custom attributes:

```xml
<co.lujun.popmenulayout.PopMenuLayout
    android:id="@+id/popMenuLayout"
    android:layout_width="match_parent"
    app:level2_menu_anim_style="@style/PopAnimation"
    android:layout_height="match_parent"
    app:level1_menu_item_height="100dp"
    app:child_menu_item_height="70dp"
    app:divider_margin_left="10dp"
    app:divider_margin_right="5dp"
    app:divider_margin_top="10dp"
    app:divider_margin_bottom="4dp"
    app:cmenu_layout_bg_color="#0000ff"
    app:level1_menu_layout_bg_color="#ff0000"
    app:cmenu_w_follow_level1_menu="false"
    app:menu_divider_color="#ff0000"
    app:menu_expandable_icon="@mipmap/ic_launcher"
    app:menu_text_color="#00ff00"
    app:horizontal_menu_bg="@mipmap/ic_launcher"
    app:vertical_menu_bg="@mipmap/ic_launcher"
    app:menu_text_size="8"
    app:menu_divider_width="4dp"
    app:menu_left_padding="0dp"
    app:menu_right_padding="0dp"
    app:menu_top_padding="0dp"
    app:menu_bottom_padding="0dp"
    app:child_menu_max_count="5"/>
```

### Step 3

Use this library in your code.

**Ensure correct json format, like [this](https://github.com/whilu/PopMenuLayout/blob/master/popmenulayout/src/test/java/co/lujun/popmenulayout/menu_config.json), make sure the keys are correct:**
```json
{
  "menus" : [
    {
      "index" : "0",
      "expandable" : true,
      "text" : "Menu 0",
      "child" : []
    }
  ]
}
```

then make menus:

```java
String confJson = "your config json string...";
PopMenuLayout popMenuLayout = (PopMenuLayout) findViewById(R.id.popMenuLayout);

popMenuLayout.setConfigJson(confJson);
// you can also make menus with MenuBean, like this:
// MenuBean menu0 = new MenuBean();
// menu0.setIndex("0");
// menu0.setExpandable(false);
// menu0.setText("menu0");
// MenuBean menu1 = new MenuBean();
// menu1.setIndex("1");
// menu1.setExpandable(false);
// menu1.setText("menu1");

// List<MenuBean> menus = new ArrayList<MenuBean>();
// menus.add(menu0);
// menus.add(menu1);
// popMenuLayout.setMenus(menus);

// Add callback for menu click
popMenuLayout.setOnMenuClickListener(new OnMenuClickListener() {
    @Override
    public void onMenuClick(int level1Index, int level2Index, int level3Index) {
        // deal the menu click callback...
        // If you click a menu and the menu's index is '121', 
        // in this callback, level1Index = 1, level2Index = 2, level3Index = 1
        
    }
});
```

## Attributes

|name|format|description|
|:---:|:---:|:---:|
| config_json | string | Config json string to make menus
| level2_menu_anim_style | reference | Animation style to use when the child popup menu appears and disappears.
| level1_menu_item_height | dimension | Level 1 menu height, default 50(dp).
| child_menu_item_height | dimension | Child popup menu height, default 50(dp).
| cmenu_w_follow_level1_menu | boolean | Whether the child popup menu's width always equals to level 1 menu's width.
| menu_divider_color | color | The divider color between menus, default Color.GRAY.
| menu_expandable_icon | reference | The icon for expandable menu.
| menu_text_color | color | The text color for menu, default Color.BLACK.
| horizontal_menu_bg | reference | The background resource for Level 1 menu.
| vertical_menu_bg | reference | The background resource for child menus.
| menu_text_size | dimension | The text size for menu's content text, default 14(sp).
| menu_divider_width | dimension | The divider width(height) between menus, default 1(dp).
| menu_left_padding | dimension | The left padding for menu's content text, default 10(dp).
| menu_right_padding | dimension | The right padding for menu's content text, default 10(dp).
| menu_top_padding | dimension | The top padding for menu's content text, default 5(dp).
| menu_bottom_padding | dimension | The bottom padding for menu's content text, default 5(dp).
| child_menu_max_count | integer | The max size for child menu item, default 4.
| level1_menu_layout_bg_color | color | The level 1 menu's container layout background color(default white).
| cmenu_layout_bg_color | color | The child menu's container layout background color(default white).
| divider_margin_left | dimension | Divider margin left, default 0(dp).
| divider_margin_right | dimension | Divider margin right, default 0(dp).
| divider_margin_top | dimension | Divider margin top, default 0(dp).
| divider_margin_bottom | dimension | Divider margin bottom, default 0(dp).

**You can set these attributes in layout file, or use setters(each attribute has get and set method) to set them.**

## Change logs
### 0.9.3(2016-12-1)
- issue [#3](https://github.com/whilu/PopMenuLayout/issues/3)
- fix bugs

### 0.9.2(2016-8-25)
- issue [#2](https://github.com/whilu/PopMenuLayout/issues/2)
- fix bugs

### 0.9.1(2016-8-23)
- Alpha version(0.9.1)

## About
If you have any questions, contact me: [lujun.byte#gmail.com](mailto:lujun.byte@gmail.com).

## License

[MIT](LICENSE)
