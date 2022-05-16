package ke.co.eclectic.quickstore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ke.co.eclectic.quickstore.models.Country;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * STEP 2
 * DAO (data access object), you specify SQL queries and associate them with method calls
 * The DAO must be an interface or abstract class.
 *  Created by Manduku O. David on 21/12/2018.
 */
@Dao
public interface CountryDao {
    /**
     * Adds countryData to countryData table
     *
     * @param countryData the country data
     */
    @Insert(onConflict = REPLACE)
    void insert(Country countryData);

    /**
     * Deleted all country from table
     */
    @Query("DELETE FROM country")
    void deleteAll();

    /**
     * deletes a specific countryData from db based on the id
     *
     * @param name unique name of the country
     */
    @Query("DELETE FROM country WHERE name = :name ")
    void deleteSingleCountryData(String name);

    /**
     * fetches all countryData from countryData table
     * LiveData a lifecycle library class for data observation
     *
     * @return list of word object/records in database
     */
    @Query("SELECT * from country ORDER BY code ASC")
    LiveData<List<Country>> getAllCountryDatas();

    /**
     * fetches all countryData from countryData table
     * LiveData a lifecycle library class for data observation
     *
     * @return list of word object/records in database
     */
    @Query("SELECT * from country ORDER BY name ASC")
    LiveData<List<Country>> getAllCountryDatasByName();

    /**
     * fetched a specific countryData from db based on the id
     *
     * @param name unique code of the countryData
     * @return the single country data
     */
    @Query("SELECT * FROM country WHERE code = :name ")
    LiveData<List<Country>> getSingleCountryData(String name);


    @Query("UPDATE country SET imagestr = :imgStr WHERE code = :code")
    void updateImage(String imgStr,String code);

}
