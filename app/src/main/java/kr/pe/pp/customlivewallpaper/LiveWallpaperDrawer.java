package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018-01-23.
 */

public class LiveWallpaperDrawer implements IDrawer {

    private static boolean isBackground3D = true;

    Context context = null;
    BackgroundSwitcher backgroundSwitcher = null;
    ParticleDrawer particleDrawer = null;

    boolean isGetBaseAngle = false;
    AngleSensor angleSensor = null;
    double basePitch = 0, baseRoll = 0;
    int offsetX = 0, offsetY = 0;

    public LiveWallpaperDrawer() {
        backgroundSwitcher = new BackgroundSwitcher(BackgroundSwitcher.SwitchMode.Cover);
        particleDrawer = new ParticleDrawer();
    }

    @Override
    public void init(Context context) {
        this.context = context;

        BackgroundSwitcher.SwitchMode switchMode = null;
        try {
            switchMode = BackgroundSwitcher.SwitchMode.valueOf(ApplicationData.getSlideType());
        } catch(IllegalArgumentException ex) { }
        if(switchMode == null) switchMode = BackgroundSwitcher.SwitchMode.Cover;
        backgroundSwitcher.init(context, switchMode);
        angleSensor = new AngleSensor(context);
        particleDrawer.init(context);
    }

    @Override
    public void destroy() {
        backgroundSwitcher.destroy();
        particleDrawer.destroy();
    }

    @Override
    public void active() {
        backgroundSwitcher.active();
        particleDrawer.active();

        angleSensor.register();
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                basePitch = angleSensor.pitch;
                baseRoll = angleSensor.roll;
                isGetBaseAngle = true;
            }
        }, 10);
    }

    @Override
    public void deactive() {
        isGetBaseAngle = false;
        angleSensor.unregister();

        particleDrawer.deactive();
        backgroundSwitcher.deactive();
    }

    @Override
    public void update() {
        offsetX = 0;
        offsetY = 0;
        if(isGetBaseAngle && isBackground3D) {
            double diffPitch = angleSensor.pitch - basePitch;
            if (diffPitch > 90) diffPitch = 90;
            if (diffPitch < -90) diffPitch = -90;
            offsetX = (int) (((float) BackgroundSwitcher.margin / (float) 90) * diffPitch);

            double diffRoll = angleSensor.roll - baseRoll;
            if (diffRoll > 90) diffRoll = 90;
            if (diffRoll < -90) diffRoll = -90;
            offsetY = (int) (((float) BackgroundSwitcher.margin / (float) 90) * diffRoll);
        }

        backgroundSwitcher.update();
        particleDrawer.update();
    }

    @Override
    public void draw(Canvas canvas) {
        backgroundSwitcher.draw(canvas, offsetX, offsetY);
        if(ApplicationData.getIsEnableEffect()) {
            particleDrawer.draw(canvas, offsetX, offsetY);
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        //canvas.drawText("Pitch:" + angleSensor.pitch + ", Roll:" + angleSensor.roll + ", OffsetX:" + offsetX + ", OffsetY:" + offsetY, 10, 10, paint);
    }


}

