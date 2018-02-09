package kr.pe.pp.customlivewallpaper;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2018-01-22.
 */

public class Util {

    public enum ResizeMode {
        RESIZE_FIT_CANVAS(1),
        RESIZE_FIT_IMAGE(2);

        private int value;
        private ResizeMode(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    public enum CropMode {
        CROP_CENTER(1),
        CROP_CENTER_TOP(2),
        CROP_CENTER_BOTTOM(3),
        CROP_LEFT_CENTER(4),
        CROP_LEFT_TOP(5),
        CROP_LEFT_BOTTOM(6),
        CROP_RIGHT_CENTER(7),
        CROP_RIGHT_TOP(8),
        CROP_RIGHT_BOTTOM(9);

        private int value;
        private CropMode(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }


    public static class Size {
        private int width;
        private int height;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return this.width;
        }
        public void setWidth(int width) {
            this.width = width;
        }
        public int getHeight() {
            return this.height;
        }
        public void setHeight(int height) {
            this.height = height;
        }
    }

    public static Util.Size getScreenSize(Context context) {
        WindowManager window = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        return new Util.Size(width, height);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap createBitmapFromPath(String filePath, int width, int height, int rotate) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        Log.d("__Debug__", "CreateBitmapFromPath : Origin Size(" + options.outWidth + ", " + options.outHeight + ")");
        Log.d("__Debug__", "CreateBitmapFromPath : Dest Size(" + width + ", " + height + ")");

        boolean isRotate = (rotate == 90 || rotate == 270 ? true : false);
        if(isRotate) {
            options.inSampleSize = Util.calculateInSampleSize(options, height, width);
        } else {
            options.inSampleSize = Util.calculateInSampleSize(options, width, height);
        }
        Log.d("__Debug__", "CreateBitmapFromPath : SampleSize(" + options.inSampleSize + ")");

        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        Log.d("__Debug__", "CreateBitmapFromPath : Result Size(" + bmp.getWidth() + ", " + bmp.getHeight() + ")");
        if(isRotate) {
            Matrix matrix = new Matrix();
            matrix.reset();
            matrix.setRotate(rotate);
            Bitmap bmpRotate = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            bmp.recycle();
            return bmpRotate;
        } else {
            return bmp;
        }
    }

    public static Bitmap resizeBitmapWithMargin(Bitmap bmp, int width, int height) {
        return resizeBitmapWithMargin(bmp, width, height, ResizeMode.RESIZE_FIT_IMAGE, 0, null);
    }

    public static Bitmap resizeBitmapWithMargin(Bitmap bmp, int width, int height, ResizeMode mode) {
        return resizeBitmapWithMargin(bmp, width, height, mode, 0, null);
    }

    public static Bitmap resizeBitmapWithMargin(Bitmap bmp, int width, int height, ResizeMode mode, int margin) {
        return resizeBitmapWithMargin(bmp, width, height, mode, margin, null);
    }

    public static Bitmap resizeBitmapWithMargin(Bitmap bmp, int width, int height, ResizeMode mode, int margin, CropMode crop) {
        Bitmap bitmap = null;
        int srcWidth = bmp.getWidth();
        int srcHeight = bmp.getHeight();
        float srcRatio = (float)srcWidth / (float)srcHeight;
        int destWidth = width + (margin*2);
        int destHeight = height + (margin*2);
        float destRatio = (float)destWidth / (float)destHeight;
        int w = 0, h = 0;

        if(mode == ResizeMode.RESIZE_FIT_CANVAS) {
            // 캔버스내에 이미지 전체가 보이도록 표시
            if(destRatio > srcRatio) {
                // 높이로 맞춤
                h = destHeight;
                w = (int)(srcWidth * ((float)destHeight / (float)srcHeight));
            } else {
                // 넓이로 맞춤
                h = (int)(srcHeight * ((float)destWidth / (float)srcWidth));
                w = destWidth;
            }
        } else if(mode == ResizeMode.RESIZE_FIT_IMAGE) {
            // 이미지를 캔버스에 꽉채워 공백이 없도록 표시
            if(destRatio > srcRatio) {
                // 넓이로 맞춤
                h = (int)(srcHeight * ((float)destWidth / (float)srcWidth));
                w = destWidth;
            } else {
                // 높이로 맞춤
                h = destHeight;
                w = (int)(srcWidth * ((float)destHeight / (float)srcHeight));
            }
        }

        Log.d("__Debug__","resizeBitmapWithMargin - width(" + width + "), height(" + height + "), margin(" + margin + ")");
        Log.d("__Debug__","resizeBitmapWithMargin - destWidth(" + destWidth + "), destHeight(" + destHeight + ")");
        Log.d("__Debug__","resizeBitmapWithMargin - w(" + w + "), h(" + h + ")");
        bitmap = Bitmap.createScaledBitmap(bmp, w, h, true);
        if(crop != null && mode == ResizeMode.RESIZE_FIT_IMAGE) {
            Bitmap bitmapCrop = cropBitmap(bitmap, destWidth, destHeight, crop);
            bitmap.recycle();
            return bitmapCrop;
        }
        return bitmap;
    }

    public static Bitmap cropBitmap(Bitmap src, int width, int height, CropMode cropMode) {
        int cropX = 0, cropY = 0, cropWidth = width, cropHeight = height;
        switch(cropMode) {
            case CROP_CENTER: {
                cropX = (src.getWidth() - width) / 2;
                cropY = (src.getHeight() - height) / 2;
                break;
            }
            case CROP_CENTER_TOP: {
                cropX = (src.getWidth() - width) / 2;
                cropY = 0;
                break;
            }
            case CROP_CENTER_BOTTOM: {
                cropX = (src.getWidth() - width) / 2;
                cropY = src.getHeight() - height;
                break;
            }
            case CROP_LEFT_CENTER: {
                cropX = 0;
                cropY = (src.getHeight() - height) / 2;
                break;
            }
            case CROP_LEFT_TOP: {
                cropX = 0;
                cropY = 0;
                break;
            }
            case CROP_LEFT_BOTTOM: {
                cropX = 0;
                cropY = src.getHeight() - height;
                break;
            }
            case CROP_RIGHT_CENTER: {
                cropX = src.getWidth() - width;
                cropY = (src.getHeight() - height) / 2;
                break;
            }
            case CROP_RIGHT_TOP: {
                cropX = src.getWidth() - width;
                cropY = 0;
                break;
            }
            case CROP_RIGHT_BOTTOM: {
                cropX = src.getWidth() - width;
                cropY = src.getHeight() - height;
                break;
            }
        }
        Log.d("__Debug__", "Crop(" + cropX + ", " + cropY + ", " + cropWidth + ", " + cropHeight + ")");
        return Bitmap.createBitmap(src, cropX, cropY, cropWidth, cropHeight);
    }

    public static int getResourceId(String defType, String resName, Context context) {

        Context resContext = null;
        try {
            resContext = context.createPackageContext(context.getPackageName(), 0);
        } catch(PackageManager.NameNotFoundException ex) {
            return -1;
        }

        Resources res = resContext.getResources();

        int id = res.getIdentifier(resName, defType, context.getPackageName());
        return id;
    }

    public static int getResourceId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            //Log.d("__ServiceList__", service.service.getClassName());
            if (serviceName.equals(service.service.getClassName())) {
                Log.d("__IsServiceRunning__", serviceName + " - running");
                return true;
            }
        }
        Log.d("__IsServiceRunning__", serviceName + " - not running");
        return false;
    }

    public static boolean isRunningWallpaper(Context context) {
        WallpaperManager wpm = WallpaperManager.getInstance(context);
        WallpaperInfo info = wpm.getWallpaperInfo();

        if(info != null && info.getPackageName().equals(context.getPackageName())) {
            return true;
        } else {
            return false;
        }
    }
}
