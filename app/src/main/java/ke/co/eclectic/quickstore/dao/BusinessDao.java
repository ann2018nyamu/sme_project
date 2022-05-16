package ke.co.eclectic.quickstore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ke.co.eclectic.quickstore.models.Business;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * STEP 2
 * DAO (data access object), you specify SQL queries and associate them with method calls
 * The DAO must be an interface or abstract class.
 *  Created by Manduku O. David on 21/12/2018.
 */
@Dao
public interface BusinessDao {
    /**
     * Adds business to business table
     *
     * @param business the business
     */
    @Insert(onConflict = REPLACE)
    void insert(Business business);

    /**
     * Deleted all businesss from table
     */
    @Query("DELETE FROM business")
    void deleteAll();

    /**
     * deletes a specific business from db based on the id
     *
     * @param id unique id of the business
     */
    @Query("DELETE FROM business WHERE businessid = :id ")
    void deleteSingleBusiness(Integer id);

    /**
     * fetches all business from business table
     * LiveData a lifecycle library class for data observation
     *
     * @return list of word object/records in database
     */
    @Query("SELECT * from business ORDER BY businessid ASC")
    LiveData<List<Business>> getAllBusinesss();

    /**
     * fetched a specific business from db based on the id
     *
     * @param id unique id of the business
     * @return the single business
     */
    @Query("SELECT * FROM business WHERE businessid = :id ")
    LiveData<List<Business>> getSingleBusiness(Integer id);


}
