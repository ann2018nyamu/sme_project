package ke.co.eclectic.quickstore.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.dao.QuickStoreRoomDb;
import ke.co.eclectic.quickstore.dao.PaymentTypeDao;
import ke.co.eclectic.quickstore.models.PaymentType;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.PaymentTypeResponse;

/**
 * STEP 4
 * A Repository manages query threads and allows you to use multiple backends.
 * the Repository implements the logic for deciding whether to fetch data from a
 * network or use results cached in a local database.
 */
public class PaymentTypeRepository {
    private PaymentTypeDao mPaymentTypeDao;
    private LiveData<List<PaymentType>> mAllPaymentType;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<MyApiResponses> myApiResponses = new MutableLiveData<MyApiResponses>();


    /**
     * gets a handle to the database and initializes the member variables
     *
     * @param application the application
     */
    public PaymentTypeRepository(Application application) {
        QuickStoreRoomDb db = QuickStoreRoomDb.getDatabase(application);
        mPaymentTypeDao = db.paymentTypeDao();
        mAllPaymentType = mPaymentTypeDao.getAllPaymentTypes();
    }

    /**
     * Gets all repository network reesponse.
     *
     * @return all suppliers
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return myApiResponses;
    }


    /**
     * Deletes all paymentType form database
     */
    public void deleteAll(){
        deleteFromDb(new PaymentType());
    }

    /**
     * Deletes all paymentType attached to  unique business id from  database
     *
     * @param paymentType the paymentType
     */
    public void deletePaymentType(PaymentType paymentType){
        deleteFromDb(paymentType);
    }


    /**
     * Gets all paymentTypes.
     *
     * @return all paymentTypes
     *
     */
    public LiveData<List<PaymentType>> getAllpaymentTypes() {
        refreshPaymentTypes();
        return mAllPaymentType;
    }

    /**
     * Retrieves payment option type from api
     */
    private void refreshPaymentTypes(){
        //sending request to api
        disposable.add(
                ApiClient.getApi().getPaymentTypes()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PaymentTypeResponse>(){
                            @Override
                            public void onSuccess(PaymentTypeResponse paymentTypeResponse) {
                                if(paymentTypeResponse.getStatus().contentEquals("success")){
                                    myApiResponses.postValue(new MyApiResponses("apirefresh","success",paymentTypeResponse));

                                    deleteAll();
                                    for(PaymentType u:paymentTypeResponse.getPaymentTypeList()){
                                        insertToDb(u);
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e){
                                myApiResponses.postValue(new MyApiResponses("apirefresh","fail",e));
                                //CodingMsg.t(getActivity(),e.getMessage());
                            }
                        })
        );
    }



    /**
     * Get single paymentType live data.
     *
     * @param paymentType details/information
     * @return a single paymentType instance/record
     */
    public LiveData<List<PaymentType>>  getSinglePaymentType(PaymentType paymentType){
            return  mPaymentTypeDao.getSinglePaymentType(paymentType.getPaymenttypeid());
    }


    /**
     * Insert payment type record to db.
     *
     * @param paymentType the payment type
     */
    public void insertToDb(PaymentType paymentType){
        disposable.add(Completable.fromAction(()->{
            mPaymentTypeDao.insert(paymentType) ;
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
     * Delete paymentType from db.
     *
     * @param paymentType the payment type
     */
    public void deleteFromDb(PaymentType paymentType){
        disposable.add(Completable.fromAction(()->{
            if(paymentType.getPaymenttypeid() != 0){
                mPaymentTypeDao.deleteSinglePaymentType(paymentType.getPaymenttypeid());
            }else{
                mPaymentTypeDao.deleteAll();
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
     * clears diposable memory
     */
    public void disposeQ() {
        disposable.clear();
    }
}
