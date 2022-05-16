package ke.co.eclectic.quickstore.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.dao.UserDao;
import ke.co.eclectic.quickstore.dao.QuickStoreRoomDb;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.User;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.UserResponse;
import timber.log.Timber;

/**
 * STEP 4
 * A Repository manages query threads and allows you to use multiple backends.
 * the Repository implements the logic for deciding whether to fetch data from a
 * network or use results cached in a local database.
 */
public class UserRepository {
    private UserDao mUserDao;
    private LiveData<List<User>> mAllUsers;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<MyApiResponses> myApiResponses = new MutableLiveData<MyApiResponses>();
    /**
     * The Dis.
     */
    Disposable dis = null;

    /**
     * gets a handle to the database and initializes the member variables
     *
     * @param application the application
     */
    public UserRepository(Application application) {
        QuickStoreRoomDb db = QuickStoreRoomDb.getDatabase(application);
        mUserDao = db.userDao();
        mAllUsers = mUserDao.getAllUsers();

    }

    /**
     *sends Log in request  to api.
     *
     * @param user the user
     */
    public void loginUser(User user){
        HashMap<String,String> data = new HashMap<>();
        data.put("request_type","login");
        data.put("phonenumber",user.getPhonenumber().replace("+",""));
        data.put("password",user.getPassword());
        CodingMsg.l("loginUser "+data.toString());
        disposable.add(
                ApiClient.getApi().api(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>(){
                            @Override
                            public void onSuccess(UserResponse userResponse) {

                                if(userResponse.getStatus().contentEquals("success")) {
                                    Timber.v("new Gson().toJson(GlobalVariable.getCurrentUser())");
                                    myApiResponses.postValue(new MyApiResponses("apilogin","success",userResponse));
                                    GlobalVariable.setCurrentUser(userResponse.getUser());
                                    insertToDb(userResponse.getUser());
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apilogin","fail",e));
                                //CodingMsg.t(getActivity(),e.getMessage());
                            }
                        })
        );

    }

    /**
     * sends  Signup request to api.
     *
     * @param user the user
     */
    public void saveUpdate(User user){

        //sending data to backend
        HashMap<String,String> data = new HashMap<>();
        data.put("request_type","signup");
        if(user.getUserid() != 0){
            data.put("request_type","updateuser");
        }

        data.put("firstname",user.getFirstname());
        data.put("othernames",user.getOthernames());
        data.put("middlename",user.getMiddlename());
        data.put("lastname",user.getLastname());
        data.put("phonenumber",user.getPhonenumber());
        data.put("email",user.getEmail());
        data.put("gender",user.getGender());
        data.put("dateofbirth",user.getDateofbirth());
        data.put("nationality",user.getNationality());
        data.put("nationalidnumber",user.getNationalidnumber());
        data.put("countrycode",user.getCountrycode().replace("+",""));

        disposable.add(
                ApiClient.getApi().api(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>(){
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                if(userResponse.getStatus().contentEquals("success")) {
                                    myApiResponses.postValue(new MyApiResponses("apisave","success",userResponse));
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
     * sends Set user password request to api.
     *
     * @param user the user
     */
    public void setpassword(User user){
        HashMap<String,String> data = new HashMap<>();

        data.put("request_type","reset");
        data.put("phonenumber",user.getPhonenumber().replace("+",""));
         data.put("password",user.getPassword());

        disposable.add(
                ApiClient.getApi().api(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>(){
                            @Override
                            public void onSuccess(UserResponse userResponse) {

                                if(userResponse.getStatus().contentEquals("success")) {
                                    Timber.v("new Gson().toJson(GlobalVariable.getCurrentUser())");
                                    myApiResponses.postValue(new MyApiResponses("apisetpassword","success",userResponse));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apisetpassword","fail",e));
                            }
                        })
        );

    }

    /**
     * Sends Refresh user details request to api.
     */
    public void refreshUserDetails(){
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","verifytoken");
        data.put("auth_token",GlobalVariable.getCurrentUser().getAuth_token().replaceAll("\"",""));

        disposable.add(
                ApiClient
                        .getApi()
                        .api(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>(){
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                if(userResponse.getStatus().contentEquals("success")) {
                                    Timber.v("new Gson().toJson(GlobalVariable.getCurrentUser())");
                                    User user =   userResponse.getRefreshedUserData();
                                    GlobalVariable.getCurrentUser().setRolesStr(user.getRolesStr());
                                    insertToDb(GlobalVariable.getCurrentUser());
                                    myApiResponses.postValue(new MyApiResponses("apirefresh","success",userResponse));
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
     * sends Validate otp request to api.
     *
     * @param user the user
     */
    public void validateotp(User user){
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","validateotp");
        data.put("phonenumber",user.getPhonenumber().replace("+",""));
        data.put("otp",Integer.parseInt(user.getPassword()));


        disposable.add(
                ApiClient.getApi().api(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>(){
                            @Override
                            public void onSuccess(UserResponse userResponse) {

                                if(userResponse.getStatus().contentEquals("success")) {
                                    Timber.v("new Gson().toJson(GlobalVariable.getCurrentUser())");
                                    myApiResponses.postValue(new MyApiResponses("apivalidateotp","success",userResponse));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apivalidateotp","fail",e));
                            }
                        })
        );

    }

    /**
     * sends Reset password request to api.
     *
     * @param user the user
     */
    public void resetpassword(User user){
        HashMap<String,String> data = new HashMap<>();
        data.put("request_type","change");
        data.put("phonenumber",user.getPhonenumber().replace("+",""));
       // data.put("password",user.getPassword());

        disposable.add(
                ApiClient.getApi().api(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>(){
                            @Override
                            public void onSuccess(UserResponse userResponse) {

                                if(userResponse.getStatus().contentEquals("success")) {
                                    Timber.v("new Gson().toJson(GlobalVariable.getCurrentUser())");
                                    myApiResponses.postValue(new MyApiResponses("apiresetpassword","success",userResponse));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apiresetpassword","fail",e));
                            }
                        })
        );

    }
    /**
     * Gets all repository network reesponse.
     *
     * @return MutableLiveData
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return myApiResponses;
    }

    /**
     * Gets all users.
     *
     * @return the all users
     */
    public LiveData<List<User>> getAllUsers() {
        return mAllUsers;
    }

    /**
     * Delete all users.
     */
    public void deleteAll(){
        deleteFromDb(0);

    }
    /**
     * Deletes all supplier attached to  unique business id from  database
     *
     * @param userid the supplier
     */
    public void deleteUser(Integer userid){
        HashMap<String, Object> data = new HashMap<>();
        data.put("request_type", "deleteuser");
        data.put("userid", userid);

        // sending request to api
        disposable.add(
                ApiClient.getApi().api(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                if (userResponse.getStatus().contentEquals("success")) {
                                    Timber.v("userResponse");
                                    myApiResponses.postValue(new MyApiResponses("apidelete","success",userResponse));
                                    deleteFromDb(userid);
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
     * Removes store user.
     *
     * @param storeuserid the store user id
     */
    public void removeStoreUser(Integer storeuserid) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("request_type", "removestoreuser");
        data.put("storeuserid", storeuserid);
        // sending request to api
        disposable.add(
                ApiClient.getApi().api(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                if (userResponse.getStatus().contentEquals("success")) {
                                    myApiResponses.postValue(new MyApiResponses("apiremove","success",userResponse));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apiremove","fail",e));
                            }
                        })
        );

    }
    /**
     * Fetches store user details.
     *
     * @param storeuserid the store user id
     */
    public void getStoreUserDetail(Integer storeuserid) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("request_type", "getstoreuser");
        data.put("storeuserid", storeuserid);
        // sending request to api
        disposable.add(
                ApiClient.getApi().api(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                if (userResponse.getStatus().contentEquals("success")) {
                                    myApiResponses.postValue(new MyApiResponses("apigetstoreuser","success",userResponse));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apigetstoreuser","fail",e));
                            }
                        })
        );
    }
    public void getBusinessStaff() {
        HashMap<String, Object>   data = new HashMap<>();
        data.put("request_type","getstaffbybusiness");
        data.put("businessid",GlobalVariable.getCurrentUser().getBusinessid() );

        disposable.add(
                ApiClient.getApi().api(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                if (userResponse.getStatus().contentEquals("success")) {
                                    myApiResponses.postValue(new MyApiResponses("apistaffrefresh","success",userResponse));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apistaffrefresh","fail",e));
                            }
                        })
        );
    }

    /**
     * Delete user from db.
     *
     * @param userid the user
     */
    public void deleteFromDb(Integer userid){
        disposable.add(Completable.fromAction(()->{
            if(userid == 0){
                mUserDao.deleteAll();
            }else{
                mUserDao.deleteSingleUser(userid);
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
     * Get single user live data.
     *
     * @param user   the user
     * @param action the action
     * @return the live data
     */
    public LiveData<List<User>>  getSingleUser(User user,String action){

        if(action.contentEquals("login")){
            return  mUserDao.getSingleUser(user.getEmail(),user.getPhonenumber(),user.getPassword());
        }else{
            return  mUserDao.getSingleUser(user.getUserid());
        }
    }



    /**
     * Insert to localdb
     *
     * @param user the user
     */
    public void insertToDb(User user){
        disposable.add(Completable.fromAction(()->{
            mUserDao.insert(user) ;
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
     * clears disposable memory
     */
    public void disposeQ() {
        disposable.clear();
    }



}
