package ke.co.eclectic.quickstore.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.dao.StoreDao;
import ke.co.eclectic.quickstore.dao.QuickStoreRoomDb;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Store;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.StoreResponse;
import timber.log.Timber;

/**
 * STEP 4
 * A Repository manages query threads and allows you to use multiple backends.
 * the Repository implements the logic for deciding whether to fetch data from a
 * network or use results cached in a local database.
 */
public class StoreRepository {
    private StoreDao mStoreDao;
    private LiveData<List<Store>> mAllStores;
    private MutableLiveData<MyApiResponses> myApiResponses = new MutableLiveData<MyApiResponses>();

    private CompositeDisposable disposable = new CompositeDisposable();

    /**
     * gets a handle to the database and initializes the member variables
     *
     * @param application the application
     */
    public StoreRepository(Application application) {
        QuickStoreRoomDb db = QuickStoreRoomDb.getDatabase(application);
        mStoreDao = db.storeDao();
        mAllStores = mStoreDao.getAllStores();

    }


    /**
     * Gets business stores.
     *
     * @param bid business id which the store belongs to
     * @return business stores
     */
    public LiveData<List<Store>> getBusinessStores(Integer bid) {
        return mStoreDao.getBusinessStores(bid);
    }

    /**
     * Deletes all store from database
     */
    public void deleteAll(){
        deleteFromDb(new Store());
    }

    /**
     * Deletes all store attached to  unique business id from  database
     *
     * @param store the store
     */
    public void deleteSingleStore(Store store){
        deleteApiSupplier(store);
    }

    /**
     * deletes store from api
     *
     * @param store
     */
    private void deleteApiSupplier(Store store){
        HashMap<String, Object> data = new HashMap<>();
        data.put("request_type", "deletestore");
        data.put("supplierid", store.getStoreid());
        data.put("businessid", store.getBusinessid());

        // sending request to api
        disposable.add(
                ApiClient.getApi().storeApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<StoreResponse>() {
                            @Override
                            public void onSuccess(StoreResponse storeResponse) {
                                if (storeResponse.getStatus().contentEquals("success")) {
                                    Timber.v("supplierResponse");
                                    myApiResponses.postValue(new MyApiResponses("apidelete","success",storeResponse));
                                    deleteFromDb(store);
                                    //GlobalMethod.goToActivity(AddSupplierActivity.this, MenuActivity.class);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apidelete","fail",e));
                            }
                        })
        );
    }

    /**
     * Delete store from db.
     *
     * @param store the store
     */
    public void deleteFromDb(Store store){
        disposable.add(Completable.fromAction(()->{
            if(store.getStoreid() == 0){
                mStoreDao.deleteAll();
            }else{
                mStoreDao.deleteSingleStore(store.getStoreid());
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        myApiResponses.postValue(new MyApiResponses("dbdelete","success",new JsonObject()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        myApiResponses.postValue(new MyApiResponses("dbdelete","error",e));
                    }
                }));
    }

    /**
     * Gets all stores.
     *
     * @return all stores
     */
    public LiveData<List<Store>> getAllStores() {
        refreshStores();
        return mAllStores;
    }

    /**
     * fetches store from api
     *
     */
    private void refreshStores() {
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","getstores");
        data.put("businessid", GlobalVariable.getCurrentUser().getBusinessid());
        //sending request to api
        disposable.add(
                ApiClient.getApi().storeApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<StoreResponse>(){
                            @Override
                            public void onSuccess(StoreResponse storeResponse) {
                                if(storeResponse.getStatus().contentEquals("success")){
                                    myApiResponses.postValue(new MyApiResponses("apirefresh","success",storeResponse));

                                    deleteAll();
                                    for(Store p:storeResponse.getStoreList()){
                                        insertToDb(p);
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apirefresh","fail",e));
                            }
                        })
        );
    }

    /**
     * Insert store to db.
     *
     * @param store the store
     */
    public void insertToDb(Store store){
        disposable.add(Completable.fromAction(()->{
            mStoreDao.insert(store) ;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        myApiResponses.postValue(new MyApiResponses("dbsave","success",new JsonObject()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        myApiResponses.postValue(new MyApiResponses("dbsave","fail",e));
                    }
                }));
    }

    /**
     * Get single store live data.
     *
     * @param store details/information
     * @return a single store instance/record
     */
    public LiveData<List<Store>>  getSingleStore(Store store){
                    getSingleApiStore(store);
            return  mStoreDao.getSingleStore(store.getStoreid());
    }

    /**
     * fetches single store from api
     * @param store
     */
    private void getSingleApiStore(Store store) {
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","individualstore");
        data.put("storeid",store.getStoreid());

        //sending request to api
        disposable.add(
                ApiClient.getApi().storeApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<StoreResponse>(){
                            @Override
                            public void onSuccess(StoreResponse storeResponse) {
                                if(storeResponse.getStatus().contentEquals("success")){
                                     insertToDb(storeResponse.getStoreList().get(0));
                                     myApiResponses.postValue(new MyApiResponses("apigetstore","success", storeResponse));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apigetstore","fail",e));
                            }
                        })
        );
    }

    /**
     * Insert store to api/db.
     *
     * @param store inserts store detail inn the database
     */
    public void insert (Store store) {
        saveStore(store);
    }

    /**
     * saves store in api
     * @param store
     */
    private void saveStore(Store store){
        HashMap<String, Object> data = new HashMap<>();

        if(store.getStoreid() == 0){
            data.put("request_type", "addstore");
            data.put("businessid", store.getBusinessid());
        }else{
            data.put("request_type", "editstore");
            data.put("storeid", store.getStoreid());
        }

        data.put("name", store.getStorename());
        data.put("storetypeid", store.getStoretypeid());
        data.put("location", store.getLocation());
        data.put("country", store.getCountry());
        data.put("countrycode", store.getCountrycode());
        data.put("telephone", store.getPhonenumber());
        data.put("postaladdress", store.getPostaladdress());
        data.put("email", store.getEmail());
        data.put("website", store.getWebsite());


        // sending request to api
        disposable.add(
                ApiClient.getApi().storeApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<StoreResponse>() {
                            @Override
                            public void onSuccess(StoreResponse storeResponse) {
                                if (storeResponse.getStatus().contentEquals("success")) {
                                    myApiResponses.postValue(new MyApiResponses("apisave","success",storeResponse));
                                    insertToDb(store);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                                myApiResponses.postValue(new MyApiResponses("apisave","fail",e));
                                //CodingMsg.t(this,e.getMessage());
                            }
                        })
        );

    }


    /**
     * Gets all repository network reesponse.
     *
     * @return all Mutable Live Data
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return myApiResponses;
    }


    /**
     * clears diposable memory
     */
    public void disposeQ() {
        disposable.clear();
    }
}
