package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018-01-24.
 */

public class ParticleDrawer {

    private class GenerateTimerTask implements Runnable {
        @Override
        public void run() {
            Particle particle = new Particle();
            particle.setParticleEventListener(new Particle.ParticleEventListener() {
                @Override
                public void onParticleDestroy(Particle particle) {
                    removeQueue.add(particle);
                }
            });

            if(bitmapParticles.size() > 0) {
                particle.init(context, bitmapParticles.get((int)(Math.random() * bitmapParticles.size())), ApplicationData.getEffectIsUseRotate());
                addQueue.add(particle);
            }
        }
    }

    private Context context = null;
    private ArrayList<Bitmap> bitmapParticles = new ArrayList<>();
    private ArrayList<Particle> particles = new ArrayList<>();
    private boolean timerGeneratorStarted = false;
    private int timerGeneratorTick = 0;
    private int timerGeneratorFinishTick = 0;
    private int generateSpeed = 300;
    private Runnable timerTaskGenerator = new GenerateTimerTask();
    private Queue<Particle> removeQueue = new LinkedList<Particle>();
    private Queue<Particle> addQueue = new LinkedList<Particle>();

    public void init(Context context) {
        this.context = context;

        generateSpeed = (int)((10 - ApplicationData.getEffectDensity()) / 2.0f * 100.0f) + 100;   // 150 ~ 600
        final BitmapFactory.Options options = new BitmapFactory.Options();
        final int particleResourceId = Util.getResourceId("drawable", ApplicationData.getEffectParticleType(), context);
        options.inSampleSize = 2;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), particleResourceId, options);
        int size = bmp.getHeight();
        int count = (int)(bmp.getWidth() / size);
        Log.d("__Debug__", "Particle Count : " + count + ", " + bmp.getWidth() + " / " + size);
        if(count == 1) {
            bitmapParticles.add(bmp);
        } else {
            for (int i = 0; i < count; i++) {
                Bitmap tmp = Bitmap.createBitmap(bmp, i * size, 0, size, size);
                bitmapParticles.add(tmp);
            }
            bmp.recycle();
        }
    }

    public void destroy() {
    }

    public void active() {
        timerGeneratorStarted = true;
        timerGeneratorTick = 0;
        timerGeneratorFinishTick = (int)(generateSpeed / 1000.0f * 60.0f);
    }

    public void deactive() {
        timerGeneratorStarted = false;
        timerGeneratorTick = 0;
        timerGeneratorFinishTick = (int)(generateSpeed / 1000.0f * 60.0f);
    }

    private void timerCheck() {
        if(timerGeneratorStarted) {
            timerGeneratorTick++;
            if(timerGeneratorTick >= timerGeneratorFinishTick) {
                timerGeneratorTick = 0;
                timerTaskGenerator.run();
            }
        }
    }

    public void update() {
        for(Particle particle : particles) {
            particle.update();
        }

        Particle particle = removeQueue.poll();
        if(particle != null) {
            particles.remove(particle);
        }

        particle = addQueue.poll();
        if(particle != null) {
            particles.add(particle);
        }

        timerCheck();
    }

    public void draw(Canvas canvas, int offsetX, int offsetY) {
        for(Particle particle : particles) {
            particle.draw(canvas, offsetX, offsetY);
        }
    }
}
