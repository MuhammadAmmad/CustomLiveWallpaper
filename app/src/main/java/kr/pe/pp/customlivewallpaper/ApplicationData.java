package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Administrator on 2018-01-22.
 */

public class ApplicationData {
    private static ArrayList<String> _ImagePathList = new ArrayList<>();

    public static ArrayList<String> getImagePathList() {
        return _ImagePathList;
    }

    public static void LoadImagePathList(Context context) {
        _ImagePathList.clear();
        SharedPreferences pref = context.getSharedPreferences("ImagePathList", context.MODE_PRIVATE);
        Map<String, ?> map = pref.getAll();
        TreeMap<String, Object> sortedMap = new TreeMap<String, Object>(map);
        Iterator<String> keys = sortedMap.keySet().iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            String path = (String)sortedMap.get(key);
            Log.d("__Debug__", "Load - [" + key + "] = " + path);
            _ImagePathList.add(path);
        }
    }

    public static void SaveImagePathList(Context context) {
        SharedPreferences pref = context.getSharedPreferences("ImagePathList", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        for(int i=0; i<_ImagePathList.size(); i++) {
            editor.putString(String.format("%05d", i), _ImagePathList.get(i));
            Log.d("__Debug__", "Save - " + String.format("%05d", i) + ":" + _ImagePathList.get(i));
        }
        editor.commit();
    }

    public static void Load(Context context) {
        LoadImagePathList(context);
    }

    public static void Save(Context context) {
        SaveImagePathList(context);
    }
}
