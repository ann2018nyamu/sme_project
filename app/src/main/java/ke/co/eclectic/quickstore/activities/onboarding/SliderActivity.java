package ke.co.eclectic.quickstore.activities.onboarding;

import android.arch.lifecycle.ViewModelProviders;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Observable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.auth.AuthActivity;
import ke.co.eclectic.quickstore.adapters.ViewPagerAdapter;
import ke.co.eclectic.quickstore.activities.onboarding.fragment.SliderFragment1;
import ke.co.eclectic.quickstore.activities.onboarding.fragment.SliderFragment2;
import ke.co.eclectic.quickstore.activities.onboarding.fragment.SliderFragment3;
import ke.co.eclectic.quickstore.activities.onboarding.fragment.SliderFragment4;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Country;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.CountryResponse;
import ke.co.eclectic.quickstore.viewModel.CountryViewModel;
import timber.log.Timber;

/**
 * Created by David Manduku on 08/10/2018.
 */
public class SliderActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager sliderVp;
    private Button btnSkip;
    private ImageView imgSlider1,imgSlider2,imgSlider3,imgSlider4;
    private CompositeDisposable disposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_slider);

        init();
        setupViewPager();
        initButton();
        initImageview(0);
        fetchCountryData();

    }

    /**
     * cleans up the activity data
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    /**
     * Fetch country data.
     */
    public void fetchCountryData(){
        CountryViewModel mCountryViewModel = ViewModelProviders.of(this).get(CountryViewModel.class);

        disposable.add(
                ApiClient.getApi().getCountryData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CountryResponse>(){
                            @Override
                            public void onSuccess(CountryResponse countryResponse) {
                                if(countryResponse.getStatus().contentEquals("success")){
                                    for(Country c:countryResponse.getDataArrayList()){
                                        Timber.v(c.getCode());
                                        mCountryViewModel.insert(c);
                                    }
                                }
                            }
                            @Override
                            public void onError(Throwable e) {
                                GlobalMethod.parseCommRawError(e);
                            }
                        })
        );
    }


    /**
     * changes image to be show to the user
     * @param i image to be shown
     */
    private void initImageview(int i) {
        btnSkip.setText(R.string.skip);
        imgSlider1.setOnClickListener(this);
        imgSlider2.setOnClickListener(this);
        imgSlider3.setOnClickListener(this);
        imgSlider4.setOnClickListener(this);
        if(i == 0){
            imgSlider1.setImageResource(R.drawable.slider_btn_active);
            imgSlider2.setImageResource(R.drawable.slider_btn_inactive);
            imgSlider3.setImageResource(R.drawable.slider_btn_inactive);
            imgSlider4.setImageResource(R.drawable.slider_btn_inactive);
        }
        if(i == 1){
            imgSlider1.setImageResource(R.drawable.slider_btn_inactive);
            imgSlider2.setImageResource(R.drawable.slider_btn_active);
            imgSlider3.setImageResource(R.drawable.slider_btn_inactive);
            imgSlider4.setImageResource(R.drawable.slider_btn_inactive);
        }
        if(i == 2){
            imgSlider1.setImageResource(R.drawable.slider_btn_inactive);
            imgSlider2.setImageResource(R.drawable.slider_btn_inactive);
            imgSlider3.setImageResource(R.drawable.slider_btn_active);
            imgSlider4.setImageResource(R.drawable.slider_btn_inactive);
        }
        if(i == 3){
            imgSlider1.setImageResource(R.drawable.slider_btn_inactive);
            imgSlider2.setImageResource(R.drawable.slider_btn_inactive);
            imgSlider3.setImageResource(R.drawable.slider_btn_inactive);
            imgSlider4.setImageResource(R.drawable.slider_btn_active);
        }

    }

    /**
     * initializes view buttons
     */
    private void initButton() {
        btnSkip.setOnClickListener(this);
        btnSkip.setTypeface(GlobalVariable.getMontserratSemiBold(this));
    }

    /**
     * initializes view pager with data
     */
    private void setupViewPager( ) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(SliderFragment1.newInstance(), "Fragment 1");
        adapter.addFragment(SliderFragment2.newInstance(), "Fragment 2");
        adapter.addFragment(SliderFragment3.newInstance(), "Fragment 3");
        adapter.addFragment(SliderFragment4.newInstance(), "Fragment 4");


        sliderVp.setAdapter(adapter);
        sliderVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                initImageview(position);
            }

            @Override
            public void onPageSelected(int position) {
                //Timber.v("onPageSelected"+position);
                //loadChildFragment(position);
                //initFragViews(position);
              //  initImageview(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Timber.v("onPageScrollStateChanged "+state);
                //  MainActivity.mainThis.searchClear();
            }
        });

    }

    /**
     * initializeng view variables to be used in the activity
     */
    private void init() {
        sliderVp = findViewById(R.id.sliderVp);
        TabLayout sliderTab = findViewById(R.id.sliderTab);
        btnSkip = findViewById(R.id.btnSkip);
        imgSlider1 = findViewById(R.id.imgSlider1);
        imgSlider2 = findViewById(R.id.imgSlider2);
        imgSlider3 = findViewById(R.id.imgSlider3);
        imgSlider4 = findViewById(R.id.imgSlider4);
    }

    /**
     * listens to click events
     * @param v view clicked by the user
     */
    @Override
    public void onClick(View v) {
        if(v == btnSkip){
            GlobalMethod.goToActivity(this,AuthActivity.class);
        }

        if(v == imgSlider1){
            sliderVp.setCurrentItem(0);
        }
         if(v == imgSlider2){
             sliderVp.setCurrentItem(1);
        }
         if(v == imgSlider3){
             sliderVp.setCurrentItem(2);
        }
         if(v == imgSlider4){
             sliderVp.setCurrentItem(3);
        }
    }
}
