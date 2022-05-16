package ke.co.eclectic.quickstore.activities.pos.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import ke.co.eclectic.quickstore.activities.pos.PosActivity;
import ke.co.eclectic.quickstore.activities.products.BarCodeActivity;
import ke.co.eclectic.quickstore.activities.products.ProductCategoryActivity;
import ke.co.eclectic.quickstore.adapters.PosInventoryStockAdapter;
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
public class PosStocksFragment extends Fragment implements PosInventoryStockAdapter.PosProdComm {
    private static final int BARCODE_REQUEST_CODE = 10;
    private static final int CATEGORY_REQUEST_CODE = 2;
    public static final int RC_BARCODE_CAPTURE = 9001;

    private static PosStocksFragment fragment;

    @BindView(R.id.imgSearchAction)
     ImageView imgSearchAction;
    @BindView(R.id.etxtSearch)
     EditText etxtSearch;
    @BindView(R.id.etxtSelectCategory)
     EditText etxtSelectCategory;
     @BindView(R.id.laySpinner)
     LinearLayout laySpinner;
    // @BindView(R.id.spinnerCategory)
    // Spinner spinnerCategory;
     @BindView(R.id.vSeparator)
     View vSeparator;
    @BindView(R.id.productRV)
    RecyclerView productRV;
    @BindView(R.id.txtInfo)
    TextView txtInfo;

    private View rootView;
    private CompositeDisposable disposable = new CompositeDisposable();

    Observable<Boolean> observable;
    private String category="";
   // ArrayAdapter<String> catSpinnerAdapter;
   // List<String> catSpinnerList =new ArrayList<>() ;
  //  HashMap<Integer,InventoryStock> selectedInventoryStockH =new HashMap<>() ;
    private Double initialCount=0.0;
    List<InventoryStock> productlists = new ArrayList<>();
    List<InventoryStock> productlistsCopy = new ArrayList<>();
    PosInventoryStockAdapter posInventoryStockAdapter;
    private InventoryStockViewModel mInventoryStockViewModel;
    private PosActivity posActivity;


    /**
     * Instantiates a new Login fragment.
     */
    public PosStocksFragment() {
        // Required empty public constructor
    }


    /**
     * Singltone of the Cart Fragment.
     *
     * @return the login fragment
     */
    public static PosStocksFragment getInstance() {
        if (fragment == null){
            fragment = new PosStocksFragment();
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_product, container, false);
        ButterKnife.bind(this,rootView);
       // init();
        posActivity = ((PosActivity) getActivity());
        initButton();
        initEditText();
        initImageView();
       // initSpinner();
        initRV();
        initObservable();
        initDbData();

        return rootView;
    }

    /**
     * initializes recyclerview
     */
    private void initRV() {
         posInventoryStockAdapter    = new PosInventoryStockAdapter(getActivity(), productlists,this);
         productRV.setLayoutManager(new LinearLayoutManager(getActivity()));
         productRV.setAdapter(posInventoryStockAdapter);

    }

    /**
     * initializes edittexts
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
            data.put("from","pos");
            GlobalMethod.goToActivity(this, ProductCategoryActivity.class,data,CATEGORY_REQUEST_CODE);
        });

    }

    /**
     * initializes fragment with data
     */
    public void  initDbData(){
        mInventoryStockViewModel  = ViewModelProviders.of(this).get(InventoryStockViewModel.class);
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","getinventory");
        data.put("storeid",GlobalVariable.getCurrentUser().getStoreid(getActivity()));


        mInventoryStockViewModel.getAllInventoryStocks(data).observe(this,pList->{
            try {
                if( null != pList){
                    productlists = pList;
//                    InventoryStock inventoryStock  = new InventoryStock();
//                    inventoryStock.setInventoryid(0);
//                    inventoryStock.setProductname("Reward");
//                    inventoryStock.setSaleprice(0);
//                    productlists.add(0,inventoryStock);
//
                    productlistsCopy = productlists;
                    posInventoryStockAdapter.refresh(productlists);
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
                    txtInfo.setVisibility(View.GONE);
                    if (inventoryStockResponse.getInventoryStockList().size() == 0) {
                        CodingMsg.tl(getActivity(),"Add stock to ".concat(GlobalVariable.getCurrentUser().getStorename()).concat(" to continue"));
                        txtInfo.setText("Add stock to \"".concat(GlobalVariable.getCurrentUser().getStorename()).concat("\" to continue"));
                        txtInfo.setVisibility(View.VISIBLE);
                    }
                }

            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(getActivity(), myApiResponses.getError(),rootView);
                }
            }
        });

        mInventoryStockViewModel.getIsInternetConnected().observe(this, aBoolean -> {
            if(!aBoolean){
                //CodingMsg.tle(getActivity(), "Something went wrong.Please check your Internet");
            }

        });
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        //disposes all memory used by rx
        disposable.dispose();
    }



    /**
     * initializes views observable
     *
     */
    private void initObservable(){


        Observable<String> searchObservable = RxTextView.textChanges(etxtSearch)
                .skip(1)
                .debounce(300,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(charSequence -> charSequence.toString());
        //subscribing an observer
        searchObservable.subscribe(new DisposableObserver<String>() {
            @Override
            public void onNext(String charSequence) {
                Timber.v("searchObservable  "+charSequence);
                filterProducts(charSequence,"edittext");
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
     * filters product list depending on the search
     *
     * @param filterStr string to be searched for in product list
     * @param type view(edittext/spinner) invoking the filter
     */
    public void filterProducts(String filterStr, String type){
        filterStr = filterStr.toLowerCase();
        List<InventoryStock> filterListCopy = new ArrayList<>();
        if(type.contentEquals("edittext")){
            // Filter data
            //filterListCopy.add(productlistsCopy.get(0));
            for (int i = 0; i < productlistsCopy.size(); i++) {
                if (productlistsCopy.get(i).getCategoryname().toLowerCase().contains(filterStr)
                 || productlistsCopy.get(i).getProductname().toLowerCase().contains(filterStr)
                 || productlistsCopy.get(i).getUnitname().toLowerCase().contains(filterStr)
                 || productlistsCopy.get(i).getOffertype().toLowerCase().contains(filterStr)
                        ) {
                    if(i != 0){
                        filterListCopy.add(productlistsCopy.get(i));
                    }
                }
            }

        }else{
            //filterListCopy.add(productlistsCopy.get(0));
            for (int i = 0; i < productlistsCopy.size(); i++) {
                if (productlistsCopy.get(i).getCategoryname().toLowerCase().contains(filterStr)
                        ) {
                    if(i != 0){
                        filterListCopy.add(productlistsCopy.get(i));
                    }
                }
            }
        }

        productlists = filterListCopy;
        if(filterStr.trim().length()<1){
            productlists = productlistsCopy;
        }
            posInventoryStockAdapter.refresh(productlists);

    }

    private void initImageView() {

    }

    private void initButton() {
       // btnLogin.setOnClickListener(this);

    }


    /**
     * toggle search icon and invokes filter of product
     */
    @OnClick(R.id.imgSearchAction)
    public void submit() {
        Timber.v("search ");
        //resetting search parameters
        etxtSelectCategory.getText().clear();
        filterProducts("","category");
        etxtSearch.getText().clear();
        productlists = productlistsCopy;
        posInventoryStockAdapter.refresh(productlists);
        if(laySpinner.getVisibility() == View.VISIBLE){
            Timber.v("View.VISIBLE");
            imgSearchAction.setImageResource(R.drawable.search_cancel);
            etxtSearch.setVisibility(View.VISIBLE);
            vSeparator.setVisibility(View.GONE);
            laySpinner.setVisibility(View.GONE);
            ((PosActivity)getActivity()).hideFab();
        }else{
            View view = getActivity().getCurrentFocus();
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            ((PosActivity)getActivity()).showFab();
            Timber.v("View.Gone");
            imgSearchAction.setImageResource(R.drawable.product_search);
            etxtSearch.setVisibility(View.GONE);
            vSeparator.setVisibility(View.VISIBLE);
            laySpinner.setVisibility(View.VISIBLE);
        }


    }


    /**
     * displays inventory stock dialogue
     *
     * @param inventoryStock item to be shown
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
                posActivity.selectedInventoryStockH.remove(inventoryStock.getInventoryid());
                addProdDialog.dismiss();
                return;
            }
            etxtCount.setText( initialCount.toString());

            inventoryStock.setChoosenquantity(initialCount);
            posActivity.selectedInventoryStockH.put(inventoryStock.getInventoryid(),inventoryStock);
            posActivity.refreshCart();
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
            posActivity.selectedInventoryStockH.put(inventoryStock.getInventoryid(),inventoryStock);
            posActivity.refreshCart();
        });
        
        btnDone.setOnClickListener(v -> {
            initialCount = Double.parseDouble(etxtCount.getText().toString());
            if(initialCount > inventoryStock.getQuantity()){
                initialCount =  initialCount -1;
                etxtCount.setError("Stock limit reached");
                return;
            }

            inventoryStock.setChoosenquantity(initialCount);
            posActivity.selectedInventoryStockH.put(inventoryStock.getInventoryid(),inventoryStock);
            posActivity.refreshCart();
            addProdDialog.dismiss();
        });
        
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialCount =  initialCount -1;
                inventoryStock.setChoosenquantity(initialCount);
                posActivity.selectedInventoryStockH.put(inventoryStock.getInventoryid(),inventoryStock);
                addProdDialog.dismiss();
                posActivity.refreshCart();
            }
        });

        addProdDialog.setCancelable(true);
        addProdDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BARCODE_REQUEST_CODE && resultCode == RESULT_OK) {
          String  searchBarcodeStr = data.getStringExtra(BarCodeActivity.EXTRA_REPLY);
            filterProducts(searchBarcodeStr,"edittext");

            CodingMsg.tlw(getActivity(),getString(R.string.under_development)+searchBarcodeStr);
        }
        if (requestCode == CATEGORY_REQUEST_CODE && resultCode == RESULT_OK) {
            //returns a single category object
            //fetch all category again
            Category selectedCategory = new Gson().fromJson(data.getStringExtra(ProductCategoryActivity.EXTRA_REPLY), Category.class);
            etxtSelectCategory.setText(selectedCategory.getCategoryname());
            filterProducts(selectedCategory.getCategoryname(),"category");
            //getCategory();
        }
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

                    filterProducts(barcode.displayValue,"edittext");
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
        // intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());
        this.startActivityForResult(intent, RC_BARCODE_CAPTURE);
        //GlobalMethod.goToActivity(this,BarCodeActivity.class,null,BARCODE_REQUEST_CODE);
    }

    /**
     * listens to clicked stock item
     *
     * @param inventoryStock clicked item
     * @param action      action to be performed
     */
    @Override
    public void posProdMessage(InventoryStock inventoryStock, String action) {
        Timber.v("posProdMessage action: "+action);
        if(inventoryStock.getInventoryid() == 0){
            return;
        }

        if(posActivity.selectedInventoryStockH.containsKey(inventoryStock.getInventoryid())){
            inventoryStock = posActivity.selectedInventoryStockH.get(inventoryStock.getInventoryid());
            inventoryStock.addQty(1.0);
            posActivity.selectedInventoryStockH.put(inventoryStock.getInventoryid(),inventoryStock);
            if(inventoryStock.getChoosenquantity() > 3){
                showAddDialogue(inventoryStock);
            }
        } else {
            inventoryStock.setChoosenPrice(inventoryStock.getComputedsaleprice());
            inventoryStock.addQty(1.0);
            posActivity.selectedInventoryStockH.put(inventoryStock.getInventoryid(),inventoryStock);
        }

        posActivity.refreshCart();
    }
}
