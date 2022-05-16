package ke.co.eclectic.quickstore.activities.pos.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.CountryCodeActivity;
import ke.co.eclectic.quickstore.activities.pos.CartEditActivity;
import ke.co.eclectic.quickstore.adapters.CartInventoryStockAdapter;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.models.Country;
import ke.co.eclectic.quickstore.models.InventoryStock;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment implements CartInventoryStockAdapter.CartComm {

    private static final int CART_REQUEST_CODE = 21;
    @BindView(R.id.cartRV)
    RecyclerView cartRV;


    private CompositeDisposable disposable = new CompositeDisposable();

    Observable<Boolean> observable;

   static CartFragment fragment = null;
   public static List<InventoryStock> productlists = new ArrayList<>();

    /**
     * Instantiates a new Login fragment.
     */
    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Singltone of the Cart Fragment.
     *
     * @return the login fragment
     */
    public static CartFragment getInstance() {
        if (fragment == null){
            fragment = new CartFragment();
        }
        return fragment;
    }

    /**
     * cleans up the fragment
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragment = null;
        Timber.v("CartFragment onDestroyView");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, rootView);
        // init();
        initRV();
        return rootView;
    }

    /**
     * initializes recyclerview
     */
    private void initRV() {
        CartInventoryStockAdapter cartProductAdapter    = new CartInventoryStockAdapter(getActivity(), productlists,this);
        cartRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartRV.setAdapter(cartProductAdapter);
    }

    /**
     * refreshes cart list
     */
    public void refreshList() {
        initRV();
    }


    /**
     * cleans up the fragment
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }


    @Override
    public void cartMessage(int position, InventoryStock inventoryStock, String action) {
        if (action.contentEquals("edit")) {
            HashMap<String ,String> data = new HashMap<>();
            data.put("inventorystock", new Gson().toJson(inventoryStock));
            GlobalMethod.goToActivity(CartFragment.this, CartEditActivity.class,data,CART_REQUEST_CODE);
        }
    }
    /**
     *
     * @param requestCode request code sent to the activity/operation
     * @param resultCode result received by called activity/operation
     * @param data data return by the called activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CART_REQUEST_CODE && resultCode == RESULT_OK) {
            CodingMsg.l(CartEditActivity.EXTRA_REPLY);
            CodingMsg.l(CartEditActivity.EXTRA_REPLY);
            InventoryStock inventoryStock = new Gson().fromJson(data.getStringExtra(CartEditActivity.EXTRA_REPLY),InventoryStock.class);
            for(int i=0;i< productlists.size();i++){
                if(productlists.get(i).getInventoryid() == inventoryStock.getInventoryid()){
                    if(data.getStringExtra(CartEditActivity.DELETE_REPLY) != null && data.getStringExtra(CartEditActivity.DELETE_REPLY).contentEquals("yes")){
                                productlists.remove(i);

                    } else {
                                productlists.set(i,inventoryStock);
                    }
                   break;
                }
            }

            initRV();

        }

    }
}
