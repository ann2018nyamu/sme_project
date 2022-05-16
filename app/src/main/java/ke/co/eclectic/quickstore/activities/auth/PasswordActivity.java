package ke.co.eclectic.quickstore.activities.auth;

import android.annotation.SuppressLint;
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

//import com.github.mikephil.charting.matrix.Vector3;
import com.jakewharton.rxbinding2.widget.RxTextView;

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
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.User;
import ke.co.eclectic.quickstore.rest.response.UserResponse;
import ke.co.eclectic.quickstore.viewModel.UserViewModel;
import timber.log.Timber;

/**
 * Actvity responsible for changing user password to the  application
 * @author  David Manduku(Eclectics International Ltd)
 * @version 2018.1209
 * @since 1.0
 */
public class PasswordActivity extends AppCompatActivity {

    /**
     *EditText for holding/displaying  password value.
     */
    @BindView(R.id.etxtPassword)
    EditText etxtPassword;
    /**
     *EditText for holding/displaying  confirmation password value.
     */
    @BindView(R.id.etxtCPassword)
    EditText etxtCPassword;
    /**
     *Button for listening to change of pasword click event
     */
    @BindView(R.id.btnContinue)
    Button btnContinue;

    /**
     * The Tool bar.
     */
    @BindView(R.id.toolBar)
     Toolbar toolBar;
    /**
     * The Txt success info.
     */
    @BindView(R.id.txtSuccessInfo)
    TextView txtSuccessInfo;
    /**
     * The Txt info.
     */
    @BindView(R.id.txtInfo)
    TextView txtInfo;
    /**
     * The password Root view.
     */
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;


    /**
     * Holds subscription event for later managment
     */
    private CompositeDisposable disposable = new CompositeDisposable();

    private UserViewModel mUserViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);
        initToolbar();
        initEditText();
        initObservables();
        initData();
    }
    /**
     * Initializes this fragment with relevant user data
     */
    private void initData() {
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mUserViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            if(myApiResponses.getStatus().contentEquals("success")){
                UserResponse userResponse =  (UserResponse)myApiResponses.getObject();

                if(myApiResponses.getOperation().contentEquals("apisetpassword")){
                    GlobalMethod.goToActivity(PasswordActivity.this,AuthActivity.class);
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
     * initializes observable and subscrbers
     */
    private void initObservables() {
        Observable<String> pass1Obs = RxTextView.textChanges(etxtPassword).skip(1).map(charSequence -> charSequence.toString());
        Observable<String> pass2Obs = RxTextView.textChanges(etxtCPassword).skip(1)
                .debounce(1, TimeUnit.SECONDS)
                .map(charSequence -> charSequence.toString());

        //combining observables
        //checks to see if all field has data
         Observable.combineLatest(pass1Obs, pass2Obs,
                 (s, s2) -> s.length() > 0 && s2.length() > 0 )
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new DisposableObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                updateButton(validate_info());
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
     * preparing  EditTextview data
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEditText() {

        etxtPassword.setOnTouchListener((v, event) -> {

            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });
        etxtCPassword.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });
    }

    /**
     * initializes toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolBar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.create_password);

    }

    /**
     * listens to submit(btnContinue) click event
     */
    @OnClick(R.id.btnContinue)
    public void submit() {
        Timber.v("verify submit ");
        savePassword();
    }

    /**
     * Update button state apperance(active/inactive).
     *
     * @param valid the valid
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
     * function to validate user input data
     * @return true/false
     */
    private  boolean validate_info(){
        if(etxtPassword.getText().toString().contentEquals(etxtCPassword.getText().toString())){
            GlobalVariable.getCurrentUser().setPassword(etxtPassword.getText().toString());
        } else {
            etxtCPassword.setError(getString(R.string.password_dont_match));
            return false;
        }
        return true;
    }

    /**
     * sends reset request
     */
    private void savePassword() {
        if(!validate_info()){return ;}
        GlobalMethod.showloader(this,getString(R.string.resending),true);
        User user = new User();
        user.setPassword(GlobalVariable.getCurrentUser().getPassword());
        user.setPhonenumber(GlobalVariable.getCurrentUser().getCountrycode().concat(GlobalVariable.getCurrentUser().getPhonenumber()));
        mUserViewModel.setpassword(user);

    }

    /**
     * method listen to and responds accordiing to user menu choice
     * @param item selected menu item
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
