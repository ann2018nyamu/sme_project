package ke.co.eclectic.quickstore.activities.payment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxTextView;
//import com.manojbhadane.PaymentCardView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.creditors.CreditorsActivity;
import ke.co.eclectic.quickstore.activities.salesorder.SalesOrderActivity;
import ke.co.eclectic.quickstore.activities.salesorder.fragment.SOrderListFragment;
import ke.co.eclectic.quickstore.activities.salesorder.fragment.SOrderProductFragment;
import ke.co.eclectic.quickstore.adapters.ViewPagerAdapter;
import ke.co.eclectic.quickstore.barcode.BarcodeCaptureActivity;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Creditor;
import ke.co.eclectic.quickstore.models.Customer;
import ke.co.eclectic.quickstore.models.InventoryStock;
import ke.co.eclectic.quickstore.models.PaymentType;
import ke.co.eclectic.quickstore.models.PurchaseOrder;
import ke.co.eclectic.quickstore.models.SalesOrder;
import ke.co.eclectic.quickstore.models.others.PurchaseItems;
import ke.co.eclectic.quickstore.rest.response.PaymentTypeResponse;
import ke.co.eclectic.quickstore.rest.response.PurchaseOrderResponse;
import ke.co.eclectic.quickstore.rest.response.SalesOrderResponse;
import ke.co.eclectic.quickstore.viewModel.PaymentTypeViewModel;
import ke.co.eclectic.quickstore.viewModel.PurchaseOrderViewModel;
import ke.co.eclectic.quickstore.viewModel.SalesOrderViewModel;
import timber.log.Timber;


public class PaymentsActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "REPLY";
    private static final int CREDITOR_REQUEST_CODE = 103;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.txtPurchaseTotal)
    TextView txtPurchaseTotal;
    @BindView(R.id.txtSalesOrderId)
    TextView txtSalesOrderId;
    @BindView(R.id.layMpesa)
    LinearLayout layMpesa;
    @BindView(R.id.layCash)
    LinearLayout layCash;
    @BindView(R.id.layCard)
    LinearLayout layCard;
    @BindView(R.id.layPayOption)
    LinearLayout layPayOption;
    @BindView(R.id.layQr)
    LinearLayout layQr;
    @BindView(R.id.layPay)
    LinearLayout layPay;
    @BindView(R.id.txtStoreName)
    TextView txtStoreName;
    @BindView(R.id.txtChange)
    TextView txtChange;
    @BindView(R.id.tilPhoneNumber)
    TextInputLayout tilPhoneNumber;
    @BindView(R.id.tilAmount)
    TextInputLayout tilAmount;

    @BindView(R.id.etxtName)
    EditText etxtName;
    @BindView(R.id.etxtPhoneNumber)
    EditText etxtPhoneNumber;
    @BindView(R.id.etxtAmount)
    EditText etxtAmount;

    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;
//    @BindView(R.id.creditCard)
//    PaymentCardView creditCard;


    private SalesOrder salesOrder;
    private PurchaseOrder purchaseOrder;
    public HashMap<Integer,InventoryStock> selectedInventoryStockH = new HashMap<>() ;
    private SalesOrderViewModel mSalesOrderViewModel;
    private PurchaseOrderViewModel mPurchaseOrderViewModel;
    public Customer selectedCustomer= new Customer();
    private String choosenPayOption="";
    private CompositeDisposable disposable = new CompositeDisposable();
    private Double totalAmount=0.00;
    public HashMap<String,Integer> paymentTypeH =new HashMap<>() ;
    private Creditor selectedCreditor = new Creditor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle  extra = getIntent().getExtras();
        if(extra  != null){

            if(null != extra.get("salesorder")) {
                salesOrder = new Gson().fromJson(extra.get("salesorder").toString(), SalesOrder.class);
                totalAmount = salesOrder.getTotalcost();
                mSalesOrderViewModel = ViewModelProviders.of(this).get(SalesOrderViewModel.class);
            }else if(null != extra.get("posorder")) {
                salesOrder  = new Gson().fromJson(extra.get("posorder").toString(),SalesOrder.class);
                totalAmount = salesOrder.getTotalcost();
                mSalesOrderViewModel = ViewModelProviders.of(this).get(SalesOrderViewModel.class);
            }else if(null != extra.get("purchaseorder")) {
                purchaseOrder  = new Gson().fromJson(extra.get("purchaseorder").toString(),PurchaseOrder.class);
                totalAmount = purchaseOrder.getTotalcost();
                mPurchaseOrderViewModel = ViewModelProviders.of(this).get(PurchaseOrderViewModel.class);

            }else{
                totalAmount = 100.00;
            }
        }

        setContentView(R.layout.activity_payments);
        ButterKnife.bind(this);

        initToolbar();
        initEditTextView();
        initButton();
        initTextView();
        initData();
        initObservable();

    }
    /**
     * initializes sales order data
     */
    private void initData() {
        PaymentTypeViewModel mPaymentTypeViewModel = ViewModelProviders.of(this).get(PaymentTypeViewModel.class);

        mPaymentTypeViewModel.getAllPaymentTypes();
        mPaymentTypeViewModel.getApiResponse().observe(this, myApiResponses -> {
            Timber.v(myApiResponses.getStatus() + "  " + myApiResponses.getOperation());
            if (myApiResponses.getStatus().contentEquals("success")) {
                if (myApiResponses.getOperation().contains("apirefresh")) {
                    PaymentTypeResponse paymentTypeResponse = (PaymentTypeResponse) myApiResponses.getObject();
                    for (PaymentType pt : paymentTypeResponse.getPaymentTypeList()) {
                        paymentTypeH.put(pt.getName(), pt.getPaymenttypeid());
                        Timber.v(pt.getName().concat(" -> "+ pt.getPaymenttypeid()));
                    }
                }
            } else {
                if (null != myApiResponses.getError()) {
                    GlobalMethod.parseCommError(this, myApiResponses.getError(), rootView);
                }
            }
        });
        if(mSalesOrderViewModel != null){
            mSalesOrderViewModel.getApiResponse().observe(this, myApiResponses->{
                Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
                GlobalMethod.stoploader(this);
                if(myApiResponses.getStatus().contentEquals("success")){
                    if(myApiResponses.getOperation().contains("apipossave")) {
                        SalesOrderResponse salesOrderResponse = (SalesOrderResponse) myApiResponses.getObject();
                        startPayment(salesOrderResponse.getPosOrderId().toString());
                    }

                    if(myApiResponses.getOperation().contains("apisave")) {
                        SalesOrderResponse salesOrderResponse = (SalesOrderResponse) myApiResponses.getObject();
                        startPayment(salesOrder.getOrderid());
                    }

                }else{
                    if (null != myApiResponses.getError()){
                        GlobalMethod.parseCommError(this,myApiResponses.getError(),rootView);
                    }
                }
            });
        }
        if(mPurchaseOrderViewModel != null){

            mPurchaseOrderViewModel.getApiResponse().observe(this,myApiResponses->{
                Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
                GlobalMethod.stoploader(this);
                if(myApiResponses.getStatus().contentEquals("success")){

                    if(myApiResponses.getOperation().contains("apisave")) {
                        PurchaseOrderResponse purchaseOrderResponse = (PurchaseOrderResponse) myApiResponses.getObject();
                        GlobalMethod.showSuccess(this,purchaseOrderResponse.getMessage(),true);
                        Completable.timer(4, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                                .subscribe(new DisposableCompletableObserver() {
                                    @Override
                                    public void onComplete() {
                                        try {
                                            finish();
                                        }catch (Exception e){

                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                });
                    }

                }else{
                    if (null != myApiResponses.getError()){
                        GlobalMethod.parseCommError(this,myApiResponses.getError(),rootView);
                    }
                }
            });
        }


    }

    /**
     * initializes observables
     */
    private void initObservable() {
        disposable.add(  RxTextView.textChanges(etxtName)
                .skip(1).map(CharSequence::toString).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( ));
        //disposable.add(
                RxTextView.textChanges(etxtPhoneNumber)
                .skip(1)
                .map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( );
        //txtChange.setText();

                RxTextView.textChanges(etxtAmount)
                .skip(1).map(CharSequence::toString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        try {
                            if (s.contentEquals("")) {
                                s = "0.00";
                            }
                            Double change = totalAmount - Double.parseDouble(s);
                            txtChange.setText("Change Ksh. ".concat(String.format("%.2f", change)));
                        }catch (Exception e){
                            etxtAmount.getText().clear();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEditTextView() {

        if(salesOrder != null){
            etxtName.setText(salesOrder.getCustomername());
        }

        etxtName.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });

        etxtPhoneNumber.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });

        etxtAmount.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });

    }

    public void toggleView(){
        if(layPay.getVisibility() == View.GONE){
            layPay.setVisibility(View.VISIBLE);
            layPayOption.setVisibility(View.GONE);
        }else{
            layPay.setVisibility(View.GONE);
            layPayOption.setVisibility(View.VISIBLE);
        }

    }
    @OnClick(R.id.layMpesa)
    public void payMpesa(){
        choosenPayOption = "mpesa";
        if(mPurchaseOrderViewModel!=null){
            purchaseOrder.setPaymentType(paymentTypeH.get(choosenPayOption));
        }else{
            salesOrder.setPaymentType(paymentTypeH.get(choosenPayOption));
        }

        toggleView();
        tilAmount.setVisibility(View.GONE);
        tilPhoneNumber.setVisibility(View.VISIBLE);
        txtChange.setVisibility(View.GONE);
    }
    @OnClick(R.id.layCash)
    public void payCash(){
        choosenPayOption = "cash";
        if(mPurchaseOrderViewModel!=null){
            purchaseOrder.setPaymentType(paymentTypeH.get(choosenPayOption));
        }else{
            salesOrder.setPaymentType(paymentTypeH.get(choosenPayOption));
        }

        toggleView();
        tilAmount.setVisibility(View.VISIBLE);
        txtChange.setVisibility(View.VISIBLE);
        tilPhoneNumber.setVisibility(View.GONE);
    }

    @OnClick(R.id.layCard)
    public void payCard(){
        choosenPayOption = "card";
        if(mPurchaseOrderViewModel!=null){
            purchaseOrder.setPaymentType(paymentTypeH.get(choosenPayOption));
        }else{
            salesOrder.setPaymentType(paymentTypeH.get(choosenPayOption));
        }
//        creditCard.setVisibility(View.VISIBLE);
        //Callbacks
//        creditCard.setOnPaymentCardEventListener(new PaymentCardView.OnPaymentCardEventListener() {
//            @Override
//            public void onCardDetailsSubmit(String month, String year, String cardNumber, String cvv) {
//                CodingMsg.tlw(PaymentsActivity.this, month+" "+year+" "+cardNumber+" "+cvv);
//            }
//
//            @Override
//            public void onError(String error) {
//                CodingMsg.tlw(PaymentsActivity.this, error);
//            }
//
//            @Override
//            public void onCancelClick() {

//            }
//        });
        CodingMsg.tl(this,"Under progress");
    }

    @OnClick(R.id.layQr)
    public void payQr(){
        choosenPayOption = "qr";
        if(mPurchaseOrderViewModel!=null){
            purchaseOrder.setPaymentType(paymentTypeH.get(choosenPayOption));
        }else{
            salesOrder.setPaymentType(paymentTypeH.get(choosenPayOption));
        }
        if(salesOrder != null ){
            //generate qr code and display it
        }
        if(purchaseOrder != null){
            //generate qr code and display it
        }

       //open qrcode
        CodingMsg.tl(this,"Under progress");
    }
    @OnClick(R.id.layCreditors)
    public void payCredit(){
        choosenPayOption = "credit";
        if(mPurchaseOrderViewModel!=null){
            purchaseOrder.setPaymentType(paymentTypeH.get(choosenPayOption));
        }else{
            salesOrder.setPaymentType(paymentTypeH.get(choosenPayOption));
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("action", "select");
        if(mPurchaseOrderViewModel!=null){
            data.put("totalAmount", purchaseOrder.getTotalcost().toString());
        }else{
            data.put("totalAmount", salesOrder.getTotalcost().toString());
        }

        GlobalMethod.goToActivity(this, CreditorsActivity.class, data,  CREDITOR_REQUEST_CODE);

    }

    /**
     * initializes buttons
     */
    private void initButton() {

    }

    /**
     * saves or updates paid orders in db/application
     */
    @OnClick(R.id.btnSubmit)
    public void saveOrder(){

        if(!validate_info()){

            return;
        }

        processOrderPayment();

    }
    /*
      This method is used for processing the changing of the payment status of the order
     */
    private void processOrderPayment(){
        GlobalMethod.showloader(this,"Processing payment",true);
        if(mPurchaseOrderViewModel!=null){
            mPurchaseOrderViewModel.insert(purchaseOrder, "approvepurchase");
        }else if(salesOrder.getOrdertype().contentEquals("pos") ){
            mSalesOrderViewModel.savePos(salesOrder);
        } else {
            mSalesOrderViewModel.insert(salesOrder, "approvesalesorder");
        }
    }
    private void startPayment(String orderid){
        if(choosenPayOption.contentEquals("mpesa")) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("payment_type", "mpesa");
            data.put("orderid", orderid);
            data.put("amount", totalAmount);
            data.put("type", salesOrder.getOrdertype());
            data.put("phone", etxtPhoneNumber.getText().toString());
            GlobalMethod.showloader(this,"processing request",true);

            if(mPurchaseOrderViewModel!=null){
                data.put("type", "purchaseorder");
                mPurchaseOrderViewModel.sendPaymentRequest(data);
            }else{
                mSalesOrderViewModel.sendPaymentRequest(data);
            }

        }
        if(choosenPayOption.contentEquals("cash")){
            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_REPLY, "paid");
            setResult(RESULT_OK, replyIntent);
            finish();
        }

        if(choosenPayOption.contentEquals("card")){
             CodingMsg.tlw(this,"Under development");

        }

        if(choosenPayOption.contentEquals("qrcode")){
            CodingMsg.tlw(this,"Under development");
        }
        if(choosenPayOption.contentEquals("credit")){

        }


    }

    /**
     * validates sales order information( valid if true and vice versa)
     *
     * @return boolean true if valid
     */
    private boolean validate_info() {
       if(choosenPayOption.contentEquals("mpesa")){
           if(etxtName.getText().toString().trim().isEmpty()){
               etxtName.setError("Please Enter customer name");
               GlobalMethod.showMessage(this,"Please customer name",rootView,"",false);
               return false;
           }
           if(etxtPhoneNumber.getText().toString().trim().isEmpty()){
               etxtPhoneNumber.setError("Please Enter customer phone");
               GlobalMethod.showMessage(this,"Please customer phonenumber",rootView,"",false);
               return false;
           }


       }

       if(choosenPayOption.contentEquals("cash")){
           if(etxtName.getText().toString().trim().isEmpty()){
               etxtName.setError("Please Enter customer name");
               GlobalMethod.showMessage(this,"Please customer name",rootView,"",false);
               return false;
           }
           if(etxtAmount.getText().toString().trim().isEmpty()){
               etxtAmount.setError("Please Enter customer amount");
               GlobalMethod.showMessage(this,"Please customer amount",rootView,"",false);
               return false;
           }
       }

       if(choosenPayOption.contentEquals("card")){
           return false;
       }

       if(choosenPayOption.contentEquals("qrcode")){
           return false;
       }
       if(choosenPayOption.contentEquals("credit")){
           return false;
       }


       return true;
    }



    /**
     * initializes textviews
     */
    private void initTextView() {
        txtPurchaseTotal.setText("Total Ksh ".concat( String.format("%.2f",totalAmount)));
        txtStoreName.setText(GlobalVariable.getCurrentUser().getStorename(this));
    }

    /**
     * initializes toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Payment Option".toUpperCase());
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

        //OPTION WHEN USER SELECTS PAY WITH CREDITORS
        if (requestCode == CREDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            //returns a single category object
            selectedCreditor = new Gson().fromJson(data.getStringExtra(CreditorsActivity.EXTRA_REPLY), Creditor.class);
            //send request to server
            processOrderPayment();

        }



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
                if(layPayOption.getVisibility() == View.GONE){
                    toggleView();
                }else{
                    finish();
                }

                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }



}
