package co.lujun.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import co.lujun.popmenulayout.OnMenuClickListener;
import co.lujun.popmenulayout.PopMenuLayout;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView) findViewById(R.id.text);

        String confJson = "{\n" +
                "  \"menus\" : [\n" +
                "    {\n" +
                "      \"index\" : \"0\",\n" +
                "      \"expandable\" : false,\n" +
                "      \"text\" : \"Menu 0\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"index\" : \"1\",\n" +
                "      \"expandable\" : true,\n" +
                "      \"text\" : \"Menu 1\",\n" +
                "      \"child\" : [\n" +
                "        {\n" +
                "          \"index\" : \"10\",\n" +
                "          \"expandable\" : false,\n" +
                "          \"text\" : \"Child Menu 10\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"index\" : \"11\",\n" +
                "          \"expandable\" : false,\n" +
                "          \"text\" : \"Child Menu 11\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"index\" : \"12\",\n" +
                "          \"expandable\" : true,\n" +
                "          \"text\" : \"Child Menu 12\",\n" +
                "          \"child\" : [\n" +
                "            {\n" +
                "              \"index\" : \"120\",\n" +
                "              \"expandable\" : false,\n" +
                "              \"text\" : \"Child Menu 120\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"index\" : \"121\",\n" +
                "              \"expandable\" : false,\n" +
                "              \"text\" : \"Child Menu 121\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"index\" : \"2\",\n" +
                "      \"expandable\" : true,\n" +
                "      \"text\" : \"Menu 2\",\n" +
                "      \"child\" : [\n" +
                "        {\n" +
                "          \"index\" : \"20\",\n" +
                "          \"expandable\" : false,\n" +
                "          \"text\" : \"Child Menu 20\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"index\" : \"21\",\n" +
                "          \"expandable\" : true,\n" +
                "          \"text\" : \"Child Menu 21\",\n" +
                "          \"child\" : [\n" +
                "            {\n" +
                "              \"index\" : \"210\",\n" +
                "              \"expandable\" : false,\n" +
                "              \"text\" : \"Child Menu 210\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"index\" : \"211\",\n" +
                "              \"expandable\" : false,\n" +
                "              \"text\" : \"Child Menu 211\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        PopMenuLayout popMenuLayout = (PopMenuLayout) findViewById(R.id.popMenuLayout);
        popMenuLayout.setConfigJson(confJson);
        popMenuLayout.setLevel2MenuAnimStyle(R.style.PopAnimation);
        popMenuLayout.setOnMenuClickListener(new OnMenuClickListener() {
            @Override
            public void onMenuClick(int level1Index, int level2Index, int level3Index) {
                textView.setText("Click menu index:" +
                        "\nlevel1Index = " + level1Index + "\nlevel2Index = " +
                        level2Index + "\nlevel3Index = " + level3Index);
                Log.d(TAG, "level1Index = " + level1Index + ", level2Index = " +
                        level2Index + ", level3Index = " + level3Index);
            }
        });
    }
}
