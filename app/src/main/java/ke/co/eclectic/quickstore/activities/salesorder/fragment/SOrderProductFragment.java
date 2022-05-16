package ke.co.eclectic.quickstore.activities.salesorder.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import ke.co.eclectic.quickstore.activities.products.BarCodeActivity;
import ke.co.eclectic.quickstore.activities.products.ProductCategoryActivity;
import ke.co.eclectic.quickstore.activities.salesorder.AddSalesOrderActivity;
import ke.co.eclectic.quickstore.adapters.SalesOrderAddAdapter;
import ke.co.eclectic.quickstore.barcode.BarcodeCaptureActivity;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Category;
import ke.co.eclectic.quickstore.models.InventoryStock;
import ke.co.eclectic.quickstore.rest.response.InventoryStockResponse;
import ke.co.eclectic.quickstore.viewModel.InventoryStockViewModel;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */

public class SOrderProductFragment extends Fragment implements SalesOrderAddAdapter.SalesOComm {
    private static final int BARCODE_REQUEST_CODE = 10;
    private static final int CATEGORY_REQUEST_CODE = 2;
    private static final int RC_BARCODE_CAPTURE = 9001;

    private static SOrderProductFragment fragment;

    @BindView(R.id.imgSearchAction)
     ImageView imgSearchAction;
    @BindView(R.id.etxtSearch)
     EditText etxtSearch;
    @BindView(R.id.etxtSelectCategory)
     EditText etxtSelectCategory;
     @BindView(R.id.laySpinner)
     LinearLayout laySpinner;

     @BindView(R.id.vSeparator)
     View vSeparator;
    @BindView(R.id.productRV)
    RecyclerView productRV;
    @BindView(R.id.productSR)
    SwipeRefreshLayout productSR;

    private View rootView;
    private CompositeDisposable disposable = new CompositeDisposable();

    Observable<Boolean> observable;
    private String category="";
    private Double initialCount=0.0;
    List<InventoryStock> inventoryStockList = new ArrayList<>();
    List<InventoryStock> inventoryStockListCopy = new ArrayList<>();
    SalesOrderAddAdapter salesOrderAddAdapter;
    private InventoryStockViewModel mInventoryStockViewModel;


    /**
     * Instantiates a new Login fragment.
     */
    public SOrderProductFragment() {
        // Required empty public constructor
    }


    /**
     * Singltone of the Cart Fragment.
     *
     * @return the login fragment
     */
    public static SOrderProductFragment getInstance() {
        if (fragment == null){
            fragment = new SOrderProductFragment();
        }

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_sorderproduct, container, false);
        ButterKnife.bind(this,rootView);
       //init();
        initEditText();
       // initSpinner();
        initRV();
        initObservable();
        initSwiperefresh();

        return rootView;
    }

    /**
     * initializes swipe refresh
     */
    private void initSwiperefresh() {
        productSR.setOnRefreshListener(this::getInventoryStock);
    }

    /**
     * initializes sales order list view
     */
    private void initRV() {
        salesOrderAddAdapter = new SalesOrderAddAdapter(getActivity(), inventoryStockList,this);
        productRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        productRV.setAdapter(salesOrderAddAdapter);
    }

    /**
     * initializes edittext
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEditText() {
        etxtSearch.setFocusable(true);
        etxtSearch.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });

        etxtSelectCategory.setOnClickListener(view -> {
            HashMap<String,String>  data = new HashMap<>();
            data.put("action","select");
            data.put("from","salesorder");
            GlobalMethod.goToActivity(this, ProductCategoryActivity.class,data,CATEGORY_REQUEST_CODE);
        });

    }

    /**
     * Initializes inventory stock data.
     */
    public void  initDbData(){
        mInventoryStockViewModel  = ViewModelProviders.of(this).get(InventoryStockViewModel.class);
        //setting up storeid
        getInventoryStock();
    }

    /**
     * refreshes selected inventory list
     * @param iList inventory list item
     */
    private void refreshSelectedItemsData(List<InventoryStock> iList){
        //updating selected inventory item when editing
        if(((AddSalesOrderActivity) getActivity()).selectedInventoryStockH.size() > 0){
            for(InventoryStock inventoryStock:iList){
                if(((AddSalesOrderActivity) getActivity()).selectedInventoryStockH.containsKey(inventoryStock.getInventoryid())){
                    InventoryStock oldInventoryStock =  ((AddSalesOrderActivity) getActivity()).selectedInventoryStockH.get(inventoryStock.getInventoryid());
                    inventoryStock.setChoosenPrice(oldInventoryStock.getChoosenPrice());
                    inventoryStock.getComputedsaleprice();
                    inventoryStock.setChoosenquantity( oldInventoryStock.getChoosenquantity());
                    ((AddSalesOrderActivity) getActivity()).selectedInventoryStockH.put(inventoryStock.getInventoryid(),inventoryStock);
                }
            }
        }
    }

    /**
     * fetches inventory stock from api/db
     */
    private void getInventoryStock() {
        Timber.v("getInventoryStock");
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","getinventory");
        data.put("storeid",GlobalVariable.getCurrentUser().getStoreid(getActivity()));

        mInventoryStockViewModel.getAllInventoryStocks(data).observe(this,pList->{
            try {
                Timber.v("getAllInventoryStocks");
                if( null != pList  && pList.size() > 0){
                    inventoryStockList = pList;
                    inventoryStockListCopy = inventoryStockList;
                    salesOrderAddAdapter.refresh(inventoryStockList);
                    refreshSelectedItemsData(inventoryStockList);
                }
            }catch (Exception e){
                Timber.v("orderproductfragment error"+ e.getMessage());
            }
        });

        mInventoryStockViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            productSR.setRefreshing(false);
            if(myApiResponses.getStatus().contentEquals("success")){
                  if(myApiResponses.getOperation().contains("refresh")){
                      InventoryStockResponse inventoryStockResponse = (InventoryStockResponse) myApiResponses.getObject();
                      if(inventoryStockResponse.getInventoryStockList().size() == 0){
                        GlobalMethod.showMessage(getActivity(),"No inventory in store",rootView,"success",false);
                      }else{
                          refreshSelectedItemsData(inventoryStockResponse.getInventoryStockList());
                      }
                  }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(getActivity(), myApiResponses.getError(),rootView);
                }
            }
        });

        mInventoryStockViewModel.getIsInternetConnected().observe(this, aBoolean -> {
//            if(!aBoolean){
//                CodingMsg.tle(getActivity(), "Something went wrong.Please check your Internet");
//            }
        });
        
    }

    /**
     * handles resume state
     */
    @Override
    public void onResume() {
        super.onResume();
        initDbData();
    }

    /**
     * clears observable used memory
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }



    /**
     * initializes views observable
     *
     */
    private void initObservable(){


        Observable<String> searchObservable = RxTextView.textChanges(etxtSearch)
                .debounce(300,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(charSequence -> charSequence.toString());
        //subscribing an observer
        searchObservable.subscribe(new DisposableObserver<String>() {
            @Override
            public void onNext(String charSequence) {
                Timber.v("searchObservable  "+charSequence);
                filterInventoryStock(charSequence,"edittext");
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
     * filters inventory stock
     * @param filterStr filtering string
     * @param type type of filter to perform
     */
    private void filterInventoryStock(String filterStr,String type){
        filterStr = filterStr.toLowerCase();
        List<InventoryStock> filterListCopy = new ArrayList<>();
        if(type.contentEquals("edittext")){
            etxtSearch.setText(filterStr);
            // Filter data
            for (int i = 0; i < inventoryStockListCopy.size(); i++) {
                if (inventoryStockListCopy.get(i).getCategoryname().toLowerCase().contains(filterStr)
                 || inventoryStockListCopy.get(i).getProductname().toLowerCase().contains(filterStr)
                 || inventoryStockListCopy.get(i).getUnitname().toLowerCase().contains(filterStr)
                 || inventoryStockListCopy.get(i).getOffertype().toLowerCase().contains(filterStr)
                        ) {

                        filterListCopy.add(inventoryStockListCopy.get(i));

                }
            }

        }else{
            for (int i = 0; i < inventoryStockListCopy.size(); i++) {
                if (inventoryStockListCopy.get(i).getCategoryname().toLowerCase().contains(filterStr)
                        ) {
                        filterListCopy.add(inventoryStockListCopy.get(i));

                }
            }
        }

        inventoryStockList = filterListCopy;
        if(filterStr.trim().length()<1){
            inventoryStockList = inventoryStockListCopy;
        }
            salesOrderAddAdapter.refresh(inventoryStockList);

    }


    /**
     * handles search action
     */
    @OnClick(R.id.imgSearchAction)
    public void submit() {
        Timber.v("search ");
        //resetting search parameters
        etxtSelectCategory.getText().clear();
        filterInventoryStock("","category");
        etxtSearch.getText().clear();
        inventoryStockList = inventoryStockListCopy;
        salesOrderAddAdapter.refresh(inventoryStockList);
        if(laySpinner.getVisibility() == View.VISIBLE){
            Timber.v("View.VISIBLE");
            imgSearchAction.setImageResource(R.drawable.ic_cancel_black);
            etxtSearch.setVisibility(View.VISIBLE);
            vSeparator.setVisibility(View.GONE);
            laySpinner.setVisibility(View.GONE);
            ((AddSalesOrderActivity)getActivity()).hideFab();
        }else{
            View view = getActivity().getCurrentFocus();
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            ((AddSalesOrderActivity)getActivity()).showFab();
            Timber.v("View.Gone");
            imgSearchAction.setImageResource(R.drawable.product_search);
            etxtSearch.setVisibility(View.GONE);
            vSeparator.setVisibility(View.VISIBLE);
            laySpinner.setVisibility(View.VISIBLE);
        }

    }

    /**
     * show add dialogue of inventory stock to
     * @param inventoryStock item to be added
     */
    private void showAddDialogue(InventoryStock inventoryStock) {
        Dialog addProdDialog = new Dialog(getActivity());
        addProdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addProdDialog.setContentView(R.layout.dialogue_productadd_layout);
        addProdDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtTitle =  addProdDialog.findViewById(R.id.txtTitle);
        TextView txtInfo =  addProdDialog.findViewById(R.id.txtInfo);
        Button btnMinus =  addProdDialog.findViewById(R.id.btnMinus);
        EditText etxtCount =  addProdDialog.findViewById(R.id.etxtCount);
        Button btnAdd =  addProdDialog.findViewById(R.id.btnAdd);
        Button btnCancel =  addProdDialog.findViewById(R.id.btnCancel);
        Button btnDone =  addProdDialog.findViewById(R.id.btnDone);
        initialCount = inventoryStock.getChoosenquantity();
        txtTitle.setText("Add Prefered Quantity");
        txtInfo.setText("Product Name: ".concat(inventoryStock.getProductname()));

        etxtCount.setText( initialCount.toString());
        etxtCount.setSelection(1);
        btnMinus.setOnClickListener(v -> {
            initialCount =  initialCount -1;
            if(initialCount == 0){
                ((AddSalesOrderActivity) getActivity()).selectedInventoryStockH.remove(inventoryStock.getInventoryid());
                addProdDialog.dismiss();
                return;
            }
            etxtCount.setText( initialCount.toString());

            inventoryStock.setChoosenquantity(initialCount);
            ((AddSalesOrderActivity) getActivity()).selectedInventoryStockH.put(inventoryStock.getInventoryid(),inventoryStock);
            ((AddSalesOrderActivity) getActivity()).refreshOrder();
        });

        btnAdd.setOnClickListener(v -> {

            initialCount =  initialCount +1;
            if(initialCount > inventoryStock.getQuantity()){
                initialCount =  initialCount -1;
                etxtCount.setError("Stock limit reached");
                return;
            }
            etxtCount.setText( initialCount.toString());

            inventoryStock.setChoosenquantity(initialCount);
            ((AddSalesOrderActivity) getActivity()).selectedInventoryStockH.put(inventoryStock.getInventoryid(),inventoryStock);
            ((AddSalesOrderActivity) getActivity()).refreshOrder();
        });
        
        btnDone.setOnClickListener(v -> {
            if(etxtCount.getText().toString().trim().isEmpty()){
                etxtCount.setText("0");
                ((AddSalesOrderActivity) getActivity()).selectedInventoryStockH.remove(inventoryStock.getInventoryid());
                addProdDialog.dismiss();
                return;
            }

            initialCount = Double.parseDouble(etxtCount.getText().toString());
            if(initialCount > inventoryStock.getQuantity()){
                initialCount =  initialCount -1;
                etxtCount.setError("Stock limit reached");
                return;
            }

            inventoryStock.setChoosenquantity(initialCount);
            ((AddSalesOrderActivity) getActivity()).selectedInventoryStockH.put(inventoryStock.getInventoryid(),inventoryStock);
            ((AddSalesOrderActivity) getActivity()).refreshOrder();
            addProdDialog.dismiss();
        });
        
        btnCancel.setOnClickListener(v -> {
            initialCount =  initialCount -1;
            inventoryStock.setChoosenquantity(initialCount);
            ((AddSalesOrderActivity) getActivity()).selectedInventoryStockH.put(inventoryStock.getInventoryid(),inventoryStock);
            addProdDialog.dismiss();
            ((AddSalesOrderActivity) getActivity()).refreshOrder();
        });

        addProdDialog.setCancelable(true);
        addProdDialog.show();
    }

    /**
     * handles activity result response
     *
     * @param requestCode request code
     * @param resultCode result code
     * @param data  received data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BARCODE_REQUEST_CODE && resultCode == RESULT_OK) {
          String  searchBarcodeStr = data.getStringExtra(BarCodeActivity.EXTRA_REPLY);
            filterInventoryStock(searchBarcodeStr,"edittext");
            CodingMsg.tlw(getActivity(),"Under development Barcode: "+searchBarcodeStr);
        }
        if (requestCode == CATEGORY_REQUEST_CODE && resultCode == RESULT_OK) {
            //returns a single category object
            Category selectedCategory = new Gson().fromJson(data.getStringExtra(ProductCategoryActivity.EXTRA_REPLY), Category.class);
            etxtSelectCategory.setText(selectedCategory.getCategoryname());
            filterInventoryStock(selectedCategory.getCategoryname(),"category");
        }

        if (requestCode == RC_BARCODE_CAPTURE){
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    filterInventoryStock(barcode.displayValue,"edittext");
                    Timber.v("Barcode read: " + barcode.displayValue);
                }else{
                    CodingMsg.tle(getActivity(),getString(R.string.barcode_failure));
                    Timber.v("No barcode captured, intent data is null");
                }

            } else {
                CodingMsg.tle(getActivity(),String.format(getString(R.string.barcode_error),CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
    }

    /**
     * opens barcode activity
     */
    public void openBarcode() {
        Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
        //intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());
        this.startActivityForResult(intent, RC_BARCODE_CAPTURE);
      //  GlobalMethod.goToActivity(this,BarCodeActivity.class,null,BARCODE_REQUEST_CODE);

        if(laySpinner.getVisibility() == View.VISIBLE){
            Timber.v("View.VISIBLE");
            imgSearchAction.setImageResource(R.drawable.ic_cancel_black);
            etxtSearch.setVisibility(View.VISIBLE);
            vSeparator.setVisibility(View.GONE);
            laySpinner.setVisibility(View.GONE);
        }

    }

    /**
     * Handles inventory stock item clicks
     *
     * @param inventoryStock the inventory stock item
     * @param action         the action
     */
    @Override
    public void posProdMessage(InventoryStock inventoryStock, String action) {
        Timber.v("posProdMessage action: "+action);
        if(inventoryStock.getInventoryid() == 0){
            return;
        }

        if(((AddSalesOrderActivity) getActivity()).selectedInventoryStockH.containsKey(inventoryStock.getInventoryid())){
            inventoryStock = ((AddSalesOrderActivity) getActivity()).selectedInventoryStockH.get(inventoryStock.getInventoryid());
            inventoryStock.addQty(1.0);
            ((AddSalesOrderActivity) getActivity()).selectedInventoryStockH.put(inventoryStock.getInventoryid(),inventoryStock);
            if(inventoryStock.getChoosenquantity() > 3){
                showAddDialogue(inventoryStock);
            }
        }else{
            inventoryStock.setChoosenPrice(inventoryStock.getComputedsaleprice());
            inventoryStock.addQty(1.0);
            ((AddSalesOrderActivity) getActivity()).selectedInventoryStockH.put(inventoryStock.getInventoryid(),inventoryStock);
        }
        ((AddSalesOrderActivity) getActivity()).refreshOrder();
    }
}
