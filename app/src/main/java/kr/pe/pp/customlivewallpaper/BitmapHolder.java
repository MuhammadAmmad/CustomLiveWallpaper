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

    private Context context = null;
    private BitmapHolderMixMode mixMode = null;
    private BitmapWrapper current = null;
    private BitmapWrapper next = null;
    private BitmapWrapper afternext = null;
    private ArrayList<String> pathList = new ArrayList<>();
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
        for(String path : ApplicationData.getImagePathList()) { pathList.add(path); }

        if(this.mixMode == BitmapHolderMixMode.MIXMODE_RANDOM) {
            ShakePathList();
        }

        (new Thread(new Runnable() {
            @Override
            public void run() {
                // load current image
                if(pathList.size() > 0) {
                    Bitmap bmp = Util.createBitmapFromPath(pathList.get(currentIndex), screenSize.getWidth(), screenSize.getHeight());
                    bmp = Util.resizeBitmapWithMargin(bmp, screenSize.getWidth(), screenSize.getHeight(), Util.ResizeMode.RESIZE_FIT_IMAGE, imageMargin, Util.CropMode.CROP_CENTER);
                    current = new BitmapWrapper(self.context, bmp);
                }

                // load next image
                if(pathList.size() > 1) {
                    currentIndex++;
                    Bitmap bmp = Util.createBitmapFromPath(pathList.get(currentIndex), screenSize.getWidth(), screenSize.getHeight());
                    bmp = Util.resizeBitmapWithMargin(bmp, screenSize.getWidth(), screenSize.getHeight(), Util.ResizeMode.RESIZE_FIT_IMAGE, imageMargin, Util.CropMode.CROP_CENTER);
                    next = new BitmapWrapper(self.context, bmp);
                }

                // load afternext image
                if(pathList.size() > 2) {
                    currentIndex++;
                    Bitmap bmp = Util.createBitmapFromPath(pathList.get(currentIndex), screenSize.getWidth(), screenSize.getHeight());
                    bmp = Util.resizeBitmapWithMargin(bmp, screenSize.getWidth(), screenSize.getHeight(), Util.ResizeMode.RESIZE_FIT_IMAGE, imageMargin, Util.CropMode.CROP_CENTER);
                    afternext = new BitmapWrapper(self.context, bmp);
                }
                isLoading = false;
            }
        })).start();
    }

    public void destroy() {
        if(afternext != null) {
            afternext.getBitmap().recycle();
        }
        if(next != null) {
            next.getBitmap().recycle();
        }
        if(current != null) {
            current.getBitmap().recycle();
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
                    Bitmap bmp = Util.createBitmapFromPath(pathList.get(currentIndex), screenSize.getWidth(), screenSize.getHeight());
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
