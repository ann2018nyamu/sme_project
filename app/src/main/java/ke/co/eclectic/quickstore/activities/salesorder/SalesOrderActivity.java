package ke.co.eclectic.quickstore.activities.salesorder;

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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import ke.co.eclectic.quickstore.adapters.SalesOrderAdapter;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.SalesOrder;
import ke.co.eclectic.quickstore.rest.response.SalesOrderResponse;
import ke.co.eclectic.quickstore.viewModel.SalesOrderViewModel;
import timber.log.Timber;

/**
 * Sales order activity
 */
public class SalesOrderActivity extends AppCompatActivity {

    /**
     * The Tool bar.
     */
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.salesOrderRV)
    RecyclerView salesOrderRV;
    @BindView(R.id.rootView)
     CoordinatorLayout rootView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.extCatSearch)
    EditText extCatSearch;
  @BindView(R.id.txtInfo)
  TextView txtInfo;

    private CompositeDisposable disposable = new CompositeDisposable();
    List<SalesOrder> salesOrderList = new ArrayList<>();
    List<SalesOrder> salesOrderListCopy = new ArrayList<>();
    private SalesOrderAdapter salesOrderAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order);
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
     * handles resume state of the ativity
     */
    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    /**
     * initializes sales order data
     */
    private void initData() {
        SalesOrderViewModel mSalesOrderViewModel = ViewModelProviders.of(this).get(SalesOrderViewModel.class);
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","getsaleordersbystore");
        data.put("storeid",GlobalVariable.getCurrentUser().getStoreid(this));

        mSalesOrderViewModel.getAllSalesOrders(data).observe(this, poLists->{
            try {
                Timber.v("soLists size "+poLists.size());
                salesOrderListCopy = poLists;
                salesOrderAdapter.refresh(poLists);
            }catch (Exception e){
                Timber.v("categories error"+ e.getMessage());
            }

        });

        mSalesOrderViewModel.getApiResponse().observe(this, myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            if(myApiResponses.getStatus().contentEquals("success")){

                if(myApiResponses.getOperation().contains("refresh")) {
                    SalesOrderResponse salesOrderResponse = (SalesOrderResponse) myApiResponses.getObject();
                    if (salesOrderResponse.getSalesOrderList().size() == 0) {
                     //   CodingMsg.tl(this,"No sales order");
                        txtInfo.setVisibility(View.VISIBLE);
                        txtInfo.setText("No sales order");
                        GlobalMethod.showMessage(this,"No sales order in store",rootView,"",false);

                    }else{
                        txtInfo.setVisibility(View.GONE);
                    }
                }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(SalesOrderActivity.this,myApiResponses.getError(),rootView);
                }
            }
        });
    }

    /**
     * initializes  observables
     */
    private void initObservable() {
        Observable<String> searchObservable = RxTextView.textChanges(extCatSearch)
                .skip(1)
                .map(charSequence -> charSequence.toString());
        //subscribing an observer
        searchObservable.subscribe(new DisposableObserver<String>() {
            @Override
            public void onNext(String charSequence) {
                filterSalesOrders(charSequence);
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
     * filters sales order list
     * @param filterStr filtering string
     */
    private void filterSalesOrders(String filterStr) {
        List<SalesOrder> filterListCopy = new ArrayList<>();
        // Filter data
        for (int i = 0; i < salesOrderListCopy.size(); i++) {
            if (
                    salesOrderListCopy.get(i).getCustomername().toLowerCase().contains(filterStr.toLowerCase())
                    || salesOrderListCopy.get(i).getStatusname().toLowerCase().contains(filterStr.toLowerCase())
                    || salesOrderListCopy.get(i).getStorename().toLowerCase().contains(filterStr.toLowerCase())
                    || salesOrderListCopy.get(i).getLastedit().toLowerCase().contains(filterStr.toLowerCase())
                    ) {
                    filterListCopy.add(salesOrderListCopy.get(i));
            }
        }
        salesOrderList = filterListCopy;
        if(filterStr.trim().length()<1){
            salesOrderList = salesOrderListCopy;
        }

        salesOrderAdapter.refresh(salesOrderList);
    }

    /**
     * initialises edittext
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
        if (!GlobalVariable.getCurrentUser().getCurrentBsRule("salesorders", "cancreate")) {
            fab.hide();
            return;
        }

        fab.setOnClickListener(view ->{
           GlobalMethod.goToActivity(this,AddSalesOrderActivity.class);
        });
    }

    /**
     * clears observable memory
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    /**
     * initializes toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Sale Orders");
    }

    /**
     * initializes sales order list
     */
    private void initRV() {
        salesOrderAdapter = new SalesOrderAdapter(this,salesOrderList);
        salesOrderRV.setLayoutManager(new LinearLayoutManager(this));
        salesOrderRV.setAdapter(salesOrderAdapter);
    }



    /**
     * handles action bar item clicks
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
