package ke.co.eclectic.quickstore.activities.supplier;

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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.adapters.SupplierAdapter;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Supplier;
import ke.co.eclectic.quickstore.viewModel.SupplierViewModel;
import timber.log.Timber;

public class SupplierActivity extends AppCompatActivity implements SupplierAdapter.SupplierComm {
    public static final String EXTRA_REPLY = "SUPPLIER_REPLY";
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.extSupplierSearch)
    EditText extSupplierSearch;
    @BindView(R.id.imgAddSupplier)
    ImageView imgAddSupplier;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;
    @BindView(R.id.suppliersRV)
     RecyclerView suppliersRV;
    @BindView(R.id.txtInfo)
     TextView txtInfo;
    @BindView(R.id.supplierSRL)
    SwipeRefreshLayout supplierSRL;

    private List<Supplier> supplierLists = new ArrayList<>();
    private List<Supplier> supplierListsCopy = new ArrayList<>();
    private SupplierAdapter supplierAdapter;
    private SupplierViewModel mSupplierViewModel;
    private String action="";
    private CompositeDisposable disposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);
        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            action = extra.get("action").toString();
        }
        mSupplierViewModel = ViewModelProviders.of(this).get(SupplierViewModel.class);

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
     * initializes swipe refresh layout
     */
    private void initSwipeRefresh(){
        supplierSRL.setOnRefreshListener(() -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("request_type","allsuppliers");
            data.put("businessid",GlobalVariable.getCurrentUser().getBusinessid(this));
            mSupplierViewModel.getAllSuppliers(data);
        });
    }

    /**
     * initializes supplier data
     */
    private void initData() {
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","allsuppliers");
        data.put("businessid",GlobalVariable.getCurrentUser().getBusinessid(this));

        mSupplierViewModel.getAllSuppliers(data).observe(this,supplierList->{
            if( null != supplierList){
                Timber.v("From getAllSuppliers "+ supplierList.size());

                if(supplierList.size() ==0){
                    txtInfo.setVisibility(View.VISIBLE);
                    txtInfo.setText("No suppliers");
                    dummyData();
                }else{
                    txtInfo.setVisibility(View.GONE);
                }

                orderSupplierList(supplierList);
            }

        });

        mSupplierViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            supplierSRL.setRefreshing(false);

            if(myApiResponses.getStatus().contentEquals("success")){

//                SupplierResponse supplierResponse = (SupplierResponse) myApiResponses.getObject();
//                if(myApiResponses.getOperation().contains("refresh")) {
//                    if (supplierResponse.getSupplierList().size() == 0) {
//                        CodingMsg.tl(this,"No supplier");
//                    }
//                }

            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(SupplierActivity.this, myApiResponses.getError(),rootView);
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
     * initializes observables
     */
    private void initObservable() {
        Observable<String> searchObservable = RxTextView.textChanges(extSupplierSearch)
                .skip(1)
                .debounce(500,TimeUnit.MILLISECONDS)
                .map(charSequence -> charSequence.toString());
        //subscribing an observer
        disposable.add( searchObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        filterSuppliers(s);
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
     * Filters supplier list
     *
     * @param filterStr filtering string
     */
    private void filterSuppliers(String filterStr) {
        if(supplierListsCopy.size() == 0){return;}
        List<Supplier> filterListCopy = new ArrayList<>();
            // Filter data
            for (int i = 0; i < supplierListsCopy.size(); i++) {
                if (supplierListsCopy.get(i).getCompanyname().toLowerCase().contains(filterStr)
                        || supplierListsCopy.get(i).getContactname().toLowerCase().contains(filterStr)
                        ) {

                        filterListCopy.add(supplierListsCopy.get(i));

                }
            }
        supplierLists = filterListCopy;
        if(filterStr.trim().length()<1){
            supplierLists = supplierListsCopy;
        }
        supplierAdapter.refresh(supplierLists);
    }

    /**
     * initializes edittext
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEdittext() {
        extSupplierSearch.setFocusable(true);
        extSupplierSearch.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });
    }

    /**
     * refershes supplier list recyclerview
     * @param sList supplier list
     * @return categorised list of products based on their category
     */
    public void orderSupplierList(List<Supplier> sList){
        supplierLists = sList;
        supplierListsCopy = sList;
        supplierAdapter.refresh(supplierLists);
    }

    /**
     * dummy data  used for testing //Todo remove dummy data
     */
  public void dummyData(){
      Supplier supplier = new Supplier();
      supplier.setSupplierid(1);
      supplier.setCompanyname("Company 1");
      supplier.setContactname("Contact 1");
      supplierLists.add(supplier);
      supplier = new Supplier();
      supplier.setSupplierid(2);
      supplier.setCompanyname("Company 2");
      supplier.setContactname("Contact 2");
      supplierLists.add(supplier);
       supplier = new Supplier();
      supplier.setSupplierid(3);
      supplier.setCompanyname("Company 3");
      supplier.setContactname("Contact 3");
      supplierLists.add(supplier);
       supplier = new Supplier();
      supplier.setSupplierid(4);
      supplier.setCompanyname("Company 4");
      supplier.setContactname("Contact 4");
      supplierLists.add(supplier);


  }

    /**
     * initializes supplier list display
     */
    private void initRV(){
       // dummyData();
        supplierAdapter = new SupplierAdapter(this,supplierLists);
        suppliersRV.setLayoutManager(new LinearLayoutManager(this));
        suppliersRV.setAdapter(supplierAdapter);
    }
    /**
     * initializes imageview
     */
    private void initImageview() {
        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("suppliers", "cancreate")) {
            imgAddSupplier.setVisibility(View.GONE);
            return;
        }

            imgAddSupplier.setOnClickListener(v -> GlobalMethod.goToActivity(SupplierActivity.this, AddSupplierActivity.class));
    }


    /**
     * initializes toolbar
     */

    private void initToolbar() {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Supplier");
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

    /**
     * handles supplier clicks events
     * @param position position of the supplier item
     * @param supplier supplier item
     * @param action aciton to be performed
     */
    @Override
    public void supplierMessage(int position, Supplier supplier, String action) {
        if(this.action.contentEquals("select")){
            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_REPLY, new Gson().toJson(supplier));
            setResult(RESULT_OK, replyIntent);
            finish();
        }else{
            HashMap<String,String>  data = new HashMap<>();
            data.put("supplier",new Gson().toJson(supplier));
            GlobalMethod.goToActivity(this,AddSupplierActivity.class,data);
        }
    }
}
