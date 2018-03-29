package kr.pe.pp.customlivewallpaper;

import android.app.Application;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class EchoActivity extends AppCompatActivity implements ImageManageFragment.ImageManageFragmentListener, EchoEffectFragment.OnFragmentInteractionListener {
    private ImageManageFragment imageManageFragment;
    private EchoEffectFragment echoEffectFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_echo);

        ApplicationData.Load(this.getApplicationContext());
        ApplicationData.setMode(ApplicationData.WallpaperMode.ECHO);
        ApplicationData.Save(this.getApplicationContext());

        startApplication();
    }

    private void showPreview() {
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(getBaseContext(), LiveWallpaperService.class));
        startActivityForResult(intent, Consts.WALLPAPER_CHANGED);
    }

    private void startApplication() {
        imageManageFragment = new ImageManageFragment();
        echoEffectFragment = new EchoEffectFragment();
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, echoEffectFragment).commit();
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
