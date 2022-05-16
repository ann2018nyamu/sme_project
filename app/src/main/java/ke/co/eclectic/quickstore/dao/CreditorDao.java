package ke.co.eclectic.quickstore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ke.co.eclectic.quickstore.models.Creditor;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * STEP 2
 * DAO (data access object), you specify SQL queries and associate them with method calls
 * The DAO must be an interface or abstract class.
 *  Created by Manduku O. David on 21/12/2018.
 */
@Dao
public interface CreditorDao {
    /**
     * Adds creditorObj to creditorObj table
     *
     * @param creditorObj the creditor data
     */
    @Insert(onConflict = REPLACE)
    void insert(Creditor creditorObj);

    /**
     * Deleted all creditor from table
     */
    @Query("DELETE FROM creditor")
    void deleteAll();

    /**
     * deletes a specific creditorObj from db based on the id
     *
     * @param creditorid unique name of the creditor
     */
    @Query("DELETE FROM creditor WHERE creditorid = :creditorid ")
    void deleteSingleCreditor(int creditorid);

    /**
     * fetches all creditorObj from creditorObj table
     * LiveData a lifecycle library class for data observation
     *
     * @return list of word object/records in database
     */
    @Query("SELECT * from creditor ORDER BY creditorid DESC")
    LiveData<List<Creditor>> getAllCreditors();

    /**
     * fetched a specific creditorObj from db based on the id
     *
     * @param creditorid unique id of the creditorObj
     * @return the single creditor data
     */
    @Query("SELECT * FROM creditor WHERE creditorid = :creditorid ")
    LiveData<List<Creditor>> getSingleCreditor(Integer creditorid);


}
