package ke.co.eclectic.quickstore.activities.purchaseOrder;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.purchaseOrder.fragment.OrderListFragment;
import ke.co.eclectic.quickstore.activities.purchaseOrder.fragment.OrderProductFragment;
import ke.co.eclectic.quickstore.adapters.ViewPagerAdapter;
import ke.co.eclectic.quickstore.barcode.BarcodeCaptureActivity;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.InventoryStock;
import ke.co.eclectic.quickstore.models.PurchaseOrder;
import ke.co.eclectic.quickstore.models.Supplier;
import ke.co.eclectic.quickstore.rest.response.PurchaseOrderResponse;
import ke.co.eclectic.quickstore.viewModel.PurchaseOrderViewModel;
import timber.log.Timber;

/**
 * The Pos activity.
 */
public class AddPurchaseOrderActivity extends AppCompatActivity {
    private static final int BARCODE_REQUEST_CODE = 10;
    private static final int RC_BARCODE_CAPTURE = 9010;
    public Supplier selectedSupplier= new Supplier();
    @BindView(R.id.viewpager_main)
    ViewPager viewpager_main;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtPurchaseTitle)
    TextView txtPurchaseTitle;
    @BindView(R.id.txtPurchaseCount)
    TextView txtPurchaseCount;
    @BindView(R.id.txtPurchaseTotal)
    TextView txtPurchaseTotal;
    @BindView(R.id.txtPurchaseOrderId)
    TextView txtPurchaseOrderId;
    @BindView(R.id.txtStoreName)
    TextView txtStoreName;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.imgCancel)
    ImageView imgCancel;
    @BindView(R.id.btnSavePOrder)
    Button btnSavePOrder;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;


    private PurchaseOrder purchaseorder = new PurchaseOrder();
    public HashMap<Integer,InventoryStock> selectedInventoryStockH =new HashMap<>() ;
    private PurchaseOrderViewModel mPurchaseOrderViewModel;
    private Integer page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            try{
                purchaseorder = new Gson().fromJson(extra.get("purchaseorder").toString(),PurchaseOrder.class);
                page = Integer.valueOf(extra.get("page").toString());

                if(null == page){
                    page = 0;
                }
                if(null == purchaseorder){
                    purchaseorder = new PurchaseOrder();
                }
            }catch (Exception e){
                Timber.v(e.getMessage());
            }
        }
        setContentView(R.layout.activity_add_purchase_order);
        ButterKnife.bind(this);
        initToolbar();
        initFab();
        initButton();
        initTextView();
        initImageview();
        setupViewPager();
        setupTabIcons();
        initData();
    }


    /**
     * initializes buttons
     */
    private void initButton() {
        if( purchaseorder.getPurchaseorderid()!=0){
            btnSavePOrder.setText(getString(R.string.update));
        }
    }

    /**
     * saves order
     */
    @OnClick(R.id.btnSavePOrder)
    public void saveOrder(){
        if(validate_info()){
            String requestType = "createpurchase";
            if(purchaseorder.getPurchaseorderid() != 0){
                requestType = "editpurchase";
            }
            mPurchaseOrderViewModel.insert(purchaseorder,requestType);
        }
    }

    /**
     * validated purchase order information filled by user
     * @return boolean true/false
     */
    private boolean validate_info() {
        if(selectedSupplier.getSupplierid() == 0){
            CodingMsg.tl(this,"Please select a supplier");
            return false;
        }
        purchaseorder.setSupplierid(selectedSupplier.getSupplierid());
        purchaseorder.setStoreid(GlobalVariable.getCurrentUser().getStoreid());
        return true;
    }

    /**
     * initializes data
     */
    private void initData() {
        mPurchaseOrderViewModel = ViewModelProviders.of(this).get(PurchaseOrderViewModel.class);
        mPurchaseOrderViewModel.getApiResponse().observe(this,myApiResponses -> {
            if(myApiResponses.getStatus().contentEquals("success")){
                Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
                if(myApiResponses.getOperation().contains("apisave")) {
                    PurchaseOrderResponse purchaseOrderResponse = (PurchaseOrderResponse) myApiResponses.getObject();
                   // CodingMsg.tls(this,purchaseOrderResponse.getMessage());
                    selectedInventoryStockH.clear();
                    refreshOrder();
                    if(page != 0){
                        finish();
                    }else{
                        viewpager_main.setCurrentItem(0);
                    }
                    GlobalMethod.showSuccess(this,purchaseOrderResponse.getMessage(),true);

                }

            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(this, myApiResponses.getError(),rootView);
                }
            }
        });

        if(purchaseorder.getPurchaseorderid() != 0){
            selectedSupplier = new Supplier();
            selectedSupplier.setSupplierid(purchaseorder.getSupplierid());
            selectedSupplier.setContactname(purchaseorder.getSuppliername());

            selectedInventoryStockH = purchaseorder.getInventoryStockListH();
        }
    }

    /**
     * refreshes order list
     */
    public void refreshOrder(){
        Timber.v("selectedInventoryStockH size "+selectedInventoryStockH.size());

        Double orderTotal = 0.00;
        Double itemCount = 0.0;
        OrderListFragment.inventoryStockList.clear();
        for(Map.Entry<Integer,InventoryStock> entry : selectedInventoryStockH.entrySet()){
            OrderListFragment.inventoryStockList.add(entry.getValue());
            orderTotal += entry.getValue().getChoosenPrice()*entry.getValue().getChoosenquantity();
            Timber.v("porder item "+entry.getValue().getProductname()+"  "+entry.getValue().getChoosenPrice() +"  "+entry.getValue().getChoosenquantity()+"  total:"+ orderTotal);
            itemCount += entry.getValue().getChoosenquantity();
        }
        Timber.v("porder Total  "+ orderTotal);
        Timber.v("porder itemCount  "+ itemCount);
        refreshPurchaseOrderViews(orderTotal.toString(),itemCount);
        OrderListFragment.getInstance().refreshList();

        //setting object
        purchaseorder.setTotalcost(orderTotal);
        purchaseorder.setInventoryStockListStr(new Gson().toJson( OrderListFragment.inventoryStockList));
        purchaseorder.setAutoapporval(false);
        purchaseorder.setStoreid(GlobalVariable.getCurrentUser().getStoreid(this));

        if (itemCount > 0){
            btnSavePOrder.setEnabled(true);
            btnSavePOrder.setBackgroundColor(ContextCompat.getColor(this, R.color.activeColor));
        }else{
            btnSavePOrder.setEnabled(false);
            btnSavePOrder.setBackgroundColor(ContextCompat.getColor(this, R.color.grayColor));
        }

    }

    /**
     * initializes imageview
     */
    private void initImageview() {
        imgCancel.setOnClickListener( view -> finish());
        if(purchaseorder.getPurchaseorderid() == 0){
            imgCancel.setVisibility(View.GONE);
        }
    }

    /**
     * initializes textview
     */
    private void initTextView() {
        txtPurchaseTotal.setText(R.string.total_pos_label );
        txtPurchaseCount.setText( "0");
        txtStoreName.setText(GlobalVariable.getCurrentUser().getStorename(this));
        if(purchaseorder.getPurchaseorderid() != 0){
            txtPurchaseTitle.setText("Edit Purchase");
        }
    }

    /**
     * refreshes order view (total and count)
     * @param cartTotal
     * @param itemCount
     */
    private void refreshPurchaseOrderViews(String cartTotal, Double itemCount){
        txtPurchaseTotal.setText("Total Ksh "+ cartTotal);
        txtPurchaseCount.setText(((Integer)itemCount.intValue()).toString());
    }


    /**
     * initializes floating button
     */
    private void initFab() {
        fab.setOnClickListener(view -> {
            OrderProductFragment.getInstance().openBarcode();
        });
    }

    /**
     * Hides floating button
     */
    public void hideFab(){
        fab.hide();
    }

    /**
     * shows floating button
     */
    public void showFab(){
        fab.show();
    }

    /**
     * initializes toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(purchaseorder.getPurchaseorderid() != 0){
            setTitle("Edit purchase order");
        }else{
            setTitle("Add purchase order");
        }

    }

    /**
     * Sets up tab icon/label
     */
    private void setupTabIcons() {
        tabLayout.setupWithViewPager(viewpager_main);
        View view = (View) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);

        TextView tabOne = view.findViewById(R.id.tab);
        tabOne.setText(R.string.products);
        tabOne.setTextColor(ContextCompat.getColor(this ,R.color.editTextColor));
        tabOne.setTypeface(GlobalVariable.getMontserratSemiBold(this));

        tabLayout.getTabAt(0).setCustomView(view);

        view = (View) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne = view.findViewById(R.id.tab);
        tabOne.setText(R.string.order_list);
        tabOne.setTextColor(ContextCompat.getColor(this,R.color.editTextColor));
        tabOne.setTypeface(GlobalVariable.getMontserratSemiBold(this));
        tabLayout.getTabAt(1).setCustomView(view);
    }

    /**
     * Initializes view pager
     */
    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(OrderProductFragment.getInstance(), getResources().getString(R.string.products));
        adapter.addFragment(OrderListFragment.getInstance(),getString(R.string.order_list) );//getResources().getString(R.string.invoice)


        viewpager_main.setAdapter(adapter);
        viewpager_main.setOffscreenPageLimit(2);
        viewpager_main.setCurrentItem(page);
        viewpager_main.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 1){
                    fab.hide();
                    btnSavePOrder.setVisibility(View.VISIBLE);
                }else{
                    fab.show();
                    btnSavePOrder.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                    fab.hide();
                    btnSavePOrder.setVisibility(View.VISIBLE);
                }else{
                    fab.show();
                    btnSavePOrder.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });
    }

    /**
     * Handles action bar clicks
     *
     * @param item menu ite clicked
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
     * Handle activiry results responses
     * @param requestCode request code
     * @param resultCode result code
     * @param data data received
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==  OrderProductFragment.getInstance().RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {

                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    OrderProductFragment.getInstance().filterProducts(barcode.displayValue,"edittext");


                    Timber.v("Barcode read: " + barcode.displayValue);
                } else {
                    CodingMsg.tle(this,getString(R.string.barcode_failure));
                    Timber.v("No barcode captured, intent data is null");
                }
            } else {
                CodingMsg.tle(this,String.format(getString(R.string.barcode_error),CommonStatusCodes.getStatusCodeString(resultCode)));

            }
        }


    }


}
