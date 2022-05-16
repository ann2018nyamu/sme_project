package ke.co.eclectic.quickstore.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.NetworkInfo;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.models.StoreType;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.repository.StoreTypeRepository;
import timber.log.Timber;

/**
 * ViewModel holds your app's UI data in a lifecycle-conscious way
 * that survives configuration changes
 */
public class StoreTypeViewModel extends AndroidViewModel {
    private StoreTypeRepository mRepository;
    private LiveData<List<StoreType>> mAllStoreType;
    //implementing singleton
    private Disposable networkDisposable;
    private Disposable internetDisposable;
    private  MutableLiveData<Boolean>  isInternetConnected = new MutableLiveData<>();
    private boolean listenToActiveInternet = false;



    /**
     * Instantiates a new Store type view model.
     *
     * @param application the application
     */
    public StoreTypeViewModel(Application application) {
        super(application);
        mRepository = new StoreTypeRepository(application);
        mAllStoreType = mRepository.getAllStoresType();
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
                            Timber.v("Refresh store type data ");
                        }
                        listenToActiveInternet = true;
                    }
                    Timber.v(isConnected.toString());
                });

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        internetDisposable.dispose();
        networkDisposable.dispose();
        mRepository.disposeQ();
    }

    /**
     * Get is internet connected mutable live data.
     *
     * @return the mutable live data
     */
    public MutableLiveData<Boolean> getIsInternetConnected(){
        return isInternetConnected;
    }

    /**
     * gets api responses
     *
     * @return MutableLiveData responses
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return mRepository.getApiResponse();
    }

    /**
     * gets all the stores
     *
     * @return lists of stores
     */
    public LiveData<List<StoreType>> getAllStoreType() { return mAllStoreType; }


    /**
     * gets a single store based on unique id
     *
     * @param storeType the store type
     * @return list of stores
     */
    public LiveData<List<StoreType>> getSingleStore(StoreType storeType) { return mRepository.getSingleStore(storeType); }

    //DELETES

    /**
     * deletes all the stores in db
     */
    public void deleteAll() { mRepository.deleteAll(); }

}
