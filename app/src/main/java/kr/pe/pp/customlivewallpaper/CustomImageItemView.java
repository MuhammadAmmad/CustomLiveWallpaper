package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by prgmmer on 2018-01-21.
 */

public class CustomImageItemView extends FrameLayout {
    ImageView imageView;
    ImageView imageViewCheckbox;

    public CustomImageItemView(@NonNull Context context) {
        super(context);
        Init(context);
    }

    public CustomImageItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public void Init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_image_item, this, true );

        imageView = (ImageView)findViewById(R.id.imageView);
        imageViewCheckbox = (ImageView)findViewById(R.id.imageViewCheckbox);
    }

    public void setImage(int resId) {
        imageView.setImageResource(resId);
    }
    public void setImage(Bitmap bmp) {
        imageView.setImageBitmap(bmp);
    }
    public boolean isChecked() {
        if(imageViewCheckbox.getVisibility() == GONE) {
            return false;
        } else {
            return true;
        }
    }
    public void setChecked(boolean checked) {
        if(checked) {
            imageViewCheckbox.setVisibility(VISIBLE);
        } else {
            imageViewCheckbox.setVisibility(GONE);
        }
    }
    public boolean toggle() {
        setChecked(!isChecked());
        return isChecked();
    }

    public void setAdjustViewBounds(boolean bounds) {
        //imageView.setAdjustViewBounds(bounds);
    }
}
