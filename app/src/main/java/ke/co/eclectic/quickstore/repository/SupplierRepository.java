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
import ke.co.eclectic.quickstore.dao.SupplierDao;
import ke.co.eclectic.quickstore.dao.QuickStoreRoomDb;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.models.Supplier;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.SupplierResponse;
import timber.log.Timber;

/**
 * STEP 4
 * A Repository manages query threads and allows you to use multiple backends.
 * the Repository implements the logic for deciding whether to fetch data from a
 * network or use results cached in a local database.
 */

public class SupplierRepository {
    private SupplierDao mSupplierDao;
    private LiveData<List<Supplier>> mAllSupplier;
    private MutableLiveData<MyApiResponses> myApiResponses = new MutableLiveData<MyApiResponses>();
    private CompositeDisposable disposable = new CompositeDisposable();
    /**
     * gets a handle to the database and initializes the member variables
     *
     * @param application the application
     */
    public SupplierRepository(Application application) {
        QuickStoreRoomDb db = QuickStoreRoomDb.getDatabase(application);
        mSupplierDao = db.supplierDao();
        mAllSupplier = mSupplierDao.getAllSuppliers();
    }

    /**
     * Deletes all supplier from database
     */
    public void deleteAll(){
        deleteFromDb(new Supplier());
    }

    /**
     * Deletes all supplier attached to  unique business id from  database
     *
     * @param supplier the supplier
     */
    public void deleteSupplier(Supplier supplier){
        deleteApiSupplier(supplier);
    }
   
        public void deleteFromDb(Supplier supplier){
            disposable.add(Completable.fromAction(()->{
                if(supplier.getSupplierid() == 0){
                    mSupplierDao.deleteAll();
                } else {
                    mSupplierDao.deleteSingleSupplier(supplier.getSupplierid());
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
     * Gets all suppliers.
     *
     * @return all suppliers
     */
    public LiveData<List<Supplier>> getAllSuppliers(HashMap<String,Object> data) {
        refreshSuppliers(data);
        return mAllSupplier;
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
     * Get single supplier live data.
     *
     * @param supplier details/information
     * @return a single supplier instance/record
     */
    public LiveData<List<Supplier>>  getSingleSupplier(Supplier supplier){
            return  mSupplierDao.getSingleSupplier(supplier.getSupplierid());
    }

    /**
     * Insert.
     *
     * @param supplier inserts supplier detail inn the database
     */
    public void insert (Supplier supplier) {
        saveSupplier(supplier);
    }


    /**
     * Insert supplier to db.
     *
     * @param supplier the supplier
     */
    public void insertToDb(Supplier supplier){
        disposable.add(Completable.fromAction(()->{
            mSupplierDao.insert(supplier) ;
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
     * send saves supplier request to api
     * @param supplier
     */
    private void saveSupplier(Supplier supplier){
        HashMap<String, Object> data = new HashMap<>();
        if(supplier.getSupplierid() == 0){
            data.put("businessid", supplier.getBusinessid());
            data.put("request_type", "addsupplier");
        }else{
            data.put("supplierid", supplier.getSupplierid());
            data.put("request_type", "editsupplier");
        }

        data.put("companyname", supplier.getCompanyname());
        data.put("contactname", supplier.getContactname());
        data.put("identificationnumber", supplier.getIdentificationnumber());
        data.put("countrycode", supplier.getCountrycode());
        data.put("country", supplier.getCountry());
        data.put("town", supplier.getTown());
        data.put("phonenumber", supplier.getPhonenumber());
        data.put("othernumber", supplier.getOthernumber());
        data.put("email", supplier.getEmail());
        data.put("otheremail", supplier.getOtheremail());



        // sending request to api
        disposable.add(
                ApiClient.getApi().supplierApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<SupplierResponse>() {
                            @Override
                            public void onSuccess(SupplierResponse supplierResponse) {
                                if (supplierResponse.getStatus().contentEquals("success")) {
                                    //myApiResponses.postValue(new MyApiResponses("apisave","success",supplierResponse.getData()));

                                    myApiResponses.postValue(new MyApiResponses("apisave","success",supplierResponse));
                                    if(supplier.getSupplierid() == 0){
                                        supplier.setSupplierid(supplierResponse.getSupplier().getSupplierid());
                                    }
                                    insertToDb(supplier);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apisave","fail",e));
                            }
                        })
        );
    }

    /**
     * fetches supplier records from api
     *
     * @param data request data
     */
    private void refreshSuppliers(HashMap<String,Object> data) {
        //sending request to api
        disposable.add(
                ApiClient.getApi().supplierApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<SupplierResponse>(){
                            @Override
                            public void onSuccess(SupplierResponse supplierResponse) {

                                if(supplierResponse.getStatus().contentEquals("success")){
                                    deleteAll();
                                    for(Supplier p:supplierResponse.getSupplierList()){
                                        insertToDb(p);
                                    }
                                    myApiResponses.postValue(new MyApiResponses("apirefresh","success",supplierResponse));
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
     * send deletes supplier request to api
     * @param supplier supplier object
     */
    private void deleteApiSupplier(Supplier supplier){
        HashMap<String, Object> data = new HashMap<>();
        data.put("request_type", "deletesupplier");
        data.put("supplierid", supplier.getSupplierid());
        data.put("businessid", supplier.getBusinessid());

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
                                    deleteFromDb(supplier);
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
     * Dispose q.
     */
    public void disposeQ() {
        disposable.dispose();
    }
}
