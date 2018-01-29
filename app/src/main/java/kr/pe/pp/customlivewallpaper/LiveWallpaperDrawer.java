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

    private static int maxAngle = 80;
    private static boolean isBackground3D = true;

    Context context = null;
    BackgroundSwitcher backgroundSwitcher = null;
    ParticleDrawer particleDrawer = null;

    boolean isGetBaseAngle = false;
    AngleSensor angleSensor = null;
    double baseAngleX = 0, baseAngleY = 0;
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
        isGetBaseAngle = false;
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
        //offsetX = 0;
        //offsetY = 0;
        if(isGetBaseAngle && isBackground3D) {
            //Log.d("__Sensor__", "angle(" + angleSensor.angleX + ", " + angleSensor.angleY + ")");
            if(angleSensor.angleX <= maxAngle && angleSensor.angleX >= -maxAngle
                && angleSensor.angleY <= maxAngle && angleSensor.angleY >= -maxAngle) {
                double diffX = angleSensor.angleX - baseAngleX;
                offsetX = (int) (((float) BackgroundSwitcher.margin / (float) maxAngle) * diffX);
                if(offsetX > BackgroundSwitcher.margin) offsetX = BackgroundSwitcher.margin;
                if(offsetX < -BackgroundSwitcher.margin) offsetX = -BackgroundSwitcher.margin;

                double diffY = angleSensor.angleY - baseAngleY;
                offsetY = (int) (((float) BackgroundSwitcher.margin / (float) maxAngle) * diffY);
                if(offsetY > BackgroundSwitcher.margin) offsetY = BackgroundSwitcher.margin;
                if(offsetY < -BackgroundSwitcher.margin) offsetY = -BackgroundSwitcher.margin;
            }
        }
        if(!isGetBaseAngle) {
            if(baseAngleX != angleSensor.angleX && baseAngleY != angleSensor.angleY) {
                baseAngleX = angleSensor.angleX;
                baseAngleY = angleSensor.angleY;
                isGetBaseAngle = true;
            }
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

