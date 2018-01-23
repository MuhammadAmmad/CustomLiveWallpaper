package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018-01-23.
 */

public class BackgroundSwitcher {
    public static int margin = 100;

    SwitchMode switchMode = null;
    SwitchMode currentSwitchMode = null;
    BitmapHolder bitmapHolder = null;

    Util.Size screenSize = null;
    Context context = null;

    Timer timerSwitcher = null;
    TimerTask timerTaskSwitcher = null;

    boolean isSwitching = false;
    int switchingDelay = 7000;
    int switchingAlpha = 0;
    int switchingSpeed = 5;
    int switchingCurrentX = 0;
    int switchingNextX = 0;
    int switchingStep = 0;

    public BackgroundSwitcher(SwitchMode switchMode) {
        this.switchMode = switchMode;
        this.currentSwitchMode = switchMode;
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
        timerSwitcher.schedule(timerTaskSwitcher, 0, switchingDelay);
    }

    public void deactive() {
        timerSwitcher.cancel();
    }

    public void draw(Canvas canvas, int x, int y) {
        if(bitmapHolder.getCurrentBitmap() != null) {
            BitmapWrapper currentWrapper = bitmapHolder.getCurrentBitmap();
            Bitmap currentBitmap = currentWrapper.getBitmap();

            if(isSwitching && bitmapHolder.getNextBitmap() != null) {
                BitmapWrapper nextWrapper = bitmapHolder.getNextBitmap();
                Bitmap nextBitmap = nextWrapper.getBitmap();

                if(currentSwitchMode == SwitchMode.SWITCH_FADE) {
                    switchingAlpha += switchingSpeed;
                    Paint nextPaint = new Paint();
                    nextPaint.setAlpha(switchingAlpha);

                    canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x, currentWrapper.getTopBase() - y, null);
                    canvas.drawBitmap(nextBitmap, nextWrapper.getLeftBase() - x, nextWrapper.getTopBase() - y, nextPaint);

                    if (switchingAlpha + switchingSpeed >= 255) {
                        isSwitching = false;
                        bitmapHolder.next();
                    }
                } else if(currentSwitchMode == SwitchMode.SWITCH_SLIDE_NORMAL) {
                    switchingCurrentX -= switchingStep;
                    switchingNextX -= switchingStep;

                    canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x + switchingCurrentX, currentWrapper.getTopBase() - y, null);
                    canvas.drawBitmap(nextBitmap, nextWrapper.getLeftBase() - x + switchingNextX, nextWrapper.getTopBase() - y, null);

                    if(switchingNextX - switchingStep <= 0) {
                        isSwitching = false;
                        bitmapHolder.next();
                    }
                } else if(currentSwitchMode == SwitchMode.SWITCH_SLIDE_OVERLAY) {
                    switchingCurrentX -= (switchingStep / 2);
                    switchingNextX -= switchingStep;

                    canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x + switchingCurrentX, currentWrapper.getTopBase() - y, null);
                    canvas.drawBitmap(nextBitmap, nextWrapper.getLeftBase() - x + switchingNextX, nextWrapper.getTopBase() - y, null);

                    if(switchingNextX - switchingStep <= 0) {
                        isSwitching = false;
                        bitmapHolder.next();
                    }
                }
            } else {
                canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x, currentWrapper.getTopBase() - y, null);
            }
        }
    }

    private class SwitcherTimerTask extends TimerTask {
        @Override
        public void run() {
            if(!isSwitching && bitmapHolder.isLoadComplete()) {
                if(switchMode == SwitchMode.SWITCH_RANDOM) {
                    currentSwitchMode = SwitchMode.values()[(int)(Math.random() * SwitchMode.SWITCH_RANDOM.getValue())];
                }

                isSwitching = true;
                switchingAlpha = 0;
                switchingCurrentX = 0;
                switchingNextX = screenSize.getWidth();
                switchingStep = screenSize.getWidth() / 100 * switchingSpeed;
            }
        }
    }

    public enum SwitchMode {
        SWITCH_SLIDE_NORMAL(0),
        SWITCH_SLIDE_OVERLAY(1),
        SWITCH_FADE(2),
        SWITCH_RANDOM(3);

        private final int value;
        private SwitchMode(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }
}
