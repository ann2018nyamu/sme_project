package ke.co.eclectic.quickstore.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import ke.co.eclectic.quickstore.helper.GlobalMethod;

/**
 * Created by David Manduku on 08/10/2018.
 */
@Entity(tableName = "country",indices = {@Index(value = {"code"}, unique = true)})
public class Country {
    @PrimaryKey
    @NonNull
    @SerializedName("code")
    String code;
    String nationality;
    String dialCode;
    String name;

    String imagestr="";

    @Ignore
    Bitmap imgBitmap;

    /**
     * Gets img bitmap.
     *
     * @return the img bitmap
     */
    public Bitmap getImgBitmap() {
        if(!imagestr.contentEquals("")){
           return GlobalMethod.stringToBitMap(imagestr);
        }
        return imgBitmap;
    }

    /**
     * Sets img bitmap.
     *
     * @param imgBitmap the img bitmap
     */
    public void setImgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    /**
     * Gets imagestr.
     *
     * @return the imagestr
     */
    public String getImagestr() {
        return imagestr;
    }

    /**
     * Sets imagestr.
     *
     * @param imagestr the imagestr
     */
    public void setImagestr(String imagestr) {
        this.imagestr = imagestr;
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets code.
     *
     * @param code the code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets nationality.
     *
     * @return the nationality
     */
    public String getNationality() {
        return nationality;
    }

    /**
     * Sets nationality.
     *
     * @param nationality the nationality
     */
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    /**
     * Gets dial code.
     *
     * @return the dial code
     */
    public String getDialCode() {
        return dialCode;
    }

    /**
     * Sets dial code.
     *
     * @param dialCode the dial code
     */
    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }
}
