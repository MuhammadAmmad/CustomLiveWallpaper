package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EffectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EffectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EffectFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private CheckBox checkBoxSlideImage = null;
    private Spinner spinnerSlideType = null;
    private SeekBar seekBarSlideSpeed = null;
    private CheckBox checkBoxEffect = null;
    private Spinner spinnerEffectType = null;
    private Switch switchUseRotate = null;
    private RadioButton radioRotateLeft = null;
    private RadioButton radioRotateRight = null;
    private SeekBar seekBarRotateSpeed  = null;
    private RadioButton radioMoveToLeftBottom = null;
    private RadioButton radioMoveToBottom = null;
    private RadioButton radioMoveToRightBottom = null;
    private RadioButton radioMoveToRight = null;
    private RadioButton radioMoveToRightTop = null;
    private RadioButton radioMoveToTop = null;
    private RadioButton radioMoveToLeftTop = null;
    private RadioButton radioMoveToLeft = null;
    private SeekBar seekBarMoveSpeed = null;
    private SeekBar seekBarMoveVibrate = null;
    private SeekBar seekBarSize = null;
    private SeekBar seekBarSizeVibrate = null;

    public EffectFragment() {
        // Required empty public constructor
    }

    public static EffectFragment newInstance(String param1, String param2) {
        EffectFragment fragment = new EffectFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        return inflater.inflate(R.layout.fragment_effect, container, false);
    }

    private void enableChildViews(ViewGroup group, View exceptView, boolean enable) {
        for(int i=0; i<group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if(view != exceptView) {
                view.setEnabled(enable);
            }
            if(view instanceof ViewGroup) {
                enableChildViews((ViewGroup)view, exceptView, enable);
            }
        }
    }

    private void ApplicationDataToViews() {
        checkBoxSlideImage.setChecked(ApplicationData.getIsEnableSlide());
        //spinnerSlideType.;
        seekBarSlideSpeed.setMax(10);
        seekBarSlideSpeed.setProgress(ApplicationData.getSlideSpeed());

        checkBoxEffect.setChecked(ApplicationData.getIsEnableEffect());
        //spinnerEffectType;
        switchUseRotate.setChecked(ApplicationData.getEffectIsUseRotate());
        if(ApplicationData.getEffectIsRotateRight()) {
            radioRotateRight.setChecked(true);
        } else {
            radioRotateLeft.setChecked(true);
        }
        seekBarRotateSpeed .setMax(10);
        seekBarRotateSpeed .setProgress(ApplicationData.getEffectRotateSpeed());
        switch(ApplicationData.getEffectMoveDirection()) {
            case UP:
                radioMoveToTop.setChecked(true);
                break;
            case DOWN:
                radioMoveToBottom.setChecked(true);
                break;
            case LEFT:
                radioMoveToLeft.setChecked(true);
                break;
            case RIGHT:
                radioMoveToRight.setChecked(true);
                break;
            case LEFT_DOWN:
                radioMoveToLeftBottom.setChecked(true);
                break;
            case LEFT_UP:
                radioMoveToLeftTop.setChecked(true);
                break;
            case RIGHT_DOWN:
                radioMoveToRightBottom.setChecked(true);
                break;
            case RIGHT_UP:
                radioMoveToRightTop.setChecked(true);
                break;
        }
        seekBarMoveSpeed.setMax(10);
        seekBarMoveSpeed.setProgress(ApplicationData.getEffectMoveSpeed());
        seekBarMoveVibrate.setMax(5);
        seekBarMoveVibrate.setProgress(ApplicationData.getEffectMoveVibrate());
        seekBarSize.setMax(10);
        seekBarSize.setProgress(ApplicationData.getEffectSize());
        seekBarSizeVibrate.setMax(5);
        seekBarSizeVibrate.setProgress(ApplicationData.getEffectSizeVibrate());
    }

    private void ApplicationDataFromViews() {
        ApplicationData.setIsEnableSlide(checkBoxSlideImage.isChecked());
        //spinnerSlideType = (Spinner)getView().findViewById(R.id.spinnerSlideType);
        ApplicationData.setSlideSpeed(seekBarSlideSpeed.getProgress());

        ApplicationData.setIsEnableEffect(checkBoxEffect.isChecked());
        //spinnerEffectType = (Spinner)getView().findViewById(R.id.spinnerEffectType);
        ApplicationData.setEffectIsUseRotate(switchUseRotate.isChecked());
        if(radioRotateLeft.isChecked()) ApplicationData.setEffectIsRotateRight(false);
        else if(radioRotateRight.isChecked()) ApplicationData.setEffectIsRotateRight(true);
        ApplicationData.setEffectRotateSpeed(seekBarRotateSpeed.getProgress());

        if(radioMoveToLeftBottom.isChecked()) ApplicationData.setEffectMoveDirection(ApplicationData.MoveDirection.LEFT_DOWN);
        else if(radioMoveToBottom.isChecked()) ApplicationData.setEffectMoveDirection(ApplicationData.MoveDirection.DOWN);
        else if(radioMoveToRightBottom.isChecked()) ApplicationData.setEffectMoveDirection(ApplicationData.MoveDirection.RIGHT_DOWN);
        else if(radioMoveToRight.isChecked()) ApplicationData.setEffectMoveDirection(ApplicationData.MoveDirection.RIGHT);
        else if(radioMoveToRightTop.isChecked()) ApplicationData.setEffectMoveDirection(ApplicationData.MoveDirection.RIGHT_UP);
        else if(radioMoveToTop.isChecked()) ApplicationData.setEffectMoveDirection(ApplicationData.MoveDirection.UP);
        else if(radioMoveToLeftTop.isChecked()) ApplicationData.setEffectMoveDirection(ApplicationData.MoveDirection.LEFT_UP);
        else if(radioMoveToLeft.isChecked()) ApplicationData.setEffectMoveDirection(ApplicationData.MoveDirection.LEFT);

        ApplicationData.setEffectMoveSpeed(seekBarMoveSpeed.getProgress());
        ApplicationData.setEffectMoveVibrate(seekBarMoveVibrate.getProgress());
        ApplicationData.setEffectSize(seekBarSize.getProgress());
        ApplicationData.setEffectSizeVibrate(seekBarSizeVibrate.getProgress());;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CheckBox.OnCheckedChangeListener disableCheckBoxChangeListener = new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ConstraintLayout layout = (ConstraintLayout)compoundButton.getParent();
                enableChildViews(layout, compoundButton, b);
            }
        };
        Spinner.OnItemSelectedListener spinnerChangedListener = new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ApplicationDataFromViews();
                ApplicationData.SaveEffects(getContext());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };
        SeekBar.OnSeekBarChangeListener seekBarChangedListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                ApplicationDataFromViews();
                ApplicationData.SaveEffects(getContext());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
        Switch.OnCheckedChangeListener switchChangedListener = new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ApplicationDataFromViews();
                ApplicationData.SaveEffects(getContext());
            }
        };
        RadioButton.OnCheckedChangeListener radioCheckedChangedListener = new RadioButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ApplicationDataFromViews();
                ApplicationData.SaveEffects(getContext());
            }
        };

        checkBoxSlideImage = (CheckBox)getView().findViewById(R.id.checkBoxSlideImage);
        checkBoxSlideImage.setOnCheckedChangeListener(disableCheckBoxChangeListener);
        enableChildViews((ViewGroup)checkBoxSlideImage.getParent(), checkBoxSlideImage, checkBoxSlideImage.isChecked());

        spinnerSlideType = (Spinner)getView().findViewById(R.id.spinnerSlideType);
        spinnerSlideType.setOnItemSelectedListener(spinnerChangedListener);

        seekBarSlideSpeed = (SeekBar)getView().findViewById(R.id.seekBarSlideSpeed);
        seekBarSlideSpeed.setOnSeekBarChangeListener(seekBarChangedListener);

        checkBoxEffect = (CheckBox)getView().findViewById(R.id.checkBoxEffect);
        checkBoxEffect.setOnCheckedChangeListener(disableCheckBoxChangeListener);
        enableChildViews((ViewGroup)checkBoxEffect.getParent(), checkBoxEffect, checkBoxEffect.isChecked());

        spinnerEffectType = (Spinner)getView().findViewById(R.id.spinnerEffectType);
        spinnerEffectType.setOnItemSelectedListener(spinnerChangedListener);

        switchUseRotate = (Switch)getView().findViewById(R.id.switchUseRotate);
        switchUseRotate.setOnCheckedChangeListener(switchChangedListener);

        radioRotateLeft = (RadioButton)getView().findViewById(R.id.radioRotateLeft);
        radioRotateLeft.setOnCheckedChangeListener(radioCheckedChangedListener);

        radioRotateRight = (RadioButton)getView().findViewById(R.id.radioRotateRight);
        radioRotateRight.setOnCheckedChangeListener(radioCheckedChangedListener);

        seekBarRotateSpeed  = (SeekBar)getView().findViewById(R.id.seekBarRotateSpeed);
        seekBarRotateSpeed.setOnSeekBarChangeListener(seekBarChangedListener);

        radioMoveToLeftBottom = (RadioButton)getView().findViewById(R.id.radioMoveToLeftBottom);
        radioMoveToLeftBottom.setOnCheckedChangeListener(radioCheckedChangedListener);

        radioMoveToBottom = (RadioButton)getView().findViewById(R.id.radioMoveToBottom);
        radioMoveToBottom.setOnCheckedChangeListener(radioCheckedChangedListener);

        radioMoveToRightBottom = (RadioButton)getView().findViewById(R.id.radioMoveToRightBottom);
        radioMoveToRightBottom.setOnCheckedChangeListener(radioCheckedChangedListener);

        radioMoveToRight = (RadioButton)getView().findViewById(R.id.radioMoveToRight);
        radioMoveToRight.setOnCheckedChangeListener(radioCheckedChangedListener);

        radioMoveToRightTop = (RadioButton)getView().findViewById(R.id.radioMoveToRightTop);
        radioMoveToRightTop.setOnCheckedChangeListener(radioCheckedChangedListener);

        radioMoveToTop = (RadioButton)getView().findViewById(R.id.radioMoveToTop);
        radioMoveToTop.setOnCheckedChangeListener(radioCheckedChangedListener);

        radioMoveToLeftTop = (RadioButton)getView().findViewById(R.id.radioMoveToLeftTop);
        radioMoveToLeftTop.setOnCheckedChangeListener(radioCheckedChangedListener);

        radioMoveToLeft = (RadioButton)getView().findViewById(R.id.radioMoveToLeft);
        radioMoveToLeft.setOnCheckedChangeListener(radioCheckedChangedListener);

        seekBarMoveSpeed = (SeekBar)getView().findViewById(R.id.seekBarMoveSpeed);
        seekBarMoveSpeed.setOnSeekBarChangeListener(seekBarChangedListener);

        seekBarMoveVibrate = (SeekBar)getView().findViewById(R.id.seekBarMoveVibrate);
        seekBarMoveVibrate.setOnSeekBarChangeListener(seekBarChangedListener);

        seekBarSize = (SeekBar)getView().findViewById(R.id.seekBarSize);
        seekBarSize.setOnSeekBarChangeListener(seekBarChangedListener);

        seekBarSizeVibrate = (SeekBar)getView().findViewById(R.id.seekBarSizeVibrate);
        seekBarSizeVibrate.setOnSeekBarChangeListener(seekBarChangedListener);

        ApplicationData.LoadEffects(getContext());
        ApplicationDataToViews();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
