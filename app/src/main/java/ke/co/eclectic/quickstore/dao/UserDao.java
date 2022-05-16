package ke.co.eclectic.quickstore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ke.co.eclectic.quickstore.models.User;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * STEP 2
 * DAO (data access object), you specify SQL queries and associate them with method calls
 * The DAO must be an interface or abstract class.
 *  Created by Manduku O. David on 21/12/2018.
 */
@Dao
public interface UserDao {
    /**
     * Adds user to user table
     *
     * @param user the user
     */
    @Insert(onConflict = REPLACE)
    void insert(User user);

    /**
     * Deleted all users from table
     */
    @Query("DELETE FROM users")
    void deleteAll();

    /**
     * deletes a specific user from db based on the id
     *
     * @param id unique id of the user
     */
    @Query("DELETE FROM users WHERE userid = :id ")
    void deleteSingleUser(Integer id);

    /**
     * fetches all user from user table
     * LiveData a lifecycle library class for data observation
     *
     * @return list of word object/records in database
     */
    @Query("SELECT * from users ORDER BY userid ASC")
    LiveData<List<User>> getAllUsers();

    /**
     * fetched a specific user from db based on the id
     *
     * @param id unique id of the user
     * @return the single user
     */
    @Query("SELECT * FROM users WHERE userid = :id ")
    LiveData<List<User>> getSingleUser(Integer id);


    /**
     * Logs in a user based on phone,pass or email
     *
     * @param email the email
     * @param phone the phone
     * @param pass  the pass
     * @return single user
     */
    @Query("SELECT * FROM users WHERE (phonenumber = :phone AND password = :pass) OR (email = :email AND password = :pass)")
    LiveData<List<User>> getSingleUser(String email,String phone,String pass);


}
