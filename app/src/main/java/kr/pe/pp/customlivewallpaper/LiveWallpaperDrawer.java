package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

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

    boolean isGetBaseAngle = false;
    AngleSensor angleSensor = null;
    double basePitch = 0, baseRoll = 0;

    public LiveWallpaperDrawer() {
        backgroundSwitcher = new BackgroundSwitcher(BackgroundSwitcher.SwitchMode.SWITCH_RANDOM);
    }

    @Override
    public void init(Context context) {
        this.context = context;

        backgroundSwitcher.init(context);
        angleSensor = new AngleSensor(context);
    }

    @Override
    public void destroy() {
        backgroundSwitcher.destroy();
    }

    @Override
    public void active() {
        backgroundSwitcher.active();

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

        backgroundSwitcher.deactive();
    }

    @Override
    public void draw(Canvas canvas) {
        int offsetX = 0, offsetY = 0;
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
        backgroundSwitcher.draw(canvas, offsetX, offsetY);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        //canvas.drawText("Pitch:" + angleSensor.pitch + ", Roll:" + angleSensor.roll + ", OffsetX:" + offsetX + ", OffsetY:" + offsetY, 10, 10, paint);
    }


}

