package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018-01-23.
 */

public class BackgroundSwitcher {
    public static int margin = 100;

    SwitchMode switchMode = null;
    BitmapHolder bitmapHolder = null;

    Util.Size screenSize = null;
    Context context = null;

    Timer timerSwitcher = null;
    TimerTask timerTaskSwitcher = null;

    public BackgroundSwitcher(SwitchMode switchMode) {
        this.switchMode = switchMode;
        this.bitmapHolder = new BitmapHolder(BitmapHolder.BitmapHolderMixMode.MIXMODE_SEQUENTIAL, margin);
    }

    public void init(Context context) {
        this.context = context;
        screenSize = Util.getScreenSize(context);
        bitmapHolder.init(context);
    }

    public void destroy() {
        bitmapHolder.destroy();
    }

    public void active() {
        timerTaskSwitcher = new BackgroundSwitcher.SwitcherTimerTask();
        timerSwitcher = new Timer();
        timerSwitcher.schedule(timerTaskSwitcher, 0, 5000);
    }

    public void deactive() {
        timerSwitcher.cancel();
    }

    public void draw(Canvas canvas, int x, int y) {
        bitmapHolder.applyChanges();

        if(bitmapHolder.getCurrentBitmap() != null) {
            BitmapWrapper wrapper = bitmapHolder.getCurrentBitmap();
            Bitmap bmp = wrapper.getBitmap();
            canvas.drawBitmap(bmp, wrapper.getLeftBase() - x, wrapper.getTopBase() - y, null);
        }
    }

    private class SwitcherTimerTask extends TimerTask {
        @Override
        public void run() {
            bitmapHolder.next();
        }
    }

    public enum SwitchMode {
        SWITCH_SLIDE_NORMAL,
        SWITCH_SLIDE_OVERLAY,
        SWITCH_FADE
    }
}
