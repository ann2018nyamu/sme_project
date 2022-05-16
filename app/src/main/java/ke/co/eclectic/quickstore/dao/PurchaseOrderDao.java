package ke.co.eclectic.quickstore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ke.co.eclectic.quickstore.models.PurchaseOrder;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * STEP 2
 * DAO (data access object), you specify SQL queries and associate them with method calls
 * The DAO must be an interface or abstract class.
 *  Created by Manduku O. David on 21/12/2018.
 */
@Dao
public interface PurchaseOrderDao {
    /**
     * Adds purchaseOrderObj to purchaseOrderObj table
     *
     * @param purchaseOrderObj the purchaseorder data
     */
    @Insert(onConflict = REPLACE)
    void insert(PurchaseOrder purchaseOrderObj);

    /**
     * Deleted all purchaseorder from table
     */
    @Query("DELETE FROM purchaseorder")
    void deleteAll();

    /**
     * deletes a specific purchaseOrderObj from db based on the id
     *
     * @param purchaseorderid unique name of the purchaseorder
     */
    @Query("DELETE FROM purchaseorder WHERE purchaseorderid = :purchaseorderid ")
    void deleteSinglePurchaseOrder(int purchaseorderid);

    /**
     * fetches all purchaseOrderObj from purchaseOrderObj table
     * LiveData a lifecycle library class for data observation
     *
     * @return list of word object/records in database
     */
    @Query("SELECT * from purchaseorder ORDER BY purchaseorderid DESC")
    LiveData<List<PurchaseOrder>> getAllPurchaseOrders();

    /**
     * fetched a specific purchaseOrderObj from db based on the id
     *
     * @param purchaseorderid unique id of the purchaseOrderObj
     * @return the single purchaseorder data
     */
    @Query("SELECT * FROM purchaseorder WHERE purchaseorderid = :purchaseorderid ")
    LiveData<List<PurchaseOrder>> getSinglePurchaseOrder(Integer purchaseorderid);

    /**
     * Updating only price
     * By order id
     */
    @Query("UPDATE purchaseorder SET productListStr=:productE WHERE purchaseorderid = :purchaseorderid")
    void updateProductItems(Integer purchaseorderid,String productE);

}
