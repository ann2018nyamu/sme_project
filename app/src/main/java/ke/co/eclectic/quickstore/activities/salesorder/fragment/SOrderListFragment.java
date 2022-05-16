package ke.co.eclectic.quickstore.activities.salesorder.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.customer.CustomerActivity;
import ke.co.eclectic.quickstore.activities.salesorder.AddSalesOrderActivity;
import ke.co.eclectic.quickstore.adapters.SalesOrderInventoryStockAdapter;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.models.Customer;
import ke.co.eclectic.quickstore.models.InventoryStock;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SOrderListFragment extends Fragment implements  SalesOrderInventoryStockAdapter.SoInventoryComm {

    private static final int CUSTOMER_REQUEST_CODE = 10;
    @BindView(R.id.sorderedRV)
    RecyclerView sorderedRV;
    @BindView(R.id.etxtSelectCustomer)
    EditText etxtSelectCustomer;

    private CompositeDisposable disposable = new CompositeDisposable();
    Observable<Boolean> observable;
    static SOrderListFragment fragment = null;
    public static List<InventoryStock> inventoryStockList = new ArrayList<>();
    private AddSalesOrderActivity addSalesOrderActivity;

    /**
     * Instantiates a new Login fragment.
     */
    public SOrderListFragment() {
        // Required empty public constructor
    }

    /**
     * Singltone of the Cart Fragment.
     *
     * @return the login fragment
     */
    public static SOrderListFragment getInstance() {
        if (fragment == null){
            fragment = new SOrderListFragment();
        }
        return fragment;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.v("SOrderListFragment onDestroyView");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       
        View rootView = inflater.inflate(R.layout.fragment_sales_ordered_list, container, false);
       
        ButterKnife.bind(this, rootView);
        //init();
        addSalesOrderActivity = ((AddSalesOrderActivity) getActivity());
        initEdittext();
        initRV();
        initDbData();

        return rootView;
    }

    /**
     * initaializes edittext
     */
    private void initEdittext() {
        etxtSelectCustomer.setOnClickListener(view->{
            HashMap<String,String> data = new HashMap<>();
            data.put("action","select");
            data.put("from","salesorder");
            GlobalMethod.goToActivity(this, CustomerActivity.class,data,CUSTOMER_REQUEST_CODE);
        });

    }

    /**
     * initializes sales order list
     */
    private void initRV() {
        SalesOrderInventoryStockAdapter salesOrderInventoryStockAdapter = new SalesOrderInventoryStockAdapter(getActivity(), inventoryStockList, this);
        sorderedRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        sorderedRV.setAdapter(salesOrderInventoryStockAdapter);
    }

    /**
     * refreshes sales order list
     */
    public  void refreshList() {
        initRV();
    }

    /**
     * Init activity  data.
     */
    public void  initDbData(){
        addSalesOrderActivity.refreshOrder();
        Timber.v("initDbData");
        Timber.v("getContactname  "+((AddSalesOrderActivity) getActivity()).selectedCustomer.getContactname());
        if(((AddSalesOrderActivity) getActivity()).selectedCustomer.getCustomerid() != 0){
            etxtSelectCustomer.setText(((AddSalesOrderActivity) getActivity()).selectedCustomer.getContactname());
        }
    }


    /**
     * handles destroy state of activity
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //disposes memory used by observable
        disposable.dispose();
    }

    /**
     * handles inventory stock clicks events
     *
     *
     * @param inventoryStock the product item
     * @param action         the action to be performed to clicked item
     * @param position  position of clced item
     */
    @Override
    public void soInventoryStockMessage(InventoryStock inventoryStock, String action,Integer position) {

        if(action.contentEquals("remove")){
           // inventoryStockList.remove(position);
            addSalesOrderActivity.selectedInventoryStockH.remove(inventoryStock.getInventoryid());
        }

        if(action.contentEquals("update")){
            inventoryStockList.add(position,inventoryStock);
            addSalesOrderActivity.selectedInventoryStockH.put(inventoryStock.getInventoryid(),inventoryStock);
        }

       // salesOrderInventoryStockAdapter.notifyItemChanged(position);

        addSalesOrderActivity.refreshOrder();
    }


    /**
     * handles activity result responses
     *
     * @param requestCode request code
     * @param resultCode result code
     * @param data data received
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CUSTOMER_REQUEST_CODE && resultCode == RESULT_OK) {
            //returns a single category object
            //fetch all category again
            Customer selectedCustomer = new Gson().fromJson(data.getStringExtra(CustomerActivity.EXTRA_REPLY),Customer.class);
            etxtSelectCustomer.setText(selectedCustomer.getContactname());
            addSalesOrderActivity.selectedCustomer = selectedCustomer;
            //getCategory();
        }

    }


}
