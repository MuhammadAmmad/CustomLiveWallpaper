package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Created by Administrator on 2018-01-23.
 */

public interface IDrawer {
    void init(Context context);
    void draw(Canvas canvas);
    void destroy();
    void active();
    void deactive();
}
