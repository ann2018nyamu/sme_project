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
import ke.co.eclectic.quickstore.dao.QuickStoreRoomDb;
import ke.co.eclectic.quickstore.dao.InventoryStockDao;
import ke.co.eclectic.quickstore.models.InventoryStock;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.InventoryStockResponse;
import timber.log.Timber;

/**
 * STEP 4
 * A Repository manages query threads and allows you to use multiple backends.
 * the Repository implements the logic for deciding whether to fetch data from a
 * network or use results cached in a local database.
 */
public class InventoryStockRepository {
    private InventoryStockDao mInventoryStockDao;
    private LiveData<List<InventoryStock>> mAllInventoryStock;
    private MutableLiveData<MyApiResponses> myApiResponses = new MutableLiveData<MyApiResponses>();
    private CompositeDisposable disposable = new CompositeDisposable();
    /**
     * gets a handle to the database and initializes the member variables
     *
     * @param application the application
     */
    public InventoryStockRepository(Application application) {
        QuickStoreRoomDb db = QuickStoreRoomDb.getDatabase(application);
        mInventoryStockDao = db.inventoryStockDao();
        mAllInventoryStock = mInventoryStockDao.getAllInventoryStocks();

    }

    /**
     * Deletes all inventory stock form db
     */
    public void deleteAll(){
        deleteFromDb(new InventoryStock());
    }

    /**
     * Deletes all inventory stock attached to  unique  id from  database
     *
     * @param inventoryStock the inventory stock
     */
    public void deleteInventoryStock(InventoryStock inventoryStock){
        deleteApiInventoryStock(inventoryStock);
    }
   
        public void deleteFromDb(InventoryStock inventoryStock){
            disposable.add(Completable.fromAction(()->{
                if(inventoryStock.getInventoryid() == 0){
                    mInventoryStockDao.deleteAll();
                }else{
                    mInventoryStockDao.deleteSingleInventoryStock(inventoryStock.getInventoryid());
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
     * fetches inventory stock from db/api
     *
     * @param data  request data
     * @return inventory stock live data
     */
    public LiveData<List<InventoryStock>> getAllInventoryStocks(HashMap<String,Object> data) {
        refreshInventoryStocks(data);
        return mAllInventoryStock;
    }

    /**
     * Gets all repository network reesponse.
     *
     * @return all mutable responses
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return myApiResponses;
    }

    /**
     * Get single inventory stock .
     *
     * @param inventoryStock stock details/information
     * @return a single inventory stock instance/record
     */
    public LiveData<List<InventoryStock>>  getSingleInventoryStock(InventoryStock inventoryStock){
            return  mInventoryStockDao.getSingleInventoryStock(inventoryStock.getInventoryid());
    }

    /**
     * saves inventory stock to db/api.
     *
     * @param inventoryStock inserts supplier detail inn the database
     */
    public void insert (InventoryStock inventoryStock) {
        saveInventoryStock(inventoryStock);
    }

    /**
     * Insert inventory stock to db.
     *
     * @param inventoryStock the inventory stock
     */
    public void insertToDb(InventoryStock inventoryStock){
        disposable.add(Completable.fromAction(()->{
            mInventoryStockDao.insert(inventoryStock) ;
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
     * sends inventory stock save/update request
     *
     * @param inventoryStock inventory stock instance
     */
    private void saveInventoryStock(InventoryStock inventoryStock){
        HashMap<String, Object> data = new HashMap<>();
        if(inventoryStock.getInventoryid() == 0){
            data.put("request_type", "createinventory");
        }else{
            data.put("request_type", "editinventory");
        }

        data.put("inventoryid", inventoryStock.getInventoryid());
        data.put("purchaseprice", inventoryStock.getPurchaseprice());
        data.put("saleprice", inventoryStock.getSaleprice());
        data.put("offertypeid", inventoryStock.getOffertypeid());
        data.put("offeramount", inventoryStock.getOfferamount());
        data.put("minquantity", inventoryStock.getMinquantity());
        data.put("quantity", inventoryStock.getQuantity());
        data.put("storeid", inventoryStock.getStoreid());
        data.put("productid", inventoryStock.getProductid());

        // sending request to api
        disposable.add(
                ApiClient.getApi().inventoryStockApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<InventoryStockResponse>() {
                            @Override
                            public void onSuccess(InventoryStockResponse supplierResponse) {
                                if (supplierResponse.getStatus().contentEquals("success")) {
                                    Timber.v("supplierResponse");
                                    //myApiResponses.postValue(new MyApiResponses("apisave","success",supplierResponse.getData()));
                                    myApiResponses.postValue(new MyApiResponses("apisave","success",supplierResponse));

                                     insertToDb(inventoryStock);
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
     * retrieves all invetnory stock from api
     *
     * @param data request data
     */

    private void refreshInventoryStocks(HashMap<String,Object> data) {
        //sending request to api
        disposable.add(
                ApiClient.getApi().inventoryStockApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<InventoryStockResponse>(){
                            @Override
                            public void onSuccess(InventoryStockResponse inventoryStockResponse) {
                                if(inventoryStockResponse.getStatus().contentEquals("success")){
                                    myApiResponses.postValue(new MyApiResponses("apirefresh","success",inventoryStockResponse));

                                    deleteAll();
                                    for(InventoryStock p:inventoryStockResponse.getInventoryStockList()){
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
     * send deletes inventory stock request to api
     * @param inventoryStock
     */

    private void deleteApiInventoryStock(InventoryStock inventoryStock){
        HashMap<String, Object> data = new HashMap<>();
        data.put("request_type", "deletesupplier");
        data.put("inventoryid", inventoryStock.getInventoryid());

        // sending request to api
        disposable.add(
                ApiClient.getApi().inventoryStockApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<InventoryStockResponse>() {
                            @Override
                            public void onSuccess(InventoryStockResponse supplierResponse) {
                                if (supplierResponse.getStatus().contentEquals("success")) {
                                    Timber.v("supplierResponse");
                                    myApiResponses.postValue(new MyApiResponses("apidelete","success",supplierResponse));
                                    deleteFromDb(inventoryStock);
                                    //GlobalMethod.goToActivity(AddInventoryStockActivity.this, MenuActivity.class);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apidelete","success",e));
                            }
                        })
        );
    }

    /**
     * disposes observable memory
     */
    public void disposeQ() {
        disposable.dispose();
    }
}
