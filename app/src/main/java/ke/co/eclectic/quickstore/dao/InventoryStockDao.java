package ke.co.eclectic.quickstore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ke.co.eclectic.quickstore.models.InventoryStock;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * STEP 2
 * DAO (data access object), you specify SQL queries and associate them with method calls
 * The DAO must be an interface or abstract class.
 *  Created by Manduku O. David on 21/12/2018.
 */
@Dao
public interface InventoryStockDao {
    /**
     * Adds inventorystockObj to inventorystockObj table
     *
     * @param inventorystockObj the inventorystock data
     */
    @Insert(onConflict = REPLACE)
    void insert(InventoryStock inventorystockObj);

    /**
     * Deleted all inventorystock from table
     */
    @Query("DELETE FROM inventorystock")
    void deleteAll();

    /**
     * deletes a specific inventorystockObj from db based on the id
     *
     * @param inventorystockid unique name of the inventorystock
     */
    @Query("DELETE FROM inventorystock WHERE inventoryid = :inventorystockid ")
    void deleteSingleInventoryStock(int inventorystockid);

    /**
     * fetches all inventorystockObj from inventorystockObj table
     * LiveData a lifecycle library class for data observation
     *
     * @return list of word object/records in database
     */
    @Query("SELECT * from inventorystock ORDER BY inventoryid DESC")
    LiveData<List<InventoryStock>> getAllInventoryStocks();

    /**
     * fetched a specific inventorystockObj from db based on the id
     *
     * @param inventorystockid unique id of the inventorystockObj
     * @return the single inventorystock data
     */
    @Query("SELECT * FROM inventorystock WHERE inventoryid = :inventorystockid ")
    LiveData<List<InventoryStock>> getSingleInventoryStock(Integer inventorystockid);
  
}
