package kr.pe.pp.customlivewallpaper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-01-17.
 */

public class LiveWallpaperService extends WallpaperService {

    private IDrawer drawer = new LiveWallpaperDrawer();

    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine();
    }

    private class LiveWallpaperEngine extends Engine {
        private boolean visible = false;
        private boolean running = true;
        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                SurfaceHolder holder = getSurfaceHolder();

                Canvas canvas = holder.lockCanvas();

                drawer.draw(canvas);

                holder.unlockCanvasAndPost(canvas);
            }
        };
        private final Thread animationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long idleSleepTick = 1000 / 1;
                long runningSleepTick = 1000 / 60;

                while(running) {
                    try {
                        if(visible) {
                            long before = System.currentTimeMillis();
                            handler.post(drawRunner);
                            long runningTime = System.currentTimeMillis() - before;

                            if(runningSleepTick - runningTime > 0) {
                                Thread.sleep(runningSleepTick - runningTime);
                            }
                            //Log.d("LiveWallpaperService", "Running... " + (runningSleepTick - runningTime));
                        } else {
                            Thread.sleep(idleSleepTick);
                            Log.d("LiveWallpaperService", "idle...");
                        }
                    } catch (Exception e) {
                        Log.e("LiveWallpaperService", "Exception in thread : ", e);
                    }
                }
            }
        });

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            drawer.init(getApplicationContext());
            running = true;
            animationThread.start();
            Log.d("LiveWallpaperService", "LiveWallpaperEngine::onCreate");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            running = false;
            Log.d("LiveWallpaperService", "LiveWallpaperEngine::onDestroy");
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            Log.d("LiveWallpaperService", "LiveWallpaperEngine::onSurfaceCreated");
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            Log.d("LiveWallpaperService", "LiveWallpaperEngine::onSurfaceDestroyed");
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                drawer.active();
                handler.post(drawRunner);
            } else {
                drawer.deactive();
                handler.removeCallbacks(drawRunner);
            }
            super.onVisibilityChanged(visible);
        }
    }

}
