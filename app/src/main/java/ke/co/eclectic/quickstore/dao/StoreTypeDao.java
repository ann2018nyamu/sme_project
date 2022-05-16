package ke.co.eclectic.quickstore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ke.co.eclectic.quickstore.models.StoreType;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * STEP 2
 * DAO (data access object), you specify SQL queries and associate them with method calls
 * The DAO must be an interface or abstract class.
 *  Created by Manduku O. David on 21/12/2018.
 */
@Dao
public interface StoreTypeDao {
    /**
     * Adds storeTypeto storeType table
     *
     * @param storeType the store type
     */
    @Insert(onConflict = REPLACE)
    void insert(StoreType storeType);

    /**
     * Deleted all stores from table
     */
    @Query("DELETE FROM store")
    void deleteAll();

    /**
     * deletes a specific storeType from db based on the id
     *
     * @param typeid unique id of the store
     */
    @Query("DELETE FROM storetype WHERE typeid = :typeid ")
    void deleteSingleStoreType(String typeid);


    /**
     * fetches all storeType from storeType table
     * LiveData a lifecycle library class for data observation
     *
     * @return list of store type object/records in database
     */
    @Query("SELECT * from storetype ORDER BY storetype ASC")
    LiveData<List<StoreType>> getAllStoresType();


    /**
     * fetched a specific storeTypefrom db based on the id
     *
     * @param typeid unique id of the storeType
     * @return the single store type
     */
    @Query("SELECT * FROM storetype WHERE typeid = :typeid ")
    LiveData<List<StoreType>> getSingleStoreType(Integer typeid);




}
