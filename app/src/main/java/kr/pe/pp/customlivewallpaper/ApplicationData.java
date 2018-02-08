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
    public enum MoveDirection {
        LEFT_DOWN(0),
        DOWN(1),
        RIGHT_DOWN(2);

        private final int value;
        private MoveDirection(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    private static ArrayList<SaveImage> _ImagePathList = new ArrayList<>();

    private static boolean isEnableSlide = true;
    private static String slideType = "";
    private static int slideSpeed = 5;
    private static int slideDelay = 5;

    private static boolean isEnableEffect = false;
    private static String effectParticleType = "";
    private static int effectDensity = 5;
    private static boolean effectIsUseRotate = false;
    private static boolean effectIsRotateRight = true;
    private static int effectRotateSpeed = 5;
    private static MoveDirection effectMoveDirection = MoveDirection.DOWN;
    private static int effectMoveSpeed = 5;
    private static int effectMoveVibrate = 3;
    private static int effectSize = 5;
    private static int effectSizeVibrate = 0;

    public static ArrayList<SaveImage> getImagePathList() {
        return _ImagePathList;
    }

    public static void LoadImagePathList(Context context) {
        _ImagePathList.clear();
        SharedPreferences pref = context.getSharedPreferences("ImagePathList", context.MODE_PRIVATE);
        Map<String, ?> map = pref.getAll();
        Log.d("__Debug__", "ImagePathList count : " + map.keySet().size());
        TreeMap<String, Object> sortedMap = new TreeMap<String, Object>(map);
        Iterator<String> keys = sortedMap.keySet().iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            String value = (String)sortedMap.get(key);
            String[] pathAndRotate = value.split("\\?\\^\\^\\?");
            String path = "";
            String rotate = "";
            Integer rotateValue = 0;
            if(pathAndRotate.length > 0) path = pathAndRotate[0];
            if(pathAndRotate.length > 1) rotate = pathAndRotate[1];
            if(path == null || path.isEmpty()) continue;
            rotateValue = Integer.parseInt(rotate);

            Log.d("__Debug__", "Load - [" + key + "] = Path : " + path + ", Rotate : " + rotateValue);
            _ImagePathList.add(new SaveImage(path, rotateValue));
        }
    }

    public static void SaveImagePathList(Context context) {
        SharedPreferences pref = context.getSharedPreferences("ImagePathList", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        for(int i=0; i<_ImagePathList.size(); i++) {
            SaveImage saveImage = _ImagePathList.get(i);
            editor.putString(String.format("%05d", i), saveImage.getPath() + "?^^?" + saveImage.getRotate());
            Log.d("__Debug__", "Save - " + String.format("%05d", i) + ":Path(" + saveImage.getPath() + "), Rotate(" + saveImage.getRotate() + ")");
        }
        editor.commit();
    }

    public static boolean getIsEnableSlide() { return isEnableSlide; }
    public static void setIsEnableSlide(boolean v) { isEnableSlide = v; }
    public static String getSlideType() { return slideType; }
    public static void setSlideType(String v) { slideType = v; }
    public static int getSlideSpeed() { return slideSpeed; }
    public static void setSlideSpeed(int v) { slideSpeed = v; }
    public static int getSlideDelay() { return slideDelay; }
    public static void setSlideDelay(int v) { slideDelay = v; }

    public static boolean getIsEnableEffect() { return isEnableEffect; }
    public static void setIsEnableEffect(boolean v) { isEnableEffect = v; }
    public static int getEffectDensity() { return effectDensity; }
    public static void setEffectDensity(int v) { effectDensity = v; }
    public static String getEffectParticleType() { return effectParticleType; }
    public static void setEffectParticleType(String v) { effectParticleType = v; }
    public static boolean getEffectIsUseRotate() { return effectIsUseRotate; }
    public static void setEffectIsUseRotate(boolean v) { effectIsUseRotate = v; }
    public static boolean getEffectIsRotateRight() { return effectIsRotateRight; }
    public static void setEffectIsRotateRight(boolean v) { effectIsRotateRight = v; }
    public static int getEffectRotateSpeed() { return effectRotateSpeed; }
    public static void setEffectRotateSpeed(int v) { effectRotateSpeed = v; }
    public static MoveDirection getEffectMoveDirection() { return effectMoveDirection; }
    public static void setEffectMoveDirection(MoveDirection v) { effectMoveDirection = v; }
    public static int getEffectMoveSpeed() { return effectMoveSpeed; }
    public static void setEffectMoveSpeed(int v) { effectMoveSpeed = v; }
    public static int getEffectMoveVibrate() { return effectMoveVibrate; }
    public static void setEffectMoveVibrate(int v) { effectMoveVibrate = v; }
    public static int getEffectSize() { return effectSize; }
    public static void setEffectSize(int v) { effectSize = v; }
    public static int getEffectSizeVibrate() { return effectSizeVibrate; }
    public static void setEffectSizeVibrate(int v) { effectSizeVibrate = v; }

    public static void LoadEffects(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Effects", context.MODE_PRIVATE);

        isEnableSlide = pref.getBoolean("isEnableSlide", true);
        slideType = pref.getString("slideType", "SplitOut");;
        slideSpeed = pref.getInt("slideSpeed", 4);
        slideDelay = pref.getInt("slideDelay", 5);

        isEnableEffect = pref.getBoolean("isEnableEffect", true);
        effectParticleType = pref.getString("effectParticleType", "particle_bubble");
        effectDensity = pref.getInt("effectDensity", 5);
        effectIsUseRotate = pref.getBoolean("effectIsUseRotate", false);
        effectIsRotateRight = pref.getBoolean("effectIsRotateRight", true);
        effectRotateSpeed = pref.getInt("effectRotateSpeed", 5);
        effectMoveDirection = MoveDirection.values()[pref.getInt("effectMoveDirection", MoveDirection.DOWN.getValue())];
        effectMoveSpeed = pref.getInt("effectMoveSpeed", 3);
        effectMoveVibrate = pref.getInt("effectMoveVibrate", 3);
        effectSize = pref.getInt("effectSize", 5);
        effectSizeVibrate = pref.getInt("effectSizeVibrate", 0);
    }

    public static void SaveEffects(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Effects", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();

        editor.putBoolean("isEnableSlide", isEnableSlide);
        editor.putString("slideType", slideType);;
        editor.putInt("slideSpeed", slideSpeed);
        editor.putInt("slideDelay", slideDelay);

        editor.putBoolean("isEnableEffect", isEnableEffect);
        editor.putString("effectParticleType", effectParticleType);
        editor.putInt("effectDensity", effectDensity);
        editor.putBoolean("effectIsUseRotate", effectIsUseRotate);
        editor.putBoolean("effectIsRotateRight", effectIsRotateRight);
        editor.putInt("effectRotateSpeed", effectRotateSpeed);

        editor.putInt("effectMoveDirection", effectMoveDirection.getValue());
        editor.putInt("effectMoveSpeed", effectMoveSpeed);
        editor.putInt("effectMoveVibrate", effectMoveVibrate);
        editor.putInt("effectSize", effectSize);
        editor.putInt("effectSizeVibrate", effectSizeVibrate);

        editor.commit();
    }

    public static void Load(Context context) {
        LoadImagePathList(context);
        LoadEffects(context);
    }

    public static void Save(Context context) {
        SaveEffects(context);
        SaveImagePathList(context);
    }
}
