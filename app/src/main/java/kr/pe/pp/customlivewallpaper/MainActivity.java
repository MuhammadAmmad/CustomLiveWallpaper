package kr.pe.pp.customlivewallpaper;

import android.Manifest;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.service.wallpaper.WallpaperService;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    MainActivity _MainActivity = null;
    ArrayList<String> _ImagePathList = new ArrayList<String>();

    private static final int WALLPAPER_CHANGED = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private static final int CROP_FROM_CAMERA = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _MainActivity = this;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        } else {
            startApplication();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                // ...
            }
            requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
        }
        else
        {
            startApplication();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1)
        {
            if (grantResults.length > 0)
            {
                for (int i=0; i<grantResults.length; ++i)
                {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                        // 하나라도 거부한다면.
                        new AlertDialog.Builder(this).setTitle("알림").setMessage("권한을 허용해주셔야 앱을 이용할 수 있습니다.")
                                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        _MainActivity.finish();
                                    }
                                }).setNegativeButton("권한 설정", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                getApplicationContext().startActivity(intent);
                            }
                        }).setCancelable(false).show();

                        return;
                    }
                }
                startApplication();
            }
        }
    }

    private void startApplication() {
        /* 저장데이터 로드 */
        LoadSettings();

        /* 초기화 */
        InitControls();

        /* 라이브 배경화면 미리보기 */
        Button btnStartService = (Button)findViewById(R.id.btnStartService);
        btnStartService.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        new ComponentName(getBaseContext(), LiveWallpaperService.class));
                startActivityForResult(intent, WALLPAPER_CHANGED);
            }
        });

        /* 앨범사진 선택 */
        Button btnSelectAlbum = (Button)findViewById(R.id.btnSelectAlbum);
        btnSelectAlbum.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_FROM_ALBUM);
            }
        });

        /* 앨범사진 클리어 */
        Button btnClearSelectedAlbum = (Button)findViewById(R.id.btnClearSelectedAlbum);
        btnClearSelectedAlbum.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                _ImagePathList.clear();
                SaveSettings();
                InitControls();
            }
        });

        /* 이미지뷰 클릭 이벤트 */
        ImageView imageViewPreview = (ImageView)findViewById(R.id.imageViewPreview);
        imageViewPreview.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(_MainActivity, ImageListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void LoadSettings() {
        // load uri list
        _ImagePathList.clear();
        SharedPreferences pref = getSharedPreferences("ImagePathList", MODE_PRIVATE);
        Collection<?> col =  pref.getAll().values();
        Iterator<?> it = col.iterator();
        while(it.hasNext())
        {
            String imagePath = (String)it.next();
            _ImagePathList.add(imagePath);
            Log.d("__Debug__", "Load - " + imagePath);
        }

        // refresh image view and image count
        InitControls();
    }

    private void SaveSettings() {
        // refresh image view and image count
        InitControls();

        // save uri list
        SharedPreferences pref = getSharedPreferences("ImagePathList", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        for(int i=0; i<_ImagePathList.size(); i++) {
            editor.putString(Integer.toString(i), _ImagePathList.get(i));
            Log.d("__Debug__", "Save - " + Integer.toString(i) + ":" + _ImagePathList.get(i));
        }
        editor.commit();
    }

    private void InitControls() {
        if(_ImagePathList.size() > 0) {
            //MediaStore.Images.Thumbnails.get
            Bitmap bmp = null;

            File imgFile = new File(_ImagePathList.get(0));
            if(imgFile.exists()){
                bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }

            ImageView imageViewPreview = (ImageView)findViewById(R.id.imageViewPreview);
            imageViewPreview.setImageBitmap(bmp);
        } else {
        }
        TextView labelSelectedImage = (TextView)findViewById(R.id.labelSelectedImage);
        labelSelectedImage.setText(getResources().getString(R.string.label_selected_image, _ImagePathList.size()));
    }

    private void onResultWallpaperChanged(Intent data) {
        new AlertDialog.Builder(this)
                .setTitle("알림")
                .setMessage("변경되었습니다.")
                .show();

        SaveSettings();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void onResultPickFromAlbum(Intent data) {
        if(data.getClipData() != null) {
            ClipData clip = data.getClipData();
            for(int i=0; i<clip.getItemCount(); i++) {
                ClipData.Item item = clip.getItemAt(i);
                addImagePathToList(item.getUri());
            }
        } else if(data.getData() != null) {
            addImagePathToList(data.getData());
        }

        SaveSettings();
    }
    private void addImagePathToList(Uri uri) {
        String path = uri.getPath();
        String realPath = RealPathUtil.getRealPath(getApplicationContext(), uri);
        Log.d("__Debug__", "Path : " + path);
        Log.d("__Debug__", "Real Path : " + realPath);
        if(!_ImagePathList.contains(realPath)) {
            Log.d("__Debug__", "Add Real Path : " + realPath);
            _ImagePathList.add(realPath);
        }
    }

    private void onResultPickFromCamera(Intent data) {

    }

    private void onResultCropFromCamera(Intent data) {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            return;
        }

        switch(requestCode) {
            case WALLPAPER_CHANGED:
                onResultWallpaperChanged(data);
                break;

            case PICK_FROM_ALBUM:
                onResultPickFromAlbum(data);
                break;

            case PICK_FROM_CAMERA:
                onResultPickFromCamera(data);
                break;

            case CROP_FROM_CAMERA:
                onResultCropFromCamera(data);
                break;
        }
    }
}


/*
Intent intent = new Intent(MainActivity.this, LiveWallpaperService.class );
if (Build.VERSION.SDK_INT > 15)
{
    intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
    String pkg = WallpaperService.class.getPackage().getName();
    String cls = WallpaperService.class.getCanonicalName();
    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(pkg, cls));
}
else
{
    intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
}
startService(intent);
*/

/*

package pe.kr.theeye.cameracrop;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class CameraCropActivity extends Activity implements OnClickListener
{
  private static final int PICK_FROM_CAMERA = 0;
  private static final int PICK_FROM_ALBUM = 1;
  private static final int CROP_FROM_CAMERA = 2;

  private Uri mImageCaptureUri;
  private ImageView mPhotoImageView;
  private Button mButton;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    mButton = (Button) findViewById(R.id.button);
    mPhotoImageView = (ImageView) findViewById(R.id.image);

    mButton.setOnClickListener(this);
  }

// 카메라에서 이미지 가져오기
private void doTakePhotoAction()
{

    // 참고 해볼곳
    // http://2009.hfoss.org/Tutorial:Camera_and_Gallery_Demo
    // http://stackoverflow.com/questions/1050297/how-to-get-the-url-of-the-captured-image
    // http://www.damonkohler.com/2009/02/android-recipes.html
    // http://www.firstclown.us/tag/android/

    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    // 임시로 사용할 파일의 경로를 생성
    String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
    // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
    //intent.putExtra("return-data", true);
    startActivityForResult(intent, PICK_FROM_CAMERA);
}

    // 앨범에서 이미지 가져오기
    private void doTakeAlbumAction()
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != RESULT_OK)
        {
            return;
        }

        switch(requestCode)
        {
            case CROP_FROM_CAMERA:
            {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                final Bundle extras = data.getExtras();

                if(extras != null)
                {
                    Bitmap photo = extras.getParcelable("data");
                    mPhotoImageView.setImageBitmap(photo);
                }

                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists())
                {
                    f.delete();
                }

                break;
            }

            case PICK_FROM_ALBUM:
            {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.

                mImageCaptureUri = data.getData();
            }

            case PICK_FROM_CAMERA:
            {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 90);
                intent.putExtra("outputY", 90);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);

                break;
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                doTakePhotoAction();
            }
        };

        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                doTakeAlbumAction();
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(this)
                .setTitle("업로드할 이미지 선택")
                .setPositiveButton("사진촬영", cameraListener)
                .setNeutralButton("앨범선택", albumListener)
                .setNegativeButton("취소", cancelListener)
                .show();
    }}

 */