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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.models.Supplier;
import ke.co.eclectic.quickstore.repository.SupplierRepository;
import timber.log.Timber;

/**
 * ViewModel holds your app's UI data in a lifecycle-conscious way
 * that survives configuration changes
 */
public class SupplierViewModel extends AndroidViewModel {
    private SupplierRepository mRepository;
    private LiveData<List<Supplier>> mAllSuppliers;
    private Disposable networkDisposable;
    private Disposable internetDisposable;
    private  MutableLiveData<Boolean>  isInternetConnected = new MutableLiveData<>();
    private boolean listenToActiveInternet = false;
    private HashMap<String, Object> allSupData = new HashMap<>();

    /**
     * Instantiates a new Supplier view model.
     *
     * @param application the application
     */
    public SupplierViewModel(Application application) {
        super(application);
        mRepository = new SupplierRepository(application);
            initNetwork(application);
            }

    /**
     * initializes internet observables
     *
     * @param application app application instance
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
                                getAllSuppliers(allSupData);
                            }
                        }
                        listenToActiveInternet = true;
                    }
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
     * getter for all the supplier records
     *
     * @return all countries
     */
    public LiveData<List<Supplier>> getAllSuppliers(HashMap<String,Object> data) {
         allSupData = data;
        return mRepository.getAllSuppliers(data);
    }

    /**
     * gets repository response object
     *
     * @return all errors
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return mRepository.getApiResponse();
    }

    /**
     * Insert supplier to db.
     *
     * @param supplier the supplier
     */
    public void insert(Supplier supplier) { mRepository.insert(supplier); }

    /**
     * Delete all supplier.
     */
    public void deleteAll() { mRepository.deleteAll(); }
    /**
     * Delete single record/supplier.
     */
    public void deleteSupplier(Supplier supplier) { mRepository.deleteSupplier(supplier); }


    /**
     * Gets single supplier.
     *
     * @param supplier the supplier
     * @param action  the action
     * @return the single supplier
     */
    public LiveData<List<Supplier>> getSingleSupplier(Supplier supplier,String action) { return mRepository.getSingleSupplier(supplier); }

}
