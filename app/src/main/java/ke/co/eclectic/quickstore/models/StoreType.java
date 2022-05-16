package ke.co.eclectic.quickstore.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 *Created by David Manduku on 08/10/2018.
 */
@Entity(tableName = "storetype")
public class StoreType {
    @PrimaryKey
    @NonNull
    Integer typeid;
    String storetype;

    /**
     * Gets typeid.
     *
     * @return the typeid
     */
    public Integer getTypeid() {
        return typeid;
    }

    /**
     * Sets typeid.
     *
     * @param typeid the typeid
     */
    public void setTypeid(Integer typeid) {
        this.typeid = typeid;
    }

    /**
     * Gets storetype.
     *
     * @return the storetype
     */
    public String getStoretype() {
        return storetype;
    }

    /**
     * Sets storetype.
     *
     * @param storetype the storetype
     */
    public void setStoretype(String storetype) {
        this.storetype = storetype;
    }
}
