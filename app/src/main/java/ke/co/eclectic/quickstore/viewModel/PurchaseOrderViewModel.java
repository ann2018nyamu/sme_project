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
import ke.co.eclectic.quickstore.models.PurchaseOrder;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.repository.PurchaseOrderRepository;
import timber.log.Timber;

/**
 * ViewModel holds your app's UI data in a lifecycle-conscious way
 * that survives configuration changes
 */
public class PurchaseOrderViewModel extends AndroidViewModel {
    private PurchaseOrderRepository mRepository;
    private LiveData<List<PurchaseOrder>> mAllPurchaseOrders;
    private Disposable networkDisposable;
    private Disposable internetDisposable;
    private  MutableLiveData<Boolean>  isInternetConnected = new MutableLiveData<>();
    private boolean listenToActiveInternet = false;
    private HashMap<String, Object> allPurOrderData = new HashMap<>();

    /**
     * Instantiates a new PurchaseOrder view model.
     *
     * @param application the application
     */
    public PurchaseOrderViewModel(Application application) {
        super(application);
        mRepository = new PurchaseOrderRepository(application);
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
                            Timber.v("Refresh purchaseorder data ");
                            if(allPurOrderData.size() > 0){
                                getAllPurchaseOrders(allPurOrderData);
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
     * getter for all the users records
     *
     * @return all countries
     */
    public LiveData<List<PurchaseOrder>> getAllPurchaseOrders(HashMap<String,Object> data) {
         allPurOrderData = data;
        return mRepository.getAllPurchaseOrders(data);
    }

    /**
     * gets errors object
     *
     * @return all errors
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return mRepository.getApiResponse();
    }

    /**
     * Insert purchase order .
     *
     * @param purchaseorder the purchaseorder
     */
    public void insert(PurchaseOrder purchaseorder,String requestType) { mRepository.insert(purchaseorder,requestType); }


    /**
     * Delete all.
     */
    public void deleteAll() { mRepository.deleteAll(); }
    /**
     * Delete single record/purchaseorder.
     */
    public void deletePurchaseOrder(PurchaseOrder purchaseorder) { mRepository.deletePurchaseOrder(purchaseorder); }


    /**
     * Gets single purchaseorder.
     *
     * @param purchaseorder the purchaseorder
     * @return the single purchaseorder
     */
    public LiveData<List<PurchaseOrder>> getSinglePurchaseOrder(PurchaseOrder purchaseorder) { return mRepository.getSinglePurchaseOrder(purchaseorder); }


    /**
     * Send payment request.
     *
     * @param data the request data
     */
    public void sendPaymentRequest(HashMap<String, Object> data) {
        mRepository.sendPaymentRequest(data);
    }
}
