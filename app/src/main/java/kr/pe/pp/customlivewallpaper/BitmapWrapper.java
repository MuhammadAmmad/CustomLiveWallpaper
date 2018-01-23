package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by Administrator on 2018-01-23.
 */

public class BitmapWrapper {
    private Bitmap bitmap = null;
    private int leftBase = 0;
    private int topBase = 0;

    public BitmapWrapper(Context context, Bitmap bitmap) {
        Util.Size screenSize = Util.getScreenSize(context);
        this.bitmap = bitmap;
        this.leftBase = (screenSize.getWidth() - bitmap.getWidth()) / 2;
        this.topBase = (screenSize.getHeight() - bitmap.getHeight()) / 2;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getLeftBase() {
        return leftBase;
    }

    public int getTopBase() {
        return topBase;
    }
}
