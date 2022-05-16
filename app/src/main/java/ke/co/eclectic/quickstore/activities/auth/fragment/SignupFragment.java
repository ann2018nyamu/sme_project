package ke.co.eclectic.quickstore.activities.auth.fragment;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.CountryCodeActivity;
import ke.co.eclectic.quickstore.activities.auth.AuthActivity;
import ke.co.eclectic.quickstore.activities.auth.SmsVerificationActivity;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Country;
import ke.co.eclectic.quickstore.models.User;
import ke.co.eclectic.quickstore.rest.response.UserResponse;
//import retrofit2.HttpException;
import ke.co.eclectic.quickstore.viewModel.UserViewModel;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment responsible for user registration to the system
 * @author  David Manduku
 * @version 2018.1209
 * @since 1.0
 */
public class SignupFragment extends Fragment {
    /**
     * request code for getting country information(activity level)
     */
    private static final int COUNTRY_REQUEST_CODE = 20;

    /**
     * stores country code value
     */
    private String countrycode="";

    /**
     * Displays multiple gender status.
     */
    @BindView(R.id.spinnerGender)
    Spinner spinnerGender;
    /**
     * Holds user phonenumber data
     */
    @BindView(R.id.etxtPhoneNumber)
     EditText etxtPhoneNumber;
    /**
     * Holds user  national id and passport number
     */
    @BindView(R.id.etxtIdPassport)
     EditText etxtIdPassport;
    /**
     * Holds user date of birth
     */
    @BindView(R.id.etxtDob)
     EditText etxtDob;
    /**
     * Holds user email address
     */
    @BindView(R.id.etxtEmail)
     EditText etxtEmail;
    /**
     * Holds user selected country
     */
    @BindView(R.id.etxtcountry)
     EditText etxtcountry;
    /**
     * Holds user first name
     */
    @BindView(R.id.etxtFname)
     EditText etxtFname;
    /**
     * Holds user middle name
     */
    @BindView(R.id.etxtMname)
     EditText etxtMname;
    /**
     * Holds user lastname
     */
    @BindView(R.id.etxtLname)
     EditText etxtLname;
    /**
     * Holds user country code
     */
    @BindView(R.id.etxtSignUpcountryCode)
     EditText etxtSignUpcountryCode;
    /**
     * Button for initiating asignup event/process
     */
    @BindView(R.id.btnSignup)
     Button btnSignup;
    /**
     * stores gender value.
     */
    String gender="";
    /**
     * Holds/instatiates  terms and conditions text views
     */
    @BindView(R.id.txtTerms2)
     TextView txtTerms2;


    /**
     * Holds/instatiates privacy policy textview
     */
    @BindView(R.id.txtPrivacyPolicy2)
     TextView txtPrivacyPolicy2;

    /**
     * Holds subscription event for later managment
     */
    private CompositeDisposable disposable = new CompositeDisposable();
    /**
     * flag for differentiating country and country code request event
     */
    private boolean isDialCode = false;
    /**
     * UserViewModel for exposing user related crud functionalities
     */
    private UserViewModel mUserViewModel;
    /**
     * hold user selected year value data
     */
    private int year;
    /**
     * holds user selected month data
     */
    private int month;
    /**
     * holds user selected day data
     */
    private int day;
    /**
     * Initializing Datedialogue listener
     */
    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {
        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date date = null;
            try {
                date =  formatter.parse(String.valueOf(year) + "-" + (month + 1) + "-" + day);
                formatter2.format(date);
                etxtDob.setText(formatter2.format(date));
                Calendar c = Calendar.getInstance();
                Calendar c2 = Calendar.getInstance();
                c.setTime(date);
                int diff = c2.get(Calendar.YEAR) - c.get(Calendar.YEAR);
                if(diff < 18){
                    etxtDob.getText().clear();
                    CodingMsg.tl(getActivity(),"Sorry your are under aged ");
                }


            }catch (Exception e){
             Timber.v("Date error".concat(e.getMessage()));
            }

        }
    };
    private AuthActivity mAuthActivity;


    /**
     * New instance signup fragment.
     *
     * @return the signup fragment
     */
    public static SignupFragment newInstance( ) {
        return new SignupFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        /*
      hold fragment view
     */ /**
         * hold fragment view
         */View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        ButterKnife.bind(this, rootView);
        mAuthActivity = (AuthActivity) getActivity();
        initTextView();
        initEditTextView();
        initSpinner();
        initObservables();
        initData();

        return rootView;
    }

    /**
     * Initializes relevant data required by the signup fragment
     */
    private void initData(){
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mUserViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            if(myApiResponses.getStatus().contentEquals("success")){
                UserResponse userResponse =  (UserResponse)myApiResponses.getObject();
                if(myApiResponses.getOperation().contentEquals("apisave")){
                    GlobalMethod.goToActivity(getActivity(),SmsVerificationActivity.class);
                }

            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(getActivity(),myApiResponses.getError(),mAuthActivity.getRootView());
                }
            }
        });
    }

    /**
     * Perform any final cleanup before an activity is destroyed
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        disposable.dispose();
    }

    /**
     * initailizes spinner data
     */
    private void initSpinner() {
        List<String> locationList =new ArrayList<>() ;
        //ArrayAdapter<String> adapter = ArrayAdapter.createFromResource(this, locationList, android.R.layout.simple_spinner_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mAuthActivity,  R.layout.spinner_dropdown_item, locationList);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item2);

        // Apply the adapter to the spinner
        locationList.add("Gender");
        locationList.add("Male");
        locationList.add("Female");

        spinnerGender.setAdapter(adapter);

    }

    /**
     * initializes edittextview data
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEditTextView() {
        final Calendar c = Calendar.getInstance();

        year  = c.get(Calendar.YEAR) - 15;
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);




        etxtSignUpcountryCode.setOnClickListener(v -> {
            isDialCode = true;
            HashMap<String,String> data = new HashMap<>();
            data.put("action","showcode");
            GlobalMethod.goToActivity(this, CountryCodeActivity.class,data,COUNTRY_REQUEST_CODE);
        });
        etxtcountry.setOnClickListener(view -> {
                HashMap<String,String> data = new HashMap<>();
                data.put("action","showcountry");
                GlobalMethod.goToActivity(this, CountryCodeActivity.class,data,COUNTRY_REQUEST_CODE);
        });
        etxtDob.setOnClickListener(view -> {
      new DatePickerDialog(mAuthActivity,R.style.DateTheme, pickerListener, year, month,day).show();

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


        etxtEmail.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                if(btnSignup.isEnabled()){
                    Timber.v("signup submit ");
                    signup();
                }else{
                    CodingMsg.tl(getActivity(),"Please confirm you have entered all necessary information");
                }
                //do here your stuff f
                return true;
            }
            return false;
        });

    }

    /**
     * initializes fragment observables
     */
        private void initObservables() {

        //creating observables
            disposable.add(
                    RxAdapterView.itemSelections(spinnerGender)
                            .skip(1)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(integer -> {
                                try {
                                    CheckedTextView t = spinnerGender.getSelectedView().findViewById(R.id.text1);
                                    t.setTypeface(GlobalVariable.getMontserratRegular(getActivity()));

                                    if (integer == 0) {
                                        t.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextPlaceholderColor, null));
                                    } else {
                                        t.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextColor, null));
                                    }

                                    gender = spinnerGender.getAdapter().getItem(integer).toString();
                                }catch (Exception e){
                                    Timber.v("Exception spinnerger"+e.getMessage());
                                }

                            }));

            Observable<String> countryObservable = RxTextView.textChanges(etxtcountry).skip(1).debounce(2,TimeUnit.SECONDS).map(new Function<CharSequence, String>() {
                @Override
                public String apply(CharSequence charSequence) throws Exception {
                    return charSequence.toString();
                }
            });
        Observable<String> phoneObservable = RxTextView.textChanges(etxtPhoneNumber).skip(1).debounce(2,TimeUnit.SECONDS).map(new Function<CharSequence, String>() {
            @Override
            public String apply(CharSequence charSequence) throws Exception {
                return charSequence.toString();
            }
        });
        Observable<String> idPassportObservable = RxTextView.textChanges(etxtIdPassport).skip(1).debounce(2,TimeUnit.SECONDS).map(new Function<CharSequence, String>() {
            @Override
            public String apply(CharSequence charSequence) throws Exception {
                return charSequence.toString();
            }
        });
        Observable<String> emailObservable = RxTextView.textChanges(etxtEmail).skip(1).debounce(3,TimeUnit.SECONDS).map(new Function<CharSequence, String>() {
            @Override
            public String apply(CharSequence charSequence) throws Exception {
                return charSequence.toString();
            }
        });
        Observable<String> dobObservable = RxTextView.textChanges(etxtDob).skip(1).debounce(2,TimeUnit.SECONDS).map(new Function<CharSequence, String>() {
            @Override
            public String apply(CharSequence charSequence) throws Exception {
                return charSequence.toString();
            }
        });
        Observable<String> mnameObservable = RxTextView.textChanges(etxtMname).skip(1).debounce(2,TimeUnit.SECONDS).map(new Function<CharSequence, String>() {
            @Override
            public String apply(CharSequence charSequence) throws Exception {
                return charSequence.toString();
            }
        });
        Observable<String> lnameObservable = RxTextView.textChanges(etxtLname).skip(1).debounce(2,TimeUnit.SECONDS).map(new Function<CharSequence, String>() {
            @Override
            public String apply(CharSequence charSequence) throws Exception {
                return charSequence.toString();
            }
        });
        Observable<String> fnameObservable = RxTextView.textChanges(etxtFname).skip(1).debounce(2,TimeUnit.SECONDS).map(new Function<CharSequence, String>() {
            @Override
            public String apply(CharSequence charSequence) throws Exception {
                return charSequence.toString();
            }
        });

        //subscribing an observer
           disposable.add( emailObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            validate_info();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
    }

    /**
     * Update button.
     *
     * @param valid the valid
     */
    public void updateButton(boolean valid) {
        if (valid) {
            btnSignup.setEnabled(true);
            btnSignup.setBackgroundResource(R.drawable.btn_active);
        }else{
            btnSignup.setEnabled(false);
            btnSignup.setBackgroundResource(R.drawable.btn_inactive);
        }
    }


    /**
     *
     * @return boolean flag for valid or invalid user information
     */
    private boolean validate_info() {
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
            CodingMsg.tl(getActivity(),"Select your Gender");
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
        if(etxtSignUpcountryCode.getText().toString().trim().isEmpty() || countrycode.contentEquals("")) {
            etxtSignUpcountryCode.setError("Select your country code");
            return false;
        }

        updateButton(true);
        Timber.v("all data inserted");

        GlobalVariable.setCurrentUser(new User());
        GlobalVariable.getCurrentUser().setCountrycode(etxtSignUpcountryCode.getText().toString());
        GlobalVariable.getCurrentUser().setGender(gender);
        GlobalVariable.getCurrentUser().setNationalidnumber(etxtIdPassport.getText().toString());
        GlobalVariable.getCurrentUser().setPhonenumber(etxtPhoneNumber.getText().toString());
        GlobalVariable.getCurrentUser().setEmail(etxtEmail.getText().toString());
        GlobalVariable.getCurrentUser().setDateofbirth(etxtDob.getText().toString());
        GlobalVariable.getCurrentUser().setFirstname(etxtFname.getText().toString());
        GlobalVariable.getCurrentUser().setMiddlename(etxtMname.getText().toString());
        GlobalVariable.getCurrentUser().setLastname(etxtLname.getText().toString());
        GlobalVariable.getCurrentUser().setOthernames(etxtMname.getText().toString().concat("  ").concat(etxtLname.getText().toString()));
        GlobalVariable.getCurrentUser().setNationality(etxtcountry.getText().toString());

        return true;
    }

    /**
     * initializes textview data/handling events
     */
    private void initTextView() {

        txtTerms2.setOnClickListener(v -> CodingMsg.tls(getActivity(),getString(R.string.under_development)));
        txtPrivacyPolicy2.setOnClickListener(v -> CodingMsg.tls(getActivity(),getString(R.string.under_development)));

    }





    /**
     * performes signup operations
     */
    public void  signup(){

        if(validate_info()) {
            mUserViewModel.saveUpdate(GlobalVariable.getCurrentUser());
        }
    }

    /**
     * Captures signup button events
     */
    @OnClick(R.id.btnSignup)
    public void submit() {
        Timber.v("signup submit ");
        signup();

    }

    /**
     *
     * @param requestCode request code sent to the activity/operation
     * @param resultCode result received by called activity/operation
     * @param data data return by the called activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COUNTRY_REQUEST_CODE && resultCode == RESULT_OK) {
            Country country = new Gson().fromJson(data.getStringExtra(CountryCodeActivity.EXTRA_REPLY),Country.class);
                if(isDialCode){
                    etxtSignUpcountryCode.setText(country.getDialCode());
                    etxtcountry.setText(country.getName());
                    countrycode = country.getDialCode();
                }else{
                    etxtcountry.setText(country.getName());
                }
            isDialCode = false;

        }



    }



}
