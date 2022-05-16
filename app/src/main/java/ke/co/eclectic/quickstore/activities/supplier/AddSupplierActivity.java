package ke.co.eclectic.quickstore.activities.supplier;

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
import ke.co.eclectic.quickstore.models.Supplier;
import ke.co.eclectic.quickstore.rest.response.SupplierResponse;
import ke.co.eclectic.quickstore.viewModel.SupplierViewModel;
import timber.log.Timber;

public class AddSupplierActivity extends AppCompatActivity {
    private static final int COUNTRY_REQUEST_CODE = 1;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.etxtCompanyName)
     EditText etxtCompanyName;
    @BindView(R.id.etxtSupplierName)
     EditText etxtSupplierName;
    @BindView(R.id.etxtSupplierIdPassport)
     EditText etxtSupplierIdPassport;
    @BindView(R.id.etxtCityTown)
     EditText etxtCityTown;
    @BindView(R.id.etxtSupplierNumber)
     EditText etxtSupplierNumber;
     @BindView(R.id.etxtSupplierAltNumber)
     EditText etxtSupplierAltNumber;
    @BindView(R.id.etxtCountryCode)
    EditText etxtCountryCode;
    @BindView(R.id.etxtCountry)
    EditText etxtCountry;
    @BindView(R.id.etxtSupplierEmail)
     EditText etxtSupplierEmail;
    @BindView(R.id.etxtSupplierAltEmail)
     EditText etxtSupplierAltEmail;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.btnDelete)
    Button btnDelete;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;


    private CompositeDisposable disposable = new CompositeDisposable();
    private String countrycode="";
    private Supplier supplier = new Supplier();
    private SupplierViewModel mSupplierViewModel;
    private Supplier currentSupplier = new Supplier();
    private boolean isCountry = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_supplier);
        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            currentSupplier = new Gson().fromJson(extra.get("supplier").toString(),Supplier.class);
        }

        ButterKnife.bind(this);
        initToolbar();
        initButton();
        initEditTextView();
        initObservables();
        initDbData();
    }


    /**
     * handles resume state
     */
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
     * initializes suppliers data
     */
    private void initDbData() {
        mSupplierViewModel = ViewModelProviders.of(this).get(SupplierViewModel.class);

        mSupplierViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            GlobalMethod.stoploader(this);
            if(myApiResponses.getStatus().contentEquals("success")){
                if(myApiResponses.getOperation().contentEquals("apisave")
                        || myApiResponses.getOperation().contentEquals("apidelete")){
                            SupplierResponse supplierResponse = (SupplierResponse) myApiResponses.getObject();
                            GlobalMethod.showSuccess(this, supplierResponse.getMessage(), true);
                    Completable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                            .subscribe(this::finish);
                }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(this,myApiResponses.getError(),rootView);
                }
            }
        });

        mSupplierViewModel.getIsInternetConnected().observe(this, aBoolean -> {
            if(!aBoolean){
                //CodingMsg.tle(this, "Something went wrong.Please check your Internet");
            }
        });
    }


    /**
     * initializes buttons
     */
    private void initButton() {
        if(currentSupplier.getSupplierid() != 0){

            btnDelete.setVisibility(View.GONE);
            btnSave.setText("Update");
        }

        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("suppliers", "cancreate")) {
            btnSave.setVisibility(View.GONE);
        }

        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("suppliers", "candelete")) {
            btnDelete.setVisibility(View.GONE);
        }
    }

    /**
     * handles delete button clicks
     */
    @OnClick(R.id.btnDelete)
    public void deleteSupplier(){
       CodingMsg.tl(this,"Under  development");
//       mSupplierViewModel.deleteSupplier(currentSupplier);
    }


    /**
     * clears observable memory allocation
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    /**
     * initializes observables
     */
    private void initObservables() {

        Observable<Boolean> locationObservable = RxTextView.textChanges(etxtSupplierAltEmail).skip(1)
                .map(
                        charSequence -> validate_info()
                );

        Observable<Boolean> etxtCountryCodeObs = RxTextView.textChanges(etxtCountryCode).skip(1)
                .map(
                        charSequence -> validate_info()

                );
        Observable<Boolean> etxtSupplierNameObs = RxTextView.textChanges(etxtSupplierName).skip(1)
                .map(
                        charSequence -> validate_info()

                );


        Observable<Boolean> phoneObservable = RxTextView.textChanges(etxtSupplierNumber).skip(1)
                .map(
                        charSequence -> validate_info()

                );

        Observable<Boolean> observable = Observable.combineLatest(locationObservable, phoneObservable,
                (s, s2) -> validate_info());


    }

    /**
     * toggles save button active state
     * @param valid boolean true  if supplier information is valid
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
     * validates supplier information
     * @return boolean true if supplier information is valid
     */
    private Boolean validate_info() {
        supplier = new Supplier();
        if(currentSupplier.getSupplierid() != 0){
            supplier = currentSupplier;
        }

        if(etxtCompanyName.getText().toString().length() < 2){
            etxtCompanyName.setError("Enter your supplier name");
            return false;
        }

        if(etxtSupplierName.getText().toString().trim().isEmpty()){
            etxtSupplierName.setError("Enter the supplier  name");
            return false;
        }

        if(etxtSupplierIdPassport.getText().toString().trim().isEmpty()){
            etxtSupplierName.setError("Enter the supplier location");
            return false;
        }



        if(etxtSupplierNumber.getText().toString().trim().isEmpty()){
              etxtSupplierNumber.setError("Invalid phone number");
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

        supplier.setContactname(etxtSupplierName.getText().toString());
        supplier.setCompanyname(etxtCompanyName.getText().toString());
        supplier.setTown(etxtCityTown.getText().toString());
        supplier.setCountry(etxtCountry.getText().toString());
        supplier.setCountrycode(etxtCountryCode.getText().toString());
        supplier.setIdentificationnumber(etxtSupplierIdPassport.getText().toString());
        supplier.setPhonenumber(etxtSupplierNumber.getText().toString());
        supplier.setOthernumber(etxtSupplierAltNumber.getText().toString());
        supplier.setEmail(etxtSupplierEmail.getText().toString());
        supplier.setOtheremail(etxtSupplierAltEmail.getText().toString());

        return true;
    }

    /**
     * initializes edittext
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEditTextView() {

        if(currentSupplier.getSupplierid() != 0){
            etxtCompanyName.setText(currentSupplier.getCompanyname());
            etxtSupplierName.setText(currentSupplier.getContactname());
            etxtSupplierIdPassport.setText(currentSupplier.getIdentificationnumber());
            etxtCityTown.setText(currentSupplier.getTown());
            etxtCountry.setText(currentSupplier.getCountry());
            etxtCountryCode.setText(currentSupplier.getCountrycode());
            etxtSupplierNumber.setText(currentSupplier.getPhonenumber());
            etxtSupplierAltNumber.setText(currentSupplier.getOthernumber());
            etxtSupplierEmail.setText(currentSupplier.getEmail());
            etxtSupplierAltEmail.setText(currentSupplier.getOtheremail());
        }


        etxtCountryCode.setOnClickListener(v -> {
            HashMap<String,String> data = new HashMap<>();
            data.put("action","showcode");
            GlobalMethod.goToActivity(AddSupplierActivity.this, CountryCodeActivity.class,data);
        });

        etxtCountry.setOnClickListener(v -> {
            isCountry = true;
            HashMap<String,String> data = new HashMap<>();
            data.put("action","");
            GlobalMethod.goToActivity(AddSupplierActivity.this, CountryCodeActivity.class,data);
        });

        etxtCityTown.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });
        etxtSupplierIdPassport.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });
        etxtCompanyName.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });
        etxtSupplierName.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });
        etxtSupplierNumber.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });  etxtSupplierAltNumber.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });
        etxtSupplierEmail.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        }); etxtSupplierAltEmail.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });


        etxtSupplierAltEmail.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                if(btnSave.isEnabled()){
                    Timber.v("save supplier");
                    saveSupplier();
                }else{
                    CodingMsg.tl(AddSupplierActivity.this,"Please confirm you have filled all the necessary information");
                }
                //do here your stuff f
                return true;
            }
            return false;
        });

    }

    /**
     * initializes  toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(currentSupplier.getSupplierid() == 0){
            setTitle("Add Supplier");
        }else{
            setTitle("Edit Supplier");
        }

        // making all textview use monteserrat font
        for(int i = 0; i < toolbar.getChildCount(); i++)
        {
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setTypeface(GlobalVariable.getMontserratMedium(this));
            }
        }
    }


    /**
     * saves supplier information to its viewmodel
     */
    public void saveSupplier(){
        if(validate_info()){
            GlobalMethod.showloader(this,"Sending request...",true);
            supplier.setBusinessid(GlobalVariable.getCurrentUser().getBusinessid(this));
            mSupplierViewModel.insert(supplier);
        }
    }

    /**
     * handles save button clicks
     */
    @OnClick(R.id.btnSave)
    public void submit() {
        saveSupplier();
    }


    /**
     * handles menu item clicks
     * @param item menu item
     * @return boolean
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
