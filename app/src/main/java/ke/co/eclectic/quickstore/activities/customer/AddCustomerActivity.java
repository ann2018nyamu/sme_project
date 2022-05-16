package ke.co.eclectic.quickstore.activities.customer;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.CountryCodeActivity;
import ke.co.eclectic.quickstore.adapters.SingleViewAdapter;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Customer;
import ke.co.eclectic.quickstore.rest.response.CustomerResponse;
import ke.co.eclectic.quickstore.viewModel.CustomerViewModel;
import timber.log.Timber;

public class AddCustomerActivity extends AppCompatActivity{
    private static final int COUNTRY_REQUEST_CODE = 1;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.etxtCompanyName)
     EditText etxtCompanyName;
    @BindView(R.id.etxtCustomerName)
     EditText etxtCustomerName;
    @BindView(R.id.etxtCustomerIdPassport)
     EditText etxtCustomerIdPassport;
    @BindView(R.id.etxtCityTown)
     EditText etxtCityTown;
    @BindView(R.id.etxtCustomerNumber)
     EditText etxtCustomerNumber;
     @BindView(R.id.etxtCustomerAltNumber)
     EditText etxtCustomerAltNumber;
    @BindView(R.id.etxtCountryCode)
    EditText etxtCountryCode;
    @BindView(R.id.etxtCountry)
    EditText etxtCountry;
    @BindView(R.id.etxtCustomerEmail)
     EditText etxtCustomerEmail;
    @BindView(R.id.etxtCustomerAltEmail)
     EditText etxtCustomerAltEmail;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.btnDelete)
    Button btnDelete;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;


    private CompositeDisposable disposable = new CompositeDisposable();
    private String countrycode="";
    private Customer customer = new Customer();
    private CustomerViewModel mCustomerViewModel;
    private Customer currentCustomer = new Customer();
    private boolean isCountry = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            currentCustomer = new Gson().fromJson(extra.get("customer").toString(),Customer.class);
        }

        ButterKnife.bind(this);
        initToolbar();
        initButton();
        initEditTextView();
        initObservables();
        initDbData();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(null != GlobalVariable.choosenCountry && !GlobalVariable.choosenCountry.getName().contentEquals("")) {
            if(isCountry){
                etxtCountry.setText(GlobalVariable.choosenCountry.getName());
                etxtCountryCode.setText(GlobalVariable.choosenCountry.getDialCode());
            }else{
                etxtCountryCode.setText(GlobalVariable.choosenCountry.getDialCode());
            }
            isCountry = false;
            GlobalVariable.choosenCountry = null;
        }

    }
    /**
     * initializes customer data
     */
    private void initDbData() {
        mCustomerViewModel = ViewModelProviders.of(this).get(CustomerViewModel.class);

        mCustomerViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            GlobalMethod.stoploader(this);
            if(myApiResponses.getStatus().contentEquals("success")){
                if(myApiResponses.getOperation().contentEquals("apisave")
                        || myApiResponses.getOperation().contentEquals("apidelete")){
                            CustomerResponse customerResponse = (CustomerResponse) myApiResponses.getObject();
                            GlobalMethod.showSuccess(this, customerResponse.getMessage(), true);
                    Completable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                            .subscribe(this::finish);
                }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(this,myApiResponses.getError(),rootView);
                }
            }
        });

        mCustomerViewModel.getIsInternetConnected().observe(this, aBoolean -> {
            if (!aBoolean) {
                //CodingMsg.tle(this, "Something went wrong.Please check your Internet");
            }
        });
    }

    /**
     * initializes view button
     */
    private void initButton() {
        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("customers","cancreate")) {
            btnDelete.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);
            return;
        }

        if(currentCustomer.getCustomerid() != 0){
            btnSave.setText("Update");
        }
    }
    /**
     * listens to click event
     */
    @OnClick(R.id.btnDelete)
    public void deleteCustomer(){
       CodingMsg.tl(this,"Under  development");
//       mCustomerViewModel.deleteCustomer(currentCustomer);
    }

    /**
     * does activity clean up
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
    /**
     * initializes observables for the views
     */
    private void initObservables() {

        Observable<Boolean> locationObservable = RxTextView.textChanges(etxtCustomerAltEmail).skip(1)
                .map(
                        charSequence -> validate_info()
                );

        Observable<Boolean> etxtCountryCodeObs = RxTextView.textChanges(etxtCountryCode).skip(1)
                .map(
                        charSequence -> validate_info()

                );Observable<Boolean> etxtCustomerNameObs = RxTextView.textChanges(etxtCustomerName).skip(1)
                .map(
                        charSequence -> validate_info()

                );

        Observable<Boolean> phoneObservable = RxTextView.textChanges(etxtCustomerNumber).skip(1)
                .map(
                        charSequence -> validate_info()

                );
        Observable<Boolean> observable = Observable.combineLatest(locationObservable, phoneObservable,
                (s, s2) -> validate_info());

    }
    /**
     * changes button state based on validity of user information
     */
    private void updateButton(boolean valid) {
        if (valid) {
            btnSave.setEnabled(true);
            btnSave.setBackgroundResource(R.drawable.btn_active);
        }else{
            btnSave.setEnabled(false);
            btnSave.setBackgroundResource(R.drawable.btn_inactive);
        }
    }

    /**
     * validates customer data
     */
    private Boolean validate_info() {
        customer = new Customer();
        if(currentCustomer.getCustomerid() != 0){
            customer = currentCustomer;
        }

        if(etxtCompanyName.getText().toString().length() < 2){
            etxtCompanyName.setError("Enter your customer name");
            return false;
        }

        if(etxtCustomerName.getText().toString().trim().isEmpty()){
            etxtCustomerName.setError("Enter the customer  name");
            return false;
        }

        if(etxtCustomerIdPassport.getText().toString().trim().isEmpty()){
            etxtCustomerName.setError("Enter the customer location");
            return false;
        }



        if(etxtCustomerNumber.getText().toString().trim().isEmpty()){
              etxtCustomerNumber.setError("Invalid phone number");
            return false;
        }

        if(etxtCountry.getText().toString().trim().isEmpty()){
            etxtCountry.setError("Select your country");
            return false;
        }

        if(etxtCountryCode.getText().toString().trim().isEmpty()){
            etxtCountryCode.setError("Select your country dial code");
            return false;
        }

        updateButton(true);

        customer.setContactname(etxtCustomerName.getText().toString());
        customer.setCompanyname(etxtCompanyName.getText().toString());
        customer.setTown(etxtCityTown.getText().toString());
        customer.setCountry(etxtCountry.getText().toString());
        customer.setCountrycode(etxtCountryCode.getText().toString());
        customer.setIdentificationnumber(etxtCustomerIdPassport.getText().toString());
        customer.setPhonenumber(etxtCustomerNumber.getText().toString());
        customer.setOthernumber(etxtCustomerAltNumber.getText().toString());
        customer.setEmail(etxtCustomerEmail.getText().toString());
        customer.setOtheremail(etxtCustomerAltEmail.getText().toString());

        return true;
    }
    /**
     * initializes edittextview
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEditTextView() {

        if(currentCustomer.getCustomerid() != 0){
            etxtCompanyName.setText(currentCustomer.getCompanyname());
            etxtCustomerName.setText(currentCustomer.getContactname());
            etxtCustomerIdPassport.setText(currentCustomer.getIdentificationnumber());
            etxtCityTown.setText(currentCustomer.getTown());
            etxtCountry.setText(currentCustomer.getCountry());
            etxtCountryCode.setText(currentCustomer.getCountrycode());
            etxtCustomerNumber.setText(currentCustomer.getPhonenumber());
            etxtCustomerAltNumber.setText(currentCustomer.getOthernumber());
            etxtCustomerEmail.setText(currentCustomer.getEmail());
            etxtCustomerAltEmail.setText(currentCustomer.getOtheremail());
        }

        if(GlobalVariable.getCurrentUser().getCurrentBsRule("customers","cancreate")) {

            etxtCountryCode.setOnClickListener(v -> {
                HashMap<String, String> data = new HashMap<>();
                data.put("action", "showcode");
                GlobalMethod.goToActivity(AddCustomerActivity.this, CountryCodeActivity.class, data);
            });

            etxtCountry.setOnClickListener(v -> {
                isCountry = true;
                HashMap<String, String> data = new HashMap<>();
                data.put("action", "");
                GlobalMethod.goToActivity(AddCustomerActivity.this, CountryCodeActivity.class, data);
            });

            etxtCityTown.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtCustomerIdPassport.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtCompanyName.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtCustomerName.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtCustomerNumber.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtCustomerAltNumber.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtCustomerEmail.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtCustomerAltEmail.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });

        }
        etxtCustomerAltEmail.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                if(btnSave.isEnabled()){
                    Timber.v("save customer");
                    saveCustomer();
                }else{
                    CodingMsg.tl(AddCustomerActivity.this,"Please confirm you have filled all the necessary information");
                }
                //do here your stuff f
                return true;
            }
            return false;
        });

    }

    /**
     * initializes activity toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(currentCustomer.getCustomerid() == 0){
            setTitle("Add Customer");
        } else {
            setTitle("View Customer");
            if(GlobalVariable.getCurrentUser().getCurrentBsRule("customers","cancreate")) {
                setTitle("Edit Customer");
            }
        }

        // making all textview use monteserrat font
        for(int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setTypeface(GlobalVariable.getMontserratMedium(this));
            }
        }

    }


    /**
     * sends  customer information for saving
     */
    public void saveCustomer(){
        if(validate_info()){
            GlobalMethod.showloader(this,"Sending request...",true);
            customer.setBusinessid(GlobalVariable.getCurrentUser().getBusinessid(this));
            mCustomerViewModel.insert(customer);
        }
    }
    /**
     * listens  save button click event
     */
    @OnClick(R.id.btnSave)
    public void submit() {
        saveCustomer();
    }
    /**
     * listens to menuitem savings
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home: {
                 finish();
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }



}
