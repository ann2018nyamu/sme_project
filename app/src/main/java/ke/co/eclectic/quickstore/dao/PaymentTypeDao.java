package ke.co.eclectic.quickstore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ke.co.eclectic.quickstore.models.PaymentType;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * STEP 2
 * DAO (data access object), you specify SQL queries and associate them with method calls
 * The DAO must be an interface or abstract class.
 *  Created by Manduku O. David on 21/12/2018.
 */
@Dao
public interface PaymentTypeDao {
    /**
     * Adds paymenttypeData to paymenttypeData table
     *
     * @param paymenttypeData the paymenttype data
     */
    @Insert(onConflict = REPLACE)
    void insert(PaymentType paymenttypeData);

    /**
     * Deleted all paymenttype from table
     */
    @Query("DELETE FROM paymenttype")
    void deleteAll();

    /**
     * deletes a specific paymenttypeData from db based on the id
     *
     * @param paymenttypeid unique id of the paymenttype
     */
    @Query("DELETE FROM paymenttype WHERE paymenttypeid = :paymenttypeid ")
    void deleteSinglePaymentType(Integer paymenttypeid);

    /**
     * fetches all paymenttypeData from paymenttypeData table
     * LiveData a lifecycle library class for data observation
     *
     * @return list of word object/records in database
     */
    @Query("SELECT * from paymenttype ORDER BY paymenttypeid ASC")
    LiveData<List<PaymentType>> getAllPaymentTypes();

    /**
     * fetched a specific paymenttypeData from db based on the id
     *
     * @param paymenttypeid unique code of the paymenttypeData
     * @return the single paymenttype data
     */
    @Query("SELECT * FROM paymenttype WHERE paymenttypeid = :paymenttypeid ")
    LiveData<List<PaymentType>> getSinglePaymentType(Integer paymenttypeid);


}
