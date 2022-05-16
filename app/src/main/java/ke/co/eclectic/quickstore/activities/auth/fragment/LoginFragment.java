package ke.co.eclectic.quickstore.activities.auth.fragment;


import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.business.BusinessManageActivity;
import ke.co.eclectic.quickstore.activities.CountryCodeActivity;
import ke.co.eclectic.quickstore.activities.MenuActivity;
import ke.co.eclectic.quickstore.activities.stores.AddStoreActivity;
import ke.co.eclectic.quickstore.activities.auth.AuthActivity;
import ke.co.eclectic.quickstore.activities.auth.SmsVerificationActivity;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Business;
import ke.co.eclectic.quickstore.models.Country;
import ke.co.eclectic.quickstore.models.Roles;
import ke.co.eclectic.quickstore.models.User;
import ke.co.eclectic.quickstore.rest.response.UserResponse;
import ke.co.eclectic.quickstore.viewModel.UserViewModel;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;



/**
 * Fragment for authenticating a user.
 * @author  David Manduku(Eclectics International Ltd)
 * @version 2018.1209
 * @since 1.0
 */
public class LoginFragment extends Fragment {
    private static final int COUNTRY_REQUEST_CODE1 = 19;

    /**
     *EditText for holding/displaying  password value.
     */
    @BindView(R.id.etxtPassword)
    EditText etxtPassword;

    /**
     * EditText for holding/displaying phone number.
     */
    @BindView(R.id.etxtPhoneNumber)
    EditText etxtPhoneNumber;
    /**
     *  EditText for holding/displaying country code.
     */
    @BindView(R.id.etxtcountryCode)
    EditText etxtcountryCode;
    /**
     * Button for holding  login event.
     */
    @BindView(R.id.btnLogin)
    Button btnLogin;
    /**
     * Button for initiating forgot pass action.
     */
    @BindView(R.id.btnForgotPass)
    Button btnForgotPass;

    /**
     * Textview for redirecting to the terms and condition view.
     */
    @BindView(R.id.txtTerms)
     TextView txtTerms;
    /**
     * Textview for redirecting to the privacy and policy view.
     */
    @BindView(R.id.txtPrivacyPolicy)
    TextView txtPrivacyPolicy;

    private String password="";
    private String username="";
    /**
     * The Observable for checking if valid information has been entered by user.
     */
    Observable<Boolean> observable;

    /**
     * Holds subscription event for later managment
     */
    CompositeDisposable disposable = new CompositeDisposable();

    /**
     * UserViewModel for exposing user related crud functionalities
     */
    UserViewModel mUserViewModel;
    private AuthActivity mAuthActivity;


    /**
     * Instantiates a new Login fragment.
     */
    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * New instance login fragment.
     *
     * @return the login fragment
     */
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, rootView);
        //init();
        mAuthActivity = (AuthActivity) getActivity();
        initEdittext();
        initButton();
        initTextview();
        initObservable();
        initDbData();
        return rootView;
    }

    /**
     * Initializes this fragment with relevant user data
     */
    public void  initDbData(){
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mUserViewModel.deleteAll();
        //autologin
        mUserViewModel.getAllUsers().observe(this,users -> {
            Timber.v("users.size() "+users.size());
            if(users.size() > 0){
//                GlobalVariable.setCurrentUser(users.get(0));
//                Timber.v("users.size() "+new Gson().toJson(GlobalVariable.getCurrentUser()));
//                ArrayList<Roles> rolesList = GlobalVariable.getCurrentUser().getRoles();
//                Timber.v("users.rolesList() "+rolesList.size());
//                if( rolesList.size() == 0 ){
//                    Business currentBusiness = new Business();
//                    HashMap<String,String> data = new HashMap<>();
//                    data.put("business",new Gson().toJson(currentBusiness));
//                    GlobalMethod.goToActivity(getActivity(),BusinessManageActivity.class,data);
//                }else if(rolesList.size() == 1 &&
//                        rolesList.get(0).getStorecount() == 0 &&
//                        rolesList.get(0).getCreatedby().intValue() == GlobalVariable.getCurrentUser().getUserid().intValue()){
//                    HashMap<String,String> data = new HashMap<>();
//                    Business currentBusiness = new Business();
//                    currentBusiness.setBusinessid(rolesList.get(0).getBusinessid());
//                    currentBusiness.setbName(rolesList.get(0).getBusinessname());
//                    data.put("business",new Gson().toJson(currentBusiness));
//                    GlobalMethod.goToActivity(getActivity(),AddStoreActivity.class,data);
//                }else{
//                    GlobalMethod.goToActivity(getActivity(),MenuActivity.class);
//                }
            }else{
//              Timber.v("login first "+users.size());
//              //Todo remove automatic password
                etxtPhoneNumber.setText("715702887");//  719124626
                etxtPassword.setText("testpassword");//  1122
                etxtcountryCode.setText("+355");//+355  254
                password = "testpassword";
                username = "715702887";
                btnLogin.setEnabled(true);
               // login();
                //Todo remove automatic password
            }

        });

        mUserViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            if(myApiResponses.getStatus().contentEquals("success")){

                if(myApiResponses.getOperation().contentEquals("apiresetpassword")){
                    UserResponse userResponse =  (UserResponse)myApiResponses.getObject();

                    GlobalMethod.showSuccess(getActivity(),userResponse.getMessage(),true);
                    GlobalMethod.goToActivity(getActivity(),SmsVerificationActivity.class);
                }

                if(myApiResponses.getOperation().contentEquals("apilogin")){

                    ArrayList<Roles> rolesList = GlobalVariable.getCurrentUser().getRoles();

                    Timber.v("users.rolesList() "+rolesList.size());
                    if( GlobalVariable.getCurrentUser().getBusinessH(getActivity()).size() == 0 ){
                        GlobalVariable.getCurrentUser().setBusinessid(9999999);
                        Business currentBusiness = new Business();
                        HashMap<String,String> data = new HashMap<>();
                        data.put("business",new Gson().toJson(currentBusiness));
                        GlobalMethod.goToActivity(getActivity(),BusinessManageActivity.class,data);
                    }else if(rolesList.size() == 1 &&
                            rolesList.get(0).getStorecount() == 0 &&
                            rolesList.get(0).getCreatedby().intValue() == GlobalVariable.getCurrentUser().getUserid().intValue()){
                        HashMap<String,String> data = new HashMap<>();
                        Business currentBusiness = new Business();
                        currentBusiness.setBusinessid(rolesList.get(0).getBusinessid());
                        currentBusiness.setbName(rolesList.get(0).getBusinessname());
                        data.put("business",new Gson().toJson(currentBusiness));
                        GlobalMethod.goToActivity(getActivity(),AddStoreActivity.class,data);
                    }else{
                        GlobalMethod.goToActivity(getActivity(),MenuActivity.class);
                    }
                }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(getActivity(),myApiResponses.getError(),mAuthActivity.getRootView());
                }
            }
        });
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        //releasing memory allocated by rx operations
        disposable.clear();
    }
    /*
      * preparing all textview used in login fragment
     */

    private void initTextview() {
        txtTerms.setOnClickListener(v -> CodingMsg.tls(getActivity(),getString(R.string.under_development)));
        txtPrivacyPolicy.setOnClickListener(v -> CodingMsg.tls(getActivity(),getString(R.string.under_development)));

    }
    /*
     * preparing all Edit Textview used in login fragment
     */

    @SuppressLint("ClickableViewAccessibility")
    private void initEdittext() {
        etxtcountryCode.setOnClickListener(view -> {
                // GlobalMethod.goToActivity(getActivity(), CountryActivity.class);
                HashMap<String,String> data = new HashMap<>();
                data.put("action","showcode");
                GlobalMethod.goToActivity(this, CountryCodeActivity.class,data,COUNTRY_REQUEST_CODE1);

        });

        etxtPhoneNumber.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });
        etxtPassword.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });


        etxtPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                Timber.v("setOnEditorActionListener");
                if(isValidForm(true)){
                    Timber.v("isEnabled");
                    login();
                }
                //do here your stuff f
                return true;
            }
            return false;
        });
    }

    /*
     * preparing observables used in login fragment
     */
    private void initObservable(){
        Timber.v("users.initObservable() ");
        //creating observables
        Observable<String> nameObservable = RxTextView.textChanges(etxtPhoneNumber)
                .skip(1).map(CharSequence::toString);

        Observable<String> passwordObservable = RxTextView.textChanges(etxtPassword)
                .skip(1).map(CharSequence::toString);
       // Observable<String> ccodeObservable =
           disposable.add(  RxTextView.textChanges(etxtcountryCode)
                   .skip(1).map(CharSequence::toString).subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(  onNext->{
                                 isValidForm(false);
             },
                     error ->{

                     }
               ));


        //combining observables
        observable = Observable.combineLatest(nameObservable, passwordObservable,
                (s, s2) -> isValidForm(false));

       disposable.add(
               observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
               .subscribe(
                       onNext->{
                           isValidForm(false);
                      },
                       error->{

                       }

                       ));



    }

    /**
     * Changes the appearance of the button by toggling button color
     *
     * @param valid a flag to indicate how to update the login button
     */
    public void updateButton(boolean valid) {

        if (valid) {
            btnLogin.setEnabled(true);
            btnLogin.setBackgroundResource(R.drawable.btn_active);
        }else{
            btnLogin.setEnabled(false);
            btnLogin.setBackgroundResource(R.drawable.btn_inactive);
        }
    }

  /**
   * Initializes all the buttons used in login fragment
   *
   */

    private void initButton() {
       // btnLogin.setOnClickListener(this);
        btnLogin.setTypeface(GlobalVariable.getMontserratMedium(getActivity()));
    }


    /**
     * Does login operation
     */
    public void login(){
        GlobalMethod.showloader(getActivity(),getString(R.string.loading),true);
        User user = new User();
        user.setPhonenumber(etxtcountryCode.getText().toString().concat(username).replace("+",""));
        user.setPassword(password);
        mUserViewModel.loginUser(user);

    }

    /**
     * Listening to login button click event
     */
    @OnClick(R.id.btnLogin)
    public void submit() {
       if( isValidForm(true)){
           login();
       }

    }

    /**
     * Handles the forgot password button event
     */
    @OnClick(R.id.btnForgotPass)
    public void forgotpassword(){
        if(etxtcountryCode.getText().toString().length()<1){etxtcountryCode.setError("Choose dial code");return;}
        if(1 > etxtPhoneNumber.getText().toString().length() ){
            etxtPhoneNumber.setError("Enter your phonenumber");
            return;
        }

        GlobalMethod.showloader(getActivity(),"Sending request..",true);
        Timber.v("forgotpassword"+etxtcountryCode.getText().toString().concat(etxtPhoneNumber.getText().toString()).replace("+",""));

        String phoneNo = etxtcountryCode.getText().toString().concat(etxtPhoneNumber.getText().toString()).replace("+","");
        GlobalVariable.getCurrentUser().setPhonenumber(etxtPhoneNumber.getText().toString());
        GlobalVariable.getCurrentUser().setCountrycode(etxtcountryCode.getText().toString());
        User user = new User();
        user.setPhonenumber(phoneNo);
        mUserViewModel.resetpassword(user);



        }

    /**
     * Is valid form boolean.
     *
     * @return the boolean true if the form data is valid
     */
    public boolean isValidForm(boolean showErrors) {
        Timber.v("isValidForm ");
        String phone = etxtPhoneNumber.getText().toString();
        String pass = etxtPassword.getText().toString();

        boolean emailPhoneB ;
            emailPhoneB = !phone.isEmpty();
            if (!emailPhoneB) {
                if(showErrors){
                    etxtPhoneNumber.setError("Please enter valid phone");
                }

                return false;
            }


        if(etxtcountryCode.getText().toString().isEmpty()){
            if(showErrors) {
                etxtcountryCode.setError("Choose dial code");
            }
            return false;
        }

        boolean validPass = !pass.isEmpty();

        if (!validPass) {
            if(showErrors) {
                etxtPassword.setError("Incorrect password");
                return false;
            }
        }

        password = pass;
        username = phone;

        updateButton(emailPhoneB && validPass);

        Timber.v("isValidForm  passed");
        return emailPhoneB && validPass;
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
        if (requestCode == COUNTRY_REQUEST_CODE1 && resultCode == RESULT_OK) {
            Country country = new Gson().fromJson(data.getStringExtra(CountryCodeActivity.EXTRA_REPLY),Country.class);
            etxtcountryCode.setText(country.getDialCode());
        }

    }


}
