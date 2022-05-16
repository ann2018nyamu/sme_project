package ke.co.eclectic.quickstore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ke.co.eclectic.quickstore.models.Category;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * STEP 2
 * DAO (data access object), you specify SQL queries and associate them with method calls
 * The DAO must be an interface or abstract class.
 *  Created by Manduku O. David on 21/12/2018.
 */
@Dao
public interface CategoryDao {
    /**
     * Adds categoryData to categoryData table
     *
     * @param categoryData the category data
     */
    @Insert(onConflict = REPLACE)
    void insert(Category categoryData);

    /**
     * Deleted all category from table
     */
    @Query("DELETE FROM category")
    void deleteAll();

    /**
     * deletes a specific categoryData from db based on the id
     *
     * @param name unique name of the category
     */
    @Query("DELETE FROM category WHERE categoryname = :name ")
    void deleteSingleCategory(String name);

    /**
     * fetches all categoryData from categoryData table
     * LiveData a lifecycle library class for data observation
     *
     * @return list of word object/records in database
     */
    @Query("SELECT * from category ORDER BY categoryname ASC")
    LiveData<List<Category>> getAllCategories();

    /**
     * fetched a specific categoryData from db based on the id
     *
     * @param name unique code of the categoryData
     * @return the single category data
     */
    @Query("SELECT * FROM category WHERE categoryname = :name ")
    LiveData<List<Category>> getSingleCategoryData(String name);


}
