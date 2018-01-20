package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ImageManageFragment extends Fragment {
    private ImageManageFragmentListener mListener;
    private ArrayList<String> _ImagePathList = new ArrayList<>();

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
        return inflater.inflate(R.layout.fragment_image_manage, container, false);
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

    public ArrayList<String> getImageList() {
        return _ImagePathList;
    }
    public void setImageList(ArrayList<String> list) {
        _ImagePathList = list;
    }

    public void onButtonAddClick(View view) {
    }

    public void onButtonRemoveClick(View view) {
    }

    public void onButtonClearClick(View view) {
    }

}
