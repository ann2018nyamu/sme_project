package ke.co.eclectic.quickstore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ke.co.eclectic.quickstore.models.SalesOrder;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * STEP 2
 * DAO (data access object), you specify SQL queries and associate them with method calls
 * The DAO must be an interface or abstract class.
 *  Created by Manduku O. David on 21/12/2018.
 */
@Dao
public interface SalesOrderDao {
    /**
     * Adds salesOrderObj to salesOrderObj table
     *
     * @param salesOrderObj the salesorder data
     */
    @Insert(onConflict = REPLACE)
    void insert(SalesOrder salesOrderObj);

    /**
     * Deleted all salesorder from table
     */
    @Query("DELETE FROM salesorder")
    void deleteAll();

    /**
     * deletes a specific salesOrderObj from db based on the id
     *
     * @param salesorderid unique name of the salesorder
     */
    @Query("DELETE FROM salesorder WHERE salesorderid = :salesorderid ")
    void deleteSingleSalesOrder(int salesorderid);

    /**
     * fetches all salesOrderObj from salesOrderObj table
     * LiveData a lifecycle library class for data observation
     *
     * @return list of word object/records in database
     */
    @Query("SELECT * from salesorder ORDER BY salesorderid DESC")
    LiveData<List<SalesOrder>> getAllSalesOrders();

    /**
     * fetched a specific salesOrderObj from db based on the id
     *
     * @param salesorderid unique id of the salesOrderObj
     * @return the single salesorder data
     */
    @Query("SELECT * FROM salesorder WHERE salesorderid = :salesorderid ")
    LiveData<List<SalesOrder>> getSingleSalesOrder(Integer salesorderid);

    /**
     * Updating only price
     * By order id
     */
    @Query("UPDATE salesorder SET productListStr=:productE WHERE salesorderid = :salesorderid")
    void updateProductItems(Integer salesorderid, String productE);

}
