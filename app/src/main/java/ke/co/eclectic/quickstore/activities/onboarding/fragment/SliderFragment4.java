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
public class SliderFragment4 extends Fragment {

    private View rootView;
    private TextView txtTitle1,txtTitle2,txtTitle3;
    private ImageView imgPreview;

    /**
     * Instantiates a new Slider fragment 4.
     */
    public SliderFragment4() {
        // Required empty public constructor
    }

    /**
     * New instance slider fragment 4.
     *
     * @return the slider fragment 4
     */
    public static SliderFragment4 newInstance() {
        return new SliderFragment4();
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
        imgPreview.setImageResource(R.drawable.make_inventory);
    }
    /**
     * initializes fragment textviews
     */
    private void initTextView() {
        //assigning content  to textfield
        txtTitle2.setText(R.string.price_monitor);
        txtTitle3.setText(R.string.make_inventory);
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
