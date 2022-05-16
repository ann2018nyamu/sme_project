package ke.co.eclectic.quickstore.models.others;


/**
 * The type Staff.
 */
public class Staff {

    private Integer storeid;
    private Integer storeuserid;
    private String name;
    private String rolename;
    private String storename;
    private String imageurl;

    /**
     * Gets image url.
     *
     * @return the imageurl
     */
    public String getImageurl() {
        return imageurl;
    }

    /**
     * Sets imageurl.
     *
     * @param imageurl the imageurl
     */
    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    /**
     * Gets storeid.
     *
     * @return the storeid
     */
    public Integer getStoreid() {
        return storeid;
    }

    /**
     * Sets storeid.
     *
     * @param storeid the storeid
     */
    public void setStoreid( Integer storeid) {
        this.storeid = storeid;
    }

    /**
     * Gets storeuserid.
     *
     * @return the storeuserid
     */
    public Integer getStoreuserid() {
        return storeuserid;
    }

    /**
     * Sets storeuserid.
     *
     * @param storeuserid the storeuserid
     */
    public void setStoreuserid(Integer storeuserid) {
        this.storeuserid = storeuserid;
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

    /**
     * Gets rolename.
     *
     * @return the rolename
     */
    public String getRolename() {
        return rolename;
    }

    /**
     * Sets rolename.
     *
     * @param rolename the rolename
     */
    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    /**
     * Gets storename.
     *
     * @return the storename
     */
    public String getStorename() {
        return storename;
    }

    /**
     * Sets storename.
     *
     * @param storename the storename
     */
    public void setStorename(String storename) {
        this.storename = storename;
    }
}
