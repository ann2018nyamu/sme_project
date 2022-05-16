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
import ke.co.eclectic.quickstore.models.Store;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.repository.StoreRepository;
import timber.log.Timber;

/**
 * ViewModel holds your app's UI data in a lifecycle-conscious way
 * that survives configuration changes
 */
public class StoreViewModel extends AndroidViewModel {
    private StoreRepository mRepository;
    //implementing singleton
    private Disposable networkDisposable;
    private Disposable internetDisposable;
    private  MutableLiveData<Boolean>  isInternetConnected = new MutableLiveData<>();
    private boolean listenToActiveInternet = false;

    /**
     * Instantiates a new Store view model.
     *
     * @param application the application
     */
    public StoreViewModel(Application application) {
        super(application);
        mRepository = new StoreRepository(application);
        initNetwork(application);
    }

    /**
     * initializes internet observables
     *
     * @param application application data
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

        internetDisposable =  ReactiveNetwork.observeInternetConnectivity()
                .skip(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isConnected -> {
                    isInternetConnected.postValue(isConnected);
                    Timber.v("isConnected "+isConnected);
                    if(isConnected){
                        if(listenToActiveInternet){
                            Timber.v("Refresh store data ");

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
        internetDisposable.dispose();
        networkDisposable.dispose();
        mRepository.disposeQ();
    }

    /**
     * adds stores in db
     *
     * @param store the store
     */
    public void insert(Store store) { mRepository.insert(store); }

    /**
     * gets all the stores
     *
     * @return lists of stores
     */
    public LiveData<List<Store>> getAllStore() {
        return mRepository.getAllStores();
    }

    /**
     * gets api responses
     *
     * @return all errors
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return mRepository.getApiResponse();
    }


    /**
     * gets a single store based on unique id
     *
     * @param store the store
     * @return list of stores
     */
    public LiveData<List<Store>> getSingleStore(Store store) { return mRepository.getSingleStore(store); }

    /**
     * deletes all the stores in db
     */
    public void deleteAll() { mRepository.deleteAll(); }

    /**
     * deletes all the stores related to a unique business in db
     * store object has to have a business id
     *
     * @param store the store
     */
    public void deleteSingleStore(Store store) { mRepository.deleteSingleStore(store); }

}
