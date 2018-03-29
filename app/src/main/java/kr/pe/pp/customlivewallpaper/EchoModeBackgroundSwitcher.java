package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by Administrator on 2018-01-23.
 */

public class EchoModeBackgroundSwitcher {
    public interface EchoModeSwitcherEventListener {
        void onSwitchingAnimationComplete();
    }
    private EchoModeSwitcherEventListener switcherEventListener = null;
    public void setSwitcherEventListener(EchoModeSwitcherEventListener listener) {
        this.switcherEventListener = listener;
    }

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
    int switchingCurrentY = 0;
    int switchingCurrentW = 0;
    int switchingCurrentH = 0;
    float switchingCurrentScale = 1.0f;

    int switchingNextX = 0;
    int switchingNextY = 0;
    int switchingNextW = 0;
    int switchingNextH = 0;
    float switchingNextScale = 1.0f;

    Bitmap bitmapMask = null;
    Bitmap bitmapBuffer = null;
    Canvas canvasMask = null;
    Canvas canvasBuffer = null;

    int switchingStep = 0;

    public EchoModeBackgroundSwitcher(SwitchMode switchMode) {
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
        bitmapHolder.setBitmapHolderEventListener(new BitmapHolder.BitmapHolderEventListener() {
            @Override
            public void onLoadComplete(boolean isInit) {
                if(isInit) {
                    if(bitmapHolder.getCurrentBitmap() != null) {
                        bitmapMask = Bitmap.createBitmap(bitmapHolder.getCurrentBitmap().getBitmap().getWidth(), bitmapHolder.getCurrentBitmap().getBitmap().getHeight(), Bitmap.Config.ARGB_8888);
                        bitmapBuffer = Bitmap.createBitmap(bitmapHolder.getCurrentBitmap().getBitmap().getWidth(), bitmapHolder.getCurrentBitmap().getBitmap().getHeight(), Bitmap.Config.ARGB_8888);
                    }
                }
            }
        });
        bitmapHolder.init(context);
    }

    public void destroy() {
        if(bitmapBuffer != null) bitmapBuffer.recycle();
        bitmapBuffer = null;

        if(bitmapMask != null) bitmapMask.recycle();
        bitmapMask = null;

        bitmapHolder.destroy();
    }

    public void startSwitch() {
        timerIsStarted = false;
        timerTick = 0;
        timerTaskSwitcher = new EchoModeBackgroundSwitcher.SwitcherTimerTask();
        timerTaskSwitcher.run();
    }

    public void active(boolean isChangeSettings) {
        if(isChangeSettings) {
            bitmapHolder.active(isChangeSettings);

            this.switchingSpeed = ApplicationData.getSlideSpeed() + 1;
            this.switchingDelay = (ApplicationData.getSlideDelay() + 3) * 1000;
            try {
                switchMode = SwitchMode.valueOf(ApplicationData.getSlideType());
            } catch(IllegalArgumentException ex) { }
            if(switchMode == null) switchMode = SwitchMode.Cover;
            currentSwitchMode = switchMode;
        }

        if(bitmapHolder.getCurrentBitmap() != null && bitmapHolder.getNextBitmap() != null) {
            if(ApplicationData.getSlideTime().equals("Unlock")) {
                startSwitch();
            } else {
                timerTaskSwitcher = new EchoModeBackgroundSwitcher.SwitcherTimerTask();
                timerIsStarted = true;
                timerTick = 0;
                timerFinishTick = (int) (switchingDelay / 1000.0f * 60.0f);
            }
        }
    }

    public void deactive() {
        timerIsStarted = false;
        timerTick = 0;
        timerFinishTick = (int)(switchingDelay / 1000.0f * 60.0f);

        bitmapHolder.deactive();
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

    private void switchFinished() {
        Log.d("__SWITCH__", "switchFinished()");
        if(switcherEventListener != null) {
            switcherEventListener.onSwitchingAnimationComplete();
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

                if(ApplicationData.getIsEnableSlide()) {
                    switch(currentSwitchMode) {
                        case Slide: {
                            switchingCurrentX -= switchingStep;
                            switchingNextX -= switchingStep;

                            canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x + switchingCurrentX, currentWrapper.getTopBase() - y, null);
                            canvas.drawBitmap(nextBitmap, nextWrapper.getLeftBase() - x + switchingNextX, nextWrapper.getTopBase() - y, null);

                            if(switchingNextX - switchingStep <= 0) {
                                isSwitching = false;
                                bitmapHolder.next();
                                switchFinished();
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
                                switchFinished();
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
                                switchFinished();
                            }
                            break;
                        }
                        case Wipe: {
                            switchingCurrentW -= switchingStep;
                            if(switchingCurrentW <= 0) switchingCurrentW = 0;

                            switchingNextX -= switchingStep / 4;
                            if(switchingNextX <= 0) switchingNextX = 0;

                            switchingCurrentX -= switchingStep / 4;

                            canvas.drawBitmap(nextBitmap, nextWrapper.getLeftBase() - x + switchingNextX, nextWrapper.getTopBase() - y, null);

                            int left = currentWrapper.getLeftBase() - x + switchingCurrentX;
                            int top = currentWrapper.getTopBase() - y;
                            Rect srcRect = new Rect(0, 0, switchingCurrentW, currentWrapper.getBitmap().getHeight());
                            Rect destRect = new Rect(left, top, left + switchingCurrentW, top + currentWrapper.getBitmap().getHeight());
                            canvas.drawBitmap(currentBitmap, srcRect, destRect, null);

                            if(switchingNextX <= 0) {
                                isSwitching = false;
                                bitmapHolder.next();
                                switchFinished();
                            }
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
                                switchFinished();
                            }
                            break;
                        }
                        case BoxIn: {
                            switchingCurrentX += (switchingStep / 2);
                            switchingCurrentW = currentBitmap.getWidth() - (switchingCurrentX * 2);
                            switchingCurrentY += (int)((float)(switchingStep / 2) * ((float)screenSize.getHeight() / (float)screenSize.getWidth()));
                            switchingCurrentH = currentBitmap.getHeight() - (switchingCurrentY * 2);
                            int halfWidth = currentBitmap.getWidth() / 2;
                            int halfHeight = currentBitmap.getHeight() / 2;
                            if(switchingCurrentX >= halfWidth || switchingCurrentY >= halfHeight) {
                                switchingCurrentW = halfWidth;
                                switchingCurrentH = halfHeight;
                            }

                            canvas.drawBitmap(nextBitmap, nextWrapper.getLeftBase() - x, nextWrapper.getTopBase() - y, null);
                            if(switchingCurrentX < halfWidth && switchingCurrentY < halfHeight) {
                                int left = currentWrapper.getLeftBase() - x + switchingCurrentX;
                                int top = currentWrapper.getTopBase() - y + switchingCurrentY;
                                Rect srcRect = new Rect(switchingCurrentX, switchingCurrentY, switchingCurrentX + switchingCurrentW, switchingCurrentY + switchingCurrentH);
                                Rect destRect = new Rect(left, top, left + switchingCurrentW, top + switchingCurrentH);
                                canvas.drawBitmap(currentBitmap, srcRect, destRect, null);
                            }

                            if(switchingCurrentX >= halfWidth || switchingCurrentY >= halfHeight) {
                                isSwitching = false;
                                bitmapHolder.next();
                                switchFinished();
                            }
                            break;
                        }
                        case BoxOut: {
                            switchingNextX -= (switchingStep / 2);
                            switchingNextW = nextBitmap.getWidth() - (switchingNextX * 2);
                            switchingNextY -= (int)((float)(switchingStep / 2) * ((float)screenSize.getHeight() / (float)screenSize.getWidth()));
                            switchingNextH = nextBitmap.getHeight() - (switchingNextY * 2);
                            int afterNextX = switchingNextX - (switchingStep / 2);
                            int afterNextY = switchingNextY - (int)((float)(switchingStep / 2) * ((float)screenSize.getHeight() / (float)screenSize.getWidth()));

                            canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x, currentWrapper.getTopBase() - y, null);

                            int left = nextWrapper.getLeftBase() - x + switchingNextX;
                            int top = nextWrapper.getTopBase() - y + switchingNextY;
                            Rect srcRect = new Rect(switchingNextX, switchingNextY, switchingNextX + switchingNextW, switchingNextY + switchingNextH);
                            Rect destRect = new Rect(left, top, left + switchingNextW, top + switchingNextH);
                            canvas.drawBitmap(nextBitmap, srcRect, destRect, null);

                            if(afterNextX <= 0 || afterNextY <= 0) {
                                isSwitching = false;
                                bitmapHolder.next();
                                switchFinished();
                            }
                            break;
                        }
                        case CircleIn: {
                            switchingNextX -= switchingStep;
                            if(switchingNextX <= 0) switchingNextX = 0;

                            // draw next image in buffer
                            Canvas canvasBuffer = new Canvas(bitmapBuffer);
                            canvasBuffer.drawBitmap(nextBitmap, 0, 0, null);

                            // draw circle in buffer
                            if(switchingNextX > 0) {
                                Paint paint = new Paint();
                                paint.setColor(Color.TRANSPARENT);
                                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                                canvasBuffer.drawCircle(nextBitmap.getWidth() / 2, nextBitmap.getHeight() / 2, switchingNextX, paint);
                            }

                            canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x, currentWrapper.getTopBase() - y, null);
                            canvas.drawBitmap(bitmapBuffer, nextWrapper.getLeftBase() - x, nextWrapper.getTopBase() - y, null);

                            if(switchingNextX <= 0) {
                                isSwitching = false;
                                bitmapHolder.next();
                                switchFinished();
                            }
                            break;
                        }
                        case CircleOut: {
                            switchingCurrentX += switchingStep;
                            int finishRadius = (int)(currentBitmap.getHeight() / 1.7f);
                            if(switchingCurrentX >= finishRadius) switchingCurrentX = finishRadius;

                            canvas.drawBitmap(nextBitmap, nextWrapper.getLeftBase() - x, nextWrapper.getTopBase() - y, null);

                            // draw Current image in buffer
                            Canvas canvasBuffer = new Canvas(bitmapBuffer);
                            canvasBuffer.drawBitmap(currentBitmap, 0, 0, null);

                            // draw circle in buffer
                            Paint paint = new Paint();
                            paint.setColor(Color.TRANSPARENT);
                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                            canvasBuffer.drawCircle(currentBitmap.getWidth() / 2, currentBitmap.getHeight() / 2, switchingCurrentX, paint);

                            canvas.drawBitmap(bitmapBuffer, currentWrapper.getLeftBase() - x, currentWrapper.getTopBase() - y, null);

                            if(switchingCurrentX >= finishRadius) {
                                isSwitching = false;
                                bitmapHolder.next();
                                switchFinished();
                            }
                            break;
                        }
                        case SplitIn: {
                            switchingCurrentX += (switchingStep / 2);
                            switchingCurrentW = currentBitmap.getWidth() - (switchingCurrentX * 2);
                            int halfWidth = currentBitmap.getWidth() / 2;
                            if(switchingCurrentX >= halfWidth) {
                                switchingCurrentW = halfWidth;
                            }

                            canvas.drawBitmap(nextBitmap, nextWrapper.getLeftBase() - x, nextWrapper.getTopBase() - y, null);
                            if(switchingCurrentX < halfWidth) {
                                int left = currentWrapper.getLeftBase() - x + switchingCurrentX;
                                int top = currentWrapper.getTopBase() - y;
                                Rect srcRect = new Rect(switchingCurrentX, 0, switchingCurrentX + switchingCurrentW, currentBitmap.getHeight());
                                Rect destRect = new Rect(left, top, left + switchingCurrentW, top + currentBitmap.getHeight());
                                canvas.drawBitmap(currentBitmap, srcRect, destRect, null);
                            }

                            if(switchingCurrentX >= halfWidth) {
                                isSwitching = false;
                                bitmapHolder.next();
                                switchFinished();
                            }
                            break;
                        }
                        case SplitOut: {
                            switchingNextX -= (switchingStep / 2);
                            switchingNextW = nextBitmap.getWidth() - (switchingNextX * 2);
                            int afterNextX = switchingNextX - (switchingStep / 2);

                            canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x, currentWrapper.getTopBase() - y, null);

                            int left = nextWrapper.getLeftBase() - x + switchingNextX;
                            int top = nextWrapper.getTopBase() - y;
                            Rect srcRect = new Rect(switchingNextX, 0, switchingNextX + switchingNextW, nextBitmap.getHeight());
                            Rect destRect = new Rect(left, top, left + switchingNextW, top + nextBitmap.getHeight());
                            canvas.drawBitmap(nextBitmap, srcRect, destRect, null);

                            if(afterNextX <= 0) {
                                isSwitching = false;
                                bitmapHolder.next();
                                switchFinished();
                            }
                            break;
                        }
                    }
                } else {
                    isSwitching = false;
                    bitmapHolder.next();
                    canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x, currentWrapper.getTopBase() - y, null);
                    switchFinished();
                }

            } else {
                canvas.drawBitmap(currentBitmap, currentWrapper.getLeftBase() - x, currentWrapper.getTopBase() - y, null);
                switchFinished();
            }
        } else {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            canvas.drawRect(screenSize.toRect(), paint);
        }
    }

    private class SwitcherTimerTask implements Runnable {
        @Override
        public void run() {
            if(!isSwitching && bitmapHolder.isLoadComplete() && bitmapHolder.getCurrentBitmap() != null && bitmapHolder.getNextBitmap() != null) {
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
                        switchingCurrentW = bitmapHolder.getCurrentBitmap().getBitmap().getWidth();
                        switchingNextX = screenSize.getWidth() / 5;
                        break;
                    }
                    case Fade: {
                        switchingAlpha = 0;
                        break;
                    }
                    case BoxIn: {
                        switchingCurrentX = 0;
                        switchingCurrentY = 0;
                        switchingNextX = 0;
                        break;
                    }
                    case BoxOut: {
                        switchingNextX = bitmapHolder.getNextBitmap().getBitmap().getWidth() / 2;
                        switchingNextY = bitmapHolder.getNextBitmap().getBitmap().getHeight() / 2;
                        switchingCurrentX = 0;
                        switchingCurrentY = 0;
                        break;
                    }
                    case CircleIn: {
                        if(screenSize.getWidth() > screenSize.getHeight()) {
                            switchingNextX = (int)(screenSize.getWidth() / 1.7f);
                        } else {
                            switchingNextX = (int)(screenSize.getHeight() / 1.7f);
                        }
                        break;
                    }
                    case CircleOut: {
                        switchingCurrentX = 0;
                        break;
                    }
                    case SplitIn: {
                        switchingCurrentX = 0;
                        switchingCurrentY = 0;
                        switchingNextX = 0;
                        break;
                    }
                    case SplitOut: {
                        switchingNextX = bitmapHolder.getNextBitmap().getBitmap().getWidth() / 2;
                        switchingNextY = 0;
                        switchingCurrentX = 0;
                        switchingCurrentY = 0;
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
