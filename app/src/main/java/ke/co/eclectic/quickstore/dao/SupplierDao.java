package ke.co.eclectic.quickstore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ke.co.eclectic.quickstore.models.Supplier;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * STEP 2
 * DAO (data access object), you specify SQL queries and associate them with method calls
 * The DAO must be an interface or abstract class.
 *  Created by Manduku O. David on 21/12/2018.
 */
@Dao
public interface SupplierDao {
    /**
     * Adds supplierObj to supplierObj table
     *
     * @param supplierObj the supplier data
     */
    @Insert(onConflict = REPLACE)
    void insert(Supplier supplierObj);

    /**
     * Deleted all supplier from table
     */
    @Query("DELETE FROM supplier")
    void deleteAll();

    /**
     * deletes a specific supplierObj from db based on the id
     *
     * @param supplierid unique name of the supplier
     */
    @Query("DELETE FROM supplier WHERE supplierid = :supplierid ")
    void deleteSingleSupplier(int supplierid);

    /**
     * fetches all supplierObj from supplierObj table
     * LiveData a lifecycle library class for data observation
     *
     * @return list of word object/records in database
     */
    @Query("SELECT * from supplier ORDER BY supplierid DESC")
    LiveData<List<Supplier>> getAllSuppliers();

    /**
     * fetched a specific supplierObj from db based on the id
     *
     * @param supplierid unique id of the supplierObj
     * @return the single supplier data
     */
    @Query("SELECT * FROM supplier WHERE supplierid = :supplierid ")
    LiveData<List<Supplier>> getSingleSupplier(Integer supplierid);


}
