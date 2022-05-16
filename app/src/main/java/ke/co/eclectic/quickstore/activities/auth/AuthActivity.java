package ke.co.eclectic.quickstore.activities.auth;

import android.arch.lifecycle.ViewModelProviders;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.adapters.ViewPagerAdapter;
import ke.co.eclectic.quickstore.activities.auth.fragment.LoginFragment;
import ke.co.eclectic.quickstore.activities.auth.fragment.SignupFragment;
import ke.co.eclectic.quickstore.dao.QuickStoreRoomDb;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.rest.response.UserResponse;
import ke.co.eclectic.quickstore.viewModel.CountryViewModel;
import ke.co.eclectic.quickstore.viewModel.UserViewModel;
import timber.log.Timber;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;


/**
 * Actvity responsible for user authentication to the quickstore application
 * @author  David Manduku(Eclectics International Ltd)
 * @version 2018.1209
 * @since 1.0
 */
public class AuthActivity extends AppCompatActivity {
    /**
     * The Tab layout.
     */
    @BindView(R.id.tabLayout)
     TabLayout tabLayout;
    /**
     * The Viewpager main.
     */
    @BindView(R.id.viewpager_main)
    ViewPager viewpager_main;
    /**
     * The Root view.
     */
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;

    /**
     * hold activity bundle action data
     */
    private String action="";

    public  AuthActivity mAuthActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            try{
                action =  extra.get("action").toString();
            }catch (Exception e){
                Timber.v(extra.toString() +"    /   "+e.getMessage());
            }
        }
        setContentView(R.layout.activity_auth);
        mAuthActivity = this;
       // init();
        ButterKnife.bind(this);
        setupViewPager();
        setupTabIcons();
        initDbData();
        AppCenter.start(getApplication(), "7554f63e-624c-4ad3-9254-a92de0531cef", Analytics.class, Crashes.class);
    }

    /**
     * Initializes this fragment with relevant user data
     */
    private void initDbData() {
        CountryViewModel mCountryViewModel = ViewModelProviders.of(this).get(CountryViewModel.class);
     // UserViewModel for exposing user related crud functionalities
         UserViewModel mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
//        SalesOrderViewModel mSalesOrderViewModel = ViewModelProviders.of(this).get(SalesOrderViewModel.class);
//        HashMap<String, Object> data = new HashMap<>();
//        data.put("payment_type", "mpesa");
//        data.put("amount", 100);
//        data.put("phone", "254719124626");
//        mSalesOrderViewModel.sendPaymentRequest(data);
        mCountryViewModel.getAllCountries().observe(this,countries->{
            Timber.v(countries.size()+" country size");});
        if(action.contentEquals("deleteuser")){
            //when a users token has expired
            mUserViewModel.deleteAll();
            QuickStoreRoomDb.deleteData();
        }

        mUserViewModel.getApiResponse().observe(this, myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            if(myApiResponses.getStatus().contentEquals("success")){
                UserResponse userResponse =  (UserResponse)myApiResponses.getObject();
                if(myApiResponses.getOperation().contentEquals("apisave")){
                    GlobalVariable.getCurrentUser().setPhonenumber(GlobalVariable.getCurrentUser().getCountrycode().concat( GlobalVariable.getCurrentUser().getPhonenumber()));
                    GlobalMethod.goToActivity(this,SmsVerificationActivity.class);
                }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(this,myApiResponses.getError(),rootView);
                }
            }
        });

        mUserViewModel.getIsInternetConnected().observe(this, aBoolean -> {
            if(!aBoolean){
                Timber.v(getString(R.string.check_internet));
            }
        });
    }

    /**
     * Get root view coordinator layout.
     *
     * @return the coordinator layout
     */
    public  CoordinatorLayout getRootView(){
        return rootView;
    }

    /**
     * defines up individual  tablayout view in the viewpager
     */
    private void setupTabIcons() {
        tabLayout.setupWithViewPager(viewpager_main);
        View view =  LayoutInflater.from(this).inflate(R.layout.custom_tab, null);

        TextView tabOne = view.findViewById(R.id.tab);
        tabOne.setText(R.string.sign_in);
        tabOne.setTypeface(GlobalVariable.getMontserratSemiBold(this));

        tabLayout.getTabAt(0).setCustomView(view);

        view =  LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne = view.findViewById(R.id.tab);
        tabOne.setText(R.string.register);
        tabOne.setTypeface(GlobalVariable.getMontserratSemiBold(this));
        tabLayout.getTabAt(1).setCustomView(view);

    }

    /**
     * initializes viewpager data
     */
    private void setupViewPager( ) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(LoginFragment.newInstance(), getString(R.string.sign_in));
        adapter.addFragment(SignupFragment.newInstance(), getString(R.string.register));
        viewpager_main.setAdapter(adapter);
    }


}
