package kr.pe.pp.customlivewallpaper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements ImageManageFragment.ImageManageFragmentListener, EffectFragment.OnFragmentInteractionListener {
    HomeActivity _HomeActivity = null;

    private ImageManageFragment imageManageFragment;
    private EffectFragment effectFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        _HomeActivity = this;

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
                                        _HomeActivity.finish();
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
        imageManageFragment = new ImageManageFragment();
        effectFragment = new EffectFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageManageFragment).commit();

        TextView textImageManage = (TextView)findViewById(R.id.text_image_manage);
        textImageManage.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageManageFragment).commit();
            }
        });

        TextView textEffect = (TextView)findViewById(R.id.text_effect);
        textEffect.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, effectFragment).commit();
            }
        });

        TextView textPreview = (TextView)findViewById(R.id.text_preview);
        textPreview.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        new ComponentName(getBaseContext(), LiveWallpaperService.class));
                startActivityForResult(intent, Consts.WALLPAPER_CHANGED);
            }
        });

        TextView textStop = (TextView)findViewById(R.id.text_stop);
        textStop.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
                WallpaperInfo info = manager.getWallpaperInfo();
                if(info != null && info.getPackageName().equals(getApplicationContext().getPackageName())) {
                    try {
                        manager.clear();
                        Toast.makeText(getApplicationContext(), "지정된 배경화면을 해제 하였습니다.", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("__Debug", "onActivityResult");
        if(resultCode != RESULT_OK) {
            return;
        }

        Log.d("__Debug", "onActivityResult : requestCode = " + requestCode);
        switch(requestCode) {
            case Consts.WALLPAPER_CHANGED:
                Toast.makeText(getApplicationContext(), "배경화면이 적용되었습니다.", Toast.LENGTH_LONG).show();
                //onResultWallpaperChanged(data);
                break;

            case Consts.PICK_FROM_ALBUM:
                imageManageFragment.onResultPickFromAlbum(data);
                break;

            case Consts.PICK_FROM_CAMERA:
                //onResultPickFromCamera(data);
                break;

            case Consts.CROP_FROM_CAMERA:
                //onResultCropFromCamera(data);
                break;

        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onChangeImageList(ArrayList<String> list) {
    }
}
