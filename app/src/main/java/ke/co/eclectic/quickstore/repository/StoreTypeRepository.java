package ke.co.eclectic.quickstore.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.dao.StoreTypeDao;
import ke.co.eclectic.quickstore.dao.QuickStoreRoomDb;
import ke.co.eclectic.quickstore.models.StoreType;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.StoreTypeResponse;
import timber.log.Timber;

/**
 * STEP 4
 * A Repository manages query threads and allows you to use multiple backends.
 * the Repository implements the logic for deciding whether to fetch data from a
 * network or use results cached in a local database.
 */
public class StoreTypeRepository {
    private StoreTypeDao mStoreDao;
    private LiveData<List<StoreType>> mAllStoresType;
    private MutableLiveData<MyApiResponses> myApiResponses = new MutableLiveData<MyApiResponses>();
    private CompositeDisposable disposable = new CompositeDisposable();

    /**
     * gets a handle to the database and initializes the member variables
     *
     * @param application the application
     */
    public StoreTypeRepository(Application application) {
        QuickStoreRoomDb db = QuickStoreRoomDb.getDatabase(application);
        mStoreDao = db.storeTypeDao();
    }


    /**
     * Deletes all storetype from database
     */
    public void deleteAll(){
        deleteStoreType(new StoreType());
    }

    /**
     * Deletes single store attached  from  database
     *
     * @param storeType the store type
     */
    public void deleteStoreType(StoreType storeType){
        disposable.add(Completable.fromAction(()->{
          mStoreDao.deleteAll();
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
     * Gets all stores type.
     *
     * @return all stores
     */
    public LiveData<List<StoreType>> getAllStoresType() {
        LiveData<List<StoreType>>  m = mStoreDao.getAllStoresType();
        try{
            if(m.getValue().size() == 0){
                refreshStoreType();
            }
        }catch (Exception e){
            refreshStoreType();
        }

        return  mStoreDao.getAllStoresType();
    }

    /**
     * fetches store types from api
     */
    private void refreshStoreType() {
        disposable.add(
                ApiClient.getApi().getStoreTypeData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<StoreTypeResponse>(){
                            @Override
                            public void onSuccess(StoreTypeResponse storeTypeResponse) {
                                if(storeTypeResponse.getStatus().contentEquals("success")){
                                    deleteAll();
                                    for(StoreType st:storeTypeResponse.getData()){
                                        insertToDb(st);
                                    }
                                    Timber.v("storeTypeResponse :  "+storeTypeResponse.getData().size());
                                    myApiResponses.postValue(new MyApiResponses("apirefresh","success",storeTypeResponse));

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
     * Insert store type to db.
     *
     * @param storeType the store type
     */
    public void insertToDb(StoreType storeType){
        disposable.add(Completable.fromAction(()->{
            mStoreDao.insert(storeType) ;
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
     * @param storeType details/information
     * @return a single store instance/record
     */
    public LiveData<List<StoreType>>  getSingleStore(StoreType storeType){
            return  mStoreDao.getSingleStoreType(storeType.getTypeid());
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
