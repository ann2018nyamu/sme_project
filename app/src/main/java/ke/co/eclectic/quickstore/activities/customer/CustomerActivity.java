package ke.co.eclectic.quickstore.activities.customer;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.customer.AddCustomerActivity;
import ke.co.eclectic.quickstore.adapters.CustomerAdapter;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Customer;
import ke.co.eclectic.quickstore.viewModel.CustomerViewModel;
import timber.log.Timber;

public class CustomerActivity extends AppCompatActivity implements CustomerAdapter.CustomerComm {
    public static final String EXTRA_REPLY = "CUSTOMER_REPLY";
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.extCustomerSearch)
    EditText extCustomerSearch;
    @BindView(R.id.imgAddCustomer)
    ImageView imgAddCustomer;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;
    @BindView(R.id.customersRV)
     RecyclerView customersRV;
    @BindView(R.id.txtInfo)
     TextView txtInfo;
    @BindView(R.id.customerSRL)
    SwipeRefreshLayout customerSRL;

    private List<Customer> customerLists = new ArrayList<>();
    private List<Customer> customerListsCopy = new ArrayList<>();
    private CustomerAdapter customerAdapter;
    private CustomerViewModel mCustomerViewModel;
    private String action="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            action = extra.get("action").toString();
        }

        ButterKnife.bind(this);
        initToolbar();
        initImageview();
        initEdittext();
        initRV();
        initSwipeRefresh();
        initObservable();
        initData();
        
    }
    /**
     * initializes swiperefresh layout
     */
    private void initSwipeRefresh() {
        customerSRL.setOnRefreshListener(() -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("request_type","allcustomers");
            data.put("businessid",GlobalVariable.getCurrentUser().getBusinessid(this));
            mCustomerViewModel.getAllCustomers(data);
        });
    }
    /**
     * initializes customer data
     */
    private void initData() {
        mCustomerViewModel = ViewModelProviders.of(this).get(CustomerViewModel.class);
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","customersbybusinessid");
        data.put("businessid",GlobalVariable.getCurrentUser().getBusinessid(this));

        mCustomerViewModel.getAllCustomers(data).observe(this,customerList->{
            if( null != customerList){
                Timber.v("From getAllCustomers "+ customerList.size());
                if(customerList.size() ==0){
                    txtInfo.setVisibility(View.VISIBLE);
                    txtInfo.setText("No customers");
                    dummyData();
                }else{
                    txtInfo.setVisibility(View.GONE);
                }

                orderCustomerList(customerList);
            }
        });

        mCustomerViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            customerSRL.setRefreshing(false);

            if(myApiResponses.getStatus().contentEquals("success")){
//                CustomerResponse customerResponse = (CustomerResponse) myApiResponses.getObject();
//                if(myApiResponses.getOperation().contains("refresh")) {
//                    if (customerResponse.getCustomerList().size() == 0) {
//                        CodingMsg.tl(this,"No customer");
//                    }
//                }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(CustomerActivity.this, myApiResponses.getError(),rootView);
                }
            }
        });


        mCustomerViewModel.getIsInternetConnected().observe(this, aBoolean -> {
            if(!aBoolean){
                //CodingMsg.tle(this, "Something went wrong.Please check your Internet");
            }
        });
    }



    /**
     * initializes view  observables
     */
    private void initObservable() {
        Observable<String> searchObservable = RxTextView.textChanges(extCustomerSearch)
                .skip(1)
                .debounce(500,TimeUnit.MILLISECONDS)
                .map(charSequence -> charSequence.toString());
        //subscribing an observer
        searchObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        filterCustomers(s);
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
     * filters customer list based on the filter string
     *
     * @param filterStr  string to be searchd for
     */
    private void filterCustomers(String filterStr) {
        if(customerListsCopy.size() == 0){return;}
        List<Customer> filterListCopy = new ArrayList<>();
            // Filter data
            for (int i = 0; i < customerListsCopy.size(); i++) {
                if (customerListsCopy.get(i).getCompanyname().toLowerCase().contains(filterStr)
                        || customerListsCopy.get(i).getContactname().toLowerCase().contains(filterStr)
                        ) {

                        filterListCopy.add(customerListsCopy.get(i));

                }
            }
        customerLists = filterListCopy;
        if(filterStr.trim().length()<1){
            customerLists = customerListsCopy;
        }
        customerAdapter.refresh(customerLists);
    }
    /**
     * initializes edittext
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEdittext() {
        extCustomerSearch.setFocusable(true);
        extCustomerSearch.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });
    }

    /**
     * invokes refresh of customer list in the recylerview
     *
     * @param sList list of customers
     */
    public void orderCustomerList(List<Customer> sList){
        customerLists = sList;
        customerListsCopy = sList;
        customerAdapter.refresh(customerLists);
        //return sList;
    }
    /**
     * dummy data for testing purposes
     */
  public void dummyData(){
      Customer customer = new Customer();
      customer.setCustomerid(1);
      customer.setCompanyname("Company 1");
      customer.setContactname("Contact 1");
      customerLists.add(customer);
      customer = new Customer();
      customer.setCustomerid(2);
      customer.setCompanyname("Company 2");
      customer.setContactname("Contact 2");
      customerLists.add(customer);

  }
    /**
     * initializes recyclerview
     */
    private void initRV(){
       // dummyData();
        customerAdapter = new CustomerAdapter(this,customerLists);
        customersRV.setLayoutManager(new LinearLayoutManager(this));
        customersRV.setAdapter(customerAdapter);
    }
    /**
     * initializes imageview
     */
    private void initImageview() {
        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("customers","cancreate")) {
            imgAddCustomer.setVisibility(View.GONE);
            return;
        }
            imgAddCustomer.setOnClickListener(v -> GlobalMethod.goToActivity(CustomerActivity.this, AddCustomerActivity.class));
    }

    /**
     * initializes toolbar
     *
     */
    private void initToolbar() {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Customer");

    }

    /**
     * initializes user data
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

    /**
     * impementation of CustomerAdapter interface
     *
     * @param position of item clicked
     * @param customer  object clicked
     * @param action operation to be performed to the clicked customer
     */
    @Override
    public void customerMessage(int position, Customer customer, String action) {
        if(this.action.contentEquals("select")){
            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_REPLY, new Gson().toJson(customer));
            setResult(RESULT_OK, replyIntent);
            finish();
        }else{
            HashMap<String,String>  data = new HashMap<>();
            data.put("customer",new Gson().toJson(customer));
            GlobalMethod.goToActivity(this,AddCustomerActivity.class,data);
        }
    }
}
