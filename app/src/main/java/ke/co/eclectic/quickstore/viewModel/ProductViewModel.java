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
import ke.co.eclectic.quickstore.models.Product;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.repository.ProductRepository;
import timber.log.Timber;

/**
 * ViewModel holds your app's UI data in a lifecycle-conscious way
 * that survives configuration changes
 */
public class ProductViewModel extends AndroidViewModel {
    private ProductRepository mRepository;
    private Disposable networkDisposable;
    private Disposable internetDisposable;
    private  MutableLiveData<Boolean>  isInternetConnected = new MutableLiveData<>();
    private boolean listenToActiveInternet = false;
    private HashMap<String, Object> allProdData = new HashMap<>();

    /**
     * Instantiates a new Product view model.
     *
     * @param application the application
     */
    public ProductViewModel(Application application) {
        super(application);
        mRepository = new ProductRepository(application);
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isConnected -> {
                    isInternetConnected.postValue(isConnected);
                    if(isConnected){
                        if(listenToActiveInternet){
                            Timber.v("Refresh product data ");
                            if(allProdData.size() > 0){
                                getAllProducts(allProdData);
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
     * getter for all the product records
     *
     * @return all countries
     */
    public LiveData<List<Product>> getAllProducts(HashMap<String,Object> data) {
       allProdData = data;
        return mRepository.getAllProducts(data);
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
     * Insert.
     *
     * @param product the product
     */
    public void insert(Product product) { mRepository.insert(product); }

    /**
     * Delete all product.
     */
    public void deleteAll() { mRepository.deleteAll(); }

    /**
     * Gets single product.
     *
     * @param pName  name of the product
     * @return the single product
     */
    public LiveData<Product> getSingleProductByName(String pName) { return mRepository.getSingleProductByName(pName); }

    /**
     * Gets single product.
     *
     * @param pId  unique id of the product
     * @return the single product
     */
    public LiveData<Product> getSingleProductById(Integer pId) { return mRepository.getSingleProductById(pId); }

}
