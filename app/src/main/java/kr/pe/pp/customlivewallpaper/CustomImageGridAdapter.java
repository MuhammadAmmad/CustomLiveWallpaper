package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by prgmmer on 2018-01-20.
 */

public class CustomImageGridAdapter extends BaseAdapter {

    private ArrayList<String> _ImagePathList = new ArrayList<>();
    private Context _Context = null;

    public CustomImageGridAdapter(Context context, ArrayList<String> list) {
        _Context = context;
        _ImagePathList = list;
    }

    @Override
    public int getCount() {
        return _ImagePathList.size();
    }

    @Override
    public Object getItem(int i) {
        return _ImagePathList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String filePath = _ImagePathList.get(i);
        Log.d("__Debug__", "CustomImageGridAdapter::getView(" + i + ") - " + _ImagePathList.get(i));

        if(view == null) {
            view = new ImageView(_Context);
        }

        Bitmap bmp = BitmapFactory.decodeFile(filePath);
        ((ImageView)view).setAdjustViewBounds(true);
        ((ImageView)view).setImageBitmap(bmp);

        return view;
    }
}
