package ke.co.eclectic.quickstore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ke.co.eclectic.quickstore.models.Customer;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * STEP 2
 * DAO (data access object), you specify SQL queries and associate them with method calls
 * The DAO must be an interface or abstract class.
 *  Created by Manduku O. David on 21/12/2018.
 */
@Dao
public interface CustomerDao {
    /**
     * Adds customerObj to customerObj table
     *
     * @param customerObj the customer data
     */
    @Insert(onConflict = REPLACE)
    void insert(Customer customerObj);

    /**
     * Deleted all customer from table
     */
    @Query("DELETE FROM customer")
    void deleteAll();

    /**
     * deletes a specific customerObj from db based on the id
     *
     * @param customerid unique name of the customer
     */
    @Query("DELETE FROM customer WHERE customerid = :customerid ")
    void deleteSingleCustomer(int customerid);

    /**
     * fetches all customerObj from customerObj table
     * LiveData a lifecycle library class for data observation
     *
     * @return list of word object/records in database
     */
    @Query("SELECT * from customer ORDER BY customerid DESC")
    LiveData<List<Customer>> getAllCustomers();

    /**
     * fetched a specific customerObj from db based on the id
     *
     * @param customerid unique id of the customerObj
     * @return the single customer data
     */
    @Query("SELECT * FROM customer WHERE customerid = :customerid ")
    LiveData<List<Customer>> getSingleCustomer(Integer customerid);


}
