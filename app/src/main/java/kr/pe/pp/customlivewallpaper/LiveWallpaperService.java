package kr.pe.pp.customlivewallpaper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
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

    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    Util.Size screenSize = null;

    private void LoadBitmaps() {
        screenSize = Util.getScreenSize(getApplicationContext());
        Log.d("__Debug__", "LiveWallpaperService::LoadBitmaps-ScreenSize(" + screenSize.getWidth() + ", " + screenSize.getHeight() + ")");

        ApplicationData.Load(getApplicationContext());
        for(String path : ApplicationData.getImagePathList()) {
            Bitmap bmp = Util.createBitmapFromPath(path, screenSize.getWidth(), screenSize.getHeight());
            bmp = Util.resizeBitmapWithMargin(bmp, screenSize.getWidth(), screenSize.getHeight(), Util.ResizeMode.RESIZE_FIT_IMAGE, 50, Util.CropMode.CROP_CENTER);
            bitmaps.add(bmp);
        }
    }

    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine();
    }

    private class LiveWallpaperEngine extends Engine {
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            LoadBitmaps();
            Log.d("__Debug__", "LiveWallpaperEngine::onCreate");
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            SurfaceHolder holder = getSurfaceHolder();

            Canvas canvas = holder.lockCanvas();

            if(bitmaps.size() > 0) {
                Bitmap bmp = bitmaps.get(0);

                canvas.drawBitmap(bitmaps.get(0), (screenSize.getWidth() - bmp.getWidth()) / 2, (screenSize.getHeight() - bmp.getHeight()) / 2, null);
            }

            holder.unlockCanvasAndPost(canvas);
        }
    }
}
