package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by prgmmer on 2018-01-20.
 */

public class CustomImageGridAdapter extends BaseAdapter {

    private ArrayList<String> _ImagePathList = new ArrayList<>();
    private Map<String, Bitmap> _Bitmaps = new HashMap<String, Bitmap>();
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String filePath = _ImagePathList.get(i);
        GridView gridView = (GridView)viewGroup;

        if(view == null) {
            view = new CustomImageItemView(_Context);
        }

        Bitmap bmp = null;
        if(_Bitmaps.containsKey(filePath)) {
            bmp = _Bitmaps.get(filePath);
        } else {
            bmp = Util.createBitmapFromPath(filePath, gridView.getColumnWidth(), 160);
            _Bitmaps.put(filePath, bmp);
        }
        ((CustomImageItemView)view).setImage(bmp);
        ((CustomImageItemView)view).setChecked(gridView.getCheckedItemPositions().get(i));

        return view;
    }
}
