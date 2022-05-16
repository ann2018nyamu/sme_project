package ke.co.eclectic.quickstore.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import ke.co.eclectic.quickstore.models.PaymentType;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.repository.PaymentTypeRepository;

/**
 * ViewModel holds your app's UI data in a lifecycle-conscious way
 * that survives configuration changes
 */
public class PaymentTypeViewModel extends AndroidViewModel {
    private PaymentTypeRepository mRepository;

    /**
     * Instantiates a new PaymentType view model.
     *
     * @param application the application
     */
    public PaymentTypeViewModel(Application application) {
        super(application);
        mRepository = new PaymentTypeRepository(application);
    }

    /**
     * gets api responses
     *
     * @return all errors
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return mRepository.getApiResponse();
    }
    /**
     * getter for all the payment Type records
     *
     * @return all countries
     */
    public LiveData<List<PaymentType>> getAllPaymentTypes() {
        return mRepository.getAllpaymentTypes();
    }

    /**
     * Delete all paymentType.
     */
    public void deleteAll() { mRepository.deleteAll(); }

    /**
     * Gets single paymentType unit.
     *
     * @param paymentType the unit
     * @param action  the action
     * @return the single unit
     */
    public LiveData<List<PaymentType>> getSinglePaymentType(PaymentType paymentType,String action) { return mRepository.getSinglePaymentType(paymentType); }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.disposeQ();
    }
}
