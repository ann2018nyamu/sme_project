package ke.co.eclectic.quickstore.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.dao.CreditorDao;
import ke.co.eclectic.quickstore.dao.QuickStoreRoomDb;
import ke.co.eclectic.quickstore.models.Creditor;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.CreditorResponse;
import timber.log.Timber;

/**
 * STEP 4
 * A Repository manages query threads and allows you to use multiple backends.
 * the Repository implements the logic for deciding whether to fetch data from a
 * network or use results cached in a local database.
 */

public class CreditorRepository {
    public Double totalAmount=0.00;
    private CreditorDao mCreditorDao;
    private LiveData<List<Creditor>> mAllCreditor;
    private MutableLiveData<MyApiResponses> myApiResponses = new MutableLiveData<MyApiResponses>();
    private MutableLiveData<List<Creditor>> myDummyCreditors = new MutableLiveData<List<Creditor>>();
    private CompositeDisposable disposable = new CompositeDisposable();

    /**
     * gets a handle to the database and initializes the member variables
     *
     * @param application the application
     */
    public CreditorRepository(Application application) {
        QuickStoreRoomDb db = QuickStoreRoomDb.getDatabase(application);
        mCreditorDao = db.creditorDao();
        mAllCreditor = mCreditorDao.getAllCreditors();
    }

    /**
     * Deletes all creditor form database
     */
    public void deleteAll() {
        deleteFromDb(new Creditor());
    }

    /**
     * Deletes all creditor attached to  unique business id from  database
     *
     * @param creditor the creditor
     */
    public void deleteCreditor(Creditor creditor) {
        deleteApiCreditor(creditor);
    }

    /**
     * Delete from db.
     *
     * @param creditor the creditor
     */
    public void deleteFromDb(Creditor creditor) {
        disposable.add(Completable.fromAction(() -> {
            if (creditor.getCreditorid() == 0) {
                mCreditorDao.deleteAll();
            } else {
                mCreditorDao.deleteSingleCreditor(creditor.getCreditorid());
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        myApiResponses.postValue(new MyApiResponses("dbdelete", "success", new JsonObject()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        myApiResponses.postValue(new MyApiResponses("dbdelete", "error", e));
                    }
                }));
    }


    /**
     * Gets all creditors.
     *
     * @return all creditors
     */
    public LiveData<List<Creditor>> getAllCreditors(HashMap<String, Object> data) {
        refreshCreditors(data);
        myDummyCreditors.setValue(dummyData());
        return mAllCreditor;
    }
    /**
     * dummy data for testing purposes
     */
    public List<Creditor> dummyData(){
         List<Creditor> creditorLists = new ArrayList<>();

        Creditor creditor = new Creditor();
        creditor.setCreditorid(1);
        creditor.setCreditorname("Equity Bank");
        creditor.setAmount(totalAmount);
        creditor.setInterest(10);
        creditorLists.add(creditor);
        insertToDb(creditor);

        creditor = new Creditor();
        creditor.setCreditorid(2);
        creditor.setCreditorname("Stanbic Bank");
        creditor.setAmount(totalAmount);
        creditor.setInterest(12);
        insertToDb(creditor);

        creditor = new Creditor();
        creditor.setCreditorid(3);
        creditor.setCreditorname("National Bank");
        creditor.setAmount(totalAmount);
        creditor.setInterest(8);
        insertToDb(creditor);

        return creditorLists;
    }

    /**
     * Gets all repository network response.
     *
     * @return all creditors
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return myApiResponses;
    }

    /**
     * Get single creditor live data.
     *
     * @param creditor details/information
     * @return a single creditor instance/record
     */
    public LiveData<List<Creditor>> getSingleCreditor(Creditor creditor) {
        return mCreditorDao.getSingleCreditor(creditor.getCreditorid());
    }

    /**
     * saves creditor to db/send save request to api
     *
     * @param creditor inserts creditor detail inn the database
     */
    public void insert(Creditor creditor) {
        saveCreditor(creditor);
    }


    /**
     * Insert creditor record to  local db.
     *
     * @param creditor the creditor
     */
    public void insertToDb(Creditor creditor) {
        disposable.add(Completable.fromAction(() -> {
            mCreditorDao.insert(creditor);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        myApiResponses.postValue(new MyApiResponses("dbsave", "success", new JsonObject()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        myApiResponses.postValue(new MyApiResponses("dbsave", "fail", e));
                    }
                }));
    }

    /**
     * send creditor save request to api
     *
     * @param creditor creditor record to save
     */
    private void saveCreditor(Creditor creditor) {
        HashMap<String, Object> data = new HashMap<>();
        if (creditor.getCreditorid() == 0) {
            data.put("businessid", creditor.getCreditorid());
            data.put("request_type", "addcreditor");
        } else {
            data.put("creditorid", creditor.getCreditorid());
            data.put("request_type", "editcreditor");
        }

        data.put("creditorname", creditor.getCreditorname());
        data.put("amount", creditor.getAmount());
        data.put("amountPayable", creditor.getAmountPayable());
        data.put("duration", creditor.getDuration());
        data.put("interest", creditor.getInterest());

        // sending request to api
        disposable.add(
                ApiClient.getApi().creditorApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CreditorResponse>() {
                            @Override
                            public void onSuccess(CreditorResponse creditorResponse) {
                                if (creditorResponse.getStatus().contentEquals("success")) {
                                    //myApiResponses.postValue(new MyApiResponses("apisave","success",creditorResponse.getData()));
                                    myApiResponses.postValue(new MyApiResponses("apisave", "success", creditorResponse));
                                    if (creditor.getCreditorid() == 0) {
                                        creditor.setCreditorid(creditorResponse.getCreditor().getCreditorid());
                                    }
                                    insertToDb(creditor);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apisave", "fail", e));
                            }
                        })
        );

    }

    /**
     * fetches creditor record from api
     * @param data request data to send
     */
    private void refreshCreditors(HashMap<String, Object> data) {
        //sending request to api
//        disposable.add(
//                ApiClient.getApi().creditorApi(new JsonParser().parse(new Gson().toJson(data)))
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeWith(new DisposableSingleObserver<CreditorResponse>() {
//                            @Override
//                            public void onSuccess(CreditorResponse creditorResponse) {
//
//                                if (creditorResponse.getStatus().contentEquals("success")) {
//                                    deleteAll();
//                                    for (Creditor p : creditorResponse.getCreditorList()) {
//                                        insertToDb(p);
//                                    }
//                                    Timber.v("creditorResponse :  " + creditorResponse.getCreditorList().size());
//                                    myApiResponses.postValue(new MyApiResponses("apirefresh", "success", creditorResponse));
//                                }
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                myApiResponses.postValue(new MyApiResponses("apirefresh", "fail", e));
//                            }
//                        })
//        );
    }

    /**
     * send deletes creditor request to api
     * @param creditor
     */
    private void deleteApiCreditor(Creditor creditor) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("request_type", "deletecreditor");
        data.put("creditorid", creditor.getCreditorid());

        // sending request to api
        disposable.add(
                ApiClient.getApi().creditorApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CreditorResponse>() {
                            @Override
                            public void onSuccess(CreditorResponse creditorResponse) {
                                if (creditorResponse.getStatus().contentEquals("success")) {
                                    Timber.v("creditorResponse");
                                    myApiResponses.postValue(new MyApiResponses("apidelete", "success", creditorResponse));
                                    deleteFromDb(creditor);
                                    //GlobalMethod.goToActivity(AddCreditorActivity.this, MenuActivity.class);
                                }
                            }
                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apidelete", "fail", e));
                            }
                        })
        );
    }

    /**
     * clears memory used by disposable/network request observable
     */
    public void disposeQ() {
        disposable.dispose();
    }
}
