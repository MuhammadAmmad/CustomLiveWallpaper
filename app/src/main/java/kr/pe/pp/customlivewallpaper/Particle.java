package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
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

    private boolean isRotate = true;
    private boolean isRotateRight = true;

    private float particleSpeed = 4;
    private int particleRotateSpeed = 5;
    private float particleHorMoveSpeed = 0.0f;

    private int particleAngle = 0;
    private float particleX = 0;
    private float particleY = 0;
    private float particleScale = 1.0f;
    private float particleScaleVibrateMax = 0.0f;
    private float particleScaleVibrateStep = 0.01f;
    private float particleScaleVibrateCurrent = 0.0f;
    private float particleRotateCenter = 0.0f;
    private Paint particlePaint = null;
    private Matrix particleMatrix = null;
    private boolean isFinish = false;

    private int bitmapWidth = 0;
    private int bitmapHeight = 0;
    private int bitmapHalfWidth = 0;
    private int bitmapHalfHeight = 0;

    public float getX() {
        return particleX;
    }
    public float getY() {
        return particleY;
    }
    public float getWidth() {
        return bitmapParticle.getWidth() * particleScale;
    }
    public float getHeight() {
        return bitmapParticle.getHeight() * particleScale;
    }
    public float getRight() {
        return particleX + bitmapParticle.getWidth() * particleScale;
    }
    public float getBottom() {
        return particleY + bitmapParticle.getHeight() * particleScale;
    }
    public Rect getRect() {
        return new Rect((int)particleX,
                (int)particleY,
                (int)(particleX + bitmapParticle.getWidth() * particleScale),
                (int)(particleY + bitmapParticle.getHeight() * particleScale));
    }

    public void setParticleEventListener(ParticleEventListener listener) {
        particleEventListener = listener;
    }

    public void init(Context context, Bitmap bitmapParticle, boolean isRotate) {
        this.context = context;
        this.bitmapParticle = bitmapParticle;
        this.screenSize = Util.getScreenSize(context);
        this.isRotate = isRotate;
        this.isRotateRight = ApplicationData.getEffectIsRotateRight();

        bitmapWidth = bitmapParticle.getWidth();
        bitmapHeight = bitmapParticle.getHeight();
        bitmapHalfWidth = bitmapWidth / 2;
        bitmapHalfHeight = bitmapHeight / 2;

        particleScaleVibrateMax = 0.4f / 4 * ApplicationData.getEffectSizeVibrate();
        particleScaleVibrateStep = 0.01f;
        particleScaleVibrateCurrent = 0.0f;
        particleScale = (float)((Math.random() * 30) + (30 / 10 * (ApplicationData.getEffectSize() + 1))) / 100.0f;
        particleRotateCenter = 2 / particleScale;
        particleSpeed = particleScale * (4 + ApplicationData.getEffectMoveSpeed());           // speed 2 ~ 7
        particleRotateSpeed = (int)(Math.random() * 6) + (ApplicationData.getEffectRotateSpeed() / 2) + 1;    // rotate speed 2 ~ 7
        switch(ApplicationData.getEffectMoveDirection()) {
            case DOWN:
                particleHorMoveSpeed = 0;
                particleX = (int)(Math.random() * screenSize.getWidth());
                break;
            case LEFT_DOWN:
                particleHorMoveSpeed = -(particleSpeed / 2);
                particleX = (int)(Math.random() * (screenSize.getWidth() * 2));
                break;
            case RIGHT_DOWN:
                particleHorMoveSpeed = particleSpeed / 2;
                particleX = (int)(Math.random() * (screenSize.getWidth() * 2)) - screenSize.getWidth();
                break;
        }
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

    public void update() {
        if(bitmapParticle == null || isFinish) return;

        particleY += particleSpeed;
        particleX += particleHorMoveSpeed;
        if(particleY >= screenSize.getHeight() + bitmapHeight) {
            isFinish = true;
            if(particleEventListener != null) {
                particleEventListener.onParticleDestroy(this);
            }
            return;
        } else if(particleHorMoveSpeed > 0 && particleX >= screenSize.getWidth() + bitmapWidth) {
            isFinish = true;
            if(particleEventListener != null) {
                particleEventListener.onParticleDestroy(this);
            }
            return;
        } else if(particleHorMoveSpeed < 0 && particleX <= -bitmapWidth) {
            isFinish = true;
            if(particleEventListener != null) {
                particleEventListener.onParticleDestroy(this);
            }
            return;
        } else if(particleHorMoveSpeed > 0 && particleX < -bitmapWidth) {
            return;
        } else if(particleHorMoveSpeed < 0 && particleX > screenSize.getWidth() + bitmapWidth) {
            return;
        }

        particleScaleVibrateCurrent += particleScaleVibrateStep;
        if(particleScaleVibrateCurrent >= particleScaleVibrateMax || particleScaleVibrateCurrent <= 0) {
            particleScaleVibrateStep *= -1;
            particleScaleVibrateCurrent += particleScaleVibrateStep;
        }

        if(isRotate) {
            if (isRotateRight) {
                particleAngle += particleRotateSpeed;
                if (particleAngle >= 360) {
                    particleAngle -= 360;
                }
            } else {
                particleAngle -= particleRotateSpeed;
                if (particleAngle < 0) {
                    particleAngle += 360;
                }
            }
            particleRotateCenter = (2 / (particleScale + particleScaleVibrateCurrent));
        }
    }

    public void draw(Canvas canvas, int offsetX, int offsetY) {
        if(bitmapParticle == null || isFinish) return;

        particleMatrix.reset();

        // rotate
        if(isRotate) {
            particleMatrix.postRotate(particleAngle, bitmapWidth / particleRotateCenter, bitmapHeight / particleRotateCenter);
        }

        // translate
        particleMatrix.postTranslate(particleX - (offsetX * (0.8f - particleScale)) - (bitmapHalfWidth * particleScaleVibrateCurrent), particleY - (offsetY * (0.8f - particleScale)) - (bitmapHalfHeight * particleScaleVibrateCurrent));

        // scale
        particleMatrix.preScale(particleScale + particleScaleVibrateCurrent, particleScale + particleScaleVibrateCurrent);

        canvas.drawBitmap(bitmapParticle, particleMatrix, null);
    }
}
