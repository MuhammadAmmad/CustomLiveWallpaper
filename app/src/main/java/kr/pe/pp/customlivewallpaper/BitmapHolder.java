package kr.pe.pp.customlivewallpaper;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

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

    private boolean isNext = false;

    public void applyChanges() {
        if (isNext) {
            nextProcess();
            isNext = false;
        }
    }

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
        ApplicationData.Load(context);

        pathList.clear();
        for(String path : ApplicationData.getImagePathList()) { pathList.add(path); }

        if(this.mixMode == BitmapHolderMixMode.MIXMODE_RANDOM) {
            ShakePathList();
        }

        // load current image
        if(pathList.size() > 0) {
            Bitmap bmp = Util.createBitmapFromPath(pathList.get(currentIndex), screenSize.getWidth(), screenSize.getHeight());
            bmp = Util.resizeBitmapWithMargin(bmp, screenSize.getWidth(), screenSize.getHeight(), Util.ResizeMode.RESIZE_FIT_IMAGE, imageMargin, Util.CropMode.CROP_CENTER);
            current = new BitmapWrapper(context, bmp);
        }

        // load next image
        if(pathList.size() > 1) {
            currentIndex++;
            Bitmap bmp = Util.createBitmapFromPath(pathList.get(currentIndex), screenSize.getWidth(), screenSize.getHeight());
            bmp = Util.resizeBitmapWithMargin(bmp, screenSize.getWidth(), screenSize.getHeight(), Util.ResizeMode.RESIZE_FIT_IMAGE, imageMargin, Util.CropMode.CROP_CENTER);
            next = new BitmapWrapper(context, bmp);
        }

        // load afternext image
        if(pathList.size() > 2) {
            currentIndex++;
            Bitmap bmp = Util.createBitmapFromPath(pathList.get(currentIndex), screenSize.getWidth(), screenSize.getHeight());
            bmp = Util.resizeBitmapWithMargin(bmp, screenSize.getWidth(), screenSize.getHeight(), Util.ResizeMode.RESIZE_FIT_IMAGE, imageMargin, Util.CropMode.CROP_CENTER);
            afternext = new BitmapWrapper(context, bmp);
        }
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

    private void nextProcess() {
        if (pathList.size() == 2) {
            BitmapWrapper temp = current;
            current = next;
            next = temp;
        } else if (pathList.size() > 2) {
            BitmapWrapper temp = current;
            current = next;
            next = afternext;

            currentIndex++;
            if (pathList.size() <= currentIndex) {
                currentIndex = 0;
            }
            Bitmap bmp = Util.createBitmapFromPath(pathList.get(currentIndex), screenSize.getWidth(), screenSize.getHeight());
            bmp = Util.resizeBitmapWithMargin(bmp, screenSize.getWidth(), screenSize.getHeight(), Util.ResizeMode.RESIZE_FIT_IMAGE, imageMargin, Util.CropMode.CROP_CENTER);
            afternext = new BitmapWrapper(this.context, bmp);

            temp.getBitmap().recycle();
        }
    }
    public void next() {
        isNext = true;
    }
}
