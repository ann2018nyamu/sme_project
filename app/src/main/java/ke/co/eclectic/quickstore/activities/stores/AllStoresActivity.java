package ke.co.eclectic.quickstore.activities.stores;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.adapters.StoresAdapter;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Store;
import ke.co.eclectic.quickstore.viewModel.UserViewModel;

public class AllStoresActivity extends AppCompatActivity implements StoresAdapter.StoreComm {
    public static final String EXTRA_REPLY = "STORE_REPLY";
    @BindView(R.id.toolBar)
    Toolbar toolBar;
  
  
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;
    @BindView(R.id.storesRV)
     RecyclerView storesRV;

    private CompositeDisposable disposable = new CompositeDisposable();
    private List<Store> storeLists = new ArrayList<>();
   // private List<Store> storeListsCopy = new ArrayList<>();
    private StoresAdapter allStoreAdapter;
    private String action="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_stores);
        ButterKnife.bind(this);
        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            action = extra.get("action").toString();
            if(null == action){
                action = "";
            }
        }

        initToolbar();
        initRV();
        initData();
    }

    /**
     * initializes store data
     */
    private void initData() {

        HashMap<String,Store>  bsH =  GlobalVariable.getCurrentUser().getBusinessStoresH();
        storeLists.clear();
       
        for(Map.Entry<String,Store> entry: bsH.entrySet()){
            storeLists.add(entry.getValue());
        }
       // storeListsCopy = storeLists;
        allStoreAdapter.notifyDataSetChanged();

    }

    /**
     * initializes store list data
     */
    private void initRV(){
       // dummyData();
        allStoreAdapter = new StoresAdapter(this,storeLists);
        storesRV.setLayoutManager(new LinearLayoutManager(this));
        storesRV.setAdapter(allStoreAdapter);
    }


    /**
     * clears observable memory allocation
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    /**
     * initializes toolbar 
     */
    private void initToolbar() {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Select a Store");
    }

    /**
     * handles menu item click event
     * @param item  menu item
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
     * handles store item clicks
     * 
     * @param position of the clicked item
     * @param storeitem single store item
     * @param action action to be performed
     */
    @Override
    public void storeMessage(int position, Store storeitem, String action) {
        Store store = new Store();
        store.setStoreid(storeitem.getStoreid());
        store.setStorename(storeitem.getStorename());
        store.setBusinessid(storeitem.getBusinessid());
        store.setBusinessname(storeitem.getBusinessname());
        store.setStoreuserid(storeitem.getStoreuserid());

        if(this.action.contentEquals("select")){
            //checking if user wanted to select a store
            //resetting default business  data and store data to user selection
            GlobalVariable.getCurrentUser().setStoreid(storeitem.getStoreid());
            GlobalVariable.getCurrentUser().setStorename(storeitem.getStorename());
            GlobalVariable.getCurrentUser().setBusinessid(storeitem.getBusinessid());
            GlobalVariable.getCurrentUser().setBusinessname(storeitem.getBusinessname());
            GlobalVariable.getCurrentUser().saveUserInfo( GlobalVariable.getCurrentUser(),this);

            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_REPLY, new Gson().toJson(store));
            setResult(RESULT_OK, replyIntent);
            finish();

        }else{
            if(action.contentEquals("view")){
                HashMap<String ,String> data = new HashMap<>();
                data.put("store", new Gson().toJson(store));
                GlobalMethod.goToActivity(this,AddStoreActivity.class,data);
            }
        }
    }
}
