package ke.co.eclectic.quickstore.activities.salesorder;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.vatsal.imagezoomer.ImageZoomButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.CountryCodeActivity;
import ke.co.eclectic.quickstore.activities.payment.PaymentsActivity;
import ke.co.eclectic.quickstore.adapters.SalesItemsAdapter;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Country;
import ke.co.eclectic.quickstore.models.PaymentType;
import ke.co.eclectic.quickstore.models.SalesOrder;
import ke.co.eclectic.quickstore.models.others.SalesItems;
import ke.co.eclectic.quickstore.rest.response.PaymentTypeResponse;
import ke.co.eclectic.quickstore.rest.response.SalesOrderResponse;
import ke.co.eclectic.quickstore.viewModel.PaymentTypeViewModel;
import ke.co.eclectic.quickstore.viewModel.SalesOrderViewModel;
import timber.log.Timber;


/**
 * Purchase order information activity
 */
public class SOrderDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.txtToolbarTitle)
    TextView txtToolbarTitle;
    @BindView(R.id.imgCancelOrder)
    ImageView imgCancelOrder;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;
    @BindView(R.id.orderProductsRV)
    RecyclerView orderProductsRV;
    @BindView(R.id.btnEdit)
    Button btnEdit;
    @BindView(R.id.btnConfirm)
    Button btnConfirm;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    @BindView(R.id.txtCreatedBy)
    TextView txtCreatedBy;
    @BindView(R.id.txtSupplierName)
    TextView txtSupplierName;
    @BindView(R.id.txtLastUpdate)
    TextView txtLastUpdate;
    @BindView(R.id.txtCreatedDate)
    TextView txtCreatedDate;
    @BindView(R.id.txtOrderId)
    TextView txtOrderId;
    @BindView(R.id.txtStoreName)
    TextView txtStoreName;
    @BindView(R.id.txtTotalPrice)
    TextView txtTotalPrice;
    @BindView(R.id.txtTotalQty)
    TextView txtTotalQty;
    @BindView(R.id.txtApprovedBy)
    TextView txtApprovedBy;
    @BindView(R.id.txtStatus)
    TextView txtStatus;
    @BindView(R.id.txtTotalDiscount)
    TextView txtTotalDiscount;
    @BindView(R.id.approvedLay)
    LinearLayout approvedLay;
    @BindView(R.id.statusLay)
    LinearLayout statusLay;
    @BindView(R.id.layBtn)
    LinearLayout layBtn;
    @BindView(R.id.imgPicPreview1)
    ImageZoomButton imgPicPreview1;


    private static final int PAYMENT_REQUEST_CODE = 10;
    private SalesOrder salesorder;
    private List<SalesItems> salesItemsList = new ArrayList<>();
    private SalesOrderViewModel mSalesOrderViewModel;
    public HashMap<String, Integer> paymentTypeH = new HashMap<>();
    private String paymentMethod = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorder_details);
        ButterKnife.bind(this);
        //  init();
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            salesorder = new Gson().fromJson(extra.get("salesorder").toString(), SalesOrder.class);
        }

        initToolbar();
        populateViews();

    }

    private void  populateViews(){
        initTextview();
        initButton();
        initRV();
        initLayout();
        initImageView();
    }



    /**
     * initializes imageviews
     */
    private void initImageView() {
        if(!salesorder.getStatusname().contentEquals("pending")){
            imgCancelOrder.setVisibility(View.GONE);
        }
    }

    /**
     * initializes buttons
     */
    private void initButton() {

        btnEdit.setEnabled(false);
        btnCancel.setVisibility(View.GONE);
        if (salesorder.getStatusname().toLowerCase().contentEquals("completed") || salesorder.getStatusname().toLowerCase().contentEquals("cancelled")) {
            btnEdit.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);
        }

        if (salesorder.getStatusname().toLowerCase().contentEquals("approved")) {
            btnEdit.setVisibility(View.GONE);
            btnConfirm.setText("Complete");
        }

        if (!GlobalVariable.getCurrentUser().getCurrentBsRule("salesorders", "cancreate")) {
            btnEdit.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);

        }


    }
    /**
     * handles confirm button clicks
     */
    @OnClick(R.id.btnConfirm)
    public void approveOrder() {
        if (btnConfirm.getText().toString().toLowerCase().contains("complete")) {
            mSalesOrderViewModel.insert(salesorder, "closesales");
        } else {
            HashMap<String,String> data= new HashMap<>();
            data.put("salesorder",new Gson().toJson(salesorder));
            GlobalMethod.goToActivity(this, PaymentsActivity.class,data,PAYMENT_REQUEST_CODE);
          //  showPaymentDialgue();
        }
    }

    /**
     * handles cancel order clicks
     */
    @OnClick(R.id.imgCancelOrder)
    public void showCancelOrderDialogue() {
        //showing cancel dialogue
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Do you want to cancel the order")
                .setContentText("This action is irreversable")
                .setConfirmText("Yes,cancel it!")
                .setConfirmClickListener(sDialog -> {
                    mSalesOrderViewModel.insert(salesorder, "cancelsalesorder");
                    sDialog
                            .setTitleText("Cancelling!")
                            .setContentText("Please wait as we cancel the order")
                            .setConfirmText("OK")
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                })
                .show();

    }

    /**
     * displays a payment method dialogue
     */
    private void showPaymentDialgue() {
        //Initialize the Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//Source of the data in the DIalog
        // CharSequence[] array = {"Mpesa","Card", "Credit","cash","Qr Code"};
        CharSequence[] paymentAr = paymentTypeH.keySet().toArray(new String[0]);
        String title = "Select Payment Method";
        if (paymentTypeH.size() == 0) {
            title = "Please check your internet connection";
        }
        paymentMethod = paymentAr[0].toString();

        // Set the dialog title
        builder.setTitle(title)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(paymentAr, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        paymentMethod = paymentAr[which].toString();
                    }
                })
                .setPositiveButton("Ok", (dialog, id) -> {
                    // User clicked OK, so save the result somewhere
                    // or return them to the component that opened the dialog
                    Timber.v("setPositiveButton " + id);
                    salesorder.setPaymentType(paymentTypeH.get(paymentMethod));

                    if (paymentMethod.toLowerCase().contains("mpesa")) {//mpesa payment
                        showPhoneNumber();
                    } else if (paymentMethod.toLowerCase().contains("cash")) {//cash payment
                        GlobalMethod.showSuccess(this, "Payment received successfully", true);
                        showCashDialogue();
                    } else if (paymentMethod.toLowerCase().contains("qr")) {//qr payment
                      // imgPicPreview1.performClick();
                        mSalesOrderViewModel.insert(salesorder, "approvesalesorder");
                    } else {
                        mSalesOrderViewModel.insert(salesorder, "approvesalesorder");
                    }
                    mSalesOrderViewModel.insert(salesorder, "approvesalesorder");
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
    private void showCashDialogue() {
        Dialog addCateogryDialog = new Dialog(this);
        addCateogryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addCateogryDialog.setContentView(R.layout.dialogue_paycash);
        addCateogryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtTitle = addCateogryDialog.findViewById(R.id.txtTitle);
        Button btnSave = addCateogryDialog.findViewById(R.id.btnSave);
        Button btnCancel = addCateogryDialog.findViewById(R.id.btnCancel);
        EditText etxtReceivedAmount = addCateogryDialog.findViewById(R.id.etxtReceivedAmount);
        TextView txtBalanceAmount = addCateogryDialog.findViewById(R.id.txtBalanceAmount);
        TextView txtTotalCash = addCateogryDialog.findViewById(R.id.txtTotalCash);
        etxtReceivedAmount.setHint(salesorder.getTotalcostStr());
        txtTotalCash.setText(salesorder.getTotalcostStr());
        RxTextView.textChanges(etxtReceivedAmount)
                .skip(1).map(CharSequence::toString).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        Timber.v(s);
                        if (s.isEmpty()) {
                            s = salesorder.getTotalcost().toString();
                            etxtReceivedAmount.setHint(salesorder.getTotalcostStr());
                        }

                        salesorder.setPaidamount(Double.parseDouble(s));
                        salesorder.setBalance(Double.parseDouble(s) - salesorder.getTotalcost());

                        if (salesorder.getBalance() < 0) {
                            txtBalanceAmount.setTextColor( ContextCompat.getColor(SOrderDetailsActivity.this,R.color.errorColor));
                        } else {
                            txtBalanceAmount.setTextColor(ContextCompat.getColor(SOrderDetailsActivity.this,R.color.successColor));
                        }

                        txtBalanceAmount.setText(salesorder.getBalanceStr());
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
            if (salesorder.getBalance() < 0) {
                etxtReceivedAmount.setError("Product sold in low price");
                CodingMsg.tle(this, "Product sold in low price");
                return;
            }
            mSalesOrderViewModel.insert(salesorder, "approvesalesorder");
            addCateogryDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            addCateogryDialog.dismiss();
        });

        addCateogryDialog.setCancelable(true);
        addCateogryDialog.show();

    }

    /**
     * shows phone number dialogue
     */
    @SuppressLint("ClickableViewAccessibility")
    private void showPhoneNumber() {
        Dialog addCateogryDialog = new Dialog(this);
        addCateogryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addCateogryDialog.setContentView(R.layout.dialogue_addphone);
        addCateogryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtTitle = addCateogryDialog.findViewById(R.id.txtTitle);
        TextInputLayout tilName = addCateogryDialog.findViewById(R.id.tilName);
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
            if (etxtPhone.getText().toString().trim().isEmpty()) {
                etxtPhone.setError("Enter Phone Number");
                return;
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("payment_type", "mpesa");
            data.put("amount", salesorder.getTotalcost());
            data.put("phone", etxtPhone.getText().toString());
            mSalesOrderViewModel.sendPaymentRequest(data);
            mSalesOrderViewModel.insert(salesorder, "approvesalesorder");
            addCateogryDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            addCateogryDialog.dismiss();
        });

        addCateogryDialog.setCancelable(true);
        addCateogryDialog.show();

    }

    /**
     * initializes layout
     */
    private void initLayout() {
        if (salesorder.getStatusname().toLowerCase().contentEquals("completed") || salesorder.getStatusname().toLowerCase().contentEquals("approved")) {
            approvedLay.setVisibility(View.VISIBLE);
        }
        statusLay.setBackground(salesorder.getStatusBgColor(this));
    }

    /**
     * handles resume  state fo activity
     */
    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    /**
     * initialiazes textviews
     */
    private void initTextview() {
        txtStoreName.setText(salesorder.getStorename());
        txtCreatedBy.setText(salesorder.getStaffname());
        txtSupplierName.setText(salesorder.getCustomername());
        txtLastUpdate.setText(GlobalMethod.getFormatedDateStr(salesorder.getLastedit()));
        txtCreatedDate.setText(GlobalMethod.getFormatedDateStr(salesorder.getCreatedon()));
        txtOrderId.setText("Order Id - ".concat(salesorder.getOrderid()));
        txtTotalPrice.setText(salesorder.getTotalcostStr());
        txtStatus.setText(salesorder.getStatusname());
        txtApprovedBy.setText(salesorder.getStaffname());
    }

    /**
     * handles cancel button clicks
     */
    @OnClick(R.id.btnCancel)
    public void cancelOrder() {
        finish();
    }

    /**
     * handles edit button clicks
     */
    @OnClick(R.id.btnEdit)
    public void editOrder() {
        if (!btnEdit.isEnabled()) {
            CodingMsg.tlw(this, "Please check your internet connection");
            return;
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("salesorder", new Gson().toJson(salesorder));
        data.put("page", "1");
        GlobalMethod.goToActivity(this, AddSalesOrderActivity.class, data);
    }

    /**
     * initializes sales items display
     */
    private void initRV() {
        //dummyData();
        Timber.v("salesItemsList  " + salesItemsList.size());
        SalesItemsAdapter salesItemsAdapter = new SalesItemsAdapter(this, salesItemsList);
        orderProductsRV.setLayoutManager(new LinearLayoutManager(this));
        orderProductsRV.setAdapter(salesItemsAdapter);
    }

    /**
     * initializes sales order data
     */
    private void initData() {
        mSalesOrderViewModel = ViewModelProviders.of(this).get(SalesOrderViewModel.class);
        PaymentTypeViewModel mPaymentTypeViewModel = ViewModelProviders.of(this).get(PaymentTypeViewModel.class);

        mPaymentTypeViewModel.getAllPaymentTypes().observe(this,paymentTypes -> {
            for (PaymentType pt : paymentTypes) {
                paymentTypeH.put(pt.getName(), pt.getPaymenttypeid());
            }
        });

        mSalesOrderViewModel.getSingleSalesOrder(salesorder).observe(this, salesOrders -> {
            Timber.v("getSingleSalesOrder");
            Timber.v(new Gson().toJson(salesOrders));
        });


        mSalesOrderViewModel.getApiResponse().observe(this, myApiResponses -> {
            Timber.v(myApiResponses.getStatus() + "  " + myApiResponses.getOperation());
            if (myApiResponses.getStatus().contentEquals("success")) {
                if (myApiResponses.getOperation().contains("apisitems")) {
                    SalesOrderResponse salesOrderResponse = (SalesOrderResponse) myApiResponses.getObject();
                    salesItemsList = salesOrderResponse.getSalesOrder().getSalesorder_items();
                    salesorder = salesOrderResponse.getSalesOrder();

                    Double qty = 0.0;
                    Double tDiscount = 0.0;
                    for (int i = 0; i < salesItemsList.size(); i++) {
                        tDiscount += salesItemsList.get(i).getItemdiscount();
                        salesItemsList.get(i).setItemdiscount(salesItemsList.get(i).getItemdiscount() / salesItemsList.get(i).getQuantity());
                        qty += salesItemsList.get(i).getQuantity();
                    }

                    salesorder.setSalesorder_items(salesItemsList);

                    txtTotalDiscount.setText("Ksh ".concat(tDiscount.toString()));
                    txtTotalQty.setText("Qty ".concat(((Integer) qty.intValue()).toString()));
                    btnEdit.setEnabled(true);
                    initRV();
                    populateViews();
                }
                if (myApiResponses.getOperation().contains("apisave")) {

                    SalesOrderResponse salesOrderResponse = (SalesOrderResponse) myApiResponses.getObject();
                    CodingMsg.tls(this, salesOrderResponse.getMessage());
                    GlobalMethod.showSuccess(this, salesOrderResponse.getMessage(), true);
                    Completable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                            .subscribe(this::finish);
                }
            } else {
                if (null != myApiResponses.getError()) {
                    GlobalMethod.parseCommError(this, myApiResponses.getError(), rootView);
                }
            }
        });

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
     * initializes toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Order Details");
        // making all textview use monteserrat font
//        for(int i = 0; i < toolBar.getChildCount(); i++)
//        {
//            View view = toolBar.getChildAt(i);
//            if(view instanceof TextView) {
//                TextView textView = (TextView) view;
//                textView.setTypeface(GlobalVariable.getMontserratMedium(this));
//            }
//        }

    }


    /**
     * handles menu item clicks
     * @param item single menu item
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


        }


    }




}
