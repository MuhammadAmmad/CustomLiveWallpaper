package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.sip.SipSession;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-01-24.
 */

public class Particle {
    public interface ParticleEventListener {
        void onParticleDestroy(Particle particle);
    }
    private ParticleEventListener particleEventListener = null;
    private Context context = null;
    private Bitmap bitmapParticle = null;
    private Util.Size screenSize = null;

    private int particleAngle = 0;
    private int particleX = 0;
    private int particleY = 0;
    private Paint particlePaint = null;
    private Matrix particleMatrix = null;
    private boolean isFinish = false;

    public void setParticleEventListener(ParticleEventListener listener) {
        particleEventListener = listener;
    }

    public void init(Context context, Bitmap bitmapParticle) {
        this.context = context;
        this.bitmapParticle = bitmapParticle;
        this.screenSize = Util.getScreenSize(context);

        particleX = (int)(Math.random() * screenSize.getWidth());
        particleY = bitmapParticle.getHeight() * -1;
        particlePaint = new Paint();
        particleMatrix = new Matrix();
    }

    public void destroy() {
    }

    public void active() {
    }

    public void deactive() {
    }

    public void draw(Canvas canvas) {
        if(bitmapParticle == null || isFinish) return;

        particleAngle += 5;
        if(particleAngle >= 360) {
            particleAngle = 0;
        }
        Log.d("__Debug__", "particleAngle : " + particleAngle);
        particleMatrix.postRotate(1.0f / (float)particleAngle, bitmapParticle.getWidth() / 2, bitmapParticle.getHeight() / 2);

        particleY += 2;
        particleMatrix.setTranslate(particleX, particleY);
        if(particleY >= screenSize.getHeight() + bitmapParticle.getHeight()) {
            isFinish = true;
            if(particleEventListener != null) {
                particleEventListener.onParticleDestroy(this);
            }
        }

        canvas.drawBitmap(bitmapParticle, particleMatrix, particlePaint);
    }
}
