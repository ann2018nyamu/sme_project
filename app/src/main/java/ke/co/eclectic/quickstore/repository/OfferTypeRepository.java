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
import ke.co.eclectic.quickstore.dao.QuickStoreRoomDb;
import ke.co.eclectic.quickstore.dao.OfferTypeDao;
import ke.co.eclectic.quickstore.models.OfferType;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.OfferTypeResponse;

/**
 * STEP 4
 * A Repository manages query threads and allows you to use multiple backends.
 * the Repository implements the logic for deciding whether to fetch data from a
 * network or use results cached in a local database.
 */
public class OfferTypeRepository {
    private OfferTypeDao mOfferTypeDao;
    private LiveData<List<OfferType>> mAllOfferType;
    private MutableLiveData<MyApiResponses> myApiResponses = new MutableLiveData<MyApiResponses>();
    private CompositeDisposable disposable = new CompositeDisposable();
    /**
     * gets a handle to the database and initializes the member variables
     *
     * @param application the application
     */
    public OfferTypeRepository(Application application) {
        QuickStoreRoomDb db = QuickStoreRoomDb.getDatabase(application);
        mOfferTypeDao = db.offerTypeDao();
        mAllOfferType = mOfferTypeDao.getAllOfferTypes();

    }

    /**
     * Deletes all offertype form database
     */
    public void deleteAll(){
        
        deleteFromDb(new OfferType());
    }

    /**
     * Delete from offertype from db.
     *
     * @param offerType the offer type instance
     */
    public void deleteFromDb(OfferType offerType){
            disposable.add(Completable.fromAction(()->{
                if(offerType.getOffertypeid() == 0){
                    mOfferTypeDao.deleteAll();
                }else{
                    mOfferTypeDao.deleteSingleOfferType(offerType.getOffertypeid());
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
     * Gets all inventory stock.
     *
     * @return all inventory stock livedata
     */
    public LiveData<List<OfferType>> getAllOfferTypes() {
        refreshOfferTypes();
        return mAllOfferType;
    }

    /**
     * Gets all repository network reesponse.
     *
     * @return all inventory stock
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return myApiResponses;
    }

    /**
     * Get single inventory stock live data.
     *
     * @param offerType details/information
     * @return a single supplier instance/record
     */
    public LiveData<List<OfferType>>  getSingleOfferType(OfferType offerType){
            return  mOfferTypeDao.getSingleOfferType(offerType.getOffertypeid());
    }

    /**
     * Insert to db.
     *
     * @param supplier the supplier
     */
    public void insertToDb(OfferType supplier){
        disposable.add(Completable.fromAction(()->{
            mOfferTypeDao.insert(supplier) ;
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
     * retrieves all offer type from api
     */
    private void refreshOfferTypes() {
        //sending request to api
        disposable.add(
                ApiClient.getApi().getOfferTypeData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<OfferTypeResponse>(){
                            @Override
                            public void onSuccess(OfferTypeResponse offerTypeResponse) {
                                if(offerTypeResponse.getStatus().contentEquals("success")){
                                    myApiResponses.postValue(new MyApiResponses("apirefresh","success",offerTypeResponse));

                                    deleteAll();
                                    for(OfferType p:offerTypeResponse.getOfferTypeList()){
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
     * frees up disposable memory
     */
    public void disposeQ() {
        disposable.dispose();
    }



    /**
     * Deletes all inventory stock attached to  unique business id from  database
     *
     * @param offerType the offer type
     */
    /**
     * Insert to db.
     *
     * @param offerType the supplier
     */
    public void deleteOfferType(OfferType offerType){
            disposable.add(Completable.fromAction(()->{
                if(offerType.getOffertypeid() == 0){
                    mOfferTypeDao.deleteAll();
                }else{
                    mOfferTypeDao.deleteSingleOfferType(offerType.getOffertypeid());
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

}
