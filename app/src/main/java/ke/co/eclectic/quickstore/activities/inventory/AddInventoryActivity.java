package ke.co.eclectic.quickstore.activities.inventory;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.vatsal.imagezoomer.ImageZoomButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.products.AllProductActivity;
import ke.co.eclectic.quickstore.activities.products.ProductCategoryActivity;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.InventoryStock;
import ke.co.eclectic.quickstore.models.OfferType;
import ke.co.eclectic.quickstore.models.Product;
import ke.co.eclectic.quickstore.models.Store;
import ke.co.eclectic.quickstore.rest.response.InventoryStockResponse;
import ke.co.eclectic.quickstore.rest.response.OfferTypeResponse;
import ke.co.eclectic.quickstore.rest.response.StoreResponse;
import ke.co.eclectic.quickstore.viewModel.InventoryStockViewModel;
import ke.co.eclectic.quickstore.viewModel.OfferTypeViewModel;
import ke.co.eclectic.quickstore.viewModel.StoreViewModel;
import timber.log.Timber;

public class AddInventoryActivity extends AppCompatActivity {
    private static final int STORE_REQUEST_CODE = 2;
    private static final int PRODUCT_REQUEST_CODE = 3;


    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.etxtSelectedStore)
    EditText etxtSelectedStore;
    @BindView(R.id.etxtStockQty)
    EditText etxtStockQty;
    @BindView(R.id.etxtMinStockQty)
    EditText etxtMinStockQty;
    @BindView(R.id.etxtPurchasePrice)
    EditText etxtPurchasePrice;
    @BindView(R.id.etxtSalePrice)
    EditText etxtSalePrice;
    @BindView(R.id.etxtOfferprice)
    EditText etxtOfferprice;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;
    @BindView(R.id.imgPreview)
    ImageView imgPreview;
    @BindView(R.id.spinnerSaleType)
    Spinner spinnerSaleType;
    @BindView(R.id.imgPicPreview1)
    ImageZoomButton imgPicPreview1;
    
    @BindView(R.id.etxtSelectedProduct)
    EditText etxtSelectedProduct;
    @BindView(R.id.etxtSalesType)
    EditText etxtSalesType;
    
    @BindView(R.id.btnSaveInventory)
    Button btnSaveInventory;
    @BindView(R.id.txtProductUnit)
    TextView txtProductUnit;
    @BindView(R.id.txtProductCategory)
    TextView txtProductCategory;

    private CompositeDisposable disposable = new CompositeDisposable();
    private String fileName="";
    private Store selectedStore=new Store();
    private Product selectedProduct=new Product();
    private String selectedSaleOfferTypeStr="";
    private OfferType selectedSaleOfferTypeObj= new OfferType();
    private ArrayList<String> saleOfferTypeList = new ArrayList<>();
    private HashMap<String,OfferType> saleOfferTypeListH = new HashMap<>();

    private HashMap<String,Store> storeListH = new HashMap<>();
    private ArrayList<String> storeList = new ArrayList<>();
    private ArrayAdapter<String> salesOfferTypeAdapter;
    private InventoryStock inventoryStock = new InventoryStock();
    private InventoryStockViewModel mInventoryStockViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory);
        ButterKnife.bind(this);
        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            Timber.v("inventoryStock :"+extra.get("inventorystock").toString());
            inventoryStock =  new Gson().fromJson(extra.get("inventorystock").toString(),InventoryStock.class);
            if(inventoryStock == null){
                inventoryStock = new InventoryStock();
            }
        }
        initToolbar();
        initEdittext();
        initButton();
        initSpinnerSaleType();
        initObservables();
        initData();
        populateData();
    }

    private void initButton() {
        if (!GlobalVariable.getCurrentUser().getCurrentBsRule("inventory", "cancreate")) {
         btnSaveInventory.setVisibility(View.GONE);
        }

    }

    /**
     * Populates data to the relevant views
     */
    private void populateData() {
        //user should not edit the product and the store assigned
        if(inventoryStock.getInventoryid() != 0){
            etxtSalePrice.setText(inventoryStock.getSaleprice().toString());
            etxtPurchasePrice.setText(inventoryStock.getPurchaseprice().toString());
            etxtStockQty.setText(inventoryStock.getQuantity().toString());
            etxtMinStockQty.setText(inventoryStock.getMinquantity().toString());
            etxtOfferprice.setText(inventoryStock.getOfferamount().toString());
            etxtSelectedProduct.setText(inventoryStock.getProductname());
            txtProductCategory.setText(inventoryStock.getCategoryname());
            txtProductUnit.setText(inventoryStock.getUnitname());

            for(int i =0;i<saleOfferTypeList.size();i++){
                if(inventoryStock.getOffertype().contentEquals(saleOfferTypeList.get(i))){
                    spinnerSaleType.setSelection(i);
                }
            }

            btnSaveInventory.setText("Update Stock");
            etxtSelectedStore.setText(GlobalVariable.getCurrentUser().getStorename(this));
            etxtSelectedStore.setOnClickListener(null);
            etxtSelectedProduct.setOnClickListener(null);
            selectedStore.setStoreid(inventoryStock.getStoreid());
            selectedProduct.setProductid(inventoryStock.getProductid());
            selectedStore.setStoreid(GlobalVariable.getCurrentUser().getStoreid(this));
           updateButton(true);
        }else {

            if (selectedStore.getStoreid() != 0) {
                etxtSelectedStore.setText(selectedStore.getName());
            }

            if (selectedProduct.getProductid() != 0) {
                etxtSelectedProduct.setText(selectedProduct.getProductname());
                Timber.v("bitmap " + selectedProduct.getImage());
                if (selectedProduct.getImage().length() > 10) {
                    imgPreview.setImageBitmap(selectedProduct.getImageBitmap(this));
                }
                txtProductCategory.setText(selectedProduct.getCategoryname());
                txtProductUnit.setText(selectedProduct.getUnitname());
            }
        }



    }
    /**
     * Initializes edittext views
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEdittext() {

        if(GlobalVariable.getCurrentUser().getCurrentBsRule("inventory", "cancreate")) {
            etxtSelectedStore.setOnClickListener(view -> {
                showStoresDialogue();
            });
            etxtSalesType.setOnClickListener(view -> {
                showSalesTypeDialogue();
            });
            etxtSelectedProduct.setOnClickListener(view -> {
                HashMap<String, String> data = new HashMap<>();
                data.put("action", "select");
                GlobalMethod.goToActivity(this, AllProductActivity.class, data, PRODUCT_REQUEST_CODE);
            });
            etxtStockQty.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtMinStockQty.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtPurchasePrice.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtSalePrice.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
            etxtOfferprice.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
        }

    }
    /**
     * Displays store dialogue for user selection
     */
    private void showStoresDialogue() {

        String[]   mSelectedItem1 = storeList.toArray(new String[0]);
        //String[]   mSelectedItem1 = storeList.toArray(new String[storeList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle("Choose Store")
                .setSingleChoiceItems(mSelectedItem1, -1, (dialogInterface, i) -> {
                    selectedStore =  storeListH.get(mSelectedItem1[i]);
                    dialogInterface.dismiss();
                    etxtSelectedStore.setText(mSelectedItem1[i]);
                });

        AlertDialog mDialog = builder.create();
        mDialog.show();
    }
    /**
     * Displays sales type  dialogue for user selection
     */
    private void showSalesTypeDialogue() {
        String[]   mSelectedItem = saleOfferTypeList.toArray(new String[saleOfferTypeList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle("Choose Sales Type")
                .setSingleChoiceItems(mSelectedItem, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedSaleOfferTypeObj =  saleOfferTypeListH.get(mSelectedItem[i]);
                        dialogInterface.dismiss();
                        etxtSalesType.setText(mSelectedItem[i]);
                    }
                });
        AlertDialog mDialog = builder.create();
        mDialog.show();
    }

    /**
     * initializes/prepare data
     */
    private void initData() {
        OfferTypeViewModel mOfferTypeViewModel = ViewModelProviders.of(this).get(OfferTypeViewModel.class);
        StoreViewModel mStoreViewModel = ViewModelProviders.of(this).get(StoreViewModel.class);
        mInventoryStockViewModel = ViewModelProviders.of(this).get(InventoryStockViewModel.class);
        //getting store data
        mStoreViewModel.getAllStore().observe(this, stores->{
//            Timber.v("getAllStore "+stores.size());
            storeListH.clear();
            storeList.clear();
            for(Store s:stores){
                storeListH.put(s.getStorename(),s);
                storeList.add(s.getStorename());
            }
        });
//        mStoreViewModel.getApiResponse().observe(this, myApiResponses->{
//            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
//            GlobalMethod.stoploader(this);
//            if(myApiResponses.getStatus().contentEquals("success")){
//                if(myApiResponses.getOperation().contains("apirefresh")) {
//                    StoreResponse storeResponse = (StoreResponse) myApiResponses.getObject();
//
//                                Timber.v("getAllStore "+storeResponse.getData().size());
//                        storeListH.clear();
//                        storeList.clear();
//                        for(Store s:storeResponse.getData()){
//                            storeListH.put(s.getStorename(),s);
//                            storeList.add(s.getStorename());
//                        }
//                }
//            }else{
//                if (null != myApiResponses.getError()){
//                    GlobalMethod.parseCommError(AddInventoryActivity.this, myApiResponses.getError(),rootView);
//                }
//            }
//
//        });


        //getting offertype data
        mOfferTypeViewModel.getAllOfferTypes().observe(this , offerTypes->{
           // Timber.v("getAllOfferTypes "+offerTypes.size());

            if(offerTypes.size() == 0){
                GlobalMethod.showloader(this,"Fetching data",true);
            }
            refreshOfferType(offerTypes);

//            saleOfferTypeList.clear();
//            saleOfferTypeListH.clear();
//            saleOfferTypeListH.put("Sale type",new OfferType());
//            saleOfferTypeList.add(0,"Sale type");
//                for(OfferType ot:offerTypes){
//                    saleOfferTypeListH.put(ot.getOffername(),ot);
//                    saleOfferTypeList.add(ot.getOffername());
//
//                }
//                if(inventoryStock.getInventoryid() != 0){
//                    for(int i =0;i<saleOfferTypeList.size();i++){
//                        if(inventoryStock.getOffertype().contentEquals(saleOfferTypeList.get(i))){
//                            spinnerSaleType.setSelection(i);
//                            selectedSaleOfferTypeObj = saleOfferTypeListH.get(saleOfferTypeList.get(i));
//                        }
//                    }
//                }
//
//
//             salesOfferTypeAdapter.notifyDataSetChanged();

            //initSpinnerSaleType();

        });

        mOfferTypeViewModel.getApiResponse().observe(this, myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            GlobalMethod.stoploader(this);
            if(myApiResponses.getStatus().contentEquals("success")){
                if(myApiResponses.getOperation().contains("apirefresh")) {
                    OfferTypeResponse offerTypeResponse = (OfferTypeResponse) myApiResponses.getObject();
                    refreshOfferType(offerTypeResponse.getOfferTypeList());

                }

            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(AddInventoryActivity.this, myApiResponses.getError(),rootView);
                }
            }

        });

        mInventoryStockViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            GlobalMethod.stoploader(this);
            if(myApiResponses.getStatus().contentEquals("success")){
                if(myApiResponses.getOperation().contains("apisave")) {
                   // InventoryStockResponse supplierResponse = (InventoryStockResponse) myApiResponses.getObject();
                    CodingMsg.tls(AddInventoryActivity.this,"Inventorystock added successfully");
                    Completable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                            .subscribe(AddInventoryActivity.this::finish);

                }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(AddInventoryActivity.this,(Throwable) myApiResponses.getError(),rootView);
                }
            }
        });

        mOfferTypeViewModel.getIsInternetConnected().observe(this, aBoolean -> {
            if(!aBoolean){
                //CodingMsg.tle(this ,"Something went wrong.Please check your internet ");
            }
        });

    }
    public void refreshOfferType(List<OfferType> offerTypeList){
        saleOfferTypeList.clear();
        saleOfferTypeListH.clear();
        saleOfferTypeListH.put("Sale type", new OfferType());
        saleOfferTypeList.add(0, "Sale type");
        for (OfferType ot : offerTypeList) {
            saleOfferTypeListH.put(ot.getOffername(), ot);
            saleOfferTypeList.add(ot.getOffername());

        }
        if (inventoryStock.getInventoryid() != 0) {
            for (int i = 0; i < saleOfferTypeList.size(); i++) {
                if (inventoryStock.getOffertype().contentEquals(saleOfferTypeList.get(i))) {
                    spinnerSaleType.setSelection(i);
                    selectedSaleOfferTypeObj = saleOfferTypeListH.get(saleOfferTypeList.get(i));
                }
            }
        }


        salesOfferTypeAdapter.notifyDataSetChanged();
    }


    /**
     * initializes activity observables
     */
    private void initObservables() {
        //code generator

        //category spinner
        disposable.add(
                RxAdapterView.itemSelections(spinnerSaleType)
                        .skip(1)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(integer -> {
                            try {
                                CheckedTextView t = spinnerSaleType.getSelectedView().findViewById(R.id.text1);

                               
                                selectedSaleOfferTypeStr = t.getText().toString();
                                if (integer == 0) {

                                    selectedSaleOfferTypeObj = new OfferType();
                                    t.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextPlaceholderColor, null));
                                } else {
                                    t.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextColor, null));
                                    selectedSaleOfferTypeObj = saleOfferTypeListH.get(selectedSaleOfferTypeStr);
                                }


                                validateInfo(false);
                            }catch (Exception e){
                                Timber.v(e.getMessage());
                            }
                        }));

        Observable<String> nameObservable = RxTextView.textChanges(etxtStockQty)
                .debounce(2,TimeUnit.SECONDS)
                .skip(1).map(charSequence -> {
                    validateInfo(false);
                    return charSequence.toString();
                });
        Observable<String> minStockObservable = RxTextView.textChanges(etxtMinStockQty)
                .debounce(2,TimeUnit.SECONDS)
                .skip(1).map(charSequence -> {
                    validateInfo(false);
                    return charSequence.toString();
                });
        Observable<String> ppObservable = RxTextView.textChanges(etxtPurchasePrice)
                .debounce(2,TimeUnit.SECONDS)
                .skip(1).map(charSequence -> {
                    validateInfo(false);
                    return charSequence.toString();
                });
        Observable<String> spObservable = RxTextView.textChanges(etxtSalePrice)
                .debounce(2,TimeUnit.SECONDS)
                .skip(1).map(charSequence -> {
                    validateInfo(false);
                    return charSequence.toString();
                });

    }

    /**
     * validates user information before saving/updating
     * @param showErrors true if you want to display error to user and vice versa
     * @return boolean true if validation passes and vice versa
     */
    private boolean validateInfo(boolean showErrors) {
        updateButton(false);
        if(selectedStore.getStoreid()==0){
            if(showErrors){
                CodingMsg.tle(this,"Please select a store");
            }
            return false;
        }
        if(selectedProduct.getProductid()==0){
            if(showErrors){
                CodingMsg.tle(this,"Please select a product");
            }
            return false;
        }
        if(selectedSaleOfferTypeObj.getOffertypeid()==0){
            if(showErrors){
                CodingMsg.tle(this,"Please select a sales type");
            }
            return false;
        }
        if(etxtStockQty.getText().toString().trim().isEmpty()){
            if(showErrors){
                etxtStockQty.setError("Enter  stock quantity");
            }
            return false;
        }
        if(etxtMinStockQty.getText().toString().trim().isEmpty()){
            if(showErrors){
                etxtMinStockQty.setError("Enter minimum stock quantity");
            }
            return false;
        }
        if(etxtPurchasePrice.getText().toString().trim().isEmpty()){
            if(showErrors){
                etxtPurchasePrice.setError("Enter recommended purchase price");
            }
            return false;
        }
        
        if(etxtSalePrice.getText().toString().trim().isEmpty()){
            if(showErrors){
                etxtSalePrice.setError("Enter recommended sales price");
            }
            return false;
        }

        updateButton(true);

        inventoryStock.setOffertypeid(selectedSaleOfferTypeObj.getOffertypeid());
        inventoryStock.setStoreid(selectedStore.getStoreid());
        inventoryStock.setQuantity(Double.valueOf(etxtStockQty.getText().toString()));
        inventoryStock.setMinquantity(Double.valueOf(etxtMinStockQty.getText().toString()));
        inventoryStock.setPurchaseprice(Double.valueOf(etxtPurchasePrice.getText().toString()));
        inventoryStock.setSaleprice(Double.valueOf(etxtSalePrice.getText().toString()));
        inventoryStock.setOfferamount(Double.valueOf(etxtSalePrice.getText().toString()));
        inventoryStock.setProductid(selectedProduct.getProductid());

        return true;
    }
    /**
     * changes button state depending on validity of user input data
     *
     * @param valid  true to make button active
     */
    private void updateButton(boolean valid) {
        if (valid) {
            btnSaveInventory.setEnabled(true);
            btnSaveInventory.setBackgroundResource(R.drawable.btn_active);
        }else{
            btnSaveInventory.setEnabled(false);
            btnSaveInventory.setBackgroundResource(R.drawable.btn_inactive);
        }
    }


    /**
     * trigers displays large image
     */
    @OnClick(R.id.imgPreview)
    public void largeDisplay(){
            imgPicPreview1.setImageDrawable(imgPreview.getDrawable());
            imgPicPreview1.performClick();

    }


    /**
     * listens to buttom save inventory click event
     */
    @OnClick(R.id.btnSaveInventory)
    public void updateStockAction(){
        if(validateInfo(true)){
            updateStock();
        }
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
     * sends inventory stock information to viewmodel
     */
    private void updateStock() {
        mInventoryStockViewModel.insert(inventoryStock);
    }


    /**
     * initializes sales type spinner
     */
    private void initSpinnerSaleType() {

        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("inventory", "cancreate")) {
            spinnerSaleType.setEnabled(false);
        }

        salesOfferTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, saleOfferTypeList);
        // Specify the layout to use when the list of choices appears
        salesOfferTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item2);
        spinnerSaleType.setAdapter(salesOfferTypeAdapter);
    }

    /**
     * initializes activity toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(inventoryStock.getInventoryid() == 0){
            setTitle("Stock In");
        }else{
            setTitle("View inventory");
            if (GlobalVariable.getCurrentUser().getCurrentBsRule("inventory", "cancreate")) {
                setTitle("Edit inventory");
            }
        }
    }

    /**
     * listens to activity result requested by current activity
     *
     * @param requestCode code used to make the request
     * @param resultCode code received from the called activity
     * @param data data received from the called activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PRODUCT_REQUEST_CODE && resultCode == RESULT_OK) {
            //returns a single category object
            selectedProduct = new Gson().fromJson(data.getStringExtra(AllProductActivity.EXTRA_REPLY),Product.class);
            populateData();
        }

        if (requestCode == STORE_REQUEST_CODE && resultCode == RESULT_OK) {
            //returns a single category object
            selectedStore = new Gson().fromJson(data.getStringExtra(ProductCategoryActivity.EXTRA_REPLY),Store.class);
            populateData();
        }

    }


    /**
     * listens to menuitem clicked
     *
     * @param item menuitem
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
