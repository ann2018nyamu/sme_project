package ke.co.eclectic.quickstore.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.NetworkInfo;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.models.InventoryStock;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.repository.InventoryStockRepository;
import timber.log.Timber;

/**
 * ViewModel holds your app's UI data in a lifecycle-conscious way
 * that survives configuration changes
 */
public class InventoryStockViewModel extends AndroidViewModel {
    private InventoryStockRepository mRepository;
    private LiveData<List<InventoryStock>> mAllInventoryStocks;
    private Disposable networkDisposable;
    private Disposable internetDisposable;
    private  MutableLiveData<Boolean>  isInternetConnected = new MutableLiveData<>();
    private boolean listenToActiveInternet = false;
    private HashMap<String, Object> allSupData = new HashMap<>();

    /**
     * Instantiates a new InventoryStock view model.
     *
     * @param application the application
     */
    public InventoryStockViewModel(Application application) {
        super(application);
        mRepository = new InventoryStockRepository(application);
            initNetwork(application);
            }

    /**
     * initializes network listening variables
     * @param application
     */
    private void initNetwork(Application application) {
        isInternetConnected.postValue(true);
        isInternetConnected.setValue(true);
        networkDisposable = ReactiveNetwork.observeNetworkConnectivity(application)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    final NetworkInfo.State state = connectivity.state();
                    final String name = connectivity.typeName();
                   Timber.v(String.format("state: %s, typeName: %s", state, name));
                });

        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
                .skip(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isConnected -> {
                    isInternetConnected.postValue(isConnected);
                    Timber.v("isConnected "+isConnected);
                    if(isConnected){
                        if(listenToActiveInternet){
                            Timber.v("Refresh supplier data ");
                            if(allSupData.size() > 0){
                                getAllInventoryStocks(allSupData);
                            }
                        }
                        listenToActiveInternet = true;
                    }
                    Timber.v(isConnected.toString());
                });

    }

    /**
     * Get is internet connected mutable live data.
     *
     * @return the mutable live data
     */
    public MutableLiveData<Boolean> getIsInternetConnected(){
        return isInternetConnected;
    }


    @Override
    protected void onCleared() {
        networkDisposable.dispose();
        internetDisposable.dispose();
        mRepository.disposeQ();
        super.onCleared();

    }
    /**
     * getter for all the InventoryStock records
     *
     * @return InventoryStock LiveData
     */
    public LiveData<List<InventoryStock>> getAllInventoryStocks(HashMap<String,Object> data) {
         allSupData = data;
        return mRepository.getAllInventoryStocks(data);
    }

    /**
     * gets errors object responses
     *
     * @return MutableLiveData response
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return mRepository.getApiResponse();
    }

    /**
     * Insert.
     *
     * @param inventoryStock the inventory
     */
    public void insert(InventoryStock inventoryStock) { mRepository.insert(inventoryStock); }

    /**
     *calls repository delete all
     */
    public void deleteAll() { mRepository.deleteAll(); }
    /**
     * Delete single inventoryStock record.
     */
    public void deleteInventoryStock(InventoryStock inventoryStock) { mRepository.deleteInventoryStock(inventoryStock); }


    /**
     * Gets single inventoryStock.
     *
     * @param inventoryStock the inventoryStock
     * @param action  the action
     * @return the single inventoryStockq
     */
    public LiveData<List<InventoryStock>> getSingleInventoryStock(InventoryStock inventoryStock,String action) { return mRepository.getSingleInventoryStock(inventoryStock); }


}
