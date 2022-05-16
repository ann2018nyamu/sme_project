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
import ke.co.eclectic.quickstore.models.OfferType;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.repository.OfferTypeRepository;
import timber.log.Timber;

/**
 * ViewModel holds your app's UI data in a lifecycle-conscious way
 * that survives configuration changes
 */
public class OfferTypeViewModel extends AndroidViewModel {
    private OfferTypeRepository mRepository;
    private LiveData<List<OfferType>> mAllOfferTypes;
    private Disposable networkDisposable;
    private Disposable internetDisposable;
    private  MutableLiveData<Boolean>  isInternetConnected = new MutableLiveData<>();
    private boolean listenToActiveInternet = false;
    private HashMap<String, Object> allSupData = new HashMap<>();


    /**
     * Instantiates a new OfferType view model.
     *
     * @param application the application
     */
    public OfferTypeViewModel(Application application) {
        super(application);
        mRepository = new OfferTypeRepository(application);
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
                            Timber.v("Refresh offerType data ");
                            if(allSupData.size() > 0){
                                getAllOfferTypes();
                            }
                        }
                        listenToActiveInternet = true;
                    }
                    Timber.v(isConnected.toString());
                });

    }

    /**
     * gets is sonnected instance
     * @return
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
    public LiveData<List<OfferType>> getAllOfferTypes() {
        return mRepository.getAllOfferTypes();
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
     * Delete all.
     */
    public void deleteAll() { mRepository.deleteAll(); }
    /**
     * Delete signle record/offerType.
     */
    public void deleteOfferType(OfferType offerType) { mRepository.deleteOfferType(offerType); }


    /**
     * Gets single offerType.
     *
     * @param offerType the offerType
     * @param action  the action
     * @return the single offerType
     */
    public LiveData<List<OfferType>> getSingleOfferType(OfferType offerType,String action) { return mRepository.getSingleOfferType(offerType); }



}
