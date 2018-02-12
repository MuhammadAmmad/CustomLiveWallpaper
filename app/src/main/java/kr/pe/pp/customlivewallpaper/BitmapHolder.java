package kr.pe.pp.customlivewallpaper;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-01-23.
 */

public class BitmapHolder {
    public interface BitmapHolderEventListener {
        void onLoadComplete(boolean isInit);
    }
    private BitmapHolderEventListener bitmapHolderEventListener = null;
    public void setBitmapHolderEventListener(BitmapHolderEventListener listener) {
        this.bitmapHolderEventListener = listener;
    }

    private Context context = null;
    private BitmapHolderMixMode mixMode = null;
    private BitmapWrapper[] wrappers = new BitmapWrapper[3];
    private ArrayList<SaveImage> pathList = new ArrayList<>();
    private int imageMargin = 0;
    private Util.Size screenSize = null;
    private int currentIndex = 0;
    private boolean isLoading = true;
    private final BitmapHolder self = this;


    public enum BitmapHolderMixMode {
        MIXMODE_SEQUENTIAL,
        MIXMODE_RANDOM
    }

    public BitmapHolder(BitmapHolderMixMode mixMode, int imageMargin) {
        this.mixMode = mixMode;
        this.imageMargin = imageMargin;
    }

    private void ShakePathList() {

    }

    private BitmapWrapper LoadImageFromIndex(int index) {
        Bitmap bitmap = Util.createBitmapFromPath(pathList.get(index).getPath(), screenSize.getWidth(), screenSize.getHeight(), pathList.get(index).getRotate());
        if(bitmap == null) {
            return null;
        }
        Bitmap bmp = Util.resizeBitmapWithMargin(bitmap, screenSize.getWidth(), screenSize.getHeight(), Util.ResizeMode.RESIZE_FIT_IMAGE, imageMargin, Util.CropMode.CROP_CENTER);
        BitmapWrapper wrapper = new BitmapWrapper(self.context, bmp);
        bitmap.recycle();
        return wrapper;
    }

    private void LoadImagesFromData(final boolean isInit) {
        pathList.clear();
        for(SaveImage path : ApplicationData.getImagePathList()) { pathList.add(path); }

        if(this.mixMode == BitmapHolderMixMode.MIXMODE_RANDOM) {
            ShakePathList();
        }

        currentIndex = 0;
        (new Thread(new Runnable() {
            @Override
            public void run() {
                int wrapperIndex = 0;
                while(wrapperIndex < 3 && currentIndex < pathList.size()) {
                    BitmapWrapper wrapper = LoadImageFromIndex(currentIndex);
                    if(wrapper != null) {
                        wrappers[wrapperIndex] = wrapper;
                        wrapperIndex++;
                    }
                    currentIndex++;
                }

                isLoading = false;
                if(bitmapHolderEventListener != null) {
                    bitmapHolderEventListener.onLoadComplete(isInit);
                }
            }
        })).start();
    }

    public void init(Context context) {
        this.context = context;
        screenSize = Util.getScreenSize(context);

        LoadImagesFromData(true);
    }

    public void active(boolean isChangeSettings) {
        if(isChangeSettings) {
            for(int i=0; i<wrappers.length; i++) {
                if(wrappers[i] != null) {
                    wrappers[i].getBitmap().recycle();
                    wrappers[i] = null;
                }
            }

            LoadImagesFromData(false);
        }
    }

    public void deactive() {

    }

    public void destroy() {
        for(int i=0; i<wrappers.length; i++) {
            if(wrappers[i] != null) {
                wrappers[i].getBitmap().recycle();
                wrappers[i] = null;
            }
        }
    }

    public BitmapWrapper getCurrentBitmap() {
        return wrappers[0];
    }

    public BitmapWrapper getNextBitmap() {
        return wrappers[1];
    }

    public boolean next() {
        if(isLoading) return false;

        if (wrappers[0] != null && wrappers[1] != null && wrappers[2] == null) {
            BitmapWrapper temp = wrappers[0];
            wrappers[0] = wrappers[1];
            wrappers[1] = temp;
        } else if (wrappers[0] != null && wrappers[1] != null && wrappers[2] != null) {
            BitmapWrapper temp = wrappers[0];
            wrappers[0] = wrappers[1];
            wrappers[1] = wrappers[2];
            wrappers[2] = null;

            isLoading = true;
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    int loopCount = 0;
                    BitmapWrapper wrapper = null;
                    while(wrapper == null) {
                        if(loopCount > pathList.size() * 2) break;

                        if (pathList.size() <= currentIndex) {
                            currentIndex = 0;
                        }

                        wrapper = LoadImageFromIndex(currentIndex);
                        currentIndex++;
                        loopCount++;
                    }
                    wrappers[2] = wrapper;

                    self.isLoading = false;
                }
            })).start();

            temp.getBitmap().recycle();
        }
        return true;
    }

    public boolean isLoadComplete() {
        return !isLoading;
    }
}
