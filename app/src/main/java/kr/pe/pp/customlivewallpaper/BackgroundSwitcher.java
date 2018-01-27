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

    boolean timerIsStarted = false;
    int timerTick = 0;
    int timerFinishTick = 0;
    Runnable timerTaskSwitcher = null;

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
        init(context, this.switchMode);
    }
    public void init(Context context, SwitchMode switchMode) {
        this.switchingSpeed = ApplicationData.getSlideSpeed() + 1;
        this.switchingDelay = (ApplicationData.getSlideDelay() + 3) * 1000;
        this.switchMode = switchMode;
        this.currentSwitchMode = switchMode;
        this.context = context;
        screenSize = Util.getScreenSize(context);
        bitmapHolder.init(context);
    }

    public void destroy() {
        bitmapHolder.destroy();
    }

    public void active() {
        if(ApplicationData.getIsEnableSlide()) {
            timerTaskSwitcher = new BackgroundSwitcher.SwitcherTimerTask();
            timerIsStarted = true;
            timerTick = 0;
            timerFinishTick = (int)(switchingDelay / 1000.0f * 60.0f);
        }
    }

    public void deactive() {
        timerIsStarted = false;
        timerTick = 0;
        timerFinishTick = (int)(switchingDelay / 1000.0f * 60.0f);
    }

    private void timerCheck() {
        if(timerIsStarted) {
            timerTick ++;
            if(timerTick >= timerFinishTick) {
                timerTick = 0;
                timerTaskSwitcher.run();
            }
        }
    }

    public void update() {
        timerCheck();
    }

    public void draw(Canvas canvas, int x, int y) {
        if(bitmapHolder.getCurrentBitmap() != null) {
            BitmapWrapper currentWrapper = bitmapHolder.getCurrentBitmap();
            Bitmap currentBitmap = currentWrapper.getBitmap();

            if(isSwitching && bitmapHolder.getNextBitmap() != null) {
                BitmapWrapper nextWrapper = bitmapHolder.getNextBitmap();
                Bitmap nextBitmap = nextWrapper.getBitmap();

                switch(currentSwitchMode) {
                    case Slide: {
                        switchingCurrentX -= switchingStep;
                        switchingNextX -= switchingStep;

                        canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x + switchingCurrentX, currentWrapper.getTopBase() - y, null);
                        canvas.drawBitmap(nextBitmap, nextWrapper.getLeftBase() - x + switchingNextX, nextWrapper.getTopBase() - y, null);

                        if(switchingNextX - switchingStep <= 0) {
                            isSwitching = false;
                            bitmapHolder.next();
                        }
                        break;
                    }
                    case Cover: {
                        switchingCurrentX -= (switchingStep / 2);
                        switchingNextX -= switchingStep;

                        canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x + switchingCurrentX, currentWrapper.getTopBase() - y, null);
                        canvas.drawBitmap(nextBitmap, nextWrapper.getLeftBase() - x + switchingNextX, nextWrapper.getTopBase() - y, null);

                        if(switchingNextX - switchingStep <= 0) {
                            isSwitching = false;
                            bitmapHolder.next();
                        }
                        break;
                    }
                    case Withdraw: {
                        switchingCurrentX -= switchingStep;
                        if(switchingNextX > 0) {
                            switchingNextX -= (switchingStep / 2);
                        } else {
                            switchingNextX = 0;
                        }

                        canvas.drawBitmap(nextBitmap, nextWrapper.getLeftBase() - x + switchingNextX, nextWrapper.getTopBase() - y, null);
                        canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x + switchingCurrentX, currentWrapper.getTopBase() - y, null);

                        if(switchingCurrentX <= -currentBitmap.getWidth()) {
                            isSwitching = false;
                            bitmapHolder.next();
                        }
                        break;
                    }
                    case Wipe: {
                        canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x, currentWrapper.getTopBase() - y, null);
                        isSwitching = false;
                        bitmapHolder.next();
                        break;
                    }
                    case Fade: {
                        switchingAlpha += switchingSpeed;
                        Paint nextPaint = new Paint();
                        nextPaint.setAlpha(switchingAlpha);

                        canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x, currentWrapper.getTopBase() - y, null);
                        canvas.drawBitmap(nextBitmap, nextWrapper.getLeftBase() - x, nextWrapper.getTopBase() - y, nextPaint);

                        if (switchingAlpha + switchingSpeed >= 255) {
                            isSwitching = false;
                            bitmapHolder.next();
                        }
                        break;
                    }
                    case BoxIn: {
                        canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x, currentWrapper.getTopBase() - y, null);
                        isSwitching = false;
                        bitmapHolder.next();
                        break;
                    }
                    case BoxOut: {
                        canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x, currentWrapper.getTopBase() - y, null);
                        isSwitching = false;
                        bitmapHolder.next();
                        break;
                    }
                    case CircleIn: {
                        canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x, currentWrapper.getTopBase() - y, null);
                        isSwitching = false;
                        bitmapHolder.next();
                        break;
                    }
                    case CircleOut: {
                        canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x, currentWrapper.getTopBase() - y, null);
                        isSwitching = false;
                        bitmapHolder.next();
                        break;
                    }
                    case SplitIn: {
                        canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x, currentWrapper.getTopBase() - y, null);
                        isSwitching = false;
                        bitmapHolder.next();
                        break;
                    }
                    case SplitOut: {
                        canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x, currentWrapper.getTopBase() - y, null);
                        isSwitching = false;
                        bitmapHolder.next();
                        break;
                    }
                }
            } else {
                canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x, currentWrapper.getTopBase() - y, null);
            }
        }
    }

    private class SwitcherTimerTask implements Runnable {
        @Override
        public void run() {
            if(!isSwitching && bitmapHolder.isLoadComplete()) {
                if(switchMode == SwitchMode.Random) {
                    currentSwitchMode = SwitchMode.values()[(int)(Math.random() * (SwitchMode.SplitOut.getValue()+1))];
                }

                isSwitching = true;
                switchingStep = screenSize.getWidth() / 100 * switchingSpeed;

                switch(currentSwitchMode) {
                    case Slide: {
                        switchingCurrentX = 0;
                        switchingNextX = screenSize.getWidth();
                        break;
                    }
                    case Cover: {
                        switchingCurrentX = 0;
                        switchingNextX = screenSize.getWidth();
                        break;
                    }
                    case Withdraw: {
                        switchingCurrentX = 0;
                        switchingNextX = screenSize.getWidth() / 2;
                        break;
                    }
                    case Wipe: {
                        switchingCurrentX = 0;
                        switchingNextX = screenSize.getWidth();
                        break;
                    }
                    case Fade: {
                        switchingAlpha = 0;
                        break;
                    }
                    case BoxIn: {
                        switchingCurrentX = 0;
                        switchingNextX = screenSize.getWidth();
                        break;
                    }
                    case BoxOut: {
                        switchingCurrentX = 0;
                        switchingNextX = screenSize.getWidth();
                        break;
                    }
                    case CircleIn: {
                        switchingCurrentX = 0;
                        switchingNextX = screenSize.getWidth();
                        break;
                    }
                    case CircleOut: {
                        switchingCurrentX = 0;
                        switchingNextX = screenSize.getWidth();
                        break;
                    }
                    case SplitIn: {
                        switchingCurrentX = 0;
                        switchingNextX = screenSize.getWidth();
                        break;
                    }
                    case SplitOut: {
                        switchingCurrentX = 0;
                        switchingNextX = screenSize.getWidth();
                        break;
                    }
                }

            }
        }
    }

    public enum SwitchMode {
        Slide(0),
        Cover(1),
        Withdraw(2),
        Wipe(3),
        Fade(4),
        BoxIn(5),
        BoxOut(6),
        CircleIn(7),
        CircleOut(8),
        SplitIn(9),
        SplitOut(10),
        Random(99);

        private final int value;
        private SwitchMode(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }
}
