package kr.pe.pp.customlivewallpaper;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class ImageManageFragment extends Fragment {
    private ImageManageFragmentListener mListener;

    private static final int PICK_FROM_ALBUM = 101;

    public interface ImageManageFragmentListener {
        void onFragmentInteraction(Uri uri);
        void onChangeImageList(ArrayList<String> list);
    }

    public ImageManageFragment() {
        // Required empty public constructor
    }

    public static ImageManageFragment newInstance() {
        ImageManageFragment fragment = new ImageManageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("__Debug__", "ImageManageFragment::onCreateView");
        return inflater.inflate(R.layout.fragment_image_manage, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("__Debug__", "ImageManageFragment::onActivityCreated");

        final GridView gridView = (GridView)getView().findViewById(R.id.gridImageList);
        gridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        CustomImageGridAdapter adapter = new CustomImageGridAdapter(getActivity().getApplicationContext(), ApplicationData.getImagePathList());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                CustomImageItemView itemView = (CustomImageItemView)view;
                itemView.toggle();
                gridView.setItemChecked(position, itemView.isChecked());

                Log.d("__Debug__", "Set Item Checked : " + position + "(" + itemView.isChecked() + ") - " + gridView.getCheckedItemPositions().size());
            }
        });

        LoadSettings();

        getView().findViewById(R.id.buttonAdd).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("__Debug", "buttonAdd.onClick");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_PICK);
                getActivity().startActivityForResult(Intent.createChooser(intent,"Select Picture"), Consts.PICK_FROM_ALBUM);
            }
        });

        getView().findViewById(R.id.buttonRotate).setOnClickListener(new Button.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                Log.d("__Debug", "buttonRotate.onClick");
                SparseBooleanArray positions = gridView.getCheckedItemPositions();
                Log.d("__Debug__", "Rotate Count : " + positions.size());
                if(positions.size() > 0) {
                    CustomImageGridAdapter adapter = (CustomImageGridAdapter)gridView.getAdapter();
                    for(int i = gridView.getCount()-1; i >= 0; i--) {
                        if(positions.get(i)) {
                            Log.d("__Debug__", "Remove Index : " + i);
                            SaveImage saveImage = ApplicationData.getImagePathList().get(i);
                            Integer rotate = saveImage.getRotate() + 90;
                            if(rotate >= 360) rotate -= 360;
                            saveImage.setRotate(rotate);

                            Bitmap oldBitmap = adapter.getBitmap(saveImage.getPath());
                            Bitmap newBitmap = Util.createBitmapFromPath(saveImage.getPath(), gridView.getColumnWidth(), 160, rotate);
                            adapter.setBitmap(saveImage.getPath(), newBitmap);
                            oldBitmap.recycle();

                            gridView.setItemChecked(i, false);
                        }
                    }
                }
                SaveSettings();
                RefreshControls();
            }
        });

        getView().findViewById(R.id.buttonDelete).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray positions = gridView.getCheckedItemPositions();
                Log.d("__Debug__", "Remove Count : " + positions.size());
                if(positions.size() > 0) {
                    for(int i = gridView.getCount()-1; i >= 0; i--) {
                        if(positions.get(i)) {
                            Log.d("__Debug__", "Remove Index : " + i);
                            ApplicationData.getImagePathList().remove(i);
                            gridView.setItemChecked(i, false);
                        }
                    }
                }
                SaveSettings();
                RefreshControls();
            }
        });

        getView().findViewById(R.id.buttonClear).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationData.getImagePathList().clear();
                SaveSettings();
                RefreshControls();
            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ImageManageFragmentListener) {
            mListener = (ImageManageFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void LoadSettings() {
        // load uri list
        ApplicationData.Load(getContext());

        // refresh image grid
        RefreshControls();
    }

    private void SaveSettings() {
        // refresh image grid
        RefreshControls();

        // save uri list
        ApplicationData.Save(getContext());
    }

    private void RefreshControls() {
        GridView gridView = (GridView)getView().findViewById(R.id.gridImageList);
        CustomImageGridAdapter adapter = (CustomImageGridAdapter)gridView.getAdapter();
        adapter.notifyDataSetChanged();
        gridView.invalidateViews();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onResultPickFromAlbum(Intent data) {
        Log.d("__Debug__", "onResultPickFromAlbum");
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
        String realPath = RealPathUtil.getRealPath(getActivity().getApplicationContext(), uri);
        Log.d("__Debug__", "Path : " + path);
        Log.d("__Debug__", "Real Path : " + realPath);
        if(!ApplicationData.getImagePathList().contains(realPath)) {
            Log.d("__Debug__", "Add Real Path : " + realPath);
            ApplicationData.getImagePathList().add(new SaveImage(realPath, 0));
        }
    }

}
