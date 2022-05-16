package ke.co.eclectic.quickstore.activities.purchaseOrder;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vatsal.imagezoomer.ImageZoomButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.payment.PaymentsActivity;
import ke.co.eclectic.quickstore.adapters.PurchaseItemsAdapter;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.PaymentType;
import ke.co.eclectic.quickstore.models.PurchaseOrder;
import ke.co.eclectic.quickstore.models.others.PurchaseItems;
import ke.co.eclectic.quickstore.rest.response.PaymentTypeResponse;
import ke.co.eclectic.quickstore.rest.response.PurchaseOrderResponse;
import ke.co.eclectic.quickstore.viewModel.PaymentTypeViewModel;
import ke.co.eclectic.quickstore.viewModel.PurchaseOrderViewModel;
import timber.log.Timber;


/**
 * Purchase order information activity
 *
 */
public class POrderDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.imgCancelOrder)
    ImageView imgCancelOrder;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;
    @BindView(R.id.orderProductsRV)
    RecyclerView orderProductsRV;
    @BindView(R.id.btnEdit)
    Button btnEdit;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    @BindView(R.id.btnConfirm)
    Button btnConfirm;
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
    @BindView(R.id.approvedLay)
    LinearLayout approvedLay;
    @BindView(R.id.statusLay)
    LinearLayout statusLay;
    @BindView(R.id.layBtn)
    LinearLayout layBtn;
    @BindView(R.id.imgPicPreview1)
    ImageZoomButton imgPicPreview1;

    private PurchaseOrder purchaseorder;
    private List<PurchaseItems> purchaseItemsList = new ArrayList<>();
    private PurchaseOrderViewModel mPurchaseOrderViewModel;
    public HashMap<String, Integer> paymentTypeH = new HashMap<>();
    private String paymentMethod="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);
      //  init();
        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            purchaseorder =  new Gson().fromJson(extra.get("purchaseorder").toString(),PurchaseOrder.class);
        }

        initToolbar();
        populateData();

    }
    private void populateData(){
        initImageView();
        initTextview();
        initButton();
        initRV();
        initLayout();
    }

    /**
     * Initializes imageview
     */
    private void initImageView() {
        if(!purchaseorder.getStatus().contentEquals("pending")){
            imgCancelOrder.setVisibility(View.GONE);
        }
    }

    /**
     * handles image cancel  clicks
     */
    @OnClick(R.id.imgCancelOrder)
    public void showCancelOrderDialogue() {
        //shows cancelling dialogue
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Do you want to cancel the order")
                .setContentText("This action is irreversable")
                .setConfirmText("Yes,cancel it!")
                .setConfirmClickListener(sDialog -> {
                    mPurchaseOrderViewModel.insert(purchaseorder, "cancelpurchase");
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
     * initializes button
     */
    private void initButton() {
           // layBtn.setVisibility(View.GONE);
            btnEdit.setEnabled(false);
            btnCancel.setVisibility(View.GONE);
        if(purchaseorder.getStatus().toLowerCase().contentEquals("completed")|| purchaseorder.getStatus().toLowerCase().contentEquals("cancelled")){
            btnEdit.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);
        }

        if(purchaseorder.getStatus().toLowerCase().contentEquals("approved")){
            btnEdit.setVisibility(View.GONE);
            btnConfirm.setText("Complete");
        }

        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("purchaseorder", "cancreate")) {
            btnEdit.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);
        }

    }

    /**
     * initializes layout
     */
    private void initLayout() {
        if(purchaseorder.getStatus().toLowerCase().contentEquals("completed")){
            approvedLay.setVisibility(View.VISIBLE);

        }

        if(purchaseorder.getStatus().toLowerCase().contentEquals("approved")){
            approvedLay.setVisibility(View.VISIBLE);

        }

        statusLay.setBackground(purchaseorder.getStatusBgColor(this));
    }

    /**
     * handles resume state of the activity
     */
    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    /**
     * INitialiazes textview
     */
    private void initTextview(){
        txtStoreName.setText(purchaseorder.getStorename());
        txtCreatedBy.setText(purchaseorder.getRaisedby());
        txtSupplierName.setText(purchaseorder.getSuppliername());
        txtLastUpdate.setText(GlobalMethod.getFormatedDateStr(purchaseorder.getLastedit()));
        txtCreatedDate.setText(GlobalMethod.getFormatedDateStr(purchaseorder.getRaisedon()));
        txtOrderId.setText("Order Id - ".concat(purchaseorder.getOrderid()));
        txtTotalPrice.setText(purchaseorder.getTotalcostStr());
        txtStatus.setText(purchaseorder.getStatus());
        txtApprovedBy.setText(purchaseorder.getRaisedby());
    }

    /**
     * Handles cancel button clicks
     */
    @OnClick(R.id.btnCancel)
    public void cancelOrder(){
            finish();
    }

    /**
     * handles confirm button clicks
     */
    @OnClick(R.id.btnConfirm)
    public void approveOrder(){
        if(btnConfirm.getText().toString().toLowerCase().contains("complete")){
            mPurchaseOrderViewModel.insert(purchaseorder, "closepurchase");
        } else {
            HashMap<String,String> data= new HashMap<>();
            data.put("purchaseorder",new Gson().toJson(purchaseorder));
            GlobalMethod.goToActivity(this, PaymentsActivity.class,data);
           // showPaymentDialgue();
//            purchaseorder.setPaymentType(4);//defaulting to cash payment
//            mPurchaseOrderViewModel.insert(purchaseorder, "approvepurchase");
        }
    }

    /**
     * show payment dialogue
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

                        paymentMethod = paymentAr[which].toString();
                        Timber.v("paymentMethod " +  paymentAr[which].toString());
                    }
                })
                .setPositiveButton("Ok", (dialog, id) -> {
                    // User clicked OK, so save the result somewhere
                    // or return them to the component that opened the dialog
                    purchaseorder.setPaymentType(paymentTypeH.get(paymentMethod));
                    Timber.v("paymentMethod " + paymentMethod);
                    Timber.v("paymentMethod " + paymentTypeH.get(paymentMethod));
                    Timber.v("paymentMethods " +  paymentTypeH.toString());

                    if (paymentMethod.toLowerCase().contains("mpesa")) {//mpesa payment
                        showPhoneNumber();
                    } else if (paymentMethod.toLowerCase().contains("cash")) {//cash payment
                        GlobalMethod.showSuccess(this, "Payment received successfully", true);
                        mPurchaseOrderViewModel.insert(purchaseorder, "approvepurchase");
                    } else if (paymentMethod.toLowerCase().contains("credit")) {//qr payment
                        //imgPicPreview1.performClick();
                        showCreditdialogue();
                    }
                    else {
                        mPurchaseOrderViewModel.insert(purchaseorder, "approvepurchase");
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
     * displays credit dialogue
     */
    @SuppressLint("ClickableViewAccessibility")
    private void showCreditdialogue() {
        Dialog addCateogryDialog = new Dialog(this);
        addCateogryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addCateogryDialog.setContentView(R.layout.dialogue_addcredit);
        addCateogryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtTitle = addCateogryDialog.findViewById(R.id.txtTitle);
        TextInputLayout tilName = addCateogryDialog.findViewById(R.id.tilName);
        Button btnSave = addCateogryDialog.findViewById(R.id.btnSave);
        Button btnCancel = addCateogryDialog.findViewById(R.id.btnCancel);
        EditText etxtCreditAmount = addCateogryDialog.findViewById(R.id.etxtCreditAmount);
        TextView txtInfo = addCateogryDialog.findViewById(R.id.txtInfo);
        TextView txtTotalPAmount = addCateogryDialog.findViewById(R.id.txtTotalPAmount);
        txtTitle.setText("Enter  Credit Amount");
        txtTotalPAmount.setText("Total ".concat(purchaseorder.getTotalcostStr()));
        btnSave.setText("Send Request");
        txtInfo.setText(Html.fromHtml("<b>Note:</b> You can request for credit less than total cost of the purchase order"));

        etxtCreditAmount.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });

        btnSave.setOnClickListener(v -> {
            String name = "";
            if (etxtCreditAmount.getText().toString().trim().isEmpty()) {
                etxtCreditAmount.setError("Enter credit amount");
                return;
            }

            if(Double.parseDouble(etxtCreditAmount.getText().toString()) > purchaseorder.getTotalcost()){
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("You can request for credit less than total cost of the purchase order")
                        .show();

                return;
            }


            mPurchaseOrderViewModel.insert(purchaseorder, "approvepurchase");
            addCateogryDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            addCateogryDialog.dismiss();
        });

        addCateogryDialog.setCancelable(true);
        addCateogryDialog.show();
    }


    /**
     * displays phone number dialogue
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
            data.put("amount", purchaseorder.getTotalcost());
            data.put("phone", etxtPhone.getText().toString());
            mPurchaseOrderViewModel.sendPaymentRequest(data);
            mPurchaseOrderViewModel.insert(purchaseorder, "approvepurchase");
            addCateogryDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            addCateogryDialog.dismiss();
        });

        addCateogryDialog.setCancelable(true);
        addCateogryDialog.show();

    }

    /**
     * handles edit button clicks
     */
    @OnClick(R.id.btnEdit)
    public void editOrder(){
        if(!btnEdit.isEnabled()){
            CodingMsg.tlw(this,"Please check your internet connection");
            return;
        }
            HashMap<String, String> data = new HashMap<>();
            data.put("purchaseorder", new Gson().toJson(purchaseorder));
            data.put("page", "1");
            GlobalMethod.goToActivity(this, AddPurchaseOrderActivity.class, data);
    }

    /**
     * initializes recyclerview
     */
    private void initRV(){
        //dummyData();
        Timber.v("purchaseItemsList  "+purchaseItemsList.size());
        PurchaseItemsAdapter purchaseItemsAdapter = new PurchaseItemsAdapter(this, purchaseItemsList);
        orderProductsRV.setLayoutManager(new LinearLayoutManager(this));
        orderProductsRV.setAdapter(purchaseItemsAdapter);
    }


    /**
     * initializes data used in activity
     */
    private void initData() {
        mPurchaseOrderViewModel = ViewModelProviders.of(this).get(PurchaseOrderViewModel.class);
        PaymentTypeViewModel mPaymentTypeViewModel = ViewModelProviders.of(this).get(PaymentTypeViewModel.class);
        mPurchaseOrderViewModel.getSinglePurchaseOrder(purchaseorder);

        mPurchaseOrderViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            if(myApiResponses.getStatus().contentEquals("success")){
                if(myApiResponses.getOperation().contains("apipitems")) {
                    PurchaseOrderResponse purchaseOrderResponse = (PurchaseOrderResponse) myApiResponses.getObject();
                   purchaseItemsList = purchaseOrderResponse.getPurchaseOrder().getPurchase_items();
//                    purchaseorder.setPurchase_items(purchaseItemsList);

                    purchaseorder = purchaseOrderResponse.getPurchaseOrder();
                    Double qty = 0.0;
                    for(PurchaseItems pi : purchaseItemsList){
                        qty += pi.getQuantity();
                    }
                    txtTotalQty.setText("Qty ".concat(qty.toString()));
                    btnEdit.setEnabled(true);
                    initRV();
                    populateData();

                }
                if(myApiResponses.getOperation().contains("apisave")) {
                    PurchaseOrderResponse purchaseOrderResponse = (PurchaseOrderResponse) myApiResponses.getObject();
                    CodingMsg.tls(this,purchaseOrderResponse.getMessage());
                    finish();
                }

            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(this,myApiResponses.getError(),rootView);
                }
            }
        });

        mPaymentTypeViewModel.getAllPaymentTypes().observe(this,paymentTypes -> {
            for (PaymentType pt : paymentTypes) {
                paymentTypeH.put(pt.getName(), pt.getPaymenttypeid());
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
     * initializes toolbar menu layout
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_menu, menu);
        MenuItem cancelItem = menu.findItem(R.id.action_cancel);

        return true;

    }


    /**
     * handles menu item click events
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
                Intent replyIntent = new Intent();
                setResult(RESULT_CANCELED, replyIntent);
                finish();
                return true;
            }

            case R.id.action_cancel: {

                mPurchaseOrderViewModel.insert(purchaseorder, "cancelpurchase");
                return true;
            }

            default:{
                return super.onOptionsItemSelected(item);
            }

        }
    }


}
