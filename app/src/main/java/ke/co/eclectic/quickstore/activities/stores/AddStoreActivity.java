package ke.co.eclectic.quickstore.activities.stores;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.CountryCodeActivity;
import ke.co.eclectic.quickstore.activities.MenuActivity;
import ke.co.eclectic.quickstore.adapters.SingleViewAdapter;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Business;
import ke.co.eclectic.quickstore.models.Country;
import ke.co.eclectic.quickstore.models.Store;
import ke.co.eclectic.quickstore.rest.response.StoreResponse;
import ke.co.eclectic.quickstore.rest.response.StoreTypeResponse;
import ke.co.eclectic.quickstore.viewModel.StoreTypeViewModel;
import ke.co.eclectic.quickstore.viewModel.StoreViewModel;
import timber.log.Timber;

public class AddStoreActivity extends AppCompatActivity implements SingleViewAdapter.StringComm{
    private static final int COUNTRY_REQUEST_CODE = 1;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    //adding store layout
    @BindView(R.id.txtInfo)
    TextView txtInfo;
    @BindView(R.id.etxtStoreName)
     EditText etxtStoreName;
    @BindView(R.id.etxtStoreLocation)
    EditText etxtStoreLocation;

    @BindView(R.id.etxtStoreTelephone)
    EditText etxtStoreTelephone;
    @BindView(R.id.etxtStorePostalAddr)
    EditText etxtStorePostalAddr;

    @BindView(R.id.etxtStoreEmail)
    EditText etxtStoreEmail;
    @BindView(R.id.etxtStoreWebsite)
    EditText etxtStoreWebsite;
    @BindView(R.id.btnContinue)
    Button btnContinue;
    @BindView(R.id.btnDelete)
    Button btnDelete;

    @BindView(R.id.rootView)
    CoordinatorLayout rootView;
    @BindView(R.id.etxtcountryCode)
    EditText etxtcountryCode;
    @BindView(R.id.etxtcountry)
    EditText etxtcountry;

    @BindView(R.id.spinnerStoreType)
    Spinner spinnerStoreType;
    @BindView(R.id.spinnerBusiness)
    Spinner spinnerBusiness;
    
    private CompositeDisposable disposable = new CompositeDisposable();
    private Store currentStore = new Store();
    private Business currentBusiness = new Business();

    private HashMap<String, Integer> mStoreTypeH = new HashMap<>();
    private StoreTypeViewModel   mStoreTypeViewModel;
    private Integer businessid;
    List<String> storeTypeList =new ArrayList<>() ;
    private String storeType="";
    ArrayAdapter<String> storeTypeAdapter;
    public  StoreViewModel mStoreViewModel;
    private String selectedBusiness="";
    private HashMap<String,Integer> businessH= new HashMap<>();
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_store);
        Bundle  extra = getIntent().getExtras();

        if(extra  != null){
            try{
                currentBusiness = new Gson().fromJson(extra.get("business").toString(),Business.class);
            }catch (Exception e){
                currentStore = new Gson().fromJson(extra.get("store").toString(),Store.class);
                currentBusiness.setBusinessid(currentStore.getBusinessid());
                currentBusiness.setbName(currentStore.getBusinessname());
            }

        }
        ButterKnife.bind(this);

        initToolbar();
        initFab();
        initTextview();
        initButton();
        initEditTextView();
        initSpinnerStoreType();
       //initSpinnerBusiness();
        initObservables();
        initDbData();
    }

//    private void initSpinnerBusiness() {
//        ArrayList<String> businessList = new ArrayList<>();
//        businessList.add(0,"Select Business");
//        if(currentBusiness.getBusinessid() != 0){
//            businessList.add(1,currentBusiness.getbName());
//            spinnerBusiness.setVisibility(View.GONE);
//        }
//        for(Roles roles : GlobalVariable.getCurrentUser().getRoles()){
//            if(roles.getCreatedby().intValue() == GlobalVariable.getCurrentUser().getUserid().intValue()){
//                businessList.add(roles.getBusinessname());
//                businessH.put(roles.getBusinessname(),roles.getBusinessid());
//            }
//        }
//        ArrayAdapter businessAdapter = new ArrayAdapter<String>(this,  R.layout.spinner_dropdown_item, businessList);
//        //Specify the layout to use when the list of choices appears
//        businessAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item2);
//        spinnerBusiness.setAdapter(businessAdapter);
//
//        if(currentBusiness != null){
//          for(int i = 0;i<businessList.size();i++){
//              if(currentBusiness.getbName().contentEquals(businessList.get(i))){
//                  spinnerBusiness.setSelection(i);
//              }
//          }
//        }
//    }

    /**
     * initializes store type spinner
     */
    private void initSpinnerStoreType() {
        Timber.v("storeTypeList"+ storeTypeList.size());
        storeTypeList.add(0,"Store Type");
        storeTypeAdapter = new ArrayAdapter<String>(this,  R.layout.spinner_dropdown_item, storeTypeList);
        // Specify the layout to use when the list of choices appears
        storeTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item2);
        spinnerStoreType.setAdapter(storeTypeAdapter);

    }


    /**
     * initializes store data
     */
    private void initDbData() {
           mStoreTypeViewModel = ViewModelProviders.of(this).get(StoreTypeViewModel.class);
           mStoreViewModel = ViewModelProviders.of(this).get(StoreViewModel.class);

        mStoreTypeViewModel.getAllStoreType().observe(this, storeTypes->{
            if( null != storeTypes){
                Timber.v("From getAllStoreType "+ storeTypes.size());
                if(storeTypes.size() !=0){
                    mStoreTypeH.clear();
                    storeTypeList.clear();
                    for(int i =0; i<storeTypes.size();i++){
                        if(!mStoreTypeH.containsKey(storeTypes.get(i).getStoretype())){
                            storeTypeList.add(storeTypes.get(i).getStoretype());
                            mStoreTypeH.put(storeTypes.get(i).getStoretype (),storeTypes.get(i).getTypeid());
                            if(currentStore.getStoreid() != 0){
                                if(currentStore.getStoretype().contentEquals(storeTypes.get(i).getStoretype()) ){
                                    spinnerStoreType.setSelection(i);
                                }
                            }
                        }
                    }
                    initSpinnerStoreType();
                }
            }
        });
        mStoreTypeViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            if(myApiResponses.getStatus().contentEquals("success")){

                if(myApiResponses.getOperation().contains("refresh")) {
                    StoreTypeResponse storeTypeResponse = (StoreTypeResponse) myApiResponses.getObject();
                    if (storeTypeResponse.getData().size() == 0) {
                       // CodingMsg.tl(this,"No store type available");
                        GlobalMethod.showMessage(this,"No store type available",rootView,"error",false);
                    }
                }

            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(AddStoreActivity.this, myApiResponses.getError(),rootView);
                }
            }
        });

        mStoreViewModel.getApiResponse().observe(this,myApiResponses->{
            if(myApiResponses.getStatus().contentEquals("success")) {
                if (myApiResponses.getOperation().contains("apigetstore")) {
                    StoreResponse storeResponse = (StoreResponse) myApiResponses.getObject();
                    Timber.v("apigetstore ");
                    Timber.v(new Gson().toJson(storeResponse.getStoreList().get(0)));
                    currentStore = storeResponse.getStoreList().get(0);
                    initEditTextView();

                    storeType = currentStore.getStoretype();
                    disposable.add(Observable
                            .fromCallable(() -> {
                                for (int i = 0; i < storeTypeList.size(); i++) {
                                    if (currentStore.getStoretype().contentEquals(storeTypeList.get(i))) {
                                        return i;
                                    }
                                }
                                return 0;
                            }).delay(2, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableObserver<Integer>() {
                                @Override
                                public void onNext(Integer s) {
                                    Timber.v("currentStore.getStoretype() " + currentStore.getStoretype() + "/ " + storeTypeList.get(s));
                                    spinnerStoreType.setSelection(s);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            }));
                    validate_info(true);
                }

                if (myApiResponses.getOperation().contains("apisave")) {
                    StoreResponse storeResponse = (StoreResponse) myApiResponses.getObject();

                    if (currentStore.getStoreid() == 0) {//user first add a store on onboarding
                        currentStore.setStoreid(storeResponse.getSingleData().getStoreid());
                        GlobalVariable.getCurrentUser().setStorename(currentStore.getName());
                        GlobalVariable.getCurrentUser().setStoreid(currentStore.getStoreid());
                        GlobalVariable.getCurrentUser().saveUserInfo(GlobalVariable.getCurrentUser(),this);
                        GlobalVariable.getCurrentUser().refreshUserInfo(this);
                        //show dialogue of adding many stores or exiting to main menu
                        GlobalMethod.goToActivity(AddStoreActivity.this, MenuActivity.class);
                    } else {//user is updating a store
                        GlobalMethod.showSuccess(this, storeResponse.getMessage(), false);
                    }

                }

            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(AddStoreActivity.this,(Throwable) myApiResponses.getError(),rootView);
                }
            }
        });

        if(currentStore.getStoreid() != 0){
            mStoreViewModel.getSingleStore(currentStore).observe(this,stores->{
                Timber.v("getSingleStore "+stores.size());
                if(stores.size() >0){
                    if(stores.get(0).getStoreid() != 0){

                    }
                }
            });
        }
    }

    /**
     * initializes button
     */
    private void initButton() {
        if(currentStore.getStoreid() != 0) {
            btnDelete.setVisibility(View.VISIBLE);
            btnContinue.setText("Update");
        }

        btnContinue.setTypeface(GlobalVariable.getMontserratMedium(this));

        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("stores", "cancreate")) {
            btnContinue.setVisibility(View.GONE);
        }

        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("stores", "candelete")) {
            btnDelete.setVisibility(View.GONE);

        }
    }

    /**
     * handles delete button clicks
     */
    @OnClick(R.id.btnDelete)
    public void deleteStore(){
        CodingMsg.tl(this,getString(R.string.under_development));
      //  mStoreViewModel.deleteSingleStore(currentStore);
    }

    /**
     * clears observable memeory
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    /**
     * initialializes observables
     */
    private void initObservables() {
        disposable.add(
                RxAdapterView.itemSelections(spinnerBusiness)
                        .skip(1)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(integer -> {
                            CheckedTextView t =  spinnerBusiness.getSelectedView().findViewById(R.id.text1);
                            t.setTypeface(GlobalVariable.getMontserratRegular(this));

                            if(integer == 0){
                                t.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextPlaceholderColor, null));
                            }else{
                                t.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextColor, null));
                            }
                            selectedBusiness = spinnerBusiness.getAdapter().getItem(integer).toString();
                        }));

        disposable.add(
                RxAdapterView.itemSelections(spinnerStoreType)
                        .skip(1)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(integer -> {
                            try {
                                CheckedTextView t = spinnerStoreType.getSelectedView().findViewById(R.id.text1);
                                t.setTypeface(GlobalVariable.getMontserratRegular(this));
                                if (integer == 0) {
                                    t.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextPlaceholderColor, null));
                                } else {
                                    t.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextColor, null));
                                }
                                storeType = spinnerStoreType.getAdapter().getItem(integer).toString();
                                validate_info(false);
                            }catch (Exception e){
                                Timber.v("spinner error "+e.getMessage());
                            }
                        }));


        Observable<Boolean> locationObservable = RxTextView.textChanges(etxtStoreWebsite).skip(1)
                .map(
                        charSequence -> validate_info(false)

                );
        Observable<Boolean> etxtcountryCodeObs = RxTextView.textChanges(etxtcountryCode).skip(1)
                .map(
                        charSequence -> validate_info(false)

                );Observable<Boolean> etxtStoreLocationObs = RxTextView.textChanges(etxtStoreLocation).skip(1)
                .map(
                        charSequence -> validate_info(false)

                );

        Observable<Boolean> phoneObservable = RxTextView.textChanges(etxtStoreTelephone).skip(1)
                .map(
                        charSequence -> validate_info(false)

                );
        Observable<Boolean> observable = Observable.combineLatest(locationObservable, phoneObservable,
                (s, s2) -> validate_info(false));


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
     * toggles button color/ depending on validity of store information
     * @param valid true for active state
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
     * validates store data
     * @param showerrors flag to display error to user
     * @return boolean true if store data is valid
     */
    private Boolean validate_info(boolean showerrors) {
        if(currentStore.getStoreid() == 0){
            currentStore = new Store();
        }


        if(etxtStoreName.getText().toString().length() < 2){
            if(showerrors){
                etxtStoreName.setError("Enter your store name");
            }

            return false;
        }

        if(etxtStoreLocation.getText().toString().length() < 2){
            if(showerrors){
                etxtStoreLocation.setError("Enter the store location");
            }

            return false;
        }



        if(etxtStoreTelephone.getText().toString().length() < 2){
            if(showerrors){
                etxtStoreTelephone.setError("Invalid phone number");
            }

            return false;
        }

        if(etxtcountryCode.getText().toString().isEmpty()){
            if(showerrors){
                CodingMsg.tl(this,"Select your country dial code");
            }
            return false;
        }
        if(storeType.contentEquals("Store Type")){
            if(showerrors){
                CodingMsg.tl(this,"Select store type");
            }

            return false;
        }
        updateButton(true);
        currentStore.setCountry(etxtcountry.getText().toString());
        currentStore.setCountrycode(etxtcountryCode.getText().toString());
        currentStore.setStoretype(storeType);
        currentStore.setPhonenumber(etxtStoreTelephone.getText().toString());
        currentStore.setLocation(etxtStoreLocation.getText().toString());
        currentStore.setStorename(etxtStoreName.getText().toString());
        currentStore.setPostaladdress(etxtStorePostalAddr.getText().toString());
        currentStore.setEmail(etxtStoreEmail.getText().toString());
        currentStore.setWebsite(etxtStoreWebsite.getText().toString());

        return true;
    }

    /**
     * initializes edittextview
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEditTextView() {

//        etxtStoreName.setText("StoreTest1");
//        etxtStoreLocation.setText("Nairobi");
//        etxtStoreTelephone.setText("719124626");
//        etxtStorePostalAddr.setText("postadddr");
//        etxtStoreEmail.setText("store@gmail.com");
//        etxtStoreWebsite.setText("http://store.co.ke");
        if(GlobalVariable.getCurrentUser().getCurrentBsRule("stores", "cancreate")) {

            etxtcountry.setOnClickListener(v -> {
                HashMap<String, String> data = new HashMap<>();
                data.put("action", "");
                GlobalMethod.goToActivity(AddStoreActivity.this, CountryCodeActivity.class, data, COUNTRY_REQUEST_CODE);
            });
            etxtcountryCode.setOnClickListener(v -> {
                HashMap<String, String> data = new HashMap<>();
                data.put("action", "showcode");
                GlobalMethod.goToActivity(AddStoreActivity.this, CountryCodeActivity.class, data, COUNTRY_REQUEST_CODE);
            });

            etxtStoreName.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtStoreLocation.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtStoreTelephone.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtStorePostalAddr.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtStoreEmail.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtStoreWebsite.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtStoreWebsite.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if(btnContinue.isEnabled()){
                        Timber.v("save store");
                        saveStore();
                    }else{
                        CodingMsg.tl(AddStoreActivity.this,"Please confirm you have filled all the necessary information");
                    }
                    //do here your stuff f
                    return true;
                }
                return false;
            });

        }



        if(currentStore.getStoreid() != 0){
            etxtStoreName.setText(currentStore.getName());
            etxtStoreLocation.setText(currentStore.getLocation());
            etxtStoreTelephone.setText(currentStore.getPhonenumber());
            etxtStorePostalAddr.setText(currentStore.getPostaladdress());
            etxtStoreEmail.setText(currentStore.getEmail());
            etxtStoreWebsite.setText(currentStore.getWebsite());
            etxtcountryCode.setText(currentStore.getCountrycode());
            etxtcountry.setText(currentStore.getCountry());
        }
    }

    /**
     * initializes textview
     */
    private void initTextview() {
        if(currentStore.getStoreid() != 0){
            txtInfo.setVisibility(View.GONE);
        }
    }

    /**
     * initializes floatign button
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
        setTitle("About Your Store");
        if(currentStore.getStoreid() != 0){
            setTitle("Edit/View Your Store");
        }

        // making all textview use monteserrat font
//        for(int i = 0; i < toolbar.getChildCount(); i++)
//        {
//            View view = toolbar.getChildAt(i);
//            if(view instanceof TextView) {
//                TextView textView = (TextView) view;
//                textView.setTypeface(GlobalVariable.getMontserratMedium(this));
//            }
//        }
    }

    /**
     * implementation of store adapter
     * @param position the position
     * @param val      the val
     * @param action   the action
     */
    @Override
    public void strMessage(int position, String val, String action) {

    }

    /**
     * sends store data to its viewmodel
     */
    public void saveStore(){
        if(validate_info(true)) {
            currentStore.setBusinessid(currentBusiness.getBusinessid());
            currentStore.setStoretypeid(mStoreTypeH.get(storeType));
            GlobalMethod.showloader(this,"Saving store",true);
            mStoreViewModel.insert(currentStore);
        }
    }

    /**
     * handles continue button clicks events
     */
    @OnClick(R.id.btnContinue)
    public void submit() {
        saveStore();
    }

    /**
     * handles menu item clicks
     *
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
              GlobalMethod.goToActivity(this,MenuActivity.class);

                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }

    /**
     * handles activiry result responses
     * @param requestCode request code
     * @param resultCode result code
     * @param data data received
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.v("onActivityResult"+data.getStringExtra(CountryCodeActivity.EXTRA_REPLY));
        if (requestCode == COUNTRY_REQUEST_CODE && resultCode == RESULT_OK) {
            Country country = new Gson().fromJson(data.getStringExtra(CountryCodeActivity.EXTRA_REPLY),Country.class);
            Timber.v("onActivityResult "+country.getDialCode());
                etxtcountryCode.setText(country.getDialCode());
                etxtcountry.setText(country.getName());

        }


    }



}
