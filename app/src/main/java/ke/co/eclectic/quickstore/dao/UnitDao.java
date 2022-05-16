package ke.co.eclectic.quickstore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ke.co.eclectic.quickstore.models.Unit;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * STEP 2
 * DAO (data access object), you specify SQL queries and associate them with method calls
 * The DAO must be an interface or abstract class.
 *  Created by Manduku O. David on 21/12/2018.
 */
@Dao
public interface UnitDao {
    /**
     * Adds unitData to unitData table
     *
     * @param unitData the unit data
     */
    @Insert(onConflict = REPLACE)
    void insert(Unit unitData);

    /**
     * Deleted all unit from table
     */
    @Query("DELETE FROM unit")
    void deleteAll();

    /**
     * deletes a specific unitData from db based on the id
     *
     * @param name unique name of the unit
     */
    @Query("DELETE FROM unit WHERE unitname = :name ")
    void deleteSingleUnit(String name);

    /**
     * fetches all unitData from unitData table
     * LiveData a lifecycle library class for data observation
     *
     * @return list of word object/records in database
     */
    @Query("SELECT * from unit ORDER BY unitname ASC")
    LiveData<List<Unit>> getAllUnits();

    /**
     * fetched a specific unitData from db based on the id
     *
     * @param name unique code of the unitData
     * @return the single unit data
     */
    @Query("SELECT * FROM unit WHERE unitname = :name ")
    LiveData<List<Unit>> getSingleUnit(String name);


}
