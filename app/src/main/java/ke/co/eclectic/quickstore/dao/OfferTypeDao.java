package ke.co.eclectic.quickstore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ke.co.eclectic.quickstore.models.OfferType;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * STEP 2
 * DAO (data access object), you specify SQL queries and associate them with method calls
 * The DAO must be an interface or abstract class.
 *  Created by Manduku O. David on 21/12/2018.
 */
@Dao
public interface OfferTypeDao {
    /**
     * Adds offerType to offerType table
     *
     * @param offerType the offertype data
     */
    @Insert(onConflict = REPLACE)
    void insert(OfferType offerType);

    /**
     * Deleted all offertype from table
     */
    @Query("DELETE FROM offertype")
    void deleteAll();

    /**
     * deletes a specific offerType from db based on the id
     *
     * @param offertypeid unique id of the offertype
     */
    @Query("DELETE FROM offertype WHERE offertypeid = :offertypeid ")
    void deleteSingleOfferType(Integer offertypeid);

    /**
     * fetches all offerType from offerType table
     * LiveData a lifecycle library class for data observation
     *
     * @return list of word object/records in database
     */
    @Query("SELECT * from offertype ORDER BY offertypeid ASC")
    LiveData<List<OfferType>> getAllOfferTypes();

    /**
     * fetched a specific offerType from db based on the id
     *
     * @param offertypeid unique code of the offerType
     * @return the single offertype data
     */
    @Query("SELECT * FROM offertype WHERE offertypeid = :offertypeid ")
    LiveData<List<OfferType>> getSingleOfferType(Integer offertypeid);


}
