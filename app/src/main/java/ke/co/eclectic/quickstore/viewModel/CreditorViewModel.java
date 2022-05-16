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
import ke.co.eclectic.quickstore.models.Creditor;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.repository.CreditorRepository;
import timber.log.Timber;

/**
 * ViewModel holds your app's UI data in a lifecycle-conscious way
 * that survives configuration changes
 */
public class CreditorViewModel extends AndroidViewModel {
    public Double totalAmount=0.00;
    private CreditorRepository mRepository;
    private LiveData<List<Creditor>> mAllCreditors;
    private Disposable networkDisposable;
    private Disposable internetDisposable;
    private  MutableLiveData<Boolean>  isInternetConnected = new MutableLiveData<>();
    private boolean listenToActiveInternet = false;
    private HashMap<String, Object> allSupData = new HashMap<>();

    /**
     * Instantiates a new Creditor view model.
     *
     * @param application the application
     */
    public CreditorViewModel(Application application) {
        super(application);
        mRepository = new CreditorRepository(application);
            initNetwork(application);
            }

    /**
     * initializes network listening
     *
     * @param application  app  application
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
                            Timber.v("Refresh creditor data ");
                            if(allSupData.size() > 0){
                                getAllCreditors(allSupData);
                            }
                        }
                        listenToActiveInternet = true;
                    }
                    Timber.v(isConnected.toString());
                });

    }

    /**
     *
     *
     * @return MutableLiveData
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
    public LiveData<List<Creditor>> getAllCreditors(HashMap<String,Object> data) {
        allSupData = data;
        mRepository.totalAmount = totalAmount;
        return mRepository.getAllCreditors(data);
    }

    /**
     * gets repository response object
     *
     * @return MutableLiveData response
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return mRepository.getApiResponse();
    }

    /**
     * Insert.
     *
     * @param creditor the creditor
     */
    public void insert(Creditor creditor) { mRepository.insert(creditor); }

    /**
     * calls repository delete
     */
    public void deleteAll() { mRepository.deleteAll(); }
    /**
     * Delete single record/creditor.
     */
    public void deleteCreditor(Creditor creditor) { mRepository.deleteCreditor(creditor); }


    /**
     * Gets single creditor.
     *
     * @param creditor the creditor
     * @param action  the action
     * @return the single creditor
     */
    public LiveData<List<Creditor>> getSingleCreditor(Creditor creditor,String action) { return mRepository.getSingleCreditor(creditor); }



}
