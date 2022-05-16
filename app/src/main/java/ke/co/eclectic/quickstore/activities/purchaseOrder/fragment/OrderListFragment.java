package ke.co.eclectic.quickstore.activities.purchaseOrder.fragment;


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
import ke.co.eclectic.quickstore.activities.purchaseOrder.AddPurchaseOrderActivity;
import ke.co.eclectic.quickstore.activities.supplier.SupplierActivity;
import ke.co.eclectic.quickstore.adapters.PurchaseOrderInventoryStockAdapter;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.models.InventoryStock;
import ke.co.eclectic.quickstore.models.Supplier;
import ke.co.eclectic.quickstore.viewModel.PurchaseOrderViewModel;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * Displays Order list iin purchase order
 */
public class OrderListFragment extends Fragment implements  PurchaseOrderInventoryStockAdapter.PoInventoryComm {

    private static final int SUPPLIER_REQUEST_CODE = 10;
    @BindView(R.id.porderedRV)
    RecyclerView porderedRV;
    @BindView(R.id.etxtSelectSupplier)
    EditText etxtSelectSupplier;

    private CompositeDisposable disposable = new CompositeDisposable();
    Observable<Boolean> observable;
   static OrderListFragment fragment = null;
   public static List<InventoryStock> inventoryStockList = new ArrayList<>();
    private PurchaseOrderViewModel mPurchaseOrderViewModel;

    /**
     * Instantiates a new Login fragment.
     */
    public OrderListFragment() {
        // Required empty public constructor
    }

    /**
     * Singltone of the Cart Fragment.
     *
     * @return the login fragment
     */
    public static OrderListFragment getInstance() {
        if (fragment == null){
            fragment = new OrderListFragment();
        }
        return fragment;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.v("CartFragment onDestroyView");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_purchase_ordered_list, container, false);
        ButterKnife.bind(this, rootView);
        // init();
        initEdittext();
        initRV();
        initDbData();
        return rootView;
    }

    /**
     * initializes edittext
     */
    private void initEdittext() {
        etxtSelectSupplier.setOnClickListener(view->{
            HashMap<String,String> data = new HashMap<>();
            data.put("action","select");
            data.put("from","purchaseorder");
            GlobalMethod.goToActivity(this, SupplierActivity.class,data,SUPPLIER_REQUEST_CODE);
        });
    }

    /**
     * initializes recyclerview
     */
    private void initRV() {
        PurchaseOrderInventoryStockAdapter purchaseOrderInventoryStockAdapter = new PurchaseOrderInventoryStockAdapter(getActivity(), inventoryStockList, this);
        porderedRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        porderedRV.setAdapter(purchaseOrderInventoryStockAdapter);
    }

    /**
     * refreshes recyclerview
     */
    public  void refreshList() {

        initRV();
    }

    /**
     * Initializes  data.
     */
    public void  initDbData(){
          ((AddPurchaseOrderActivity) getActivity()).refreshOrder();

        if(((AddPurchaseOrderActivity) getActivity()).selectedSupplier.getSupplierid() != 0){
            etxtSelectSupplier.setText(((AddPurchaseOrderActivity) getActivity()).selectedSupplier.getContactname());
        }
    }


    /**
     * cleans up memory used by observables
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @Override
    public void poInventoryStockMessage(InventoryStock inventoryStock, String action,Integer position) {
        if(action.contentEquals("remove")){
           // inventoryStockList.remove(position);
            ((AddPurchaseOrderActivity) getActivity()).selectedInventoryStockH.remove(inventoryStock.getInventoryid());
        }

        if(action.contentEquals("update")){
            inventoryStockList.add(position,inventoryStock);
            ((AddPurchaseOrderActivity) getActivity()).selectedInventoryStockH.put(inventoryStock.getInventoryid(),inventoryStock);
        }

        ((AddPurchaseOrderActivity) getActivity()).refreshOrder();
    }

    /**
     * handles activity results
     * @param requestCode request code used by currect activity
     * @param resultCode result code used by
     * @param data  data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SUPPLIER_REQUEST_CODE && resultCode == RESULT_OK) {
            //returns a single category object
            //fetch all category again
          Supplier  selectedSupplier = new Gson().fromJson(data.getStringExtra(SupplierActivity.EXTRA_REPLY),Supplier.class);
            etxtSelectSupplier.setText(selectedSupplier.getContactname());
            ((AddPurchaseOrderActivity) getActivity()).selectedSupplier = selectedSupplier;
            //getCategory();
        }


    }


}
