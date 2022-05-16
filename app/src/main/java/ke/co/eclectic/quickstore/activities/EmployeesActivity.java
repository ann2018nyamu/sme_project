package ke.co.eclectic.quickstore.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

//import com.github.mikephil.charting.matrix.Vector3;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.jakewharton.rxbinding2.widget.RxAdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.inventory.AddInventoryActivity;
import ke.co.eclectic.quickstore.adapters.EmployeeAdapter;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.BusinessRoles;
import ke.co.eclectic.quickstore.models.Country;
import ke.co.eclectic.quickstore.models.User;
import ke.co.eclectic.quickstore.models.Store;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.BusinessResponse;
import ke.co.eclectic.quickstore.rest.response.BusinessRolesResponse;
import ke.co.eclectic.quickstore.rest.response.InventoryStockResponse;
import ke.co.eclectic.quickstore.rest.response.StoreResponse;
import ke.co.eclectic.quickstore.rest.response.UserResponse;
import ke.co.eclectic.quickstore.viewModel.BusinessViewModel;
import ke.co.eclectic.quickstore.viewModel.StoreViewModel;
import ke.co.eclectic.quickstore.viewModel.UserViewModel;
import timber.log.Timber;

public class EmployeesActivity extends AppCompatActivity  {
    private static final int COUNTRY_REQUEST_CODE = 10;

    @BindView(R.id.staffRV)
    RecyclerView staffRV;
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.txtInfo)
     TextView txtInfo;
    @BindView(R.id.txtMessage)
     TextView txtMessage;
    @BindView(R.id.fab)
     FloatingActionButton fab;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;

    List<String> roleList =new ArrayList<>();
    HashMap<String,BusinessRoles> bRoleListH =new HashMap<>();
    List<String> storeList =new ArrayList<>() ;
    HashMap<String,Store> storeListH =new HashMap<>();
    List<User> staffList = new ArrayList<>();
    List<User> staffListCopy = new ArrayList<>();
    private SearchView searchView;


    private EmployeeAdapter employeeAdapter;
    private CompositeDisposable disposable = new CompositeDisposable();
    UserViewModel   mUserViewModel;
    BusinessViewModel   mBusinessViewModel;
    private StoreViewModel mStoreViewModel;
    private ArrayAdapter<String> roleListSpadapter ;
    private ArrayAdapter<String> storeListSpAdapter ;
    EditText etxtcountryCode;
    private Dialog addStaffDialog;
    private String assignedStore="Assign Store";
    private String assignedRole="Assign Role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);
        ButterKnife.bind(this);
        //  init();
        initToolbar();
        initTextView();
        initFab();
        initRV();
        initDbData();
        initKeyboardListener();

    }

    /**
     * handles keyboard visibility event
     */
    private void initKeyboardListener() {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getRootView().getHeight();

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                // keyboard is opened
                fab.hide();
            }else {
                // keyboard is closed
                if(GlobalVariable.getCurrentUser().getCurrentBsRule("staff", "cancreate")) {
                    fab.show();
                }
            }
        });
    }

    /**
     * initializes data used by activity
     */
    private void initDbData() {
        roleListSpadapter = new ArrayAdapter<String>(this,  R.layout.spinner_dropdown_item, roleList);
        storeListSpAdapter = new ArrayAdapter<String>(this,  R.layout.spinner_dropdown_item, storeList);
        mStoreViewModel = ViewModelProviders.of(this).get(StoreViewModel.class);
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mBusinessViewModel = ViewModelProviders.of(this).get(BusinessViewModel.class);
       // fetchBusinessRoles();
       // fetchBusinessStores();
//        fetchBusinessStaff();
        //getting business roles
        if(GlobalVariable.getCurrentUser().getCurrentBsRule("staff", "cancreate")) {
            mBusinessViewModel.getBusinessRole();
        }
        mBusinessViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            GlobalMethod.stoploader(this);
            if(myApiResponses.getStatus().contentEquals("success")){

                if(myApiResponses.getOperation().contains("apirefreshbusinessrole")) {
                    BusinessResponse businessResponse = (BusinessResponse) myApiResponses.getObject();
                    roleList.clear();
                    roleListSpadapter.clear();
                    roleList.add(0,"Assign Role");

                    for(BusinessRoles businessRoles: businessResponse.getBusinessRole()){
                        bRoleListH.put(businessRoles.getName(),businessRoles);
                        // businessRoleViewModel.insert(businessRoles);
                        roleList.add(businessRoles.getName());
                    }
//                    for (Map.Entry<String, BusinessRoles> entry : bRoleListH.entrySet()) {
//                        roleList.add(entry.getValue().getName());
//                    }

                    roleListSpadapter.notifyDataSetChanged();
                }
            } else {
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(this,(Throwable) myApiResponses.getError(),rootView);
                }
            }
        });
        //fetching business stores

        mStoreViewModel.getAllStore();
        mStoreViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            GlobalMethod.stoploader(this);
            if(myApiResponses.getStatus().contentEquals("success")){

                if(myApiResponses.getOperation().contains("apirefresh")) {
                    StoreResponse storeResponse = (StoreResponse) myApiResponses.getObject();
                    storeList.clear();
                    storeListSpAdapter.clear();
                    storeList.add(0,"Assign Store");
                    for(Store store: storeResponse.getData()){
                        storeListH.put(store.getStorename(),store);
                        storeList.add(store.getStorename());
                    }

                    storeListSpAdapter.notifyDataSetChanged();

                }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(this,(Throwable) myApiResponses.getError(),rootView);
                }
            }
        });
        //getting staff
        mUserViewModel.getBusinessStaff();
        mUserViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            GlobalMethod.stoploader(this);
            if(myApiResponses.getStatus().contentEquals("success")){

                if(myApiResponses.getOperation().contains("apistaffrefresh")) {
                    UserResponse userResponse = (UserResponse) myApiResponses.getObject();
                    staffList.clear();
                    staffList.addAll(userResponse.getStaffListData());
                    staffListCopy = staffList;

                    employeeAdapter.refresh(staffList);
                    txtMessage.setVisibility(View.GONE);
                    if(staffList.size() == 0){
                        txtMessage.setVisibility(View.VISIBLE);
                        txtMessage.setText("Add Employees");
                    }
                }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(this,(Throwable) myApiResponses.getError(),rootView);
                }
            }
        });

    }

    /**
     * fetches business staff  records
     */
    private void fetchBusinessStaff(){
        //Todo transfer code to its(user) repository
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","getstaffbybusiness");
        data.put("businessid",GlobalVariable.getCurrentUser().getBusinessid(this) );
        // sending request to api
        disposable.add(
                ApiClient.getApi().api(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>(){
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                if(userResponse.getStatus().contentEquals("success")){
                                    staffList.clear();
                                    staffList.addAll(userResponse.getStaffListData());
                                    staffListCopy = staffList;

                                    employeeAdapter.refresh(staffList);
                                    txtMessage.setVisibility(View.GONE);
                                    if(staffList.size() == 0){
                                        txtMessage.setVisibility(View.VISIBLE);
                                        txtMessage.setText("Add Employees");
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                GlobalMethod.parseCommError(EmployeesActivity.this, e, rootView);
                                //CodingMsg.t(this,e.getMessage());
                            }
                        })
        );
    }

    /**
     * fetches business roles from repository
     */
    private void fetchBusinessRoles(){
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","getroles");
        data.put("businessid",GlobalVariable.getCurrentUser().getBusinessid(this));
        // sending request to api
        disposable.add(
                ApiClient.getApi().getBusinessRoles(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<BusinessRolesResponse>(){
                            @Override
                            public void onSuccess(BusinessRolesResponse businessRolesResponse) {
                                if(businessRolesResponse.getStatus().contentEquals("success")){
                                    for(BusinessRoles businessRoles: businessRolesResponse.getData()){
                                        bRoleListH.put(businessRoles.getName(),businessRoles);
                                       // businessRoleViewModel.insert(businessRoles);
                                    }
                                    
                                    roleList.clear();
                                    roleListSpadapter.clear();
                                    roleList.add(0,"Assign Role");
                                    for (Map.Entry<String, BusinessRoles> entry : bRoleListH.entrySet()) {
                                        roleList.add(entry.getValue().getName());
                                    }

                                    roleListSpadapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                GlobalMethod.parseCommError(EmployeesActivity.this, e, rootView);
                                //CodingMsg.t(this,e.getMessage());
                            }
                        })
        );
    }

    /**
     * fetches business store from repository
     */
    private void fetchBusinessStores(){

        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","getstores");
        data.put("businessid",GlobalVariable.getCurrentUser().getBusinessid(this));
        // sending request to api
        disposable.add(
                ApiClient.getApi().storeApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<StoreResponse>(){
                            @Override
                            public void onSuccess(StoreResponse storeResponse) {
                                if(storeResponse.getStatus().contentEquals("success")){
                                    //CodingMsg.t(EmployeesActivity.this,storeResponse.getMessage());
//                                    for(Store store: storeResponse.getData()){
//                                        storeListH.put(store.getStorename(),store);
//                                        mStoreViewModel.insert(store);
//                                    }
                                    storeList.clear();
                                    storeListSpAdapter.clear();
                                    storeList.add(0,"Assign Store");
                                    for (Map.Entry<String, Store> entry : storeListH.entrySet()) {
                                        storeList.add(entry.getValue().getStorename());
                                    }
                                    storeListSpAdapter.notifyDataSetChanged();
                                            
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                 GlobalMethod.parseCommError(EmployeesActivity.this, e, rootView);
                                //CodingMsg.t(this,e.getMessage());
                            }
                        })
        );
    }


    /**
     * handles floating button click event
     */
    private void initFab() {
        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("staff", "cancreate")) {
            fab.hide();
            return;
        }
        fab.setOnClickListener(view -> showAddStaffDialogue());
    }


    /**
     * shows add staff dialogue
     */
    @SuppressLint("ClickableViewAccessibility")
    private void showAddStaffDialogue(){

        addStaffDialog = new Dialog(this);
        addStaffDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addStaffDialog.setContentView(R.layout.dialogue_staffadd_layout);
        addStaffDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtTitle =  addStaffDialog.findViewById(R.id.txtTitle);
        Button btnInvite =  addStaffDialog.findViewById(R.id.btnInvite);
        Button btnCancel =  addStaffDialog.findViewById(R.id.btnCancel);
        EditText etxtPhoneNumber =  addStaffDialog.findViewById(R.id.etxtPhoneNumber);

        EditText etxtUsername =  addStaffDialog.findViewById(R.id.etxtUsername);
        etxtcountryCode =  addStaffDialog.findViewById(R.id.etxtcountryCode);
        Spinner spinnerAssignedRole =  addStaffDialog.findViewById(R.id.spinnerAssignedRole);
        Spinner spinnerAssignedStore =  addStaffDialog.findViewById(R.id.spinnerAssignedStore);
        txtTitle.setText(R.string.new_staff);
        txtTitle.setTypeface(GlobalVariable.getMontserratMedium(this));
        etxtPhoneNumber.setTypeface(GlobalVariable.getMontserratRegular(this));
        etxtUsername.setTypeface(GlobalVariable.getMontserratRegular(this));
        btnInvite.setTypeface(GlobalVariable.getMontserratSemiBold(this));
        btnCancel.setTypeface(GlobalVariable.getMontserratSemiBold(this));

        etxtcountryCode.setOnClickListener(v -> {
            HashMap<String,String> data = new HashMap<>();
            data.put("action","showcode");
            GlobalMethod.goToActivity(this, CountryCodeActivity.class,data,COUNTRY_REQUEST_CODE);
        });


        etxtUsername.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });

        etxtPhoneNumber.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });


        //setting spinner

        // Specify the layout to use when the list of choices appears
        storeListSpAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item2);

        spinnerAssignedStore.setAdapter(storeListSpAdapter);

        // Specify the layout to use when the list of choices appears
        roleListSpadapter.setDropDownViewResource(R.layout.spinner_dropdown_item2);

        spinnerAssignedRole.setAdapter(roleListSpadapter);


        disposable.add(
                RxAdapterView.itemSelections(spinnerAssignedStore)
                        .skip(1)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(integer -> {
                            CheckedTextView t =  spinnerAssignedStore.getSelectedView().findViewById(R.id.text1);
                            t.setTypeface(GlobalVariable.getMontserratRegular(this));


                            if(integer == 0){
                                t.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextPlaceholderColor, null));
                            }else{
                                t.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextColor, null));
                            }
                           assignedStore = t.getText().toString();
                        }));
        disposable.add(
                RxAdapterView.itemSelections(spinnerAssignedRole)
                        .skip(1)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(integer -> {
                            CheckedTextView t =  spinnerAssignedRole.getSelectedView().findViewById(R.id.text1);
                            t.setTypeface(GlobalVariable.getMontserratRegular(this));

                            if(integer == 0){
                                t.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextPlaceholderColor, null));
                            }else{
                                t.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextColor, null));
                            }
                            assignedRole = t.getText().toString();
                        }));



        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validating info
                if(etxtUsername.getText().toString().trim().length() < 1){
                    etxtUsername.setError("Enter the name");
                    return;
                }
                if(etxtPhoneNumber.getText().toString().isEmpty()){
                    etxtPhoneNumber.setError("Enter the mobile number");
                    return;
                }

                if(assignedStore.contentEquals("Assign Store")){
                    CodingMsg.tle(EmployeesActivity.this,"Assign store to user");
                    return ;
                }

                if(assignedRole.contentEquals("Assign Role")){
                    CodingMsg.tle(EmployeesActivity.this,"Assign role to user");
                    return ;
                }

                if(etxtcountryCode.getText().toString().isEmpty()){
                    CodingMsg.tle(EmployeesActivity.this,"Choose country code");
                    return ;
                }

                HashMap<String,Object> data = new HashMap<>();
                data.put("phonenumber",etxtcountryCode.getText().toString().concat( etxtPhoneNumber.getText().toString()));
                data.put("name", etxtUsername.getText().toString());
                data.put("storeid",storeListH.get(assignedStore).getStoreid());
                data.put("roleid",bRoleListH.get(assignedRole).getRoleid());
                data.put("request_type","addstaff");

                inviteStaff(data);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStaffDialog.dismiss();
            }
        });

        addStaffDialog.setCancelable(false);
        addStaffDialog.show();
    }

    /**
     * send staff invite request
     * @param data  staff data
     */
    private void inviteStaff(HashMap<String,Object>  data) {
        //todo trasfer code to its repository(userrepository)
        GlobalMethod.showloader(this,"Sending invite...",true);
        disposable.add(
                ApiClient.getApi().api(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>(){
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                GlobalMethod.stoploader(EmployeesActivity.this);
                                if(userResponse.getStatus().contentEquals("success")){
                                    CodingMsg.tls(EmployeesActivity.this,userResponse.getMessage());
                                }
                                addStaffDialog.dismiss();

                                mUserViewModel.getBusinessStaff();
                            }
                            @Override
                            public void onError(Throwable e) {
                                addStaffDialog.dismiss();
                                GlobalMethod.parseCommError(EmployeesActivity.this, e, rootView);
                                //CodingMsg.t(this,e.getMessage());
                            }
                        })
        );

    }

    /**
     * initializes textview
     */
    private void initTextView() {
        txtInfo.setTypeface(GlobalVariable.getMontserratMedium(this));
        txtInfo.setText(GlobalVariable.getCurrentUser().getBusinessname() );
    }

    /**
     * initialized toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Employees");
        // making all textview use monteserrat font
        for(int i = 0; i < toolBar.getChildCount(); i++)
        {
            View view = toolBar.getChildAt(i);
            if(view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setTypeface(GlobalVariable.getMontserratMedium(this));
            }
        }
    }

    /**
     * initializes stafflist display
     */
    private void initRV(){
        employeeAdapter = new EmployeeAdapter(this,staffList);
        staffRV.setLayoutManager(new LinearLayoutManager(this));
        staffRV.setAdapter(employeeAdapter);

    }

    /**
     * initializes toolbar menu
     * @param menu toolbar menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
            searchView.setIconified(true);

            // Getting selected (clicked) item suggestion
            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionClick(int position) {
                    // Add clicked text to search box
                    CursorAdapter ca = searchView.getSuggestionsAdapter();
                    Cursor cursor = ca.getCursor();
                    cursor.moveToPosition(position);
                    searchView.setQuery(cursor.getString(cursor.getColumnIndex("UserName")), false);
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onSuggestionSelect(int position) {
                    return true;
                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    fab.hide();
                    List<User> filterListCopy = new ArrayList<>();
                    // Filter data
                    for (int i = 0; i < staffListCopy.size(); i++) {
                        if (staffListCopy.get(i).getStorename().toLowerCase().contains(s.toLowerCase().trim()) ||
                                staffListCopy.get(i).getOthernames().toLowerCase().contains(s.toLowerCase().trim()) ||
                                staffListCopy.get(i).getFirstname().toLowerCase().contains(s.toLowerCase().trim()) ||
                                staffListCopy.get(i).getRolename().toLowerCase().contains(s.toLowerCase().trim())
                                ){
                            filterListCopy.add(staffListCopy.get(i));
                        }
                    }

                    staffList = filterListCopy;
                    if(s.trim().length()<1){
                        staffList = staffListCopy;
                        if(GlobalVariable.getCurrentUser().getCurrentBsRule("staff", "cancreate")) {
                            fab.show();
                        }

                    }
                    employeeAdapter.refresh(staffList);

                    return false;
                }
            });
        }
        return true;
    }


    /**
     * handles  menu item clicks events
     * @param item menu item
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
     * @param data intent data received
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COUNTRY_REQUEST_CODE && resultCode == RESULT_OK) {
            Timber.v("onActivityResult"+data.getStringExtra(CountryCodeActivity.EXTRA_REPLY));

            Country country = new Gson().fromJson(data.getStringExtra(CountryCodeActivity.EXTRA_REPLY),Country.class);
            etxtcountryCode.setText(country.getDialCode());

        }


    }



}
