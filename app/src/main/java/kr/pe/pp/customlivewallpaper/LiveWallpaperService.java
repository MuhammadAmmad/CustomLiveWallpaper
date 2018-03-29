package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * Created by Administrator on 2018-01-17.
 */

public class LiveWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        ApplicationData.Load(this.getApplicationContext());
        Log.d("__MODE__", "ApplicationData.getMode() = " + ApplicationData.getMode().toString());
        if(ApplicationData.getMode() == ApplicationData.WallpaperMode.ECHO) {
            return new EchoModeEngine();
        } else {
            return new PerformanceModeEngine();
        }
    }

    public class PerformanceModeEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {
        private IDrawer drawer = new LiveWallpaperDrawer();
        private boolean visible = false;
        private boolean isDraw = true;
        private boolean isChangeSettings = false;
        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                SurfaceHolder holder = getSurfaceHolder();

                Canvas canvas = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    canvas = holder.lockHardwareCanvas();
                } else {
                    canvas = holder.lockCanvas();
                }

                drawer.draw(canvas);

                holder.unlockCanvasAndPost(canvas);
            }
        };
        private final Runnable updateRunner = new Runnable() {
            @Override
            public void run() {
                drawer.update();
            }
        };

        private class AnimationRunnable implements Runnable {
            public boolean running = false;

            @Override
            public void run() {
                long idleSleepTick = 1000 / 10;
                long runningSleepTick = 1000 / 60;

                while(this.running) {
                    try {
                        if(visible) {
                            long before = System.currentTimeMillis();
                            updateRunner.run();
                            if(isDraw) {
                                drawRunner.run();
                            } else {
                                isDraw = true;
                            }
                            long runningTime = System.currentTimeMillis() - before;

                            if(runningSleepTick - runningTime > 0) {
                                Thread.sleep(runningSleepTick - runningTime);
                            } else if(runningTime << 1 >= runningSleepTick) {
                                //isDraw = false;
                            }
                        } else {
                            Thread.sleep(idleSleepTick);
                        }
                    } catch (Exception e) {
                        Log.e("LiveWallpaperService", "Exception in thread : ", e);
                    }
                }
            }
        }
        private AnimationRunnable animationLoop = null;
        private Thread animationThread = null;

        public PerformanceModeEngine() {
            super();
            Log.d("LiveWallpaperService", "PerformanceModeEngine::Creator - " + System.identityHashCode(this));
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            SharedPreferences prefImagePathList = getSharedPreferences("ImagePathList", Context.MODE_PRIVATE);
            prefImagePathList.registerOnSharedPreferenceChangeListener(this);
            SharedPreferences prefEffects = getSharedPreferences("Effects", Context.MODE_PRIVATE);
            prefEffects.registerOnSharedPreferenceChangeListener(this);

            ApplicationData.Load(getApplicationContext());
            drawer.init(getApplicationContext());
            Log.d("LiveWallpaperService", "PerformanceModeEngine::onCreate isPreview(" + isPreview() + ") - " + System.identityHashCode(this));
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            isChangeSettings = true;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            drawer.destroy();
            Log.d("LiveWallpaperService", "PerformanceModeEngine::onDestroy isPreview(" + isPreview() + ") - " + System.identityHashCode(this));
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            Log.d("LiveWallpaperService", "PerformanceModeEngine::onSurfaceCreated isPreview(" + isPreview() + ") - " + System.identityHashCode(this));
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            Log.d("LiveWallpaperService", "PerformanceModeEngine::onSurfaceDestroyed isPreview(" + isPreview() + ") - " + System.identityHashCode(this));
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            Log.d("LiveWallpaperService", "PerformanceModeEngine::onVisibilityChanged(" + visible + ") isPreview(" + isPreview() + ") - " + System.identityHashCode(this));
            this.visible = visible;
            if (visible) {
                if(isChangeSettings) {
                    ApplicationData.Load(getApplicationContext());
                }
                drawer.active(isChangeSettings);
                isChangeSettings = false;

                updateRunner.run();
                drawRunner.run();
                animationLoop = new AnimationRunnable();
                animationLoop.running = true;
                animationThread = new Thread(animationLoop);
                animationThread.start();
            } else {
                animationLoop.running = false;
                drawer.deactive();
            }
            super.onVisibilityChanged(visible);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            //super.onTouchEvent(event);
            drawer.onTouchEvent(event);
        }
    }

    public class EchoModeEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {
        private EchoModeDrawer drawer = new EchoModeDrawer();
        private boolean visible = false;
        private boolean isDraw = true;
        private boolean isChangeSettings = false;
        private Timer timerSwitch;
        private TimerTask timerTaskSwitch;
        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                SurfaceHolder holder = getSurfaceHolder();

                Canvas canvas = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    canvas = holder.lockHardwareCanvas();
                } else {
                    canvas = holder.lockCanvas();
                }

                drawer.draw(canvas);

                holder.unlockCanvasAndPost(canvas);
            }
        };
        private final Runnable updateRunner = new Runnable() {
            @Override
            public void run() {
                drawer.update();
            }
        };

        private class AnimationRunnable implements Runnable {
            public boolean running = false;

            @Override
            public void run() {
                long idleSleepTick = 1000 / 10;
                long runningSleepTick = 1000 / 60;

                while(this.running) {
                    try {
                        if(visible) {
                            long before = System.currentTimeMillis();
                            updateRunner.run();
                            if(isDraw) {
                                drawRunner.run();
                            } else {
                                isDraw = true;
                            }
                            long runningTime = System.currentTimeMillis() - before;

                            if(runningSleepTick - runningTime > 0) {
                                Thread.sleep(runningSleepTick - runningTime);
                            } else if(runningTime << 1 >= runningSleepTick) {
                                //isDraw = false;
                            }
                            Log.d("_TIMER_", "animationLoop - run()");
                        } else {
                            Thread.sleep(idleSleepTick);
                        }
                    } catch (Exception e) {
                        Log.e("LiveWallpaperService", "Exception in thread : ", e);
                    }
                }
            }
        }
        private AnimationRunnable animationLoop = null;
        private Thread animationThread = null;

        public EchoModeEngine() {
            super();
            Log.d("LiveWallpaperService", "EchoModeEngine::Creator - " + System.identityHashCode(this));
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            SharedPreferences prefImagePathList = getSharedPreferences("ImagePathList", Context.MODE_PRIVATE);
            prefImagePathList.registerOnSharedPreferenceChangeListener(this);
            SharedPreferences prefEffects = getSharedPreferences("Effects", Context.MODE_PRIVATE);
            prefEffects.registerOnSharedPreferenceChangeListener(this);

            ApplicationData.Load(getApplicationContext());
            drawer.init(getApplicationContext());
            drawer.setDrawerEventListener(new EchoModeDrawer.EchoModeDrawerEventListener() {
                @Override
                public void onDrawAnimationComplete() {
                    animationLoop.running = false;
                }
            });

            Log.d("LiveWallpaperService", "EchoModeEngine::onCreate isPreview(" + isPreview() + ") - " + System.identityHashCode(this));
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            isChangeSettings = true;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            drawer.destroy();
            Log.d("LiveWallpaperService", "EchoModeEngine::onDestroy isPreview(" + isPreview() + ") - " + System.identityHashCode(this));
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            Log.d("LiveWallpaperService", "EchoModeEngine::onSurfaceCreated isPreview(" + isPreview() + ") - " + System.identityHashCode(this));
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            Log.d("LiveWallpaperService", "EchoModeEngine::onSurfaceDestroyed isPreview(" + isPreview() + ") - " + System.identityHashCode(this));
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            Log.d("LiveWallpaperService", "EchoModeEngine::onVisibilityChanged(" + visible + ") isPreview(" + isPreview() + ") - " + System.identityHashCode(this));
            this.visible = visible;
            if (visible) {
                if(isChangeSettings) {
                    ApplicationData.Load(getApplicationContext());
                }
                drawer.active(isChangeSettings);
                isChangeSettings = false;

                timerTaskSwitch = new TimerTask() {
                    @Override
                    public void run() {
                        Log.d("_TIMER_", "timerTaskSwitch run()");
                        if(ApplicationData.getSlideTime() == "SettingDelay") {
                            if (animationLoop == null || (!animationLoop.running && !animationThread.isAlive())) {
                                drawer.active(isChangeSettings);
                                isChangeSettings = false;

                                updateRunner.run();
                                drawRunner.run();
                                animationLoop = new AnimationRunnable();
                                animationLoop.running = true;
                                animationThread = new Thread(animationLoop);
                                animationThread.start();
                                drawer.startSwitchBackground();
                            }
                        }
                    }
                };
                timerSwitch = new Timer();
                timerSwitch.schedule(timerTaskSwitch, ApplicationData.getSlideDelay() * 1000 * 3);

                updateRunner.run();
                drawRunner.run();
                if(animationLoop == null || (!animationLoop.running && !animationThread.isAlive())) {
                    animationLoop = new AnimationRunnable();
                    animationLoop.running = true;
                    animationThread = new Thread(animationLoop);
                    animationThread.start();
                }
            } else {
                animationLoop.running = false;
                drawer.deactive();
                timerSwitch.cancel();
            }
            super.onVisibilityChanged(visible);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            //super.onTouchEvent(event);
            drawer.onTouchEvent(event);
        }
    }

}
