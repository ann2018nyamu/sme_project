package ke.co.eclectic.quickstore.activities.onboarding.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.helper.GlobalVariable;

/**
 * A simple {@link Fragment} subclass.
 */
public class SliderFragment3 extends Fragment {

    private View rootView;
    private TextView txtTitle1,txtTitle2,txtTitle3;
    private ImageView imgPreview;

    /**
     * Instantiates a new Slider fragment 3.
     */
    public SliderFragment3() {
        // Required empty public constructor
    }

    /**
     * New instance slider fragment 3.
     *
     * @return the slider fragment 3
     */
    public static SliderFragment3 newInstance() {
        SliderFragment3 fragment = new SliderFragment3();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_slider, container, false);
        init();
        initTextView();
        initImageView();
        return rootView;
    }
    /**
     * initializes fragment imageview
     */
    private void initImageView() {
        imgPreview.setImageResource(R.drawable.outlets);
    }
    /**
     * initializes fragment textviews
     */
    private void initTextView() {
        //assigning content  to textfield
        txtTitle2.setText(R.string.monitor_work);
        txtTitle3.setText(R.string.outlets);
        //setting typeface
        txtTitle1.setTypeface(GlobalVariable.getMontserratMedium(getActivity()));
        txtTitle2.setTypeface(GlobalVariable.getMontserratLight(getActivity()));
        txtTitle3.setTypeface(GlobalVariable.getMontserratMedium(getActivity()));
    }
    /**
     * initializes vairiables to  be used
     */
    private void init() {
        txtTitle1 = rootView.findViewById(R.id.txtTitle1);
        txtTitle2 = rootView.findViewById(R.id.txtTitle2);
        txtTitle3 = rootView.findViewById(R.id.txtTitle3);
        imgPreview = rootView.findViewById(R.id.imgPreview);
    }


}
