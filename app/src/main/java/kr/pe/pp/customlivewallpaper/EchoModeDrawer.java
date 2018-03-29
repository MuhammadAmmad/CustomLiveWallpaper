package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2018-01-23.
 */

public class EchoModeDrawer implements IDrawer {
    public interface EchoModeDrawerEventListener {
        void onDrawAnimationComplete();
    }
    private EchoModeDrawerEventListener drawerEventListener = null;
    public void setDrawerEventListener(EchoModeDrawerEventListener listener) {
        this.drawerEventListener = listener;
    }

    Context context = null;
    EchoModeBackgroundSwitcher backgroundSwitcher = null;
    int offsetX = 0, offsetY = 0;

    public EchoModeDrawer() {
        backgroundSwitcher = new EchoModeBackgroundSwitcher(EchoModeBackgroundSwitcher.SwitchMode.Cover);
    }

    @Override
    public void init(Context context) {
        this.context = context;

        EchoModeBackgroundSwitcher.SwitchMode switchMode = null;
        try {
            switchMode = EchoModeBackgroundSwitcher.SwitchMode.valueOf(ApplicationData.getSlideType());
        } catch(IllegalArgumentException ex) { }
        if(switchMode == null) switchMode = EchoModeBackgroundSwitcher.SwitchMode.Cover;
        backgroundSwitcher.init(context, switchMode);
        backgroundSwitcher.setSwitcherEventListener(new EchoModeBackgroundSwitcher.EchoModeSwitcherEventListener() {
            @Override
            public void onSwitchingAnimationComplete() {
                if(drawerEventListener != null) {
                    drawerEventListener.onDrawAnimationComplete();
                }
            }
        });
    }

    @Override
    public void destroy() {
        backgroundSwitcher.destroy();
    }

    @Override
    public void active(boolean isChangeSettings) {
        backgroundSwitcher.active(isChangeSettings);
    }

    @Override
    public void deactive() {
        backgroundSwitcher.deactive();
    }

    @Override
    public void update() {
        offsetX = 0;
        offsetY = 0;
        backgroundSwitcher.update();
    }

    @Override
    public void draw(Canvas canvas) {
        backgroundSwitcher.draw(canvas, offsetX, offsetY);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        //canvas.drawText("Pitch:" + angleSensor.pitch + ", Roll:" + angleSensor.roll + ", OffsetX:" + offsetX + ", OffsetY:" + offsetY, 10, 10, paint);
    }

    public void startSwitchBackground() {
        backgroundSwitcher.startSwitch();
    }

    public void onTouchEvent(MotionEvent event) {
    }

}

