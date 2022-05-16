package ke.co.eclectic.quickstore.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxAdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.creditors.CreditorsActivity;
import ke.co.eclectic.quickstore.activities.customer.CustomerActivity;
import ke.co.eclectic.quickstore.activities.inventory.InventoryActivity;
import ke.co.eclectic.quickstore.activities.payment.PaymentsActivity;
import ke.co.eclectic.quickstore.activities.pos.PosActivity;
import ke.co.eclectic.quickstore.activities.products.AllProductActivity;
import ke.co.eclectic.quickstore.activities.purchaseOrder.PurchaseOrderActivity;
import ke.co.eclectic.quickstore.activities.reports.SupplierReportActivity;
import ke.co.eclectic.quickstore.activities.salesorder.SalesOrderActivity;
import ke.co.eclectic.quickstore.activities.stores.AddStoreActivity;
import ke.co.eclectic.quickstore.activities.stores.AllStoresActivity;
import ke.co.eclectic.quickstore.activities.supplier.SupplierActivity;
import ke.co.eclectic.quickstore.adapters.MenuAdapter;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.AppMenu;
import ke.co.eclectic.quickstore.models.Business;
import ke.co.eclectic.quickstore.models.Country;
import ke.co.eclectic.quickstore.models.Roles;
import ke.co.eclectic.quickstore.models.Store;
import ke.co.eclectic.quickstore.viewModel.UserViewModel;
import timber.log.Timber;

/**
 * The type Menu activity.
 */
public class MenuActivity extends AppCompatActivity implements  MenuAdapter.MenuComm{
    private static final int STORE_REQUEST_CODE = 233;
    /**
     * The Menu rv.
     */
    @BindView(R.id.menuRV)
    RecyclerView menuRV;
    /**
     * The Txt username.
     */
    @BindView(R.id.txtUsername)
    TextView txtUsername;
    @BindView(R.id.spinnerBusiness)
    Spinner spinnerBusiness;
    private CompositeDisposable disposable = new CompositeDisposable();
    HashMap<String,Business>  bsH = new HashMap<>();
    private String seleMenu="";
    UserViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        ButterKnife.bind(this);

        GlobalVariable.getCurrentUser().getBusinessid(this);
        initTextview();
        initRV();
        initSpinner();
        initData();

        //Todo remove this code
       // HashMap<String,String>  data = new HashMap<>();
       // data.put("staffData",new Gson().toJson(GlobalVariable.getCurrentUser()));
        //GlobalMethod.goToActivity(this,UserProfileActivity.class,data);
        //GlobalMethod.goToActivity(this, CreditorsActivity.class);
        //end of code
    }

    private void initData() {

    }

    /**
     * handles resume state of the activity
     */
    @Override
    protected void onResume() {
        super.onResume();
        GlobalVariable.getCurrentUser().refreshUserInfo(this);
    }


    /**
     * initializes spinner
     */
    private void initSpinner() {
        disposable.add(
                RxAdapterView.itemSelections(spinnerBusiness)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(integer -> {
                            try {
                                TextView t = spinnerBusiness.getSelectedView().findViewById(R.id.text1);
                                t.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, null));
                                t.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                                String sBs = spinnerBusiness.getAdapter().getItem(integer).toString();

                                if(!sBs.contentEquals("Choose Business")){
                                    GlobalVariable.getCurrentUser().setBusinessid( bsH.get(sBs.trim()).getBusinessid());
                                    GlobalVariable.getCurrentUser().setBusinessname( bsH.get(sBs.trim()).getbName());
                                }

                            }catch (Exception e){
                                Timber.v("Exception spinner business"+e.getMessage());
                            }
                        }));

        List<String> locationList =new ArrayList<>() ;
        locationList.add("Choose Business");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  R.layout.spinner_dropdown_item_white, locationList);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item22);
        spinnerBusiness.setAdapter(adapter);
        //Apply the adapter to the spinner
        bsH =  GlobalVariable.getCurrentUser().getBusinessH(this);
        locationList.clear();
       if(bsH.size() == 1){
           locationList.add(GlobalVariable.getCurrentUser().getBusinessname());
            Timber.v("locationList"+GlobalVariable.getCurrentUser().getBusinessname());
       }else{
           Timber.v("locationList 22");
           locationList.add("Choose Business");
           int i =0;
           for(Map.Entry<String,Business> entry: bsH.entrySet()){
               locationList.add(entry.getKey());
               if(locationList.get(i).contentEquals(GlobalVariable.getCurrentUser().getBusinessname())){
                   spinnerBusiness.setSelection(i);
               }
               i++;
           }
       }
        adapter.notifyDataSetChanged();



    }

    /**
     * initializes menu item data
     */
    private void initRV(){

        List<AppMenu> countryList = new ArrayList<>();
        if(GlobalVariable.getCurrentUser().getCurrentBsRule("products","canview")) {
            countryList.add(new AppMenu("Product", R.drawable.product_img));
        }
        if(GlobalVariable.getCurrentUser().getCurrentBsRule("inventory","canview")) {
            countryList.add(new AppMenu("Inventory", R.drawable.inventory));
        }
       // countryList.add("Commission");
        if(GlobalVariable.getCurrentUser().getCurrentBsRule("salesorders","canview")){
            countryList.add(new AppMenu("Sales Order", R.drawable.sales));
        }
        if(GlobalVariable.getCurrentUser().getCurrentBsRule("suppliers","canview")) {
            countryList.add(new AppMenu("Supplier", R.drawable.supplier));
        }
        if(GlobalVariable.getCurrentUser().getCurrentBsRule("customers","canview")) {
            countryList.add(new AppMenu("customer", R.drawable.supplier));
        }
        if(GlobalVariable.getCurrentUser().getCurrentBsRule("pos","canview")) {
            countryList.add(new AppMenu("POS", R.drawable.pos));
        }
        if(GlobalVariable.getCurrentUser().getCurrentBsRule("stores","canview")) {
            countryList.add(new AppMenu("Store", R.drawable.stores));
        }
        if(GlobalVariable.getCurrentUser().getCurrentBsRule("staff","canview")) {
            countryList.add(new AppMenu("Employees", R.drawable.staff));
        }
       // if(GlobalVariable.getCurrentUser().getCurrentBsRule("report","canview")) {
            countryList.add(new AppMenu("Reports", R.drawable.reports));
       // }
        if(GlobalVariable.getCurrentUser().getCurrentBsRule("settings","canview")) {
            countryList.add(new AppMenu("Settings", R.drawable.settings));
        }
        if(GlobalVariable.getCurrentUser().getCurrentBsRule("purchaseorder","canview")) {
            countryList.add(new AppMenu("Purchase Order", R.drawable.purchase_order));
        }
        if(GlobalVariable.getCurrentUser().getCurrentBsRule("marketplace","canview")) {
            countryList.add(new AppMenu("Market Place", R.drawable.purchase_order));
        }
        if(GlobalVariable.getCurrentUser().getCurrentBsRule("banking","canview")) {
            countryList.add(new AppMenu("Banking", R.drawable.purchase_order));
        }
            countryList.add(new AppMenu("logout", R.drawable.settings));


        MenuAdapter menuViewAdapter = new MenuAdapter(this, countryList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        menuRV.setLayoutManager(gridLayoutManager);
        menuRV.setAdapter(menuViewAdapter);

    }

    /**
     * initializes textview
     */
    private void initTextview() {
        txtUsername.setTypeface(GlobalVariable.getMontserratMedium(this));
        txtUsername.setText(GlobalVariable.getCurrentUser().getFirstname());
    }

    /**
     *handles update profile layout clicks
     */
    @OnClick(R.id.updateProfileLay)
    public void updateProfile(){
        HashMap<String,String>  data = new HashMap<>();
        data.put("staffData",new Gson().toJson(GlobalVariable.getCurrentUser()));
        GlobalMethod.goToActivity(this,UserProfileActivity.class,data);
    }

    /**
     * implement stringadapter method
     *
     * @param position the position of clicked item
     * @param val      the value of the clicked item
     * @param action   the action to be performed
     */
    @Override
    public void strMessage(int position, String val, String action) {
        HashMap<String,Store>  bsStoreH =  GlobalVariable.getCurrentUser().getBusinessStoresH();
        Timber.v("bsStoreH "+bsStoreH.size());
        seleMenu = val.toLowerCase();
            if(seleMenu.contains("employees")){
                GlobalMethod.goToActivity(this,EmployeesActivity.class);
            }else
            if(seleMenu.contains("product")){
                GlobalMethod.goToActivity(this,AllProductActivity.class);
            }else
            if(seleMenu.contains("supplier")){
                GlobalMethod.goToActivity(this,SupplierActivity.class);
            }else
            if(seleMenu.contains("customer")){
                GlobalMethod.goToActivity(this,CustomerActivity.class);
            }else
            if(seleMenu.contains("inventory")){
                if(bsStoreH.size() > 1){
                    CodingMsg.tls(this,"choose store");
                    HashMap<String,String> data = new HashMap<>();
                    data.put("action","select");
                    GlobalMethod.goToActivity(this,AllStoresActivity.class,data,STORE_REQUEST_CODE);
                }else {
                    GlobalMethod.goToActivity(this, InventoryActivity.class);
                }
            }else
            if(seleMenu.contains("report")){
                GlobalMethod.goToActivity(this,MainActivity.class);
            }else
            if(seleMenu.contains("store")){
                if(bsStoreH.size()== 0){
                    HashMap<String,String> data = new HashMap<>();
                    Business currentBusiness = new Business();
                    currentBusiness.setBusinessid(GlobalVariable.getCurrentUser().getBusinessid());
                    currentBusiness.setbName(GlobalVariable.getCurrentUser().getBusinessname());
                    data.put("business",new Gson().toJson(currentBusiness));
                    Timber.v(new Gson().toJson(currentBusiness));
                    GlobalMethod.goToActivity(this,AddStoreActivity.class,data);
                }else{
                    GlobalMethod.goToActivity(this,AllStoresActivity.class);
                }
            }else
            if(seleMenu.contains("pos")){
                if(bsStoreH.size() >1){
                    HashMap<String,String> data = new HashMap<>();
                    data.put("action","select");
                    GlobalMethod.goToActivity(this,AllStoresActivity.class,data,STORE_REQUEST_CODE);
                }else {
                    GlobalMethod.goToActivity(this, PosActivity.class);
                }
            }else
            if(seleMenu.contains("purchase")){
                if(bsStoreH.size() >1){
                    HashMap<String,String> data = new HashMap<>();
                    data.put("action","select");
                    GlobalMethod.goToActivity(this,AllStoresActivity.class,data,STORE_REQUEST_CODE);
                }else {
                    GlobalMethod.goToActivity(this, PurchaseOrderActivity.class);
                }
            }
            else
            if(seleMenu.contains("sales order")){
                if(bsStoreH.size() > 1){
                    HashMap<String,String> data = new HashMap<>();
                    data.put("action","select");
                    GlobalMethod.goToActivity(this,AllStoresActivity.class,data,STORE_REQUEST_CODE);
                }else {
                    GlobalMethod.goToActivity(this, SalesOrderActivity.class);
                }
            }
            else
            if(seleMenu.contains("logout")){
                GlobalMethod.logOut(this);
            }else{
                CodingMsg.tl(this,"under development");
                //GlobalMethod.goToActivity(this,TestActivity.class);
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
        Timber.v("onActivityResult"+data.getStringExtra(AllStoresActivity.EXTRA_REPLY));
        if (requestCode == STORE_REQUEST_CODE && resultCode == RESULT_OK) {
            Country country = new Gson().fromJson(data.getStringExtra(AllStoresActivity.EXTRA_REPLY),Country.class);
            Timber.v("onActivityResult "+country.getDialCode());
            if(seleMenu.contains("inventory")){
                GlobalMethod.goToActivity(this, InventoryActivity.class);
            }

            if(seleMenu.contains("purchase")){
                GlobalMethod.goToActivity(this, PurchaseOrderActivity.class);
            }
            if(seleMenu.contains("pos")){
                GlobalMethod.goToActivity(this, PosActivity.class);
            }
            if(seleMenu.contains("sales order")){
                GlobalMethod.goToActivity(this, SalesOrderActivity.class);
            }

        }
    }


}
