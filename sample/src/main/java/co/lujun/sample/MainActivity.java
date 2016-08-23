package co.lujun.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.lujun.popmenulayout.MenuBean;
import co.lujun.popmenulayout.OnMenuClickListener;
import co.lujun.popmenulayout.PopMenuLayout;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView) findViewById(R.id.text);

        String confJson = "{" +
                "  \"menus\" : [" +
                "    {" +
                "      \"index\" : \"0\"," +
                "      \"expandable\" : false," +
                "      \"text\" : \"Menu 0\"" +
                "    }," +
                "    {" +
                "      \"index\" : \"1\"," +
                "      \"expandable\" : true," +
                "      \"text\" : \"Menu 1\"," +
                "      \"child\" : [" +
                "        {" +
                "          \"index\" : \"10\"," +
                "          \"expandable\" : false," +
                "          \"text\" : \"Child Menu 10\"" +
                "        }," +
                "        {" +
                "          \"index\" : \"11\"," +
                "          \"expandable\" : false," +
                "          \"text\" : \"Child Menu 11\"" +
                "        }," +
                "        {" +
                "          \"index\" : \"12\"," +
                "          \"expandable\" : true," +
                "          \"text\" : \"Child Menu 12\"," +
                "          \"child\" : [" +
                "            {" +
                "              \"index\" : \"120\"," +
                "              \"expandable\" : false," +
                "              \"text\" : \"Child Menu 120\"" +
                "            }," +
                "            {" +
                "              \"index\" : \"121\"," +
                "              \"expandable\" : false," +
                "              \"text\" : \"Child Menu 121\"" +
                "            }" +
                "          ]" +
                "        }" +
                "      ]" +
                "    }," +
                "    {" +
                "      \"index\" : \"2\"," +
                "      \"expandable\" : true," +
                "      \"text\" : \"Menu 2\"," +
                "      \"child\" : [" +
                "        {" +
                "          \"index\" : \"20\"," +
                "          \"expandable\" : false," +
                "          \"text\" : \"Child Menu 20\"" +
                "        }," +
                "        {" +
                "          \"index\" : \"21\"," +
                "          \"expandable\" : true," +
                "          \"text\" : \"Child Menu 21\"," +
                "          \"child\" : [" +
                "            {" +
                "              \"index\" : \"210\"," +
                "              \"expandable\" : false," +
                "              \"text\" : \"Child Menu 210\"" +
                "            }," +
                "            {" +
                "              \"index\" : \"211\"," +
                "              \"expandable\" : false," +
                "              \"text\" : \"Child Menu 211\"" +
                "            }" +
                "          ]" +
                "        }" +
                "      ]" +
                "    }" +
                "  ]" +
                "}";

        PopMenuLayout popMenuLayout = (PopMenuLayout) findViewById(R.id.popMenuLayout);
        popMenuLayout.setConfigJson(confJson);

        // make menus with MenuBean
//        MenuBean menu0 = new MenuBean();
//        menu0.setIndex("0");
//        menu0.setExpandable(false);
//        menu0.setText("menu0");
//
//        MenuBean menu1 = new MenuBean();
//        menu1.setIndex("1");
//        menu1.setExpandable(false);
//        menu1.setText("menu1");
//
//        List<MenuBean> menus = new ArrayList<MenuBean>();
//        menus.add(menu0);
//        menus.add(menu1);
//        popMenuLayout.setMenus(menus);

        popMenuLayout.setLevel2MenuAnimStyle(R.style.PopAnimation);
        popMenuLayout.setOnMenuClickListener(new OnMenuClickListener() {
            @Override
            public void onMenuClick(int level1Index, int level2Index, int level3Index) {
                textView.setText("Click menu index:" +
                        "\nlevel1 index = " + level1Index + "\nlevel2 index = " +
                        level2Index + "\nlevel3 index = " + level3Index);
            }
        });
    }
}
