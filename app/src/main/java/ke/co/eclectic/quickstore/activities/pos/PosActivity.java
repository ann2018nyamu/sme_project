package ke.co.eclectic.quickstore.activities.pos;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.vatsal.imagezoomer.ImageZoomButton;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.CountryCodeActivity;
import ke.co.eclectic.quickstore.activities.payment.PaymentsActivity;
import ke.co.eclectic.quickstore.adapters.ViewPagerAdapter;
import ke.co.eclectic.quickstore.activities.pos.fragment.CartFragment;
import ke.co.eclectic.quickstore.activities.pos.fragment.PosStocksFragment;
import ke.co.eclectic.quickstore.barcode.BarcodeCaptureActivity;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.InventoryStock;
import ke.co.eclectic.quickstore.models.PaymentType;
import ke.co.eclectic.quickstore.models.SalesOrder;
import ke.co.eclectic.quickstore.rest.response.PaymentTypeResponse;
import ke.co.eclectic.quickstore.rest.response.SalesOrderResponse;
import ke.co.eclectic.quickstore.viewModel.PaymentTypeViewModel;
import ke.co.eclectic.quickstore.viewModel.SalesOrderViewModel;
import timber.log.Timber;

/**
 * The type Pos activity.
 */
@SuppressWarnings("deprecation")
public class PosActivity extends AppCompatActivity {
    private static final int BARCODE_REQUEST_CODE = 10;
    @BindView(R.id.viewpager_main)
    ViewPager viewpager_main;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtSaleInfo)
    TextView txtSaleInfo;
    @BindView(R.id.txtSalesCount)
    TextView txtSalesCount;
    @BindView(R.id.txtCartTotal)
    TextView txtCartTotal;
    @BindView(R.id.txtStoreName)
    TextView txtStoreName;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.btnCheckout)
    Button btnCheckout;
    @BindView(R.id.btnGetQrCode)
    Button btnGetQrCode;
    @BindView(R.id.checkoutLay)
    LinearLayout checkoutLay;
    @BindView(R.id.imgPicPreview1)
    ImageZoomButton imgPicPreview1;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;

    private static final int PAYMENT_REQUEST_CODE = 10;
    HashMap<String,Object> qrData= new HashMap<>();
    String qrDataStr= "";
    private SalesOrderViewModel mSalesOrderViewModel;

    private  Double paidAmount=0.00 ;
    private  Double balance=0.00 ;
    SalesOrder posSalesOrder = new SalesOrder();
    public HashMap<Integer,InventoryStock> selectedInventoryStockH =new HashMap<>() ;
    public HashMap<String,Integer> paymentTypeH =new HashMap<>() ;
    private String paymentMethod="";
    private CompositeDisposable disposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);
        ButterKnife.bind(this);
        mSalesOrderViewModel = ViewModelProviders.of(this).get(SalesOrderViewModel.class);

        initToolbar();
        initFab();
        initTextView();
        setupViewPager();
        setupTabIcons();
        initData();

    }

    /**
     * initiailizes activity data
     */
    private void initData() {
        PaymentTypeViewModel mPaymentTypeViewModel = ViewModelProviders.of(this).get(PaymentTypeViewModel.class);

        mSalesOrderViewModel.getApiResponse().observe(this,myApiResponses -> {

            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());

            if(myApiResponses.getStatus().contentEquals("success")){
                Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
                if(myApiResponses.getOperation().contains("apipossave")) {
                    SalesOrderResponse salesOrderResponse = (SalesOrderResponse) myApiResponses.getObject();

                    CodingMsg.tls(this,salesOrderResponse.getMessage());
                    selectedInventoryStockH.clear();
                    refreshCart();
                    viewpager_main.setCurrentItem(0);
                }
                if(myApiResponses.getOperation().contains("apipayment")) {
                    SalesOrderResponse salesOrderResponse = (SalesOrderResponse) myApiResponses.getObject();
                    CodingMsg.tls(this,salesOrderResponse.getMessage());
                }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(this, myApiResponses.getError(),rootView);
                }
            }
        });

        mPaymentTypeViewModel.getAllPaymentTypes();
        mPaymentTypeViewModel.getApiResponse().observe(this, myApiResponses -> {
            Timber.v(myApiResponses.getStatus() + "  " + myApiResponses.getOperation());
            if (myApiResponses.getStatus().contentEquals("success")) {
                if (myApiResponses.getOperation().contains("refresh")) {
                    PaymentTypeResponse paymentTypeResponse = (PaymentTypeResponse) myApiResponses.getObject();
                    for (PaymentType pt : paymentTypeResponse.getPaymentTypeList()) {
                        paymentTypeH.put(pt.getName(), pt.getPaymenttypeid());
                    }
                }
            } else {
                if (null != myApiResponses.getError()) {
                    GlobalMethod.parseCommError(this, myApiResponses.getError(), rootView);
                }
            }
        });

    }

    /**
     * initializes textviews
     */
    private void initTextView() {
        txtCartTotal.setText(R.string.total_pos_label );
        txtSalesCount.setText( "0");
        txtStoreName.setText( GlobalVariable.getCurrentUser().getStorename());
    }

    /**
     * performs operation to refresh cart details
     */
    public void refreshCart(){
        Timber.v("selectedInventoryStockH size "+selectedInventoryStockH.size());

        Double orderTotal = 0.00;
        Double orderTotalDiscount = 0.00;
        Double itemCount = 0.0;

        CartFragment.productlists.clear();
        for(Map.Entry<Integer,InventoryStock> entry : selectedInventoryStockH.entrySet()){
            CartFragment.productlists.add(entry.getValue());
            orderTotal += entry.getValue().getChoosenPrice()*entry.getValue().getChoosenquantity();
            Timber.v("cartitem  "+entry.getValue().getProductname()+"  "+entry.getValue().getChoosenPrice() +"  "+entry.getValue().getChoosenquantity()+"  total:"+ orderTotal);
            itemCount += entry.getValue().getChoosenquantity();
            orderTotalDiscount += entry.getValue().getItemdiscount()*entry.getValue().getChoosenquantity();
        }
        //setting object

        posSalesOrder.setTotaldiscount(orderTotalDiscount);
        posSalesOrder.setPaidamount(orderTotal);
        posSalesOrder.setTotalcost(orderTotal);
        posSalesOrder.setInventoryStockListStr(new Gson().toJson( CartFragment.productlists));
        posSalesOrder.setStoreid(GlobalVariable.getCurrentUser().getStoreid(this));
        posSalesOrder.setOrdertype("pos");

        Timber.v("cartTotal "+ orderTotal);
        CartFragment.getInstance().refreshList();
        refreshCartViews(orderTotal.toString(),itemCount);
    }

    /**
     * refereshes cart details in activity  views
     *
     * @param cartTotal total cost of the cart items
     * @param itemCount  total item in cart
     */
    public void refreshCartViews(String cartTotal,Double itemCount){

        txtCartTotal.setText("Total Ksh "+ cartTotal);
        txtSalesCount.setText(((Integer)itemCount.intValue()).toString());
        qrData.put("Total",cartTotal);
        qrData.put("Business",GlobalVariable.getCurrentUser().getBusinessname());

        Timber.v(GlobalVariable.getCurrentUser().getBusinessname().concat("\nTotal ksh ".concat(cartTotal)));
        qrDataStr = GlobalVariable.getCurrentUser().getBusinessname().concat("\nTotal ksh ".concat(cartTotal));
        autogenerateCode();
        if (itemCount > 0) {
            txtSaleInfo.setText("Sales");
            btnCheckout.setEnabled(true);
            btnCheckout.setBackgroundColor(ContextCompat.getColor(this, R.color.activeColor));
            btnGetQrCode.setBackgroundColor(ContextCompat.getColor(this, R.color.activeColor));
        }else{
            txtSaleInfo.setText("No Sale");
            btnCheckout.setEnabled(false);
            btnCheckout.setBackgroundColor(ContextCompat.getColor(this, R.color.grayColor));
            btnGetQrCode.setBackgroundColor(ContextCompat.getColor(this, R.color.grayColor));
        }


    }

    /**
     * initializes floating button
     */
    private void initFab() {
        fab.setOnClickListener(view -> {
            PosStocksFragment.getInstance().openBarcode();
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
     * displays checkout dialogue  for payment
     */
    @OnClick(R.id.btnCheckout)
    public void savePos(){
        HashMap<String,String> data= new HashMap<>();

        data.put("posorder",new Gson().toJson(posSalesOrder));
        GlobalMethod.goToActivity(this, PaymentsActivity.class,data,PAYMENT_REQUEST_CODE);

       // showPaymentDialogue();
    }

    private void showPaymentDialogue() {

        //Initialize the Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//Source of the data in the DIalog
        // CharSequence[] array = {"Mpesa","Card", "Credit","cash","Qr Code"};
        CharSequence[] paymentAr = paymentTypeH.keySet().toArray(new String[0]);

        // Set the dialog title
        builder.setTitle("Select Payment Method")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(paymentAr, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        paymentMethod = paymentAr[which].toString();
                    }
                })
                .setPositiveButton("Ok", (dialog, id) -> {
                    // User clicked OK, so save the result somewhere
                    // or return them to the component that opened the dialog
                    Timber.v("setPositiveButton "+id);
                    posSalesOrder.setPaymentType(paymentTypeH.get(paymentMethod));

                    if(paymentMethod.toLowerCase().contains("mpesa")){//mpesa payment
                        showPhoneNumber();
                    }else if(paymentMethod.toLowerCase().contains("cash")){//cash payment
                        GlobalMethod.showSuccess(this,"Payment received successfully",true);
                        showCashDialogue();
                    }else if(paymentMethod.toLowerCase().contains("qr")){//qr payment
                        imgPicPreview1.performClick();
                    }else{
                        mSalesOrderViewModel.savePos(posSalesOrder);
                    }

                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                });

        Dialog dialog = builder.create();
        dialog.show();
    }

    /**
     * displays cash dialogue
     */
    @SuppressLint("ClickableViewAccessibility")
    private void showCashDialogue() {
        Dialog addCateogryDialog = new Dialog(this);
        addCateogryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addCateogryDialog.setContentView(R.layout.dialogue_paycash);
        addCateogryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtTitle =  addCateogryDialog.findViewById(R.id.txtTitle);
        Button btnSave = addCateogryDialog.findViewById(R.id.btnSave);
        Button btnCancel = addCateogryDialog.findViewById(R.id.btnCancel);
        EditText etxtReceivedAmount = addCateogryDialog.findViewById(R.id.etxtReceivedAmount);
        TextView txtBalanceAmount = addCateogryDialog.findViewById(R.id.txtBalanceAmount);
        TextView txtTotalCash = addCateogryDialog.findViewById(R.id.txtTotalCash);
        etxtReceivedAmount.setHint(posSalesOrder.getTotalcostStr());
        txtTotalCash.setText(posSalesOrder.getTotalcostStr());
         RxTextView.textChanges(etxtReceivedAmount)
                .skip(1).map(CharSequence::toString).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        Timber.v(s);
                        if(s.isEmpty()){
                            s = posSalesOrder.getTotalcost().toString();
                            etxtReceivedAmount.setHint(posSalesOrder.getTotalcostStr());
                        }

                        posSalesOrder.setPaidamount(Double.parseDouble(s));
                        posSalesOrder.setBalance(Double.parseDouble(s)  - posSalesOrder.getTotalcost() );

                        if(posSalesOrder.getBalance() < 0){
                            txtBalanceAmount.setTextColor(ContextCompat.getColor(PosActivity.this,R.color.errorColor));
                        }else{
                            txtBalanceAmount.setTextColor(ContextCompat.getColor(PosActivity.this,R.color.successColor));
                        }

                        txtBalanceAmount.setText(posSalesOrder.getBalanceStr());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


        btnSave.setOnClickListener(v -> {
            String name = "";
            if(posSalesOrder.getBalance() < 0){
                etxtReceivedAmount.setError("Product sold in low price");
                CodingMsg.tle(this,"Product sold in low price");
                return ;
            }

            mSalesOrderViewModel.savePos(posSalesOrder);

            addCateogryDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            addCateogryDialog.dismiss();
        });

        addCateogryDialog.setCancelable(true);
        addCateogryDialog.show();
    }

    /**
     * displays phone dialogue
     */
    @SuppressLint("ClickableViewAccessibility")
    private void showPhoneNumber(){
        Dialog addCateogryDialog = new Dialog(this);
        addCateogryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addCateogryDialog.setContentView(R.layout.dialogue_addphone);
        addCateogryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtTitle =  addCateogryDialog.findViewById(R.id.txtTitle);
        TextInputLayout tilName =  addCateogryDialog.findViewById(R.id.tilName);
        Button btnSave = addCateogryDialog.findViewById(R.id.btnSave);
        Button btnCancel = addCateogryDialog.findViewById(R.id.btnCancel);
        EditText etxtPhone = addCateogryDialog.findViewById(R.id.etxtPhone);
        txtTitle.setText("Enter  Phone Number");
        btnSave.setText("Send Request");

        etxtPhone.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });


        btnSave.setOnClickListener(v -> {
            String name = "";
            if(etxtPhone.getText().toString().trim().isEmpty()){
                etxtPhone.setError("Enter Phone Number");
                return ;
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("payment_type", "mpesa");
            data.put("amount", posSalesOrder.getTotalcost());
            data.put("phone", etxtPhone.getText().toString());
            mSalesOrderViewModel.sendPaymentRequest(data);
            mSalesOrderViewModel.savePos(posSalesOrder);

            addCateogryDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            addCateogryDialog.dismiss();
        });

        addCateogryDialog.setCancelable(true);
        addCateogryDialog.show();

    }


    /**
     * enlarges qr code displayed to  user
     */
    @OnClick(R.id.btnGetQrCode)
    public void btnGetQrCode(){
        imgPicPreview1.performClick();
    }

    /**
     * Autogenerate qr code
     */
    private void autogenerateCode() {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            Bitmap bitmap = null;
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(qrDataStr, BarcodeFormat.QR_CODE, 200, 200);
                    bitmap = new BarcodeEncoder().createBitmap(bitMatrix);
                    imgPicPreview1.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
    }

    /**
     * initializes toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("About Your Business");

    }

    /**
     * sets up toolbar icons/text
     */
    private void setupTabIcons() {
        tabLayout.setupWithViewPager(viewpager_main);
        View view =  LayoutInflater.from(this).inflate(R.layout.custom_tab, null);

        TextView tabOne = view.findViewById(R.id.tab);
        tabOne.setText(R.string.products);
        tabOne.setTextColor(getResources().getColor(R.color.editTextColor));
        tabOne.setTypeface(GlobalVariable.getMontserratSemiBold(this));

        tabLayout.getTabAt(0).setCustomView(view);

        view =  LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne = view.findViewById(R.id.tab);
        tabOne.setText(R.string.invoice);
        tabOne.setTextColor(getResources().getColor(R.color.editTextColor));
        tabOne.setTypeface(GlobalVariable.getMontserratSemiBold(this));
        tabLayout.getTabAt(1).setCustomView(view);
    }

    /**
     * initializes viewpager details
     */
    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(PosStocksFragment.getInstance(), getResources().getString(R.string.products));
        adapter.addFragment(CartFragment.getInstance(),getString(R.string.cart) );//getResources().getString(R.string.invoice)


        viewpager_main.setAdapter(adapter);
        viewpager_main.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 1){
                    fab.hide();
                    checkoutLay.setVisibility(View.VISIBLE);

                }else{
                    fab.show();
                    checkoutLay.setVisibility(View.GONE);

                }
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                    fab.hide();
                    checkoutLay.setVisibility(View.VISIBLE);
                }else{
                    fab.show();
                    checkoutLay.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * cleans up the activity data
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //releasing memory allocated by rx operations
        disposable.clear();
    }

    /**
     * listen to menu click event
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
     * handles activiry result responses
     * @param requestCode request code
     * @param resultCode result code
     * @param data data received
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENT_REQUEST_CODE && resultCode == RESULT_OK) {

            selectedInventoryStockH.clear();
            refreshCart();
            viewpager_main.setCurrentItem(0);

        }


        if (requestCode == PosStocksFragment.getInstance().RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

                    PosStocksFragment.getInstance().filterProducts(barcode.displayValue,"edittext");
                    Timber.v("Barcode read: " + barcode.displayValue);
                }else{
                    CodingMsg.tle(this,getString(R.string.barcode_failure));
                    Timber.v("No barcode captured, intent data is null");
                }

            } else {
                CodingMsg.tle(this,String.format(getString(R.string.barcode_error),CommonStatusCodes.getStatusCodeString(resultCode)));

            }
        }



    }


}
