package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
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


public class EchoEffectFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Switch switchUseSlide = null;
    private Spinner spinnerSlideType = null;
    private Spinner spinnerSlideTime = null;
    private SeekBar seekBarSlideSpeed = null;
    private SeekBar seekBarSlideDelay = null;

    private String[] slideTypeValues = null;
    private String[] slideTimeValues = null;

    public EchoEffectFragment() {
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
        return inflater.inflate(R.layout.fragment_echo_effect, container, false);
    }

    private void enableChildViews(ViewGroup group, View exceptView, boolean enable) {
        enableChildViews(group, exceptView, enable, true);
    }
    private void enableChildViews(ViewGroup group, View exceptView, boolean enable, boolean main) {
        for(int i=0; i<group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if(view != exceptView) {
                view.setEnabled(enable);
            }
            if(view instanceof ViewGroup) {
                enableChildViews((ViewGroup)view, exceptView, enable, false);
            }
        }
        if(main && enable) {
            //radioRotateLeft.setEnabled(switchUseRotate.isChecked());
            //radioRotateRight.setEnabled(switchUseRotate.isChecked());
            //seekBarRotateSpeed.setEnabled(switchUseRotate.isChecked());
        }
    }

    private void ApplicationDataToViews() {
        switchUseSlide.setChecked(ApplicationData.getIsEnableSlide());

        for(int i=0; i<slideTypeValues.length; i++) {
            if(slideTypeValues[i].equals(ApplicationData.getSlideType())) {
                spinnerSlideType.setSelection(i);
                break;
            }
        }

        for(int i=0; i<slideTimeValues.length; i++) {
            if(slideTimeValues[i].equals(ApplicationData.getSlideTime())) {
                spinnerSlideTime.setSelection(i);
                break;
            }
        }

        seekBarSlideSpeed.setMax(10);
        seekBarSlideSpeed.setProgress(ApplicationData.getSlideSpeed());

        seekBarSlideDelay.setMax(30);
        seekBarSlideDelay.setProgress(ApplicationData.getSlideDelay());
    }

    private void ApplicationDataFromViews() {
        ApplicationData.setIsEnableSlide(switchUseSlide.isChecked());
        ApplicationData.setSlideType(slideTypeValues[spinnerSlideType.getSelectedItemPosition()]);
        ApplicationData.setSlideTime(slideTimeValues[spinnerSlideTime.getSelectedItemPosition()]);
        ApplicationData.setSlideSpeed(seekBarSlideSpeed.getProgress());
        ApplicationData.setSlideDelay(seekBarSlideDelay.getProgress());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        slideTypeValues = getResources().getStringArray(R.array.slide_value);
        slideTimeValues = getResources().getStringArray(R.array.slide_time_value);

        switchUseSlide = (Switch) getView().findViewById(R.id.switchUseSlide);
        spinnerSlideType = (Spinner)getView().findViewById(R.id.spinnerSlideType);
        spinnerSlideTime = (Spinner)getView().findViewById(R.id.spinnerSlideTime);
        seekBarSlideSpeed = (SeekBar)getView().findViewById(R.id.seekBarSlideSpeed);
        seekBarSlideDelay = (SeekBar)getView().findViewById(R.id.seekBarSlideDelay);

        ApplicationData.LoadEffects(getContext());
        ApplicationDataToViews();

        switchUseSlide.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                spinnerSlideType.setEnabled(b);
                seekBarSlideSpeed.setEnabled(b);

                ApplicationDataFromViews();
                ApplicationData.SaveEffects(getContext());
            }
        });
        spinnerSlideType.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ApplicationDataFromViews();
                ApplicationData.SaveEffects(getContext());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerSlideTime.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ApplicationDataFromViews();
                ApplicationData.SaveEffects(getContext());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
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
        seekBarSlideSpeed.setOnSeekBarChangeListener(seekBarChangedListener);
        seekBarSlideDelay.setOnSeekBarChangeListener(seekBarChangedListener);

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
