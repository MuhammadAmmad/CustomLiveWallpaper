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
        void onLoadComplete();
    }
    private BitmapHolderEventListener bitmapHolderEventListener = null;
    public void setBitmapHolderEventListener(BitmapHolderEventListener listener) {
        this.bitmapHolderEventListener = listener;
    }

    private Context context = null;
    private BitmapHolderMixMode mixMode = null;
    private BitmapWrapper current = null;
    private BitmapWrapper next = null;
    private BitmapWrapper afternext = null;
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

    public void init(Context context) {
        this.context = context;
        screenSize = Util.getScreenSize(context);

        pathList.clear();
        for(SaveImage path : ApplicationData.getImagePathList()) { pathList.add(path); }

        if(this.mixMode == BitmapHolderMixMode.MIXMODE_RANDOM) {
            ShakePathList();
        }

        currentIndex = 0;
        (new Thread(new Runnable() {
            @Override
            public void run() {
                // load current image
                if(pathList.size() > 0) {
                    Bitmap bitmap = Util.createBitmapFromPath(pathList.get(currentIndex).getPath(), screenSize.getWidth(), screenSize.getHeight(), pathList.get(currentIndex).getRotate());
                    Bitmap bmp = Util.resizeBitmapWithMargin(bitmap, screenSize.getWidth(), screenSize.getHeight(), Util.ResizeMode.RESIZE_FIT_IMAGE, imageMargin, Util.CropMode.CROP_CENTER);
                    current = new BitmapWrapper(self.context, bmp);
                    bitmap.recycle();
                }

                // load next image
                if(pathList.size() > 1) {
                    currentIndex++;
                    Bitmap bitmap = Util.createBitmapFromPath(pathList.get(currentIndex).getPath(), screenSize.getWidth(), screenSize.getHeight(), pathList.get(currentIndex).getRotate());
                    Bitmap bmp = Util.resizeBitmapWithMargin(bitmap, screenSize.getWidth(), screenSize.getHeight(), Util.ResizeMode.RESIZE_FIT_IMAGE, imageMargin, Util.CropMode.CROP_CENTER);
                    next = new BitmapWrapper(self.context, bmp);
                    bitmap.recycle();
                }

                // load afternext image
                if(pathList.size() > 2) {
                    currentIndex++;
                    Bitmap bitmap = Util.createBitmapFromPath(pathList.get(currentIndex).getPath(), screenSize.getWidth(), screenSize.getHeight(), pathList.get(currentIndex).getRotate());
                    Bitmap bmp = Util.resizeBitmapWithMargin(bitmap, screenSize.getWidth(), screenSize.getHeight(), Util.ResizeMode.RESIZE_FIT_IMAGE, imageMargin, Util.CropMode.CROP_CENTER);
                    afternext = new BitmapWrapper(self.context, bmp);
                    bitmap.recycle();
                }
                isLoading = false;
                if(bitmapHolderEventListener != null) {
                    bitmapHolderEventListener.onLoadComplete();
                }
            }
        })).start();
    }

    public void destroy() {
        if(afternext != null) {
            afternext.getBitmap().recycle();
            afternext = null;
        }
        if(next != null) {
            next.getBitmap().recycle();
            next = null;
        }
        if(current != null) {
            current.getBitmap().recycle();
            current = null;
        }
    }

    public BitmapWrapper getCurrentBitmap() {
        return current;
    }

    public BitmapWrapper getNextBitmap() {
        return next;
    }

    public void next() {
        if (pathList.size() == 2) {
            BitmapWrapper temp = current;
            current = next;
            next = temp;
        } else if (pathList.size() > 2) {
            BitmapWrapper temp = current;
            current = next;
            next = afternext;
            afternext = null;

            currentIndex++;
            if (pathList.size() <= currentIndex) {
                currentIndex = 0;
            }

            isLoading = true;
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bmp = Util.createBitmapFromPath(pathList.get(currentIndex).getPath(), screenSize.getWidth(), screenSize.getHeight(), pathList.get(currentIndex).getRotate());
                    bmp = Util.resizeBitmapWithMargin(bmp, screenSize.getWidth(), screenSize.getHeight(), Util.ResizeMode.RESIZE_FIT_IMAGE, imageMargin, Util.CropMode.CROP_CENTER);
                    self.afternext = new BitmapWrapper(self.context, bmp);
                    self.isLoading = false;
                }
            })).start();

            temp.getBitmap().recycle();
        }
    }

    public boolean isLoadComplete() {
        return !isLoading;
    }
}
