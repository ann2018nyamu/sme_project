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
import ke.co.eclectic.quickstore.models.SalesOrder;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.repository.SalesOrderRepository;
import timber.log.Timber;

/**
 * ViewModel holds your app's UI data in a lifecycle-conscious way
 * that survives configuration changes
 */
public class SalesOrderViewModel extends AndroidViewModel {
    private SalesOrderRepository mRepository;
    private LiveData<List<SalesOrder>> mAllSalesOrders;
    private Disposable networkDisposable;
    private Disposable internetDisposable;
    private  MutableLiveData<Boolean>  isInternetConnected = new MutableLiveData<>();
    private boolean listenToActiveInternet = false;
    private HashMap<String, Object> allPurOrderData = new HashMap<>();

    /**
     * Instantiates a new SalesOrder view model.
     *
     * @param application the application
     */
    public SalesOrderViewModel(Application application) {
        super(application);
        mRepository = new SalesOrderRepository(application);
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
                            Timber.v("Refresh salesOrder data ");
                            if(allPurOrderData.size() > 0){
                                getAllSalesOrders(allPurOrderData);
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
        super.onCleared();
        networkDisposable.dispose();
        internetDisposable.dispose();
        mRepository.disposeQ();
    }
    /**
     * getter for all the users records
     *
     * @return all countries
     */
    public LiveData<List<SalesOrder>> getAllSalesOrders(HashMap<String,Object> data) {
         allPurOrderData = data;
        return mRepository.getAllSalesOrders(data);
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
     *
     * @param data payment information(amount)
     */
    public void sendPaymentRequest(HashMap<String,Object> data) { mRepository.sendPaymentRequest(data); }
    /**
     *
     * @param salesOrder pos  selling data
     */
    public void savePos(SalesOrder salesOrder) { mRepository.savePos(salesOrder); }


    /**
     * Insert sales order.
     *
     * @param salesOrder the salesOrder
     */
    public void insert(SalesOrder salesOrder,String requestType) { mRepository.insert(salesOrder,requestType); }

    /**
     * Delete all sales order.
     */
    public void deleteAll() { mRepository.deleteAll(); }
    /**
     * Delete signle record/salesOrder.
     */
    public void deleteSalesOrder(SalesOrder salesOrder) { mRepository.deleteSalesOrder(salesOrder); }


    /**
     * Gets single salesOrder.
     *
     * @param salesOrder the salesOrder
     * @return the single salesOrder
     */
    public LiveData<List<SalesOrder>> getSingleSalesOrder(SalesOrder salesOrder) { return mRepository.getSingleSalesOrder(salesOrder); }



}
