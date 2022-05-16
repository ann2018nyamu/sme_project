package ke.co.eclectic.quickstore.activities.auth;

import android.arch.lifecycle.ViewModelProviders;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.User;
import ke.co.eclectic.quickstore.rest.response.UserResponse;
import ke.co.eclectic.quickstore.viewModel.UserViewModel;
import timber.log.Timber;

/**
 * Actvity responsible for validating user existence in  the application
 * @author  David Manduku(Eclectics International Ltd)
 * @version 2018.1209
 * @since 1.0
 */
public class SmsVerificationActivity extends AppCompatActivity {

    /**
     * The Tool bar.
     */
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    /**
     * The displays verify info.
     */
    @BindView(R.id.txtVerifyInfo)
    TextView txtVerifyInfo;
    /**
     * The Edittext of the first verification character
     */
    @BindView(R.id.etxt1)
     EditText etxt1;
    /**
     * The Edittext of the second verification character
     */
    @BindView(R.id.etxt2)
     EditText etxt2;
    /**
     * The Edittext of the third verification character
     */
    @BindView(R.id.etxt3)
     EditText etxt3;
    /**
     * The Edittext of the fouth verification character
     */
    @BindView(R.id.etxt4)
     EditText etxt4;
    /**
     * The Edittext of the fifth verification character
     */
    @BindView(R.id.etxt5)
     EditText etxt5;

    /**
     * The Btn verify.
     */
    @BindView(R.id.btnVerify)
    Button btnVerify;
    /**
     * The  resend code button.
     */
    @BindView(R.id.btnResendCode)
    Button btnResendCode;
    /**
     * The fragment Root view.
     */
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;
    /**
     * Holds subscription event for later managment
     */
    private CompositeDisposable disposable = new CompositeDisposable();
    /**
     * UserViewModel for exposing user related crud functionalities
     */
    UserViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification);
        ButterKnife.bind(this);
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        initToolbar();
        initEditText();
        initData();
    }

    /**
     * initializes user data
     */
    private void initData() {
        mUserViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());

            if(myApiResponses.getStatus().contentEquals("success")){
                UserResponse userResponse =  (UserResponse)myApiResponses.getObject();

                if(myApiResponses.getOperation().contentEquals("apivalidateotp")){
                    GlobalMethod.stoploader(SmsVerificationActivity.this);
                    GlobalMethod.goToActivity(SmsVerificationActivity.this,PasswordActivity.class);
                }
                if(myApiResponses.getOperation().contentEquals("apiresetpassword")){

                    CodingMsg.tls(this,userResponse.getMessage());
                }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(this,myApiResponses.getError(),rootView);
                }
            }
        });
    }

    /**
     * initializes edittextview data
     */
    private void initEditText() {
        Timber.v("initEditText non");
        etxt1.setFocusable(true);
        disposable.add(  RxTextView.textChanges(etxt1).skip(1).subscribe(text -> {
            if(text.toString().length()>0){
                etxt2.requestFocus();
            }
            updateButton();
        }));
        disposable.add(RxTextView.textChanges(etxt2).skip(1).subscribe(text -> {

            if(text.toString().length()>0){
                etxt3.requestFocus();
            }else{
                etxt1.requestFocus();
            }
            updateButton();

        }));
        disposable.add(RxTextView.textChanges(etxt3).skip(1).subscribe(text -> {

            if(text.toString().length()>0){
                etxt4.requestFocus();
            }else{
                etxt2.requestFocus();
            }
            updateButton();

        }));

        disposable.add(RxTextView.textChanges(etxt4).skip(1).subscribe(text -> {

            if(text.toString().length()>0){
                etxt5.requestFocus();
            }else{
                etxt3.requestFocus();
            }
            updateButton();

        }));

    disposable.add(RxTextView.textChanges(etxt5).skip(1).subscribe(text -> {

            if(text.toString().length()>0){

                updateButton();
            }else{
                etxt4.requestFocus();
            }
            updateButton();

        }));


    }

    /**
     * Validate user data info boolean.
     *
     * @return the boolean
     */
    public boolean validate_info(){

        if(etxt1.getText().toString().length() > 0
                && etxt2.getText().toString().length() > 0
                && etxt3.getText().toString().length() > 0
                && etxt4.getText().toString().length() > 0
                && etxt5.getText().toString().length() > 0
                ){
          String otp =  etxt1.getText().toString()+
                     etxt2.getText().toString()+
                     etxt3.getText().toString()+
                     etxt4.getText().toString()+
                     etxt5.getText().toString();


            GlobalVariable.getCurrentUser().setPassword(otp);
            return  true;
        }else{
            GlobalVariable.getCurrentUser().setPassword("0");
            return  false;
        }
    }

    /**
     * Updates button appearance.
     *
     */
    public void updateButton() {
        boolean valid = validate_info();
        if (valid) {
            btnVerify.setEnabled(true);
            btnVerify.setBackgroundResource(R.drawable.btn_active);
        }else{
            btnVerify.setEnabled(false);
            btnVerify.setBackgroundResource(R.drawable.btn_inactive);
        }
    }

    /**
     * listens to verification button click event.
     */
    @OnClick(R.id.btnVerify)
    public void submit() {
        Timber.v("verify submit ");
     //   verifyCode();
        if(! btnVerify.isEnabled()){return;}
        GlobalMethod.showloader(this,"verifying..",true);  User user = new User();
        user.setPhonenumber(GlobalVariable.getCurrentUser().getCountrycode().concat(GlobalVariable.getCurrentUser().getPhonenumber()));
        user.setPassword(GlobalVariable.getCurrentUser().getPassword());
        mUserViewModel.validateotp(user);

    }

    /**
     *listens to verification button click event.
     */
    @OnClick(R.id.btnResendCode)
    public void resendC() {
        Timber.v("verify submit ");
        GlobalMethod.showloader(this,getString(R.string.resending),true);
        User user = new User();
        user.setPhonenumber(GlobalVariable.getCurrentUser().getCountrycode().concat(GlobalVariable.getCurrentUser().getPhonenumber()).replace("+",""));
        mUserViewModel.resetpassword(user);
    }

    /**
     * sends resend request to api
     */
    private void resendCode() {
//        GlobalMethod.showloader(this,getString(R.string.resending),true);

//        GlobalVariable.getCurrentUser().setRequest_type("change");
//        HashMap<String,Object> data = new HashMap<>();
//        data.put("request_type","change");
//        data.put("phonenumber",GlobalVariable.getCurrentUser().getCountrycode().concat(GlobalVariable.getCurrentUser().getPhonenumber()).replace("+",""));

//        disposable.add(
//                ApiClient.getApi().api( new JsonParser().parse(new Gson().toJson(data)))
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeWith(new DisposableSingleObserver<UserResponse>(){
//                            @Override
//                            public void onSuccess(UserResponse userResponse) {
//                                GlobalMethod.stoploader(SmsVerificationActivity.this);
//                                if(userResponse.getStatus().contentEquals("success")){
//                                    GlobalMethod.showSuccess(SmsVerificationActivity.this,userResponse.getMessage(),true);
//                                }
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                GlobalMethod.parseCommError(SmsVerificationActivity.this,e,rootView);
//                                //CodingMsg.t(getActivity(),e.getMessage());
//                            }
//                        })
//        );

    }

    /**
     * sends user entered code to api
     */
    private void verifyCode() {
      //  if(! btnVerify.isEnabled()){return;}

    //    GlobalMethod.showloader(this,"verifying..",true);
//        GlobalVariable.getCurrentUser().setRequest_type("validateotp");
//        HashMap<String,Object> data = new HashMap<>();
//        data.put("request_type","validateotp");
//        data.put("otp",Integer.parseInt(GlobalVariable.getCurrentUser().getPassword()));
//        data.put("phonenumber",GlobalVariable.getCurrentUser().getCountrycode().concat(GlobalVariable.getCurrentUser().getPhonenumber()));
        //GlobalMethod.goToActivity(this,PasswordActivity.class);
//        disposable.add(
//                ApiClient.getApi().api(new JsonParser().parse(new Gson().toJson(data)))
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeWith(new DisposableSingleObserver<UserResponse>(){
//                            @Override
//                            public void onSuccess(UserResponse userResponse) {
//                                GlobalMethod.stoploader(SmsVerificationActivity.this);
//                                if(userResponse.getStatus().contentEquals("success")){
//                                    GlobalMethod.goToActivity(SmsVerificationActivity.this,PasswordActivity.class);
//                                }
//                            }
//                            @Override
//                            public void onError(Throwable e) {
//
//                                GlobalMethod.parseCommError(SmsVerificationActivity.this,e,rootView);
//                                //CodingMsg.t(getActivity(),e.getMessage());
//                            }
//                        })
//        );
    }


    /**
     * initializes toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolBar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.sms_verification);

    }

    /**
     * listens to clicked menu item
     * @param item clicked menu item
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
