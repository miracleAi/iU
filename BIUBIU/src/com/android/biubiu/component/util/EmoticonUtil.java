package com.android.biubiu.component.util;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * 表情工具
 */
public class EmoticonUtil {
    public static String[] emoticonData;

    public static void init(Context context) {
        if (emoticonData == null) {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open("EmojiList.json"), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                inputStreamReader.close();
                JSONArray jsonArray = new JSONArray(stringBuilder.toString());
                int length = jsonArray.length();
                emoticonData = new String[length];
                for (int i = 0; i < length; i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    emoticonData[i] = object.getString("d");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
