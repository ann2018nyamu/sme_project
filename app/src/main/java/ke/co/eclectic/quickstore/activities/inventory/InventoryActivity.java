package ke.co.eclectic.quickstore.activities.inventory;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.adapters.InventoryAdapter;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.InventoryStock;
import ke.co.eclectic.quickstore.rest.response.InventoryStockResponse;
import ke.co.eclectic.quickstore.viewModel.InventoryStockViewModel;
import timber.log.Timber;

/**
 * The type Inventory activity.
 */
public class InventoryActivity extends AppCompatActivity implements InventoryAdapter.InventoryComm {
    /**
     * The Tool bar.
     */
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    /**
     * The Ext product search.
     */
    @BindView(R.id.extProductSearch)
    EditText extProductSearch;
    /**
     * The Img add inventory.
     */
    @BindView(R.id.imgAddInventory)
    ImageView imgAddInventory;
    /**
     * The Root view.
     */
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;
    /**
     * The Inventory rv.
     */
    @BindView(R.id.inventoryRV)
    RecyclerView inventoryRV;
    /**
     * The Txt product title.
     */
    @BindView(R.id.txtProductTitle)
    TextView txtProductTitle;
    /**
     * The Txt info.
     */
    @BindView(R.id.txtInfo)
    TextView txtInfo;

    private CompositeDisposable disposable = new CompositeDisposable();
    private List<InventoryStock> inventoryLists = new ArrayList<>();
    private List<InventoryStock> inventoryListsCopy = new ArrayList<>();
    private InventoryAdapter inventoryAdapter;
    private InventoryStockViewModel mInventoryStockViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        ButterKnife.bind(this);
        initToolbar();
        initImageview();
        initEdittext();
        initRV();
        initObservable();
        initData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    /**
     * initializes activity data
     */
    private void initData() {
        mInventoryStockViewModel = ViewModelProviders.of(this).get(InventoryStockViewModel.class);
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","getinventory");
        data.put("storeid",GlobalVariable.getCurrentUser().getStoreid(this));
        mInventoryStockViewModel.deleteAll();
        mInventoryStockViewModel.getAllInventoryStocks(data).observe(this,inventoryList->{
            try {
                if( null != inventoryList){
                    if(inventoryList.size() == 0){
                        //  dummyData();
                    }
                    Timber.v("From getAllInventoryStocks "+ inventoryList.size());
                    txtProductTitle.setText("Inventory(".concat(((Integer)inventoryList.size()).toString()).concat(")"));

                    categoriseInvetoryList(inventoryList);
                }
            }catch (Exception e){
                Timber.v("products error"+ e.getMessage());
            }
        });
        mInventoryStockViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            if(myApiResponses.getStatus().contentEquals("success")){
                if(myApiResponses.getOperation().contains("refresh")) {
                    InventoryStockResponse inventoryStockResponse = (InventoryStockResponse) myApiResponses.getObject();

                    if (inventoryStockResponse.getInventoryStockList().size() == 0) {
                        CodingMsg.tl(this,"No store has been assign a product");
                        txtInfo.setText("No store has been assign a product");
                        txtInfo.setVisibility(View.VISIBLE);
                    }
                }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(this, myApiResponses.getError(),rootView);
                }
            }
        });

        mInventoryStockViewModel.getIsInternetConnected().observe(this, aBoolean -> {
            if(!aBoolean){
                //CodingMsg.tle(this, "Something went wrong.Please check your Internet");
            }

        });
    }


    /**
     * initializes activity observables
     */
    private void initObservable() {
        Observable<String> searchObservable = RxTextView.textChanges(extProductSearch).skip(1).map(charSequence -> charSequence.toString());
        //subscribing an observer
        searchObservable.subscribe(new DisposableObserver<String>() {
            @Override
            public void onNext(String charSequence) {
                Timber.v("searchObservable  "+charSequence);
                filterProducts(charSequence);
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
     * Filters product list based on filter stirng
     *
     * @param filterStr  string
     */
    private void filterProducts(String filterStr) {
        if(inventoryListsCopy.size() == 0){
            Timber.v("inventoryListsCopy ");
        return;
        }
        List<InventoryStock> filterListCopy = new ArrayList<>();
            // Filter data
            for (int i = 0; i < inventoryListsCopy.size(); i++) {
                if (inventoryListsCopy.get(i).getCategoryname().toLowerCase().contains(filterStr)
                        || inventoryListsCopy.get(i).getProductname().toLowerCase().contains(filterStr)
                        || inventoryListsCopy.get(i).getOffertype().toLowerCase().contains(filterStr)
                        ) {
                    if(i != 0){
                        filterListCopy.add(inventoryListsCopy.get(i));
                    }
                }
            }
        inventoryLists = filterListCopy;
        if(filterStr.trim().length()<1){
            inventoryLists = inventoryListsCopy;
        }
        inventoryAdapter.refresh(inventoryLists);
    }

    /**
     * initializes edittext
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEdittext() {
        extProductSearch.setFocusable(true);
        extProductSearch.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });
    }

    /**
     * categorises investory stock list
     * @param pList lsit to be categorised
     */
    private void categoriseInvetoryList(List<InventoryStock> pList){
//        inventoryLists.clear();
//        List<InventoryStock> pList2 = new ArrayList<>();
//        InventoryStock productTitle;
//        String title = "";
//        for(InventoryStock p: pList){
//            if(!title.toLowerCase().contentEquals(p.getCategoryname().toLowerCase())){
//                productTitle = new InventoryStock();
//                productTitle.setProductname(p.getCategoryname());
//                productTitle.setCategoryname(p.getCategoryname());
//                pList2.add(productTitle);
//                title=p.getCategoryname();
//            }
//            pList2.add(p);
//        }
        inventoryLists =pList;
        inventoryListsCopy =pList;

        inventoryAdapter.refresh(inventoryLists);

    }

    /**
     * dummy data to be used for testing purposes
     */
    public void dummyData(){
      InventoryStock inventoryStock = new InventoryStock();
      inventoryStock.setInventoryid(1);
      inventoryStock.setProductname("Milk");
      inventoryStock.setCategoryname("Milk");
      inventoryLists.add(inventoryStock);
      inventoryStock = new InventoryStock();
      inventoryStock.setProductname("Zawadi");
      inventoryStock.setCategoryname("Milk");
      inventoryLists.add(inventoryStock);
      inventoryStock = new InventoryStock();
      inventoryStock.setCategoryname("Milk");
      inventoryStock.setProductname("Brookside  - 500ml");
      inventoryLists.add(inventoryStock);
      inventoryStock = new InventoryStock();
      inventoryStock.setCategoryname("Cerials");
      inventoryStock.setProductname("Cerials");
      inventoryLists.add(inventoryStock);

      inventoryStock = new InventoryStock();
      inventoryStock.setCategoryname("Cerials");
      inventoryStock.setProductname("Millet");
      inventoryLists.add(inventoryStock);
      inventoryStock = new InventoryStock();
      inventoryStock.setCategoryname("Cerials");
      inventoryStock.setProductname("Maize");
      inventoryLists.add(inventoryStock);
      inventoryStock = new InventoryStock();
      inventoryStock.setCategoryname("Cerials");
      inventoryStock.setProductname("Sorghum");
      inventoryLists.add(inventoryStock);
  }

    /**
     * initializes recyclerview
     */
    private void initRV(){
        inventoryAdapter = new InventoryAdapter(this,inventoryLists);
        inventoryRV.setLayoutManager(new LinearLayoutManager(this));
        inventoryRV.setAdapter(inventoryAdapter);
    }

    /**
     * initializes activity imageviews
     */
    private void initImageview() {
        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("inventory","cancreate")) {
            imgAddInventory.setVisibility(View.GONE);
            return;
        }
        imgAddInventory.setOnClickListener(v -> GlobalMethod.goToActivity(InventoryActivity.this, AddInventoryActivity.class));
    }


    /**
     * does activity clean up
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
        setTitle("Inventory");
    }

    /**
     * listens to clicked menu item
     *
     * @param item clicked item
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
     * listens to clicked stock item
     *
     * @param position      of the clicked stock item
     * @param inventoryStock the inventory item
     * @param actioni  action to be performed to the clicked object
     */
    @Override
    public void inventoryMessage(int position, InventoryStock inventoryStock, String actioni) {

            if(actioni.contentEquals("view")){
                HashMap<String ,String> data = new HashMap<>();
                data.put("inventorystock", new Gson().toJson(inventoryStock));
                GlobalMethod.goToActivity(this,AddInventoryActivity.class,data);
            }



    }
}
