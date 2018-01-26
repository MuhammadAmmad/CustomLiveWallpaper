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
    private SeekBar seekBarMoveSpeed = null;
    private SeekBar seekBarMoveVibrate = null;
    private SeekBar seekBarSize = null;
    private SeekBar seekBarSizeVibrate = null;

    private String[] slideValues = null;
    private String[] effectValues = null;

    public EffectFragment() {
        // Required empty public constructor
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

        for(int i=0; i<slideValues.length; i++) {
            if(slideValues[i] == ApplicationData.getSlideType()) {
                spinnerSlideType.setSelection(i);
                break;
            }
        }

        seekBarSlideSpeed.setMax(10);
        seekBarSlideSpeed.setProgress(ApplicationData.getSlideSpeed());

        checkBoxEffect.setChecked(ApplicationData.getIsEnableEffect());

        for(int i=0; i<effectValues.length; i++) {
            if(effectValues[i] == ApplicationData.getEffectParticleType()) {
                spinnerEffectType.setSelection(i);
            }
        }

        switchUseRotate.setChecked(ApplicationData.getEffectIsUseRotate());
        if(ApplicationData.getEffectIsRotateRight()) {
            radioRotateRight.setChecked(true);
        } else {
            radioRotateLeft.setChecked(true);
        }
        seekBarRotateSpeed .setMax(10);
        seekBarRotateSpeed .setProgress(ApplicationData.getEffectRotateSpeed());
        switch(ApplicationData.getEffectMoveDirection()) {
            case DOWN:
                radioMoveToBottom.setChecked(true);
                break;
            case LEFT_DOWN:
                radioMoveToLeftBottom.setChecked(true);
                break;
            case RIGHT_DOWN:
                radioMoveToRightBottom.setChecked(true);
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
        ApplicationData.setSlideType(slideValues[spinnerSlideType.getSelectedItemPosition()]);
        ApplicationData.setSlideSpeed(seekBarSlideSpeed.getProgress());

        ApplicationData.setIsEnableEffect(checkBoxEffect.isChecked());
        ApplicationData.setEffectParticleType(effectValues[spinnerEffectType.getSelectedItemPosition()]);
        ApplicationData.setEffectIsUseRotate(switchUseRotate.isChecked());
        if(radioRotateLeft.isChecked()) ApplicationData.setEffectIsRotateRight(false);
        else if(radioRotateRight.isChecked()) ApplicationData.setEffectIsRotateRight(true);
        ApplicationData.setEffectRotateSpeed(seekBarRotateSpeed.getProgress());

        if(radioMoveToLeftBottom.isChecked()) ApplicationData.setEffectMoveDirection(ApplicationData.MoveDirection.LEFT_DOWN);
        else if(radioMoveToBottom.isChecked()) ApplicationData.setEffectMoveDirection(ApplicationData.MoveDirection.DOWN);
        else if(radioMoveToRightBottom.isChecked()) ApplicationData.setEffectMoveDirection(ApplicationData.MoveDirection.RIGHT_DOWN);

        ApplicationData.setEffectMoveSpeed(seekBarMoveSpeed.getProgress());
        ApplicationData.setEffectMoveVibrate(seekBarMoveVibrate.getProgress());
        ApplicationData.setEffectSize(seekBarSize.getProgress());
        ApplicationData.setEffectSizeVibrate(seekBarSizeVibrate.getProgress());;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        slideValues = getResources().getStringArray(R.array.slide_value);
        effectValues = getResources().getStringArray(R.array.effect_value);

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
        spinnerSlideType = (Spinner)getView().findViewById(R.id.spinnerSlideType);
        seekBarSlideSpeed = (SeekBar)getView().findViewById(R.id.seekBarSlideSpeed);
        checkBoxEffect = (CheckBox)getView().findViewById(R.id.checkBoxEffect);
        spinnerEffectType = (Spinner)getView().findViewById(R.id.spinnerEffectType);
        switchUseRotate = (Switch)getView().findViewById(R.id.switchUseRotate);
        radioRotateLeft = (RadioButton)getView().findViewById(R.id.radioRotateLeft);
        radioRotateRight = (RadioButton)getView().findViewById(R.id.radioRotateRight);
        seekBarRotateSpeed  = (SeekBar)getView().findViewById(R.id.seekBarRotateSpeed);
        radioMoveToLeftBottom = (RadioButton)getView().findViewById(R.id.radioMoveToLeftBottom);
        radioMoveToBottom = (RadioButton)getView().findViewById(R.id.radioMoveToBottom);
        radioMoveToRightBottom = (RadioButton)getView().findViewById(R.id.radioMoveToRightBottom);
        seekBarMoveSpeed = (SeekBar)getView().findViewById(R.id.seekBarMoveSpeed);
        seekBarMoveVibrate = (SeekBar)getView().findViewById(R.id.seekBarMoveVibrate);
        seekBarSize = (SeekBar)getView().findViewById(R.id.seekBarSize);
        seekBarSizeVibrate = (SeekBar)getView().findViewById(R.id.seekBarSizeVibrate);

        ApplicationData.LoadEffects(getContext());
        ApplicationDataToViews();

        enableChildViews((ViewGroup)checkBoxSlideImage.getParent(), checkBoxSlideImage, checkBoxSlideImage.isChecked());
        enableChildViews((ViewGroup)checkBoxEffect.getParent(), checkBoxEffect, checkBoxEffect.isChecked());

        checkBoxSlideImage.setOnCheckedChangeListener(disableCheckBoxChangeListener);
        spinnerSlideType.setOnItemSelectedListener(spinnerChangedListener);
        seekBarSlideSpeed.setOnSeekBarChangeListener(seekBarChangedListener);
        checkBoxEffect.setOnCheckedChangeListener(disableCheckBoxChangeListener);
        spinnerEffectType.setOnItemSelectedListener(spinnerChangedListener);
        switchUseRotate.setOnCheckedChangeListener(switchChangedListener);
        radioRotateLeft.setOnCheckedChangeListener(radioCheckedChangedListener);
        radioRotateRight.setOnCheckedChangeListener(radioCheckedChangedListener);
        seekBarRotateSpeed.setOnSeekBarChangeListener(seekBarChangedListener);
        radioMoveToLeftBottom.setOnCheckedChangeListener(radioCheckedChangedListener);
        radioMoveToBottom.setOnCheckedChangeListener(radioCheckedChangedListener);
        radioMoveToRightBottom.setOnCheckedChangeListener(radioCheckedChangedListener);
        seekBarMoveSpeed.setOnSeekBarChangeListener(seekBarChangedListener);
        seekBarMoveVibrate.setOnSeekBarChangeListener(seekBarChangedListener);
        seekBarSize.setOnSeekBarChangeListener(seekBarChangedListener);
        seekBarSizeVibrate.setOnSeekBarChangeListener(seekBarChangedListener);

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
