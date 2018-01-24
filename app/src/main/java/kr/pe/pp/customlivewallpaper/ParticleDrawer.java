package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.NonNull;

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
    private class GenerateTimerTask extends TimerTask {
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
                particle.init(context, bitmapParticles.get((int)(Math.random() * bitmapParticles.size())));
                addQueue.add(particle);
            }
        }
    }

    private Context context = null;
    private ArrayList<Bitmap> bitmapParticles = new ArrayList<>();
    private ArrayList<Particle> particles = new ArrayList<>();
    private TimerTask timerTaskGenerator = null;
    private Timer timerGenerator = null;
    private Queue<Particle> removeQueue = new LinkedList<Particle>();
    private Queue<Particle> addQueue = new LinkedList<Particle>();

    public void init(Context context) {
        this.context = context;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.particle_snow, options);
        int size = bmp.getHeight();
        int count = (int)(bmp.getWidth() / size);
        for(int i=0; i<count; i++) {
            Bitmap tmp = Bitmap.createBitmap(bmp, i*size, 0, size, size);
            bitmapParticles.add(tmp);
        }
        bmp.recycle();
    }

    public void destroy() {
    }

    public void active() {
        timerTaskGenerator = new GenerateTimerTask();
        timerGenerator = new Timer();
        timerGenerator.schedule(timerTaskGenerator, 0, 300);
    }

    public void deactive() {
        timerGenerator.cancel();
    }

    public void draw(Canvas canvas) {
        for(Particle particle : particles) {
            particle.draw(canvas);
        }

        Particle particle = removeQueue.poll();
        if(particle != null) {
            particles.remove(particle);
        }

        particle = addQueue.poll();
        if(particle != null) {
            particles.add(particle);
        }
    }
}
