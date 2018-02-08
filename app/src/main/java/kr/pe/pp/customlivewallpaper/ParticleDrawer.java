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

    private ArrayList<Bitmap> LoadParticleBitmapsFromSetting() {
        ArrayList<Bitmap> result = new ArrayList<Bitmap>();

        final int particleResourceId = Util.getResourceId("drawable", ApplicationData.getEffectParticleType(), context);
        Log.d("__Debug__", "Load Particle Resource : " + ApplicationData.getEffectParticleType() + " : " + particleResourceId);
        if(particleResourceId <= 0) {
            Log.d("__Debug__", "Cannot Load Particle Resource :" + ApplicationData.getEffectParticleType());
            return null;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), particleResourceId, options);
        int size = bmp.getHeight();
        int count = (int)(bmp.getWidth() / size);
        Log.d("__Debug__", "Particle Count : " + count + ", " + bmp.getWidth() + " / " + size);
        if(count == 1) {
            result.add(bmp);
        } else {
            for (int i = 0; i < count; i++) {
                Bitmap tmp = Bitmap.createBitmap(bmp, i * size, 0, size, size);
                result.add(tmp);
            }
            bmp.recycle();
        }
        return result;
    }

    public void init(Context context) {
        this.context = context;
        bitmapParticles = LoadParticleBitmapsFromSetting();
        generateSpeed = (int)((10 - ApplicationData.getEffectDensity()) / 2.0f * 100.0f) + 100;   // 150 ~ 600
    }

    public void destroy() {
        Particle particle = removeQueue.poll();
        while(particle != null) {
            particle.destroy();
            particle = removeQueue.poll();
        }

        particle = addQueue.poll();
        while(particle != null) {
            particle.destroy();
            particle = addQueue.poll();
        }

        for(Particle p : particles) {
            p.destroy();
        }
        particles.clear();

        for(Bitmap bmp : bitmapParticles) {
            bmp.recycle();
        }
        bitmapParticles.clear();
    }

    public void active(boolean isChangeSettings) {
        if(isChangeSettings) {
            bitmapParticles = LoadParticleBitmapsFromSetting();
            generateSpeed = (int)((10 - ApplicationData.getEffectDensity()) / 2.0f * 100.0f) + 100;   // 150 ~ 600
        }
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
            particle.destroy();
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
