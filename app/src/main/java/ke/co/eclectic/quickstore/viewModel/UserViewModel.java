package ke.co.eclectic.quickstore.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.NetworkInfo;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.models.User;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.repository.UserRepository;
import timber.log.Timber;

/**
 * ViewModel holds your app's UI data in a lifecycle-conscious way
 * that survives configuration changes
 */
public class UserViewModel extends AndroidViewModel {
    private UserRepository mRepository;
    private LiveData<List<User>> mAllUsers;
    private  MutableLiveData<Boolean>  isInternetConnected = new MutableLiveData<>();
    private boolean listenToActiveInternet = false;
    Disposable internetDisposable;

    /**
     * Instantiates a new User view model.
     *
     * @param application the application
     */
    public UserViewModel(Application application) {
        super(application);
        mRepository = new UserRepository(application);
        mAllUsers = mRepository.getAllUsers();
        //initNetwork(application);
    }

    /**
     * initializes internet status observer
     * @param application application reference
     */
    private void initNetwork(Application application) {
        isInternetConnected.postValue(true);
        isInternetConnected.setValue(true);
        internetDisposable = ReactiveNetwork.observeNetworkConnectivity(application)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    final NetworkInfo.State state = connectivity.state();
                    final String name = connectivity.typeName();
                    Timber.v(String.format("state: %s, typeName: %s", state, name));
                });

          internetDisposable =  ReactiveNetwork.observeInternetConnectivity()
                .skip(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isConnected -> {
                    isInternetConnected.postValue(isConnected);
                    Timber.v("isConnected "+isConnected);
                    if(isConnected){
                        if(listenToActiveInternet){
                            Timber.v("Refresh user data ");
                        }
                        listenToActiveInternet = true;
                    }
                    Timber.v(isConnected.toString());
                });

    }

    /**
     * Get is internet connected mutable live data.
     *
     * @return the mutable live data
     */
    public MutableLiveData<Boolean> getIsInternetConnected(){
        return isInternetConnected;
    }

    /**
     * gets repository response code
     *
     * @return all errors
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return mRepository.getApiResponse();
    }

    /**
     * getter for all the users records
     *
     * @return all users
     */
    public LiveData<List<User>> getAllUsers() {
        return mAllUsers;
    }

    /**
     * Insert.
     *
     * @param user the user
     */
    public void insertToDb(User user) { mRepository.insertToDb(user); }

    /**
     * Delete all.
     */
    public void deleteAll() { mRepository.deleteAll(); }

    /**
     * Gets single user.
     *
     * @param user   the user
     * @param action the action
     * @return the single user
     */
    public LiveData<List<User>> getSingleUser(User user,String action) { return mRepository.getSingleUser(user,action); }

    /**
     * sends login request
     * @param user user object
     */
    public void loginUser(User user) {
        mRepository.loginUser(user);
    }

    /**
     * refreshes user details
     *
     */
    public void refreshUserDetails() {
        mRepository.refreshUserDetails();
    }
/**
     * send reset codes request
     * @param user user objects
     */
    public void resetpassword(User user) {
        mRepository.resetpassword(user);
    }

    /**
     * sets user passwords
     * @param user  user model
     */
    public void setpassword(User user) {
        mRepository.setpassword(user);
    }
    /**
     * resets user passwords
     * @param user user model
     */
    public void validateotp(User user) {
        mRepository.validateotp(user);
    }

    /**
     * sends save update request to api
     *
     * @param user  user model
     */
    public void saveUpdate(User user) {
        mRepository.saveUpdate(user);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.disposeQ();
    }

    /**
     * Removes store user.
     *
     * @param storeuserid the storeuserid
     */
    public void removeStoreUser(Integer storeuserid) {
        mRepository.removeStoreUser(storeuserid);
    }
    /**
     * Fetches store user by store user id
     *
     * @param storeuserid the storeuserid
     */
    public void getStoreUserDetail(Integer storeuserid) {
        mRepository.getStoreUserDetail(storeuserid);
    }

    public void deleteUser(Integer storeuserid) {
        mRepository.deleteUser(storeuserid);
    }

    public void getBusinessStaff() {
        mRepository.getBusinessStaff();
    }
}
