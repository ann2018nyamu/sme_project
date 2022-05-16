package ke.co.eclectic.quickstore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ke.co.eclectic.quickstore.models.Product;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * STEP 2
 * DAO (data access object), you specify SQL queries and associate them with method calls
 * The DAO must be an interface or abstract class.
 *  Created by Manduku O. David on 21/12/2018.
 */
@Dao
public interface ProductDao {
    /**
     * Adds productData to productData table
     *
     * @param productData the product data
     */
    @Insert(onConflict = REPLACE)
    void insert(Product productData);

    /**
     * Deleted all product from table
     */
    @Query("DELETE FROM product")
    void deleteAll();

    /**
     * deletes a specific productData from db based on the id
     *
     * @param name unique name of the product
     */
    @Query("DELETE FROM product WHERE productname = :name ")
    void deleteSingleProduct(String name);

    /**
     * fetches all productData from productData table
     * LiveData a lifecycle library class for data observation
     *
     * @return list of word object/records in database
     */
    @Query("SELECT * from product ORDER BY categoryid ASC")
    LiveData<List<Product>> getAllProducts();


    /**
     * fetched a specific productData from db based on the id
     *
     * @param pName name of the product
     * @return the single product data
     */
    @Query("SELECT * FROM product WHERE productname = :pName ")
    LiveData<Product> getSingleProductByName(String pName);
    /**
     * fetched a specific productData from db based on the id
     *
     * @param pId unique id of the productData
     * @return the single product data
     */
    @Query("SELECT * FROM product WHERE productid = :pId ")
    LiveData<Product> getSingleProductById(Integer pId);


}
