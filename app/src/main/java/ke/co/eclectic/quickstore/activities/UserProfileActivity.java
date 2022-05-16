package ke.co.eclectic.quickstore.activities;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.InventoryStock;
import ke.co.eclectic.quickstore.models.Store;
import ke.co.eclectic.quickstore.models.User;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.ProductResponse;
import ke.co.eclectic.quickstore.rest.response.UserResponse;
import ke.co.eclectic.quickstore.viewModel.CountryViewModel;
import ke.co.eclectic.quickstore.viewModel.UserViewModel;
import timber.log.Timber;

public class UserProfileActivity extends AppCompatActivity {

    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.rootView)
     CoordinatorLayout rootView;

    @BindView(R.id.spinnerGender)
    Spinner spinnerGender;
    @BindView(R.id.etxtcountryCode)
    EditText etxtcountryCode;
    @BindView(R.id.etxtPhoneNumber)
    EditText etxtPhoneNumber;
    @BindView(R.id.etxtIdPassport)
    EditText etxtIdPassport;
    @BindView(R.id.etxtDob)
    EditText etxtDob;
    @BindView(R.id.etxtEmail)
    EditText etxtEmail;
    @BindView(R.id.etxtcountry)
    EditText etxtcountry;
    @BindView(R.id.etxtFname)
    EditText etxtFname;
    @BindView(R.id.etxtMname)
    EditText etxtMname;
    @BindView(R.id.etxtLname)
    EditText etxtLname;
    @BindView(R.id.etxtStore)
    EditText etxtStore;
    @BindView(R.id.etxtRole)
    EditText etxtRole;
    @BindView(R.id.btnRemove)
    Button btnRemove;
    @BindView(R.id.btnEditRule)
    Button btnEditRule;

    @BindView(R.id.imgUserprev)
    CircleImageView imgUserprev;

    String gender="";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ArrayAdapter<String> countryCodeAdapter;
    private User updatedUser = new User();
    private boolean isDialCode = false;
    UserViewModel mUserViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            updatedUser =  new Gson().fromJson(extra.get("staffData").toString(),User.class);
        }

        Timber.v("updated "+new Gson().toJson(updatedUser));
        Timber.v("currrent "+new Gson().toJson(GlobalVariable.getCurrentUser()));

        ButterKnife.bind(this);
        //init();
        initToolbar();
        initButton();
        initData();
        initSpinner();

    }

    /**
     * repopulates data to views
     */
    public void prepopulateData(){
        Timber.v(new Gson().toJson(updatedUser));
        initEditTextView();
        initImageview();
        initButton();
        initSpinner();
        initObservables();
    }

    /**
     * handles resume state of the activity
     */
    @Override
    protected void onResume() {
        super.onResume();
            if (null != GlobalVariable.choosenCountry && !GlobalVariable.choosenCountry.getName().contentEquals("")) {
                if(isDialCode) {
                    etxtcountryCode.setText(GlobalVariable.choosenCountry.getDialCode());
                } else {
                    etxtcountry.setText(GlobalVariable.choosenCountry.getName());
                }
                isDialCode = false;
                GlobalVariable.choosenCountry = null;
            }
    }

    /**
     * initilizes data
     */
    private void initData(){
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        mUserViewModel.getApiResponse().observe(this, myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            if(myApiResponses.getStatus().contentEquals("success")){
                GlobalMethod.stoploader(this);
                if(myApiResponses.getOperation().contains("apiremove")) {
                    UserResponse userResponse = (UserResponse) myApiResponses.getObject();
                    GlobalMethod.showSuccess(this,userResponse.getMessage(),false);
                }

                if(myApiResponses.getOperation().contains("apisave")) {// user updates his/her profile
                    UserResponse userResponse = (UserResponse) myApiResponses.getObject();
                    GlobalMethod.showSuccess(this,userResponse.getMessage(),false);
                }

                if(myApiResponses.getOperation().contains("apigetstoreuser")) {
                    UserResponse userResponse = (UserResponse) myApiResponses.getObject();
                    updatedUser = userResponse.getStaffData();
                    prepopulateData();
                }

            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(this,myApiResponses.getError(),rootView);
                }
            }
        });

        fetchStaffData();
    }

    /**
     * fetches staff data
     */
    private void fetchStaffData(){

        GlobalMethod.showloader(this,"Fetching information...",true);
        //fetch user details
        mUserViewModel.getStoreUserDetail(updatedUser.getStoreuserid());

    }

    /**
     * initializes button
     */
    private void initButton() {
        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("staff", "cancreate")) {
            btnRemove.setVisibility(View.GONE);
        }
            if(updatedUser.getNationalidnumber().contentEquals(GlobalVariable.getCurrentUser().getNationalidnumber())){
            btnRemove.setText("Update Profile");
            btnRemove.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            btnRemove.setTextColor(getResources().getColor(R.color.errorColor));
        }else{
            btnRemove.setText("Remove Staff");
        }

        if( !updatedUser.getNationalidnumber().contentEquals(GlobalVariable.getCurrentUser().getNationalidnumber())
                &&    GlobalVariable.getCurrentUser().getCurrentBsRule("staff", "cancreate")) {
            btnEditRule.setText("Edit Rule");
        }

       // updateButton(true);
    }
    @OnClick(R.id.btnEditRule)
    public void editRule(){
        HashMap<String, String> data = new HashMap<>();
        data.put("staffData",new Gson().toJson(updatedUser));
        GlobalMethod.goToActivity(this, RulesActivity.class, data);
    }

    /**
     * handles remove button clicks event
     */
    @OnClick(R.id.btnRemove)
    public void removeUser(){
        if(btnRemove.getText().toString().toLowerCase().contains("update")){
            if(true){//Todo remove condition once api has been exposed
                CodingMsg.tl(this,"In  progress");
                return;
            }

            if(validate_info()){
                GlobalMethod.showloader(this,"Updating...",false);
                mUserViewModel.saveUpdate(updatedUser);
            }

        }else{
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Do you want to ".concat(btnRemove.getText().toString().toLowerCase()))
                    .setContentText("This action is irreversable")
                    .setConfirmText("Yes")
                    .setConfirmClickListener(sDialog -> {

                        if(true){//Todo remove condition once api has been exposed
                            CodingMsg.tl(this,"Under  Development");
                            return;
                        }
                        GlobalMethod.showloader(this,"Please wait as we process your request...",false);

                        if(btnRemove.getText().toString().toLowerCase().contains("delete")){
                            mUserViewModel.deleteUser(updatedUser.getStoreuserid());
                        }else{
                            mUserViewModel.removeStoreUser(updatedUser.getStoreuserid());
                        }
                    })
                    .show();
        }


    }

    /**
     *
     * @return boolean flag for valid or invalid user information
     */
    private boolean validate_info() {
        updateButton(false);
        if(etxtPhoneNumber.getText().toString().length() < 5) {
            etxtPhoneNumber.setError("invalid  phone number");
            return false;
        }
        if(!GlobalMethod.isValidEmail(etxtEmail.getText().toString())){
            etxtEmail.setError("invalid  email");
            return false;
        }


        if(etxtPhoneNumber.getText().toString().trim().isEmpty()) {
            etxtPhoneNumber.setError("invalid  phone number");
            return false;
        }

        if (gender.contentEquals("Gender")) {
            CodingMsg.tl(this,"Select your Gender");
            return false;
        }
        if(etxtDob.getText().toString().trim().isEmpty()) {
            etxtDob.setError("Invalid  date of birth");
            return false;

        }
        if(etxtFname.getText().toString().trim().isEmpty()) {
            etxtFname.setError("Enter first name");
            return false;

        }
        if(etxtMname.getText().toString().trim().isEmpty()) {
            etxtFname.setError("Enter middle name");
            return false;
        }
        if(etxtLname.getText().toString().trim().isEmpty()) {
            etxtLname.setError("Enter last name");
            return false;
        }
        if(etxtcountry.getText().toString().trim().isEmpty()) {
            etxtcountry.setError("Enter your country");
            return false;
        }
        if(etxtcountryCode.getText().toString().trim().isEmpty()) {
            etxtcountryCode.setError("Select your country code");
            return false;
        }

        updateButton(true);
        Timber.v("all data inserted");


        updatedUser.setCountrycode(etxtcountryCode.getText().toString());
        updatedUser.setGender(gender);
        updatedUser.setNationalidnumber(etxtIdPassport.getText().toString());
        updatedUser.setPhonenumber(etxtPhoneNumber.getText().toString());
        updatedUser.setEmail(etxtEmail.getText().toString());
        updatedUser.setDateofbirth(etxtDob.getText().toString());
        updatedUser.setFirstname(etxtFname.getText().toString());
        updatedUser.setMiddlename(etxtMname.getText().toString());
        updatedUser.setLastname(etxtLname.getText().toString());
        updatedUser.setOthernames(etxtMname.getText().toString().concat("  ").concat(etxtLname.getText().toString()));
        updatedUser.setNationality(etxtcountry.getText().toString());

        return true;
    }



    /**
     * initializes  observables
     */
    private void initObservables() {

        disposable.add(
                RxAdapterView.itemSelections(spinnerGender)
                        .skip(1)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(integer -> {
                            View t =  spinnerGender.getSelectedView();

                                CheckedTextView t1 = t.findViewById(R.id.text1);
                                t1.setTypeface(GlobalVariable.getMontserratRegular(this));
                            Timber.v("spinnerGender "+t1.getText().toString());
                                int dp = (int) (getResources().getDimension(R.dimen.spinner_tsize) / getResources().getDisplayMetrics().density);
                                t1.setTextSize(dp);
                                if (integer == 0) {
                                    t1.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextPlaceholderColor, null));
                                } else {
                                    t1.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextColor, null));
                                }
                                gender = spinnerGender.getAdapter().getItem(integer).toString();

                        }));
        
       
    }

    /**
     * clears obsrvable memory allocation
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    /**
     * initializes spinner
     */
    private void initSpinner() {
        List<String> genderList =new ArrayList<>() ;
        //ArrayAdapter<String> adapter = ArrayAdapter.createFromResource(this, locationList, android.R.layout.simple_spinner_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  R.layout.spinner_dropdown_item, genderList);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item2);

        // Apply the adapter to the spinner
        genderList.add("Gender");
        genderList.add("Male");
        genderList.add("Female");

        spinnerGender.setAdapter(adapter);
        for(int i =0 ;i<genderList.size() ; i++ ){
            if(updatedUser.getGender().toLowerCase().contentEquals(genderList.get(i).toLowerCase())) {
                spinnerGender.setSelection(i);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * toggle button active state depinding on validity of user information
     * @param valid boolean true if user inforation is valid
     */
    public void updateButton(boolean valid) {
        if (valid) {
            btnRemove.setEnabled(true);
            btnRemove.setBackgroundResource(R.drawable.btn_active);
        }else{
            btnRemove.setEnabled(false);
            btnRemove.setBackgroundResource(R.drawable.btn_inactive);
        }

    }


    /**
     * initializes imageview
     */
    private void initImageview() {
       if(updatedUser.getUserimg() != null && updatedUser.getUserimg().length() > 5){
           Picasso.get()
                   .load(GlobalVariable.getCurrentUser().getUserimg())
                   .fit()
                   .placeholder(R.drawable.placeholder_profile)
                   .error(R.drawable.placeholder_profile)
                   .into(imgUserprev);
       }


    }

    /**
     * initiliazes edittextview
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEditTextView() {
        //populate data
        etxtcountryCode.setText("+".concat(updatedUser.getCountrycode()));
        etxtPhoneNumber.setText(updatedUser.getPhonenumber());
        etxtIdPassport.setText(updatedUser.getNationalidnumber());
        etxtEmail.setText(updatedUser.getEmail());

        etxtcountry.setText(updatedUser.getNationality());
        etxtDob.setText(updatedUser.getDateofbirth());

        etxtMname.setText(updatedUser.getMiddlename());
        etxtLname.setText(updatedUser.getLastname());

        etxtFname.setText(updatedUser.getFirstname());
        etxtRole.setText(updatedUser.getRolename());
        etxtStore.setText(updatedUser.getStorename());



        //only the owner can edit his/her profile
        if( updatedUser.getNationalidnumber().contentEquals(GlobalVariable.getCurrentUser().getNationalidnumber()) ) {

            etxtStore.setOnClickListener(v -> {
                showStoresDialogue();
            });

            etxtcountryCode.setOnClickListener(v -> {
                isDialCode = true;
                HashMap<String, String> data = new HashMap<>();
                data.put("action", "showcode");
                GlobalMethod.goToActivity(this, CountryCodeActivity.class, data);
            });

            etxtcountry.setOnClickListener(v -> {
                HashMap<String, String> data = new HashMap<>();
                data.put("action", "showcountry");
                GlobalMethod.goToActivity(this, CountryCodeActivity.class, data);
            });

            etxtPhoneNumber.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });

            etxtIdPassport.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });

            etxtEmail.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtDob.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });

            etxtMname.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });

            etxtLname.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtFname.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
        }

    }
    /**
     * Displays store dialogue for user selection
     */
    private void showStoresDialogue() {
         ArrayList<String> storeList = new ArrayList<>();
        HashMap<String, Store> storeH = GlobalVariable.getCurrentUser().getBusinessStoresH();
        for(Map.Entry<String, Store> entry : storeH.entrySet()){
            storeList.add(entry.getKey());
        }


        String[]   mSelectedItem1 = storeList.toArray(new String[0]);
        //String[]   mSelectedItem1 = storeList.toArray(new String[storeList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle("Choose Store")
                .setSingleChoiceItems(mSelectedItem1, -1, (dialogInterface, i) -> {

                    updatedUser.setStoreuserid(storeH.get(mSelectedItem1[i]).getStoreuserid());
                    fetchStaffData();
                    dialogInterface.dismiss();

                });

        AlertDialog mDialog = builder.create();
        mDialog.show();
    }

    /**
     * initializes toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("View Profile");
        if(updatedUser.getUserid().intValue() == GlobalVariable.getCurrentUser().getUserid()) {
            setTitle("Edit Profile");
        }

            // making all textview use monteserrat font
        for(int i = 0; i < toolBar.getChildCount(); i++)
        {
            View view = toolBar.getChildAt(i);
            if(view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setTypeface(GlobalVariable.getMontserratMedium(this));
            }
        }
    }


    /**
     * handles menu item clicks events
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
