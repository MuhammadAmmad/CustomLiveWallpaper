package kr.pe.pp.customlivewallpaper;

import android.graphics.Canvas;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

/**
 * Created by Administrator on 2018-01-17.
 */

public class LiveWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine();
    }

    private class LiveWallpaperEngine extends Engine {
        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            SurfaceHolder holder = getSurfaceHolder();

            Canvas canvas = holder.lockCanvas();



            holder.unlockCanvasAndPost(canvas);
        }
    }
}
