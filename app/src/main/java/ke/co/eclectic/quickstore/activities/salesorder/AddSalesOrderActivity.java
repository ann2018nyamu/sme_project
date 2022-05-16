package ke.co.eclectic.quickstore.activities.salesorder;

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
import io.reactivex.disposables.CompositeDisposable;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.adapters.ViewPagerAdapter;
import ke.co.eclectic.quickstore.barcode.BarcodeCaptureActivity;
import ke.co.eclectic.quickstore.activities.salesorder.fragment.SOrderListFragment;
import ke.co.eclectic.quickstore.activities.salesorder.fragment.SOrderProductFragment;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Customer;
import ke.co.eclectic.quickstore.models.InventoryStock;
import ke.co.eclectic.quickstore.models.SalesOrder;
import ke.co.eclectic.quickstore.rest.response.SalesOrderResponse;
import ke.co.eclectic.quickstore.viewModel.SalesOrderViewModel;
import timber.log.Timber;

/**
 * The type Pos activity.
 */
public class AddSalesOrderActivity extends AppCompatActivity {
    private static final int BARCODE_REQUEST_CODE = 10;
    private static final int RC_BARCODE_CAPTURE = 9010;

    @BindView(R.id.viewpager_main)
    ViewPager viewpager_main;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtSalesTitle)
    TextView txtSalesTitle;
    @BindView(R.id.txtPurchaseCount)
    TextView txtPurchaseCount;
    @BindView(R.id.txtPurchaseTotal)
    TextView txtPurchaseTotal;
    @BindView(R.id.txtSalesOrderId)
    TextView txtSalesOrderId;
    @BindView(R.id.txtStoreName)
    TextView txtStoreName;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.imgCancel)
    ImageView imgCancel;
    @BindView(R.id.btnCheckOutOrder)
    Button btnCheckOutOrder;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;


    private SalesOrder salesOrder = new SalesOrder();
    public HashMap<Integer,InventoryStock> selectedInventoryStockH =new HashMap<>() ;
    private SalesOrderViewModel mSalesOrderViewModel;
    private Integer page = 0;
    public Customer selectedCustomer= new Customer();
    private String storename="";
    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            try{
                salesOrder = new Gson().fromJson(extra.get("salesorder").toString(),SalesOrder.class);
                page = Integer.valueOf(extra.get("page").toString());

                if(null == page){
                    page = 0;
                }
                if(null == salesOrder){
                    salesOrder = new SalesOrder();
                }
            }catch (Exception e){
                Timber.v(e.getMessage());
            }
        }
        setContentView(R.layout.activity_add_sale_order);
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
        if( salesOrder.getSalesorderid()!=0){
            btnCheckOutOrder.setText(getString(R.string.update));
        }
    }

    /**
     * handles checkout button clicks events
     */
    @OnClick(R.id.btnCheckOutOrder)
    public void saveOrder(){
        if(validate_info()){
            String requestType = "createsale";
            if(salesOrder.getSalesorderid() != 0){
                requestType = "editsalesorder";
            }
            mSalesOrderViewModel.insert(salesOrder,requestType);
        }

    }

    /**
     * validates sales order information( valid if true and vice versa)
     *
     * @return boolean true if valid
     */
    private boolean validate_info() {
        if(selectedCustomer.getCustomerid() == 0){
            GlobalMethod.showMessage(this,"Please select a customer",rootView,"",false);
            return false;
        }
        salesOrder.setCustomerid(selectedCustomer.getCustomerid());
        salesOrder.setStoreid(GlobalVariable.getCurrentUser().getStoreid());
        return true;
    }

    /**
     * initializes sales order data
     */
    private void initData() {
          mSalesOrderViewModel = ViewModelProviders.of(this).get(SalesOrderViewModel.class);
         mSalesOrderViewModel.getApiResponse().observe(this,myApiResponses -> {
            if(myApiResponses.getStatus().contentEquals("success")){
                Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
                if(myApiResponses.getOperation().contains("apisave")) {
                    SalesOrderResponse salesOrderResponse = (SalesOrderResponse) myApiResponses.getObject();
                    CodingMsg.tls(this,salesOrderResponse.getMessage());
                    selectedInventoryStockH.clear();
                    refreshOrder();
                    finish();
//                    if(page != 0){
//                        finish();
//                    }else{
//                        viewpager_main.setCurrentItem(0);
//                    }
                }

            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(this, myApiResponses.getError(),rootView);
                }
            }
        });

        if(salesOrder.getSalesorderid() != 0){
            selectedCustomer = new Customer();
            selectedCustomer.setCustomerid(salesOrder.getCustomerid());
            selectedCustomer.setContactname(salesOrder.getCustomername());
            Timber.v("selectedSupplier "+salesOrder.getCustomername());
            Timber.v("getInventoryStockListStr "+salesOrder.getInventoryStockListStr());
            Timber.v("getPurchase_items size "+salesOrder.getSalesorder_items().size());
            selectedInventoryStockH = salesOrder.getInventoryStockListH();
        }
    }

    /**
     * refereshes sales order
     */
    public void refreshOrder(){
        Timber.v("selectedInventoryStockH size "+selectedInventoryStockH.size());

        Double orderTotal = 0.00;
        Double orderTotalDiscount = 0.00;
        Double itemCount = 0.0;
        SOrderListFragment.inventoryStockList.clear();
        for(Map.Entry<Integer,InventoryStock> entry : selectedInventoryStockH.entrySet()){
            SOrderListFragment.inventoryStockList.add(entry.getValue());
            orderTotal += entry.getValue().getChoosenPrice()*entry.getValue().getChoosenquantity();
            Timber.v("sorder item "+entry.getValue().getProductname()+"  "+entry.getValue().getChoosenPrice() +"  "+entry.getValue().getChoosenquantity()+"  total:"+ orderTotal);
            itemCount += entry.getValue().getChoosenquantity();
            orderTotalDiscount += entry.getValue().getItemdiscount()*entry.getValue().getChoosenquantity();
        }

        Timber.v("sorder Total  "+ orderTotal);
        Timber.v("sorder itemCount  "+ itemCount);
        refreshSalesOrderViews(orderTotal.toString(),itemCount);
        SOrderListFragment.getInstance().refreshList();

        //settign object
        salesOrder.setTotaldiscount(orderTotalDiscount);
        salesOrder.setTotalcost(orderTotal);
        salesOrder.setInventoryStockListStr(new Gson().toJson(SOrderListFragment.inventoryStockList));
        salesOrder.setStoreid(GlobalVariable.getCurrentUser().getStoreid(this));
        salesOrder.setCustomerid(0);

        if (itemCount > 0){
            btnCheckOutOrder.setEnabled(true);
            btnCheckOutOrder.setBackgroundColor(ContextCompat.getColor(this, R.color.activeColor));
        }else{
            btnCheckOutOrder.setEnabled(false);
            btnCheckOutOrder.setBackgroundColor(ContextCompat.getColor(this, R.color.grayColor));
        }

    }

    /**
     * initializes imageview
     */
    private void initImageview() {
        imgCancel.setOnClickListener( view -> finish());
        if(salesOrder.getSalesorderid() == 0){
            imgCancel.setVisibility(View.GONE);
        }
    }

    /**
     * initializes textviews
     */
    private void initTextView() {
        txtPurchaseTotal.setText(R.string.total_pos_label);
        txtPurchaseCount.setText( "0");
        txtStoreName.setText(GlobalVariable.getCurrentUser().getStorename(this));
        if(salesOrder.getSalesorderid() != 0){
            txtSalesTitle.setText("Edit Sales Order");
        }
    }

    /**
     * refereshes salesorderview
     * @param cartTotal total cart amount
     * @param itemCount total cart item count
     */
    public void refreshSalesOrderViews(String cartTotal,Double itemCount){
        txtPurchaseTotal.setText("Total Ksh."+ cartTotal);
        txtPurchaseCount.setText( itemCount.toString());
    }


    /**
     *
     * initializes floating button
     *
     */
    private void initFab() {
        fab.setOnClickListener(view -> {
            SOrderProductFragment.getInstance().openBarcode();
        });
    }

    /**
     * hides floating button
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
        if(salesOrder.getSalesorderid() != 0){
            setTitle("Edit Sales Order");
        }else{
            setTitle("Add Sales Order");
        }
    }

    /**
     * initializes viewpager title
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
     * initializes viewpager data
     */
    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(SOrderProductFragment.getInstance(), getResources().getString(R.string.products));
        adapter.addFragment(SOrderListFragment.getInstance(),getString(R.string.order_list) );//getResources().getString(R.string.invoice)

        viewpager_main.setAdapter(adapter);
        viewpager_main.setOffscreenPageLimit(2);
        viewpager_main.setCurrentItem(page);
        viewpager_main.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if(position == 1){
                    fab.hide();
                    btnCheckOutOrder.setVisibility(View.VISIBLE);
                }else{
                    fab.show();
                    btnCheckOutOrder.setVisibility(View.GONE);
                }

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                    fab.hide();
                    btnCheckOutOrder.setVisibility(View.VISIBLE);
                }else{
                    fab.show();
                    btnCheckOutOrder.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });
    }

    /**
     * handle menuitem clicked
     *
     * @param item menu item clicked
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
     * handle activity result response
     *
     * @param requestCode request code
     * @param resultCode result code
     * @param data data received
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

                    CodingMsg.tle(this,barcode.displayValue);

                } else {
                    CodingMsg.tle(this,getString(R.string.barcode_failure));

                }
            } else {
                CodingMsg.tle(this,String.format(getString(R.string.barcode_error),CommonStatusCodes.getStatusCodeString(resultCode)));

            }
        }
    }

}
