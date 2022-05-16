package ke.co.eclectic.quickstore.activities.business;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.CountryCodeActivity;
import ke.co.eclectic.quickstore.activities.MenuActivity;
import ke.co.eclectic.quickstore.activities.stores.AddStoreActivity;
import ke.co.eclectic.quickstore.adapters.SingleViewAdapter;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Business;
import ke.co.eclectic.quickstore.models.Country;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.BusinessResponse;
import ke.co.eclectic.quickstore.viewModel.BusinessViewModel;
import timber.log.Timber;


public class BusinessManageActivity extends AppCompatActivity implements SingleViewAdapter.StringComm{
    private static final int COUNTRY_REQUEST_CODE = 10;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    //adding business layout
    @BindView(R.id.txtInfo)
    TextView txtInfo;
    @BindView(R.id.etxtBusinessName)
 EditText etxtBusinessName;
    @BindView(R.id.etxtCityTown)
 EditText etxtCityTown;
    @BindView(R.id.etxtcountry)
 EditText etxtcountry;
    @BindView(R.id.etxtTelephone)
 EditText etxtTelephone;
    @BindView(R.id.etxtPostalAddr)
 EditText etxtPostalAddr;
    @BindView(R.id.etxtLocation)
 EditText etxtLocation;
    @BindView(R.id.etxtEmail)
 EditText etxtEmail;
    @BindView(R.id.etxtWebsite)
 EditText etxtWebsite;

    @BindView(R.id.btnContinue)
Button btnContinue;

    @BindView(R.id.rootView)
     CoordinatorLayout rootView;
    @BindView(R.id.etxtcountryCode)
    EditText etxtcountryCode;

    private CompositeDisposable disposable = new CompositeDisposable();
    private String countrycode="";
    private Business business = new Business();
    private boolean isDialCode= false;
    //view models
    private BusinessViewModel mBusinessViewModel;
    private String businessid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_manage);
        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            business = new Gson().fromJson(extra.get("business").toString(),Business.class);
        }

        ButterKnife.bind(this);
        initToolbar();
        initFab();
        initTextview();
        initButton();
        initEditTextView();
        initObservables();
        initData();

    }
    /**
     * initializes business data
     */
    private void initData() {
        mBusinessViewModel = ViewModelProviders.of(this).get(BusinessViewModel.class);

        mBusinessViewModel.getApiResponse().observe(this,myApiResponses -> {
            if(myApiResponses.getStatus().contentEquals("success")){
                if(myApiResponses.getOperation().contains("apisave")) {
                    BusinessResponse businessResponse = (BusinessResponse) myApiResponses.getObject();
                    if(business.getBusinessid() == 0){
                        business.setBusinessid( businessResponse.getData().getBusinessid());
                        GlobalVariable.getCurrentUser().setBusinessname(business.getbName());
                        GlobalVariable.getCurrentUser().setBusinessid(business.getBusinessid());
                        GlobalVariable.getCurrentUser().saveUserInfo(GlobalVariable.getCurrentUser(),this);

                        HashMap<String,String> data = new HashMap<>();
                        data.put("business",new Gson().toJson(business));
                        GlobalMethod.goToActivity(BusinessManageActivity.this, AddStoreActivity.class,data);
                    }
                }

            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(this,myApiResponses.getError(),rootView);
                }
            }
        });

    }
    /**
     * initializes view buttons
     */
    private void initButton() {
        btnContinue.setTypeface(GlobalVariable.getMontserratMedium(this));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
    /**
     * initializes observable to views objects
     */
    private void initObservables() {
        Observable<Boolean> locationObservable = RxTextView.textChanges(etxtLocation).skip(1)
                .map(
                        charSequence -> validate_info()

                );
        Observable<Boolean> webObservable = RxTextView.textChanges(etxtWebsite).skip(1)
                .map(
                        charSequence -> validate_info()
                );

        Observable<Boolean> bbObservable = RxTextView.textChanges(etxtBusinessName).skip(1)
                .map(
                        charSequence -> validate_info()
                );
        Observable<Boolean> tlObservable = RxTextView.textChanges(etxtTelephone).skip(1)
                .map(
                        charSequence -> validate_info()
                );

        Observable<Boolean> observable = Observable.combineLatest(locationObservable, webObservable,
                (s, s2) -> validate_info());

        observable.subscribe(new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean s) {
              updateButton(s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


    }
    /**
     * changes button color depending on validation state
     */
    public void updateButton(boolean valid) {
        if (valid) {
            btnContinue.setEnabled(true);
            btnContinue.setBackgroundResource(R.drawable.btn_active);
        }else{
            btnContinue.setEnabled(false);
            btnContinue.setBackgroundResource(R.drawable.btn_inactive);
        }
    }
    /**
     * validates user input
     */
    private Boolean validate_info() {
        business = new Business();
        if(etxtBusinessName.getText().toString().length() < 2){
            etxtBusinessName.setError("Enter your business name");
            return false;
        }

        if(etxtCityTown.getText().toString().length() < 2){
            etxtCityTown.setError("Enter the city/town");
            return false;
        }

        if(etxtcountry.getText().toString().length() < 2){
            etxtcountry.setError("Select country");
            return false;
        }
        if(etxtTelephone.getText().toString().length() < 2){
              etxtTelephone.setError("Invalid phone number");
            return false;
        }

        if(etxtcountryCode.getText().toString().isEmpty()){
            //CodingMsg.tl(this,"Select your country dial code");
            GlobalMethod.showMessage(this,"Select your country dial code",rootView,"",false);

            return false;
        }

        business.setbDialCode(etxtcountryCode.getText().toString());
        business.setbCountry(etxtcountry.getText().toString());
        business.setbTelephone(etxtTelephone.getText().toString());
        business.setbTown(etxtCityTown.getText().toString());
        business.setbName(etxtBusinessName.getText().toString());
        business.setbPostalAddr(etxtPostalAddr.getText().toString());
        business.setbLocation(etxtLocation.getText().toString());
        business.setbEmail(etxtEmail.getText().toString());
        business.setbWebsite(etxtWebsite.getText().toString());
        updateButton(true);

        return true;
    }

    /**
     * initializes edittexts
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEditTextView() {


//        etxtBusinessName.setText("BusTest1");
//        etxtCityTown.setText("Nairobi");
//        etxtcountry.setText("Kenya");
//        etxtTelephone.setText("719124626");
//        etxtPostalAddr.setText("postadddr");
//        etxtLocation.setText("Waiyaki");
//        etxtEmail.setText("bus@gmail.com");
//        etxtWebsite.setText("http://bus.co.ke");

        etxtcountryCode.setOnClickListener(v -> {
            // GlobalMethod.goToActivity(getActivity(), CountryActivity.class);
            isDialCode = true;
            HashMap<String,String> data = new HashMap<>();
            data.put("action","showcode");
            GlobalMethod.goToActivity(this, CountryCodeActivity.class,data,COUNTRY_REQUEST_CODE);
        });

        etxtBusinessName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });  etxtCityTown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });   etxtTelephone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });  etxtPostalAddr.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });  etxtLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        etxtEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        }); etxtWebsite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });


        etxtcountry.setOnClickListener(v ->{

            HashMap<String,String> data = new HashMap<>();
            data.put("action","");
            GlobalMethod.goToActivity(BusinessManageActivity.this, CountryCodeActivity.class,data,COUNTRY_REQUEST_CODE);

                });

        etxtLocation.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                if(btnContinue.isEnabled()){
                    saveBusiness();
                }else{
                    GlobalMethod.showMessage(this,"Please confirm you have entered necessary information",rootView,"",false);

                   // CodingMsg.tl(BusinessManageActivity.this,"Please confirm you have entered necessary information");
                }
                //do here your stuff f
                return true;
            }
            return false;
        });

    }

    /**
     * initializes textviews
     */
    private void initTextview() {
        txtInfo.setTypeface(GlobalVariable.getMontserratMedium(this));
        txtInfo.setText("Welcome to QuickStore.\n".concat(GlobalVariable.getCurrentUser().getFirstname()).concat(" tell us a little about yourself"));
    }

    /**
     * initializes floating button
     */
    private void initFab() {
        fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show());
    }
    /**
     * initializes toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("About Your Business");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.v("onActivityResult"+data.getStringExtra(CountryCodeActivity.EXTRA_REPLY));
        if (requestCode == COUNTRY_REQUEST_CODE && resultCode == RESULT_OK) {
            Timber.v("onActivityResult"+data.getStringExtra(CountryCodeActivity.EXTRA_REPLY));

            Country country = new Gson().fromJson(data.getStringExtra(CountryCodeActivity.EXTRA_REPLY),Country.class);
            etxtcountry.setText(country.getName());
            Timber.v("country");
            if(isDialCode){
                etxtcountryCode.setText(country.getDialCode());
                isDialCode = false;
            }else{
                etxtcountry.setText(country.getName());
            }
        }


    }

    /**
     * implementation of menu adapter
     */
    @Override
    public void strMessage(int position, String val, String action) {

    }
    /**
     * save/updates business information to serve
     */
    public void saveBusiness(){
        if(validate_info()) {
//            business.setBid(new Date().getTime()+"");
//            business.setbCreatedTime(new Date().getTime());
           mBusinessViewModel.insert(business); //put this after user has signup successfully

            HashMap<String, String> data = new HashMap<>();
            data.put("request_type", "addbusiness");
            data.put("name", business.getbName());
            data.put("country", business.getbCountry());
            data.put("location", business.getbLocation());
            data.put("telephone", business.getbDialCode().concat(business.getbTelephone()));
            data.put("postaladdress", business.getbPostalAddr());
            data.put("email", business.getbEmail());
            data.put("website", business.getbWebsite());


            // sending request to api
            disposable.add(
                    ApiClient.getApi().businessApi(new JsonParser().parse(new Gson().toJson(data)))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<BusinessResponse>() {
                                @Override
                                public void onSuccess(BusinessResponse businessResponse) {
                                    if (businessResponse.getStatus().contentEquals("success")) {
                                        Timber.v("new Gson().toJson(GlobalVariable.getCurrentUser())");

                                        business.setBusinessid(businessResponse.getData().getBusinessid());
                                        mBusinessViewModel.insert(business);
                                        GlobalVariable.getCurrentUser().setBusinessname(business.getbName());
                                        GlobalVariable.getCurrentUser().setBusinessid(business.getBusinessid());

                                        HashMap<String,String> data = new HashMap<>();
                                        data.put("business",new Gson().toJson(business));
                                        GlobalMethod.goToActivity(BusinessManageActivity.this, AddStoreActivity.class,data);
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    GlobalMethod.parseCommError(BusinessManageActivity.this, e, rootView);
                                    //CodingMsg.t(this,e.getMessage());
                                }
                            })
            );
        }
    }


    /**
     * listens to btncontinue click event
     */
    @OnClick(R.id.btnContinue)
    public void submit() {
        Timber.v("save business submit ");
        saveBusiness();
    }
    /**
     * listens to menu item click event
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home: {
               GlobalMethod.goToActivity(this,MenuActivity.class);
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }
}
