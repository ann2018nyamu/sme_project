package ke.co.eclectic.quickstore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ke.co.eclectic.quickstore.models.Store;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * STEP 2
 * DAO (data access object), you specify SQL queries and associate them with method calls
 * The DAO must be an interface or abstract class.
 *  Created by Manduku O. David on 21/12/2018.
 */
@Dao
public interface StoreDao {
    /**
     * Adds store to store table
     *
     * @param store the store
     */
    @Insert(onConflict = REPLACE)
    void insert(Store store);

    /**
     * Deleted all stores from table
     */
    @Query("DELETE FROM store")
    void deleteAll();

    /**
     * deletes a specific store from db based on the id
     *
     * @param id unique id of the store
     */
    @Query("DELETE FROM store WHERE storeid = :id ")
    void deleteSingleStore(Integer id);



    /**
     * fetches all store from store table
     * LiveData a lifecycle library class for data observation
     *
     * @return list of word object/records in database
     */
    @Query("SELECT * from store ORDER BY storeid ASC")
    LiveData<List<Store>> getAllStores();

    /**
     * fetches all business store from store table
     * LiveData a lifecycle library class for data observation
     *
     * @param bid the bid
     * @return list of word object/records in database
     */
    @Query("SELECT * from store WHERE businessid = :bid ORDER BY storeid ASC")
    LiveData<List<Store>> getBusinessStores(Integer bid);

    /**
     * fetched a specific store from db based on the id
     *
     * @param id unique id of the store
     * @return the single store
     */
    @Query("SELECT * FROM store WHERE storeid = :id ")
    LiveData<List<Store>> getSingleStore(Integer id);




}
