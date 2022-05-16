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
import ke.co.eclectic.quickstore.dao.BusinessDao;
import ke.co.eclectic.quickstore.dao.QuickStoreRoomDb;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Business;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.BusinessResponse;
import ke.co.eclectic.quickstore.rest.response.BusinessRolesResponse;
import ke.co.eclectic.quickstore.rest.response.SupplierResponse;
import timber.log.Timber;

/**
 * STEP 4
 * A Repository manages query threads and allows you to use multiple backends.
 * the Repository implements the logic for deciding whether to fetch data from a
 * network or use results cached in a local database.
 *
 * Created by David Manduku on 08/10/2018.
 */
public class BusinessRepository {
    private BusinessDao mBusinessDao;
    private LiveData<List<Business>> mAllBusinesss;
    private MutableLiveData<MyApiResponses> myApiResponses = new MutableLiveData<MyApiResponses>();
    private CompositeDisposable disposable = new CompositeDisposable();

    /**
     * gets a handle to the database and initializes the member variables
     *
     * @param application the application
     */
    public BusinessRepository(Application application) {
        QuickStoreRoomDb db = QuickStoreRoomDb.getDatabase(application);
        mBusinessDao = db.businessDao();
        mAllBusinesss = mBusinessDao.getAllBusinesss();
    }

    /**
     * Gets all repository network/api response.
     *
     * @return MutableLiveData of the response
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return myApiResponses;
    }



    /**
     * Gets all businesss.
     *
     * @return the all businesss
     */
    public LiveData<List<Business>> getAllBusinesss() {
        return mAllBusinesss;
    }


    /**
     * Delete all business record.
     */
    public void deleteAll(){
        deleteFromDb(new Business());
        deleteApiBusiness(new Business());
    }

    /**
     * sends delete request to server
     *
     * @param business the business object
     */
    void deleteApiBusiness(Business business){
        HashMap<String, Object> data = new HashMap<>();
        data.put("request_type", "deletebusiness");
        data.put("businessid", business.getBusinessid());

        // sending request to api
        disposable.add(
                ApiClient.getApi().supplierApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<SupplierResponse>() {
                            @Override
                            public void onSuccess(SupplierResponse supplierResponse) {
                                if (supplierResponse.getStatus().contentEquals("success")) {
                                    Timber.v("supplierResponse");
                                    myApiResponses.postValue(new MyApiResponses("apidelete","success",supplierResponse));
                                    deleteFromDb(business);
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
     * deletes business record from local db
     * @param business
     */
    private void deleteFromDb(Business business){
        disposable.add(Completable.fromAction(()->{
            if(business.getBusinessid() == 0){
                mBusinessDao.deleteAll();
            }else{
                mBusinessDao.deleteSingleBusiness(business.getBusinessid());
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
     * Get single business live data.
     *
     * @param business the business
     * @return the live data
     */
    public LiveData<List<Business>>  getSingleBusiness(Business business){
            return  mBusinessDao.getSingleBusiness(business.getBusinessid());
    }


    /**
     * saves business record
     *
     * @param business the business
     */
    public void insert (Business business) {
        saveBusinessApi(business);
    }

    /**
     * sends save business record  request to api
     * @param business
     */
    private void saveBusinessApi(Business business) {
        HashMap<String, String> data = new HashMap<>();
        if(business.getBusinessid() == 0){
            data.put("request_type", "addbusiness");
        }else{
            data.put("request_type", "editbusiness");
        }

        data.put("name", business.getbName());
        data.put("country", business.getbCountry());
        data.put("location", business.getbLocation());
        data.put("telephone", business.getbDialCode().concat(business.getbTelephone()));
        data.put("postaladdress", business.getbPostalAddr());
        data.put("email", business.getbEmail());
        data.put("website", business.getbWebsite());


        // sending request to api
        disposable.add(
                ApiClient.getApi().businessApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<BusinessResponse>() {
                            @Override
                            public void onSuccess(BusinessResponse businessResponse) {

                                if (businessResponse.getStatus().contentEquals("success")) {
                                    if(business.getBusinessid() == 0){
                                        business.setBusinessid(businessResponse.getData().getBusinessid());
                                    }
                                    myApiResponses.postValue(new MyApiResponses("apisave","success",businessResponse));
                                    insertToDb(business);
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
    public void getBusinessRole() {
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","getroles");
        data.put("businessid", GlobalVariable.getCurrentUser().getBusinessid());
        disposable.add(
                ApiClient.getApi().businessApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<BusinessResponse>() {
                            @Override
                            public void onSuccess(BusinessResponse businessResponse) {

                                if (businessResponse.getStatus().contentEquals("success")) {
                                    myApiResponses.postValue(new MyApiResponses("apirefreshbusinessrole","success",businessResponse));


                                }


                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apirefreshbusinessrole","fail",e));

                                //CodingMsg.t(this,e.getMessage());
                            }
                        })
        );
    }

    /**
     * saves business record to local db
     *
     * @param business the business to save
     */
    public void insertToDb(Business business){
        disposable.add(Completable.fromAction(()->{
            mBusinessDao.insert(business) ;
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
     * clears diposable memory
     */
    public void disposeQ() {
        disposable.clear();
    }


}
