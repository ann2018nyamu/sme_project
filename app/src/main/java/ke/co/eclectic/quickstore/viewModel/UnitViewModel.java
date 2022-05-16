package ke.co.eclectic.quickstore.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.List;

import ke.co.eclectic.quickstore.models.Unit;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.repository.UnitRepository;

/**
 * ViewModel holds your app's UI data in a lifecycle-conscious way
 * that survives configuration changes
 */
public class UnitViewModel extends AndroidViewModel {
    private UnitRepository mRepository;

    /**
     * Instantiates a new Unit view model.
     *
     * @param application the application
     */
    public UnitViewModel(Application application) {
        super(application);
        mRepository = new UnitRepository(application);
    }

    /**
     * gets respository api responses
     *
     * @return all errors
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return mRepository.getApiResponse();
    }

    /**
     * getter for all the units records
     *
     * @return all countries
     * @param data unit list livedata
     */
    public LiveData<List<Unit>> getAllUnits(HashMap<String, Object> data) {
        return mRepository.getAllunits(data);
    }

    /**
     * Insert units to db.
     *
     * @param unit the unit
     */
    public void insert(Unit unit) { mRepository.insert(unit); }

    /**
     * Delete all units records.
     */
    public void deleteAll() { mRepository.deleteAll(); }

    /**
     * Gets single unit.
     *
     * @param unit the unit
     * @param action  the action
     * @return the single unit
     */
    public LiveData<List<Unit>> getSingleUnit(Unit unit,String action) { return mRepository.getSingleUnit(unit); }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.disposeQ();
    }
}
