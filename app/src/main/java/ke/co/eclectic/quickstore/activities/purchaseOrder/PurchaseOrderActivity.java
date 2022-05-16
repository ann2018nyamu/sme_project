package ke.co.eclectic.quickstore.activities.purchaseOrder;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.adapters.PurchaseOrderAdapter;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.PaymentType;
import ke.co.eclectic.quickstore.models.PurchaseOrder;
import ke.co.eclectic.quickstore.rest.response.PaymentTypeResponse;
import ke.co.eclectic.quickstore.rest.response.PurchaseOrderResponse;
import ke.co.eclectic.quickstore.viewModel.PaymentTypeViewModel;
import ke.co.eclectic.quickstore.viewModel.PurchaseOrderViewModel;
import timber.log.Timber;

/**
 * Hanldes Purchase order activity
 */
public class PurchaseOrderActivity extends AppCompatActivity  {

    /**
     * The Tool bar.
     */
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.purchaseOrderRV)
    RecyclerView purchaseOrderRV;
    @BindView(R.id.rootView)
     CoordinatorLayout rootView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.extCatSearch)
    EditText extCatSearch;

    private CompositeDisposable disposable = new CompositeDisposable();
    List<PurchaseOrder> purchaseOrderList = new ArrayList<>();
    List<PurchaseOrder> purchaseOrderListCopy = new ArrayList<>();
    private PurchaseOrderAdapter purchaseOrderAdapter;
    private HashMap<String, Integer> paymentTypeH = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_order);
        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            String action = extra.get("action").toString();
        }
        ButterKnife.bind(this);
        initToolbar();
        initEdittext();
        initFab();
        initRV();
        initObservable();

    }

    /**
     * handle resume state of the activity
     */
    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    /**
     * initializes data
     */
    private void initData() {
        PurchaseOrderViewModel mPurchaseOrderViewModel = ViewModelProviders.of(this).get(PurchaseOrderViewModel.class);
        PaymentTypeViewModel  mPaymentTypeViewModel = ViewModelProviders.of(this).get(PaymentTypeViewModel.class);
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","getpurchasesbystore");
        data.put("storeid",GlobalVariable.getCurrentUser().getStoreid(this));

        mPurchaseOrderViewModel.getAllPurchaseOrders(data).observe(this, poLists->{
            try {
                Timber.v("poLists size "+poLists.size());
                purchaseOrderListCopy = poLists;
                initPurchaseOrderData(poLists);
            }catch (Exception e){
                Timber.v("categories error"+ e.getMessage());
            }

        });

        mPurchaseOrderViewModel.getApiResponse().observe(this, myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            if(myApiResponses.getStatus().contentEquals("success")){

                if(myApiResponses.getOperation().contains("refresh")) {
                    PurchaseOrderResponse purchaseOrderResponse = (PurchaseOrderResponse) myApiResponses.getObject();
                    if (purchaseOrderResponse.getPurchaseOrderList().size() == 0) {
                        CodingMsg.tl(this,"No purchase order");
                    }
                }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(PurchaseOrderActivity.this,myApiResponses.getError(),rootView);
                }
            }
        });
        mPaymentTypeViewModel.getApiResponse().observe(this,myApiResponses -> {
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            if(myApiResponses.getStatus().contentEquals("success")){

                if(myApiResponses.getOperation().contains("refresh")) {
                    PaymentTypeResponse paymentTypeResponse = (PaymentTypeResponse) myApiResponses.getObject();
                    for(PaymentType pt: paymentTypeResponse.getPaymentTypeList()) {
                        paymentTypeH.put(pt.getName(),pt.getPaymenttypeid());
                    }
                }

            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(PurchaseOrderActivity.this,myApiResponses.getError(),rootView);
                }
            }
        });
    }

    /**
     * initializes observables
     */
    private void initObservable() {
        Observable<String> searchObservable = RxTextView.textChanges(extCatSearch)
                .skip(1)
                .map(charSequence -> charSequence.toString());
        //subscribing an observer
        searchObservable.subscribe(new DisposableObserver<String>() {
            @Override
            public void onNext(String charSequence) {
                filterPurchaseOrders(charSequence);
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
     * Filters purchase order list
     *
     *
     * @param filterStr string to be searched
     */
    private void filterPurchaseOrders(String filterStr) {
        List<PurchaseOrder> filterListCopy = new ArrayList<>();
        // Filter data
        for (int i = 0; i < purchaseOrderListCopy.size(); i++) {
            if (
                    purchaseOrderListCopy.get(i).getSuppliername().toLowerCase().contains(filterStr.toLowerCase())
                    || purchaseOrderListCopy.get(i).getStatus().toLowerCase().contains(filterStr.toLowerCase())
                    || purchaseOrderListCopy.get(i).getStorename().toLowerCase().contains(filterStr.toLowerCase())
                    || purchaseOrderListCopy.get(i).getLastedit().toLowerCase().contains(filterStr.toLowerCase())
                    ) {
                    filterListCopy.add(purchaseOrderListCopy.get(i));
            }
        }
        purchaseOrderList = filterListCopy;
        if(filterStr.trim().length()<1){
            purchaseOrderList = purchaseOrderListCopy;
        }

        purchaseOrderAdapter.refresh(purchaseOrderList);
    }

    /**
     * Initializes edittext
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEdittext() {
        extCatSearch.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });

    }

    /**
     * initializes floating button
     */
    private void initFab() {
        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("purchaseorder", "cancreate")) {
            fab.hide();
            return;
        }
        fab.setOnClickListener(view ->{ GlobalMethod.goToActivity(this,AddPurchaseOrderActivity.class); });
    }

    /**
     * handle action performed when fragment is destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    /**
     * initializes  toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Purchase Orders");
    }

    /**
     * initializes recyclerview
     */
    private void initRV() {
        purchaseOrderAdapter = new PurchaseOrderAdapter(this,purchaseOrderList);
        purchaseOrderRV.setLayoutManager(new LinearLayoutManager(this));
        purchaseOrderRV.setAdapter(purchaseOrderAdapter);
    }

    /**
     * refreshes  purchase order list
     * @param poList
     */
    private void initPurchaseOrderData(List<PurchaseOrder> poList){
        purchaseOrderAdapter.refresh(poList);
    }




    /**
     * handles  action bar item clicks
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
                finish();
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
