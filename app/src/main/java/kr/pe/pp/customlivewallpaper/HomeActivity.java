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
                        new AlertDialog.Builder(this).setTitle(R.string.message_alert).setMessage(R.string.message_must_appr_permit)
                                .setPositiveButton(R.string.message_exit, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        _HomeActivity.finish();
                                    }
                                }).setNegativeButton(R.string.set_permission, new DialogInterface.OnClickListener() {
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

    private void showPreview() {
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(getBaseContext(), LiveWallpaperService.class));
        startActivityForResult(intent, Consts.WALLPAPER_CHANGED);
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
                /*
                final WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
                WallpaperInfo info = manager.getWallpaperInfo();
                if(info != null && info.getPackageName().equals(getApplicationContext().getPackageName())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setMessage("라이브 배경화면이 실행중입니다.\n변경사항이 모두 반영되길 원하시면 기존 배경화면을 중지후 재시작 하여야합니다.\n중지후 미리보기를 실행하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        manager.clear();
                                        Toast.makeText(getApplicationContext(), R.string.message_clear_wallpaper, Toast.LENGTH_LONG).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    showPreview();
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showPreview();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    showPreview();
                }
                */
                showPreview();
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
                        Toast.makeText(getApplicationContext(), R.string.message_clear_wallpaper, Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), R.string.message_apply_wallpaper, Toast.LENGTH_LONG).show();
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
