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

    private int particleSpeed = 4;
    private int particleRotateSpeed = 5;

    private int particleAngle = 0;
    private int particleX = 0;
    private int particleY = 0;
    private float particleScale = 1.0f;
    private float particleRotateCenter = 0.0f;
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

        particleScale = (float)((Math.random() * 60) + 20) / 100.0f;
        particleRotateCenter = 2 / particleScale;
        particleSpeed = (int)(particleScale * 8);           // speed 2 ~ 7
        particleRotateSpeed = (int)(Math.random() * 6) + 2;    // rotate speed 2 ~ 7
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

        particleMatrix.reset();

        particleAngle += particleRotateSpeed;
        if(particleAngle >= 360) {
            particleAngle -= 360;
        }
        //Log.d("__Debug__", "particleAngle : " + particleAngle);
        particleMatrix.postRotate(particleAngle, bitmapParticle.getWidth() / particleRotateCenter, bitmapParticle.getHeight() / particleRotateCenter);

        particleY += particleSpeed;
        particleMatrix.postTranslate(particleX, particleY);
        if(particleY >= screenSize.getHeight() + bitmapParticle.getHeight()) {
            isFinish = true;
            if(particleEventListener != null) {
                particleEventListener.onParticleDestroy(this);
            }
        }

        particleMatrix.preScale(particleScale, particleScale);
        //particleMatrix.postScale(0.5f, 0.5f);

        canvas.drawBitmap(bitmapParticle, particleMatrix, null);
    }
}
