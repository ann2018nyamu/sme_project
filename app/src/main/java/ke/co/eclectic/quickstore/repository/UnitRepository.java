package ke.co.eclectic.quickstore.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

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
import ke.co.eclectic.quickstore.dao.UnitDao;
import ke.co.eclectic.quickstore.dao.QuickStoreRoomDb;
import ke.co.eclectic.quickstore.models.Unit;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.UnitResponse;
import timber.log.Timber;

/**
 * STEP 4
 * A Repository manages query threads and allows you to use multiple backends.
 * the Repository implements the logic for deciding whether to fetch data from a
 * network or use results cached in a local database.
 */
public class UnitRepository {
    private UnitDao mUnitDao;
    private LiveData<List<Unit>> mAllUnit;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<MyApiResponses> myApiResponses = new MutableLiveData<MyApiResponses>();


    /**
     * gets a handle to the database and initializes the member variables
     *
     * @param application the application
     */
    public UnitRepository(Application application) {
        Context context = application.getBaseContext();
        QuickStoreRoomDb db = QuickStoreRoomDb.getDatabase(application);
        mUnitDao = db.unitDao();
        mAllUnit = mUnitDao.getAllUnits();
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
     * Deletes all unit form database
     */
    public void deleteAll(){
        deleteFromDb(new Unit());
    }

    /**
     * Deletes all unit attached to  unique business id from  database
     *
     * @param unit the unit
     */
    public void deleteUnit(Unit unit){
        deleteFromDb(unit);

    }


    /**
     * Gets all units from db and initiates request to server to sync data.
     * @return all units
     * @param data livedata of unit object
     */
    public LiveData<List<Unit>> getAllunits(HashMap<String, Object> data) {
        refreshUnits(data);
        return mAllUnit;
    }

    /**
     * fetches unit from api
     *
     * @param data
     */
    private void refreshUnits(HashMap<String, Object> data){
        //sending request to api
        disposable.add(
                ApiClient.getApi().unitApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UnitResponse>(){
                            @Override
                            public void onSuccess(UnitResponse unitResponse) {
                                if(unitResponse.getStatus().contentEquals("success")){
                                    deleteAll();
                                    for(Unit u:unitResponse.getUnitList()){
                                        insertToDb(u);
                                    }
                                    myApiResponses.postValue(new MyApiResponses("apirefresh","success",unitResponse));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apirefresh","fail",e));
                                //CodingMsg.t(getActivity(),e.getMessage());
                            }
                        })
        );
    }



    /**
     * Get single unit live data.
     *
     * @param unit details/information
     * @return a single unit instance/record
     */
    public LiveData<List<Unit>>  getSingleUnit(Unit unit){
            return  mUnitDao.getSingleUnit(unit.getUnitname());
    }


    /**
     * Insert unit to local db.
     *
     * @param unit inserts unit detail inn the database
     */
    public void insert (Unit unit) {
        saveUnit(unit);
    }

    /**
     * sends Save unit request to api.
     *
     * @param unit the unit
     */
    public void saveUnit(Unit unit){
        //Todo transfer this code to unit repository
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","addunits");
        data.put("unitname",unit.getUnitname());
        data.put("businessid",unit.getBusinessid());
        // sending request to api
        disposable.add(
                ApiClient.getApi().unitApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UnitResponse>(){
                            @Override
                            public void onSuccess(UnitResponse unitResponse) {
                                if(unitResponse.getStatus().contentEquals("success")){

                                    myApiResponses.postValue(new MyApiResponses("apisave","success",unitResponse));
                                    if(unit.getUnitid() == 0){
                                        unit.setUnitid(unitResponse.getUnit().getUnitid());
                                    }
                                    insertToDb(unit);
                                    Timber.v("UnitResponse");
                                    Timber.v(new Gson().toJson(unitResponse.getData()));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apisave","fail",e));

                                //CodingMsg.t(getActivity(),e.getMessage());
                            }
                        })
        );
    }

    /**
     * Insert to unit  db.
     *
     * @param unit the unit
     */
    public void insertToDb(Unit unit){
        disposable.add(Completable.fromAction(()->{
            mUnitDao.insert(unit) ;
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
     * Delete unit from local db.
     *
     * @param unit the unit
     */
    public void deleteFromDb(Unit unit){
        disposable.add(Completable.fromAction(()->{
            if(unit.getUnitname().contentEquals("")){
                mUnitDao.deleteSingleUnit(unit.getUnitname());
            }else{
                mUnitDao.deleteAll();
            }

        })
                .subscribeOn(Schedulers.io())
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
     * clears memory used by disposable
     */
    public void disposeQ() {
        disposable.clear();
    }
}
